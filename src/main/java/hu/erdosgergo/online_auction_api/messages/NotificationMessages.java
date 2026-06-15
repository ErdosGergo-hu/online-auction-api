package hu.erdosgergo.online_auction_api.messages;

public final class NotificationMessages {

    private NotificationMessages() {}

    public static final String OUTBID =
            "You have been outbid on %s by %s";

    public static final String NEW_BID =
            "A new bid has been placed on your auction %s.";

    public static final String AUCTION_WON =
            "Congratulations! You won the auction.";
}