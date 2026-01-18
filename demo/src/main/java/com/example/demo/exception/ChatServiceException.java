package com.example.demo.exception;

public class ChatServiceException extends RuntimeException {
  public ChatServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
