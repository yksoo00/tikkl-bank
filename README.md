# Tikkl Bank

인공지능 공동창업자 양성과정 MCP

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: MySQL 8.0 (로컬)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Gradle 8.5
- **Container**: Docker

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/tikkl/bank/
│   │   ├── exception/     # 예외 처리 (GlobalExceptionHandler, CustomException)
│   │   └── config/        # 설정 클래스
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/tikkl/bank/
```

## 예외 처리

### CustomException

커스텀 예외를 생성하려면 `CustomException`을 상속받아 구현합니다:

```java
public class MyCustomException extends CustomException {
    public MyCustomException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "MY_ERROR_CODE");
    }
}
```

### GlobalExceptionHandler

`GlobalExceptionHandler`에서 다음 예외를 처리합니다:
- `CustomException`: 커스텀 예외
- `MethodArgumentNotValidException`: 유효성 검사 예외
- `Exception`: 일반 예외

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
# Docker 이미지 빌드
docker build -t tikkl-bank .

# Docker 컨테이너 실행 (로컬 MySQL에 연결)
docker run -p 8080:8080 \
  -e MYSQL_HOST=host.docker.internal \
  -e MYSQL_PORT=3306 \
  -e MYSQL_DATABASE=tikklbank \
  -e MYSQL_USER=root \
  -e MYSQL_PASSWORD=password \
  tikkl-bank
```

## 환경 변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| MYSQL_HOST | localhost | MySQL 호스트 |
| MYSQL_PORT | 3306 | MySQL 포트 |
| MYSQL_DATABASE | tikklbank | 데이터베이스 이름 |
| MYSQL_USER | root | MySQL 사용자 |
| MYSQL_PASSWORD | password | MySQL 비밀번호 |
