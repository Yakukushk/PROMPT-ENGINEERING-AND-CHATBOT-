package com.example.demo.repository;

import com.example.demo.dto.SystemPromptDto;
import com.example.demo.entity.SystemPrompt;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemPromptRepository extends JpaRepository<SystemPrompt, Long> {
  List<SystemPrompt> findSystemPromptByUser(User user);
  boolean existsByCode(String code);

  Optional<SystemPrompt> findByCode(String code);
}
