package hu.erdosgergo.online_auction_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

@Entity
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean read;

    @Column(nullable = false)
    private Instant createdAt;
}