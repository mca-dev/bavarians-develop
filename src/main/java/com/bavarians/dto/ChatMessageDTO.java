package com.bavarians.dto;

import java.io.Serializable;

/**
 * DTO for chat messages sent from frontend to backend
 */
public class ChatMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private String sessionId;
    private Long vehicleId;  // Optional: if user is viewing a specific vehicle

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
