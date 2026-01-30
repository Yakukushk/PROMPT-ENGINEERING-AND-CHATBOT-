package com.example.demo.controller;

import com.example.demo.dto.MessageDto;
import com.example.demo.service.interfaces.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Message API endpoints")
@RequestMapping("/api/v1/conversation")
public class MessageController {

  private final MessageService messageService;

  @GetMapping("/{conversationId}/messages")
  @Operation(summary = "Get messages by conversation")
  public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long conversationId) {
    return ResponseEntity.ok(messageService.findByConversationId(conversationId));
  }
}
