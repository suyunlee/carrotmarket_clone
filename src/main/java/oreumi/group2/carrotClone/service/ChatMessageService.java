package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    void saveMessage(Long chatRoomId, String chatMessage); /* 채팅 메세지 (저장) */
    List<ChatMessage> getMessages(Long chatRoomId); /* 채팅 메세지 (get) */
}