package hu.erdosgergo.online_auction_api.repository;

import hu.erdosgergo.online_auction_api.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("""
    UPDATE Notification n
    SET n.read = TRUE
    WHERE n.user.id = :userId
      AND n.read = FALSE
    """)
    void markAllAsRead(Long userId);
}