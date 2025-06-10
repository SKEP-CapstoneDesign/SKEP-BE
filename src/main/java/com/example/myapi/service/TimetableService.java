package com.example.myapi.service;

import com.example.myapi.dto.TimetableRequest;
import com.example.myapi.dto.TimetableResponse;
import com.example.myapi.dto.TimetableListResponse;
import com.example.myapi.entity.Timetable;
import com.example.myapi.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;

    // ✅ 시간표 추가
    public TimetableResponse addTimetable(TimetableRequest request) {
        // 1. 유효성 검사
        if (request.getUserId() == null || request.getDayOfWeek() == null ||
                request.getStartTime() == null || request.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "입력값이 누락되었거나 형식이 잘못되었습니다");
        }

        // 2. 겹치는 시간 확인
        List<Timetable> existing = timetableRepository.findByUserIdAndDayOfWeek(
                request.getUserId(), request.getDayOfWeek());

        for (Timetable t : existing) {
            if (!(request.getEndTime().compareTo(t.getStartTime()) <= 0 ||
                    request.getStartTime().compareTo(t.getEndTime()) >= 0)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "해당 시간에 이미 등록된 수업이 존재합니다");
            }
        }

        // 3. 저장
        Timetable timetable = new Timetable(
                request.getUserId(),
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime()
        );

        Timetable saved = timetableRepository.save(timetable);
        return new TimetableResponse("시간표가 성공적으로 추가되었습니다", saved.getId());
    }

    // ✅ 내 시간표 목록 조회
    public List<TimetableListResponse> getMyTimetable(Long userId) {
        List<Timetable> list = timetableRepository.findByUserId(userId);

        return list.stream()
                .map(t -> new TimetableListResponse(
                        t.getId(),
                        t.getDayOfWeek(),
                        t.getStartTime(),
                        t.getEndTime()
                ))
                .toList();
    }
}
