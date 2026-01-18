package com.example.demo.services;


import com.example.demo.exception.ChatServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChatService {
  private final OllamaChatModel ollamaChatModel;
  private final VectorStore vectorStore;
  private final DocReaderService docReaderService;

  public ChatService(OllamaChatModel ollamaChatModel, VectorStore vectorStore, DocReaderService docReaderService) {
    this.ollamaChatModel = ollamaChatModel;
    this.vectorStore = vectorStore;
    this.docReaderService = docReaderService;
  }

  public void uploadFile(MultipartFile file) {
    docReaderService.splitFile(file);
  }

  public String buildPrompt(String message) {
    try {
      QuestionAnswerAdvisor advisor =
              QuestionAnswerAdvisor.builder(vectorStore)
                      .build();

      return ChatClient.builder(ollamaChatModel)
              .build()
              .prompt()
              .advisors(advisor)
              .user(message)
              .call()
              .content();
    } catch (RuntimeException e) {
      throw new ChatServiceException("Failed send prompt", e);
    }
  }
}
