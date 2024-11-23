package com.example.newsfeed_project.friend.service;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.entity.Friend;
import org.springframework.data.domain.Page;

public interface FriendService {
    void addFriend(FriendDto friendDto);
    //Page<FriendDto> listFriends(FriendDto frienddto);
    //void acceptFriend(FriendDto friendDto);
    void deleteFriend(FriendDto friendDto);
}
