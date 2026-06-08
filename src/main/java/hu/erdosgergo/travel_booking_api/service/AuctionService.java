package hu.erdosgergo.travel_booking_api.service;

import hu.erdosgergo.travel_booking_api.dto.request.CreateBidRequest;
import hu.erdosgergo.travel_booking_api.dto.response.AuctionResponse;
import hu.erdosgergo.travel_booking_api.exception.BidTooLowException;
import hu.erdosgergo.travel_booking_api.exception.OwnAuctionException;
import hu.erdosgergo.travel_booking_api.mapper.AuctionMapper;
import hu.erdosgergo.travel_booking_api.model.Favorite;
import hu.erdosgergo.travel_booking_api.repository.FavoriteRepository;
import hu.erdosgergo.travel_booking_api.search.criteria.AuctionSearchCriteria;
import hu.erdosgergo.travel_booking_api.model.Auction;
import hu.erdosgergo.travel_booking_api.repository.AuctionRepository;
import hu.erdosgergo.travel_booking_api.search.specification.AuctionSpecification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuctionService {

    private final AuctionRepository repository;

    private final AuctionSpecification auctionSpecification;

    private final BidService bidService;

    private final AuctionMapper mapper;

    private final UserService userService;

    private final FavoriteRepository favoriteRepository;

    public AuctionResponse getResponseById(Long id) {
        Auction auction = getAuctionById(id);
        return getResponseByAuction(auction);
    }

    private AuctionResponse getResponseByAuction(Auction auction) {
        AuctionResponse response = mapper.toResponse(auction);
        int bidCountForAuction = bidService.getBidCountForAuction(auction.getId());
        response.setBidCount(bidCountForAuction);
        return response;
    }

    @Transactional
    public Auction getAuctionById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional
    public Auction updateAuctionByPrice(Long id, CreateBidRequest request) {
        BigDecimal newPrice = request.newPrice();
        Long userId = request.userId();
        Auction auction = repository.findById(id).orElseThrow();

        if(auction.getSeller().getId().equals(userId)) {
            throw new OwnAuctionException();
        }

        if(auction.getCurrentPriceHuf().compareTo(newPrice) >= 0) {
            throw new BidTooLowException(auction.getCurrentPriceHuf().setScale(0, RoundingMode.DOWN), newPrice);
        }

        auction.setCurrentPriceHuf(newPrice);
        auction.setBidder(userService.getUserById(userId));

        bidService.createBidByAuction(auction);
        return repository.save(auction);
    }

    public AuctionResponse updateAuctionByRequest(Long id, CreateBidRequest request) {
        Auction auction = updateAuctionByPrice(id, request);
        return getResponseByAuction(auction);
    }

    public Page<@NonNull AuctionResponse> search(AuctionSearchCriteria criteria, Pageable pageable) {
        Page<@NonNull Auction> auctionPage;
        if(criteria == null) {
            auctionPage = repository.findAll(pageable);
        } else {
            Specification<@NonNull Auction> spec = auctionSpecification.build(criteria);
            auctionPage = repository.findAll(spec, pageable);
        }
        Long currentUserId = userService.getCurrentUser().getId();
        Set<Long> auctionIdsByUserId = favoriteRepository.findAuctionIdsByUserId(currentUserId);

        return auctionPage.map(auction -> {
            AuctionResponse response = mapper.toResponse(auction);
            response.setFavorite(auctionIdsByUserId.contains(auction.getId()));
            return response;
        });
    }

    public List<AuctionResponse> findWonAuctionsByCurrentUser() {
        Long currentUserId = userService.getCurrentUser().getId();
        List<Auction> wonAuctions = repository.findWonAuctionsByBidder(currentUserId);
        return mapper.toResponseList(wonAuctions);
    }

    public List<AuctionResponse> findActiveAuctionsByCurrentUser() {
        Long currentUserId = userService.getCurrentUser().getId();
        List<Auction> wonAuctions = repository.findActiveAuctionsByBidder(currentUserId);
        return mapper.toResponseList(wonAuctions);
    }

    public List<AuctionResponse> findFavoriteAuctionsByCurrentUser() {
        Long currentUserId = userService.getCurrentUser().getId();
        List<Favorite> favorites = favoriteRepository.findAllByUserId(currentUserId);
        List<Auction> favoriteAuctions = favorites.stream().map(Favorite::getAuction).toList();
        return mapper.toResponseList(favoriteAuctions);
    }

}
