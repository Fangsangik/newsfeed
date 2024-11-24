package com.example.newsfeed_project.friend.entity;

import com.example.newsfeed_project.friend.type.FriendStatus;
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
@Table(name = "Friend", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requestId", "responseId"})
})
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //enum 타입
    @Enumerated(value = EnumType.STRING)
    private FriendStatus status;

    private LocalDateTime updatedAt;
    private String image;

    // 요청자
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "request_friend_id")
    private Member requestFriend;

    // 수락자
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "response_friend_id")
    private Member responseFriend;


}