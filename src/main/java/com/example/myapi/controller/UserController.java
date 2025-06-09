package com.example.myapi.controller;

import com.example.myapi.dto.NicknameUpdateRequest;
import com.example.myapi.dto.PasswordUpdateRequest;
import com.example.myapi.dto.UserInfoResponse;
import com.example.myapi.exception.UnauthorizedException;
import com.example.myapi.security.UserDetailsImpl;
import com.example.myapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // ✅ 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인 하지 않은 사용자입니다.");
        }

        UserInfoResponse response = userService.getMyInfo(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // ✅ 닉네임 수정 API
    @PutMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody NicknameUpdateRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", "인증되지 않은 사용자입니다."));
        }

        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request", "message", "유효하지 않은 닉네임이거나 누락됐습니다."));
        }

        try {
            String updatedNickname = userService.updateNickname(userDetails.getUserId(), request.getNickname());
            return ResponseEntity.ok(Map.of(
                    "message", "닉네임 변경에 성공했습니다.",
                    "nickname", updatedNickname
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."));
        }
    }
    // 비밀번호 변경 API
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody PasswordUpdateRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", "현재 비밀번호가 잘못되었거나 인증되지 않은 사용자 요청입니다."));
        }

        if (request.getCurrent_password() == null || request.getNew_password() == null ||
                request.getCurrent_password().isBlank() || request.getNew_password().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request", "message", "유효하지 않은 비밀번호입니다."));
        }

        try {
            userService.updatePassword(userDetails.getUserId(), request.getCurrent_password(), request.getNew_password());
            return ResponseEntity.ok(Map.of("message", "비밀번호 변경에 성공했습니다."));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."));
        }
    }
}
