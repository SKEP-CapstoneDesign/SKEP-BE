package com.example.myapi.repository;

import com.example.myapi.entity.MeetingRoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRoomScheduleRepository extends JpaRepository<MeetingRoomSchedule, Integer> {
    List<MeetingRoomSchedule> findByRoomId(Integer roomId);
    List<MeetingRoomSchedule> findByRoomIdAndDayOfWeek(Integer roomId, String dayOfWeek);

    boolean existsByRoomId(Integer roomId);
}