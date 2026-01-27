package com.example.demo.controller;

import com.example.demo.dto.ChatDto;
import com.example.demo.dto.request.ChatRequest;
import com.example.demo.service.interfaces.MessageService;
import com.example.demo.service.ChatService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chat API endpoints")
@RequestMapping("api/v1/chat")
public class ChatController {
  private final ChatService chatService;
  private final MessageService messageService;

  @Hidden
  @PostMapping("/build/prompt/{conversationId}")
  @Operation(
          summary = "Query AI with PDF context",
          description = "Send a prompt to the Large Language Model using the uploaded PDF document as contextual reference. The AI will generate responses based on both the prompt and the PDF content."
  )
  public String buildPrompt(@RequestBody String message, @PathVariable Long conversationId) {
    return chatService.buildPrompt(conversationId, message);
  }

  @PostMapping
  @Operation(
          summary = "Submit Message"
  )
  public ResponseEntity<ChatDto> postMessage(@RequestBody ChatRequest request) {
      ChatDto chatDto = chatService.chat(request);
       return ResponseEntity.ok(chatDto);
  }

  @PostMapping(
          value = "/conversations/{conversationId}/documents",
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @Operation(
          summary = "Query AI with PDF context",
          description = "Send a prompt to the Large Language Model using the uploaded PDF document as contextual reference. The AI will generate responses based on both the prompt and the PDF content."
  )
  public ResponseEntity<Void> uploadFile(
          @PathVariable Long conversationId,
          @RequestPart("files") List<MultipartFile> files) {
    chatService.uploadFile(files, conversationId);
    return ResponseEntity.accepted().build();
  }
}
