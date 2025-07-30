// ABOUTME: Service for exporting voting results to PDF format
// ABOUTME: Creates professionally formatted PDF reports with winner, results table, and statistics

package com.school.voting.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.school.voting.controller.ResultsController;
import com.school.voting.dao.ParentDAO;
import com.school.voting.dao.VoteDAO;
import com.school.voting.dao.VotingSessionDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.VotingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfExportService {
    private static final Logger logger = LoggerFactory.getLogger(PdfExportService.class);
    
    private final ParentDAO parentDAO = new ParentDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final VotingSessionDAO sessionDAO = new VotingSessionDAO();
    private final DecimalFormat percentFormat = new DecimalFormat("#.#");
    
    // Colors
    private final DeviceRgb PRIMARY_COLOR = new DeviceRgb(52, 73, 94); // #34495e
    private final DeviceRgb SUCCESS_COLOR = new DeviceRgb(39, 174, 96); // #27ae60
    private final DeviceRgb INFO_COLOR = new DeviceRgb(52, 152, 219); // #3498db
    private final DeviceRgb LIGHT_GRAY = new DeviceRgb(236, 240, 241); // #ecf0f1
    
    public File exportResults(VotingSession session) throws Exception {
        // Create filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String className = session.getClassName() != null ? session.getClassName() : "Unknown";
        String filename = String.format("VotingResults_%s_%s.pdf", 
                                       className.replaceAll("[^a-zA-Z0-9]", ""), 
                                       timestamp);
        File outputFile = new File(filename);
        
        logger.info("Exporting results to PDF: {}", outputFile.getAbsolutePath());
        
        try (PdfWriter writer = new PdfWriter(outputFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Set up fonts
            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();
            
            // Add title
            addTitle(document, session, titleFont);
            
            // Add session info
            addSessionInfo(document, session, headerFont, normalFont);
            
            // Add winner and deputy
            addWinnerSection(document, session, headerFont, normalFont);
            
            // Add complete results table
            addResultsTable(document, session, headerFont, normalFont);
            
            // Add voting statistics
            addVotingStatistics(document, session, headerFont, normalFont);
            
            // Add footer
            addFooter(document, normalFont);
        }
        
        logger.info("PDF export completed successfully: {}", outputFile.getAbsolutePath());
        return outputFile;
    }
    
    private void addTitle(Document document, VotingSession session, PdfFont font) {
        Paragraph title = new Paragraph("Voting Results Report")
                .setFont(font)
                .setFontSize(24)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);
        
        String className = session.getClassName() != null ? session.getClassName() : "Unknown";
        Paragraph subtitle = new Paragraph("Class " + className + " - Spokesperson Election")
                .setFont(font)
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);
    }
    
    private void addSessionInfo(Document document, VotingSession session, PdfFont headerFont, PdfFont normalFont) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
        
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        infoTable.addCell(createInfoCell("Session ID:", headerFont));
        infoTable.addCell(createInfoCell(session.getId() != null ? session.getId().toString() : "Not assigned", normalFont));
        
        infoTable.addCell(createInfoCell("Class:", headerFont));
        infoTable.addCell(createInfoCell(session.getClassName() != null ? session.getClassName() : "Unknown", normalFont));
        
        infoTable.addCell(createInfoCell("Created:", headerFont));
        infoTable.addCell(createInfoCell(session.getCreatedAt() != null ? session.getCreatedAt().format(formatter) : "Unknown", normalFont));
        
        if (session.getCompletedAt() != null) {
            infoTable.addCell(createInfoCell("Completed:", headerFont));
            infoTable.addCell(createInfoCell(session.getCompletedAt().format(formatter), normalFont));
        }
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }
    
    private void addWinnerSection(Document document, VotingSession session, PdfFont headerFont, PdfFont normalFont) throws SQLException {
        if (session.getId() == null) {
            logger.warn("Session ID is null, skipping winner section");
            return;
        }
        
        List<Parent> candidates = parentDAO.getCandidatesBySession(session.getId());
        Map<Integer, Integer> voteCounts = voteDAO.getVoteCountsBySession(session.getId());
        
        List<ResultsController.ResultRow> results = candidates.stream()
                .map(candidate -> {
                    int votes = voteCounts.getOrDefault(candidate.getId(), 0);
                    return new ResultsController.ResultRow(candidate.getName(), votes);
                })
                .sorted((a, b) -> Integer.compare(b.votes, a.votes))
                .collect(Collectors.toList());
        
        if (!results.isEmpty()) {
            // Winner section
            Paragraph winnerHeader = new Paragraph("🏆 CLASS SPOKESPERSON")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setFontColor(SUCCESS_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(winnerHeader);
            
            ResultsController.ResultRow winner = results.get(0);
            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            double winnerPercent = totalVotes > 0 ? (double) winner.votes / totalVotes * 100 : 0;
            
            Paragraph winnerName = new Paragraph(winner.candidateName)
                    .setFont(headerFont)
                    .setFontSize(22)
                    .setFontColor(SUCCESS_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(winnerName);
            
            Paragraph winnerVotes = new Paragraph(winner.votes + " votes (" + percentFormat.format(winnerPercent) + "%)")
                    .setFont(normalFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(winnerVotes);
            
            // Deputy section
            if (results.size() > 1) {
                Paragraph deputyHeader = new Paragraph("🥈 DEPUTY SPOKESPERSON")
                        .setFont(headerFont)
                        .setFontSize(16)
                        .setFontColor(INFO_COLOR)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(deputyHeader);
                
                ResultsController.ResultRow deputy = results.get(1);
                double deputyPercent = totalVotes > 0 ? (double) deputy.votes / totalVotes * 100 : 0;
                
                Paragraph deputyName = new Paragraph(deputy.candidateName)
                        .setFont(headerFont)
                        .setFontSize(18)
                        .setFontColor(INFO_COLOR)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5);
                document.add(deputyName);
                
                Paragraph deputyVotes = new Paragraph(deputy.votes + " votes (" + percentFormat.format(deputyPercent) + "%)")
                        .setFont(normalFont)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(deputyVotes);
            }
        }
    }
    
    private void addResultsTable(Document document, VotingSession session, PdfFont headerFont, PdfFont normalFont) throws SQLException {
        if (session.getId() == null) {
            logger.warn("Session ID is null, skipping results table");
            return;
        }
        
        Paragraph tableHeader = new Paragraph("Complete Results")
                .setFont(headerFont)
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setMarginTop(10)
                .setMarginBottom(10);
        document.add(tableHeader);
        
        List<Parent> candidates = parentDAO.getCandidatesBySession(session.getId());
        Map<Integer, Integer> voteCounts = voteDAO.getVoteCountsBySession(session.getId());
        int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
        
        Table resultsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2}));
        resultsTable.setWidth(UnitValue.createPercentValue(100));
        
        // Header row
        resultsTable.addHeaderCell(createHeaderCell("Candidate", headerFont));
        resultsTable.addHeaderCell(createHeaderCell("Votes", headerFont));
        resultsTable.addHeaderCell(createHeaderCell("Percentage", headerFont));
        
        // Data rows
        List<ResultsController.ResultRow> results = candidates.stream()
                .map(candidate -> {
                    int votes = voteCounts.getOrDefault(candidate.getId(), 0);
                    return new ResultsController.ResultRow(candidate.getName(), votes);
                })
                .sorted((a, b) -> Integer.compare(b.votes, a.votes))
                .collect(Collectors.toList());
        
        for (ResultsController.ResultRow result : results) {
            double percentage = totalVotes > 0 ? (double) result.votes / totalVotes * 100 : 0;
            
            resultsTable.addCell(createDataCell(result.candidateName, normalFont));
            resultsTable.addCell(createDataCell(String.valueOf(result.votes), normalFont).setTextAlignment(TextAlignment.CENTER));
            resultsTable.addCell(createDataCell(percentFormat.format(percentage) + "%", normalFont).setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(resultsTable);
    }
    
    private void addVotingStatistics(Document document, VotingSession session, PdfFont headerFont, PdfFont normalFont) throws SQLException {
        if (session.getId() == null) {
            logger.warn("Session ID is null, skipping voting statistics");
            return;
        }
        
        Paragraph statsHeader = new Paragraph("Voting Statistics")
                .setFont(headerFont)
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setMarginTop(20)
                .setMarginBottom(10);
        document.add(statsHeader);
        
        int totalParents = parentDAO.getParentCount(session.getId());
        int totalVotes = voteDAO.getTotalVotes(session.getId());
        double turnout = totalParents > 0 ? (double) totalVotes / totalParents * 100 : 0;
        
        Table statsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        statsTable.setWidth(UnitValue.createPercentValue(100));
        
        statsTable.addCell(createStatsCell("Total Parents", String.valueOf(totalParents), headerFont, normalFont));
        statsTable.addCell(createStatsCell("Votes Cast", String.valueOf(totalVotes), headerFont, normalFont));
        statsTable.addCell(createStatsCell("Turnout", percentFormat.format(turnout) + "%", headerFont, normalFont));
        
        document.add(statsTable);
    }
    
    private void addFooter(Document document, PdfFont normalFont) {
        Paragraph footer = new Paragraph("\nGenerated by School Voting System on " + 
                                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a")))
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.GRAY);
        document.add(footer);
    }
    
    private Cell createInfoCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(12))
                .setBorder(null)
                .setPadding(5);
    }
    
    private Cell createHeaderCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(12).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }
    
    private Cell createDataCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(11))
                .setPadding(6)
                .setBorder(null);
    }
    
    private Cell createStatsCell(String label, String value, PdfFont headerFont, PdfFont normalFont) {
        Cell cell = new Cell()
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(LIGHT_GRAY)
                .setPadding(10);
        
        cell.add(new Paragraph(label).setFont(headerFont).setFontSize(12).setMarginBottom(5));
        cell.add(new Paragraph(value).setFont(normalFont).setFontSize(16).setFontColor(PRIMARY_COLOR));
        
        return cell;
    }
}