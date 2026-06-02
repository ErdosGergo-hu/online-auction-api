package hu.erdosgergo.travel_booking_api.repository;

import hu.erdosgergo.travel_booking_api.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findAllByUserId(Long userId);

    Optional<Favorite> findByAuctionIdAndUserId(Long auctionId, Long userId);

    @Query("""
    select f.auction.id
    from Favorite f
    where f.user.id = :userId
    """)
    Set<Long> findAuctionIdsByUserId(Long userId);
}
