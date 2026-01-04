package com.bavarians.graphql.repository;

import com.bavarians.graphql.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findBySessionId(String sessionId);

    List<ChatSession> findByUserId(Long userId);

    List<ChatSession> findByStatus(String status);

    @Query("SELECT c FROM chat_session c WHERE c.expiresAt < ?1")
    List<ChatSession> findExpiredSessions(Date currentDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM chat_session c WHERE c.expiresAt < ?1")
    void deleteExpiredSessions(Date currentDate);

    @Modifying
    @Transactional
    @Query("DELETE FROM chat_session c WHERE c.createdAt < ?1")
    void deleteOldSessions(Date olderThan);
}
