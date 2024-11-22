package com.example.newsfeed_project.friend.controller;

import com.example.newsfeed_project.friend.dto.FriendDto;
import com.example.newsfeed_project.friend.service.FriendService;
import com.example.newsfeed_project.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }


    //친구 리스트 조회
    @GetMapping("/friendList")
    public ResponseEntity<?> getFriendList(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpSession session) {
        String loggedInUserEmail = SessionUtil.validateSession(session);

        Page<FriendDto> response = friendService.getApprovedFriendList(page, size, loggedInUserEmail);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 친구 요청 생성
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendDto friendDto, HttpSession session) {
        String loggedInUserEmail = SessionUtil.validateSession(session);
        friendService.sendFriendRequest(friendDto, loggedInUserEmail);
        return ResponseEntity.ok("친구 요청이 성공적으로 보내졌습니다.");
    }

    // 친구 요청 승인/거부 API
    @PatchMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(
            @PathVariable Long requestId,
            @RequestParam Long responseId,
            @RequestParam boolean isAccepted,
            HttpSession session) {

        // 세션에서 현재 로그인된 사용자의 이메일 확인
        String loggedInUserEmail = SessionUtil.validateSession(session);

        // 서비스 레이어 호출: 요청 처리
        friendService.acceptFriendRequest(requestId, responseId, isAccepted, loggedInUserEmail);

        // 처리 결과 메시지 생성
        String responseMessage = isAccepted ? "친구 요청이 수락되었습니다." : "친구 요청이 거절되었습니다.";
        return ResponseEntity.ok(responseMessage);
    }

    // 친구 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteFriend(
            @PathVariable Long requestId,
            @RequestParam Long responseId,
            HttpSession session) {

        String loggedInUserEmail = SessionUtil.validateSession(session);
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
