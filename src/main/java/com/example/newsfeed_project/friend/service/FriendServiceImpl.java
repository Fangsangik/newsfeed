package com.example.newsfeed_project.friend.service;

import static com.example.newsfeed_project.exception.ErrorCode.ALREADY_FRIEND;
import static com.example.newsfeed_project.exception.ErrorCode.ALREADY_SEND;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_FRIEND_REQUEST;
import static com.example.newsfeed_project.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.newsfeed_project.exception.ErrorCode.NO_AUTHOR_APPROVE;
import static com.example.newsfeed_project.exception.ErrorCode.NO_SESSION;
import static com.example.newsfeed_project.exception.ErrorCode.SELF_FRIEND;
import static com.example.newsfeed_project.exception.ErrorCode.WRONG_REQUEST;

import com.example.newsfeed_project.exception.InternalServerException;
import com.example.newsfeed_project.exception.InvalidInputException;
import com.example.newsfeed_project.exception.NoAuthorizedException;
import com.example.newsfeed_project.exception.NotFoundException;
import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.friend.type.FriendStatus;
import com.example.newsfeed_project.friend.repository.FriendRepository;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;

import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import com.example.newsfeed_project.newsfeed.entity.Newsfeed;
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
    private final NewsfeedRepository newsfeedRepository;

    public FriendServiceImpl(FriendRepository friendRepository, MemberRepository memberRepository, NewsfeedRepository newsfeedRepository) {
        this.friendRepository = friendRepository;
        this.memberRepository = memberRepository;
        this.newsfeedRepository = newsfeedRepository;
    }
    @Override
    @Transactional
    public void sendFriendRequest(FriendDto friendDto, Long loggedInUserId) {

        Member requestFriend = memberRepository.findById(loggedInUserId)
                .orElseThrow(() -> new NoAuthorizedException(NO_SESSION));
        Member responseFriend = memberRepository.findById(friendDto.getResponseFriendId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));

        //여기서 걸리면 서비스가 멈추기 때문에 위로
        if (requestFriend.getId().equals(responseFriend.getId())) {
            throw new InvalidInputException(SELF_FRIEND);
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
                .status(FriendStatus.PENDING)
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
                .orElseThrow(() -> new NoAuthorizedException(NO_SESSION));

        // requestId를 기반으로 requestFriend와 responseFriend로 조회
        Friend friendRequest = friendRepository.findByRequestFriendAndResponseFriend(
                memberRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER)),
                loggedInUser
        ).orElseThrow(() -> new NotFoundException(NOT_FOUND_FRIEND_REQUEST));

        if (!friendRequest.getResponseFriend().getId().equals(loggedInUser.getId())) {
            throw new NoAuthorizedException(NO_AUTHOR_APPROVE);
        }

        // 상태 업데이트 및 저장
        FriendStatus status = isApproved ? FriendStatus.APPROVED : FriendStatus.REJECTED;
        friendRequest.setStatus(status);
        friendRequest.setUpdatedAt(LocalDateTime.now());
        friendRepository.save(friendRequest);

        // 로그 기록
        log.info("Friend request {} for requestId={}, by userId={}",
                isApproved ? FriendStatus.APPROVED : FriendStatus.REJECTED,  requestId, loggedInUserId);
    }

    @Transactional(readOnly = true)
    public Page<FriendDto> getApprovedFriendList(int page, int size, Long loggedInUserId) {
        // 페이지 크기를 10으로 제한
        size = Math.min(size, 10);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 승인된 친구 목록 조회
        Page<Friend> friends = friendRepository.findApprovedFriendsByUserId(loggedInUserId, FriendStatus.APPROVED, pageable);

        // FriendDto 리스트 변환
        List<FriendDto> friendDtos = new ArrayList<>();
        for (Friend friend : friends.getContent()) {
            FriendDto friendDto = FriendDto.builder()

                    //정보를 알수 있게 friendDto가 pk 값이 아니라 다른 정보를 가져오게끔
                    .name(friend.getRequestFriend().getName())
                    .image(friend.getRequestFriend().getImage())
                    .name(friend.getResponseFriend().getName())
                    .image(friend.getResponseFriend().getImage())
                    .responseFriendId(friend.getResponseFriend().getId())
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
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_FRIEND_REQUEST));

        // 친구 관계 삭제
        friendRepository.delete(friend);
    }


    private void handleExistingRequest(Friend friend) {
        switch (friend.getStatus()) {
            case REJECTED:
                friend.setStatus(FriendStatus.PENDING);
                friend.setUpdatedAt(LocalDateTime.now());
                friendRepository.save(friend);
                log.info("거부된 요청을 새로 보냄: RequestFriendId={}, ResponseFriendId={}",
                        friend.getRequestFriend().getId(), friend.getResponseFriend().getId());
                break;

            case APPROVED:
                throw new InvalidInputException(ALREADY_FRIEND);
            case PENDING:
                throw new InvalidInputException(ALREADY_SEND);
            default:
                throw new InternalServerException(WRONG_REQUEST);
        }
    }
    @Transactional(readOnly = true)
    public Page<NewsfeedResponseDto> getFriendsNewsfeed(Long loggedInUserId, boolean isLike, Pageable pageable) {
        pageable = checkSortedByLike(isLike, pageable);
        // 친구 목록 가져오기
        List<Long> friendIds = friendRepository.findApprovedFriendIdsByUserId(loggedInUserId, FriendStatus.APPROVED);
        // 승인된 친구들의 게시물 조회
        Page<Newsfeed> newsfeeds = newsfeedRepository.findByMemberIdIn(friendIds, pageable);

        // NewsfeedResponseDto로 변환
        List<NewsfeedResponseDto> newsfeedDtos = new ArrayList<>();

        // 향상된 for문으로 Newsfeed -> NewsfeedResponseDto 변환
        for (Newsfeed newsfeed : newsfeeds) {
            NewsfeedResponseDto dto = NewsfeedResponseDto.toDto(newsfeed); // 변환 메서드 호출
            newsfeedDtos.add(dto);
        }
        return new PageImpl<>(newsfeedDtos, pageable, newsfeeds.getTotalElements());
    }
    private Pageable checkSortedByLike(boolean isLike, Pageable pageable ) {
        if (isLike) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("likeCount").descending().and(Sort.by("updatedAt").descending()));
        }
        return pageable;
    }
}
