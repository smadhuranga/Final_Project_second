package lk.ijse.back_end.adviser;

import io.jsonwebtoken.JwtException;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.util.VarList;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseDTO<>(400, ex.getMessage(), null));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseDTO<?>> handleNotFound(EmptyResultDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(404, "User not found", null));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO(VarList.Not_Found, ex.getMessage(), null));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseDTO> handleJwtErrors(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDTO(VarList.Unauthorized, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleAllExceptions(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO<>(500, "Internal Server Error", null));
    }
}