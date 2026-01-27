package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API endpoints")
@RequestMapping("api/v1/user")
public class UserController {

private final UserService userService;

  @GetMapping("/username")
  public ResponseEntity<UserDto> getUserByUsername(
          @RequestParam(required = false) String username,
          Principal principal
  ) {
    String resolvedUsername = username != null ? username : principal.getName();
    UserDto userDto = userService.findByUsername(resolvedUsername);
    if (userDto == null) {
      return ResponseEntity.notFound().build();
    }
    userDto.setPassword(null);
    return ResponseEntity.ok(userDto);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    userService.delete(userId);
    return ResponseEntity.ok("User was deleted");
  }
}
