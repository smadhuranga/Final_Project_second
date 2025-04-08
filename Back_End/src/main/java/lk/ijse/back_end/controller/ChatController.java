package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    // Add UserService to constructor
    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatService chatService,
                          UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(@Payload ChatMessageDTO message,
                                  SimpMessageHeaderAccessor headerAccessor) {
        // Get sender ID from authenticated user
        String username = headerAccessor.getUser().getName();
        Long senderId = userService.getUserIdByEmail(username);

        // Set values using correct DTO methods
        message.setSenderId(senderId);
        message.setTimestamp(LocalDateTime.now());

        chatService.saveMessage(message);

        // Use getReceiverId() instead of getRecipientId()
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(), // Convert Long to String for user destination
                "/queue/messages",
                message
        );
    }
}