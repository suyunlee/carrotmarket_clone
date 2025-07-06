package oreumi.group2.carrotClone.service.Impl;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.ChatMessage;
import oreumi.group2.carrotClone.model.ChatRoom;
import oreumi.group2.carrotClone.repository.ChatMessageRepository;
import oreumi.group2.carrotClone.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void saveMessage(Long chatRoomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방이 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(content)
                .build();

        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessages(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages;
    }
}
