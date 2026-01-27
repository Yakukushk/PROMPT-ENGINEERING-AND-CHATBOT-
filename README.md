# PROMPT-ENGINEERING-AND-CHATBOT-

PROMPT ENGINEERING AND CHATBOT (Design and analysis of a web chatbot based on prompt engineering and LLM models, operating in a specific document context)

## Project Structure

This project consists of:
- **Backend**: Spring Boot application with RAG (Retrieval-Augmented Generation) capabilities
- **Frontend**: React + TypeScript application with modern UI

## Quick Start

### Backend

1. Ensure PostgreSQL and Ollama are running
2. Configure database connection in `demo/src/main/resources/application.properties`
3. Run the Spring Boot application:
```bash
cd demo
./mvnw spring-boot:run
```

The backend will be available at `http://localhost:8080`

### Frontend

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

## Features

- 🔐 User authentication (JWT-based)
- 📁 Document upload (PDF, DOC, DOCX, TXT)
- 💬 Interactive chat interface
- 🤖 RAG-powered responses using LLM (Mistral via Ollama)
- 📚 Conversation management
- 🎨 Modern, responsive UI

## Technology Stack

### Backend
- Spring Boot
- PostgreSQL with pgvector
- Ollama (Mistral model)
- JWT authentication

### Frontend
- React 18 + TypeScript
- Vite
- Tailwind CSS
- Axios

For more details, see:
- Backend: `demo/HELP.md`
- Frontend: `frontend/README.md`
