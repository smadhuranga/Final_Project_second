package lk.ijse.back_end.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
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
        message.setFrom("edusync58@gmail.com");

        mailSender.send(message);
    }


    public void sendChatNotification(String sellerEmail, String customerName, String customerEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(sellerEmail);
            message.setSubject("New Chat Request on UNI Freelancers");
            message.setText(
                    "Hello Seller,\n\nYou have a new chat request from: " + customerName +
                            " (" + customerEmail + ")\n\n" +
                            "Please login to your account to respond.\n\n" +
                            "Best regards,\nUNI Freelancers Team"
            );
            message.setFrom("edusync58@gmail.com");

            mailSender.send(message);
            System.out.println( "Email sent to: " + sellerEmail);
        } catch (MailAuthenticationException ex) {
            log.error("Error authenticating with email server", ex);
            throw new EmailException("Email server authentication failed", ex);
        } catch (MailSendException ex) {
           log.error("Error sending email to: " + sellerEmail, ex);
            throw new EmailException("Email server connection failed", ex);
        }
    }

    public void sendCustomerChatNotification(String customerEmail, String senderName, String senderEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(customerEmail);
            message.setSubject("New Chat Request from UNI Freelancers");
            message.setText(
                    "Hello Customer,\n\nYou have a new chat request from: " + senderName +
                            " (" + senderEmail + ")\n\n" +
                            "Please login to your account to respond.\n\n" +
                            "Best regards,\nUNI Freelancers Team"
            );
            message.setFrom("edusync58@gmail.com");

            mailSender.send(message);
            System.out.println("Customer notification sent to: " + customerEmail);
        } catch (MailException ex) {
            log.error("Error sending customer notification", ex);
            throw new EmailException("Failed to send customer notification", ex);
        }
    }

}