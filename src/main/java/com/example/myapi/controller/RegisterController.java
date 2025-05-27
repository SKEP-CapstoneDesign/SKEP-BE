package com.example.myapi.controller;

import com.example.myapi.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public String sendMail(@RequestParam String to) {
        String code = generateCode();
        try {
            emailService.sendVerificationEmail(to, code);
            return "이메일 인증 성공!";
        } catch (Exception e) {
            return "전송 실패: " + e.getMessage();
        }
    }

    @PostMapping("/verify-email")
    public String verifyCode(@RequestParam String to, @RequestParam String code) {
        boolean result = emailService.verifyCode(to, code);
        return result ? "인증 성공!" : "인증 실패!";
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
