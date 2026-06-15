package hu.erdosgergo.online_auction_api.dto.response;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String message,
        boolean read,
        Instant createdAt
) {}