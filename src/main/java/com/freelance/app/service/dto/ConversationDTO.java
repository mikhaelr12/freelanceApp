package com.freelance.app.service.dto;

public record ConversationDTO(Long conversationId, String lastMessage, Boolean isPersonal, String receiverName) {}
