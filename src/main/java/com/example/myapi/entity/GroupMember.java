package com.example.myapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(GroupMemberId.class)
public class GroupMember {
    @Id
    private Long groupId;

    @Id
    private Long userId;

    public Long getUserId() {
        return this.userId;
    }

    public Long getGroupId() {
        return this.groupId;
    }

}