package com.example.demo.repository;

import com.example.demo.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findByConversationId(Long conversationId);

  Page<Message> findByConversationId(Long conversationId, Pageable pageable);

  @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC LIMIT :limit")
  List<Message> findTopByConversationIdOrderByCreatedAtDesc(
          @Param("conversationId") Long conversationId,
          @Param("limit") int limit);
  long countByConversationId(Long conversationId);
}
