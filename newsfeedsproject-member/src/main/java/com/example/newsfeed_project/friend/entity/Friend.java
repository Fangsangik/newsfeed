package com.example.newsfeed_project.friend.entity;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean friendApproval;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Friend(Member member) {
        this.member = member;
    }

    public static Friend toEntity(FriendDto friendDto, MemberRepository memberRepository) {

        Member member = memberRepository.findByEmail(friendDto.getMember().getEmail())
                .orElseThrow(() -> new RuntimeException("Member not found with email: " + friendDto.getMember().getEmail()));

        return Friend.builder()
                .id(friendDto.getId())
                .member(member)
                .friendApproval(friendDto.isFriendApproval())
                .build();
    }


}
