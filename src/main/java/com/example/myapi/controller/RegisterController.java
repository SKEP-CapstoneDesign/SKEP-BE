package com.example.myapi.controller;

import com.example.myapi.service.EmailService;
import com.example.myapi.dto.*;
import com.example.myapi.entity.User;
import com.example.myapi.repository.UserRepository;
import com.example.myapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final EmailService emailService;
    private final UserService userService;
    private final UserRepository userRepository;

    // ✅ 이메일 인증 코드 전송
    @PostMapping("/send-email")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String code = generateCode();
        try {
            emailService.sendVerificationEmail(to, code);
            return ResponseEntity.ok("이메일 인증 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("전송 실패: " + e.getMessage());
        }
    }

    // ✅ 이메일 인증 코드 검증
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String code = request.get("code");
        boolean result = emailService.verifyCode(to, code);
        return ResponseEntity.ok(result ? "인증 성공!" : "인증 실패!");
    }

    // ✅ 인증 코드 생성
    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // ✅ 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse response = userService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Conflict", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "예기치 못한 예외가 발생했습니다."));
        }
    }

    // ✅ 로그인 처리 (JWT 발급 포함)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

}
