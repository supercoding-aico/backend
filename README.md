
# 📡AiCo Project🤝
![아이코 리드미 2](https://github.com/user-attachments/assets/2e9c0a3a-d1ee-415c-bf21-bcf2dc65154b)

<br/><br/>

## 소개 및 개요
- 프로젝트 기간 : 2025.02.24 ~ 2025.03.21
- 인원 : 백엔드 2명, 프론트 2명


### [프로젝트 소개]
- 본 프로젝트는 협업 툴 프로젝트로 개발 일정과 업무를 체계적으로 등록하고 공유할 수 있으며, 실시간 알림과 채팅 기능을 활용해 원활한 커뮤니케이션이 가능합니다.
- 일정을 등록하면 팀원들에게 실시간으로 알림이 전송되며, 일정 관리가 더욱 편리해집니다.
- 팀별 실시간 채팅 기능을 제공하여 원활한 소통을 지원하고, 캘린더를 활용해 등록된 일정을 한눈에 확인하고 공유할 수 있습니다.
- AI를 활용한 회의 및 채팅 요약 기능으로 채팅 내용이나 회의 기록을 입력하면, AI가 이를 자동으로 정리하여 핵심 내용을 요약해줍니다. 이를 통해 팀원들은 중요한 정보만 빠르게 확인할 수 있어 업무 효율성을 높일 수 있습니다.



<details>
  <summary>📌 목차 </summary>

- [1. 팀원 소개](#1-팀원-소개)  
- [2. 기술 스택](#2-기술-스택) 
- [3. System Architecture & ERD](#3-system-architecture--erd)  
- [4. 기능 전략](#4-기능-전략)  
- [5. 트러블 슈팅](#5-트러블-슈팅)  
- [6. Lessons Learned](#6-lessons-learned)    
- [7. 느낀점](#7-느낀점)  

</details>
<br/>



## 1. 팀원 소개

| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;한유진🐰&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 임홍현😺 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | 
| :--------------- | :--------------- | 
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@yj267](https://github.com/yj267) | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@limhhyeon](https://github.com/limhhyeon) 
| &nbsp;&nbsp;&nbsp;&nbsp;🖥️ Backend  | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;🖥️ Backend  | 

<br/>

## 2. 기술 스택
### [사용 기술]
#### **🛠️ Backend**  
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white) ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)  
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)  
- **Spring MVC + REST API** : RESTful API 개발  
- **Spring Security** : 인증 및 권한 관리  
- **SLF4J** : 애플리케이션 로깅  
- **Spring Cache** : DB 리소스를 줄이기 위한 캐시 관리  
- **Spring Schedule** : 스케줄 관리
- **WebSocket + STOMP** : 실시간 알림 및 채팅 

#### **💻 Database & Cache**    
 ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)  
- **비관적 락(Pessimistic Lock)** : 동시성 제어

#### **☁️ DevOps & Deployment**    
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)  
- **AWS EC2** : 클라우드 서버  
- **AWS RDS(MariaDB)** : 클라우드 DB 관리
- **AWS S3** : 파일, 이미지 관리  

#### **📝 Collaboration Tools**  
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)  ![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white) ![Figma](https://img.shields.io/badge/figma-%23F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?style=for-the-badge&logo=discord&logoColor=white) 

### [커밋 컨벤션]
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

```java
feat: 새로운 기능을 추가했을 때
fix: 버그를 수정했을 때
docs: 문서 수정
refactor: 코드 개선했을 때
perf: 성능 최적화할 때
test: 테스트 코드 추가, 수정
build: 빌드 시스템이나 외부 의존성 변경할 때
ci: CI 설정 수정
```
<br/>

## 3. System Architecture & ERD
### [System Architecture]
<img src="https://github.com/user-attachments/assets/b0c6937f-c09c-402a-8cb1-254354f8c402" width="700">  

#### ❗ 채팅 처리  
<img src="https://github.com/user-attachments/assets/57a1148b-d38e-44ec-b157-55b306ac1b33" width="500">


### [ERD]
<img src="https://github.com/user-attachments/assets/e041519f-27c6-4efa-ba22-3d3d200a6949" width="700">

<br/>

## 4. 기능 전략

#### 1. Auth
-  ` JWT 기반 인증 체계 `  :  로그인 시 Access Token과 Refresh Token을 발급하며, Access Token은 HttpOnly, Secure 속성이 적용된 쿠키에 저장해 **XSS 기반 토큰 탈취(Bearer Token Theft)**를 방지  
-  ` CSRF 대응 전략 `  : 쿠키 방식에서 발생할 수 있는 **CSRF(Cross-Site Request Forgery)**에 대비하여 SameSite 속성 설정 및 Origin, Referer 검증 등의 서버 보안정책 적용  
-  ` 안정적인 세션 유지 및 재발급 `  : Access Token 만료 시 Refresh Token 유효성 검사를 통해 자동 재발급하여 사용자 경험 개선  
-  ` 로그아웃 처리 `  : 로그아웃 시 쿠키와 서버 저장 토큰을 모두 삭제하여 세션 완전 종료
-  ` 스프링 캐싱 적용 `  : 로그인 후 유저 정보는 조회 빈도가 높고 변경이 자주 일어나지 않기 때문에 스프링 캐시 서버를 활용해 사용자 프로필 정보를 조회 → DB 접근 최소화
 
#### 2. Team
- ` 팀 멤버 조회 최적화 ` :팀 관련 서비스 로직에서 멤버 정보 조회가 빈번하게 사용되어 시스템 부하를 줄이기 위해, 팀 ID 기반의 멤버 리스트를 Redis 캐시에서 우선 조회하여 캐시가 존재하지 않을 경우에만 DB 조회 후 캐시 저장하는 구조로 성능 최적화  
- ` 팀 탈퇴 기능 ` : 2명의 Manager가 동시에 탈퇴하는 경우를 방지하기 위해 DB Lock 처리로 동시성 제어 구현 (! Manager 권한: 팀원(Member) 강제 탈퇴 가능 / Member 권한: 본인만 탈퇴 가능 단, Manager가 1명만 남아있는 경우에는 탈퇴 불가 처리)  
- ` 팀 생성/수정/삭제 기능 ` : 삭제 시 팀 ID와 인증 토큰 기반으로 권한 검증 수행  
- ` 유저 팀 리스트 조회 ` : 대용량 사용자를 고려한 페이지네이션 적용  
- ` 초대 기반 팀원 등록 ` : 이메일을 통해 팀원 초대 메일 발송 초대 링크 클릭 시 이메일 인증을 통해 팀 토큰과 유저 토큰에 teamId와 동일한지 검증 후 가입  



#### 3. Chat
- ` WebSocket ` : WebSocket을 통해 실시간 송수신 처리
- ` 보안 강화를 위한 접근 제어 ` : WebSocket 연결 시, websocket 핸드쉐이크를 통해 쿠키에 포함된 JWT 토큰을 검증하여 인증되지 않은 사용자의 접근 차단 (비인가 접근 방지) 후 websocket세션에 userId 저장(매번 유저id를 찾을 필요없이 한 번 저장후 세션동안 사용)
- ` WebSocket 연결 상태 관리 ` : WebSocket 연결이 예기치 않게 끊긴 경우를 대비하여 Spring의 EventListener를 활용해 연결 종료 이벤트를 감지 및 처리 → 사용자 접속 상태 관리
- ` 팀 소속 검증 로직 ` : 채팅 메시지를 전송할 때, 해당 사용자가 해당 팀에 소속되어 있는지 검증 하지만 매번 DB 접근 시 성능 저하와 채팅 한번 보낼 때 속도 느려짐 우려가 있으므로 teamId 기반 유저 목록을 Redis에 캐싱하여 빠르게 검증
- ` 채팅 데이터 처리 전략 ` : 채팅 메시지를 Redis에 임시 저장하여 유실 방지 및 db접근 최소화, 일정 시간 간격으로 배치 작업을 통해 DB로 일괄 저장하거나 채팅 리스트를 조회할 때 DB 반영
- ` 채팅 리스트 조회 ` :  팀 Id 기준으로 과거 채팅 내역을 조회 → 시간 역순 기반의 페이지네이션 적용으로 대량 데이터 대응


#### 4. User
- ` 자기 프로필 ` : 토큰 받아서 본인 프로필 조회 (프로필 이미지, 닉네임, 이메일, 전화번호)
- ` 프로필 수정 ` : 본인 프로필 내용 변경
- ` 회원탈퇴 ` : 토큰에 해당하는 User 탈퇴
- ` 이미지 변경 ` : 프로필 이미지 변경
- ` 유저 역할 수정 ` : 팀 역할이 Manager일 경우, 팀원(Member) 역할 수정 가능 / MANAGER 본인 또는 MEMBER는 역할 수정 불가

#### 5. Notification
- ` 알림 안읽음 리스트 ` : 스케줄 등록 및 수정 관련 안읽음 리스트 조회
- ` 알림 읽음 처리 ` : 알림 Id에 해당하는 알림 읽음 처리

#### 6. Schedule
- ` 팀 전체 스케줄 조회 ` : 팀 ID를 통해 팀 스케줄 조회
- ` 스케줄 등록 ` : 팀 ID에 해당하는 팀원들의 스케줄 등록 (BEFORE/WIP/DONE 상태 설정)
- ` 스케줄 수정 ` : 스케줄 ID에 해당하는 스케줄 수정
- ` 스케줄 삭제 ` : 스케줄 ID에 해당하는 스케줄 삭제
- ` 자기 스케줄 조회 ` : 팀 ID에 해당하는 본인의 스케줄 조회

#### 7. AI Meeting
- ` 회의록 정리 ` : 팀 ID에 해당하는 회의록 내용을 받아 OpenAI 답변 받기
- ` 회의록 리스트 ` : 팀 ID에 해당하는 회의록 리스트를 조회하여 페이지네이션 처리
- ` 회의록 삭제 ` : 미팅 ID에 해당하는 회의록 삭제
- ` 회의록 수정 ` : 미팅 ID에 해당하는 회의록 내용 수정

<br/>


## 5. 트러블 슈팅


| 🔴 error                        | 🔵 문제                                                                 | 🟢 해결 방법                                                               |
|---------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------|
| `Filter에서 발생한 예외처리`  | 토큰이 만료되었을 때는 filter에서 예외처리가 발생하는데 해당 예외처리는 ControllerAdvice에서 동작을 하지 않아 클라이언트에게는 500이 발생  | CustomException을 만들어서 Filter에서 예외처리 메시지를 출력하도록 하여 해결                                                    |
| `webSocket https시 접속 불가능한 error`  | https로 배포시 webSocket 연결이 안 되는 문제  | Nginx 파일에 webSocket에 해당하는 url도 proxyPass로 추가해주어 해결                                  |
| `webSocket 연결이 아무나 가능한 error`  | webSocket연결 시 회원이 아니어도 접속이 가능한 문제  | webSocket 연결 전에 쿠키 안에 있는 토큰을 검증하고 websocket 연결할 수 있는 handShake추가                                  |
| `webSocket을 통한 DB 전송 시 db부하`  | webSocket을 통한 전송은 매우 가벼우므로 채팅 같은데 유용하다 그래서 메시지 하나당 db를 타는 것은 일부 메시지는손실되는 문제  | 메시지를 키 값으로 redis에 저장하여 @Schedule을 통해 일정 시간만큼 한 번에 저장하거나 채팅 리스트 불러올 때 redis에 있는 메시지 저장                                  |
| `S3 서버 client에서 사용시 denied error 발생`  | client가 S3 이미지 접근 시도 시 접근 불가 문제  | S3서버에서 해당 client 요청에 대한 것은 허용하여 해결                                 |
| `N+1`  | TeamUser를 조회하는 과정에서 유저가 포함되어 있는 Team 수만큼 select 진행 | TeamUser 조회할 때 항상 Team이 필요하므로 @EntityGraph를 통해 Team도 한 번에 조회                                                    |
| `saveAll`  | 메시지를 한 번에 저장할 때 메시지 개수만큼 insert문이 실행되는 문제  | saveAll은 save의 for문 동작만 생략한 것이지 내부적으로 for문을 돌고 있다는 것을 알게되었다. 그래서 jdbcTemplate를 사용해 Batch개수만큼 한 번에 insert하는 걸로 바꾸었다. 
| `동시성 문제 발생`  | 팀 탈퇴 시 역할이 Manger이면서 Manger 한 명일 때는 본인 탈퇴가 불가능하다 하지만 두명의 Manager가 동시에 탈퇴를 누르면 탈퇴가 진헹되는 문제가 있다.                  | Select해올 때 persimisitc Lock을 사용하여 다음 요청은 이전 요청이 끝나면 들어오도록 하여 해결 |
| `  | 팀당 참여인원이 10명으로 제한이므로 9명일 때 동시에 초대를 하면 초대가 가능한 문제 발생                 | 멤버를 조회할 때 persimisitc Lock을 사용하여 해결 |
| `Cors에러`        | 백엔드 배포 주소가 https여서 프론트가 로컬에서 http로 접속 시 에러 발생             | corsConfig에 프론트 로컬 주소도 허용하여 해결 | 


<br/>

## 5-1 주요 트러블 슈팅
### 1) 동시성  
#### - 한 파티의 2명의 매니저가 동시에 탈퇴 시도 시(manager가 한명일 경우에는 본인이 팀 탈퇴가 불가능함) 

#### *Lock 적용 전*  
```
        List<TeamUser> findByTeamAndTeamRole(Team team, TeamRole role);
```   
![아이코 동시성 실패 리드미](https://github.com/user-attachments/assets/2632f3d4-8e8b-4557-bce8-a3684ee2cb7a)  


#### *Lock 적용 후*  
```
    @Override
    public List<TeamUser> findByTeamAndRoleWithLockDsl(Team team, TeamRole role) {
        QTeamUser teamUser = QTeamUser.teamUser;

        return jpaQueryFactory.selectFrom(teamUser)
                .where(teamUser.team.eq(team)
                        .and(teamUser.teamRole.eq(role)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) 
                .fetch(); 
    }
```    
![아이코 동시성 성공 리드미](https://github.com/user-attachments/assets/98dc16e4-46fa-4c0e-ab26-0257429fe3d8)  


<br/>

## 6. Lessons Learned


### Lessons

### *Redis를 활용한 성능 최적화*  

프로젝트를 진행하면서 **웹소켓 기반 실시간 채팅**과 **데이터 조회 성능 최적화**를 위해 **Redis**를 적용하는 방법을 배웠습니다.  
이를 통해 DB 부하를 줄이고 빠른 데이터 처리를 가능하게 했습니다.  

#### 배운 점:  

##### 1. Redis의 역할  
- **메모리 기반 저장소**로, 빠른 읽기/쓰기 성능을 제공  
- **Key-Value 구조**로 데이터를 저장하며, 캐싱, 세션 관리, 실시간 데이터 저장 등에 활용  
- **웹소켓 기반 채팅**에서 DB에 직접 저장하는 대신 Redis에 저장하여 빠르게 처리하고, 일정 조건에 따라 배치 저장  

##### 2. 설정 방법  
- `채팅 메시지 저장`  
  - 채팅이 오갈 때마다 DB에 바로 저장하지 않고, Redis에 우선 저장  
  - 이후 특정 스케줄(예: 일정 시간이 지나거나 채팅 리스트 불러올 때) DB에 일괄 저장  
- `데이터 조회 최적화`  
  - 매번 DB에서 유저 데이터를 가져오는 대신, Redis에 저장해두고 빠르게 조회  
  - Redis에 데이터가 없을 경우(DB 조회 필요) → DB에서 가져온 후 Redis에 캐싱  
- `활용한 자료구조`  
  - `Hash`: 유저 정보 캐싱  
  - `List`: 채팅 메시지 저장 및 관리  

##### 3. 적용 후 개선점  
- **빠른 응답 속도** → 메모리 기반으로 작동하여 실시간 채팅에서도 지연 없이 데이터 처리 가능  
- **DB 부하 감소** → 모든 요청이 DB를 거치지 않고, Redis를 활용하여 캐싱된 데이터를 먼저 조회  
- **효율적인 데이터 관리** → 필요할 때만 DB와 동기화하여 데이터 일관성을 유지  

Redis를 도입함으로써 **웹소켓 기반 실시간 채팅**과 **데이터 조회 성능**을 최적화할 수 있었습니다.  
이를 통해 빠르고 효율적인 서비스 운영이 가능해졌습니다. 🚀  


### *QueryDSL을 활용한 쿼리 최적화*  

프로젝트를 진행하면서 **JPQL의 오타 가능성과 유지보수 어려움**을 해결하기 위해 **QueryDSL**을 적용하는 방법을 배웠습니다.  
이를 통해 **타입 안정성**을 확보하고, 복잡한 쿼리를 더 직관적으로 작성할 수 있었습니다.  

#### 배운 점:  

##### 1. QueryDSL의 역할  
- JPQL은 **문자열 기반**이라 오타 발생 가능성이 높고, **컴파일 시 오류를 확인할 수 없음**  
- QueryDSL은 **타입 안전성**을 보장하여 컴파일 단계에서 오류를 확인할 수 있음  
- 복잡한 **동적 쿼리**를 더욱 가독성 높고 유지보수하기 쉽게 작성 가능  

##### 2. 설정 방법  
- Gradle에서 QueryDSL을 사용하기 위해 의존성을 추가  
- `./gradlew compileQuerydsl` 실행하여 `Q` 클래스를 자동 생성  
- 기존 JPQL은 문자열로 작성되지만, QueryDSL을 사용하면 **메서드 체이닝 방식으로 가독성이 향상**  

##### 3. 적용 후 개선점  
- **타입 안정성 보장** → 컴파일 타임에서 오류 감지 가능  
- **가독성 및 유지보수성 향상** → 쿼리 수정이 직관적이며, 문자열 오타 문제 해결  
- **동적 쿼리 작성이 쉬움** → BooleanBuilder 등을 활용하여 유연한 조건 추가 가능  
- **JPQL보다 안전하고 강력한 쿼리 작성 가능**  

QueryDSL을 도입함으로써 **보다 안전하고 유지보수하기 쉬운 쿼리**를 작성할 수 있었습니다. 🚀 

### *웹소켓 핸드쉐이크를 통한 인증 처리*  
프로젝트를 진행하면서 웹소켓 기반 실시간 기능을 제공하기 위해, 웹소켓 핸드쉐이크 과정에서 JWT 토큰 인증을 적용하는 방법을 배웠습니다.
이를 통해 웹소켓 연결 시 인증을 처리하고, 인증된 유저만 웹소켓에 접근할 수 있도록 보안을 강화할 수 있었습니다.

#### 배운 점:
##### 1. 웹소켓 핸드쉐이크의 역할
- 웹소켓 핸드쉐이크는 클라이언트와 서버 간의 연결을 설정하는 과정
- JWT 토큰을 활용하여 클라이언트의 인증 정보를 확인하고, 유효한 토큰을 가진 유저만 연결을 허용
- 핸드쉐이크 시, beforeHandshake 메서드에서 쿠키를 통해 토큰을 가져오고, 유효성 검사를 수행하여 인증된 사용자만 접근 가능
##### 2. 설정 방법
- JwtHandshakeInterceptor 클래스에서 beforeHandshake 메서드 구현
- 토큰을 쿠키에서 가져와 검증 → 유효한 토큰일 경우 유저 정보를 attributes에 추가하여 인증 처리
**HttpServletRequest**에서 쿠키를 가져와 Authorization 헤더에 있는 토큰 값을 확인
- 토큰이 유효하지 않으면 **HttpStatus.UNAUTHORIZED**로 응답을 보내 연결을 차단
##### 3. 적용 후 개선점
- 웹소켓 연결 시 인증 보장 → 유효한 JWT 토큰을 가진 유저만 접근 가능
- 보안 강화 → 웹소켓을 통한 비인가된 접근 차단
- 효율적인 사용자 인증 처리 → 매번 웹소켓 연결 시마다 토큰을 검증하여 보안을 유지
- 직관적인 코드 구현 → 웹소켓 연결 핸들러에서 인증 로직을 명확하게 처리

웹소켓 핸드쉐이크 과정에서 JWT 토큰 인증을 적용함으로써 웹소켓 연결 보안을 강화하고, 인증된 유저만 접근할 수 있는 시스템을 구현할 수 있었습니다. 🚀

### Learned  

- **Redis를 활용하여 성능을 최적화**하면서, 데이터 저장 전략을 보다 효율적으로 설계하는 방법을 배웠다.  
- **실시간 채팅과 같은 빠른 데이터 처리가 필요한 경우**, 직접 DB에 저장하는 것보다 **캐싱을 활용하는 것이 효과적**임을 경험했다.  
- **QueryDSL을 사용하면서** JPQL보다 유지보수가 훨씬 편리하고, **동적 쿼리를 쉽게 작성할 수 있음을 실감**했다.  
- **동시성 문제를 해결하기 위해 비관적 락(Pessimistic Lock)**을 적용하는 과정에서, 성능과 안정성 사이의 균형을 고려해야 함을 깨달았다.  
- 프로젝트를 진행하면서 **데이터 일관성, 성능, 유지보수성**을 모두 고려하는 것이 중요함을 배웠다.  
- 웹소켓 연결 끊김 처리: 유저가 채팅방에서 나간 시간 외에도 예기치 않게 웹소켓이 끊겼을 때 유저의 읽은 시간을 처리하기 위해 @EventListener를 활용하여 세션 종료 시 유저의 읽은 시간을 자동으로 처리하도록 구현함으로써, 데이터 일관성을 유지할 수 있었다.



<br/>


## 7. 느낀점
- 단순히 많은 기능을 구현하는 것보다, 하나의 기능이라도 예외 처리, 알고리즘 효율화, DB 최적화, 부하 분산 등의 요소를 충분히 고려해 완성도 있게 만드는 것이 개발자로서 중요하다는 것을 느꼈다.
- 보안에 있어서도 JWT 토큰 보관 방식, CSRF 대응, WebSocket 접근 제어 등 사용자 데이터 보호와 신뢰를 위한 보안 설계의 중요성을 체감했다. 단순 구현이 아닌, 실사용 시 발생할 수 있는 공격과 위협을 실제로 고려하고 방어하는 설계를 고민했다.
- WebSocket 연결 해제나 동시성 문제 같은 예외 상황에서도 시스템이 안정적으로 동작할 수 있도록 설계하는 과정에서, 단순 기능 구현보다 시스템의 신뢰성과 유지보수성이 더 중요한 요소라는 점을 알게 되었다.

