package lk.ijse.back_end.service.impl;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.entity.ChatMessage;
import lk.ijse.back_end.repository.ChatMessageRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ChatService {

    private final ChatMessageRepo chatMessageRepository;

    public ChatService(ChatMessageRepo chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public void saveMessage(ChatMessageDTO messageDTO) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setTimestamp(messageDTO.getTimestamp());
        chatMessageRepository.save(message);
    }

    public List<ChatMessageDTO> getChatHistory(Long user1, Long user2) {
        return chatMessageRepository.findConversation(user1, user2)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}