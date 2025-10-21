# DB 마이그레이션 가이드 (Flyway)

## 📚 개념

**DB 마이그레이션**: 데이터베이스 스키마 변경을 **코드처럼 버전 관리**하는 것

### 장점
- ✅ 스키마 변경 이력 추적 (Git처럼)
- ✅ 여러 환경(dev/prod) 동일한 버전 관리
- ✅ CI/CD 파이프라인에서 자동 실행
- ✅ 실수로 삭제된 마이그레이션 방지 (checksumming)
- ✅ 롤백 가능 (Undo 마이그레이션)

---

## 🗂️ 디렉토리 구조

```
src/main/resources/
├── db/migration/
│   ├── V1__Create_users_table.sql
│   ├── V2__Add_phone_to_users.sql
│   └── V3__Create_posts_table.sql  (새로운 마이그레이션)
└── application.yml
```

---

## 📝 파일명 규칙

**Flyway는 이 패턴을 따릅니다:**

```
V{버전}__{설명}.sql
```

### 예시
- `V1__Create_users_table.sql` → 1번 마이그레이션: 사용자 테이블 생성
- `V2__Add_email_to_users.sql` → 2번 마이그레이션: 사용자 테이블에 이메일 추가
- `V3__Create_posts_table.sql` → 3번 마이그레이션: 게시물 테이블 생성

### 주의사항
- ❌ `V1__init.sql` 후 `V1__second.sql` (같은 버전 중복 불가)
- ❌ 버전 건너뛰기 (V1, V3 빠뜨리고 V4 생성)
- ✅ 순서대로 번호 매기기

---

## 🔄 마이그레이션 프로세스

### 1. 새로운 변경사항 추가

```sql
-- src/main/resources/db/migration/V2__Add_phone_to_users.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### 2. 애플리케이션 실행

```bash
# 개발환경
./gradlew bootRun

# 운영환경
java -jar app.jar --spring.profiles.active=prod
```

**Flyway 자동 실행:**
1. 실행되지 않은 마이그레이션 검색 (V1, V2, ...)
2. 순서대로 실행
3. `flyway_schema_history` 테이블에 기록

### 3. 확인

```bash
# 데이터베이스에서 확인
SELECT * FROM flyway_schema_history;
```

**결과:**
```
version  | description            | type | installed_by | success
---------|------------------------|------|--------------|--------
1        | Create users table     | SQL  | root         | 1
2        | Add phone to users     | SQL  | root         | 1
```

---

## ⚠️ 주의사항

### 1. 이미 존재하는 마이그레이션은 수정 금지

❌ **하면 안 됨:**
```sql
-- V1__Create_users_table.sql (이미 실행됨)
-- 이 파일을 수정하면 Checksum 오류 발생!
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

✅ **대신 새로운 버전 생성:**
```sql
-- V2__Add_phone_to_users.sql (새 파일)
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### 2. 운영 환경에서 조심

**절대 금지:**
```sql
DROP TABLE users;  -- 운영 데이터 날림!
```

**권장:**
- 테이블 삭제 전 백업
- 테스트 환경에서 먼저 실행
- 롤백 계획 수립

### 3. 두 가지 동시 마이그레이션 불가

만약 V3 파일을 여러 명이 동시에 생성하면:
- `V3__Add_column_A.sql`
- `V3__Add_column_B.sql`

❌ **Flyway는 둘 중 하나만 인식하므로 충돌!**

✅ **해결:**
```
V3__Add_column_A.sql
V4__Add_column_B.sql  (버전 증가)
```

---

## 🔧 H2 vs MySQL 호환성

### H2 (개발용) 문법
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### MySQL (운영용) 문법
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### ✅ 두 DB 모두 지원하는 방법
```sql
-- ON UPDATE는 MySQL 문법이지만, 무시하는 DB도 있음
-- 혹은 양쪽 버전으로 분리

-- src/main/resources/db/migration/h2/V1__Create_users_table.sql
-- src/main/resources/db/migration/mysql/V1__Create_users_table.sql
```

---

## 📊 실행 순서 (Spring Boot 시작 시)

```
1. Spring Boot 시작
   ↓
2. Flyway 확인 (enabled: true)
   ↓
3. 미실행 마이그레이션 검색
   ↓
4. 순서대로 실행 (V1, V2, V3, ...)
   ↓
5. flyway_schema_history 테이블 업데이트
   ↓
6. 앱 정상 시작
```

---

## 🚀 유용한 팁

### 1. 마이그레이션 상태 확인 (CLI)

```bash
# Flyway Maven
mvn flyway:info

# Flyway Gradle
gradle flywayInfo
```

### 2. 특정 환경에서만 마이그레이션 비활성화

```yaml
# application-test.yml
spring:
  flyway:
    enabled: false
```

### 3. 마이그레이션 로그 보기

```
2024-12-19 15:30:45 INFO  : Flyway 10.8.1
2024-12-19 15:30:45 INFO  : Successfully validated 2 migrations (execution time 00.004s)
2024-12-19 15:30:45 INFO  : Current version of schema "public": 1
2024-12-19 15:30:45 INFO  : Migrating schema "public" to version 2 - Add phone to users
2024-12-19 15:30:45 INFO  : Successfully applied 1 migration to schema "public"
```

---

## 💡 실무 팁

### 언제 마이그레이션을 새로 만들까?

- ✅ 테이블 생성/삭제
- ✅ 컬럼 추가/삭제/수정
- ✅ 인덱스 생성/삭제
- ✅ 초기 데이터 입력 (INSERT)
- ✅ 제약조건 변경

### 마이그레이션에 쓰지 말아야 할 것

- ❌ 비즈니스 로직 (Java 코드 사용)
- ❌ 응용 프로그램 설정 (application.yml 사용)

---

## 🎓 요약

| 기능 | 방법 |
|------|------|
| **새 컬럼 추가** | `ALTER TABLE` |
| **테이블 생성** | `CREATE TABLE` |
| **마이그레이션 버전** | `V1, V2, V3, ...` |
| **파일명** | `V{n}__{설명}.sql` |
| **자동 실행** | Spring Boot 시작 시 |
| **추적 기록** | `flyway_schema_history` 테이블 |

---

## 🔗 참고 자료

- Flyway 공식: https://flywaydb.org/
- MySQL 문법: https://dev.mysql.com/
- H2 문법: https://h2database.com/