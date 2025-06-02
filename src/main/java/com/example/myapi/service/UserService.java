package com.example.myapi.service;

import com.example.myapi.dto.*;
import com.example.myapi.entity.User;
import com.example.myapi.exception.UserNotFoundException;
import com.example.myapi.repository.UserRepository;
import com.example.myapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 회원가입
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
                request.getPassword() // 실제 운영 환경에서는 비밀번호 암호화 필요 (예: BCrypt)
        );
        userRepository.save(user);

        return new SignupResponse(user.getNickname(), "회원가입 성공", user.getId());
    }

    // 로그인 (JWT 토큰 생성 포함)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 또는 비밀번호"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호");
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), user.getNickname(), token, "로그인 성공");
    }

    // 사용자 본인 정보 조회
    public UserInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        return new UserInfoResponse(user);
    }


}

