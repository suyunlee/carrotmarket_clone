package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage saveMessage(Long chatRoomId, String content, String username); /* 채팅 메세지 (저장) */
    List<ChatMessage> getMessages(Long chatRoomId); /* 채팅 메세지 (get) */
}