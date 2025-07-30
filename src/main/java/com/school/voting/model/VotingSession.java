// ABOUTME: Model class representing a voting session for a specific class
// ABOUTME: Manages session lifecycle states and timing information

package com.school.voting.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class VotingSession {
    public enum Status {
        SETUP("Setup Phase"),
        VOTING("Voting in Progress"),
        COMPLETED("Voting Completed");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Integer id;
    private final String className;
    private final Status status;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;

    private VotingSession(Builder builder) {
        this.id = builder.id;
        this.className = builder.className;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.completedAt = builder.completedAt;
        
        validate();
    }

    private void validate() {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalStateException("Class name cannot be empty");
        }
        if (status == null) {
            throw new IllegalStateException("Status cannot be null");
        }
        if (status == Status.COMPLETED && completedAt == null) {
            throw new IllegalStateException("Completed session must have completion time");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public boolean isActive() {
        return status != Status.COMPLETED;
    }

    public boolean canStartVoting() {
        return status == Status.SETUP;
    }

    public boolean isVoting() {
        return status == Status.VOTING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VotingSession that = (VotingSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VotingSession{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .className(this.className)
                .status(this.status)
                .createdAt(this.createdAt)
                .completedAt(this.completedAt);
    }

    public static class Builder {
        private Integer id;
        private String className;
        private Status status = Status.SETUP;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime completedAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder className(String className) {
            this.className = className;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder status(String statusStr) {
            this.status = Status.valueOf(statusStr);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public VotingSession build() {
            return new VotingSession(this);
        }
    }
}