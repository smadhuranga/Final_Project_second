package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   ChatService chatService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO,
                            Principal principal) {

        Long senderId = getUserIdFromPrincipal(principal);
        chatMessageDTO.setSenderId(senderId);
        chatMessageDTO.setTimestamp(LocalDateTime.now());

        chatService.saveMessage(chatMessageDTO);

        messagingTemplate.convertAndSendToUser(
                chatMessageDTO.getReceiverId().toString(),
                "/queue/messages",
                chatMessageDTO
        );
    }
    private Long getUserIdFromPrincipal(Principal principal) {
        return userService.getUserIdByEmail(principal.getName());
    }
}