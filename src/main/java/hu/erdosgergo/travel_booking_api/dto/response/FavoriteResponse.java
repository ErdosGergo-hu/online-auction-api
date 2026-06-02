package hu.erdosgergo.travel_booking_api.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class FavoriteResponse {
    private Long id;

    private UserResponse user;

    private AuctionResponse auction;

    private Instant createdAt;

    private boolean favorite;
}
