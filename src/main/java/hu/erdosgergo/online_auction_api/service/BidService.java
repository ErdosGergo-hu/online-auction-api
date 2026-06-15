package hu.erdosgergo.online_auction_api.service;

import hu.erdosgergo.online_auction_api.dto.response.BidResponse;
import hu.erdosgergo.online_auction_api.mapper.BidMapper;
import hu.erdosgergo.online_auction_api.model.Auction;
import hu.erdosgergo.online_auction_api.model.Bid;
import hu.erdosgergo.online_auction_api.model.User;
import hu.erdosgergo.online_auction_api.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository repository;
    private final BidMapper mapper;

    @Transactional
    public void createBidByAuction(Auction auction, User bidder) {
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setCreatedAt(LocalDateTime.now());
        bid.setAmountHuf(auction.getCurrentPriceHuf());
        bid.setBidder(bidder);
        repository.save(bid);
    }

    public int getBidCountForAuction(Long auctionId) {
        return repository.countByAuctionId(auctionId);
    }

    public List<BidResponse> getBidByAuction(Long auctionId) {
        List<Bid> bids = repository.findAllByAuctionIdOrderByIdDesc(auctionId);
        return mapper.toResponseList(bids);
    }
}
