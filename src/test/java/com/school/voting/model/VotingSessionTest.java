// ABOUTME: Unit tests for VotingSession model class
// ABOUTME: Tests session creation, builder pattern, status transitions, and business rules

package com.school.voting.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class VotingSessionTest {

    @Nested
    @DisplayName("VotingSession Creation with Builder")
    class VotingSessionCreation {

        @Test
        @DisplayName("Should create session with builder pattern")
        void shouldCreateSessionWithBuilder() {
            // Given
            String className = "6c";

            // When
            VotingSession session = VotingSession.builder()
                    .className(className)
                    .build();

            // Then
            assertNull(session.getId());
            assertEquals(className, session.getClassName());
            assertEquals(VotingSession.Status.SETUP, session.getStatus());
            assertNotNull(session.getCreatedAt());
            assertNull(session.getCompletedAt());
        }

        @Test
        @DisplayName("Should create session with all properties")
        void shouldCreateSessionWithAllProperties() {
            // Given
            Integer id = 1;
            String className = "6c";
            VotingSession.Status status = VotingSession.Status.VOTING;
            LocalDateTime createdAt = LocalDateTime.of(2025, 7, 30, 14, 0);
            LocalDateTime completedAt = LocalDateTime.of(2025, 7, 30, 15, 30);

            // When
            VotingSession session = VotingSession.builder()
                    .id(id)
                    .className(className)
                    .status(status)
                    .createdAt(createdAt)
                    .completedAt(completedAt)
                    .build();

            // Then
            assertEquals(id, session.getId());
            assertEquals(className, session.getClassName());
            assertEquals(status, session.getStatus());
            assertEquals(createdAt, session.getCreatedAt());
            assertEquals(completedAt, session.getCompletedAt());
        }

        @Test
        @DisplayName("Should auto-generate timestamp when createdAt is null")
        void shouldAutoGenerateTimestamp() {
            // Given
            LocalDateTime before = LocalDateTime.now();

            // When
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .build();

            // Then
            LocalDateTime after = LocalDateTime.now();
            assertNotNull(session.getCreatedAt());
            assertTrue(session.getCreatedAt().isAfter(before.minusSeconds(1)));
            assertTrue(session.getCreatedAt().isBefore(after.plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("VotingSession Validation")
    class VotingSessionValidation {

        @Test
        @DisplayName("Should throw IllegalStateException when className is null")
        void shouldThrowExceptionWhenClassNameIsNull() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                VotingSession.builder()
                        .className(null)
                        .build();
            });
        }

        @Test
        @DisplayName("Should throw IllegalStateException when className is empty")
        void shouldThrowExceptionWhenClassNameIsEmpty() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                VotingSession.builder()
                        .className("")
                        .build();
            });
        }

        @Test
        @DisplayName("Should throw IllegalStateException when className is blank")
        void shouldThrowExceptionWhenClassNameIsBlank() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                VotingSession.builder()
                        .className("   ")
                        .build();
            });
        }

        @Test
        @DisplayName("Should not trim whitespace from className")
        void shouldNotTrimWhitespaceFromClassName() {
            // Given
            String classNameWithWhitespace = "  6c  ";

            // When
            VotingSession session = VotingSession.builder()
                    .className(classNameWithWhitespace)
                    .build();

            // Then
            assertEquals("  6c  ", session.getClassName()); // No trimming in actual implementation
        }

        @Test
        @DisplayName("Should default to SETUP status")
        void shouldDefaultToSetupStatus() {
            // When
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .build();

            // Then
            assertEquals(VotingSession.Status.SETUP, session.getStatus());
        }
    }

    @Nested
    @DisplayName("VotingSession Builder Pattern")
    class VotingSessionBuilderPattern {

        @Test
        @DisplayName("Should support method chaining")
        void shouldSupportMethodChaining() {
            // When & Then
            assertDoesNotThrow(() -> {
                VotingSession session = VotingSession.builder()
                        .id(1)
                        .className("6c")
                        .status(VotingSession.Status.VOTING)
                        .createdAt(LocalDateTime.now())
                        .completedAt(null)
                        .build();
                
                assertNotNull(session);
            });
        }

        @Test
        @DisplayName("Should create toBuilder from existing session")
        void shouldCreateToBuilderFromExistingSession() {
            // Given
            VotingSession original = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When
            VotingSession modified = original.toBuilder()
                    .status(VotingSession.Status.VOTING)
                    .build();

            // Then
            assertEquals(original.getId(), modified.getId());
            assertEquals(original.getClassName(), modified.getClassName());
            assertEquals(original.getCreatedAt(), modified.getCreatedAt());
            assertEquals(original.getCompletedAt(), modified.getCompletedAt());
            
            // But status should be different
            assertNotEquals(original.getStatus(), modified.getStatus());
            assertEquals(VotingSession.Status.VOTING, modified.getStatus());
        }
    }

    @Nested
    @DisplayName("VotingSession Status Enum")
    class VotingSessionStatusEnum {

        @Test
        @DisplayName("Should have correct status display names")
        void shouldHaveCorrectStatusDisplayNames() {
            assertEquals("Setup Phase", VotingSession.Status.SETUP.getDisplayName());
            assertEquals("Voting in Progress", VotingSession.Status.VOTING.getDisplayName());
            assertEquals("Voting Completed", VotingSession.Status.COMPLETED.getDisplayName());
        }

        @Test
        @DisplayName("Should have all expected status values")
        void shouldHaveAllExpectedStatusValues() {
            VotingSession.Status[] statuses = VotingSession.Status.values();
            
            assertEquals(3, statuses.length);
            assertTrue(java.util.Arrays.asList(statuses).contains(VotingSession.Status.SETUP));
            assertTrue(java.util.Arrays.asList(statuses).contains(VotingSession.Status.VOTING));
            assertTrue(java.util.Arrays.asList(statuses).contains(VotingSession.Status.COMPLETED));
        }
    }

    @Nested
    @DisplayName("VotingSession Business Logic")
    class VotingSessionBusinessLogic {

        @Test
        @DisplayName("Should allow starting voting when in SETUP status")
        void shouldAllowStartingVotingWhenInSetupStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When & Then
            assertTrue(session.canStartVoting());
        }

        @Test
        @DisplayName("Should not allow starting voting when in VOTING status")
        void shouldNotAllowStartingVotingWhenInVotingStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.VOTING)
                    .build();

            // When & Then
            assertFalse(session.canStartVoting());
        }

        @Test
        @DisplayName("Should not allow starting voting when COMPLETED")
        void shouldNotAllowStartingVotingWhenCompleted() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now()) // Required for COMPLETED status
                    .build();

            // When & Then
            assertFalse(session.canStartVoting());
        }

        @Test
        @DisplayName("Should be active when in SETUP status")
        void shouldBeActiveWhenInSetupStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When & Then
            assertTrue(session.isActive());
        }

        @Test
        @DisplayName("Should be active when in VOTING status")
        void shouldBeActiveWhenInVotingStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.VOTING)
                    .build();

            // When & Then
            assertTrue(session.isActive());
        }

        @Test
        @DisplayName("Should not be active when COMPLETED")
        void shouldNotBeActiveWhenCompleted() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now()) // Required for COMPLETED status
                    .build();

            // When & Then
            assertFalse(session.isActive());
        }

        @Test
        @DisplayName("Should be voting when in VOTING status")
        void shouldBeVotingWhenInVotingStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.VOTING)
                    .build();

            // When & Then
            assertTrue(session.isVoting());
        }

        @Test
        @DisplayName("Should not be voting when in SETUP status")
        void shouldNotBeVotingWhenInSetupStatus() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When & Then
            assertFalse(session.isVoting());
        }

        @Test
        @DisplayName("Should not be voting when COMPLETED")
        void shouldNotBeVotingWhenCompleted() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now()) // Required for COMPLETED status
                    .build();

            // When & Then
            assertFalse(session.isVoting());
        }
    }

    @Nested
    @DisplayName("VotingSession Immutability")
    class VotingSessionImmutability {

        @Test
        @DisplayName("Should be immutable - no setters available")
        void shouldBeImmutable() {
            // Given
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .build();

            // When - Try to find setters via reflection
            var methods = session.getClass().getMethods();
            var setterMethods = java.util.Arrays.stream(methods)
                    .filter(method -> method.getName().startsWith("set"))
                    .toList();

            // Then
            assertTrue(setterMethods.isEmpty(), "VotingSession should not have any setter methods");
        }
    }

    @Nested
    @DisplayName("VotingSession Equality and Hashing")
    class VotingSessionEqualityAndHashing {

        @Test
        @DisplayName("Should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime completedAt = LocalDateTime.now().plusHours(1);
            
            VotingSession session1 = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .createdAt(createdAt)
                    .completedAt(completedAt)
                    .build();
                    
            VotingSession session2 = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .createdAt(createdAt)
                    .completedAt(completedAt)
                    .build();

            // When & Then
            assertEquals(session1, session2);
            assertEquals(session1.hashCode(), session2.hashCode());
        }

        @Test
        @DisplayName("Should be equal when IDs are null")
        void shouldBeEqualWhenIDsAreNull() {
            // Given - Both have null IDs, so they're equal based on ID comparison
            VotingSession session1 = VotingSession.builder().className("6c").build();
            VotingSession session2 = VotingSession.builder().className("7a").build();

            // When & Then
            assertEquals(session1, session2); // Both have null IDs, so equal
        }

        @Test
        @DisplayName("Should not be equal when IDs differ")
        void shouldNotBeEqualWhenIDsDiffer() {
            // Given
            VotingSession session1 = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();
            VotingSession session2 = VotingSession.builder()
                    .id(2)
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();

            // When & Then
            assertNotEquals(session1, session2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            VotingSession session = VotingSession.builder().className("6c").build();

            // When & Then
            assertNotEquals(session, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            VotingSession session = VotingSession.builder().className("6c").build();
            String notASession = "not a session";

            // When & Then
            assertNotEquals(session, notASession);
        }
    }

    @Nested
    @DisplayName("VotingSession String Representation")
    class VotingSessionStringRepresentation {

        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            // Given
            LocalDateTime createdAt = LocalDateTime.of(2025, 7, 30, 14, 30);
            LocalDateTime completedAt = LocalDateTime.of(2025, 7, 30, 16, 0);
            VotingSession session = VotingSession.builder()
                    .id(1)
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .createdAt(createdAt)
                    .completedAt(completedAt)
                    .build();

            // When
            String string = session.toString();

            // Then
            assertTrue(string.contains("VotingSession{"));
            assertTrue(string.contains("id=1"));
            assertTrue(string.contains("className='6c'"));
            assertTrue(string.contains("status=COMPLETED"));
            assertTrue(string.contains(createdAt.toString()));
            assertTrue(string.contains(completedAt.toString()));
        }
    }

    @Nested
    @DisplayName("Workflow Tests")
    class WorkflowTests {

        @Test
        @DisplayName("Should support complete voting workflow")
        void shouldSupportCompleteVotingWorkflow() {
            // Given - Start with SETUP
            VotingSession setupSession = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.SETUP)
                    .build();
            
            assertTrue(setupSession.canStartVoting());
            assertTrue(setupSession.isActive());
            assertFalse(setupSession.isVoting());

            // When - Move to VOTING
            VotingSession votingSession = setupSession.toBuilder()
                    .status(VotingSession.Status.VOTING)
                    .build();
            
            assertFalse(votingSession.canStartVoting());
            assertTrue(votingSession.isActive());
            assertTrue(votingSession.isVoting());

            // Then - Complete voting
            VotingSession completedSession = votingSession.toBuilder()
                    .status(VotingSession.Status.COMPLETED)
                    .completedAt(LocalDateTime.now())
                    .build();
            
            assertFalse(completedSession.canStartVoting());
            assertFalse(completedSession.isActive());
            assertFalse(completedSession.isVoting());
            assertNotNull(completedSession.getCompletedAt());
        }

        @Test
        @DisplayName("Should maintain chronological order through timestamps")
        void shouldMaintainChronologicalOrder() {
            // Given
            LocalDateTime created = LocalDateTime.of(2025, 7, 30, 14, 0);
            LocalDateTime completed = LocalDateTime.of(2025, 7, 30, 15, 30);
            
            VotingSession session = VotingSession.builder()
                    .className("6c")
                    .status(VotingSession.Status.COMPLETED)
                    .createdAt(created)
                    .completedAt(completed)
                    .build();

            // When & Then
            assertTrue(session.getCreatedAt().isBefore(session.getCompletedAt()));
        }
    }
}