package hu.erdosgergo.travel_booking_api.mapper;

import hu.erdosgergo.travel_booking_api.dto.response.AuctionResponse;
import hu.erdosgergo.travel_booking_api.model.Auction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    AuctionResponse toResponse(Auction auction);

    List<AuctionResponse> toResponseList(List<Auction> auctions);
}
