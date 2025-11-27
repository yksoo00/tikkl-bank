# Tikkl Bank

인공지능 공동창업자 양성과정 MCP - 티끌 뱅크 서비스

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
│   │   ├── common/        # 공통 응답 (ApiResponse)
│   │   ├── controller/    # REST API 컨트롤러
│   │   ├── dto/
│   │   │   ├── request/   # 요청 DTO
│   │   │   └── response/  # 응답 DTO
│   │   ├── entity/        # JPA 엔티티
│   │   ├── exception/     # 예외 처리
│   │   ├── repository/    # JPA 레포지토리
│   │   └── service/       # 비즈니스 로직
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/tikkl/bank/
```

## 엔티티

| 엔티티 | 설명 |
|--------|------|
| `Member` | 회원 정보 (로그인ID, 비밀번호, 이름, 생년월일, 전화번호, 저축비율 등) |
| `Account` | 연동 계좌 정보 (계좌번호, 은행명, 계좌유형, 잔액 등) |
| `Card` | 연동 카드 정보 (카드번호, 카드사, 카드유형, 보너스비율 등) |
| `Transaction` | 거래 내역 (거래유형, 금액, 저축금액, 가맹점 등) |
| `SavingsAccount` | 티끌 전용 저축 계좌 (저축잔액, 누적저축금액, 이자율, 만기일 등) |
| `FinancialProduct` | 금융 상품 정보 (상품명, 금리, 기간, 약관 등) |

## API 엔드포인트

### 인증 (Auth)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |

### 회원 (Member)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}` | 회원 정보 조회 |
| GET | `/api/members/{memberId}/mypage` | 마이페이지 조회 |
| PUT | `/api/members/{memberId}/savings-settings` | 저축 설정 변경 |
| POST | `/api/members/{memberId}/onboarding` | 온보딩 완료 |

### 홈 (Home)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/home` | 홈 데이터 조회 |
| GET | `/api/members/{memberId}/savings` | 저축 계좌 조회 |

### 계좌 (Account)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/accounts` | 계좌 목록 조회 |
| GET | `/api/members/{memberId}/accounts/{accountId}` | 계좌 상세 조회 |
| POST | `/api/members/{memberId}/accounts` | 계좌 등록 |
| PUT | `/api/members/{memberId}/accounts/{accountId}/primary` | 주 계좌 설정 |
| DELETE | `/api/members/{memberId}/accounts/{accountId}` | 계좌 삭제 |

### 카드 (Card)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/cards` | 카드 목록 조회 |
| GET | `/api/members/{memberId}/cards/{cardId}` | 카드 상세 조회 |
| POST | `/api/members/{memberId}/cards` | 카드 등록 |
| PUT | `/api/members/{memberId}/cards/{cardId}/bonus-ratio` | 보너스 저축 비율 설정 |
| DELETE | `/api/members/{memberId}/cards/{cardId}` | 카드 비활성화 |

### 거래 내역 (Transaction)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/transactions` | 거래 내역 조회 (검색/필터) |
| GET | `/api/members/{memberId}/transactions/recent` | 최근 거래 내역 조회 |
| GET | `/api/members/{memberId}/transactions/{transactionId}` | 거래 상세 조회 |

### 금융 상품 (Product)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/products` | 전체 상품 목록 |
| GET | `/api/products/type/{productType}` | 유형별 상품 조회 |
| GET | `/api/products/{productId}` | 상품 상세 조회 |

## 응답 구조

### 성공 응답
```json
{
  "success": true,
  "data": { ... },
  "message": "성공 메시지"
}
```

### 에러 응답
```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

## ErrorCode

```java
// Common errors
INTERNAL_SERVER_ERROR, VALIDATION_ERROR, INVALID_REQUEST, NOT_FOUND, UNAUTHORIZED, FORBIDDEN

// Member errors
MEMBER_NOT_FOUND, DUPLICATE_LOGIN_ID, DUPLICATE_PHONE_NUMBER, INVALID_PASSWORD

// Account errors
ACCOUNT_NOT_FOUND, DUPLICATE_ACCOUNT, INSUFFICIENT_BALANCE

// Card errors
CARD_NOT_FOUND, INVALID_CARD_TYPE

// Savings errors
SAVINGS_ACCOUNT_NOT_FOUND, SAVINGS_ACCOUNT_ALREADY_EXISTS

// Transaction errors
TRANSACTION_NOT_FOUND, TRANSACTION_FAILED

// Product errors
PRODUCT_NOT_FOUND
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
