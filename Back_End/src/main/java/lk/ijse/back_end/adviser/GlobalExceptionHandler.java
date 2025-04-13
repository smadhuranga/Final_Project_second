package lk.ijse.back_end.adviser;

import io.jsonwebtoken.JwtException;
import lk.ijse.back_end.dto.ResponseDTO;
import lk.ijse.back_end.util.EmailException;
import lk.ijse.back_end.util.VarList;
import org.hibernate.ResourceClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
    public ResponseEntity<ResponseDTO> handleAllException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO(
                        VarList.Internal_Server_Error,
                        "An unexpected error occurred: " + ex.getMessage(),
                        null
                ));
    }


    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ResponseDTO> handleEmailException(EmailException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO(
                        500,
                        "Email service error: " + ex.getMessage(),
                        null
                ));
    }

}