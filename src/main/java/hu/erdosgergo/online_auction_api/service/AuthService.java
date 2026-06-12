package hu.erdosgergo.online_auction_api.service;

import hu.erdosgergo.online_auction_api.dto.request.LoginRequest;
import hu.erdosgergo.online_auction_api.dto.request.RefreshRequest;
import hu.erdosgergo.online_auction_api.dto.request.RegisterRequest;
import hu.erdosgergo.online_auction_api.dto.response.AuthResponse;
import hu.erdosgergo.online_auction_api.dto.response.UserResponse;
import hu.erdosgergo.online_auction_api.model.RefreshToken;
import hu.erdosgergo.online_auction_api.repository.RefreshTokenRepository;
import hu.erdosgergo.online_auction_api.repository.UserRepository;
import hu.erdosgergo.online_auction_api.security.JwtService;
import hu.erdosgergo.online_auction_api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl(request.avatarUrl())
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        new BCryptPasswordEncoder().encode("password123");
        // Throws if credentials are wrong
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Rotate refresh token on every login
        refreshTokenRepository.deleteByUser(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (stored.isExpired()) {
            refreshTokenRepository.delete(stored);
            throw new IllegalArgumentException("Refresh token expired, please log in again");
        }

        // Rotate: delete old, issue new
        User user = stored.getUser();
        refreshTokenRepository.delete(stored);
        refreshTokenRepository.flush();
        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String email) {
        userRepository.findByEmail(email)
                .ifPresent(refreshTokenRepository::deleteByUser);
    }

    @Transactional
    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiry())
                .user(UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .avatarUrl(user.getAvatarUrl())
                        .createdAt(user.getCreatedAt())
                        .username(user.getUsername())
                        .build())
                .build();
    }

    @Transactional
    private String createRefreshToken(User user) {
        var token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiry))
                .build();

        return refreshTokenRepository.save(token).getToken();
    }
}
