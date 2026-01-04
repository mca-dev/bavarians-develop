package com.bavarians.dto;

import java.io.Serializable;

/**
 * DTO for chat responses sent from backend to frontend
 */
public class ChatResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String responseText;
    private boolean needsConfirmation;
    private OfertaPreviewDTO ofertaPreview;  // Populated when ready for confirmation
    private String errorMessage;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String sessionId, String responseText) {
        this.sessionId = sessionId;
        this.responseText = responseText;
        this.needsConfirmation = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public boolean isNeedsConfirmation() {
        return needsConfirmation;
    }

    public void setNeedsConfirmation(boolean needsConfirmation) {
        this.needsConfirmation = needsConfirmation;
    }

    public OfertaPreviewDTO getOfertaPreview() {
        return ofertaPreview;
    }

    public void setOfertaPreview(OfertaPreviewDTO ofertaPreview) {
        this.ofertaPreview = ofertaPreview;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
