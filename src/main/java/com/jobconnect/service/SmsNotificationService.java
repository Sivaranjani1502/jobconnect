package com.jobconnect.service;

public interface SmsNotificationService {

    void sendSms(String toPhoneNumber, String message);

    boolean isEnabled();
}
