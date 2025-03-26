package lk.ijse.back_end.service.impl;


import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.entity.ChatMessage;
import lk.ijse.back_end.repository.ChatMessageRepo;
import lk.ijse.back_end.service.ChatMessageService;
import lk.ijse.back_end.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}