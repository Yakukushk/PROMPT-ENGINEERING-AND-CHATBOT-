import { useState, useRef, useEffect } from 'react';
import { Send, Bot, User as UserIcon, Loader2, FileText, Trash2, Plus } from 'lucide-react';
import { chatAPI, messageAPI } from '../services/api';
import type {
  ChatRequest,
  ChatResponse,
  DocumentFile,
  Message as ApiMessage,
  SystemPrompt,
} from '../types';
import { FileUpload } from './FileUpload';

interface UiMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

interface ChatInterfaceProps {
  conversationId: number;
  canChat: boolean;
  onUploadSuccess: (conversationId: number) => void | Promise<void>;
  onDeleteDocument: (documentId: number) => void | Promise<void>;
  documents?: DocumentFile[];
  systemPrompts: SystemPrompt[];
  selectedPromptId: number | null;
  promptsLoading: boolean;
  promptError: string | null;
  onSelectPrompt: (promptId: number | null) => void | Promise<void>;
  onCreatePrompt: (data: { name: string; template: string }) => Promise<SystemPrompt | null>;
  onDeletePrompt: (promptId: number) => void | Promise<void>;
}

export const ChatInterface = ({
  conversationId,
  canChat,
  onUploadSuccess,
  onDeleteDocument,
  documents = [],
  systemPrompts,
  selectedPromptId,
  promptsLoading,
  promptError,
  onSelectPrompt,
  onCreatePrompt,
  onDeletePrompt,
}: ChatInterfaceProps) => {
  const [messages, setMessages] = useState<UiMessage[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [promptName, setPromptName] = useState('');
  const [promptTemplate, setPromptTemplate] = useState('');
  const [creatingPrompt, setCreatingPrompt] = useState(false);
  const [promptActionError, setPromptActionError] = useState<string | null>(null);
  const [isPromptModalOpen, setIsPromptModalOpen] = useState(false);
  const [settingsExpanded, setSettingsExpanded] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  const formatBytes = (bytes: number) => {
    if (!Number.isFinite(bytes)) return '';
    const units = ['B', 'KB', 'MB', 'GB'];
    let size = bytes;
    let unitIndex = 0;
    while (size >= 1024 && unitIndex < units.length - 1) {
      size /= 1024;
      unitIndex += 1;
    }
    return `${size.toFixed(size >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return '';
    return date.toLocaleString();
  };

  const getStatusLabel = (doc: DocumentFile) => {
    return doc.status ?? (doc.indexed ? 'INDEXED' : 'UPLOADED');
  };

  const getStatusStyles = (status?: string) => {
    switch (status) {
      case 'INDEXED':
        return 'bg-green-50 text-green-700 border-green-200';
      case 'FAILED':
        return 'bg-red-50 text-red-700 border-red-200';
      case 'UPLOADED':
      default:
        return 'bg-yellow-50 text-yellow-700 border-yellow-200';
    }
  };

  const sortedDocuments = [...documents].sort((a, b) => {
    const aTime = a.createdAt ? new Date(a.createdAt).getTime() : 0;
    const bTime = b.createdAt ? new Date(b.createdAt).getTime() : 0;
    return bTime - aTime;
  });

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const mapApiMessage = (message: ApiMessage): UiMessage => ({
    id: String(message.id),
    role: message.role === 'ASSISTANT' ? 'assistant' : 'user',
    content: message.content,
    timestamp: message.createdAt ? new Date(message.createdAt) : new Date(),
  });

  useEffect(() => {
    const loadHistory = async () => {
      setLoadingHistory(true);
      try {
        const history = await messageAPI.getByConversationId(conversationId);
        setMessages(
          history.filter((message) => message.role !== 'SYSTEM').map(mapApiMessage)
        );
      } catch (error) {
        console.error('Failed to load messages:', error);
        setMessages([]);
      } finally {
        setLoadingHistory(false);
      }
    };
    setInput('');
    setLoading(false);
    setUploading(false);
    loadHistory();
  }, [conversationId]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || loading || !canChat) return;

    const userMessage: UiMessage = {
      id: Date.now().toString(),
      role: 'user',
      content: input.trim(),
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const request: ChatRequest = {
        conversationId,
        message: userMessage.content,
      };

      const response: ChatResponse = await chatAPI.sendMessage(request);

      const assistantMessage: UiMessage = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.answer,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, assistantMessage]);
    } catch (error: unknown) {
      const errorMessage: UiMessage = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content:
          error instanceof Error
            ? `Error: ${error.message}`
            : 'Failed to send message. Please try again.',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (files: File[]) => {
    setUploading(true);
    try {
      await chatAPI.uploadFile(conversationId, files);
      await onUploadSuccess(conversationId);
      const uploadMessage: UiMessage = {
        id: Date.now().toString(),
        role: 'assistant',
        content: `Successfully uploaded ${files.length} file(s). You can now ask questions about the uploaded documents.`,
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, uploadMessage]);
    } catch (error: unknown) {
      const errorMessage: UiMessage = {
        id: Date.now().toString(),
        role: 'assistant',
        content:
          error instanceof Error
            ? `Upload failed: ${error.message}`
            : 'Upload failed: Please try again.',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteDocument = async (documentId: number) => {
    try {
      await onDeleteDocument(documentId);
    } catch (error) {
      console.error('Failed to delete document:', error);
    }
  };

  const handleCreatePrompt = async () => {
    if (!promptName.trim() || !promptTemplate.trim()) {
      setPromptActionError('Enter a name and template for the prompt.');
      return;
    }
    setCreatingPrompt(true);
    setPromptActionError(null);
    try {
      await onCreatePrompt({
        name: promptName.trim(),
        template: promptTemplate.trim(),
      });
      setPromptName('');
      setPromptTemplate('');
      setIsPromptModalOpen(false);
    } catch (error) {
      console.error('Failed to create system prompt:', error);
      setPromptActionError('Failed to create prompt. Please try again.');
    } finally {
      setCreatingPrompt(false);
    }
  };

  const handleDeletePrompt = async () => {
    if (selectedPromptId == null) return;
    try {
      await onDeletePrompt(selectedPromptId);
    } catch (error) {
      console.error('Failed to delete prompt:', error);
      setPromptActionError('Failed to delete prompt. Please try again.');
    }
  };

  const openPromptModal = () => {
    setPromptActionError(null);
    setIsPromptModalOpen(true);
  };

  const closePromptModal = () => {
    if (creatingPrompt) return;
    setIsPromptModalOpen(false);
    setPromptActionError(null);
  };

  const selectedPrompt = systemPrompts.find((prompt) => prompt.id === selectedPromptId) ?? null;
  const canDeletePrompt =
    selectedPrompt != null &&
    !selectedPrompt.system &&
    selectedPrompt.code !== 'UNIVERSITY_RAG';

  const handleKeyPress = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend(e);
    }
  };

  return (
    <div className="flex flex-col h-full bg-gray-50">
      {/* System Prompt + File Upload Section */}
      <div className="bg-white border-b border-gray-200">
        <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
          <div>
            <p className="text-sm font-semibold text-gray-700">Chat settings</p>
            <p className="text-xs text-gray-500">
              System prompts, uploads, and conversation context.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setSettingsExpanded((prev) => !prev)}
            className="text-xs text-primary-600 hover:text-primary-700"
          >
            {settingsExpanded ? 'Hide settings' : 'Show settings'}
          </button>
        </div>

        {settingsExpanded && (
          <div className="p-4 space-y-4">
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-semibold text-gray-700">System prompt</p>
              <p className="text-xs text-gray-500">
                Select the active prompt for this conversation.
              </p>
            </div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={openPromptModal}
                className="inline-flex items-center gap-1 text-xs text-primary-600 hover:text-primary-700"
                title="Create new prompt"
              >
                <Plus className="w-3.5 h-3.5" />
                Add
              </button>
              <button
                type="button"
                onClick={handleDeletePrompt}
                disabled={!canDeletePrompt}
                className="inline-flex items-center gap-1 text-xs text-red-600 disabled:text-gray-300"
                title={
                  canDeletePrompt ? 'Delete selected prompt' : 'System prompts cannot be deleted'
                }
              >
                <Trash2 className="w-3.5 h-3.5" />
                Delete
              </button>
            </div>
          </div>
          <div className="flex flex-col md:flex-row gap-3">
            <select
              value={selectedPromptId ?? ''}
              onChange={(e) => {
                const value = e.target.value;
                onSelectPrompt(value ? Number(value) : null);
              }}
              disabled={promptsLoading || systemPrompts.length === 0}
              className="flex-1 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              {promptsLoading && <option>Loading prompts...</option>}
              {!promptsLoading && systemPrompts.length === 0 && (
                <option value="">No prompts available</option>
              )}
              {!promptsLoading &&
                systemPrompts.map((prompt) => (
                  <option key={prompt.id} value={prompt.id}>
                    {prompt.name}
                    {prompt.system ? ' (system)' : ''}
                  </option>
                ))}
            </select>
            <div className="flex items-center gap-2 text-xs text-gray-500">
              {selectedPrompt?.version != null && <span>v{selectedPrompt.version}</span>}
              {selectedPrompt?.active === false && (
                <span className="text-red-500">inactive</span>
              )}
            </div>
          </div>
          {(promptError || promptActionError) && (
            <div className="text-xs text-red-600">
              {promptError ?? promptActionError}
            </div>
          )}
          {selectedPrompt?.template && (
            <div className="text-xs text-gray-500 bg-gray-50 border border-gray-200 rounded-lg p-2 max-h-24 overflow-y-auto">
              {selectedPrompt.template}
            </div>
          )}
        </div>

            <FileUpload onUpload={handleFileUpload} disabled={uploading || loading} />
            {!canChat && (
              <div className="mt-3 text-sm text-gray-600 bg-primary-50 border border-primary-100 rounded-lg px-3 py-2">
                Upload a document to enable chatting with the assistant.
              </div>
            )}
            {sortedDocuments.length > 0 && (
              <div className="bg-gray-50 border border-gray-200 rounded-lg px-3 py-2">
                <p className="text-xs font-semibold text-gray-600 mb-2">Uploaded documents</p>
                <div className="space-y-2">
                  {sortedDocuments.map((doc) => {
                    const statusLabel = getStatusLabel(doc);
                    return (
                      <div key={doc.id} className="flex items-center gap-3 text-sm text-gray-700">
                        <FileText className="w-4 h-4 text-primary-600 flex-shrink-0" />
                        <div className="min-w-0 flex-1">
                          <div className="flex items-center gap-2">
                            <span className="truncate">{doc.fileName}</span>
                            {statusLabel && (
                              <span
                                className={`text-[10px] uppercase tracking-wide px-2 py-0.5 rounded-full border ${getStatusStyles(
                                  statusLabel
                                )}`}
                              >
                                {statusLabel}
                              </span>
                            )}
                          </div>
                          <div className="text-xs text-gray-500 mt-0.5">
                            {doc.size != null && <span>{formatBytes(doc.size)}</span>}
                            {doc.size != null && doc.createdAt && <span className="mx-2">•</span>}
                            {doc.createdAt && <span>{formatDate(doc.createdAt)}</span>}
                          </div>
                        </div>
                        <button
                          onClick={() => handleDeleteDocument(doc.id)}
                          className="p-1 text-gray-400 hover:text-red-600 transition"
                          aria-label="Delete document"
                          title="Delete document"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    );
                  })}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {isPromptModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
          <div className="w-full max-w-lg rounded-xl bg-white shadow-lg">
            <div className="border-b border-gray-200 px-4 py-3">
              <h3 className="text-sm font-semibold text-gray-800">Create new prompt</h3>
              <p className="text-xs text-gray-500">
                Add a custom system prompt for this conversation.
              </p>
            </div>
            <div className="space-y-3 px-4 py-4">
              <input
                type="text"
                value={promptName}
                onChange={(e) => setPromptName(e.target.value)}
                placeholder="Prompt name"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              />
              <textarea
                value={promptTemplate}
                onChange={(e) => setPromptTemplate(e.target.value)}
                placeholder="Prompt template (use {{context}})"
                rows={5}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent resize-none"
              />
              {promptActionError && (
                <div className="text-xs text-red-600">{promptActionError}</div>
              )}
            </div>
            <div className="flex items-center justify-end gap-2 border-t border-gray-200 px-4 py-3">
              <button
                type="button"
                onClick={closePromptModal}
                disabled={creatingPrompt}
                className="px-3 py-1.5 text-sm text-gray-600 hover:text-gray-800"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleCreatePrompt}
                disabled={creatingPrompt}
                className="inline-flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-sm text-white hover:bg-primary-700 disabled:opacity-60"
              >
                {creatingPrompt ? 'Creating...' : 'Create'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Messages Area */}
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {loadingHistory && (
          <div className="flex justify-center">
            <div className="flex items-center gap-2 text-sm text-gray-500">
              <Loader2 className="w-4 h-4 animate-spin" />
              Loading history...
            </div>
          </div>
        )}
        {messages.length === 0 && (
          <div className="text-center text-gray-500 mt-12">
            <Bot className="w-16 h-16 mx-auto mb-4 text-gray-300" />
            <p className="text-lg font-medium">Start a conversation</p>
            <p className="text-sm">Upload a document and ask questions about it</p>
          </div>
        )}

        {messages.map((message) => (
          <div
            key={message.id}
            className={`flex gap-4 ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
          >
            {message.role === 'assistant' && (
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
                <Bot className="w-6 h-6 text-primary-600" />
              </div>
            )}

            <div
              className={`max-w-3xl rounded-2xl px-4 py-3 ${
                message.role === 'user'
                  ? 'bg-primary-600 text-white'
                  : 'bg-white text-gray-800 shadow-sm border border-gray-200'
              }`}
            >
              <p className="whitespace-pre-wrap break-words">{message.content}</p>
              <p
                className={`text-xs mt-2 ${
                  message.role === 'user' ? 'text-primary-100' : 'text-gray-500'
                }`}
              >
                {message.timestamp.toLocaleTimeString()}
              </p>
            </div>

            {message.role === 'user' && (
              <div className="flex-shrink-0 w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center">
                <UserIcon className="w-6 h-6 text-gray-600" />
              </div>
            )}
          </div>
        ))}

        {loading && (
          <div className="flex gap-4 justify-start">
            <div className="flex-shrink-0 w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center">
              <Bot className="w-6 h-6 text-primary-600" />
            </div>
            <div className="bg-white rounded-2xl px-4 py-3 shadow-sm border border-gray-200">
              <Loader2 className="w-5 h-5 text-primary-600 animate-spin" />
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="p-4 bg-white border-t border-gray-200">
        <form onSubmit={handleSend} className="flex gap-2">
          <textarea
            ref={inputRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={
              canChat
                ? 'Type your message... (Press Enter to send, Shift+Enter for new line)'
                : 'Upload a document to start asking questions...'
            }
            className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none resize-none"
            rows={1}
            disabled={loading || !canChat}
            onInput={(e) => {
              const target = e.target as HTMLTextAreaElement;
              target.style.height = 'auto';
              target.style.height = `${Math.min(target.scrollHeight, 120)}px`;
            }}
          />
          <button
            type="submit"
            disabled={!input.trim() || loading || !canChat}
            className="px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition flex items-center gap-2"
          >
            {loading ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              <Send className="w-5 h-5" />
            )}
          </button>
        </form>
      </div>
    </div>
  );
};
