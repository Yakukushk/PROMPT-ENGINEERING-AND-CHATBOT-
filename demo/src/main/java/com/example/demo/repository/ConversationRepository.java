package com.example.demo.repository;

import com.example.demo.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  @Modifying
  @Query("UPDATE Conversation c SET c.messageCount = c.messageCount + 1 WHERE c.id = :id")
  void incrementMessageCount(@Param("id") Long id);
}
