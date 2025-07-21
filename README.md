# carrotmarket_clone

# 📁 프로젝트 개요

- 챗봇 상담사가 있는 중고 거래 플랫폼 페이지 만들기 (With Spring Boot)

# 🤝 팀 소개
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

# 🛠️ 기술 스택
- **Frontend**: <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
- **Backend**: <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
- **Database**: <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
- **Tooling**: <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white"> <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">

# 📁 디렉토리 구조

  ```
carrotmarket-clone/
├── 📦 oreumi.group2.carrotClone/
│   ├── ⚙️  Config/            # 설정 클래스 (Security, Web 등)
│   ├── 🧭 controller/        # 웹 요청 처리 컨트롤러
│   ├── 📨 dto/               # DTO (데이터 전송 객체)
│   ├── ❌ error/             # 예외 처리 관련 클래스
│   ├── 🧩 model/             # JPA 엔티티 및 도메인 모델
│   ├── 🗂️  repository/        # JPA 리포지토리
│   ├── 🔐 security/          # 인증/인가 로직
│   ├── 🧠 service/           # 비즈니스 로직 처리
│   ├── ✅ validation/        # 커스텀 검증
│   └── 🚀 CarrotCloneApplication.java  # 메인 실행 클래스

├── 📁 resources/
│   ├── 📂 data/              # 초기 데이터 또는 테스트 파일
│   ├── 🌐 static/            # 정적 자원 (브라우저에서 직접 접근)
│   │   ├── 🎨 css/           # 스타일시트
│   │   ├── 🖼️ images/        # 이미지 파일
│   │   └── ⚙️ js/            # 자바스크립트
│   └── 📄 templates/         # Thymeleaf 템플릿
│       ├── 🔐 auth/          # 로그인/회원가입 등 인증 관련 뷰
│       ├── 💬 chat/          # 채팅방 관련 뷰
│       ├── ❌ error/         # 에러 페이지
│       ├── 🧩 fragments/     # 공통 레이아웃 (header/footer 등)
│       ├── 📍 location/      # 위치 인증 관련 뷰
│       ├── 📝 post/          # 게시글 관련 뷰
│       └── 🏠 home.html       # 홈 화면 뷰 (루트 페이지)

```


# 📊 ERD (Entity Relationship Diagram)

## 🗺️ ERD 이미지

<img width="1516" height="484" alt="image" src="https://github.com/user-attachments/assets/a7810241-a469-4dde-9ae3-686a2c4144c5" />

## 🗏 페이지 구성

### 메인 페이지
<img width="1626" height="932" alt="image" src="https://github.com/user-attachments/assets/c8e49a05-22b0-48f9-9b02-35c027d91248" />
<img width="1006" height="279" alt="image" src="https://github.com/user-attachments/assets/c29661b6-db48-4efc-b631-bcd7985cd569" />


- 메인 담당자 : 노윤표
- 주요 개발 기능 : 페이지 Flex 및 반응형 구성, 헤더 푸터(공통 페이지) 구현

---

### 로그인 / 회원 가입 페이지
<img width="402" height="373" alt="image" src="https://github.com/user-attachments/assets/3b802cfe-367e-4ea0-a408-c35799b25b9a" />
<img width="372" height="411" alt="image" src="https://github.com/user-attachments/assets/646458ab-2c5f-4230-9b80-decccbc306c4" />

- 메인 담당자 : 허준
- 주요 개발 기능 : 로그인 / 가입 폼 처리, FilterChain을 사용한 인증 / 인가 처리, 소셜 로그인(구글) 연동

---

### 게시물 페이지 (리스트 / 등록 / 상세 페이지)
<img width="1007" height="913" alt="image" src="https://github.com/user-attachments/assets/c933b1c4-1981-4755-af91-f645e7dd299a" />
<img width="921" height="602" alt="image" src="https://github.com/user-attachments/assets/0e006d3c-5c8c-4145-901c-510c6f71b8f5" />
<img width="1007" height="713" alt="image" src="https://github.com/user-attachments/assets/3342672f-c0aa-4082-8779-eb8d6c2b6552" />

- 메인 담당자 : 이수윤
- 주요 개발 기능 : 게시물 페이지네이션(무한 스크롤), AWS S3 이미지 업로딩, 게시물 리스트 그리드 및 반응형 구성
