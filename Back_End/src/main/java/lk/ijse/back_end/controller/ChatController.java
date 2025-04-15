// ChatController.java
package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.entity.User;
import lk.ijse.back_end.repository.UserRepo;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final UserRepo userRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatService chatService,
                          UserService userService, UserRepo userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat")
    public void handleChatMessage(ChatMessageDTO message, Principal principal) {
        User sender = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        lk.ijse.back_end.entity.User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new UsernameNotFoundException("Receiver not found"));

        message.setSenderId(sender.getId());
        message.setTimestamp(LocalDateTime.now());

        chatService.saveMessage(message);

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                message
        );
    }

    @GetMapping("/verify-seller/{sellerId}")
    public ResponseEntity<ResponseDTO<Boolean>> verifySellerExists(
            @PathVariable Long sellerId,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean exists = userService.userExists(sellerId);
        return ResponseEntity.ok(new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Verification complete",
                exists
        ));
    }
}