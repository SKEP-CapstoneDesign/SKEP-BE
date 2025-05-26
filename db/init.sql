CREATE DATABASE IF NOT EXISTS SKEP_db;
USE SKEP_db;

-- 사용자 테이블
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       password VARCHAR(255) NOT NULL,
                       nickname VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE
);

-- 사용자시간표
CREATE TABLE user_schedules (
                                schedule_id INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                day_of_week VARCHAR(20) NOT NULL,
                                start_time TIME NOT NULL,
                                end_time TIME NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 그룹 테이블
CREATE TABLE `groups` (
                          group_id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          course VARCHAR(100) NOT NULL
);

-- 그룹 멤버
CREATE TABLE group_members (
                               group_id INT NOT NULL,
                               user_id INT NOT NULL,
                               PRIMARY KEY (group_id, user_id),
                               FOREIGN KEY (group_id) REFERENCES `groups`(group_id),
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 그룹 시간표
CREATE TABLE group_schedules (
                                 group_schedule_id INT AUTO_INCREMENT PRIMARY KEY,
                                 group_id INT NOT NULL,
                                 day_of_week VARCHAR(20) NOT NULL,
                                 start_time TIME NOT NULL,
                                 end_time TIME NOT NULL,
                                 FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
);

-- 그룹 업무
CREATE TABLE group_tasks (
                             task_id INT AUTO_INCREMENT PRIMARY KEY,
                             assigned_user_id INT NOT NULL,
                             group_id INT NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             due_date DATE,
                             FOREIGN KEY (assigned_user_id) REFERENCES users(user_id),
                             FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
);

-- 강의실 시간표
CREATE TABLE meeting_room_schedules (
                                        meeting_schedule_id INT AUTO_INCREMENT PRIMARY KEY,
                                        group_id INT NOT NULL,
                                        room_id INT NOT NULL,
                                        day_of_week VARCHAR(20) NOT NULL,
                                        start_time TIME NOT NULL,
                                        end_time TIME NOT NULL,
                                        FOREIGN KEY (group_id) REFERENCES `groups`(group_id)
);
