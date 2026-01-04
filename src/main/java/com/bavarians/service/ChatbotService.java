package com.bavarians.service;

import com.bavarians.dto.ChatMessageDTO;
import com.bavarians.dto.ChatResponseDTO;

/**
 * Service for managing chatbot conversations
 */
public interface ChatbotService {

    /**
     * Process a user message and generate a response
     * @param messageDTO The user's message
     * @param userId The current user's ID
     * @return The chatbot's response
     */
    ChatResponseDTO processMessage(ChatMessageDTO messageDTO, Long userId);

    /**
     * Confirm and create an offer from a chat session
     * @param sessionId The session ID
     * @param userId The current user's ID
     * @return The ID of the created offer, or null if failed
     */
    Long confirmAndCreateOffer(String sessionId, Long userId);

    /**
     * Check if chatbot is available (Ollama service running)
     * @return true if available
     */
    boolean isChatbotAvailable();

    /**
     * Clean up expired chat sessions
     */
    void cleanupExpiredSessions();
}
