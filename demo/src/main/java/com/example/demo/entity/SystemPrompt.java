package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_prompt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPrompt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String template;

  @Column(nullable = false)
  private Integer version;

  @Column(nullable = false, unique = true, length = 100)
  private String code;

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = true,
          foreignKey = @ForeignKey(name = "fk_system_prompt_user"))
  private User user;

  @Column(nullable = false)
  private Boolean system = true;

  @PrePersist
  void onCreate() {
    createdAt = LocalDateTime.now();
    if (active == null) active = false;
  }

  public String format(String context) {
    return template.replace("{{context}}", context == null ? "" : context);
  }
}
