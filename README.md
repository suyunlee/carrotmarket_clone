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

![carrotERD (1)](https://github.com/user-attachments/assets/fc6c611c-43f2-4f64-a58a-56ccd93c45e8)

