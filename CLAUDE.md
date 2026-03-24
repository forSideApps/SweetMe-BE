# SweetMe BE — CLAUDE.md

상위 디렉토리의 `/SweetMe/CLAUDE.md` 참고.

## 패키지 구조
```
dev.sweetme/
├── config/         # AppConfig, CorsConfig, DataInitializer, RateLimitFilter, AdminAuthInterceptor
├── controller/     # AuthController, RoomApiController, ReviewApiController, CommunityApiController, CompanyController, VisitorController
├── service/        # RoomService, ReviewService, CompanyService, CommunityService, OciStorageService
├── repository/     # Spring Data JPA Repositories
├── domain/         # JPA 엔티티 + enums/
├── dto/            # Request DTO + response/ 패키지
└── util/           # SessionHelper
```

## 컨트롤러 → 서비스 매핑

| 컨트롤러 | 서비스 | 주요 기능 |
|---------|--------|----------|
| `AuthController` | - | 로그인·회원가입·세션·마이페이지 데이터 |
| `RoomApiController` | `RoomService` | 스터디방 CRUD, 신청 관리 |
| `ReviewApiController` | `ReviewService` | 포폴·이력서 CRUD, 서로보기 교환 |
| `CommunityApiController` | `CommunityService` | 커뮤니티 CRUD |
| `CompanyController` | `CompanyService` | 기업 테마 관리 |

## 서로보기(ReviewExchange) 플로우
1. **신청**: `POST /reviews/{targetId}/exchange` → `createExchangeRequest` → PENDING 저장
2. **수락**: `POST /reviews/exchanges/{id}/accept` → `acceptExchange` → status=ACCEPTED
3. **거절**: `POST /reviews/exchanges/{id}/reject` → `rejectExchange` → **레코드 삭제** (Oracle CHECK 제약 우회)
4. **취소**: `DELETE /reviews/exchanges/{id}` → `cancelExchange` → 레코드 삭제

> 거절 시 DB에서 삭제하는 이유: Oracle CHECK 제약이 PENDING/ACCEPTED만 허용할 수 있음

## DataInitializer 시드 데이터
- admin 계정: 없으면 생성
- Company: count=0일 때 생성
- Room: count=0일 때 생성 (10개)
- CommunityPost: count=0일 때 생성 (FREE 10개 + SUGGESTION 3개)

## 주요 패턴
```java
// 세션에서 username 꺼내기
String username = SessionHelper.getUsername(request); // null이면 비로그인

// 관리자 체크
boolean isAdmin = SessionHelper.isAdmin(request);

// Oracle Sequence ID
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_xxx")
@SequenceGenerator(name = "seq_xxx", sequenceName = "SEQ_XXX", allocationSize = 1)
```

## 빌드 / 실행
```bash
# 빌드
./gradlew bootJar

# Docker
docker compose up -d

# 포트: 21002 (컨테이너), 21001 (호스트)
```
