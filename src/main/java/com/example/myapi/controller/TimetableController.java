package com.example.myapi.controller;

import com.example.myapi.dto.TimetableRequest;
import com.example.myapi.dto.TimetableResponse;
import com.example.myapi.dto.TimetableListResponse;
import com.example.myapi.security.UserDetailsImpl;
import com.example.myapi.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    // ✅ 시간표 추가 (POST /api/timetable)
    @PostMapping
    public ResponseEntity<TimetableResponse> add(@RequestBody TimetableRequest request) {
        TimetableResponse response = timetableService.addTimetable(request);
        return ResponseEntity.status(201).body(response);
    }

    // ✅ 내 시간표 조회 (GET /api/timetable/me)
    @GetMapping("/me")
    public ResponseEntity<List<TimetableListResponse>> getMyTimetable(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<TimetableListResponse> response = timetableService.getMyTimetable(userId);
        return ResponseEntity.ok(response);
    }
}
