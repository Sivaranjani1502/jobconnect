package com.jobconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DummySmsNotificationService implements SmsNotificationService {

    @Value("${sms.mode:dummy}")
    private String mode; // "dummy" or "off" or later maybe "twilio"

    @Override
    public void sendSms(String toPhoneNumber, String message) {
        if (!isEnabled()) {
            return;
        }

        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            System.out.println("[SMS DUMMY] Skipped (no phone). Message: " + message);
            return;
        }

        // Just log – this is your “fake SMS”
        System.out.println("[SMS DUMMY] To: " + toPhoneNumber + " | " + message);
    }

    @Override
    public boolean isEnabled() {
        return !"off".equalsIgnoreCase(mode);
    }
}
