package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {
  private String fileName;
  private String courseCode;
  private LocalDateTime createdAt;
}
