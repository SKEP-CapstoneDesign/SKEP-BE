package com.example.myapi.controller;

import com.example.myapi.dto.UserInfoResponse;
import com.example.myapi.security.UserDetailsImpl;
import com.example.myapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoResponse response = userService.getMyInfo(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
