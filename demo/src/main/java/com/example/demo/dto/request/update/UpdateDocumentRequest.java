package com.example.demo.dto.request.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDocumentRequest {
  private String filename;
  private String courseCode;
  private Long conversationId;
}
