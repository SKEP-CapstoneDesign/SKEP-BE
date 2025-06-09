package com.example.myapi.controller;

import com.example.myapi.entity.MeetingRoomSchedule;
import com.example.myapi.repository.MeetingRoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomScheduleRepository scheduleRepository;
    private static final Set<Integer> VALID_ROOM_IDS = Set.of(411, 414, 416, 417, 419, 420, 424);

    @GetMapping("/{roomId}/timetable")
    public ResponseEntity<?> getRoomTimetable(@PathVariable Integer roomId) {
        if (!VALID_ROOM_IDS.contains(roomId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "유효하지 않은 강의실 ID입니다"
            ));
        }

        try {
            List<MeetingRoomSchedule> schedules = scheduleRepository.findByRoomId(roomId);

            List<Map<String, Object>> responseList = new ArrayList<>();
            for (MeetingRoomSchedule schedule : schedules) {
                Map<String, Object> scheduleMap = new LinkedHashMap<>();
                scheduleMap.put("meeting_schedule_id", schedule.getMeetingScheduleId());
                scheduleMap.put("room_id", schedule.getRoomId());
                scheduleMap.put("day_of_week", schedule.getDayOfWeek());
                scheduleMap.put("start_time", schedule.getStartTime().toString());
                scheduleMap.put("end_time", schedule.getEndTime().toString());
                responseList.add(scheduleMap);
            }

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }

    @GetMapping("/{roomId}/slots")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Integer roomId,
            @RequestParam(value = "date", required = false) String dateStr) {

        if (!VALID_ROOM_IDS.contains(roomId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "유효하지 않은 강의실 ID입니다"
            ));
        }

        LocalDate date;
        try {
            if (dateStr == null || dateStr.isBlank()) {
                throw new IllegalArgumentException();
            }
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid request",
                    "message", "날짜 형식이 잘못되었거나 누락되었습니다 (예: YYYY-MM-DD)"
            ));
        }

        try {
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
            List<MeetingRoomSchedule> reservations = scheduleRepository.findByRoomIdAndDayOfWeek(roomId, dayOfWeek);

            List<MeetingRoomSchedule> sorted = new ArrayList<>(reservations);
            sorted.sort(Comparator.comparing(MeetingRoomSchedule::getStartTime));

            List<Map<String, Object>> availableSlots = new ArrayList<>();
            LocalTime start = LocalTime.of(8, 0);
            LocalTime end = LocalTime.of(22, 0);

            for (MeetingRoomSchedule res : sorted) {
                if (start.isBefore(res.getStartTime())) {
                    availableSlots.add(Map.of(
                            "start_time", start.toString(),
                            "end_time", res.getStartTime().toString()
                    ));
                }
                if (start.isBefore(res.getEndTime())) {
                    start = res.getEndTime();
                }
            }

            if (start.isBefore(end)) {
                availableSlots.add(Map.of(
                        "start_time", start.toString(),
                        "end_time", end.toString()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "room_id", roomId,
                    "date", dateStr,
                    "available_slots", availableSlots
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }

    @PostMapping("/{roomId}/reserve")
    public ResponseEntity<?> reserveRoom(@PathVariable Integer roomId, @RequestBody Map<String, Object> req) {
        try {
            Integer groupId = (Integer) req.get("group_id");
            String dateStr = (String) req.get("date");
            String startTimeStr = (String) req.get("start_time");
            String endTimeStr = (String) req.get("end_time");

            if (!VALID_ROOM_IDS.contains(roomId) || groupId == null || dateStr == null || startTimeStr == null || endTimeStr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid request",
                        "message", "입력값이 누락되었거나 형식이 잘못되었습니다"
                ));
            }

            LocalDate date;
            LocalTime startTime;
            LocalTime endTime;
            try {
                date = LocalDate.parse(dateStr);
                startTime = LocalTime.parse(startTimeStr);
                endTime = LocalTime.parse(endTimeStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid request",
                        "message", "입력값이 누락되었거나 형식이 잘못되었습니다"
                ));
            }

            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();

            List<MeetingRoomSchedule> existingSchedules = scheduleRepository.findByRoomIdAndDayOfWeek(roomId, dayOfWeek);
            for (MeetingRoomSchedule schedule : existingSchedules) {
                if (!(endTime.isBefore(schedule.getStartTime()) || startTime.isAfter(schedule.getEndTime()))) {
                    return ResponseEntity.status(404).body(Map.of(
                            "error", "Conflict",
                            "message", "해당 시간대에 이미 예약이 존재합니다"
                    ));
                }
            }

            MeetingRoomSchedule schedule = new MeetingRoomSchedule();
            schedule.setRoomId(roomId);
            schedule.setGroupId(groupId);
            schedule.setDayOfWeek(dayOfWeek);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);

            scheduleRepository.save(schedule);

            return ResponseEntity.status(201).body(Map.of(
                    "message", "그룹 예약이 성공적으로 완료되었습니다",
                    "reservation_id", schedule.getMeetingScheduleId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }
}
