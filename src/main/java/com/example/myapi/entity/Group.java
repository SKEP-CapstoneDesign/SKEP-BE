package com.example.myapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups_table")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String course;
}