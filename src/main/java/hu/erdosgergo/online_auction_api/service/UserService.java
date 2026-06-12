package hu.erdosgergo.online_auction_api.service;

import hu.erdosgergo.online_auction_api.dto.request.UserUpdateRequest;
import hu.erdosgergo.online_auction_api.dto.response.UserResponse;
import hu.erdosgergo.online_auction_api.mapper.UserMapper;
import hu.erdosgergo.online_auction_api.model.User;
import hu.erdosgergo.online_auction_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    public User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }

    public UserResponse me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("Cannot find the logged in User!");
        }
        return UserResponse.builder().id(user.getId())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .username(user.getUsername())
                .build();
    }

    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new UsernameNotFoundException("User not found by id: " + id));
    }

    @Transactional
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest) {
        User currentUser = getCurrentUser();
        currentUser.setAvatarUrl(userUpdateRequest.avatarUrl());
        currentUser.setEmail(userUpdateRequest.email());
        currentUser.setUsername(userUpdateRequest.username());
        User saved = repository.save(currentUser);

        return mapper.toResponse(saved);
    }
}
