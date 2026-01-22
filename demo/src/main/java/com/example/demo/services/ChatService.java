package com.example.demo.services;


import com.example.demo.exception.ChatServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ChatService {
  private final OllamaChatModel ollamaChatModel;
  private final VectorStore vectorStore;
  private final DocReaderService docReaderService;

  public ChatService(OllamaChatModel ollamaChatModel, VectorStore vectorStore, DocReaderService docReaderService) {
    this.ollamaChatModel = ollamaChatModel;
    this.vectorStore = vectorStore;
    this.docReaderService = docReaderService;
  }

  public void uploadFile(List<MultipartFile> file) {
    docReaderService.splitFile(file);
  }

  public String buildPrompt(String message) {
    try {

      List<Document> documents = vectorStore.similaritySearch(
              SearchRequest.builder()
                      .query(message)
                      .topK(4)
                      .build()
      );
      String context = documents.stream()
              .map(Document::getText)
              .reduce("", (a, b) -> a + "\n---\n" + b);

      String systemPrompt = """
        Jesteś asystentem informacyjnym Uniwersytetu Gdańskiego.
        Odpowiadasz na pytania studentów na podstawie sylabusów.

        ZASADY:
        - Odpowiadaj WYŁĄCZNIE na temat zapytania.
        - Jeśli pytanie dotyczy "wymagań wstępnych", NIE OPISUJ celu ani zakresu przedmiotu.
        - Korzystaj tylko z informacji zawartych w kontekście.
        - Jeśli informacja nie występuje wprost — napisz: "Brak informacji w sylabusie".
        - Nie interpretuj, nie zgaduj, nie uogólniaj.
        - Styl: krótki, rzeczowy, akademicki.
        - Język: polski.

        KONTEKST:
        %s
        """.formatted(context);


      return ChatClient.builder(ollamaChatModel)
              .build()
              .prompt()
              .system(systemPrompt)
              .user(message)
              .call()
              .content();

    } catch (RuntimeException e) {
      throw new ChatServiceException("Failed send prompt", e);
    }
  }
}
