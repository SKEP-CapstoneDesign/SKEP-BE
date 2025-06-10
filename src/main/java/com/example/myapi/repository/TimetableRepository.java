package com.example.myapi.repository;

import com.example.myapi.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // 기존: 요일 기준 중복 체크용
    List<Timetable> findByUserIdAndDayOfWeek(Long userId, String dayOfWeek);

    //  추가: 사용자 ID로 전체 시간표 조회용 (이번 조회 API용)
    List<Timetable> findByUserId(Long userId);
}

