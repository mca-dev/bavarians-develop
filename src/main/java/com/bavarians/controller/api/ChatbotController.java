package com.bavarians.controller.api;

import com.bavarians.dto.ChatMessageDTO;
import com.bavarians.dto.ChatResponseDTO;
import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.repository.KlientRepository;
import com.bavarians.service.ChatbotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for AI chatbot functionality
 */
@CrossOrigin
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotController.class);

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private KlientRepository klientRepository;

    /**
     * Check if chatbot is available
     * GET /api/chatbot/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean available = chatbotService.isChatbotAvailable();
        status.put("available", available);
        status.put("message", available ?
                "Asystent AI jest dostępny" :
                "Asystent AI jest obecnie niedostępny. Użyj formularza ręcznego.");
        return ResponseEntity.ok(status);
    }

    /**
     * Process a chat message
     * POST /api/chatbot/message
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponseDTO> processMessage(@RequestBody ChatMessageDTO messageDTO) {
        try {
            // Get current user
            Long userId = getCurrentUserId();
            if (userId == null) {
                ChatResponseDTO errorResponse = new ChatResponseDTO();
                errorResponse.setErrorMessage("Musisz być zalogowany, aby korzystać z asystenta.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Validate input
            if (messageDTO.getMessage() == null || messageDTO.getMessage().trim().isEmpty()) {
                ChatResponseDTO errorResponse = new ChatResponseDTO();
                errorResponse.setErrorMessage("Wiadomość nie może być pusta.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Limit message length
            if (messageDTO.getMessage().length() > 1000) {
                ChatResponseDTO errorResponse = new ChatResponseDTO();
                errorResponse.setErrorMessage("Wiadomość jest za długa (maksymalnie 1000 znaków).");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Process message
            ChatResponseDTO response = chatbotService.processMessage(messageDTO, userId);

            if (response.getErrorMessage() != null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            ChatResponseDTO errorResponse = new ChatResponseDTO();
            errorResponse.setErrorMessage("Wystąpił błąd podczas przetwarzania wiadomości.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Confirm and create offer from chat session
     * POST /api/chatbot/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmOffer(@RequestBody Map<String, String> request) {
        try {
            String sessionId = request.get("sessionId");

            if (sessionId == null || sessionId.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Brak identyfikatora sesji.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get current user
            Long userId = getCurrentUserId();
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Musisz być zalogowany.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Create offer
            Long offerId = chatbotService.confirmAndCreateOffer(sessionId, userId);

            Map<String, Object> response = new HashMap<>();
            if (offerId != null) {
                response.put("success", true);
                response.put("offerId", offerId);
                response.put("redirectUrl", "/oferty/" + offerId);
                response.put("message", "Oferta została utworzona pomyślnie!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Nie udało się utworzyć oferty. Spróbuj ponownie lub użyj formularza ręcznego.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            logger.error("Error confirming offer", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Wystąpił błąd podczas tworzenia oferty.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Klient user = klientRepository.findByEmail(email);
                return user != null ? user.getId() : null;
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return null;
        }
    }
}
