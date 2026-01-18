package com.example.demo.services;


import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocReaderService {

  @Value("classpath:/pdf/spring-boot-reference.pdf")
  private Resource resource;

  private final VectorStore vectorStore;
  private final UploadFileService uploadFileService;

  public DocReaderService(VectorStore vectorStore, UploadFileService uploadFileService) {
    this.vectorStore = vectorStore;
    this.uploadFileService = uploadFileService;
  }

  public void splitFile(MultipartFile file) {
    Resource uploadedrResource = uploadFileService.toResource(file);
    TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(uploadedrResource != null ? uploadedrResource : resource);

    TextSplitter splitter = new TokenTextSplitter(
            256,    // chunkSize
            350,    // minCharSizeChunk
            5,      // minChunkLengthToEmbed
            10000,  // maxNumChunks
            true    // keepSeparator
    );

    List<Document> documentList = splitter.split(tikaDocumentReader.read());

    int batchSize = 10;
    for (int i = 0; i < documentList.size(); i += batchSize) {
      int end = Math.min(i + batchSize, documentList.size());
      List<Document> batch = documentList.subList(i, end);
      vectorStore.accept(batch);
    }
  }
}