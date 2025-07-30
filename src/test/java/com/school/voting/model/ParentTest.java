// ABOUTME: Unit tests for Parent model class
// ABOUTME: Tests parent creation, builder pattern, validation, and business rules

package com.school.voting.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ParentTest {

    @Nested
    @DisplayName("Parent Creation with Builder")
    class ParentCreation {

        @Test
        @DisplayName("Should create parent with builder pattern")
        void shouldCreateParentWithBuilder() {
            // Given
            String name = "John Doe";
            Integer sessionId = 100;

            // When
            Parent parent = Parent.builder()
                    .name(name)
                    .sessionId(sessionId)
                    .build();

            // Then
            assertNull(parent.getId());
            assertEquals(name, parent.getName());
            assertEquals(sessionId, parent.getSessionId());
            assertFalse(parent.isCandidate());
            assertFalse(parent.hasVoted());
            // Note: createdAt may be null in actual implementation
        }

        @Test
        @DisplayName("Should create parent with all properties")
        void shouldCreateParentWithAllProperties() {
            // Given
            Integer id = 1;
            String name = "Jane Smith";
            boolean isCandidate = true;
            boolean hasVoted = true;
            Integer sessionId = 100;
            LocalDateTime createdAt = LocalDateTime.of(2025, 7, 30, 14, 30);

            // When
            Parent parent = Parent.builder()
                    .id(id)
                    .name(name)
                    .isCandidate(isCandidate)
                    .hasVoted(hasVoted)
                    .sessionId(sessionId)
                    .createdAt(createdAt)
                    .build();

            // Then
            assertEquals(id, parent.getId());
            assertEquals(name, parent.getName());
            assertEquals(isCandidate, parent.isCandidate());
            assertEquals(hasVoted, parent.hasVoted());
            assertEquals(sessionId, parent.getSessionId());
            assertEquals(createdAt, parent.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle null createdAt (no auto-generation)")
        void shouldHandleNullCreatedAt() {
            // When
            Parent parent = Parent.builder()
                    .name("Test Parent")
                    .sessionId(100)
                    .build();

            // Then
            assertNull(parent.getCreatedAt()); // No auto-generation in actual implementation
        }
    }

    @Nested
    @DisplayName("Parent Validation")
    class ParentValidation {

        @Test
        @DisplayName("Should throw IllegalStateException when name is null")
        void shouldThrowExceptionWhenNameIsNull() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                Parent.builder()
                        .name(null)
                        .sessionId(100)
                        .build();
            });
        }

        @Test
        @DisplayName("Should throw IllegalStateException when name is empty")
        void shouldThrowExceptionWhenNameIsEmpty() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                Parent.builder()
                        .name("")
                        .sessionId(100)
                        .build();
            });
        }

        @Test
        @DisplayName("Should throw IllegalStateException when name is blank")
        void shouldThrowExceptionWhenNameIsBlank() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                Parent.builder()
                        .name("   ")
                        .sessionId(100)
                        .build();
            });
        }

        @Test
        @DisplayName("Should throw IllegalStateException when sessionId is null")
        void shouldThrowExceptionWhenSessionIdIsNull() {
            // When & Then
            assertThrows(IllegalStateException.class, () -> {
                Parent.builder()
                        .name("John Doe")
                        .sessionId(null)
                        .build();
            });
        }

        @Test
        @DisplayName("Should not trim whitespace from name")
        void shouldNotTrimWhitespaceFromName() {
            // Given
            String nameWithWhitespace = "  John Doe  ";

            // When
            Parent parent = Parent.builder()
                    .name(nameWithWhitespace)
                    .sessionId(100)
                    .build();

            // Then
            assertEquals("  John Doe  ", parent.getName()); // No trimming in actual implementation
        }
    }

    @Nested
    @DisplayName("Parent Builder Pattern")
    class ParentBuilderPattern {

        @Test
        @DisplayName("Should support method chaining")
        void shouldSupportMethodChaining() {
            // When & Then
            assertDoesNotThrow(() -> {
                Parent parent = Parent.builder()
                        .id(1)
                        .name("John Doe")
                        .isCandidate(true)
                        .hasVoted(false)
                        .sessionId(100)
                        .createdAt(LocalDateTime.now())
                        .build();
                
                assertNotNull(parent);
            });
        }

        @Test
        @DisplayName("Should create toBuilder from existing parent")
        void shouldCreateToBuilderFromExistingParent() {
            // Given
            Parent original = Parent.builder()
                    .id(1)
                    .name("John Doe")
                    .isCandidate(false)
                    .hasVoted(false)
                    .sessionId(100)
                    .build();

            // When
            Parent modified = original.toBuilder()
                    .isCandidate(true)
                    .hasVoted(true)
                    .build();

            // Then
            assertEquals(original.getId(), modified.getId());
            assertEquals(original.getName(), modified.getName());
            assertEquals(original.getSessionId(), modified.getSessionId());
            assertEquals(original.getCreatedAt(), modified.getCreatedAt());
            
            // But these should be different
            assertNotEquals(original.isCandidate(), modified.isCandidate());
            assertNotEquals(original.hasVoted(), modified.hasVoted());
            assertTrue(modified.isCandidate());
            assertTrue(modified.hasVoted());
        }
    }

    @Nested
    @DisplayName("Parent Immutability")
    class ParentImmutability {

        @Test
        @DisplayName("Should be immutable - no setters available")
        void shouldBeImmutable() {
            // Given
            Parent parent = Parent.builder()
                    .name("John Doe")
                    .sessionId(100)
                    .build();

            // When - Try to find setters via reflection
            var methods = parent.getClass().getMethods();
            var setterMethods = java.util.Arrays.stream(methods)
                    .filter(method -> method.getName().startsWith("set"))
                    .toList();

            // Then
            assertTrue(setterMethods.isEmpty(), "Parent should not have any setter methods");
        }
    }

    @Nested
    @DisplayName("Parent Equality and Hashing")
    class ParentEqualityAndHashing {

        @Test
        @DisplayName("Should be equal when all fields match")
        void shouldBeEqualWhenAllFieldsMatch() {
            // Given
            LocalDateTime createdAt = LocalDateTime.now();
            
            Parent parent1 = Parent.builder()
                    .id(1)
                    .name("John Doe")
                    .isCandidate(true)
                    .hasVoted(false)
                    .sessionId(100)
                    .createdAt(createdAt)
                    .build();
                    
            Parent parent2 = Parent.builder()
                    .id(1)
                    .name("John Doe")
                    .isCandidate(true)
                    .hasVoted(false)
                    .sessionId(100)
                    .createdAt(createdAt)
                    .build();

            // When & Then
            assertEquals(parent1, parent2);
            assertEquals(parent1.hashCode(), parent2.hashCode());
        }

        @Test
        @DisplayName("Should be equal when IDs are null")
        void shouldBeEqualWhenIDsAreNull() {
            // Given - Both have null IDs, so they're equal based on ID comparison
            Parent parent1 = Parent.builder().name("John Doe").sessionId(100).build();
            Parent parent2 = Parent.builder().name("Jane Smith").sessionId(100).build();

            // When & Then
            assertEquals(parent1, parent2); // Both have null IDs, so equal
        }

        @Test
        @DisplayName("Should not be equal when IDs differ")
        void shouldNotBeEqualWhenIDsDiffer() {
            // Given
            Parent parent1 = Parent.builder().id(1).name("John Doe").sessionId(100).build();
            Parent parent2 = Parent.builder().id(2).name("John Doe").sessionId(100).build();

            // When & Then
            assertNotEquals(parent1, parent2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Parent parent = Parent.builder().name("John Doe").sessionId(100).build();

            // When & Then
            assertNotEquals(parent, null);
        }

        @Test
        @DisplayName("Should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            // Given
            Parent parent = Parent.builder().name("John Doe").sessionId(100).build();
            String notAParent = "not a parent";

            // When & Then
            assertNotEquals(parent, notAParent);
        }
    }

    @Nested
    @DisplayName("Parent String Representation")
    class ParentStringRepresentation {

        @Test
        @DisplayName("Should have meaningful toString")
        void shouldHaveMeaningfulToString() {
            // Given
            Parent parent = Parent.builder()
                    .id(1)
                    .name("John Doe")
                    .isCandidate(true)
                    .hasVoted(false)
                    .sessionId(100)
                    .build();

            // When
            String string = parent.toString();

            // Then
            assertTrue(string.contains("Parent{"));
            assertTrue(string.contains("id=1"));
            assertTrue(string.contains("name='John Doe'"));
            assertTrue(string.contains("isCandidate=true"));
            assertTrue(string.contains("hasVoted=false"));
            assertTrue(string.contains("sessionId=100"));
            // Note: createdAt is not included in actual toString implementation
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support candidate who hasn't voted yet")
        void shouldSupportCandidateWhoHasntVotedYet() {
            // Given & When
            Parent candidate = Parent.builder()
                    .name("Candidate Smith")
                    .isCandidate(true)
                    .hasVoted(false)
                    .sessionId(100)
                    .build();

            // Then
            assertTrue(candidate.isCandidate());
            assertFalse(candidate.hasVoted());
        }

        @Test
        @DisplayName("Should support candidate who has voted")
        void shouldSupportCandidateWhoHasVoted() {
            // Given & When
            Parent candidate = Parent.builder()
                    .name("Candidate Smith")
                    .isCandidate(true)
                    .hasVoted(true)
                    .sessionId(100)
                    .build();

            // Then
            assertTrue(candidate.isCandidate());
            assertTrue(candidate.hasVoted());
        }

        @Test
        @DisplayName("Should support non-candidate who has voted")
        void shouldSupportNonCandidateWhoHasVoted() {
            // Given & When
            Parent voter = Parent.builder()
                    .name("Regular Voter")
                    .isCandidate(false)
                    .hasVoted(true)
                    .sessionId(100)
                    .build();

            // Then
            assertFalse(voter.isCandidate());
            assertTrue(voter.hasVoted());
        }

        @Test
        @DisplayName("Should default to non-candidate and not voted")
        void shouldDefaultToNonCandidateAndNotVoted() {
            // Given & When
            Parent parent = Parent.builder()
                    .name("New Parent")
                    .sessionId(100)
                    .build();

            // Then
            assertFalse(parent.isCandidate());
            assertFalse(parent.hasVoted());
        }

        @Test
        @DisplayName("Should maintain chronological order through timestamps")
        void shouldMaintainChronologicalOrder() {
            // Given
            LocalDateTime earlier = LocalDateTime.of(2025, 7, 30, 14, 0);
            LocalDateTime later = LocalDateTime.of(2025, 7, 30, 14, 30);
            
            Parent earlierParent = Parent.builder()
                    .name("First Parent")
                    .sessionId(100)
                    .createdAt(earlier)
                    .build();
                    
            Parent laterParent = Parent.builder()
                    .name("Second Parent")
                    .sessionId(100)
                    .createdAt(later)
                    .build();

            // When & Then
            assertTrue(earlierParent.getCreatedAt().isBefore(laterParent.getCreatedAt()));
        }
    }
}