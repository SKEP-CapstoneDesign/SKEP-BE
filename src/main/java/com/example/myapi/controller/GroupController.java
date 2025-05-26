package com.example.myapi.controller;

import com.example.myapi.entity.Group;
import com.example.myapi.entity.GroupMember;
import com.example.myapi.entity.User;
import com.example.myapi.repository.GroupMemberRepository;
import com.example.myapi.repository.GroupRepository;
import com.example.myapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    // 그룹 목록 조회
    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> groups = groupRepository.findAll();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }


    // 그룹 상세 조회
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupDetail(@PathVariable Long groupId) {
        try {
            Optional<Group> group = groupRepository.findById(groupId);
            if (group.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "Not Found",
                        "message", "해당 그룹을 찾을 수 없습니다"
                ));
            }

            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            List<Map<String, Object>> memberInfo = new ArrayList<>();
            for (GroupMember gm : members) {
                userRepository.findById(gm.getUserId()).ifPresent(user -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("user_id", user.getUserId());
                    info.put("email", user.getEmail());
                    info.put("nickname", user.getNickname());
                    memberInfo.add(info);
                });
            }

            return ResponseEntity.ok(Map.of(
                    "group_id", group.get().getGroupId(),
                    "name", group.get().getName(),
                    "course", group.get().getCourse(),
                    "members", memberInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }



    // 그룹 생성
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> req) {
        try {
            String name = (String) req.get("name");
            String course = (String) req.get("course");
            Integer creatorId = (Integer) req.get("creator_user_id");

            if (name == null || name.isBlank() || course == null || course.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid request",
                        "message", "그룹명 또는 과목명이 누락되었거나 형식이 잘못되었습니다"
                ));
            }

            if (groupRepository.existsByNameAndCourse(name, course)) {
                return ResponseEntity.status(409).body(Map.of(
                        "error", "Conflict",
                        "message", "해당 이름의 그룹이 이미 존재합니다"
                ));
            }

            Group group = Group.builder().name(name).course(course).build();
            groupRepository.save(group);

            groupMemberRepository.save(GroupMember.builder()
                    .groupId(group.getGroupId())
                    .userId(creatorId.longValue())
                    .build());

            return ResponseEntity.status(201).body(Map.of(
                    "group_id", group.getGroupId(),
                    "message", "그룹이 성공적으로 생성되었습니다"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }


    @PostMapping("/{groupId}/invite")
    public ResponseEntity<?> inviteUser(@PathVariable Long groupId, @RequestBody Map<String, String> req) {
        try {
            String email = req.get("user_email");

            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid request",
                        "message", "이메일 형식이 잘못되었거나 누락되었습니다"
                ));
            }

            if (groupRepository.findById(groupId).isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "Not Found",
                        "message", "해당 사용자 또는 그룹을 찾을 수 없습니다"
                ));
            }

            Optional<User> user = userRepository.findByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "Not Found",
                        "message", "해당 사용자 또는 그룹을 찾을 수 없습니다"
                ));
            }

            if (groupMemberRepository.existsByGroupIdAndUserId(groupId, user.get().getUserId())) {
                return ResponseEntity.status(409).body(Map.of(
                        "error", "Conflict",
                        "message", "해당 사용자는 이미 그룹에 속해 있습니다"
                ));
            }

            groupMemberRepository.save(GroupMember.builder()
                    .groupId(groupId)
                    .userId(user.get().getUserId())
                    .build());

            return ResponseEntity.ok(Map.of(
                    "message", "사용자가 그룹에 성공적으로 초대되었습니다",
                    "group_id", groupId,
                    "user_id", user.get().getUserId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }


    // 그룹 수정
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId, @RequestBody Group update) {
        try {
            if (update.getName() == null || update.getName().isBlank() ||
                    update.getCourse() == null || update.getCourse().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Invalid request",
                        "message", "그룹명 또는 과목명이 누락되었거나 형식이 잘못되었습니다"
                ));
            }

            return groupRepository.findById(groupId).map(g -> {
                g.setName(update.getName());
                g.setCourse(update.getCourse());
                groupRepository.save(g);
                return ResponseEntity.ok(Map.of(
                        "group_id", g.getGroupId(),
                        "message", "그룹 정보가 성공적으로 수정되었습니다"
                ));
            }).orElse(ResponseEntity.status(404).body(Map.of(
                    "error", "Not Found",
                    "message", "해당 그룹을 찾을 수 없습니다"
            )));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }


    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        try {
            if (groupRepository.findById(groupId).isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "Not Found",
                        "message", "해당 그룹을 찾을 수 없습니다"
                ));
            }

            groupRepository.deleteById(groupId);
            return ResponseEntity.ok(Map.of(
                    "message", "그룹이 성공적으로 삭제되었습니다",
                    "group_id", groupId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal Server Error",
                    "message", "예기치 못한 예외가 발생했습니다. 다시 시도해주세요."
            ));
        }
    }
}
