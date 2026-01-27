package com.example.demo.service;


import com.example.demo.dto.DocumentDto;
import com.example.demo.dto.request.DocumentRequest;
import com.example.demo.entity.enums.DocumentStatus;
import com.example.demo.exception.DocReaderServiceException;
import com.example.demo.service.interfaces.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocReaderService {

  @Value("classpath:/pdf/spring-boot-reference.pdf")
  private Resource resource;

  private final VectorStore vectorStore;
  private final UploadFileService uploadFileService;
  private final DocumentService  documentService;

  TextSplitter splitter = new TokenTextSplitter(
          500,    // chunkSize
          100,    // minCharSizeChunk
          200,      // minChunkLengthToEmbed
          10000,  // maxNumChunks
          true    // keepSeparator
  );

  public void splitFile(List<MultipartFile> files, Long conversationId) {
    for (MultipartFile file : files) {

      DocumentDto documentDto = documentService.save(DocumentRequest.builder()
              .conversationId(conversationId)
              .filename(file.getOriginalFilename())
              .contentType(file.getContentType())
              .size(file.getSize())
              .status(DocumentStatus.UPLOADED)
              .build());

      try {
        Resource uploadedResource = uploadFileService.toResource(file);
        if (uploadedResource == null) {
          log.error("Failed to process file: {}", file.getName());
          continue;
        }
        List<Document> documentList = getDocuments(file, uploadedResource, conversationId);

        List<Document> chunks = splitter.split(documentList);
        int batchSize = 20;
        for (int i = 0; i < chunks.size(); i += batchSize) {
          int end = Math.min(i + batchSize, chunks.size());
          List<Document> batch = chunks.subList(i, end);
          vectorStore.accept(batch);
        }
        documentService.markIndexed(documentDto.getId());
      } catch (Exception e) {
        documentService.markFailed(documentDto.getId(), documentDto.getErrorMessage());
        throw new DocReaderServiceException("Failed to index file {}", file.getOriginalFilename(), e);
      }
    }
  }

  private static List<Document> getDocuments(MultipartFile file, Resource uploadedResource, Long conversationId) {
    TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(uploadedResource);

    List<Document> documentList = tikaDocumentReader.read();
    String fileName = file.getOriginalFilename();
    String fileContent = file.getContentType();

    documentList.forEach(doc -> {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("conversationId", conversationId);
      metadata.put("filename", fileName);
      metadata.put("contentType", fileContent);
      metadata.put("upload_time", LocalDateTime.now().toString());
      doc.getMetadata().putAll(metadata);
    });
    return documentList;
  }
}