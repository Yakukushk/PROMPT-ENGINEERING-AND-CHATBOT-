package com.example.demo.services;


import com.example.demo.exception.DocReaderServiceException;
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
public class DocReaderService {

  @Value("classpath:/pdf/spring-boot-reference.pdf")
  private Resource resource;

  private final VectorStore vectorStore;
  private final UploadFileService uploadFileService;
  TextSplitter splitter = new TokenTextSplitter(
          500,    // chunkSize
          100,    // minCharSizeChunk
          200,      // minChunkLengthToEmbed
          10000,  // maxNumChunks
          true    // keepSeparator
  );

  public DocReaderService(VectorStore vectorStore,
                          UploadFileService uploadFileService) {
    this.vectorStore = vectorStore;
    this.uploadFileService = uploadFileService;
  }

  public void splitFile(List<MultipartFile> files) {
    for (MultipartFile file : files) {
      try {
        Resource uploadedResource = uploadFileService.toResource(file);
        if (uploadedResource == null) {
          log.error("Failed to process file: {}", file.getName());
          continue;
        }
        List<Document> documentList = getDocuments(file, uploadedResource);

        List<Document> chunks = splitter.split(documentList);
        int batchSize = 20;
        for (int i = 0; i < chunks.size(); i += batchSize) {
          int end = Math.min(i + batchSize, chunks.size());
          List<Document> batch = chunks.subList(i, end);
          vectorStore.accept(batch);
        }
      } catch (Exception e) {
        throw new DocReaderServiceException("Failed to index file {}", file.getOriginalFilename(), e);
      }
    }
  }

  private static List<Document> getDocuments(MultipartFile file, Resource uploadedResource) {
    TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(uploadedResource);

    List<Document> documentList = tikaDocumentReader.read();
    String fileName = file.getOriginalFilename();

    documentList.forEach(doc -> {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("filename", fileName);
      metadata.put("source", "uploaded_file");
      metadata.put("upload_time", LocalDateTime.now().toString());
      doc.getMetadata().putAll(metadata);
    });
    return documentList;
  }
}