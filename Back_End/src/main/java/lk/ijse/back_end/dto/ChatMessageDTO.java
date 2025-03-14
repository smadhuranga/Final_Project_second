package lk.ijse.back_end.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime timestamp;
}
