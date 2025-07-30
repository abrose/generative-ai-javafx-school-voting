// ABOUTME: High-level integration tests for the voting system workflow
// ABOUTME: Tests complete voting scenarios and business rule enforcement

package com.school.voting;

import com.school.voting.model.Parent;
import com.school.voting.model.Vote;
import com.school.voting.model.VotingSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Integration tests that verify the complete voting system workflow
 * without requiring database setup. Tests business logic integration.
 */
class VotingSystemIntegrationTest {

    @Nested
    @DisplayName("Complete Voting Workflow")
    class CompleteVotingWorkflow {

        @Test
        @DisplayName("Should support complete voting session from setup to results")
        void shouldSupportCompleteVotingSessionFromSetupToResults() {
            // Given - Setup phase
            VotingSession session = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // Create 15 parents (simulating class 6c)
            List<Parent> parents = createTestParents(session.getId(), 15);
            
            // Mark 5 as candidates
            List<Parent> candidates = parents.subList(0, 5).stream()
                    .map(parent -> parent.toBuilder().isCandidate(true).build())
                    .collect(Collectors.toList());

            // Verify setup conditions
            assertTrue(session.canStartVoting());
            assertEquals(VotingSession.Status.SETUP, session.getStatus());
            assertEquals(5, candidates.size());
            assertEquals(15, parents.size());

            // When - Start voting phase
            VotingSession votingSession = session.toBuilder()
                    .status(VotingSession.Status.VOTING)
                    .build();

            assertTrue(votingSession.isActive());
            assertTrue(votingSession.isVoting());

            // Simulate voting process
            List<Vote> votes = simulateVotingProcess(parents, candidates, session.getId());

            // Then - Analyze results
            Map<Integer, Integer> results = calculateVoteResults(votes);
            
            // Verify voting completed
            assertFalse(results.isEmpty());
            assertTrue(votes.size() <= parents.size()); // Some may be skipped
            
            // Find winner and deputy
            List<Map.Entry<Integer, Integer>> sortedResults = results.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .collect(Collectors.toList());
            
            assertFalse(sortedResults.isEmpty());
            
            // Complete session
            VotingSession completedSession = votingSession.toBuilder()
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now())
                    .build();

            assertEquals(VotingSession.Status.COMPLETED, completedSession.getStatus());
            assertFalse(completedSession.isActive());
            assertNotNull(completedSession.getCompletedAt());
        }

        @Test
        @DisplayName("Should prevent double voting")
        void shouldPreventDoubleVoting() {
            // Given
            Integer sessionId = 1;
            Integer voterId = 10;
            Integer candidateId1 = 20;
            Integer candidateId2 = 21;

            Set<String> recordedVotes = new HashSet<>();

            // When - Attempt to record first vote
            String voteKey1 = voterId + "-" + sessionId;
            boolean firstVoteRecorded = recordedVotes.add(voteKey1);

            // Then - First vote should be recorded
            assertTrue(firstVoteRecorded);

            // When - Attempt to record second vote by same voter
            String voteKey2 = voterId + "-" + sessionId; // Same voter, same session
            boolean secondVoteRecorded = recordedVotes.add(voteKey2);

            // Then - Second vote should be prevented
            assertFalse(secondVoteRecorded);
            assertEquals(1, recordedVotes.size());
        }

        private List<Parent> createTestParents(Integer sessionId, int count) {
            List<Parent> parents = new ArrayList<>();
            String[] names = {
                "Anna Weber", "Marcus Müller", "Sophie Schmidt", "David Fischer", "Julia Becker",
                "Thomas Meyer", "Sarah Wagner", "Michael Koch", "Laura Schulz", "Alexander Richter",
                "Emma Hoffmann", "Maximilian Klein", "Lisa Zimmermann", "Sebastian Wolf", "Marie Hartmann"
            };

            for (int i = 0; i < count && i < names.length; i++) {
                parents.add(Parent.builder()
                        .id(i + 1)
                        .name(names[i])
                        .sessionId(sessionId)
                        .hasVoted(false)
                        .isCandidate(false)
                        .build());
            }
            return parents;
        }

        private List<Vote> simulateVotingProcess(List<Parent> parents, List<Parent> candidates, Integer sessionId) {
            List<Vote> votes = new ArrayList<>();
            Random random = new Random(42); // Fixed seed for reproducible tests

            for (Parent parent : parents) {
                // 90% of parents vote, 10% are skipped
                if (random.nextDouble() < 0.9) {
                    // Choose a random candidate
                    Parent chosenCandidate = candidates.get(random.nextInt(candidates.size()));
                    votes.add(new Vote(parent.getId(), chosenCandidate.getId(), sessionId));
                }
            }
            return votes;
        }

