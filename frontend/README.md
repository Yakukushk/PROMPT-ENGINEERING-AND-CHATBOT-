# RAG Chatbot Frontend

Modern React frontend for the RAG (Retrieval-Augmented Generation) Chatbot application.

## Features

- 🔐 **Authentication**: Login and registration with JWT token management
- 📁 **File Upload**: Upload PDF, DOC, DOCX, or TXT files for RAG context
- 💬 **Chat Interface**: Interactive chat interface with message history
- 🎨 **Modern UI**: Beautiful, responsive design with Tailwind CSS
- 🔄 **Real-time Chat**: Send messages and receive AI-generated responses
- 📚 **Conversation Management**: Create and manage multiple conversations

## Tech Stack

- **React 18** with TypeScript
- **Vite** for fast development and building
- **React Router** for navigation
- **Axios** for API communication
- **Tailwind CSS** for styling
- **Lucide React** for icons

## Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn
- Backend server running on `http://localhost:8080`

### Installation

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

The application will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

## Project Structure

```
frontend/
├── src/
│   ├── components/       # React components
│   │   ├── Login.tsx
│   │   ├── Register.tsx
│   │   ├── ChatInterface.tsx
│   │   ├── FileUpload.tsx
│   │   └── ProtectedRoute.tsx
│   ├── pages/           # Page components
│   │   └── ChatPage.tsx
│   ├── services/        # API services
│   │   ├── api.ts
│   │   └── auth.ts
│   ├── types/           # TypeScript types
│   │   └── index.ts
│   ├── App.tsx          # Main app component
│   ├── main.tsx         # Entry point
│   └── index.css        # Global styles
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.js
```

## API Integration

The frontend communicates with the backend API at `/api/v1`. The following endpoints are used:

### Authentication
- `POST /api/v1/auth/signin` - Login
- `POST /api/v1/auth/register` - Register

### Conversations
- `GET /api/v1/conversation` - Get all conversations
- `GET /api/v1/conversation/{username}` - Get conversations by username
- `POST /api/v1/conversation` - Create new conversation

### Chat
- `POST /api/v1/chat` - Send message
- `POST /api/v1/chat/conversations/{conversationId}/documents` - Upload files

## Configuration

The API base URL is configured in `vite.config.ts`. The development server proxies API requests to `http://localhost:8080`.

To change the backend URL, update the proxy configuration in `vite.config.ts`:

```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
},
```

## Authentication

The application uses JWT tokens for authentication. Tokens are stored in localStorage and automatically included in API requests via axios interceptors.

## Usage

1. **Register/Login**: Create an account or sign in with existing credentials
2. **Create Conversation**: Click "New Conversation" to start a new chat
3. **Upload Files**: Upload PDF or document files to provide context for the RAG system
4. **Chat**: Type messages and receive AI-generated responses based on the uploaded documents

## Notes

- The `userId` in conversation creation is currently hardcoded. You may need to add a backend endpoint like `GET /api/v1/user/me` to get the current authenticated user's information.
- File uploads support PDF, DOC, DOCX, and TXT formats
- Maximum file size is 5MB (as configured in the backend)

## Development

### Linting

```bash
npm run lint
```

### Type Checking

TypeScript type checking is performed during build. For development, your IDE should show type errors.

## License

This project is part of a university course on Prompt Engineering and Chatbots.
