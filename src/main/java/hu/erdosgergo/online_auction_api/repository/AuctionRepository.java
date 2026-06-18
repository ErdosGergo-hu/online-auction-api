package hu.erdosgergo.online_auction_api.repository;

import hu.erdosgergo.online_auction_api.enums.AuctionStatus;
import hu.erdosgergo.online_auction_api.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {

    @Query(value = """
    SELECT *
    FROM auctions a
    WHERE a.end_date_time < CURRENT_TIMESTAMP
    AND a.bidder_id =:bidderId
    """,
    nativeQuery = true)
    List<Auction> findWonAuctionsByBidder(@Param("bidderId") Long bidderId);

    @Query(value = """
    SELECT *
    FROM auctions a
    WHERE a.start_date_time < CURRENT_TIMESTAMP
    AND a.end_date_time > CURRENT_TIMESTAMP
    AND a.bidder_id =:bidderId
    """,
            nativeQuery = true)
    List<Auction> findActiveAuctionsByBidder(@Param("bidderId") Long bidderId);

    List<Auction> findByStatusAndEndDateTimeBefore(AuctionStatus status, LocalDateTime localDateTime);
}
