package hu.erdosgergo.online_auction_api.mapper;

import hu.erdosgergo.online_auction_api.dto.response.NotificationResponse;
import hu.erdosgergo.online_auction_api.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}
