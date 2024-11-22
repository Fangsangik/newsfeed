package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.friend.repository.FriendRepository;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;

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

    public FriendServiceImpl(FriendRepository friendRepository, MemberRepository memberRepository) {
        this.friendRepository = friendRepository;
        this.memberRepository = memberRepository;

    }

    @Override
    @Transactional
    public void sendFriendRequest(FriendDto friendDto, Long loggedInUserId) {

        Member requestFriend = memberRepository.findById(loggedInUserId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));
        Member responseFriend = memberRepository.findById(friendDto.getResponseFriendId())
                .orElseThrow(() -> new IllegalArgumentException("응답 사용자를 찾을 수 없습니다."));

        //여기서 걸리면 서비스가 멈추기 때문에 위로
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
    public void acceptFriendRequest(Long requestId, boolean isApproved, Long loggedInUserId) {
        // 현재 로그인된 사용자를 조회
        Member loggedInUser = memberRepository.findById(loggedInUserId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 사용자를 찾을 수 없습니다."));

        // 요청 ID로 친구 요청을 조회
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 현재 로그인된 사용자가 요청의 responseFriend인지 확인
        if (!friendRequest.getResponseFriend().getId().equals(loggedInUser.getId())) {
            throw new IllegalArgumentException("이 친구 요청을 승인하거나 거절할 권한이 없습니다.");
        }

        // 상태 업데이트 및 저장
        String status = isApproved ? "APPROVED" : "REJECTED";
        friendRequest.setStatus(status);
        friendRequest.setUpdatedAt(LocalDateTime.now());
        friendRepository.save(friendRequest);

        // 로그 기록
        log.info("Friend request {} for requestId={}, by userId={}",
                isApproved ? "approved" : "rejected", requestId, loggedInUserId);
    }

    @Transactional(readOnly = true)
    public Page<FriendDto> getApprovedFriendList(int page, int size, Long loggedInUserId) {
        // 페이지 크기를 10으로 제한
        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 승인된 친구 목록 조회
        Page<Friend> friends = friendRepository.findApprovedFriendsByUserId(loggedInUserId, "APPROVED", pageable);

        // FriendDto 리스트 변환
        List<FriendDto> friendDtos = new ArrayList<>();
        for (Friend friend : friends.getContent()) {
            FriendDto friendDto = FriendDto.builder()
                    .id(friend.getId())
                    //정보를 알수 있게 friendDto가 pk 값이 아니라 다른 정보를 가져오게끔
                    .name(friend.getRequestFriend().getName())
                    .image(friend.getRequestFriend().getImage())
                    .name(friend.getResponseFriend().getName())
                    .image(friend.getResponseFriend().getImage())
                    .build();

            friendDtos.add(friendDto);
        }

        // Page 객체 생성
        return new PageImpl<>(friendDtos, pageable, friends.getTotalElements());
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
