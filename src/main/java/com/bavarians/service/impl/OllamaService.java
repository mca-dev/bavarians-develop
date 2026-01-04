package com.bavarians.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for communicating with Ollama API
 */
@Service
public class OllamaService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    @Value("${ollama.base.url:http://ollama-service:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:PRIHLOP/PLLuM:Q4_K_M}")
    private String ollamaModel;

    private final RestTemplate restTemplate;

    public OllamaService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send a prompt to Ollama and get a response (backward compatible version)
     * @param systemPrompt The system prompt (instructions for the model)
     * @param userMessage The user's message
     * @param conversationHistory Optional conversation history JSON
     * @return The model's response text
     */
    public String sendPrompt(String systemPrompt, String userMessage, String conversationHistory) {
        return sendPrompt(systemPrompt, userMessage, conversationHistory, null);
    }

    /**
     * Send a prompt to Ollama and get a response with database context (RAG)
     * @param systemPrompt The system prompt (instructions for the model)
     * @param userMessage The user's message
     * @param conversationHistory Optional conversation history JSON
     * @param databaseContext Optional context from database (RAG)
     * @return The model's response text
     */
    public String sendPrompt(String systemPrompt, String userMessage, String conversationHistory, String databaseContext) {
        try {
            String url = ollamaBaseUrl + "/api/generate";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", ollamaModel);
            requestBody.put("stream", false);

            // Build the prompt
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("System: ").append(systemPrompt).append("\n\n");

            // Add database context for RAG (Retrieval-Augmented Generation)
            if (databaseContext != null && !databaseContext.isEmpty()) {
                promptBuilder.append("KONTEKST Z BAZY DANYCH:\n");
                promptBuilder.append(databaseContext).append("\n\n");
                promptBuilder.append("Wykorzystaj powyższe informacje z bazy danych, aby lepiej zrozumieć kontekst i udzielić precyzyjnej odpowiedzi.\n\n");
            }

            if (conversationHistory != null && !conversationHistory.isEmpty()) {
                promptBuilder.append("Historia konwersacji:\n").append(conversationHistory).append("\n\n");
            }

            promptBuilder.append("Użytkownik: ").append(userMessage).append("\n\n");
            promptBuilder.append("Asystent (odpowiedz w formacie JSON):");

            requestBody.put("prompt", promptBuilder.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            logger.info("Sending request to Ollama: {}", url);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String responseText = (String) response.getBody().get("response");
                logger.info("Received response from Ollama: {} characters", responseText != null ? responseText.length() : 0);
                return responseText;
            } else {
                logger.error("Unexpected response from Ollama: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            logger.error("Error communicating with Ollama", e);
            return null;
        }
    }

    /**
     * Check if Ollama service is available
     */
    public boolean isAvailable() {
        try {
            String url = ollamaBaseUrl + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the system prompt for the chatbot
     */
    public String getSystemPrompt() {
        return "Jesteś asystentem warsztatowym dla serwisu samochodowego. " +
                "Pomagasz użytkownikom w tworzeniu ofert serwisowych na podstawie naturalnego języka. " +
                "\n\nMasz dostęp do KONTEKSTU Z BAZY DANYCH zawierającego:\n" +
                "- Informacje o pojazdach zarejestrowanych w systemie (marka, model, VIN, przebieg)\n" +
                "- Historię serwisową pojazdów (wcześniejsze naprawy i usługi)\n" +
                "- Popularne usługi i ich typowe ceny\n" +
                "\nTwoim zadaniem jest:\n" +
                "1. WYKORZYSTAĆ informacje z kontekstu bazy danych, jeśli są dostępne\n" +
                "2. Zrozumieć, jaką usługę chce wykonać użytkownik (np. wymiana oleju, naprawa hamulców)\n" +
                "3. Zebrać informacje o pojeździe (marka, model, VIN jeśli dostępny)\n" +
                "4. Określić potrzebne części i robociznę (bazując na historii i popularnych usługach)\n" +
                "5. Zadawać pytania doprecyzowujące jeśli czegoś brakuje\n" +
                "\nODPOWIADAJ ZAWSZE W FORMACIE JSON:\n" +
                "{\n" +
                "  \"intent\": \"create_offer|ask_clarification|confirm_ready\",\n" +
                "  \"response_text\": \"Twoja odpowiedź po polsku dla użytkownika\",\n" +
                "  \"extracted_data\": {\n" +
                "    \"vehicle\": {\"make\": \"...\", \"model\": \"...\", \"vin\": \"...\"},\n" +
                "    \"services\": [\n" +
                "      {\n" +
                "        \"name\": \"Nazwa usługi\",\n" +
                "        \"parts_description\": \"Opis części\",\n" +
                "        \"labor_hours\": 1.5\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"needs_clarification\": [\"lista pytań do użytkownika\"]\n" +
                "}\n" +
                "\nPamiętaj:\n" +
                "- Używaj polskiego języka\n" +
                "- WYKORZYSTUJ informacje z kontekstu bazy danych\n" +
                "- Jeśli znaleziono pojazd w bazie, wykorzystaj jego historię serwisową\n" +
                "- Bazuj na typowych cenach z wcześniejszych usług\n" +
                "- Bądź konkretny i precyzyjny\n" +
                "- Zawsze zwracaj poprawny JSON\n" +
                "- Jeśli nie masz wszystkich informacji, zapytaj użytkownika";
    }
}
