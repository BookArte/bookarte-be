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
$ ./gradlew bootRun
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
- **회원(member/auth)**:
  - 회원가입
    - 아이디, 이름, 이메일, 연락처 정보 기반 가입
    - 가입 시 아이디 중복 확인 필수 처리
  - 로그인
    - JWT(JSON Web Token) 기반 인증 처리
  - 아이디 찾기
    - 이메일, 이름, 연락처 입력 기반 가입 내역 조회
    - 조회된 회원 아이디 일부 마스킹 처리 후 리스트 노출
  - 비밀번호 찾기 및 재설정
    - 아이디 및 이메일 입력 시 해당 이메일로 인증코드 발송
    - 인증코드 검증 성공 시 비밀번호 변경 기능 제공
  - 회원 정보 수정
    - 이름, 연락처, 이메일 정보 변경 가능
  - 비밀번호 변경
    - 회원 정보 수정과 분리된 독립된 기능으로 구현
    - 기존 비밀번호 확인 및 검증 통과 후 수정 가능
  - 회원 탈퇴
    - 대출 중인 도서가 있을 경우 회원 탈퇴 불가 처리 (대출 내역 검증 로직 추가)

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
 
- **QNA(qna)**:
  - QNA 게시글 등록 (회원 전용)
  - QNA 게시글 수정 및 삭제 권한 제어
    - 관리자 답변 대기 상태: 작성자 본인에 한해 수정 및 삭제 가능
    - 관리자 답변 완료 상태: 수정 및 삭제 불가 처리
  - QNA 목록 및 상세 조회
  - 파일 업로드
    - AWS S3를 활용한 첨부파일 업로드 지원

- **공지사항(notice)**:
  - 공지사항 등록 및 관리 (관리자 전용)
  - 상단 공지 고정 기능
    - 상단 공지 고정 및 우선순위(숫자) 기반 상위 노출 적용
  - 공지사항 목록 및 상세 조회
  - 파일 업로드
    - AWS S3를 활용한 첨부파일 업로드 지원
   
- **뉴스(news)**:
  - 뉴스 등록 및 관리 (관리자 전용)
  - 썸네일 게시판 UI 대응 기능
  - 상단 공지 고정 기능
    - 상단 공지 고정 및 우선순위(숫자) 기반 상위 노출 적용
  - 뉴스 목록 및 상세 조회
  - 이미지 및 파일 업로드
    - AWS S3를 활용한 썸네일 이미지 및 첨부파일 업로드 지원
   
- **FAQ(faq)**:
  - FAQ 등록 및 관리 (관리자 전용)
  - 상단 공지 고정 기능
    - 상단 공지 고정 및 우선순위(숫자) 기반 상위 노출 적용
  - FAQ 목록 및 상세 조회
  - 파일 업로드
    - AWS S3를 활용한 첨부파일 업로드 지원

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
- State Management:Zustand
- Http Client: Axios

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
## 7.1 배포 주소
https://bookarte.vercel.app

## 7.2 페이지별 기능 

### 7.2.1 회원(member/auth)

| 회원가입 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/652a16e0-9501-4aac-9015-bcd9707133fd" /> |

| 로그인 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/be6bf9bf-385c-4835-9ac3-a6f8e91d1c35" /> |

| 비밀번호 수정 & 회원정보 수정 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/8e4e2ea0-6af0-4bb1-92b9-e51c231de68e" /> |

### 7.2.2 도서

| 도서 등록 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/4a4a41bf-591d-40e1-8f01-016d95ee9d28" /> |

| 도서 검색 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/1f0d066e-2c1b-4516-bc76-28313f60c700" /> |

| 관심 도서 & 도서 대출 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/e5f34eac-c84d-4c5a-b107-dff2e58f525a" /> |

| 대출 연장 & 대출 반납 신청 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/a2976e00-a9b9-499c-a61e-2cf92eedb1bc" /> |

| 추천 도서 등록 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/2ca60dfc-4955-47df-bdbe-2f3891da1853" /> |

| 추천 도서 등록 실패 & 예약 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/ad3367e7-1244-4e7e-932d-aae90f002a98" /> |

### 7.2.3 게시판(board)

| 공지사항 작성 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/dfd38701-2712-4e75-bb46-bb7463512fd0" /> |

| 공지사항 수정 & 삭제 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/bd6e710d-f8c3-4997-b6f3-f46d2995ff62" /> |

| 뉴스 작성 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/cc37f7a4-bc32-440e-9ffe-f3bc5bea9268" /> |

| 뉴스 수정 & 삭제 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/f181b4c8-8429-4ad1-a9ad-65e042be7e19" /> |

| FAQ 작성 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/d05f87c7-2954-4639-982a-2355e48f9ec4" /> |

| FAQ 수정 & 삭제 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/e2766e51-bd47-464e-9837-c0b8b2e78e2c" /> |

| QNA 작성 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/405cfd47-c117-4b1e-8d56-b206be487b5a" /> |

| QNA 답변 |
|-----------------|
| <img width="800" height="450" alt="Image" src="https://github.com/user-attachments/assets/82898a12-d9cd-449e-b471-2495b642bb99" /> |


<br/>
<br/>

