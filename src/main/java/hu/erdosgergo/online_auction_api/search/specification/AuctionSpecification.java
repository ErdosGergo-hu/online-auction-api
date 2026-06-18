package hu.erdosgergo.online_auction_api.search.specification;

import hu.erdosgergo.online_auction_api.model.Auction;
import hu.erdosgergo.online_auction_api.search.criteria.AuctionSearchCriteria;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuctionSpecification extends AbstractSpecification<AuctionSearchCriteria, Auction> {

//    @Override
//    public Specification<@NonNull Auction> build(AuctionSearchCriteria criteria) {
//        Specification<@NonNull Auction> build = super.build(criteria);
//
//        return build.and(buildCustomPredicate(criteria));
//    }
//
//    public Specification<@NonNull Auction> buildCustomPredicate(AuctionSearchCriteria criteria) {
//        return (root, query, cb) -> {
//            if (criteria.getStatus() != null) {
//
//                LocalDateTime now = LocalDateTime.now();
//
//                return switch (criteria.getStatus()) {
//                    case UPCOMING -> cb.greaterThan(root.get("startDateTime"),now);
//                    case ACTIVE ->
//                        cb.and(
//                            cb.greaterThanOrEqualTo(root.get("endDateTime"), now),
//                            cb.lessThanOrEqualTo(root.get("startDateTime"),now)
//                        );
//                    case ENDED -> cb.lessThanOrEqualTo(root.get("endDateTime"), now);
//                };
//            }
//            return cb.conjunction();
//        };
//    }
}