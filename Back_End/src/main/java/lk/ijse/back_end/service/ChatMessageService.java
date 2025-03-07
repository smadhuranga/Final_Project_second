package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ChatMessageDTO;

public interface ChatMessageService {
    int saveChatMessage(ChatMessageDTO chatMessageDTO);
    ChatMessageDTO getChatMessageById(Long id);
}