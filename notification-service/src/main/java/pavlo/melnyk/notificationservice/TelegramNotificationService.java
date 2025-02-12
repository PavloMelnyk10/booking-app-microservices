package pavlo.melnyk.notificationservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final RestTemplate restTemplate;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;
    @Value("${telegram.topics.bookings}")
    private int bookingsThreadId;
    @Value("${telegram.topics.payments}")
    private int paymentsThreadId;
    @Value("${telegram.topics.accommodations}")
    private int accommodationsThreadId;

    public void sendTelegramMessage(String message, int threadId) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&message_thread_id=%d&text=%s",
                botToken, chatId, threadId, message
        );
        try {
            restTemplate.getForObject(url, String.class);
            System.out.println("Telegram message sent successfully!");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                int retryAfter = 10;
                System.err.println("Too many requests, waiting for " + retryAfter + " seconds...");
                try {
                    Thread.sleep(retryAfter * 1000L);
                    restTemplate.getForObject(url, String.class);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } else {
                System.err.println("Error sending Telegram message: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendMessage(String message, int threadId) {
        sendTelegramMessage(message, threadId);
    }

    public void sendBookingMessage(String message) {
        sendMessage(message, bookingsThreadId);
    }

    public void sendPaymentMessage(String message) {
        sendMessage(message, paymentsThreadId);
    }

    public void sendAccommodationMessage(String message) {
        sendMessage(message, accommodationsThreadId);
    }
}
