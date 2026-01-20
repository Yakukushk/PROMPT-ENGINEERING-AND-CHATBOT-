package com.example.demo.exception;

public class DocReaderServiceException extends RuntimeException {
  public DocReaderServiceException(String message, String index, Throwable cause) {
    super(message + " [index=" + index + "]", cause);
  }
}