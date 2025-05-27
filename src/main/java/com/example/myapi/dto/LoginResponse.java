package com.example.myapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long user_id;
    private String nickname;
    private String message;
}

