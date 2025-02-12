package pavlo.melnyk.accommodationservice.service.notification;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaNotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
