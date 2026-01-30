# Dokumentacja RAG (PL)

## Cel systemu
System realizuje architekturę RAG (Retrieval-Augmented Generation). Oznacza to, że model LLM nie odpowiada „z pamięci”, tylko na podstawie kontekstu wyszukanego w dokumentach użytkownika. Kontekst jest przypisany do konkretnej rozmowy (conversation), więc każda rozmowa ma własny zakres wiedzy.

## Co to jest RAG i jak działa w aplikacji
RAG łączy dwa kroki:
1. **Retrieval (wyszukiwanie)** – system wyszukuje najbardziej pasujące fragmenty dokumentów.
2. **Generation (generowanie)** – model LLM generuje odpowiedź, korzystając wyłącznie z tych fragmentów.

W tej aplikacji działa to tak:
- użytkownik uploaduje dokumenty,
- dokumenty są dzielone na fragmenty (chunki) i indeksowane,
- podczas pytania system wyszukuje najlepsze fragmenty,
- te fragmenty są wstawiane do promptu jako `{{context}}`,
- model generuje odpowiedź w oparciu o kontekst i historię rozmowy.

## Co to jest baza wektorowa i jak jest używana
Baza wektorowa przechowuje **wektory (embeddingi)** fragmentów dokumentów. Embedding to numeryczna reprezentacja znaczenia tekstu.

W tej aplikacji:
- każdy chunk dokumentu jest zamieniany na embedding,
- embeddingi są zapisywane w `VectorStore` (pgvector),
- przy pytaniu wykonywane jest wyszukiwanie podobieństwa (similarity search),
- system pobiera topK najbardziej podobnych fragmentów i składa z nich kontekst.

Wyszukiwanie jest dodatkowo ograniczone do danej rozmowy przez metadane:
`conversationId == {id}`.

## Komponenty backendu (Spring Boot)

### 1) Upload dokumentów i indeksowanie
Ścieżka: `POST /api/v1/chat/conversations/{conversationId}/documents`

Przepływ:
1. `ChatController.uploadFile()` wywołuje `ChatService.uploadFile()`.
2. `ChatService.uploadFile()` przekazuje pliki do `DocReaderService.splitFile()`.
3. `DocReaderService.splitFile()`:
   - zapisuje metadane dokumentu w bazie (`DocumentService.save`)
   - czyta plik przez `TikaDocumentReader` (Apache Tika)
   - dzieli tekst na chunki przez `TokenTextSplitter`
   - zapisuje chunki do `VectorStore` (pgvector) z metadanymi:
     - `conversationId`
     - `filename`
     - `contentType`
     - `upload_time`
   - oznacza dokument jako `INDEXED` w bazie (`DocumentService.markIndexed`)

Kluczowy fragment: `DocReaderService` uzupełnia metadane `conversationId`, dzięki czemu wyszukiwanie kontekstu odbywa się tylko w ramach danej rozmowy.

### 2) Generowanie odpowiedzi (RAG)
Ścieżka: `POST /api/v1/chat`

Przepływ:
1. `ChatController.postMessage()` przekazuje `ChatRequest` do `ChatService.chat()`.
2. `ChatService.chat()`:
   - zapisuje wiadomość użytkownika w bazie (`MessageService.save`)
   - buduje prompt w `buildPrompt()`
   - zapisuje odpowiedź asystenta w bazie (`MessageService.save`)
3. `ChatService.buildPrompt()`:
   - pobiera podobne fragmenty z `VectorStore` (topK = 4)
   - filtruje po `conversationId`:
     ```
     filterExpression("conversationId == " + conversationId)
     ```
   - składa kontekst z chunków
   - pobiera historię rozmowy (ostatnie 10 wiadomości)
   - pobiera aktywny system prompt z `SystemPromptService.getPromptForConversation()`
   - wywołuje model przez `ChatClient` (Ollama)

Wynik: odpowiedź modelu oparta o kontekst z dokumentów i historię rozmowy.

### 3) System Prompts (prompt engineering)
Ścieżka: `GET/POST/PUT/DELETE /api/v1/system-prompt`

Każda rozmowa ma przypisany aktywny system prompt (`Conversation.systemPrompt`).  
Prompt jest szablonem z `{{context}}`, który wypełnia się aktualnym kontekstem z dokumentów.

Tworzenie promptu użytkownika:
- `SystemPromptServiceImpl.save()` ustawia:
  - `code` (unikalny)
  - `system = false`
  - `active = true`

System prompty bazowe są seedowane w `SystemPromptSeeder` i mają `system = true`.

## Komponenty frontendowe (React)

### 1) Komunikacja z API
Plik: `frontend/src/services/api.ts`

Frontend komunikuje się z backendem przez `/api/v1`:
- `POST /chat` – wysłanie wiadomości
- `POST /chat/conversations/{id}/documents` – upload plików
- `GET /conversation/{id}/messages` – historia rozmowy
- `GET/POST/DELETE /system-prompt` – zarządzanie promptami
- `GET/POST/PUT/DELETE /conversation` – rozmowy

Autoryzacja JWT jest obsługiwana przez axios interceptor.

### 2) Logika rozmowy w UI
Pliki:
- `ChatPage.tsx` – zarządzanie listą rozmów, wybór aktywnej rozmowy
- `ChatInterface.tsx` – wysyłanie wiadomości, upload, wybór promptu

Przepływ czatu w UI:
1. Użytkownik wybiera rozmowę.
2. UI pobiera historię wiadomości z `/conversation/{id}/messages`.
3. Użytkownik wysyła wiadomość → `POST /chat`.
4. UI dopisuje odpowiedź do historii.

### 3) Wybór i tworzenie promptów
UI umożliwia:
- wybór aktywnego promptu dla rozmowy (select)
- tworzenie promptu użytkownika (modal)
- usuwanie promptu (jeśli nie jest systemowy)

Wybrany prompt jest zapisywany do rozmowy przez:
`PUT /conversation/{conversationId}` z `systemPromptId`.

## Jak system komunikuje się z użytkownikiem (end-to-end)

1. **Użytkownik tworzy rozmowę** (frontend → backend).
2. **Użytkownik uploaduje dokumenty**:
   - dokumenty są indeksowane w vector store
   - status dokumentu przechodzi na `INDEXED`
3. **Użytkownik zadaje pytanie**:
   - backend wyszukuje kontekst w vector store
   - buduje prompt na bazie system promptu + kontekstu
   - model generuje odpowiedź
4. **Odpowiedź wraca do UI** i jest zapisywana w historii rozmowy.

## Diagnostyka i typowe problemy

1. **Brak odpowiedzi mimo danych w PDF**  
   Najczęściej:
   - dokument nie został poprawnie zindeksowany
   - kontekst nie został znaleziony przez wyszukiwarkę (vector store)
   - pytanie jest zbyt ogólne lub używa innych sformułowań niż dokument

2. **Odpowiedź „nie posiadam informacji”**  
   To efekt system promptu, który wymusza odpowiedzi tylko z kontekstu.

3. **Odpowiedzi nie w języku oczekiwanym**  
   Zależy od treści system promptu i instrukcji w nim zawartych.

## Miejsca w kodzie (skrót)
- RAG i generowanie odpowiedzi: `ChatService.buildPrompt()`
- Indeksowanie dokumentów: `DocReaderService.splitFile()`
- Kontekst i system prompt: `SystemPromptServiceImpl.getPromptForConversation()`
- Upload i czat API: `ChatController`
- Historia wiadomości: `MessageController`
