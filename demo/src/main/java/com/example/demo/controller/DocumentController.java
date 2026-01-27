package com.example.demo.controller;

import com.example.demo.service.interfaces.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Document API endpoints")
@RequestMapping("/api/v1/document")
public class DocumentController {

  private final DocumentService documentService;

  @DeleteMapping("/{documentId}")
  @Operation(summary = "Delete document by id")
  public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
    documentService.delete(documentId);
    return ResponseEntity.noContent().build();
  }
}
