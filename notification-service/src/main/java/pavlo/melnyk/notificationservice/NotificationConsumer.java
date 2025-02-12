package pavlo.melnyk.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final TelegramNotificationService telegramNotificationService;

    public NotificationConsumer(TelegramNotificationService telegramNotificationService) {
        this.telegramNotificationService = telegramNotificationService;
    }

    @KafkaListener(topics = "accommodation_topic", groupId = "notification_group")
    public void listenAccommodation(String message) {
        System.out.println("Received message from Kafka: " + message);
        telegramNotificationService.sendAccommodationMessage(message);
    }

    @KafkaListener(topics = "booking_topic", groupId = "notification_group")
    public void listenBooking(String message) {
        System.out.println("Received message from Kafka: " + message);
        telegramNotificationService.sendBookingMessage(message);
    }

    @KafkaListener(topics = "payment_topic", groupId = "notification_group")
    public void listenPayment(String message) {
        System.out.println("Received message from Kafka: " + message);
        telegramNotificationService.sendPaymentMessage(message);
    }
}
