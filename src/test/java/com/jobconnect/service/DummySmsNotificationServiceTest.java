package com.jobconnect.service;

import org.junit.jupiter.api.Test;

class DummySmsNotificationServiceTest {

    @Test
    void sendSms_doesNotThrow() {
        DummySmsNotificationService s = new DummySmsNotificationService();
        s.sendSms("9999999999", "test message");
        // No assertions — just ensure no exception and manual check of logs if needed
    }
}
