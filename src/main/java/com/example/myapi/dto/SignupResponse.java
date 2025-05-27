package com.example.myapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private String nickname;
    private String message;
    private Long user_id;
}
