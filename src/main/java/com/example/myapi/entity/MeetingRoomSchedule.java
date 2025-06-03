package com.example.myapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDate;

@Entity
@Table(name = "meeting_room_schedules")
@Data
public class MeetingRoomSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer meetingScheduleId;

    private Integer groupId;

    private Integer roomId;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

}