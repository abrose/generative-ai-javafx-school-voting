// ABOUTME: Unit tests for Vote model class
// ABOUTME: Tests vote creation, validation, immutability, and business rules

package com.school.voting.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class VoteTest {

    @Nested
    @DisplayName("Vote Creation")
    class VoteCreation {

        @Test
        @DisplayName("Should create vote with all parameters")
        void shouldCreateVoteWithAllParameters() {
            // Given
            Integer id = 1;
            Integer voterId = 10;
            Integer candidateId = 20;
            Integer sessionId = 100;
            LocalDateTime votedAt = LocalDateTime.of(2025, 7, 30, 14, 30);

            // When
            Vote vote = new Vote(id, voterId, candidateId, sessionId, votedAt);

            // Then
            assertEquals(id, vote.getId());
            assertEquals(voterId, vote.getVoterId());
            assertEquals(candidateId, vote.getCandidateId());
            assertEquals(sessionId, vote.getSessionId());
            assertEquals(votedAt, vote.getVotedAt());
        }

        @Test
        @DisplayName("Should create vote with constructor without ID")
        void shouldCreateVoteWithoutId() {
            // Given
            Integer voterId = 10;
            Integer candidateId = 20;
            Integer sessionId = 100;

            // When
            Vote vote = new Vote(voterId, candidateId, sessionId);

            // Then
            assertNull(vote.getId());
            assertEquals(voterId, vote.getVoterId());
            assertEquals(candidateId, vote.getCandidateId());
            assertEquals(sessionId, vote.getSessionId());
            assertNotNull(vote.getVotedAt());
            assertTrue(vote.getVotedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        }

        @Test
        @DisplayName("Should auto-generate timestamp when votedAt is null")
        void shouldAutoGenerateTimestamp() {
            // Given
            LocalDateTime before = LocalDateTime.now();

            // When
            Vote vote = new Vote(1, 10, 20, 100, null);

            // Then
            LocalDateTime after = LocalDateTime.now();
            assertNotNull(vote.getVotedAt());
            assertTrue(vote.getVotedAt().isAfter(before.minusSeconds(1)));
            assertTrue(vote.getVotedAt().isBefore(after.plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("Vote Validation")
    class VoteValidation {

        @Test
        @DisplayName("Should throw exception when voterId is null")
        void shouldThrowExceptionWhenVoterIdIsNull() {
            // When & Then
            assertThrows(NullPointerException.class, () -> {
                new Vote(1, null, 20, 100, LocalDateTime.now());
            });
        }

        @Test
        @DisplayName("Should throw exception when candidateId is null")
        void shouldThrowExceptionWhenCandidateIdIsNull() {
            // When & Then
            assertThrows(NullPointerException.class, () -> {
                new Vote(1, 10, null, 100, LocalDateTime.now());
            });
        }

        @Test
        @DisplayName("Should throw exception when sessionId is null")
        void shouldThrowExceptionWhenSessionIdIsNull() {
            // When & Then
            assertThrows(NullPointerException.class, () -> {
                new Vote(1, 10, 20, null, LocalDateTime.now());
            });
        }

        @Test
        @DisplayName("Should allow self-voting (voter equals candidate)")
        void shouldAllowSelfVoting() {
            // Given
            Integer voterId = 10;
            Integer candidateId = 10; // Same as voter - self-voting

            // When & Then
            assertDoesNotThrow(() -> {
                Vote vote = new Vote(1, voterId, candidateId, 100, LocalDateTime.now());
                assertEquals(voterId, vote.getVoterId());
                assertEquals(candidateId, vote.getCandidateId());
            });
        }
    }

    @Nested
    @DisplayName("Vote Immutability")
    class VoteImmutability {

        @Test
        @DisplayName("Should be immutable - no setters available")
        void shouldBeImmutable() {
            // Given
            Vote vote = new Vote(1, 10, 20, 100, LocalDateTime.now());

            // When - Try to find setters via reflection
            var methods = vote.getClass().getMethods();
            var setterMethods = java.util.Arrays.stream(methods)
                    .filter(method -> method.getName().startsWith("set"))
                    .toList();

            // Then
            assertTrue(setterMethods.isEmpty(), "Vote should not have any setter methods");
        }
    }

    @Nested
    @DisplayName("Vote Equality and Hashing")
    class VoteEqualityAndHashing {

        @Test
        @DisplayName("Should be equal when voterId, candidateId, and sessionId match")
        void shouldBeEqualWhenKeyFieldsMatch() {
            // Given
            LocalDateTime time1 = LocalDateTime.now();
            LocalDateTime time2 = LocalDateTime.now().plusMinutes(1);
            
            Vote vote1 = new Vote(1, 10, 20, 100, time1);
            Vote vote2 = new Vote(2, 10, 20, 100, time2); // Different ID and time

            // When & Then
            assertEquals(vote1, vote2);
            assertEquals(vote1.hashCode(), vote2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when voterId differs")
        void shouldNotBeEqualWhenVoterIdDiffers() {
            // Given
            Vote vote1 = new Vote(1, 10, 20, 100, LocalDateTime.now());
            Vote vote2 = new Vote(1, 11, 20, 100, LocalDateTime.now());

            // When & Then
            assertNotEquals(vote1, vote2);
        }

        @Test
        @DisplayName("Should not be equal when candidateId differs")
        void shouldNotBeEqualWhenCandidateIdDiffers() {
            // Given
            Vote vote1 = new Vote(1, 10, 20, 100, LocalDateTime.now());
            Vote vote2 = new Vote(1, 10, 21, 100, LocalDateTime.now());

            // When & Then
            assertNotEquals(vote1, vote2);
        }

        @Test
        @DisplayName("Should not be equal when sessionId differs")
        void shouldNotBeEqualWhenSessionIdDiffers() {
            // Given
            Vote vote1 = new Vote(1, 10, 20, 100, LocalDateTime.now());
            Vote vote2 = new Vote(1, 10, 20, 101, LocalDateTime.now());

            // When & Then
            assertNotEquals(vote1, vote2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Vote vote = new Vote(1, 10, 20, 100, LocalDateTime.now());

            // When & Then
            assertNotEquals(vote, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            Vote vote = new Vote(1, 10, 20, 100, LocalDateTime.now());
            String notAVote = "not a vote";

            // When & Then
            assertNotEquals(vote, notAVote);
        }
    }

    @Nested
    @DisplayName("Vote String Representation")
    class VoteStringRepresentation {

        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            // Given
            LocalDateTime votedAt = LocalDateTime.of(2025, 7, 30, 14, 30);
            Vote vote = new Vote(1, 10, 20, 100, votedAt);

            // When
            String string = vote.toString();

            // Then
            assertTrue(string.contains("Vote{"));
            assertTrue(string.contains("id=1"));
            assertTrue(string.contains("voterId=10"));
            assertTrue(string.contains("candidateId=20"));
            assertTrue(string.contains("sessionId=100"));
            assertTrue(string.contains(votedAt.toString()));
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support same voter voting for different candidates in different sessions")
        void shouldSupportSameVoterInDifferentSessions() {
            // Given
            Integer voterId = 10;
            Vote vote1 = new Vote(1, voterId, 20, 100, LocalDateTime.now());
            Vote vote2 = new Vote(2, voterId, 21, 101, LocalDateTime.now());

            // When & Then
            assertNotEquals(vote1, vote2); // Different sessions
            assertEquals(voterId, vote1.getVoterId());
            assertEquals(voterId, vote2.getVoterId());
        }

        @Test
        @DisplayName("Should maintain chronological order through timestamps")
        void shouldMaintainChronologicalOrder() {
            // Given
            LocalDateTime earlier = LocalDateTime.of(2025, 7, 30, 14, 0);
            LocalDateTime later = LocalDateTime.of(2025, 7, 30, 14, 30);
            
            Vote earlierVote = new Vote(1, 10, 20, 100, earlier);
            Vote laterVote = new Vote(2, 11, 21, 100, later);

            // When & Then
            assertTrue(earlierVote.getVotedAt().isBefore(laterVote.getVotedAt()));
        }
    }
}