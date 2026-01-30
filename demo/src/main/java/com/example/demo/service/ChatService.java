package com.example.demo.service;


import com.example.demo.dto.ChatDto;
import com.example.demo.dto.MessageDto;
import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.MessageRequest;
import com.example.demo.entity.enums.MessageRole;
import com.example.demo.exception.ChatServiceException;
import com.example.demo.service.interfaces.DocumentService;
import com.example.demo.service.interfaces.MessageService;
import com.example.demo.service.interfaces.SystemPromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
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
  private final MessageService messageService;
  private final SystemPromptService systemPromptService;

  public void uploadFile(List<MultipartFile> files, Long conversationId) {
    docReaderService.splitFile(files, conversationId);
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

  public String buildPrompt(Long conversationId, String userMessage) {

    try {

      List<Document> documents = vectorStore.similaritySearch(
              SearchRequest.builder()
                      .query(userMessage)
                      .filterExpression("conversationId == " + conversationId)
                      .topK(4)
                      .build()
      );

      String context = documents.stream()
              .map(Document::getText)
              .reduce("", (a, b) -> a + "\n---\n" + b);

      List<MessageDto> history = messageService.findByConversationId(conversationId);

      int maxHistory = 10;
      if (history.size() > maxHistory) {
        history = history.subList(history.size() - maxHistory, history.size());
      }

      String historyText = history.stream()
              .map(m -> m.getRole().name() + ": " + m.getContent())
              .reduce("", (a, b) -> a + "\n" + b);

      String systemPrompt =
              systemPromptService.getPromptForConversation(conversationId, context)
                      + "\n\nCHAT HISTORY (reference only, not a source of truth):\n"
                      + historyText;
      
      ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();

      return chatClient.prompt()
              .system(systemPrompt)
              .user(userMessage)
              .call()
              .content();

    } catch (RuntimeException e) {
      log.error("Chat failed", e);
      throw new ChatServiceException("Failed to send prompt", e);
    }
  }
}
