package com.example.demo.dto.request;

import com.example.demo.entity.enums.DocumentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {
  private String filename;
  private String contentType;
  private Long size;
  private DocumentStatus status;
  private Long conversationId;
}
