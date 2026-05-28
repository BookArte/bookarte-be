<img width="1792" height="592" alt="Image" src="https://github.com/user-attachments/assets/456ae7b0-9f36-465d-80e9-90ba27a8677d" />
<br/>
<br/>

# 0. 시작하기
### 프론트엔드 프로젝트
```bash
$ npm install
$ npm run dev
```

### 백엔드 프로젝트
```bash
$ ./gradlew build
```

<br/>
<br/>

# 1. 프로젝트 개요
- 프로젝트 이름: BookArte
- 프로젝트 설명: 기술 향상을 위한 도서관 클론 코딩 프로젝트

<br/>
<br/>

# 2. 팀원 소개
| 남규민 | 정석현 |
|:------:|:------:|
| <img src="https://avatars.githubusercontent.com/u/157104659?v=4" alt="남규민" width="150"> |  <img src="https://avatars.githubusercontent.com/u/113079762?v=4" alt="정석현" width="150"> |
| BE,FE | BE,FE |
| [GitHub](https://github.com/ngm0228) | [GitHub](https://github.com/jeongseockhyeon) |

<br/>
<br/>

# 3. 주요 기능
- **회원가입**:
  - 회원가입 시 DB에 유저정보가 등록됩니다.

- **로그인**:
  - 사용자 인증 정보를 통해 로그인합니다.
 
- **도서(book)**:
    - 도서 등록
    - 외부 API를 활용한 도서 정보 검색
        - 카카오 API 기반 도서 검색
        - 국립중앙도서관 API 기반 도서 카테고리 정보 보강
        - ISBN 기반 외부 API 도서 상세 조회
    - 알라딘 API 기반 베스트셀러 목록 조회
    - ISBN 중복 확인
    - 도서 목록 조회 및 조건부 검색
        - 제목, 카테고리, ISBN, 출판사, 저자 기준 검색
        - 출간일 범위 검색
        - 등록일 범위 검색
        - 등록일 기준 정렬을 통한 신착 도서 조회
    - 도서 상세 조회
        - 로그인 사용자 기준 관심 도서 여부 포함
    - 도서 정보 수정
    - 벌크 도서 삭제
        - 삭제 가능 도서만 삭제
        - 삭제 제외 도서 정보 반환
    - 최근 등록 도서 등록일 조회
    - 관련 도서 목록 5건 조회
        - 1순위 해당 도서를 대출한 사용자의 다른 대출 기록 도서
        - 2순위 같은 저자 도서
        - 3순위 같은 카테고리 도서

- **추천 도서(recommendation)**:
    - 추천 도서 설정
        - 추천 기간 설정
        - 추천 코멘트 등록
        - 일자별 최대 10권 추천 제한
    - 추천 도서 제외
    - 추천 도서 재정렬
    - 추천 도서 예약
        - 시작일/종료일 기반 예약 등록
        - 진행 중 및 예약된 추천 도서 목록 조회
    - 현재 노출 중인 추천 도서 Pick 10 목록 조회
    - 추천 도서의 추천 정보 단일 조회
    - 추천 도서 정보 수정
        - 추천 코멘트 수정
        - 추천 시작일/종료일 수정
    - 기간이 지난 만료된 추천 도서 이력 목록 조회
        - 검색어 및 기간 조건 검색

- **도서 대출(borrow)**:
    - 도서 대출
        - 기본 대출 기간 14일
        - 연체 또는 활성 패널티가 있는 경우 대출 제한
    - 도서 대출 기간 연장
        - 1회 7일 연장
    - 도서 반납 신청
    - 로그인한 사용자 본인 대출 이력 확인
        - 상태, 연체 여부, 기간, 검색어 기준 조건 검색
    - 도서별 최근 1년간 월별 대출 횟수 조회
    - 인기 대출 도서 목록 조회
        - 기간 기준 인기 도서 조회
        - Top-N 캐시 기반 조회
    - 도서 반납 승인
        - 연체 반납 시 패널티 생성
    - 전체 대출 정보 목록 조회
        - 사용자, 도서, 상태, 연체 여부, 기간, 검색어 기준 조건 검색

- **패널티(penalty)**:
    - 본인 패널티 목록 확인
    - 패널티 해제
    - 패널티 해제 철회
    - 패널티 해제 사유 변경
    - 특정 사용자의 패널티 목록 확인

- **관심 도서(wish)**:
    - 관심 도서 추가
    - 관심 도서 목록 조회
    - 관심 도서 삭제

<br/>
<br/>

# 4. API 명세서

- [API 명세서 확인](https://bookarte.github.io/bookarte-swagger-ui/)

<br/>
<br/>

# 5. 각 주요 작업 도메인
|  |  |  |
|-----------------|-----------------|-----------------|
| 남규민 |  <img src="https://avatars.githubusercontent.com/u/157104659?v=4" alt="남규민" width="100">| <ul><li>회원</li><li>게시판</li><li>AI</li></ul> |
| 정석현 |  <img src="https://avatars.githubusercontent.com/u/113079762?v=4" alt="정석현" width="100"> | <ul><li>도서</li><li>대출</li><li>패널티</li></ul> |

<br/>
<br/>

# 6. 기술 스택

## 6.1 Frontend
- Language: JavaScript,HTML,CSS
- Framework: React 19
- Build Tool: Vite

<br/>

## 6.2 Backend
- Langauage: Java 17
- Framework: SpringBoot 4.0.0, Spring Security
- Build Tool: Gradle
- Database: MySql
- Cache: Redis
- ORM: Spring Data JPA, QueryDsl 5.0.0
- Migration Tool: flyway
- Infra: AWS S3

<br/>

## 6.3 협업 툴
- 버전 관리: Git
- 문서 관리: Notion
- 소통: Discord
  
<br/>
<br/>

# 7. 프로젝트 데모

<br/>
<br/>

