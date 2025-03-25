package lk.ijse.back_end.service;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.util.UserType;

import java.util.List;

public interface ChatMessageService {
    int saveChatMessage(ChatMessageDTO chatMessageDTO);
    ChatMessageDTO getChatMessageById(Long id);
    List<ChatMessageDTO> getChatHistory(Long userId, UserType userType);
    ChatMessageDTO saveMessage(ChatMessageDTO messageDTO);

}