# carrotmarket_clone

# 📁 프로젝트 개요

- 챗봇 상담사가 있는 중고 거래 플랫폼 페이지 만들기 (With Spring Boot)

# 🤝 팀 소개
<details>
  <summary>팀원 보기</summary>
<table border= 1px solid>
  <thead>
  <tr><td colspan=4 align="center">Team 커밋네고가능</td></tr>
  </thead>
  <tr align="center">
    <td>팀장 이수윤</td>
    <td>팀원 노윤표</td>
    <td>팀원 최수호</td>
    <td>팀원 허준</td>
  </tr>

  <tr>
    <td><a href=https://github.com/suyunlee><img object-fit=fill src=https://avatars.githubusercontent.com/u/87362279?v=4 width="200" height="200" alt="깃허브 페이지 바로가기"></a></td>
    <td><a href=https://github.com/Nyppp><img object-fit=fill src=https://avatars.githubusercontent.com/u/63279872?v=4 width="200" height="200" alt="깃허브 페이지 바로가기"></a></td>
    <td><a href=https://github.com/Hasegos><img object-fit=fill src=https://avatars.githubusercontent.com/u/93961708?v=4 width="200" height="200" alt="깃허브 페이지 바로가기"></a></td>
    <td><a href=https://github.com/JunHur97><img  object-fit=fill src=https://avatars.githubusercontent.com/u/206972816?v=4 width="200" height="200" alt="깃허브 페이지 바로가기"></a></td>
  </tr>
</table>
</details>

# 🛠️ 기술 스택
- **Frontend**: HTML, CSS
- **Backend**: Java, Spring Boot
- **Database**: PostgreSQL
- **Tooling**: GitHub, Figma, Discord

# 📝 커밋 컨벤션
<details>
<summary>커밋 코멘트</summary>
파일 추가 : add<br>
파일 수정 : modify <br>
파일 삭제 : delete <br>
버그수정 : fix <br>

---
</details>
<details>
<summary>네이밍 컨벤션</summary>

| 항목 | 예시 | 규칙 |
|------|------|------|
| 클래스 | `UserServiceImpl` | UpperCamelCase |
| 메서드 | `findById()` | lowerCamelCase |
| 변수 | `userName`, `postCount` | lowerCamelCase |
| 상수 | `MAX_SIZE`, `DEFAULT_TIMEOUT` | UPPER_SNAKE_CASE |

---
</details>
<details>
<summary>들여쓰기 / 공백 / 줄바꿈</summary>

- 들여쓰기: 공백 4칸<br>
- 연산자 앞뒤 공백: `=`, `+`, `==`, `&&` 등<br>
- 메서드 간 1줄 공백<br>
- 중괄호 `{}`는 한 줄 아래 (기본 Java 스타일)<br>

---
</details>
<details>
  <summary>디렉토리 구조 컨벤션</summary>

  ```
com.projectname
├── config
├── controller
├── dto
├── entity
├── repository
├── service
└── util
```

---
</details>
<details>
  <summary>API 컨벤션</summary>


| 항목 | 예시 |
|------|------|
| URI 규칙 | `/api/posts`, `/user-profile` (하이픈 소문자) |
| 응답 객체 | `ResponseEntity`로 래핑하여 반환 |
| DTO 역할 | 순수 데이터 전달용 (비즈니스 로직 X) |

---
</details>


# 📄 웹페이지 기능 명세서
<details>
<summary>🔐 로그인 페이지</summary>

#### 📌 필요 정보
- `id`, `username`, `password`, `address`, `provider`  
  → 유저 식별 및 로그인 처리용  
  → `address`는 동네 인증 여부 판단용
- 유저 입력값 (ID/비밀번호)
---

#### ⚙️ 기능
- 일반 로그인 (Spring Security 기반)
- **Google 소셜 로그인 (OAuth2)**
- 세션 설정 (로그인 상태 유지)
- 로그인 성공 시 유저의 동네 인증 여부 확인  
  → **동네 주소가 없다면 일부 기능 제한 (예: 게시글 작성 불가)**
- 아이디/비밀번호 검증 및 유효성 검사  
  → 입력값 비었을 경우 or 로그인 실패 시 에러 메시지 출력
---

#### 🔗 연결 페이지
- 헤더의 로그인 버튼 클릭 시 진입
- 게시글 작성 / 채팅 페이지 접근 시 로그인 페이지로 리다이렉트
- 회원가입 링크를 통해 회원가입 페이지로 이동 가능
---

