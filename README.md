# Tikkl Bank

인공지능 공동창업자 양성과정 MCP

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Gradle 8.5
- **Container**: Docker & Docker Compose

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/tikkl/bank/
│   │   ├── controller/    # REST API 컨트롤러
│   │   ├── entity/        # JPA 엔티티
│   │   ├── repository/    # JPA 레포지토리
│   │   ├── service/       # 비즈니스 로직
│   │   └── config/        # 설정 클래스
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/tikkl/bank/
```

## 시작하기

### 로컬 개발 환경

1. MySQL 설치 및 데이터베이스 생성
2. 빌드 및 실행:
```bash
./gradlew build
./gradlew bootRun
```

### Docker로 실행

```bash
# Docker Compose로 MySQL과 애플리케이션 함께 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app
```

## API 엔드포인트

| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/accounts | 모든 계좌 조회 |
| GET | /api/accounts/{id} | 특정 계좌 조회 |
| POST | /api/accounts | 계좌 생성 |
| POST | /api/accounts/{id}/deposit | 입금 |
| POST | /api/accounts/{id}/withdraw | 출금 |
| DELETE | /api/accounts/{id} | 계좌 삭제 |

## 환경 변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| MYSQL_HOST | localhost | MySQL 호스트 |
| MYSQL_PORT | 3306 | MySQL 포트 |
| MYSQL_DATABASE | tikklbank | 데이터베이스 이름 |
| MYSQL_USER | root | MySQL 사용자 |
| MYSQL_PASSWORD | password | MySQL 비밀번호 |
