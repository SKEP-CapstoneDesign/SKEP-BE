package com.example.myapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimetableRequest {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("day_of_week")
    private String dayOfWeek;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;
}
