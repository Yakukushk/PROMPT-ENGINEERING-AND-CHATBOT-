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
  messageCount?: number;
  systemPromptId?: number;
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

export interface Message {
  id: number;
  conversationId: number;
  content: string;
  role: 'USER' | 'ASSISTANT' | 'SYSTEM';
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
  systemPromptId?: number;
}

export interface ConversationUpdateRequest {
  userId: number;
  title: string;
  initialMessageCount?: number;
  systemPromptId?: number;
}

export interface SystemPrompt {
  id: number;
  name: string;
  template: string;
  version?: number;
  active?: boolean;
  createdAt?: string;
  userId?: number;
  code?: string;
  system?: boolean;
}

export interface SystemPromptRequest {
  name: string;
  template: string;
  version: number;
  userId: number;
}
