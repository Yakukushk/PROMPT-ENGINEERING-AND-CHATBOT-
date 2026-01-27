package com.example.demo.controller;

import com.example.demo.dto.ConversationDto;
import com.example.demo.dto.request.ConversationRequest;
import com.example.demo.service.interfaces.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {

  private final ConversationService conversationService;

  @GetMapping
  @Operation(
          summary = "Get All Conversation"
  )
  public ResponseEntity<List<ConversationDto>> getAllConversations() {
    return ResponseEntity.ok(conversationService.findAll());
  }

  @GetMapping("/{username}")
  @Operation(
          summary = "Get All Conversation By User"
  )
  public ResponseEntity<List<ConversationDto>> getAllCoConversationByUsername(@PathVariable String username) {
    return ResponseEntity.ok(conversationService.findConversationByUsername(username));
  }

  @PostMapping
  @Operation(
          summary = "Create Conversation"
  )
  public ResponseEntity<ConversationDto> createConversation(@RequestBody ConversationRequest request) {
    return ResponseEntity.ok(conversationService.save(request));
  }

  @DeleteMapping("/{conversationId}")
  public ResponseEntity<?> deleteConversation(@PathVariable Long conversationId) {
    conversationService.delete(conversationId);
    return ResponseEntity.ok("Conversation was deleted");
  }
}
