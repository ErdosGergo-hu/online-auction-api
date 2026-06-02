package hu.erdosgergo.travel_booking_api.service;

import hu.erdosgergo.travel_booking_api.dto.request.FavoriteToggleRequest;
import hu.erdosgergo.travel_booking_api.dto.response.FavoriteResponse;
import hu.erdosgergo.travel_booking_api.dto.response.FavoriteToggleResponse;
import hu.erdosgergo.travel_booking_api.exception.ResourceNotFoundException;
import hu.erdosgergo.travel_booking_api.mapper.FavoriteMapper;
import hu.erdosgergo.travel_booking_api.model.Auction;
import hu.erdosgergo.travel_booking_api.model.Favorite;
import hu.erdosgergo.travel_booking_api.model.User;
import hu.erdosgergo.travel_booking_api.repository.AuctionRepository;
import hu.erdosgergo.travel_booking_api.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FavoriteService {

    private final FavoriteRepository repository;
    private final FavoriteMapper mapper;
    private final AuctionRepository auctionRepository;
    private final UserService userService;

    public List<FavoriteResponse> getUserFavorites(Long userId) {
        return mapper.toResponseList(repository.findAllByUserId(userId));
    }

    @Transactional
    public FavoriteToggleResponse toggleFavorite(FavoriteToggleRequest request) {
        Optional<Favorite> favorite = repository.findByAuctionIdAndUserId(request.auctionId(), request.userId());
        if (favorite.isPresent()) {
            repository.delete(favorite.get());
            return new FavoriteToggleResponse(false);
        }

        Auction auction = auctionRepository.findById(request.auctionId()).orElseThrow(() -> new ResourceNotFoundException("Auction is not found with an id: " + request.auctionId()));
        User user = userService.getUserById(request.userId());

        Favorite newFavorite = new Favorite();
        newFavorite.setAuction(auction);
        newFavorite.setUser(user);
        newFavorite.setCreatedAt(Instant.now());

        repository.save(newFavorite);
        return new FavoriteToggleResponse(true);
    }
}
