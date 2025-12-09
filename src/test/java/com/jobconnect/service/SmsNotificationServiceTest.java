package com.jobconnect.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SmsNotificationServiceTest {

    // If you have an interface and a Dummy implementation, test the dummy logs
    @Test
    void dummy_send_doesNotThrow() {
        DummySmsNotificationService s = new DummySmsNotificationService();
        s.sendSms("9999999999", "hello"); // should print to console / not fail
    }
}
