package lk.ijse.back_end.service.impl;


import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.entity.ChatMessage;
import lk.ijse.back_end.repository.ChatMessageRepo;
import lk.ijse.back_end.service.ChatMessageService;
import lk.ijse.back_end.util.UserType;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepo chatMessageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public int saveChatMessage(ChatMessageDTO chatMessageDTO) {
        chatMessageRepository.save(modelMapper.map(chatMessageDTO, ChatMessage.class));
        return VarList.Created;
    }

    @Override
    public ChatMessageDTO getChatMessageById(Long id) {
        return chatMessageRepository.findById(id)
                .map(msg -> modelMapper.map(msg, ChatMessageDTO.class))
                .orElse(null);
    }

    @Override
    public List<ChatMessageDTO> getChatHistory(Long userId, UserType userType) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestamp(
                userId,
                1L, // Assuming admin has ID 1
                1L,
                userId
        );

        return messages.stream()
                .map(msg -> modelMapper.map(msg, ChatMessageDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO) {
        messageDTO.setSenderId(1L); // Admin ID
        messageDTO.setTimestamp(LocalDateTime.now());

        ChatMessage message = modelMapper.map(messageDTO, ChatMessage.class);
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return modelMapper.map(savedMessage, ChatMessageDTO.class);

    }


}