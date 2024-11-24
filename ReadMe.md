# Newsfeed TeamProject (PoopStagram)
---
## 🛠️ Tools :  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=github&logoColor=Green"> <img alt="Java" src ="https://img.shields.io/badge/Java-007396.svg?&style=for-the-badge&logo=Java&logoColor=white"/>  <img alt="Java" src ="https://img.shields.io/badge/intellijidea-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white"/>
---
## 👨‍💻 Period : 2024/11/19 ~ 2024/11/25
---
## 👨‍💻 ERD
![ERD](https://github.com/user-attachments/assets/36cfa7e8-0c93-4c29-b9c5-820b972dd06f)
---
## 👨‍💻 API명세서
<a-href>https://documenter.getpostman.com/view/39378739/2sAYBUCXcc</a-href>
---
## 👨‍💻 About Project

- ERD를 설계 할때 우선 단방향으로 무조건 설계를 하려고 노력했다. 

회원을 생성하고, 로그인 후, Newsfeed에 대한 기능, comment에 대한 기능, 친구 관계 설정을 할 수 있도록 구현 
회원의 경우 탈퇴 했을때 탈퇴한 아이디로 가입이 되면 안되기 때문에 softDelete를 적용 
---
## 🥵 Trouble Shooting & 🚀 Refactoring
**Memeber** 
1. 로그인 중 passwordEncoder가 bean으로 인식 되지 않아 비밀번호 생성시 인코딩된 문자가 나오는게 아닌, text가 나와 로그인 시 문제가 발생했었음
2. API가 Restful 하게 설계 되지 않은 부분이 있어 수정해야 했다.
3. SOFTDELETE : softDelete 기능을 처음 처리 하다 보니, DB에서 값이 지워지지 않아야 하고, email을 조회했을때 생성시 탈퇴한 계정의 이메일은 조회가 되면 안되었다. 그래서 native query를 넣어서 해결하려고 했지만, 피드백을 받고 난 뒤, existsByEmail로만 조회 하면 충분하다는 feedback을 받고 쿼리 지움 

**Login** 
1. Session 값을 email로 가져갔다. 처음에, 하지만 DB에서 가져올때 email을 가져오는데 시간도 오래걸리고, 보안적인 문제도 얽혀있어 pk 값으로 session 값을 변경함.
2. Login이 되어 있는 상태에서, 로그아웃을 진행하지 않고 로그인시, 별다른 문제 없이 진행 되고 있기에, logout을 진행 후 login을 진행 할 수 있도록 설정했다.
3. HttpSession을 util로 뺐었지만 중복코드에만 집중을 하다보니, util 기능을 하고 있는 역할을 이미 필터에서 하고 있어 controller단에 util 값을 빼야 했음.
4. Login Filter에서 whitelist에 대한 부분 중 "members/{id}" 라는 값을 적용해야 하는데 적용이 되지 않았다. 나중에 알고보니, 동적으로 처리를 해야했다.

**Friend** 
1. 로그인한 세션에 해당 되는지 판단해야 하는데, 처음에 친구 신청을 했을때 로그인 사용자의 아이디가 아닌, 다른사람의 id을 갖고 보낼 수 있었고, 
그래서 해당 사용자에 적합한지 validation을 추가해 검증해야 했다. 
2. 친구 신청을 거절했을때 다시 신청을 할 수 없는 상태였고, DB에서 거절한 상태 값이 다시 변경되지 않는 것을 확인했다. 그래서 Status 값으로, REJECT일 경우 다시 값을 PENDING 상태로 바꿔줄 수 있게헤서 친구 신청을 다시 보낼 수 있었다. 
3. 친구 수락 또한 수락을 받을 사람의 로그인 값이 유지 되어야 하는데, 그게 아닌, 다른 사람의 아이디로 로그인 해도 친구 수락을 받을 수 있는 상태였다. 그래서 로그인한 값이 해당 세션과 동일한지 validation을 추가해 진행.
4. 친구 수락을 받을때 responseId의 값으로 받을 것이라고 생각했지만, 알고 보니 FriendPk 값으로 수락을 받는 상황이었고, 그래서 원인을 찾아보니 JPARepository의 기능중 findById 라는 기능은 pk를 조회해서 pk 값을 가져오는 방식으로 코드가 적용되어 있어, JPARepository에 기능을 추가해 해결했다. 

**GIT**
- merge, pull, push중 혼자 할때와 협업을 할때 충돌의 빈도도 다르고, 충돌시 code가 날라가는 경우도 많았고, 충돌의 빈도가 많아 branch 설계 방식을 시도 했지만, 결국 잘 지켜지지 않은 것 같다. 
- .build, .gradle이 git에 올릴때 들어가면 안되는데 main에 gitIgnore를 제대로 확인하지 못해 위와 같은 기능이 git에 들어가게 되었다.

**JPA**
- JPA 쿼리 중 findBy, deleteBy, save 등과 같은 기본 JPA이외의 JPA 쿼리문을 사용하는데 어려움이 있었습니다. 이때 JPA 공식문서를 참고하면서 문제를 풀어나갈 수 있었습니다.

**RESTFULAPI**
- 같은 GetMapping에서 많은 @RequestParam을 받을 때, 각 Param에 맞게 쿼리문을 주고, 기본값을 주는 과정이 어려웠습니다. 후에 동적쿼리를 이용하여 지금보다 더 쉽게 표기할 수 있으나, 현재는 적절한 기본값을 주어서 구현하였습니다.

**NewsFeedLike**
- 좋아요를 구현하면서 좋아요 테이블과 뉴스피드 테이블사이의 관계에 관하여 고민을 많이 하였습니다. 초기설정과 다르게 뉴스피드에서도 자신의 좋아요 갯수 정보를 가지고 있어야한다는 것을 확인하였고, 연관관계를 개선하였습니다.

---
## 😭 아쉬운점 
- 깃 컨벤션을 어느 블로그의 링크를 걸고 정했으나, 나중에는 코드를 작성하기 급급해 정확히 지켜지지 않아 아쉬웠다. 
- 깃이 모두가 익숙하지 않아 깃에서 시간을 잡아 먹은 부분이 아쉬웠다. 
- 다른 사람의 코드를 보는 눈을 길러야 겠다고 느꼈다. 단순히 물어보는게 아닌 코드를 먼저 읽어보고, 의도를 생각해보는 힘을 길러야 겠다고 느꼈다.
