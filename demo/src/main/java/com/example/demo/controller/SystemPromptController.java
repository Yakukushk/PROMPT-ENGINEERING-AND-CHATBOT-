package com.example.demo.controller;

import com.example.demo.dto.SystemPromptDto;
import com.example.demo.dto.request.SystemPromptRequest;
import com.example.demo.dto.request.update.UpdateSystemPromptRequest;
import com.example.demo.service.interfaces.SystemPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "System Prompt API endpoints")
@RequestMapping("/api/v1/system-prompt")
public class SystemPromptController {
  private final SystemPromptService systemPromptService;

  @Operation(summary = "Get all system prompts", description = "Retrieve a list of all system prompts")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping
  public ResponseEntity<List<SystemPromptDto>> getAllSystemPrompts() {
    List<SystemPromptDto> prompts = systemPromptService.findAll();
    return ResponseEntity.ok(prompts);
  }

  @Operation(summary = "Get system prompt by ID", description = "Retrieve a specific system prompt by its ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved system prompt"),
          @ApiResponse(responseCode = "404", description = "System prompt not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/{id}")
  public ResponseEntity<SystemPromptDto> getSystemPromptById(
          @Parameter(description = "ID of the system prompt to retrieve", required = true)
          @PathVariable Long id) {
    return systemPromptService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Get system prompts by IDs", description = "Retrieve multiple system prompts by their IDs")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved system prompts"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/batch")
  public ResponseEntity<List<SystemPromptDto>> getSystemPromptsByIds(
          @Parameter(description = "List of system prompt IDs", required = true)
          @RequestParam List<Long> ids) {
    List<SystemPromptDto> prompts = systemPromptService.findAllById(ids);
    return ResponseEntity.ok(prompts);
  }

  @Operation(summary = "Create a new system prompt", description = "Create a new system prompt for a user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "System prompt created successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PostMapping
  public ResponseEntity<SystemPromptDto> createSystemPrompt(
          @Parameter(description = "System prompt data", required = true)
          @Valid @RequestBody SystemPromptRequest request) {
    SystemPromptDto createdPrompt = systemPromptService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPrompt);
  }

  @Operation(summary = "Update a system prompt", description = "Update an existing system prompt")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "System prompt updated successfully"),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "404", description = "System prompt or user not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @PutMapping("/{id}")
  public ResponseEntity<SystemPromptDto> updateSystemPrompt(
          @Parameter(description = "ID of the system prompt to update", required = true)
          @PathVariable Long id,
          @Parameter(description = "Updated system prompt data", required = true)
          @Valid @RequestBody UpdateSystemPromptRequest request) {
    SystemPromptDto updatedPrompt = systemPromptService.update(id, request);
    return ResponseEntity.ok(updatedPrompt);
  }

  @Operation(summary = "Delete a system prompt", description = "Delete a system prompt by its ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "System prompt deleted successfully"),
          @ApiResponse(responseCode = "404", description = "System prompt not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSystemPrompt(
          @Parameter(description = "ID of the system prompt to delete", required = true)
          @PathVariable Long id) {
    systemPromptService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get prompt for conversation", description = "Get formatted prompt for a specific conversation")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved formatted prompt"),
          @ApiResponse(responseCode = "400", description = "No active prompt for conversation"),
          @ApiResponse(responseCode = "404", description = "Conversation not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  @GetMapping("/conversation/{conversationId}")
  public ResponseEntity<String> getPromptForConversation(
          @Parameter(description = "ID of the conversation", required = true)
          @PathVariable Long conversationId,
          @Parameter(description = "Context to insert into the prompt template")
          @RequestParam(required = false) String context) {
    String formattedPrompt = systemPromptService.getPromptForConversation(conversationId, context);
    return ResponseEntity.ok(formattedPrompt);
  }
}