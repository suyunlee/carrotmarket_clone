package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    public void sendMessage(Long chatRoomId, ChatMessage chatMessage);
    public List<ChatMessage> getMessages(Long chatRoomId);
}
