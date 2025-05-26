package com.example.myapi.repository;

import com.example.myapi.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByNameAndCourse(String name, String course);
}
