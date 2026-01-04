package com.bavarians.service.impl;

import com.bavarians.dto.ChatMessageDTO;
import com.bavarians.dto.ChatResponseDTO;
import com.bavarians.dto.OfertaPreviewDTO;
import com.bavarians.graphql.model.ChatSession;
import com.bavarians.graphql.model.Element;
import com.bavarians.graphql.model.Oferta;
import com.bavarians.graphql.model.Pojazd;
import com.bavarians.graphql.repository.ChatSessionRepository;
import com.bavarians.graphql.repository.PojazdRepository;
import com.bavarians.service.ChatbotService;
import com.bavarians.service.OfertaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Implementation of ChatbotService
 */
@Service
public class ChatbotServiceImpl implements ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotServiceImpl.class);

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private PojazdRepository pojazdRepository;

    @Autowired
    private OfertaService ofertaService;

    @Value("${chatbot.session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;

    @Value("${chatbot.labor.rate:150}")
    private BigDecimal defaultLaborRate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ChatResponseDTO processMessage(ChatMessageDTO messageDTO, Long userId) {
        ChatResponseDTO response = new ChatResponseDTO();

        try {
            // Check if Ollama is available
            if (!ollamaService.isAvailable()) {
                response.setErrorMessage("Asystent AI jest obecnie niedostępny. Proszę użyć formularza ręcznego.");
                return response;
            }

            // Get or create session
            ChatSession session = getOrCreateSession(messageDTO.getSessionId(), userId, messageDTO.getVehicleId());
            response.setSessionId(session.getSessionId());

            // Get conversation history
            String conversationHistory = session.getConversationJson();

            // Send to Ollama
            String systemPrompt = ollamaService.getSystemPrompt();
            String ollamaResponse = ollamaService.sendPrompt(systemPrompt, messageDTO.getMessage(), conversationHistory);

            if (ollamaResponse == null || ollamaResponse.isEmpty()) {
                response.setErrorMessage("Nie udało się uzyskać odpowiedzi od asystenta. Spróbuj ponownie.");
                return response;
            }

            // Parse JSON response from Ollama
            JsonNode jsonResponse = parseOllamaResponse(ollamaResponse);

            if (jsonResponse == null) {
                response.setErrorMessage("Błąd parsowania odpowiedzi. Spróbuj ponownie.");
                return response;
            }

            // Update conversation history
            updateConversationHistory(session, messageDTO.getMessage(), ollamaResponse);

            // Extract response text
            String responseText = jsonResponse.has("response_text") ?
                    jsonResponse.get("response_text").asText() : "Rozumiem. Czy mogę jeszcze coś dla Ciebie zrobić?";
            response.setResponseText(responseText);

            // Check if ready for confirmation
            String intent = jsonResponse.has("intent") ? jsonResponse.get("intent").asText() : "ask_clarification";

            if ("confirm_ready".equals(intent)) {
                // Extract offer data and create preview
                JsonNode extractedData = jsonResponse.get("extracted_data");
                if (extractedData != null) {
                    session.setExtractedDataJson(extractedData.toString());
                    OfertaPreviewDTO preview = buildOfferPreview(extractedData, session);
                    response.setOfertaPreview(preview);
                    response.setNeedsConfirmation(true);
                }
            }

            // Save session
            session.setUpdatedAt(new Date());
            chatSessionRepository.save(session);

            return response;

        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            response.setErrorMessage("Wystąpił błąd. Spróbuj ponownie lub użyj formularza ręcznego.");
            return response;
        }
    }

    @Override
    @Transactional
    public Long confirmAndCreateOffer(String sessionId, Long userId) {
        try {
            Optional<ChatSession> sessionOpt = chatSessionRepository.findBySessionId(sessionId);
            if (!sessionOpt.isPresent()) {
                logger.error("Session not found: {}", sessionId);
                return null;
            }

            ChatSession session = sessionOpt.get();
            if (!session.getUserId().equals(userId)) {
                logger.error("User {} attempted to access session belonging to user {}", userId, session.getUserId());
                return null;
            }

            String extractedDataJson = session.getExtractedDataJson();
            if (extractedDataJson == null || extractedDataJson.isEmpty()) {
                logger.error("No extracted data in session: {}", sessionId);
                return null;
            }

            // Parse extracted data
            JsonNode extractedData = objectMapper.readTree(extractedDataJson);

            // Create Oferta
            Oferta oferta = new Oferta();
            oferta.setEdytowano(new Date());
            oferta.setStatus("NOWA");

            // Set vehicle
            if (session.getVehicleId() != null) {
                Optional<Pojazd> pojazdOpt = pojazdRepository.findById(session.getVehicleId());
                pojazdOpt.ifPresent(oferta::setPojazd);
            }

            // Create service elements
            List<Element> elements = new ArrayList<>();
            JsonNode servicesNode = extractedData.get("services");
            if (servicesNode != null && servicesNode.isArray()) {
                for (JsonNode serviceNode : servicesNode) {
                    Element element = new Element();
                    element.setNazwa(serviceNode.has("name") ? serviceNode.get("name").asText() : "Usługa");
                    element.setCzesc(serviceNode.has("parts_description") ? serviceNode.get("parts_description").asText() : "");

                    double laborHours = serviceNode.has("labor_hours") ? serviceNode.get("labor_hours").asDouble() : 1.0;
                    element.setIloscGodzinRobocizny(BigDecimal.valueOf(laborHours));
                    element.setCenaRobocizny(defaultLaborRate);

                    // Parts cost will be set by Inter Cars integration in Phase 3
                    element.setCenaCzesci(BigDecimal.ZERO);

                    elements.add(element);
                }
            }

            // Save offer
            Oferta savedOferta = ofertaService.recalculateAndSave(oferta, elements);

            // Update session
            session.setOfferId(savedOferta.getId());
            session.setStatus("COMPLETED");
            chatSessionRepository.save(session);

            logger.info("Created offer {} from chat session {}", savedOferta.getId(), sessionId);
            return savedOferta.getId();

        } catch (Exception e) {
            logger.error("Error creating offer from chat session", e);
            return null;
        }
    }

    @Override
    public boolean isChatbotAvailable() {
        return ollamaService.isAvailable();
    }

    @Override
    @Transactional
    public void cleanupExpiredSessions() {
        Date now = new Date();
        chatSessionRepository.deleteExpiredSessions(now);

        // Also delete sessions older than 30 days
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        chatSessionRepository.deleteOldSessions(cal.getTime());

        logger.info("Cleaned up expired chat sessions");
    }

    // Helper methods

    private ChatSession getOrCreateSession(String sessionId, Long userId, Long vehicleId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            Optional<ChatSession> existing = chatSessionRepository.findBySessionId(sessionId);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Create new session
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setVehicleId(vehicleId);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, sessionTimeoutMinutes);
        session.setExpiresAt(cal.getTime());

        return chatSessionRepository.save(session);
    }

    private JsonNode parseOllamaResponse(String responseText) {
        try {
            // Try to find JSON in the response
            int jsonStart = responseText.indexOf('{');
            int jsonEnd = responseText.lastIndexOf('}') + 1;

            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonPart = responseText.substring(jsonStart, jsonEnd);
                return objectMapper.readTree(jsonPart);
            }

            return null;
        } catch (Exception e) {
            logger.error("Error parsing Ollama response", e);
            return null;
        }
    }

    private void updateConversationHistory(ChatSession session, String userMessage, String assistantResponse) {
        try {
            List<Map<String, String>> history = new ArrayList<>();

            // Parse existing history
            if (session.getConversationJson() != null && !session.getConversationJson().isEmpty()) {
                JsonNode historyNode = objectMapper.readTree(session.getConversationJson());
                if (historyNode.isArray()) {
                    for (JsonNode node : historyNode) {
                        Map<String, String> msg = new HashMap<>();
                        msg.put("role", node.get("role").asText());
                        msg.put("content", node.get("content").asText());
                        history.add(msg);
                    }
                }
            }

            // Add new messages
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            history.add(userMsg);

            Map<String, String> assistantMsg = new HashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", assistantResponse);
            history.add(assistantMsg);

            // Keep only last 10 messages to avoid token limit
            if (history.size() > 10) {
                history = history.subList(history.size() - 10, history.size());
            }

            session.setConversationJson(objectMapper.writeValueAsString(history));

        } catch (Exception e) {
            logger.error("Error updating conversation history", e);
        }
    }

    private OfertaPreviewDTO buildOfferPreview(JsonNode extractedData, ChatSession session) {
        OfertaPreviewDTO preview = new OfertaPreviewDTO();

        // Vehicle info
        if (session.getVehicleId() != null) {
            Optional<Pojazd> pojazdOpt = pojazdRepository.findById(session.getVehicleId());
            if (pojazdOpt.isPresent()) {
                Pojazd pojazd = pojazdOpt.get();
                preview.setVehicleId(pojazd.getId());
                preview.setVehicleInfo(String.format("%s %s (%s) - %s",
                        pojazd.getMarka(), pojazd.getModel(), pojazd.getRokProdukcji(), pojazd.getVin()));
            }
        } else if (extractedData.has("vehicle")) {
            JsonNode vehicleNode = extractedData.get("vehicle");
            preview.setVehicleInfo(String.format("%s %s - %s",
                    vehicleNode.has("make") ? vehicleNode.get("make").asText() : "",
                    vehicleNode.has("model") ? vehicleNode.get("model").asText() : "",
                    vehicleNode.has("vin") ? vehicleNode.get("vin").asText() : ""));
        }

        // Service items
        JsonNode servicesNode = extractedData.get("services");
        if (servicesNode != null && servicesNode.isArray()) {
            for (JsonNode serviceNode : servicesNode) {
                OfertaPreviewDTO.ServiceItemDTO item = new OfertaPreviewDTO.ServiceItemDTO();
                item.setServiceName(serviceNode.has("name") ? serviceNode.get("name").asText() : "Usługa");
                item.setPartsDescription(serviceNode.has("parts_description") ? serviceNode.get("parts_description").asText() : "");

                BigDecimal laborHours = BigDecimal.valueOf(serviceNode.has("labor_hours") ? serviceNode.get("labor_hours").asDouble() : 1.0);
                item.setLaborHours(laborHours);
                item.setLaborRate(defaultLaborRate);

                BigDecimal laborCost = laborHours.multiply(defaultLaborRate);
                item.setLaborCost(laborCost);

                // Parts cost will be fetched from Inter Cars in Phase 3
                BigDecimal partsCost = BigDecimal.ZERO;
                item.setPartsCost(partsCost);

                BigDecimal itemTotal = laborCost.add(partsCost);
                item.setItemTotal(itemTotal);

                preview.getServiceItems().add(item);
                preview.setLaborTotal(preview.getLaborTotal().add(laborCost));
                preview.setPartsTotal(preview.getPartsTotal().add(partsCost));
            }
        }

        preview.setGrandTotal(preview.getLaborTotal().add(preview.getPartsTotal()));

        return preview;
    }
}