</details>

<details>
<summary>🧾 회원가입 페이지</summary>

#### 📌 필요 정보
- 입력정보: `username`, `password`, `provider` (소셜 여부 포함)
---

#### ⚙️ 기능
- 회원가입 처리 (DB에 유저 정보 저장)
- 회원가입 완료 후 로그인 페이지로 자동 이동 or 이동 버튼 표시
---

</details>

<details>
<summary>🏠 메인 페이지 (홈)</summary>

#### 📌 필요 정보
- 전체 매물 리스트
- 매물별 세부 정보: 위치, 조회 수, 채팅 수 등
---

#### ⚙️ 기능
- 인기 매물 우선 노출 (조회 수 기준 or 기타 알고리즘)
- 인기 매물 → 그리드 카드 형식으로 표시 (이미지 / 제목 / 가격 / 지역)
- "더보기" 버튼 → 전체 중고 거래 페이지로 이동
- 기능별 안내 버튼 → 클릭 시 관련 기능 페이지로 이동
  - 예: 인기 매물 / 중고 거래 / 채팅
- **Google Play / App Store 버튼** → 모바일 앱 다운로드 유도 링크
---

#### 🔗 연결 페이지
- 홈 디렉토리(`/`)
- 헤더 아이콘 버튼
- 인기 매물 카드 클릭 → 개별 상세 페이지로 이동
- 안내 버튼 클릭 → 기능별 페이지로 연결
---

</details>

<details>
<summary>📦 게시물 리스트 페이지</summary>

#### 📌 필요 정보
- 전체 매물 리스트
- 로그인 여부 (로그인 시 → 글쓰기 버튼 노출)
- 각 매물 카드 정보: 이미지, 상품명, 가격, 지역
---

#### ⚙️ 기능
- 플로팅 버튼 (로그인한 유저만 거래 글 작성 가능)
- 매물 리스트 출력 (카드형 UI)
  - 이미지 / 제목 / 가격 / 위치 정보 포함
  - 각 카드 클릭 시 → 상세 페이지로 이동
- 페이지네이션 또는 무한스크롤 (UX 선택)
- 검색 기능
  - 제목 기반 검색 (RequestParam)
  - 장소 기반 검색 (RequestParam)
- 정렬 기능 (선택적 추가)
  - 거리순 / 최신순 / 가격순 등
---

#### 🔗 연결 페이지
- 거래 상세 페이지 (상품 카드 클릭)
- 거래 등록 페이지 (플로팅 버튼 클릭)
---

</details>

<details>
<summary>📄 거래 상세 페이지</summary>

#### 📌 필요 정보
- 게시물 상세 정보
  - `id`, `title`, `description`, `author`, `price`, `isSelled`, `createdAt`, `location`
- 판매자(글쓴이) 정보
- 현재 로그인한 유저 정보
- 페이지 로드 수 / 조회 수
- 댓글 정보 (선택 사항)
- 채팅 기록 정보 (선택 사항)
---

#### ⚙️ 기능
- 선택한 중고 상품의 상세 정보 출력
  - 이미지 / 제목 / 설명 / 가격 / 지역 등
- 현재 로그인 유저와 글쓴이 비교
  - 일치 시: 게시물 **수정**, **삭제** 버튼 노출
    - 수정 버튼 클릭 → 게시물 등록 페이지로 이동 (수정 모드)
- **목록으로 돌아가기** 버튼
- **채팅하기 버튼**
  - 로그인한 유저에게만 노출
  - 클릭 시 상대 유저 ID 포함하여 채팅 페이지로 이동
- 상품이 판매 완료된 경우 → 버튼 비활성화 or 상태 표시
---

#### 🔗 연결 페이지
- 게시물 리스트 페이지(이전)
- 채팅 페이지 (채팅하기 버튼)
- 게시물 등록 페이지 (수정 시)
---

</details>

<details>
<summary>📋 거래 등록 페이지</summary>

#### 📌 필요 정보
- 유저 정보 (작성자)
- 입력값
  - `title` (물건명)
  - `price` (가격)
  - `description` (상세 설명)
  - `img` (상품 사진) → 없을 경우 기본 이미지 처리
  - `address` (거래 지역) → 드롭다운 또는 지도 연동
---

#### ⚙️ 기능
- **폼 입력 기능**
  - 물건명, 가격, 설명, 지역, 사진 등 작성 가능
