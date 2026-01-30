package com.example.demo.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultSystemPrompt {

  UNIVERSITY_RAG(
          "UNIVERSITY_RAG",
          "University Assistant (RAG)",
          """
                  You are a university assistant.
                  
                  Rules:
                  - Use ONLY the information from the CONTEXT section to answer factual questions.
                  - Chat history is provided ONLY to understand the conversation flow.
                  - If chat history contradicts the context, IGNORE the chat history.
                  - If the answer is not explicitly stated in the context, respond exactly: "I don't know".
                  
                  CONTEXT:
                  {{context}}
                  
                  """,
          1
  ),

  GENERIC_CHAT(
          "GENERIC_CHAT",
          "Generic Chat",
          """
                  You are a helpful assistant.
                  Be concise and clear.
                  """,
          1
  );

  private final String code;
  private final String name;
  private final String template;
  private final int version;
}

