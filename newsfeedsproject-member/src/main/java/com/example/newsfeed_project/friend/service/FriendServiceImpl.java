package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.entity.Friend;
import com.example.newsfeed_project.friend.exception.NotFoundEntityException;
import com.example.newsfeed_project.friend.repository.FriendRepository;
import com.example.newsfeed_project.member.entity.Member;
import com.example.newsfeed_project.member.repository.MemberRepository;

import static com.example.newsfeed_project.friend.exception.ExceptionCode.*;


public class FriendServiceImpl {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    public FriendServiceImpl(MemberRepository memberRepository, FriendRepository friendRepository) {
        this.memberRepository = memberRepository;
        this.friendRepository = friendRepository;
    }
    // 친구 추가하기
    public void addFriend(Member member, String friendEmail) {
        // 친구 요청 받을 친구 조회
        Member responsefriend = memberRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_MEMBER));
        // 기존 친구인지 확인
        Friend friend = findByMember(responsefriend);
        // 나에게 친구 요청했는지 확인
        //친구 요청하기
        friend = Friend.builder()
                .member(member)
                .friendApproval(false)
                .build();
        friendRepository.save(friend);
    }
    // 기존 친구인지 확인
    private Friend findByMember(Member member) {
        Friend friend = friendRepository.findByMember(member);

        return friend;
    }
    // 친구 삭제하기
    public void deleteFriend(Member member, String friendEmail){
        Member friend = memberRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_MEMBER));

        Friend deletedfriend = findByMember(member);

        friendRepository.delete(deletedfriend);

    }
    /*
    // 친구 목록 조회
    public Page<FriendDto> listFriends(Friend friend, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Friend> friendList = friendRepository.findByApprovalAndResponseMember(true, friend, pageable);
        return friendList.map(FriendDto::new);

    }
    // 친구 수락하기
    public void acceptFriend() {

    }
     */
}
