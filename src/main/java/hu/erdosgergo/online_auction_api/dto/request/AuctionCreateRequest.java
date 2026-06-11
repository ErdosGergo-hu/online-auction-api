package hu.erdosgergo.online_auction_api.dto.request;

import hu.erdosgergo.online_auction_api.enums.Category;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionCreateRequest(
        @NotNull String name,
        @NotNull String description,
        @NotNull String imageUrl,
        @NotNull @Positive Integer quantity,
        @NotNull Category category,

        @NotNull @PositiveOrZero BigDecimal startingPriceHuf,
        @NotNull @FutureOrPresent LocalDateTime endDateTime
) {}
