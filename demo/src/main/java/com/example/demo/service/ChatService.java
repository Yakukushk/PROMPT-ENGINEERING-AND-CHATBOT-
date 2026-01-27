package com.example.demo.service;


import com.example.demo.dto.ChatDto;
import com.example.demo.dto.DocumentDto;
import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.DocumentRequest;
import com.example.demo.dto.request.MessageRequest;
import com.example.demo.entity.enums.MessageRole;
import com.example.demo.exception.ChatServiceException;
import com.example.demo.prompt.SystemPrompt;
import com.example.demo.service.interfaces.DocumentService;
import com.example.demo.service.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
  private final OllamaChatModel ollamaChatModel;
  private final VectorStore vectorStore;
  private final DocReaderService docReaderService;
  private final DocumentService documentService;
  private final MessageService messageService;
  private final SystemPrompt systemPrompt;

  public void uploadFile(List<MultipartFile> file, Long conversationId) {
    docReaderService.splitFile(file, conversationId);
  }

  public ChatDto chat(@RequestBody ChatRequest chatRequest) {

    if (chatRequest.getMessage() == null || chatRequest.getMessage().isBlank()) {
      throw new IllegalArgumentException("Message cannot be empty");
    }

     messageService.save(
             MessageRequest.builder()
                     .conversationId(chatRequest.getConversationId())
                     .content(chatRequest.getMessage())
                     .role(MessageRole.USER)
                     .build()
     );

    String answer = buildPrompt(
            chatRequest.getConversationId(),
            chatRequest.getMessage()
    );

    messageService.save(
            MessageRequest.builder()
                    .conversationId(chatRequest.getConversationId())
                    .content(answer)
                    .role(MessageRole.ASSISTANT)
                    .build()
    );
    return new ChatDto(answer);
  }

  public String buildPrompt(Long conversationId, String message) {
    try {

      List<Document> documents = vectorStore.similaritySearch(
              SearchRequest.builder()
                      .query(message)
                      .filterExpression("conversationId == " + conversationId)
                      .topK(4)
                      .build()
      );
      String context = documents.stream()
              .map(Document::getText)
              .reduce("", (a, b) -> a + "\n---\n" + b);


      return ChatClient.builder(ollamaChatModel)
              .build()
              .prompt()
              .system(systemPrompt.getSystemPrompt(context))
              .user(message)
              .call()
              .content();

    } catch (RuntimeException e) {
      throw new ChatServiceException("Failed send prompt", e);
    }
  }
}
