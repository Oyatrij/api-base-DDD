# Spring Boot Actuator 설정 가이드

## 개요

Spring Boot Actuator는 애플리케이션의 상태를 모니터링하고 관리할 수 있는 도구입니다.
프로덕션 환경에서의 가시성(Observability)을 제공합니다.

---

## 🎯 설정 내용

### 의존성 추가
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

### 보안 규칙
- `/actuator/health` → 모두 접근 가능 (모니터링 용)
- `/actuator/health/**` → 모두 접근 가능
- `/actuator/**` → JWT 인증 필요

---

## 🔧 환경별 설정

### 개발 환경 (application-dev.yml)
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # 모든 엔드포인트 노출
  endpoint:
    health:
      show-details: always  # 항상 상세 정보 표시
```

**노출 엔드포인트:**
- health, metrics, env, beans, threaddump, loggers, configprops 등 **모두 가능**

### 운영 환경 (application-prod.yml)
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus  # 필요한 것만 노출
  endpoint:
    health:
      show-details: when-authorized  # 인증된 사용자에게만 상세 정보 표시
```

**노출 엔드포인트:**
- health, metrics, prometheus **만 노출**

---

## 📋 주요 Actuator 엔드포인트

### 1. Health Check
```
GET /actuator/health
```

응답 (인증 없음):
```json
{
  "status": "UP"
}
```

응답 (상세 정보, 인증 필요):
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP", "details": {...}},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 2. 메트릭스
```
GET /actuator/metrics
```

가능한 메트릭:
- `jvm.memory.used` - JVM 메모리 사용량
- `jvm.threads.live` - 실행 중인 스레드 수
- `process.cpu.usage` - CPU 사용률
- `http.server.requests` - HTTP 요청 통계

특정 메트릭 조회:
```
GET /actuator/metrics/jvm.memory.used
```

응답:
```json
{
  "name": "jvm.memory.used",
  "description": "The amount of used memory",
  "baseUnit": "bytes",
  "measurements": [
    {"statistic": "VALUE", "value": 123456789}
  ]
}
```

### 3. 환경 변수
```
GET /actuator/env
```

특정 환경 변수 조회:
```
GET /actuator/env/spring.datasource.url
```

### 4. Spring Beans
```
GET /actuator/beans
```

모든 Spring Bean 목록 조회

### 5. 로거 설정 (실시간 변경!)
```
GET /actuator/loggers
```

현재 로그 레벨 확인:
```
GET /actuator/loggers/com.example.backend
```

로그 레벨 변경:
```
POST /actuator/loggers/com.example.backend
Content-Type: application/json

{
  "configuredLevel": "DEBUG"
}
```

### 6. 스레드 덤프
```
GET /actuator/threaddump
```

현재 모든 스레드의 상태 확인

### 7. 설정 프로퍼티
```
GET /actuator/configprops
```

### 8. Prometheus 메트릭
```
GET /actuator/prometheus
```

Prometheus 형식으로 메트릭 내보내기

---

## 💻 실제 사용 예시

### 예시 1: 앱이 정상 가동 중인지 확인
```bash
curl http://localhost:8080/actuator/health
```

### 예시 2: 메모리 사용량 확인
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/actuator/metrics/jvm.memory.used
```

### 예시 3: 현재 로그 레벨 조회 (인증 필요)
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/actuator/loggers/com.example.backend
```

### 예시 4: 로그 레벨을 DEBUG로 변경 (재시작 불필요!)
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}' \
  http://localhost:8080/actuator/loggers/com.example.backend
```

### 예시 5: Prometheus로 메트릭 수집
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/actuator/prometheus
```

---

## 🛡️ 보안 고려사항

### 1. Health는 왜 누구나 접근할 수 있나?
- 로드 밸런서나 모니터링 시스템이 앱 상태를 확인하는 데 사용
- 민감한 정보는 포함되지 않음

### 2. 다른 엔드포인트는 인증이 필요한 이유
- 시스템 정보 (메모리, 스레드, Bean 목록 등) 노출 위험
- 로그 레벨 실시간 변경 가능
- 보안상 민감한 정보 포함

### 3. 운영 환경에서는 더 제한적
- 필요한 엔드포인트만 노출 (health, metrics, prometheus)
- 나머지는 비활성화

---

## 📊 모니터링 시스템과 연동

### Prometheus 연동
1. Prometheus 설정에 추가:
```yaml
scrape_configs:
  - job_name: 'backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
    # 선택사항: JWT 토큰으로 인증
    # authorization:
    #   type: Bearer
    #   credentials: 'YOUR_TOKEN'
```

2. Grafana로 시각화

### 헬스체크 연동
로드 밸런서가 주기적으로 `/actuator/health` 호출하여 앱 상태 확인

---

## ⚙️ 커스텀 헬스 인디케이터 만들기

`HealthIndicator` 구현으로 커스텀 헬스 체크 추가 가능:

```java
@Component
public class DatabaseConnectionHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            // DB 연결 테스트
            // ...
            builder.up()
                   .withDetail("database", "MySQL 8.0")
                   .withDetail("connection_pool", "20/20");
        } catch (Exception e) {
            builder.down()
                   .withDetail("error", e.getMessage());
        }
    }
}
```

호출:
```
GET /actuator/health/databaseConnectionHealthIndicator
```

---

## 🚨 주의사항

### 개발 환경
- `show-details: always` → 모든 정보 노출
- `include: "*"` → 모든 엔드포인트 활성화
- 테스트와 디버깅에 유용

### 운영 환경
- `show-details: when-authorized` → 인증된 사용자만 상세 정보 확인
- `include: health,metrics,prometheus` → 필수 엔드포인트만 노출
- **절대로 민감한 환경 변수를 표시하면 안 됨**

---

## 📈 권장 모니터링 메트릭

```yaml
# 필수 모니터링
- http.server.requests          # API 응답 시간, 요청 수
- jvm.memory.used               # JVM 메모리
- jvm.threads.live              # 스레드 수
- process.cpu.usage             # CPU 사용률
- system.load.average.1m        # 시스템 로드

# DB 성능
- hikaricp.connections          # DB 연결 풀
- hikaricp.connections.active   # 활성 연결 수
```

---

## 🔗 참고 자료

- [Spring Boot Actuator 공식 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Prometheus 메트릭 형식](https://prometheus.io/docs/instrumenting/exposition_formats/)
- [Spring Boot Health Indicators](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health)

---

## ✅ 체크리스트

- [ ] Actuator 의존성 추가됨
- [ ] 개발 환경: 모든 엔드포인트 노출
- [ ] 운영 환경: 필수 엔드포인트만 노출
- [ ] JWT 인증 설정됨
- [ ] Health는 인증 없이 접근 가능
- [ ] 모니터링 시스템과 연동 테스트됨