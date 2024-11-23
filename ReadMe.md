Newsfeed TeamProject
---
---
Tools : java, spring, mysql
---
---
Period : 2024/11/19 ~ 2024/11/25
---
---
ERD
---
---

API명세서
---
---
https://documenter.getpostman.com/view/39378739/2sAYBUCXcc

About Project
---
---
Comment
- Comment
    - Comment에 필요한 필드값
  

- CommentLike
    - CommentLike에 필요한 필드값
  

- CommentRequstDto
    - Dto & Validation
  

- CommentResponsDto
    - Dto & Validation
  

- CommentLikeResponseDto
    - 좋아요 상태에 대한 Message 반환
  

- UpdateCommentResponseDto
    - update한 contents 및 updateAt 반환 
  

- CommentRepository
    - JPA를 상속받아 구현


- CommentLikeRepository
    - JPA를 상속받아 구현


- CommentService
    - CRUD 구현 


- CommentLikeService
    - 좋아요 상태 on/off 구현


Trouble Shooting
---
---
1. git repository를 생성 시에 gitignore이 존재하지 않아 올라가면 안될 시 여러 파일들이 main에 생성되었고 main에서 다른 브랜치로 체크아웃 시
   2The following untracked working tree files would be overwritten by checkout와 같은 에러가 발생하였고 해결하기 위해 git clean -d -f -f를 터미널에서 입력 후에 다른 branch로 checkout을 실행하였습니다. 잘 실행되었고 git add -A -> git stash -> git pull 과 같은 방법으로 잘 해결되지 않아 위와 같은 방법으로 적용하였습니다.


2. 댓글 수정 및 삭제 시 게시글 작성자, 댓글 작성자만 가능하게 하는 부분에서 댓글을 삭제하려는 사용자의 comment.memberId, newsfeed.memberId를 가져와 처리하는데 만약 사용자의 comment, newsfeed가 모두 없을 시  Nullexception이 발생하였고 if( comment != null && comment != null)로 처리하여 해결하였고 만약 댓글 삭제시 댓글에 대한 좋아요가 존재할 시 if(commentLikeRepository.findByCommentId(commentId) != null)로 검사하여 같이 삭제할 수 있도록 하였습니다.


## Newsfeed package
* JPA 쿼리 중 findBy, deleteBy, save 등과 같은 기본 JPA이외의 JPA 쿼리문을 사용하는데 어려움이 있었습니다.
* * 이때 JAP 공식문서를 참고하면서 문제를 풀어나갈 수 있었습니다.

* 많은 사람들이 어려워했을 부분이라고 생각하지만 협업에 있어서 GIT HUB를 이용하여 협업을 하는 과정이 어려웠습니다. 특히 아직 GIT HUB의 이해가 부족하고, 익숙하지 않아서 협업에서 어려움이 있었습니다.
* * 많은 시행착오를 격어보고 의논하면서 극복 할 수 있었습니다.

* 같은 GetMapping에서 많은 @RequestParam을 받을 때, 각 Param에 맞게 쿼리문을 주고, 기본값을 주는 과정이 어려웠습니다.
* * 후에 동적쿼리를 이용하여 지금보다 더 쉽게 표기할 수 있으나, 현재는 적절한 기본값을 주어서 구현하였습니다.

* 좋아요를 구현하면서 좋아요 테이블과 뉴스피드 테이블사이의 관계에 관하여 고민을 많이 하였습니다.
* * 초기설정과 다르게 뉴스피드에서도 자신의 좋아요 갯수 정보를 가지고 있어야한다는 것을 확인하였고, 연관관계를 개선하였습니다.

Refactor
---