- **입력값 유효성 검사**
  - 필수 입력 항목 누락 시 등록 불가
  - 예: 제목 또는 가격이 비어 있으면 에러 메시지 표시
- **이미지 처리**
  - 이미지 업로드 기능
  - 업로드 없을 경우 → 기본 이미지(`no-img`) 처리
- **작성 완료 버튼**
  - 입력값 유효성 통과 시 → DB에 저장
  - 작성된 게시글 상세 페이지로 리다이렉트
---

#### 🔗 연결 페이지
- 메인 페이지 → 플로팅 버튼 → 거래 등록 페이지
- 등록 완료 후 → 해당 게시글 상세 페이지로 이동
- 수정 시 → 동일한 폼 재사용, 기존 정보 미리 채워진 상태
---

</details>
<details>
<summary>💬 채팅 페이지</summary>

#### 📌 필요 정보
- 로그인한 유저 정보
- 상대 유저 ID (판매자 or 구매자)
- 게시물 정보 (상품명, 가격, 썸네일 이미지 등)
- 채팅 리스트 (이전 채팅 내역)
- AI API 응답 정보 (챗봇 시나리오)
---

#### ⚙️ 기능
- **실시간 채팅 기능**
  - WebSocket 또는 SSE 방식
  - 메시지 전송/수신 즉시 렌더링
- **AI 챗봇 응답 기능**
  - 자동 메시지 추천 or 시나리오 기반 응답 제공
  - 예: "안녕하세요, 거래 가능하신가요?" 자동 응답
- **안 읽은 메시지 필터링**
  - 미확인 메시지만 보기 기능
  - 읽음 여부 상태 표시
- **최신순 정렬**
  - 최근 메시지가 아래에 표시
- **신뢰도 시스템 (선택 기능)**
  - 메시지 기반 거래 평가 또는 사용자 점수화
- **거래 완료 처리**
  - 거래가 성사되면 버튼 클릭으로 상태 전환
  - 완료된 채팅방 → 게시글 상태도 '판매 완료' 처리
- **거래 상태 표시**
  - 거래 진행 중 / 완료 등의 시각적 표시
---

#### 🔗 연결 페이지
- 거래 상세 페이지 → 채팅하기 버튼 클릭 시 진입
- 목록형 채팅방 → 해당 대화방으로 이동 가능
---
</details>

<details>
  <summary>📍 위치 인증 페이지 (동네 인증)</summary>

#### 📌 필요 정보
- 현재 사용자 위치 (GPS or 브라우저 위치 정보)
- 유저 정보 (DB에 저장된 인증 여부)
- Google Map API를 통한 위치 매핑
---

#### ⚙️ 기능
- **위치 검색 기능**
  - Google Map 또는 주소 입력 기반 검색
  - 주소는 '동' 단위까지만 허용 (예: 서울시 마포구 **서교동**)
- **현재 위치 기반 인증 기능**
  - 검색한 주소와 실제 위치(GPS 기반)가 일치할 경우에만 인증 처리
- **동네 설정 기능**
  - 인증 성공 시 사용자 정보에 `address` 필드 저장
  - 사용자 권한 또는 세션에 인증 상태 반영 (예: ROLE_AUTHENTICATED)
- **Spring Security 기반 Role 처리**
  - 인증된 사용자만 게시글 작성 / 채팅 기능 사용 가능하도록 설명
---

#### 🔗 연결 페이지
- 헤더의 "위치 인증" 버튼 클릭 시 진입
- 로그인 후 미인증 상태일 때 자동 리다이렉트될 수도 있음
---

</details>

<details>
  <summary>🔝 헤더 (상단 네비게이션)</summary>

#### 📌 필요 정보
- 로그인 여부
- 동네 인증 여부 (주소 등록 여부)
---

#### ⚙️ 기능
- 로그인 여부 및 동네 인증 여부에 따라 동적 버튼 렌더링
  - 로그인 X → "로그인" 버튼 표시
  - 로그인 O + 동네 인증 X → "동네 인증" 버튼 활성화
  - 로그인 O + 동네 인증 O → "채팅하기", "마이페이지" 등 기능 활성화
- 검색창 제공 (위치 또는 키워드 기반)
- 각 버튼 클릭 시 해당 기능 페이지로 이동
---

#### 🔗 연결 페이지
- 로그인 페이지
- 위치 인증 페이지
- 채팅 페이지 (로그인 + 동네 인증 시만 가능)
- 마이페이지 or 내 정보 페이지
---

