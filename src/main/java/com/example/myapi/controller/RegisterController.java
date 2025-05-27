package com.example.myapi.controller;

import com.example.myapi.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public String sendMail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String code = generateCode();
        try {
            emailService.sendVerificationEmail(to, code); // ✅ 인스턴스 방식 호출
            return "이메일 인증 성공!";
        } catch (Exception e) {
            return "전송 실패: " + e.getMessage();
        }
    }

    @PostMapping("/verify-email")
    public String verifyCode(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String code = request.get("code");
        boolean result = emailService.verifyCode(to, code); // ✅ 인스턴스 방식 호출
        return result ? "인증 성공!" : "인증 실패!";
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
