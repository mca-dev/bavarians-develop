/**
 * Chatbot AI Assistant
 * Handles chat interface, API communication, and offer preview
 */

const Chatbot = (function() {
    'use strict';

    // State
    let sessionId = null;
    let isWaitingForResponse = false;
    let offerPreviewData = null;

    // DOM Elements
    const elements = {
        floatingBtn: null,
        modal: null,
        messagesContainer: null,
        messageInput: null,
        sendBtn: null,
        statusAlert: null,
        statusText: null,
        offerPreview: null,
        offerItems: null,
        laborTotal: null,
        partsTotal: null,
        grandTotal: null,
        confirmBtn: null,
        editBtn: null
    };

    /**
     * Initialize chatbot
     */
    function init() {
        cacheElements();
        attachEventListeners();
        checkChatbotStatus();
    }

    /**
     * Cache DOM elements
     */
    function cacheElements() {
        elements.floatingBtn = document.getElementById('chatbot-floating-button');
        elements.modal = $('#chatbotModal');
        elements.messagesContainer = document.getElementById('chatbot-messages');
        elements.messageInput = document.getElementById('chatbot-message-input');
        elements.sendBtn = document.getElementById('chatbot-send-btn');
        elements.statusAlert = document.getElementById('chatbot-status');
        elements.statusText = document.getElementById('chatbot-status-text');
        elements.offerPreview = document.getElementById('chatbot-offer-preview');
        elements.offerItems = document.getElementById('chatbot-offer-items');
        elements.laborTotal = document.getElementById('chatbot-labor-total');
        elements.partsTotal = document.getElementById('chatbot-parts-total');
        elements.grandTotal = document.getElementById('chatbot-grand-total');
        elements.confirmBtn = document.getElementById('chatbot-confirm-btn');
        elements.editBtn = document.getElementById('chatbot-edit-btn');
    }

    /**
     * Attach event listeners
     */
    function attachEventListeners() {
        // Floating button opens modal
        if (elements.floatingBtn) {
            elements.floatingBtn.addEventListener('click', openModal);
        }

        // Send message on button click
        if (elements.sendBtn) {
            elements.sendBtn.addEventListener('click', sendMessage);
        }

        // Send message on Enter (Shift+Enter for new line)
        if (elements.messageInput) {
            elements.messageInput.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }

        // Confirm offer button
        if (elements.confirmBtn) {
            elements.confirmBtn.addEventListener('click', confirmOffer);
        }

        // Edit button (hide preview and continue chat)
        if (elements.editBtn) {
            elements.editBtn.addEventListener('click', function() {
                hideOfferPreview();
                addAssistantMessage('Oczywiście! Co chciałbyś zmienić?');
            });
        }

        // Reset on modal close
        elements.modal.on('hidden.bs.modal', function() {
            // Don't reset session - allow continuing conversation
        });
    }

    /**
     * Check if chatbot is available
     */
    function checkChatbotStatus() {
        fetch('/api/chatbot/status', {
            method: 'GET',
            headers: getAuthHeaders()
        })
        .then(response => response.json())
        .then(data => {
            if (data.available) {
                console.log('Chatbot available:', data.message);
            } else {
                console.warn('Chatbot unavailable:', data.message);
                showStatus('warning', 'Asystent AI jest obecnie niedostępny. Użyj formularza ręcznego.');
            }
        })
        .catch(error => {
            console.error('Error checking chatbot status:', error);
        });
    }

    /**
     * Open modal
     */
    function openModal() {
        elements.modal.modal('show');
        elements.messageInput.focus();
    }

    /**
     * Send message to chatbot
     */
    function sendMessage() {
        const message = elements.messageInput.value.trim();

        if (!message || isWaitingForResponse) {
            return;
        }

        // Add user message to chat
        addUserMessage(message);

        // Clear input
        elements.messageInput.value = '';

        // Show typing indicator
        showTypingIndicator();

        // Send to API
        const requestData = {
            message: message,
            sessionId: sessionId,
            vehicleId: null  // TODO: Get from context if on vehicle page
        };

        isWaitingForResponse = true;

        fetch('/api/chatbot/message', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify(requestData)
        })
        .then(response => {
            if (response.status === 401) {
                throw new Error('Musisz być zalogowany, aby korzystać z asystenta.');
            }
            if (!response.ok) {
                throw new Error('Błąd komunikacji z serwerem');
            }
            return response.json();
        })
        .then(data => {
            hideTypingIndicator();
            isWaitingForResponse = false;

            if (data.errorMessage) {
                addAssistantMessage('Przepraszam, wystąpił błąd: ' + data.errorMessage);
                return;
            }

            // Update session ID
            if (data.sessionId) {
                sessionId = data.sessionId;
            }

            // Add assistant response
            if (data.responseText) {
                addAssistantMessage(data.responseText);
            }

            // Show offer preview if ready
            if (data.needsConfirmation && data.ofertaPreview) {
                offerPreviewData = data.ofertaPreview;
                showOfferPreview(data.ofertaPreview);
            }
        })
        .catch(error => {
            hideTypingIndicator();
            isWaitingForResponse = false;
            console.error('Error sending message:', error);
            addAssistantMessage('Przepraszam, nie udało się przetworzyć wiadomości. ' + error.message);
        });
    }

    /**
     * Confirm and create offer
     */
    function confirmOffer() {
        if (!sessionId) {
            alert('Brak aktywnej sesji');
            return;
        }

        elements.confirmBtn.disabled = true;
        elements.confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Tworzenie oferty...';

        fetch('/api/chatbot/confirm', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify({ sessionId: sessionId })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.offerId) {
                // Redirect to offer details page
                addAssistantMessage('Oferta została utworzona pomyślnie! Przekierowuję...');
                setTimeout(() => {
                    window.location.href = data.redirectUrl || '/oferty/' + data.offerId;
                }, 1000);
            } else {
                elements.confirmBtn.disabled = false;
                elements.confirmBtn.innerHTML = '<i class="fas fa-check mr-2"></i>Zatwierdź i Zapisz Ofertę';
                alert('Nie udało się utworzyć oferty: ' + (data.message || 'Nieznany błąd'));
            }
        })
        .catch(error => {
            elements.confirmBtn.disabled = false;
            elements.confirmBtn.innerHTML = '<i class="fas fa-check mr-2"></i>Zatwierdź i Zapisz Ofertę';
            console.error('Error confirming offer:', error);
            alert('Błąd podczas tworzenia oferty: ' + error.message);
        });
    }

    /**
     * Add user message to chat
     */
    function addUserMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'chatbot-message chatbot-user';
        messageDiv.innerHTML = `
            <div class="chatbot-message-content">
                <p>${escapeHtml(text)}</p>
            </div>
            <div class="chatbot-message-avatar">
                <i class="fas fa-user"></i>
            </div>
        `;
        elements.messagesContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    /**
     * Add assistant message to chat
     */
    function addAssistantMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'chatbot-message chatbot-assistant';
        messageDiv.innerHTML = `
            <div class="chatbot-message-avatar">
                <i class="fas fa-robot"></i>
            </div>
            <div class="chatbot-message-content">
                <p>${escapeHtml(text).replace(/\n/g, '<br>')}</p>
            </div>
        `;
        elements.messagesContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    /**
     * Show typing indicator
     */
    function showTypingIndicator() {
        const typingDiv = document.createElement('div');
        typingDiv.id = 'chatbot-typing';
        typingDiv.className = 'chatbot-message chatbot-assistant';
        typingDiv.innerHTML = `
            <div class="chatbot-message-avatar">
                <i class="fas fa-robot"></i>
            </div>
            <div class="chatbot-message-content">
                <div class="chatbot-typing-indicator">
                    <span></span><span></span><span></span>
                </div>
            </div>
        `;
        elements.messagesContainer.appendChild(typingDiv);
        scrollToBottom();
    }

    /**
     * Hide typing indicator
     */
    function hideTypingIndicator() {
        const typingDiv = document.getElementById('chatbot-typing');
        if (typingDiv) {
            typingDiv.remove();
        }
    }

    /**
     * Show offer preview
     */
    function showOfferPreview(preview) {
        // Clear previous items
        elements.offerItems.innerHTML = '';

        // Add service items
        if (preview.serviceItems && preview.serviceItems.length > 0) {
            preview.serviceItems.forEach(item => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${escapeHtml(item.serviceName)}</td>
                    <td>${formatCurrency(item.laborCost)}</td>
                    <td>${formatCurrency(item.partsCost)}</td>
                    <td>${formatCurrency(item.itemTotal)}</td>
                `;
                elements.offerItems.appendChild(row);
            });
        }

        // Update totals
        elements.laborTotal.textContent = formatCurrency(preview.laborTotal);
        elements.partsTotal.textContent = formatCurrency(preview.partsTotal);
        elements.grandTotal.textContent = formatCurrency(preview.grandTotal);

        // Show preview
        elements.offerPreview.style.display = 'block';
        scrollToBottom();
    }

    /**
     * Hide offer preview
     */
    function hideOfferPreview() {
        elements.offerPreview.style.display = 'none';
        offerPreviewData = null;
    }

    /**
     * Show status message
     */
    function showStatus(type, message) {
        elements.statusAlert.className = 'alert alert-' + type;
        elements.statusText.textContent = message;
        elements.statusAlert.style.display = 'block';

        setTimeout(() => {
            elements.statusAlert.style.display = 'none';
        }, 5000);
    }

    /**
     * Scroll chat to bottom
     */
    function scrollToBottom() {
        setTimeout(() => {
            elements.messagesContainer.scrollTop = elements.messagesContainer.scrollHeight;
        }, 100);
    }

    /**
     * Get authentication headers (JWT token)
     */
    function getAuthHeaders() {
        // Try to get token from localStorage or sessionStorage
        const token = localStorage.getItem('jwt_token') || sessionStorage.getItem('jwt_token');

        if (token) {
            return {
                'Authorization': 'Bearer ' + token
            };
        }

        // If no token in storage, try to get from cookie (if using cookie-based auth)
        return {};
    }

    /**
     * Format currency
     */
    function formatCurrency(amount) {
        if (!amount) amount = 0;
        return parseFloat(amount).toFixed(2) + ' PLN';
    }

    /**
     * Escape HTML to prevent XSS
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Public API
    return {
        init: init
    };

})();

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    Chatbot.init();
});
