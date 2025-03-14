package lk.ijse.back_end.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    private int status;
    private String message;
    private T data;

    public ResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDTO(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}