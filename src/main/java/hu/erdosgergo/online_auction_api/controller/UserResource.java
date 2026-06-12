package hu.erdosgergo.online_auction_api.controller;

import hu.erdosgergo.online_auction_api.dto.request.UserUpdateRequest;
import hu.erdosgergo.online_auction_api.dto.response.UserResponse;
import hu.erdosgergo.online_auction_api.service.UserDashboardViewService;
import hu.erdosgergo.online_auction_api.service.UserService;
import hu.erdosgergo.online_auction_api.view.UserDashboardView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final UserDashboardViewService userDashboardViewService;

    @GetMapping("/dashboard")
    public List<UserDashboardView> getDashboardUsers() {
        return userDashboardViewService.findAll();
    }

    @PutMapping
    public UserResponse updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(userUpdateRequest);
    }

    @GetMapping("/profile/{userId}")
    public UserDashboardView getUserStat(@PathVariable Long userId) {
        return userDashboardViewService.findById(userId);
    }
}
