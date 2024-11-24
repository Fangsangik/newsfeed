package com.example.newsfeed_project.friend.controller;

import com.example.newsfeed_project.friend.dto.AcceptFriendDto;
import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.service.FriendService;
import com.example.newsfeed_project.newsfeed.dto.NewsfeedResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort.Direction;


@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }


    //친구 리스트 조회
    @GetMapping("/")
    public ResponseEntity<?> getFriendList(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        Page<FriendDto> response = friendService.getApprovedFriendList(page, size, loggedInUserId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //친구 목록의 친구들 뉴스피드 리스트 조회
    @GetMapping("/newsfeed")
    public ResponseEntity<?> getFriendNewsfeed(
            @RequestParam(required = false, defaultValue = "false") boolean isLike,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Direction.DESC)
            Pageable pageable, HttpSession session)
    {
        Long loggedInUserId = (Long) session.getAttribute("id");

        // 친구 목록에 해당하는 뉴스피드 조회
        Page<NewsfeedResponseDto> response = friendService.getFriendsNewsfeed(loggedInUserId, isLike, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 친구 요청 생성
    @PostMapping("/")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendDto friendDto, HttpSession session) {

        Long loggedInUserId = (Long) session.getAttribute("id");
        friendService.sendFriendRequest(friendDto, loggedInUserId);
        return ResponseEntity.ok("친구 요청이 성공적으로 보내졌습니다.");
    }

    // 친구 요청 승인/거부 API
    @PatchMapping("/accept") //requestBody에 -> 따로 뽑아서 보내주게 설정
    public ResponseEntity<String> acceptFriendRequest(
            @RequestBody AcceptFriendDto acceptFriendDto,
            HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");
        // 서비스 레이어 호출: 요청 처리
        friendService.acceptFriendRequest(acceptFriendDto.getRequestId(), acceptFriendDto.isAccepted(), loggedInUserId);
        // 처리 결과 메시지 생성
        String responseMessage = acceptFriendDto.isAccepted() ? "친구 요청이 수락되었습니다." : "친구 요청이 거절되었습니다.";
        return ResponseEntity.ok(responseMessage);
    }

    // 친구 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteFriend(
            @PathVariable Long requestId,
            @RequestParam Long responseId,
            HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("id");

        try {
            // 서비스 호출
            friendService.deleteFriendByResponseId(requestId, responseId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("친구 관계가 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404 Not Found
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("친구 관계 삭제 중 문제가 발생했습니다.");
        }
    }
}