package pavlo.melnyk.notificationservice;

public interface NotificationService {
    void sendMessage(String message, int topicId);
}
