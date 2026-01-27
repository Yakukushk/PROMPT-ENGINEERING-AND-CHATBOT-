package com.example.demo.entity;

import com.example.demo.entity.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "document")
public class Document {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "filename", nullable = false, length = 255)
  private String fileName;

  @Column(name = "content_type", nullable = false, length = 50)
  private String contentType;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private DocumentStatus status;

  @Column(name = "indexed")
  private boolean indexed = false;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
          name = "conversation_id",
          nullable = false,
          foreignKey = @ForeignKey(name = "fk_document_conversation")
  )
  private Conversation conversation;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
