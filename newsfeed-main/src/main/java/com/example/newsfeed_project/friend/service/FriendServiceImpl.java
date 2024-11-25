package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.config.PasswordEncoder;
import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.friend.repository.FriendRepository;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedLikeRepository;
import com.example.newsfeed_project.newsfeed.repository.NewsfeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final NewsfeedLikeRepository newsfeedLikeRepository;
    private final NewsfeedRepository newsfeedRepository;

    public FriendServiceImpl(FriendRepository friendRepository, MemberRepository memberRepository, NewsfeedRepository newsfeedRepository, PasswordEncoder passwordEncoder, NewsfeedLikeRepository newsfeedLikeRepository) {
        this.friendRepository = friendRepository;
        this.memberRepository = memberRepository;
        this.newsfeedRepository = newsfeedRepository;
        this.passwordEncoder = passwordEncoder;
        this.newsfeedLikeRepository = newsfeedLikeRepository;
    }

    @Override
    @Transactional
    public void sendFriendRequest(FriendDto friendDto, String loggedInUserEmail) {
        Member requestFriend = memberRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));
        Member responseFriend = memberRepository.findById(friendDto.getResponseFriendId())
                .orElseThrow(() -> new IllegalArgumentException("응답 사용자를 찾을 수 없습니다."));

        if (requestFriend.getId().equals(responseFriend.getId())) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        Optional<Friend> existingRequest = friendRepository.findByRequestFriendAndResponseFriend(requestFriend, responseFriend);

        if (existingRequest.isPresent()) {
            Friend friend = existingRequest.get();
            handleExistingRequest(friend);
            return;
        }

        Friend friend = Friend.builder()
                .requestFriend(requestFriend)
                .responseFriend(responseFriend)
                .status("PENDING")
                .build();
        friendRepository.save(friend);

        log.info("새로운 친구 요청 보냄: RequestFriendId={}, ResponseFriendId={}",
                requestFriend.getId(), responseFriend.getId());
    }


    @Transactional
    @Override
    public void acceptFriendRequest(Long requestId, Long responseFriendId, boolean isApproved, String loggedInUserEmail) {
        // 현재 로그인된 사용자를 조회
        Member loggedInUser = memberRepository.findByEmail(loggedInUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));

        // 요청 ID와 응답 친구 ID로 친구 요청을 조회
        Friend friendRequest = friendRepository.findByRequestFriendIdAndResponseFriendId(requestId, responseFriendId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 현재 로그인된 사용자가 요청의 responseFriend인지 확인
        if (!friendRequest.getResponseFriend().getId().equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("이 친구 요청을 승인하거나 거절할 권한이 없습니다.");
        }

        // 승인 또는 거부 처리
        if (isApproved) {
            friendRequest.setStatus("APPROVED");
            friendRequest.setUpdatedAt(LocalDateTime.now());
            friendRepository.save(friendRequest);
            log.info("Friend request approved for requestId={}, responseFriendId={}", requestId, responseFriendId);
        } else {
            friendRequest.setStatus("REJECTED");
            friendRequest.setUpdatedAt(LocalDateTime.now());
            friendRepository.save(friendRequest);
            log.info("Friend request rejected for requestId={}, responseFriendId={}", requestId, responseFriendId);
        }
    }

    @Transactional(readOnly = true)
    public Page<FriendDto> getApprovedFriendList(int page, int size, String email) {
        // 페이지 크기를 10으로 제한
        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 승인된 친구 목록 조회
        Page<Friend> friends = friendRepository.findApprovedFriendsByEmail(email, "APPROVED", pageable);

        // FriendDto 리스트 변환
        List<FriendDto> friendDtos = new ArrayList<>();
        for (Friend friend : friends.getContent()) {
            Member relatedUser = friend.getRequestFriend(); // 기본적으로 요청자 정보 사용
            if (friend.getResponseFriend() != null) {
                relatedUser = friend.getResponseFriend(); // 응답자 정보로 교체
            }

            FriendDto friendDto = FriendDto.builder()
                    .id(friend.getId())
                    .responseFriendId(friend.getResponseFriend().getId())
                    .build();

            friendDtos.add(friendDto);
        }

        // Page 객체 생성
        return new PageImpl<>(friendDtos, pageable, friends.getTotalElements());
    }
    @Transactional(readOnly = true)
    public Page<NewsfeedResponseDto> findFriendsNewsfeed(int page, int size, String email) {
        // 친구 목록 가져오기
        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")); // 게시물은 최신순 정렬
        List<Long> friendIds = friendRepository.findApprovedFriendIdsByEmail(email, "APPROVED");

        // 승인된 친구들의 게시물 조회
        Page<Newsfeed> newsfeeds = newsfeedRepository.findByMemberIdIn(friendIds, pageable);

        // NewsfeedResponseDto로 변환
        List<NewsfeedResponseDto> newsfeedDtos = new ArrayList<>();

        for (Newsfeed newsfeed : newsfeeds.getContent()) {
            long likeCount = newsfeedLikeRepository.countByNewsfeedId(newsfeed.getId());
            NewsfeedResponseDto dto = NewsfeedResponseDto.toDto(newsfeed, likeCount);
            newsfeedDtos.add(dto);
        }
        // Page 객체 반환
        return new PageImpl<>(newsfeedDtos, pageable, newsfeeds.getTotalElements());
    }


    // 친구 삭제
    @Override
    @Transactional
    public void deleteFriendByResponseId(Long requestId, Long responseId) {
        // 친구 관계 조회 (요청 ID와 응답 ID로)
        Friend friend = friendRepository.findByRequestFriendIdAndResponseFriendId(requestId, responseId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 친구 관계 삭제
        friendRepository.delete(friend);
    }


    private void handleExistingRequest(Friend friend) {
        switch (friend.getStatus()) {
            case "REJECTED":
                friend.setStatus("PENDING");
                friend.setUpdatedAt(LocalDateTime.now());
                friendRepository.save(friend);
                log.info("거부된 요청을 새로 보냄: RequestFriendId={}, ResponseFriendId={}",
                        friend.getRequestFriend().getId(), friend.getResponseFriend().getId());
                break;

            case "APPROVED":
                throw new IllegalArgumentException("이미 친구 상태입니다.");
            case "PENDING":
                throw new IllegalArgumentException("이미 친구 요청을 보냈습니다.");
            default:
                throw new IllegalStateException("지원하지 않는 요청 상태입니다.");
        }
    }
}