</details>

<details>
<summary>📎 푸터 (하단 영역)</summary>

#### ⚙️ 기능
- 하단 주요 링크 연결 (회사소개, 이용약관, 개인정보처리방침 등)
- 퀵 이동 링크 버튼 제공:
  - "거래하기" → 메인 페이지 이동
  - "채팅하기" → 유저 로그인 및 동네 인증 여부 확인 후 채팅 페이지로 이동
---

#### 🔗 연결 페이지
- 메인 페이지 (`/`)
- 채팅 페이지 (`/chat`) ← 로그인 + 인증 필요
- 외부 정책 페이지 (약관, 소개 등)
---

</details>

# 📊 ERD (Entity Relationship Diagram)

## 🗺️ ERD 이미지

![carrotERD (1)](https://github.com/user-attachments/assets/fc6c611c-43f2-4f64-a58a-56ccd93c45e8)


<details>
<summary>🧱 주요 테이블 설명</summary>

#### 1. 👤 User (사용자)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 사용자 고유 ID |
| username | VARCHAR(255) | 이메일(로그인용) |
| password | VARCHAR(255) | 암호화된 비밀번호 |
| nickname | VARCHAR(255) | 닉네임 |
| phonenumber | VARCHAR(20) | 휴대전화 번호 |
| location | VARCHAR(255) | 위치 정보 |
| createdAt | DATE | 생성일 |
| role | VARCHAR(20) | 권한 (e.g. ROLE_USER) |
| status | VARCHAR(20) | 계정 상태 |
| neighborhoodVerified | BOOLEAN | 동네 인증 여부 |
| neighborhoodName | VARCHAR(100) | 동네 이름 |
| neighborhoodVerifiedAt | DATETIME | 인증 시간 |
| oAuth2 | STRING | 로그인 방식 (local / google 등) |

---

#### 2. 📦 Post (게시물)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 게시물 ID |
| user_id | BIGSERIAL (FK) | 작성자 |
| category_id | BIGSERIAL (FK) | 카테고리 참조 |
| title | VARCHAR(255) | 제목 |
| description | TEXT | 내용 |
| price | NUMERIC | 가격 |
| isSelled | BOOLEAN | 판매 여부 |
| createdAt | DATE | 등록일 |
| updatedAt | DATE | 수정일 |
| location | VARCHAR(255) | 거래 위치 |
| viewCount | INTEGER | 조회수 |

---

#### 3. 🏷️ Category (카테고리)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 카테고리 ID |
| name | VARCHAR(100) | 카테고리 이름 |

---

#### 4. 🖼️ Image (이미지)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 이미지 ID |
| post_id | LONG (FK) | 게시물 참조 |
| imageUrl | VARCHAR(255) | 이미지 경로 |

---

#### 5. ❤️ Like (좋아요)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 좋아요 ID |
| user_id | BIGSERIAL (FK) | 유저 참조 |
| post_id | BIGSERIAL (FK) | 게시물 참조 |

---

#### 6. 💬 ChatRoom (채팅방)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 채팅방 ID |
| post_id | LONG (FK) | 관련 게시물 |
| userId | LONG (FK) | 참여자 유저 |
| isChatbot | BOOLEAN | 챗봇 여부 |

---

#### 7. 📨 ChatMessage (채팅 메시지)
| 컬럼명 | 타입 | 설명 |
|--------|------|------|
| id | BIGSERIAL (PK) | 메시지 ID |
| chatRoom_id | BIGSERIAL (FK) | 채팅방 참조 |
| senderId | BIGSERIAL (FK) | 보낸 사람 |
| content | TEXT | 메시지 내용 |
| createdAt | DATETIME | 보낸 시간 |

---
</details>

<details>
<summary>🔗 테이블 간 관계 요약</summary>

- `User` 1 : N `Post`
- `User` 1 : N `ChatRoom` / `ChatMessage` / `Like`
- `Post` 1 : N `Image`
- `Post` 1 : N `Like`
- `Post` 1 : N `ChatRoom`
- `ChatRoom` 1 : N `ChatMessage`

📦 DTO 테이블 (전송 객체)

- `UserDTO`, `PostDTO`: 클라이언트에 전달 시 불필요한 정보를 제외한 경량 데이터 구조
- 뷰단 혹은 API 응답용 객체로 사용됨 (JPA Entity와 별개)

---
</details>
