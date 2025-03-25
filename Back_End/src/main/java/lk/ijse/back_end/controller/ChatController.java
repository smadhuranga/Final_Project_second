package lk.ijse.back_end.controller;

import lk.ijse.back_end.dto.ChatMessageDTO;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.service.ChatMessageService;
import lk.ijse.back_end.util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/chat")
@PreAuthorize("hasRole('ADMIN')")
public class ChatController {

    @Autowired
    private ChatMessageService chatService;

    @GetMapping("/history")
    public ResponseDTO<List<ChatMessageDTO>> getChatHistory(
            @RequestParam Long userId,
            @RequestParam UserType userType) {

        return new ResponseDTO<>(200, "Success",
                chatService.getChatHistory(userId, userType));
    }

    @PostMapping
    public ResponseDTO<ChatMessageDTO> sendMessage(
            @RequestBody ChatMessageDTO message) {

        return new ResponseDTO<>(201, "Message sent",
                chatService.saveMessage(message));
    }
}