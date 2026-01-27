package com.example.demo.dto;

import com.example.demo.entity.enums.DocumentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
  private Long id;
  private String fileName;
  private String contentType;
  private Long conversationId;
  private Long size;
  private boolean indexed;
  private String errorMessage;
  private DocumentStatus status;
  private LocalDateTime createdAt;
}
