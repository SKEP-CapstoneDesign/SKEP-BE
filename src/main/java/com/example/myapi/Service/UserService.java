package com.example.myapi.service;

import com.example.myapi.dto.*;
import com.example.myapi.entity.User;
import com.example.myapi.exception.UnauthorizedException;
import com.example.myapi.exception.UserNotFoundException;
import com.example.myapi.repository.UserRepository;
import com.example.myapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ✅ 회원가입
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
                passwordEncoder.encode(request.getPassword()) // 비밀번호 암호화
        );
        userRepository.save(user);

        return new SignupResponse(user.getNickname(), "회원가입 성공", user.getId());
    }

    // ✅ 로그인
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 이메일 또는 비밀번호"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 이메일 또는 비밀번호");
        }

        String token = jwtUtil.createToken(user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), user.getNickname(), token, "로그인 성공");
    }

    // ✅ 내 정보 조회
    public UserInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        return new UserInfoResponse(user);
    }

    // ✅ 닉네임 변경
    public String updateNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        user.setNickname(newNickname); // 또는 user.updateNickname(...)
        userRepository.save(user);
        return user.getNickname();
    }

    // ✅ 비밀번호 변경
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("현재 비밀번호가 잘못되었거나 인증되지 않은 사용자 요청입니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
