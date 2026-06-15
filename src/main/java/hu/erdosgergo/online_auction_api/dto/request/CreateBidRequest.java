package hu.erdosgergo.online_auction_api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateBidRequest(
        @NotNull BigDecimal newPrice
) {}