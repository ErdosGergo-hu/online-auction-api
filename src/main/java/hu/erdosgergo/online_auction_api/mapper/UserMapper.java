package hu.erdosgergo.online_auction_api.mapper;

import hu.erdosgergo.online_auction_api.dto.response.UserResponse;
import hu.erdosgergo.online_auction_api.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
