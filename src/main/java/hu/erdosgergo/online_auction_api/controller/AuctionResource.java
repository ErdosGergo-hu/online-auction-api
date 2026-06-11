package hu.erdosgergo.online_auction_api.controller;

import hu.erdosgergo.online_auction_api.dto.request.AuctionCreateRequest;
import hu.erdosgergo.online_auction_api.dto.request.CreateBidRequest;
import hu.erdosgergo.online_auction_api.dto.response.AuctionResponse;
import hu.erdosgergo.online_auction_api.search.criteria.AuctionSearchCriteria;
import hu.erdosgergo.online_auction_api.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuctionResource {

    private final AuctionService auctionService;

    @GetMapping("/{id}")
    public AuctionResponse getAuction(@PathVariable Long id) {
        return auctionService.getResponseById(id);
    }

    @GetMapping("/search")
    public Page<AuctionResponse> getPageableAuctions(
            @RequestParam(required = false, defaultValue = "") Object value,
            AuctionSearchCriteria auctionSearchCriteria,
            Pageable pageable) {
        return auctionService.search(auctionSearchCriteria, pageable);
    }

    @PutMapping("/{id}/bid")
    public AuctionResponse updateActionByBid(@PathVariable Long id, @RequestBody @Valid CreateBidRequest request) {
        return auctionService.updateAuctionByRequest(id, request);
    }

    @GetMapping("/me/won")
    public List<AuctionResponse> findWonAuctionsByCurrentUser() {
        return auctionService.findWonAuctionsByCurrentUser();
    }

    @GetMapping("/me/active")
    public List<AuctionResponse> findActiveAuctionsByCurrentUser() {
        return auctionService.findActiveAuctionsByCurrentUser();
    }

    @GetMapping("/me/favorites")
    public List<AuctionResponse> findFavoriteAuctionsByCurrentUser() {
        return auctionService.findFavoriteAuctionsByCurrentUser();
    }

    @PostMapping("/create")
    public AuctionResponse createAuction(@RequestBody @Valid AuctionCreateRequest auctionCreateRequest) {
        return auctionService.createAuction(auctionCreateRequest);
    }
}
