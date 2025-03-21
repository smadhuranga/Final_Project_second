package lk.ijse.back_end.adviser;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwt() {
        return ResponseEntity.status(401).body("Invalid token format");
    }
}