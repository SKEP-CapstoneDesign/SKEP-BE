package com.example.myapi.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("이메일 인증 요청");
        helper.setText("<h1>인증 코드</h1><p><b>" + code + "</b></p>", true);

        mailSender.send(message);

        // Redis에 코드 저장 (3분 유효)
        redisTemplate.opsForValue().set(to, code, 3, TimeUnit.MINUTES);
    }

    public boolean verifyCode(String to, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get(to);
        return savedCode != null && savedCode.equals(inputCode);
    }
}
