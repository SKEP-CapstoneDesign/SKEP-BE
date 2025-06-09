package com.example.myapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimetableResponse {
    private String message;

    @JsonProperty("schedule_id")
    private Long scheduleId;
}
