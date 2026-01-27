import axios, { AxiosHeaders } from 'axios';
import type { User, Conversation, ChatRequest, ChatResponse, ConversationRequest } from '../types';

const API_BASE_URL = '/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    if (!config.headers) {
      config.headers = new AxiosHeaders();
    }
    if (config.headers instanceof AxiosHeaders) {
      config.headers.set('Authorization', `Bearer ${token}`);
    } else {
      const nextHeaders = new AxiosHeaders(config.headers);
      nextHeaders.set('Authorization', `Bearer ${token}`);
      config.headers = nextHeaders;
    }
  }
  return config;
});

// Handle 401 errors (unauthorized)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: async (username: string, password: string): Promise<string> => {
    const response = await api.post('/auth/signin', { username, password });
    return response.data;
  },

  register: async (user: User): Promise<string> => {
    const response = await api.post('/auth/register', user);
    return response.data;
  },
};

export const userAPI = {
  getCurrent: async (): Promise<User> => {
    const response = await api.get('/user/username');
    return response.data;
  },

  getByUsername: async (username: string): Promise<User> => {
    const response = await api.get('/user/username', { params: { username } });
    return response.data;
  },
};

export const conversationAPI = {
  getAll: async (): Promise<Conversation[]> => {
    const response = await api.get('/conversation');
    return response.data;
  },

  getByUsername: async (username: string): Promise<Conversation[]> => {
    const response = await api.get(`/conversation/${username}`);
    return response.data;
  },

  create: async (request: ConversationRequest): Promise<Conversation> => {
    const response = await api.post('/conversation', request);
    return response.data;
  },

  remove: async (conversationId: number): Promise<void> => {
    await api.delete(`/conversation/${conversationId}`);
  },
};

export const documentAPI = {
  remove: async (documentId: number): Promise<void> => {
    await api.delete(`/document/${documentId}`);
  },
};

export const chatAPI = {
  sendMessage: async (request: ChatRequest): Promise<ChatResponse> => {
    const response = await api.post('/chat', request);
    return response.data;
  },

  uploadFile: async (conversationId: number, files: File[]): Promise<void> => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append('files', file);
    });
    await api.post(`/chat/conversations/${conversationId}/documents`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
};

export default api;
