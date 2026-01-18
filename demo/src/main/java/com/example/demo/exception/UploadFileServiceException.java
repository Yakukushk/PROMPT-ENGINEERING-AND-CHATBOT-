package com.example.demo.exception;

public class UploadFileServiceException extends RuntimeException {
  public UploadFileServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
