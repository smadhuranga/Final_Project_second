package lk.ijse.back_end.util;


import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OtpUtil {

    private final ConcurrentHashMap<String, OtpData> otpStore = new ConcurrentHashMap<>();

    private static class OtpData {
        String otp;
        LocalDateTime expiryTime;

        OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    public String generateOtp(String email) {
        String otp = String.format("%04d", new Random().nextInt(10000));
        otpStore.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(10)));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData data = otpStore.get(email);
        if (data == null || LocalDateTime.now().isAfter(data.expiryTime)) {
            otpStore.remove(email);
            return false;
        }
        return data.otp.equals(otp);
    }

    public void removeOtp(String email) {
        otpStore.remove(email);
    }

}