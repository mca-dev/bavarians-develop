package com.bavarians.graphql.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity for storing chatbot conversation sessions
 */
@Entity(name = "chat_session")
@Table(name = "chat_session")
public class ChatSession {

    @Id
    @SequenceGenerator(name="seq", sequenceName="bav_seq")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 36)
    private String sessionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "conversation_json", columnDefinition = "TEXT")
    private String conversationJson;  // Stores full conversation history as JSON

    @Column(name = "extracted_data_json", columnDefinition = "TEXT")
    private String extractedDataJson;  // Stores extracted offer data as JSON

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Column(name = "status", length = 50)
    private String status;  // ACTIVE, COMPLETED, EXPIRED

    @Column(name = "offer_id")
    private Long offerId;  // Set when offer is created from this session

    public ChatSession() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "ACTIVE";
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getConversationJson() {
        return conversationJson;
    }

    public void setConversationJson(String conversationJson) {
        this.conversationJson = conversationJson;
    }

    public String getExtractedDataJson() {
        return extractedDataJson;
    }

    public void setExtractedDataJson(String extractedDataJson) {
        this.extractedDataJson = extractedDataJson;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }
}