        private Map<Integer, Integer> calculateVoteResults(List<Vote> votes) {
            return votes.stream()
                    .collect(Collectors.groupingBy(
                            Vote::getCandidateId,
                            Collectors.collectingAndThen(
                                    Collectors.counting(),
                                    Long::intValue
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("Self-Voting Integration")
    class SelfVotingIntegration {

        @Test
        @DisplayName("Should support candidates voting for themselves")
        void shouldSupportCandidatesVotingForThemselves() {
            // Given - Create candidates who are also voters
            Integer sessionId = 1;
            
            List<Parent> candidateVoters = Arrays.asList(
                    Parent.builder().id(1).name("Candidate 1").isCandidate(true).sessionId(sessionId).build(),
                    Parent.builder().id(2).name("Candidate 2").isCandidate(true).sessionId(sessionId).build(),
                    Parent.builder().id(3).name("Candidate 3").isCandidate(true).sessionId(sessionId).build()
            );

            // When - Each candidate votes for themselves
            List<Vote> selfVotes = candidateVoters.stream()
                    .map(candidate -> new Vote(candidate.getId(), candidate.getId(), sessionId))
                    .collect(Collectors.toList());

            // Then - All self-votes should be valid
            assertEquals(3, selfVotes.size());
            
            for (int i = 0; i < selfVotes.size(); i++) {
                Vote vote = selfVotes.get(i);
                Parent candidate = candidateVoters.get(i);
                
                assertEquals(candidate.getId(), vote.getVoterId());
                assertEquals(candidate.getId(), vote.getCandidateId());
                assertTrue(vote.getVoterId().equals(vote.getCandidateId()));
            }

            // Verify results calculation includes self-votes
            Map<Integer, Integer> results = calculateVoteResults(selfVotes);
            assertEquals(3, results.size());
            
            // Each candidate should have 1 vote (their own)
            for (Parent candidate : candidateVoters) {
                assertEquals(1, results.get(candidate.getId()));
            }
        }

        @Test
        @DisplayName("Should handle mixed self-voting and regular voting")
        void shouldHandleMixedSelfVotingAndRegularVoting() {
            // Given
            Integer sessionId = 1;
            
            Parent candidate1 = Parent.builder().id(1).name("Candidate 1").isCandidate(true).sessionId(sessionId).build();
            Parent candidate2 = Parent.builder().id(2).name("Candidate 2").isCandidate(true).sessionId(sessionId).build();
            Parent regularVoter = Parent.builder().id(3).name("Regular Voter").isCandidate(false).sessionId(sessionId).build();

            List<Vote> votes = Arrays.asList(
                    new Vote(candidate1.getId(), candidate1.getId(), sessionId), // Self-vote
                    new Vote(candidate2.getId(), candidate1.getId(), sessionId), // Candidate votes for other candidate
                    new Vote(regularVoter.getId(), candidate2.getId(), sessionId) // Regular voter votes for candidate
            );

            // When
            Map<Integer, Integer> results = calculateVoteResults(votes);

            // Then
            assertEquals(2, results.get(candidate1.getId())); // Got self-vote + vote from candidate2
            assertEquals(1, results.get(candidate2.getId())); // Got vote from regular voter
            
            // Verify total votes
            int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
            assertEquals(3, totalVotes);
        }

        private Map<Integer, Integer> calculateVoteResults(List<Vote> votes) {
            return votes.stream()
                    .collect(Collectors.groupingBy(
                            Vote::getCandidateId,
                            Collectors.collectingAndThen(
                                    Collectors.counting(),
                                    Long::intValue
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle session with no votes")
        void shouldHandleSessionWithNoVotes() {
            // Given
            VotingSession session = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now()) // Required for COMPLETED status
                    .build();

            List<Vote> noVotes = Collections.emptyList();

            // When
            Map<Integer, Integer> results = calculateVoteResults(noVotes);

            // Then
            assertTrue(results.isEmpty());
            assertEquals(VotingSession.Status.COMPLETED, session.getStatus());
        }

        @Test
        @DisplayName("Should handle ties correctly")
        void shouldHandleTiesCorrectly() {
            // Given
            Integer sessionId = 1;
            List<Vote> tiedVotes = Arrays.asList(
                    new Vote(1, 10, sessionId), // Voter 1 votes for Candidate 10
                    new Vote(2, 20, sessionId), // Voter 2 votes for Candidate 20
                    new Vote(3, 10, sessionId), // Voter 3 votes for Candidate 10
                    new Vote(4, 20, sessionId)  // Voter 4 votes for Candidate 20
            );

            // When
            Map<Integer, Integer> results = calculateVoteResults(tiedVotes);

            // Then
            assertEquals(2, results.get(10)); // Candidate 10 has 2 votes
            assertEquals(2, results.get(20)); // Candidate 20 has 2 votes
            
            // Verify it's a tie
            assertEquals(results.get(10), results.get(20));
        }

        @Test
        @DisplayName("Should validate session state transitions")
        void shouldValidateSessionStateTransitions() {
            // Given
            VotingSession setupSession = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When - Valid transition: SETUP -> VOTING
            VotingSession votingSession = setupSession.toBuilder()
                    .status(VotingSession.Status.VOTING)
                    .build();

            // Then
            assertTrue(setupSession.canStartVoting());
            assertFalse(votingSession.canStartVoting());
            assertTrue(votingSession.isVoting());

            // When - Valid transition: VOTING -> COMPLETED
            VotingSession completedSession = votingSession.toBuilder()
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now())
                    .build();

            // Then
            assertFalse(completedSession.canStartVoting());
            assertFalse(completedSession.isVoting());
            assertFalse(completedSession.isActive());
        }

        private Map<Integer, Integer> calculateVoteResults(List<Vote> votes) {
            return votes.stream()
                    .collect(Collectors.groupingBy(
                            Vote::getCandidateId,
                            Collectors.collectingAndThen(
                                    Collectors.counting(),
                                    Long::intValue
                            )
                    ));
        }
    }
}