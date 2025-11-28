# Tikkl Bank

인공지능 공동창업자 양성과정 MCP - 티끌 뱅크 서비스

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.2.0
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Gradle 8.5
- **Container**: Docker, Docker Compose

## 빠른 시작 (Quick Start)

### Docker Compose로 실행 (권장)

가장 쉬운 방법입니다. Docker와 Docker Compose가 설치되어 있어야 합니다.

```bash
# 1. 저장소 클론
git clone https://github.com/yksoo00/tikkl-bank.git
cd tikkl-bank

# 2. Docker Compose로 실행 (MySQL + 애플리케이션)
docker-compose up -d

# 3. 로그 확인
docker-compose logs -f app

# 4. 종료
docker-compose down

# 5. 데이터 포함 완전 삭제
docker-compose down -v
```

서버 실행 후 `http://localhost:8080/index.html`에서 API를 테스트할 수 있습니다.

### 로컬 개발 환경

MySQL을 로컬에 직접 설치하여 개발하는 경우:

```bash
# 1. MySQL 설치 및 데이터베이스 생성
mysql -u root -p
CREATE DATABASE tikklbank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 빌드
./gradlew build

# 3. 실행
./gradlew bootRun
```

### Docker만 사용 (MySQL은 로컬)

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

## API 테스트 페이지

서버 실행 후 `http://localhost:8080/index.html`에서 모든 API를 테스트할 수 있습니다.

**지원 기능:**
- 인증 (회원가입/로그인)
- 대시보드 (저축잔액, 이자, 카드사용, 청구예정 등 종합 조회)
- 계좌 관리 (등록, 입금, 출금)
- 카드 관리 (등록, 저축계좌 연결, 월 목표 설정)
- 카드 결제 및 자동 저축
- 카드 혜택 관리 (할인, 캐시백, 포인트 등)
- 청구 내역 조회
- 거래내역 검색
- 금융상품 조회

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
| `Card` | 연동 카드 정보 (카드번호, 카드사, 보너스비율, 월 목표 사용액, 저축계좌 연결 등) |
| `CardBenefit` | 카드 혜택 정보 (혜택명, 유형, 할인율, 목표금액, 달성률 등) |
| `CardBilling` | 카드 청구 내역 (청구월, 총액, 혜택금액, 저축금액 등) |
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
| GET | `/api/members/{memberId}/dashboard` | 대시보드 (종합 조회) |

### 계좌 (Account)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/accounts` | 계좌 목록 조회 |
| GET | `/api/members/{memberId}/accounts/{accountId}` | 계좌 상세 조회 |
| POST | `/api/members/{memberId}/accounts` | 계좌 등록 |
| POST | `/api/members/{memberId}/accounts/{accountId}/deposit` | 입금 |
| POST | `/api/members/{memberId}/accounts/{accountId}/withdraw` | 출금 |
| PUT | `/api/members/{memberId}/accounts/{accountId}/primary` | 주 계좌 설정 |
| DELETE | `/api/members/{memberId}/accounts/{accountId}` | 계좌 삭제 |

### 카드 (Card)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/cards` | 카드 목록 조회 |
| GET | `/api/members/{memberId}/cards/{cardId}` | 카드 상세 조회 |
| GET | `/api/members/{memberId}/cards/{cardId}/detail` | 카드 상세 (혜택/청구/달성률) |
| POST | `/api/members/{memberId}/cards` | 카드 등록 (월 목표 설정) |
| POST | `/api/members/{memberId}/cards/{cardId}/payment` | 카드 결제 (자동 저축) |
| POST | `/api/members/{memberId}/cards/{cardId}/link-savings` | 저축 계좌 연결 |
| PUT | `/api/members/{memberId}/cards/{cardId}/bonus-ratio` | 보너스 저축 비율 설정 |
| DELETE | `/api/members/{memberId}/cards/{cardId}` | 카드 비활성화 |

### 카드 혜택 (CardBenefit)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/cards/{cardId}/benefits` | 혜택 목록 조회 |
| POST | `/api/members/{memberId}/cards/{cardId}/benefits` | 혜택 추가 |

### 청구 내역 (CardBilling)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/members/{memberId}/cards/{cardId}/billings` | 청구 내역 조회 |

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

## 환경 변수

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| MYSQL_HOST | localhost | MySQL 호스트 |
| MYSQL_PORT | 3306 | MySQL 포트 |
| MYSQL_DATABASE | tikklbank | 데이터베이스 이름 |
| MYSQL_USER | root | MySQL 사용자 |
| MYSQL_PASSWORD | password | MySQL 비밀번호 |
