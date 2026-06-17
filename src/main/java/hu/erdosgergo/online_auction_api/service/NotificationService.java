package hu.erdosgergo.online_auction_api.service;

import hu.erdosgergo.online_auction_api.dto.response.NotificationResponse;
import hu.erdosgergo.online_auction_api.mapper.NotificationMapper;
import hu.erdosgergo.online_auction_api.model.Notification;
import hu.erdosgergo.online_auction_api.model.User;
import hu.erdosgergo.online_auction_api.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final UserService userService;

    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate;

    public void create(User previousBidder, String message) {
        Notification notification = Notification.builder()
                .user(previousBidder)
                .message(message)
                .read(false)
                .createdAt(Instant.now())
                .build();

        Notification saved = repository.save(notification);

        NotificationResponse response = notificationMapper.toResponse(saved);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + previousBidder.getId(),
                response
        );
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        User currentUser = userService.getCurrentUser();
        return repository.countByUserIdAndReadFalse(currentUser.getId());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        User currentUser = userService.getCurrentUser();
        Notification notification = repository
                .findById(notificationId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Notification not found."));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "You can only modify your own notifications.");
        }

        notification.setRead(true);
        repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = userService.getCurrentUser();
        return repository
                .findTop10ByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }
}