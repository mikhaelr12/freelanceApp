package com.freelance.app.service.dto;

public class ConversationDTO {

    private Long conversationId;
    private String lastMessage;
    private Boolean isPersonal;
    private String receiverName;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Boolean getPersonal() {
        return isPersonal;
    }

    public void setPersonal(Boolean personal) {
        isPersonal = personal;
    }

    public ConversationDTO conversationId(Long conversationId) {
        this.conversationId = conversationId;
        return this;
    }

    public ConversationDTO lastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public ConversationDTO receiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public ConversationDTO isPersonal(Boolean isPersonal) {
        this.isPersonal = isPersonal;
        return this;
    }
}
