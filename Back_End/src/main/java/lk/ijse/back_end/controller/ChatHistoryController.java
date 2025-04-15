package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.dto.UserDTO;
import lk.ijse.back_end.entity.User;
import lk.ijse.back_end.repository.UserRepo;
import lk.ijse.back_end.service.UserService;
import lk.ijse.back_end.service.impl.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatHistoryController {

    private final ChatService chatService;
    private final UserService userService;
    private final UserRepo userRepository;
    @Autowired
    public ChatHistoryController(ChatService chatService, UserService userService, UserRepo userRepository) {
        this.chatService = chatService;
        this.userService = userService;
       this.userRepository = userRepository;
    }

    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<ResponseDTO<List<ChatMessageDTO>>> getChatHistory(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = getCurrentUserId(userDetails);
        List<ChatMessageDTO> history = chatService.getChatHistory(currentUserId, otherUserId);

        return ResponseEntity.ok(new ResponseDTO<>(
                HttpStatus.OK.value(),
                "Chat history retrieved",
                history
        ));
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        UserDTO user = userService.findUserByEmail(userDetails.getUsername());

        return userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getId();
    }
}