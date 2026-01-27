export interface User {
  id?: number;
  userId?: number;
  username: string;
  password?: string;
  fullName?: string;
}

export interface Conversation {
  id?: number; // legacy field
  conversationId?: number;
  userId: number;
  title: string;
  createdAt?: string;
  documents?: DocumentFile[];
}

export interface DocumentFile {
  id: number;
  fileName: string;
  contentType: string;
  conversationId: number;
  size: number;
  indexed?: boolean;
  status?: string;
  createdAt?: string;
}

export interface ChatRequest {
  conversationId: number;
  message: string;
}

export interface ChatResponse {
  answer: string;
}

export interface ConversationRequest {
  userId: number;
  title: string;
  initialMessageCount?: number;
}
