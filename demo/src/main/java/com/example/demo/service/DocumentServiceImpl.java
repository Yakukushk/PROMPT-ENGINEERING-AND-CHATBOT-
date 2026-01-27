package com.example.demo.service;

import com.example.demo.dto.DocumentDto;
import com.example.demo.dto.request.DocumentRequest;
import com.example.demo.dto.request.update.UpdateDocumentRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Document;
import com.example.demo.entity.enums.DocumentStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.interfaces.DocumentService;
import com.example.demo.service.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {

  private final DocumentRepository documentRepository;
  private final DocumentMapper documentMapper;
  private final ConversationRepository conversationRepository;

  @Override
  public List<DocumentDto> findAll() {
    log.debug("Finding All Documents");
    return documentRepository.findAll().stream()
            .map(documentMapper::toDto)
            .toList();
  }

  @Override
  public Optional<DocumentDto> findById(Long id) {
    return documentRepository.findById(id)
            .map(documentMapper::toDto);
  }

  @Override
  public List<DocumentDto> findAllById(Iterable<Long> ids) {
    return documentRepository.findAllById(ids).stream()
            .map(documentMapper::toDto)
            .toList();
  }

  @Override
  @Transactional
  public DocumentDto save(DocumentRequest request) {
    if(request == null) {
      throw new IllegalArgumentException("Message request cannot be null");
    }
    Conversation conversation = conversationRepository.findById(request.getConversationId())
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));
    Document document = Document.builder()
            .conversation(conversation)
            .contentType(request.getContentType())
            .size(request.getSize())
            .indexed(false)
            .status(request.getStatus())
            .fileName(request.getFilename())
            .createdAt(LocalDateTime.now())
            .build();

    Document savedDocument = documentRepository.save(document);
    return  documentMapper.toDto(savedDocument);
  }

  @Override
  public DocumentDto update(Long aLong, UpdateDocumentRequest request) {
    return null;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document", "id", id));

    documentRepository.delete(document);
  }

  @Override
  @Transactional
  public void markIndexed(Long documentId) {
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
  document.setIndexed(true);
  document.setStatus(DocumentStatus.INDEXED);
  documentRepository.save(document);
  }

  @Override
  @Transactional
  public void markFailed(Long documentId, String errorMessage) {
    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
    document.setIndexed(false);
    document.setStatus(DocumentStatus.FAILED);
    document.setErrorMessage(errorMessage);

    documentRepository.save(document);
  }
}
