package com.example.demo.services;

import com.example.demo.exception.UploadFileServiceException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadFileService {
  private static final String UPLOAD_DIR = "uploads/";


  public Resource toResource(MultipartFile file) {
   try {
     return new InputStreamResource(file.getInputStream()) {
         public String getFileName() {
           return file.getOriginalFilename();
         }
       };
   } catch (IOException e) {
     throw new UploadFileServiceException("Failed to read uploaded file", e);
   }
  }

  public void uploadFile(MultipartFile file) {
    try {
      Path uploadPath = Paths.get(UPLOAD_DIR);

      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }
      Path filePath = uploadPath.resolve(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
      file.transferTo(filePath.toFile());

    } catch (IOException e) {
      throw new UploadFileServiceException("Failed upload file", e);
    }
  }
}
