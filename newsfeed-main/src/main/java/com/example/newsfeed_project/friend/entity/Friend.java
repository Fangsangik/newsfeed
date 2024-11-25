package com.example.newsfeed_project.friend.entity;

import com.example.newsfeed_project.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean friendApproval;

    private String status;

    private LocalDateTime updatedAt;

    // 요청자
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "request_friend_id")
    private Member requestFriend;

    // 수락자
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "response_friend_id")
    private Member responseFriend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 상태 업데이트
    public void approve() {
        this.friendApproval = true;
        this.status = "APPROVED";
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.friendApproval = false;
        this.status = "REJECTED";
    }

    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
}
