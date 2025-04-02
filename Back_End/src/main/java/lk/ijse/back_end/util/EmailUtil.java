package lk.ijse.back_end.util;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    private final JavaMailSender mailSender;

    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("LearningLab - Your OTP Code");
        message.setText("Your One-Time Password (OTP) for LearningLab " + purpose + " is: " + otp +
                "\n\nThis code is valid for 10 minutes. Please do not share this code with anyone.");
        message.setFrom("noreply@fitlifeifms.com");

        mailSender.send(message);
    }

}