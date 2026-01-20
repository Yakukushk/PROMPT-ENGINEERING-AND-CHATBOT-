package com.example.demo.controller;

import com.example.demo.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/chat")
@Tag(name = "Chat API Controller")
public class ChatController {
  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @PostMapping
  @Operation(
          summary = "Query AI with PDF context",
          description = "Send a prompt to the Large Language Model using the uploaded PDF document as contextual reference. The AI will generate responses based on both the prompt and the PDF content."
  )
  public String buildPrompt(@RequestBody String message) {
    return chatService.buildPrompt(message);
  }

  @PostMapping(
          value = "/upload",
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  @Operation(
          summary = "Query AI with PDF context",
          description = "Send a prompt to the Large Language Model using the uploaded PDF document as contextual reference. The AI will generate responses based on both the prompt and the PDF content."
  )
  public ResponseEntity<Void> uploadFile(
          @RequestPart("files") List<MultipartFile> files) {
    chatService.uploadFile(files);
    return ResponseEntity.accepted().build();
  }
}
