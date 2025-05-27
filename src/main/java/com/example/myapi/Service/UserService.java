package com.example.myapi.service;

import com.example.myapi.dto.SignupRequest;
import com.example.myapi.dto.SignupResponse;
import com.example.myapi.entity.User;
import com.example.myapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SignupResponse signup(SignupRequest request) {
        if (request.getEmail() == null || request.getPassword() == null || request.getNickname() == null) {
            throw new IllegalArgumentException("입력 값이 누락되었습니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        User user = new User(
                request.getEmail(),
                request.getNickname(),
                request.getPassword() // 실제 서비스에서는 BCrypt 등으로 암호화 필수
        );
        userRepository.save(user);

        return new SignupResponse(user.getNickname(), "회원가입 성공", user.getId());
    }
}
