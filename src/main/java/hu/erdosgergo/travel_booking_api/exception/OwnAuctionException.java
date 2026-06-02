package hu.erdosgergo.travel_booking_api.exception;

public class OwnAuctionException extends RuntimeException {
    public OwnAuctionException() {
        super("You cannot Bid on your own Auction!");
    }
}
