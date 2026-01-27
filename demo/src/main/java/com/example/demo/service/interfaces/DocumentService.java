package com.example.demo.service.interfaces;

import com.example.demo.dto.DocumentDto;
import com.example.demo.dto.request.DocumentRequest;
import com.example.demo.dto.request.update.UpdateDocumentRequest;
import com.example.demo.service.interfaces.base.CrudService;

public interface DocumentService extends CrudService<DocumentDto, Long, DocumentRequest, UpdateDocumentRequest> {
  void markIndexed(Long documentId);
  void markFailed(Long documentId, String errorMessage);
}
