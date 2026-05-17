# PlayBall 백엔드 흐름 문서

스포츠 매칭 플랫폼 PlayBall의 백엔드 로직을 흐름별로 정리한 문서입니다.

---

## 목차

| 번호 | 흐름 | 파일 | 핵심 API |
|------|------|------|----------|
| 01 | 로그인 / 로그아웃 / 토큰 재발급 | [01_로그인.md](01_로그인.md) | `POST /api/auth/kakao` |
| 02 | 회원가입 추가정보 입력 | [02_회원가입.md](02_회원가입.md) | `POST /api/members/signup/complete` |
| 03 | 프로필 조회·수정 | [03_프로필.md](03_프로필.md) | `GET/PATCH /api/members/me` |
| 04 | 지역 매칭 — 참가자 흐름 | [04_지역매칭_참가자.md](04_지역매칭_참가자.md) | `GET /api/matches`, `POST /api/matches/{id}/join` |
| 05 | 지역 매칭 — 주최자 흐름 | [05_지역매칭_주최자.md](05_지역매칭_주최자.md) | `POST /api/matches`, `PATCH/DELETE /api/matches/{id}` |
| 06 | 랜덤 매칭 | [06_랜덤매칭.md](06_랜덤매칭.md) | `POST /api/matches/random` |
| 07 | 매칭 성공 후 내 경기 조회 | [07_내경기조회.md](07_내경기조회.md) | `GET /api/members/me/matches`, `GET /api/matches/{id}` |
| 08 | 경기 후 칭찬 | [08_칭찬.md](08_칭찬.md) | `POST /api/matches/{id}/compliments` |
| 09 | 알림 | [09_알림.md](09_알림.md) | `GET /api/notifications` |

---

## 공통 규칙

### 인증
- 모든 API (일부 제외)는 `Authorization: Bearer {accessToken}` 헤더 필요
- 토큰이 없거나 만료된 경우 → `401 Unauthorized`
- 비로그인 허용 API: 닉네임 중복확인, 경기 목록/상세 조회

### 공통 응답 형식
```json
// 성공
{ "success": true, "message": "성공 메시지", "data": { ... } }

// 실패
{ "success": false, "message": "에러 메시지", "data": null }
```

### 에러 코드 목록
| 코드 | HTTP | 메시지 |
|------|------|--------|
| `USER_NOT_FOUND` | 404 | 존재하지 않는 유저입니다. |
| `NICKNAME_ALREADY_EXISTS` | 409 | 이미 사용 중인 닉네임입니다. |
| `SIGNUP_ALREADY_COMPLETED` | 409 | 이미 회원가입이 완료된 계정입니다. |
| `KAKAO_AUTH_FAILED` | 401 | 카카오 인증에 실패했습니다. |
| `INVALID_TOKEN` | 401 | 유효하지 않은 토큰입니다. |
| `EXPIRED_TOKEN` | 401 | 만료된 토큰입니다. |
| `MATCH_NOT_FOUND` | 404 | 존재하지 않는 경기입니다. |
| `MATCH_FULL` | 409 | 경기 인원이 꽉 찼습니다. |
| `ALREADY_JOINED` | 409 | 이미 참가한 경기입니다. |
| `MATCH_DELETED` | 404 | 이미 삭제된 경기입니다. |
| `MATCH_JOIN_CONFLICT` | 409 | 다른 사용자가 동시에 참가 중입니다. |
| `NOT_JOINED` | 400 | 참가하지 않은 경기입니다. |
| `NOT_A_PARTICIPANT` | 403 | 경기에 참가하지 않은 회원입니다. |
| `FORBIDDEN` | 403 | 접근 권한이 없습니다. |
| `INVALID_INPUT` | 400 | 잘못된 입력입니다. |
| `SELF_COMPLIMENT` | 400 | 자기 자신은 칭찬할 수 없습니다. |
| `DUPLICATE_COMPLIMENT` | 409 | 이미 칭찬한 참가자가 포함되어 있습니다. |
