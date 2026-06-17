package hu.erdosgergo.online_auction_api.service;

import hu.erdosgergo.online_auction_api.dto.request.AuctionCreateRequest;
import hu.erdosgergo.online_auction_api.dto.request.CreateBidRequest;
import hu.erdosgergo.online_auction_api.dto.response.AuctionResponse;
import hu.erdosgergo.online_auction_api.exception.BidTooLowException;
import hu.erdosgergo.online_auction_api.exception.OwnAuctionException;
import hu.erdosgergo.online_auction_api.exception.ResourceNotFoundException;
import hu.erdosgergo.online_auction_api.mapper.AuctionMapper;
import hu.erdosgergo.online_auction_api.messages.NotificationMessages;
import hu.erdosgergo.online_auction_api.model.Favorite;
import hu.erdosgergo.online_auction_api.model.Item;
import hu.erdosgergo.online_auction_api.model.User;
import hu.erdosgergo.online_auction_api.repository.FavoriteRepository;
import hu.erdosgergo.online_auction_api.repository.ItemRepository;
import hu.erdosgergo.online_auction_api.search.criteria.AuctionSearchCriteria;
import hu.erdosgergo.online_auction_api.model.Auction;
import hu.erdosgergo.online_auction_api.repository.AuctionRepository;
import hu.erdosgergo.online_auction_api.search.specification.AuctionSpecification;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

    private final ItemRepository itemRepository;

    private final NotificationService notificationService;

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
        User bidder = userService.getCurrentUser();
        BigDecimal newPrice = request.newPrice();
        Auction auction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Auction is not found with an id: " + id));

        validateBid(auction, bidder, newPrice);

        User previousBidder = auction.getBidder();
        auction.setBidder(bidder);
        auction.setCurrentPriceHuf(newPrice);

        notifyUsers(auction, previousBidder);

        bidService.createBidByAuction(auction, bidder);
        return repository.save(auction);
    }

    private void validateBid(Auction auction, User bidder, BigDecimal newPrice) {
        if(auction.getSeller().getId().equals(bidder.getId())) {
            throw new OwnAuctionException();
        }

        if(auction.getCurrentPriceHuf().compareTo(newPrice) >= 0) {
            throw new BidTooLowException(auction.getCurrentPriceHuf().setScale(0, RoundingMode.DOWN), newPrice);
        }
    }

    private void notifyUsers(Auction auction, User previousBidder) {
        if(previousBidder != null && !Objects.equals(previousBidder.getId(), auction.getBidder().getId())) {
            notificationService.create(
                previousBidder,
                NotificationMessages.OUTBID.formatted(
                        auction.getItem().getName(),
                        auction.getBidder().getUsername()));
        }

        notificationService.create(
                auction.getSeller(),
                NotificationMessages.NEW_BID.formatted(
                        auction.getItem().getName()
                )
        );
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

    @Transactional
    public AuctionResponse createAuction(AuctionCreateRequest auctionCreateRequest) {
        Item item = new Item();
        item.setCategory(auctionCreateRequest.category());
        item.setName(auctionCreateRequest.name());
        item.setDescription(auctionCreateRequest.description());
        item.setQuantity(auctionCreateRequest.quantity());
        item.setImageUrl(auctionCreateRequest.imageUrl());

        Item savedItem = itemRepository.save(item);

        Auction auction = new Auction();
        auction.setItem(savedItem);
        auction.setStartingPriceHuf(auctionCreateRequest.startingPriceHuf());
        auction.setCurrentPriceHuf(auctionCreateRequest.startingPriceHuf());
        auction.setStartDateTime(LocalDateTime.now());
        auction.setEndDateTime(auctionCreateRequest.endDateTime());
        auction.setSeller(userService.getCurrentUser());

        Auction savedAuction = repository.save(auction);

        return mapper.toResponse(savedAuction);
    }

}
