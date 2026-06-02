package hu.erdosgergo.travel_booking_api.dto.request;

import jakarta.validation.constraints.NotNull;

public record FavoriteToggleRequest(
        @NotNull Long auctionId,
        @NotNull Long userId
) {}
