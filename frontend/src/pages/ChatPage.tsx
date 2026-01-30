import { useState, useEffect, useCallback, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { LogOut, Plus, MessageCircle, Trash2 } from 'lucide-react';
import { conversationAPI, documentAPI, systemPromptAPI, userAPI } from '../services/api';
import { authService } from '../services/auth';
import { ChatInterface } from '../components/ChatInterface';
import type { Conversation, SystemPrompt } from '../types';

export const ChatPage = () => {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [currentConversationId, setCurrentConversationId] = useState<number | null>(null);
  const [uploadStatus, setUploadStatus] = useState<Record<number, boolean>>({});
  const [systemPrompts, setSystemPrompts] = useState<SystemPrompt[]>([]);
  const [promptsLoading, setPromptsLoading] = useState(false);
  const [promptError, setPromptError] = useState<string | null>(null);
  const [selectedPromptId, setSelectedPromptId] = useState<number | null>(null);
  const user = authService.getUser();
  const [currentUserId, setCurrentUserId] = useState<number | null>(
    user?.userId ?? user?.id ?? null
  );
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const getConversationId = (conversation: Conversation) =>
    conversation.conversationId ?? conversation.id ?? null;
  const currentConversation = useMemo(() => {
    return conversations.find(
      (conversation) => getConversationId(conversation) === currentConversationId
    );
  }, [conversations, currentConversationId]);

  const defaultPromptId = useMemo(() => {
    const byCode = systemPrompts.find((prompt) => prompt.code === 'UNIVERSITY_RAG')?.id;
    if (byCode != null) return byCode;
    const systemPrompt = systemPrompts.find((prompt) => prompt.system)?.id;
    if (systemPrompt != null) return systemPrompt;
    return systemPrompts[0]?.id ?? null;
  }, [systemPrompts]);

  const loadConversations = useCallback(async () => {
    try {
      const data = user?.username
        ? await conversationAPI.getByUsername(user.username)
        : await conversationAPI.getAll();
      setConversations(data);
      setUploadStatus((prev) => {
        const nextStatus: Record<number, boolean> = { ...prev };
        data.forEach((conversation) => {
          const conversationId = getConversationId(conversation);
          if (conversationId != null) {
            nextStatus[conversationId] = prev[conversationId] ?? true;
          }
        });
        return nextStatus;
      });
      if (data.length > 0 && currentConversationId === null) {
        const firstWithId = data.find((conversation) => getConversationId(conversation) != null);
        setCurrentConversationId(getConversationId(firstWithId ?? ({} as Conversation)));
      }
    } catch (error) {
      console.error('Failed to load conversations:', error);
    } finally {
      setLoading(false);
    }
  }, [currentConversationId, user?.username]);

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  const loadSystemPrompts = useCallback(async () => {
    setPromptsLoading(true);
    setPromptError(null);
    try {
      const prompts = await systemPromptAPI.getAll();
      setSystemPrompts(prompts);
    } catch (error) {
      console.error('Failed to load system prompts:', error);
      setPromptError('Failed to load system prompts');
    } finally {
      setPromptsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSystemPrompts();
  }, [loadSystemPrompts]);

  const ensureUserId = useCallback(async () => {
    if (currentUserId != null) return currentUserId;
    try {
      const profile = await userAPI.getCurrent();
      const id = profile.userId ?? profile.id ?? null;
      if (id != null) {
        setCurrentUserId(id);
      }
      authService.setUser({
        ...profile,
        username: profile.username,
        userId: profile.userId ?? profile.id,
      });
      return id;
    } catch (error) {
      console.error('Failed to load user profile:', error);
      return null;
    }
  }, [currentUserId]);

  useEffect(() => {
    if (!currentConversation) {
      setSelectedPromptId(defaultPromptId);
      return;
    }
    if (currentConversation.systemPromptId != null) {
      setSelectedPromptId(currentConversation.systemPromptId);
      return;
    }
    setSelectedPromptId(defaultPromptId);
  }, [currentConversation, defaultPromptId]);

  const createNewConversation = useCallback(async () => {
    try {
      const userId = await ensureUserId();
      if (userId == null) {
        console.error('User ID is not available');
        return;
      }
      const newConversation = await conversationAPI.create({
        userId,
        title: `Conversation ${new Date().toLocaleString()}`,
        initialMessageCount: 0,
        systemPromptId: selectedPromptId ?? defaultPromptId ?? undefined,
      });
      setConversations((prev) => [newConversation, ...prev]);
      const newConversationId = getConversationId(newConversation);
      if (newConversationId != null) {
        setCurrentConversationId(newConversationId);
        setUploadStatus((prev) => ({
          ...prev,
          [newConversationId]: false,
        }));
      } else {
        setCurrentConversationId(null);
      }
    } catch (error) {
      console.error('Failed to create conversation:', error);
    }
  }, [defaultPromptId, ensureUserId, selectedPromptId]);

  const updateConversationPrompt = async (promptId: number | null) => {
    if (!currentConversation || currentConversationId == null || promptId == null) {
      setSelectedPromptId(promptId);
      return;
    }
    const previousPromptId = selectedPromptId;
    setSelectedPromptId(promptId);
    try {
      const userId = currentConversation.userId ?? (await ensureUserId());
      if (userId == null) {
        throw new Error('User ID is not available');
      }
      const updated = await conversationAPI.update(currentConversationId, {
        userId,
        title: currentConversation.title,
        initialMessageCount: currentConversation.messageCount,
        systemPromptId: promptId,
      });
      setConversations((prev) =>
        prev.map((conversation) =>
          getConversationId(conversation) === currentConversationId
            ? { ...conversation, ...updated }
            : conversation
        )
      );
    } catch (error) {
      console.error('Failed to update conversation prompt:', error);
      setSelectedPromptId(previousPromptId ?? null);
    }
  };

  const handleCreatePrompt = async (data: {
    name: string;
    template: string;
  }): Promise<SystemPrompt | null> => {
    const userId = await ensureUserId();
    if (userId == null) {
      console.error('User ID is not available');
      return null;
    }
    const created = await systemPromptAPI.create({
      name: data.name,
      template: data.template,
      version: 1,
      userId,
    });
    setSystemPrompts((prev) => [created, ...prev]);
    setSelectedPromptId(created.id);
    await updateConversationPrompt(created.id);
    return created;
  };

  const handleDeletePrompt = async (promptId: number) => {
    if (selectedPromptId === promptId && defaultPromptId != null) {
      await updateConversationPrompt(defaultPromptId);
    }
    await systemPromptAPI.remove(promptId);
    setSystemPrompts((prev) => prev.filter((prompt) => prompt.id !== promptId));
  };

  const deleteConversation = async (conversationId: number | null) => {
    if (conversationId == null) return;
    try {
      await conversationAPI.remove(conversationId);
      setConversations((prev) =>
        prev.filter((conversation) => getConversationId(conversation) !== conversationId)
      );
      setUploadStatus((prev) => {
        const next = { ...prev };
        delete next[conversationId];
        return next;
      });
      if (currentConversationId === conversationId) {
        const remaining = conversations.filter(
          (conversation) => getConversationId(conversation) !== conversationId
        );
        const nextConversationId = getConversationId(remaining[0] ?? ({} as Conversation));
        setCurrentConversationId(nextConversationId);
      }
    } catch (error) {
      console.error('Failed to delete conversation:', error);
    }
  };

  const deleteDocument = async (documentId: number) => {
    try {
      await documentAPI.remove(documentId);
      setConversations((prev) =>
        prev.map((conversation) => {
          if (!conversation.documents) return conversation;
          const nextDocs = conversation.documents.filter((doc) => doc.id !== documentId);
          if (nextDocs.length === conversation.documents.length) {
            return conversation;
          }
          return { ...conversation, documents: nextDocs };
        })
      );
      await loadConversations();
    } catch (error) {
      console.error('Failed to delete document:', error);
    }
  };

  const handleUploadSuccess = async (conversationId: number) => {
    setUploadStatus((prev) => ({
      ...prev,
      [conversationId]: true,
    }));
    await loadConversations();
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col bg-gray-100">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-primary-600 rounded-lg flex items-center justify-center">
            <MessageCircle className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">RAG Chatbot</h1>
            <p className="text-sm text-gray-500">Welcome, {user?.username || 'User'}</p>
          </div>
        </div>
        <button
          onClick={handleLogout}
          className="px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg transition flex items-center gap-2"
        >
          <LogOut className="w-4 h-4" />
          Logout
        </button>
      </header>

      <div className="flex-1 flex overflow-hidden">
        {/* Sidebar */}
        <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
          <div className="p-4 border-b border-gray-200">
            <button
              onClick={createNewConversation}
              className="w-full px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition flex items-center justify-center gap-2 font-medium"
            >
              <Plus className="w-4 h-4" />
              New Conversation
            </button>
          </div>

          <div className="flex-1 overflow-y-auto p-2">
            {conversations.map((conversation, index) => {
              const conversationId = getConversationId(conversation);
              return (
                <div
                  key={`${conversationId ?? 'no-id'}-${index}`}
                  className={`w-full rounded-lg mb-2 transition ${
                    currentConversationId === conversationId
                      ? 'bg-primary-50 text-primary-700 border border-primary-200'
                      : 'hover:bg-gray-50 text-gray-700'
                  }`}
                >
                  <div className="flex items-center gap-2 px-4 py-3 min-w-0">
                    <button
                      onClick={() => setCurrentConversationId(conversationId ?? null)}
                      className="flex-1 min-w-0 text-left"
                    >
                      <p className="font-medium truncate">{conversation.title}</p>
                      {conversation.createdAt && (
                        <p className="text-xs text-gray-500 mt-1">
                          {new Date(conversation.createdAt).toLocaleDateString()}
                        </p>
                      )}
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        deleteConversation(conversationId);
                      }}
                      className="p-2 text-gray-500 hover:text-red-600 transition shrink-0"
                      aria-label="Delete conversation"
                      title="Delete conversation"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </aside>

        {/* Main Chat Area */}
        <main className="flex-1 flex flex-col">
          {currentConversationId !== null ? (
            <ChatInterface
              conversationId={currentConversationId}
              canChat={uploadStatus[currentConversationId] ?? true}
              onUploadSuccess={handleUploadSuccess}
              onDeleteDocument={deleteDocument}
              systemPrompts={systemPrompts}
              selectedPromptId={selectedPromptId}
              promptsLoading={promptsLoading}
              promptError={promptError}
              onSelectPrompt={updateConversationPrompt}
              onCreatePrompt={handleCreatePrompt}
              onDeletePrompt={handleDeletePrompt}
              documents={
                conversations.find(
                  (conversation) => getConversationId(conversation) === currentConversationId
                )?.documents ?? []
              }
            />
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <div className="text-center text-gray-500">
                <MessageCircle className="w-16 h-16 mx-auto mb-4 text-gray-300" />
                <p>Select a conversation or create a new one</p>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};
