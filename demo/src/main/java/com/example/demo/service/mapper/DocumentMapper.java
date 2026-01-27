package com.example.demo.service.mapper;

import com.example.demo.dto.DocumentDto;
import com.example.demo.entity.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentMapper {

  public DocumentDto toDto(Document document) {
    if (document == null) {
      return null;
    }

    return DocumentDto.builder()
            .id(document.getId())
            .fileName(document.getFileName())
            .contentType(document.getContentType())
            .size(document.getSize())
            .status(document.getStatus())
            .indexed(document.isIndexed())
            .errorMessage(document.getErrorMessage())
            .createdAt(document.getCreatedAt())
            .conversationId(
                    document.getConversation() != null
                            ? document.getConversation().getId()
                            : null
            )
            .build();
  }

  public List<DocumentDto> toDtoList(List<Document> documents) {
    if (documents == null) {
      return null;
    }

    return documents.stream()
            .map(this::toDto)
            .toList();
  }
}
