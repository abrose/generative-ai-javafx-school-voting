// ABOUTME: Immutable model class representing a single vote cast by a parent
// ABOUTME: Ensures vote integrity through immutability and validation

package com.school.voting.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Vote {
    private final Integer id;
    private final Integer voterId;
    private final Integer candidateId;
    private final Integer sessionId;
    private final LocalDateTime votedAt;

    public Vote(Integer id, Integer voterId, Integer candidateId, Integer sessionId, LocalDateTime votedAt) {
        this.id = id;
        this.voterId = Objects.requireNonNull(voterId, "Voter ID cannot be null");
        this.candidateId = Objects.requireNonNull(candidateId, "Candidate ID cannot be null");
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.votedAt = votedAt != null ? votedAt : LocalDateTime.now();
        
        validate();
    }

    public Vote(Integer voterId, Integer candidateId, Integer sessionId) {
        this(null, voterId, candidateId, sessionId, LocalDateTime.now());
    }

    private void validate() {
        if (voterId.equals(candidateId)) {
            throw new IllegalArgumentException("A parent cannot vote for themselves");
        }
    }

    public Integer getId() {
        return id;
    }

    public Integer getVoterId() {
        return voterId;
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(voterId, vote.voterId) &&
               Objects.equals(candidateId, vote.candidateId) &&
               Objects.equals(sessionId, vote.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId, candidateId, sessionId);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voterId=" + voterId +
                ", candidateId=" + candidateId +
                ", sessionId=" + sessionId +
                ", votedAt=" + votedAt +
                '}';
    }
}