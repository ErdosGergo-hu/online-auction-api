package hu.erdosgergo.online_auction_api.scheduler;

import hu.erdosgergo.online_auction_api.enums.AuctionStatus;
import hu.erdosgergo.online_auction_api.messages.NotificationMessages;
import hu.erdosgergo.online_auction_api.model.Auction;
import hu.erdosgergo.online_auction_api.repository.AuctionRepository;
import hu.erdosgergo.online_auction_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 30000)
    public void processEndedAuctions() {
        List<Auction> endedAuctions = auctionRepository.findByStatusAndEndDateTimeBefore(AuctionStatus.ACTIVE, LocalDateTime.now());

        log.info("Ending {} auctions", endedAuctions.size());

        endedAuctions.forEach(auction -> {
            auction.setStatus(AuctionStatus.ENDED);
            auctionRepository.save(auction);

            if (auction.getBidder() != null) {
                notificationService.create(auction.getBidder(), NotificationMessages.AUCTION_WON.formatted(auction.getItem().getName()));
            }

            notificationService.create(auction.getSeller(), NotificationMessages.AUCTION_ENDED.formatted(auction.getItem().getName()));

            log.info("Auction {} ended.", auction.getItem().getName());
        });

    }
}