package com.beautysalon.service;

import com.beautysalon.dto.NotificationResponse;
import com.beautysalon.entity.Notification;
import com.beautysalon.entity.User;
import com.beautysalon.exception.BadRequestException;
import com.beautysalon.exception.ResourceNotFoundException;
import com.beautysalon.repository.NotificationRepository;
import com.beautysalon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void createNotification(Long userId, String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronađen"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getRecentNotifications(Long userId) {

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<NotificationResponse> responses = new ArrayList<>();

        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            responses.add(new NotificationResponse(
                    n.getId(),
                    n.getMessage(),
                    n.getRead(),
                    n.getCreatedAt()
            ));
        }

        return responses;
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notifikacija nije pronađena"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
    public void deleteNotification(Long notificationId, Long userId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notifikacija nije pronađena"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new BadRequestException("Nemate pravo obrisati ovu notifikaciju");
        }

        notificationRepository.delete(notification);
    }
}