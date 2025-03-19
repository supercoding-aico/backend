
# 📡AiCo Project🤝

<br/><br/>

## 소개 및 개요
- 프로젝트 기간 : 2025.02.24 ~ 2025.03.24


### [프로젝트 소개]
- 본 프로젝트는 협업 툴 프로젝트로 개발 일정과 업무를 체계적으로 등록하고 공유할 수 있으며, 실시간 알림과 채팅 기능을 활용해 원활한 커뮤니케이션이 가능합니다.
- 일정을 등록하면 팀원들에게 실시간으로 알림이 전송되며, 일정 관리가 더욱 편리해집니다.
- 팀별 실시간 채팅 기능을 제공하여 원활한 소통을 지원하고, 캘린더를 활용해 등록된 일정을 한눈에 확인하고 공유할 수 있습니다.
- AI를 활용한 회의 및 채팅 요약 기능으로 채팅 내용이나 회의 기록을 입력하면, AI가 이를 자동으로 정리하여 핵심 내용을 요약해줍니다. 이를 통해 팀원들은 중요한 정보만 빠르게 확인할 수 있어 업무 효율성을 높일 수 있습니다.

<details>
  <summary>📌 목차 </summary>

- [1. 팀원 소개](#1-팀원-소개)  
- [2. 기술 스택](#2-기술-스택)  
- [3. 환경변수](#3-환경변수)  
- [4. ERD](#4-erd)  
- [5. 기능 전략](#5-기능-전략)  
- [6. 트러블 슈팅](#6-트러블-슈팅)  
- [7. Lessons Learned](#7-lessons-learned)  
- [8. Feedback](#8-feedback)  
- [9. 느낀점](#9-느낀점)  

</details>
<br/>


## 1. 팀원 소개


| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;한유진🐰 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 임홍현😺 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | 
| :--------------- | :--------------- | 
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@yj267](https://github.com/yj267) | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[@limhhyeon](https://github.com/limhhyeon) 
| &nbsp;&nbsp;&nbsp;&nbsp;🖥️ Backend  | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;🖥️ Backend  | 

<br/>

## 2. 기술 스택
### [사용 기술]
` Java 17 ` : 최신 기능과 성능 개선을 위해 사용  
` Spring Boot `:  REST API 및 웹 애플리케이션 표준  
` Gradle ` : 프로젝트 관리 및 의존성 관리  
` MriaDB ` : 관계형 DBMS  
` JPA(Hibernate) ` : Java와 DB 간의 객체-관계 매핑을 위해 사용  
` SLF4J `: 애플리케이션 로깅을 위해 사용  
` Spring MVC + REST API ` : RESTful API 개발  
` Spring Security ` : 인증 및 권한 관리  
` Spring Cache ` : DB 리소스를 줄이기 위한 스프링 캐시 생성해서 관리  
` Spring Schedule` : 스케줄 관리 필요하여 사용  
` 비관적 락(Pessimistic Lock) ` : 동시성을 위해  
` AWS EC2 ` : 클라우드 서비스  
` RDS ` : 클라우드 DB 서버  
` Git / GitHub ` : 코드 형상 관리  

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

## 3. 환경변수

`username` : MySql username  
`password` : MySql password  
`secret key` : JWT secretKey
`api key` : OpenAI secretKey  
<br/>

## 4. ERD
![erd](https://github.com/user-attachments/assets/e041519f-27c6-4efa-ba22-3d3d200a6949)

<br/>

## 5. 기능 전략

#### 1. Auth
- ` 로그인 ` :  이메일(아이디), 비밀번호 입력 받아 JWT 및 refresh 토큰 발급 후, HttpOnly 쿠키에 담아서 전달
- ` 회원가입 ` : 닉네임/이메일 중복확인 후 남은 유저 정보를 입력하여 회원가입을 진행
- ` 닉네임 중복확인 ` : 닉네임을 입력 받아 DB에서 존재하는 닉네임이 있는지 확인
- ` 이메일 중복 확인 ` : 이메일을 입력 받아 DB에서 존재하는 이메일이 있는지 확인
- ` 토큰 유효 검증 ` : 토큰 유효기간이 남아있는지 확인
- ` 토큰 재발급 ` : 토큰 유효기간이 만료되었을 시 재발급
- ` 로그아웃 ` : 로그아웃 시 쿠키와 토큰 제거

#### 2. Team
- ` 팀 생성 ` : 협업 툴에 필요한 팀 생성
- ` 팀 수정 ` : 팀에 대한 내용 변경
- ` 팀 삭제 ` : 팀 아이디와 토큰을 통해 팀 삭제
- ` 유저 팀 리스트 조회 ` : 토큰을 통해서 유저의 팀 리스트 조회하여 페이지네이션 처리
- ` 팀 탈퇴 ` : 팀 역할이 Manager일 경우, 팀원(Member) 탈퇴 가능 / Member일 경우, 본인만 탈퇴 가능
- ` 팀 멤버 정보 ` : 팀 아이디를 통해 해당 팀원 정보 조회
- ` 팀 초대 ` : 이메일 전송을 통해 팀원 초대
- ` 팀 가입 ` : 초대 링크의 이메일에 해당하는 팀원 가입

#### 3. User
- ` 자기 프로필 ` : 토큰 받아서 본인 프로필 조회 (프로필 이미지, 닉네임, 이메일, 전화번호)
- ` 프로필 수정 ` : 본인 프로필 내용 변경
- ` 회원탈퇴 ` : 토큰에 해당하는 User 탈퇴
- ` 이미지 변경 ` : 프로필 이미지 변경
- ` 유저 역할 수정 ` : 팀 역할이 Manager일 경우, 팀원(Member) 역할 수정 가능 / MANAGER 본인 또는 MEMBER는 역할 수정 불가

#### 4. Notification
- ` 알림 안읽음 리스트 ` : 스케줄 등록 및 수정 관련 안읽음 리스트 조회
- ` 알림 읽음 처리 ` : 알림 Id에 해당하는 알림 읽음 처리

#### 5. Schedule
- ` 팀 전체 스케줄 조회 ` : 팀 ID를 통해 팀 스케줄 조회
- ` 스케줄 등록 ` : 팀 ID에 해당하는 팀원들의 스케줄 등록 (BEFORE/WIP/DONE 상태 설정)
- ` 스케줄 수정 ` : 스케줄 ID에 해당하는 스케줄 수정
- ` 스케줄 삭제 ` : 스케줄 ID에 해당하는 스케줄 삭제
- ` 자기 스케줄 조회 ` : 팀 ID에 해당하는 본인의 스케줄 조회

#### 6. Chat
- ` 채팅방에 대한 채팅 리스트 조회 ` : 팀 ID에 해당하는 채팅리스트를 조회하여 페이지네이션 처리 (시간 역순)

#### 7. AI Meeting
- ` 회의록 정리 ` : 팀 ID에 해당하는 회의록 내용을 받아 OpenAI 답변 받기
- ` 회의록 리스트 ` : 팀 ID에 해당하는 회의록 리스트를 조회하여 페이지네이션 처리
- ` 회의록 삭제 ` : 미팅 ID에 해당하는 회의록 삭제
- ` 회의록 수정 ` : 미팅 ID에 해당하는 회의록 내용 수정

<br/>


## 6. 트러블 슈팅


| 🔴 error                        | 🔵 문제                                                                 | 🟢 해결 방법                                                               |
|---------------------------------|----------------------------------------------------------------------|--------------------------------------------------------------------------|
| `org.hibernate.MappingException`  | BaseEntity를 상속 받는데 BaseEntity와 필드명이 DB 컬럼명과 일치하지 않아 찾을 수 없다는 에러 발생 | 컬럼 명 일치로 해결 완료                                                     |
| `UnsatisfiedDependencyException`  | 의존성 도입 도중 repository 메소드에 제대로 된 필드 값이 들어오지 않아 발생                  | JpaRepository에 페이지네이션으로 구현했으나 파라미터에 pageable을 넣지 않아 발생했다. pageable을 파라미터로 넣어주어 해결 |
| `SQLSyntaxErrorException`        | JPQL을 쓰던 중 GroupBy절을 쓰는데 group으로 묶은 필드가 들어오지 않아 발생             | JGroupBy에 필요한 필드들을 담아서 해결 | 
| `PropertyValueException`        | PartyUser 엔티티는 Party 필드값이 NotNull인데 파티를 먼저 삭제하고 파티 유저를 삭제하려고 하니 파티 유저의 파티 값이 null로 되어 발생                  | 파티유저를 먼저 삭제 후 파티를 삭제하여 해결 | 
| `N+1`        | @ManyToOne과 @OneToMany 사용 시 조회를 하는 과정에서 한 번의 쿼리문이 아닌 반복적인 쿼리 문 발생                  | 1. 필요한 필드명만 따로 뺀 클래스를 만들어 해결  | 
|        | Fetch Join을 사용하여 해결                  | 파티유저를 먼저 삭제 후 파티를 삭제하여 해결 | 
| `cors(Mixed Content)`        | 백엔드가 배포한 프로젝트에서 프론트 배포 주소 허용 안해서 발생                  | 프론트 요청 주소 허용해주는 config 빈으로 등록 후 SecutiryConfig에 추가하여 해결 | 
| `동시성 문제`        | 파티 등록할 때 여러 사람이 동시에 파티에 참여할 때 참여 가능한 인원수보다 오바되는 경우 발생              | 파티를 조회할 때 낙관적 락을 걸어 처리가 끝난 후에 다음 조회 처리가 가능하도록 하여 해결 |
|         | 한 사람이 여러 파티에 동시에 가입할 때 돈이 한 파티에 관해서만 빠지는 현상 발생                  | 유저의 돈을 업데이트 할 때 db에서 처리하도록 UserRepository에서 update문을 넣어주어 실행하도록 변경하여 해결 |

<br/>

## 7. Lessons Learned


### Lessons

#### *비관적 락 (Pessimistic Lock)*
프로젝트를 진행하면서 상품 수량이나 파티 참여 인원 등의 동시성 문제를 해결하기 위해 **비관적 락(Pessimistic Lock)**을 적용하는 방법을 배웠습니다. 이를 통해 동시에 여러 트랜잭션이 동일한 데이터를 수정하는 충돌을 방지할 수 있었습니다.

배운 점:
##### 1.비관적 락의 역할
- 트랜잭션이 데이터를 조회할 때, 다른 트랜잭션이 해당 데이터를 수정하지 못하도록 잠금(Lock) 설정
즉, 데이터 충돌 가능성을 미리 차단하여 동시성 이슈를 방지
##### 2.설정 방법
-JPA의 @Lock(LockModeType.PESSIMISTIC_WRITE)을 사용하여 테이블 레벨에서 락을 걸 수 있음
- 상품 수량이나 파티 참여 인원을 조정할 때, 트랜잭션이 종료될 때까지 다른 트랜잭션의 접근을 차단
- PESSIMISTIC_READ와 PESSIMISTIC_WRITE의 차이를 이해하고 적절한 방식 선택
###### 3.적용 후 개선점
- 데이터 일관성 보장 → 동시 요청이 많아도 상품 수량이나 파티 참여 인원 데이터가 정확하게 유지됨
- 경쟁 조건 해결 → 동시에 여러 사용자가 접근해도 잘못된 데이터 저장 문제를 방지
- 안전하지만 성능 저하 가능성 → 트랜잭션이 길어지면 데드락(deadlock) 발생 가능성이 있으므로 주의해야 함

#### *@Modifying을 활용한 유저 금액 업데이트*
프로젝트를 진행하면서 비관적 락(Pessimistic Lock)의 성능 비용이 크다는 점을 고려하여, 유저 돈을 업데이트하는 것은 비관적인 락을 사용하는 것이 아닌 다른 방법인 데이터베이스에서 직접 처리하는 방식을 적용했습니다. 이를 위해 JPA의 @Modifying과 @Query를 사용하여 유저의 잔액을 업데이트하는 방법을 배웠습니다.

배운 점:
###### 1. @Modifying을 활용한 직접 쿼리 실행
- @Modifying을 사용하면 JPA가 아닌 DB 레벨에서 바로 데이터를 업데이트할 수 있음
- 엔터티를 조회 후 변경하는 방식 대신, 한 번의 SQL 실행으로 데이터 수정 가능
###### 2. 설정 방법
- Spring Data JPA에서 @Modifying과 @Query를 사용하여 잔액을 바로 감소시키는 SQL 실행
- @Transactional을 함께 적용하여 트랜잭션 내에서 처리되도록 보장
  
```
@Modifying
@Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :userId AND u.balance >= :amount")
int deductBalance(@Param("userId") Long userId, @Param("amount") int amount);
```
- 위 코드를 실행하면, 잔액이 충분한 경우에만 감소하며, 여러 사용자가 동시에 요청해도 충돌이 줄어듦

###### 3. 적용 후 개선점
- 성능 향상 → 비관적 락(Pessimistic Lock) 대신, 데이터베이스가 직접 처리하여 성능 비용을 절감
- 경쟁 조건 해결 → SQL 한 줄로 업데이트하므로 동시성 문제가 줄어듦
- 트랜잭션 충돌 방지 → 기존에 엔터티를 조회하고 수정하는 방식보다 트랜잭션 시간이 짧아져 데드락 위험이 감소

### Learned


<br/>

## 8. Feedback

✔️   
✔️   
✔️   
✔️   

<br/>

## 9. 느낀점


