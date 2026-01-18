package com.example.demo.controller;
import com.example.demo.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping("/upload")
  @Operation(
          summary = "Query AI with PDF context",
          description = "Send a prompt to the Large Language Model using the uploaded PDF document as contextual reference. The AI will generate responses based on both the prompt and the PDF content."
  )
  public ResponseEntity<Void> uploadFile(@RequestBody MultipartFile file) {
    chatService.uploadFile(file);
    return ResponseEntity.ok().build();
  }
}
