// ABOUTME: Model class representing a parent who can vote and potentially be voted for
// ABOUTME: Uses builder pattern for flexible object construction

package com.school.voting.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Parent {
    private final Integer id;
    private final String name;
    private final boolean isCandidate;
    private final boolean hasVoted;
    private final Integer sessionId;
    private final LocalDateTime createdAt;

    private Parent(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.isCandidate = builder.isCandidate;
        this.hasVoted = builder.hasVoted;
        this.sessionId = builder.sessionId;
        this.createdAt = builder.createdAt;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean canVote() {
        return !hasVoted;
    }

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Parent name cannot be empty");
        }
        if (sessionId == null) {
            throw new IllegalStateException("Parent must belong to a session");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parent parent = (Parent) o;
        return Objects.equals(id, parent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Parent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isCandidate=" + isCandidate +
                ", hasVoted=" + hasVoted +
                ", sessionId=" + sessionId +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .name(this.name)
                .isCandidate(this.isCandidate)
                .hasVoted(this.hasVoted)
                .sessionId(this.sessionId)
                .createdAt(this.createdAt);
    }

    public static class Builder {
        private Integer id;
        private String name;
        private boolean isCandidate = false;
        private boolean hasVoted = false;
        private Integer sessionId;
        private LocalDateTime createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder isCandidate(boolean isCandidate) {
            this.isCandidate = isCandidate;
            return this;
        }

        public Builder hasVoted(boolean hasVoted) {
            this.hasVoted = hasVoted;
            return this;
        }

        public Builder sessionId(Integer sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Parent build() {
            Parent parent = new Parent(this);
            parent.validate();
            return parent;
        }
    }
}