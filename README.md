# Backend Base (Spring Boot, Java 17)

세션리스 JWT 기반의 백엔드 스타터 프로젝트입니다. Swagger UI가 포함되어 있으며, 간단한 로그인 API와 보호/공개 엔드포인트 샘플을 제공합니다.

## 기술 스택
- Java 17
- Spring Boot 3
- Gradle
- Spring Security (Stateless)
- JWT (jjwt)
- Swagger/OpenAPI (springdoc)
- Lombok
- MyBatis
- Spring Data JPA
- Spring for Apache Kafka

## 구성
- POST /api/auth/login: 데모 사용자(user/password) 인증 후 access/refresh JWT 발급
- POST /api/auth/refresh: refresh 토큰으로 토큰 재발급(access/refresh)
- GET /api/public/hello: 공개 엔드포인트
- GET /api/secure/hello: JWT 필요(Authorization: Bearer <accessToken>)
- Swagger UI: /swagger-ui

## 아키텍처: DDD 패키지 구조
- interfaces: 외부 입출력 어댑터(Controller 등)
  - com.example.backend.interfaces.auth.AuthController
  - com.example.backend.interfaces.common.HelloController
- application: 유스케이스/서비스 계층
  - com.example.backend.application.auth.AuthService
- domain: 도메인 엔티티/리포지토리 인터페이스(현재 샘플 없음)
- infrastructure: 기술 구현(보안/설정/외부시스템 등)
  - com.example.backend.infrastructure.security.JwtUtil, JwtAuthenticationFilter
  - com.example.backend.infrastructure.config.SecurityConfig

참고: 최소 변경 원칙으로 물리적 파일 이동 없이 package 네임스페이스만 변경했습니다(Gradle 컴파일에는 영향 없음). 운영 시에는 소스 디렉터리 구조도 동일하게 정리하는 것을 권장합니다.

## 설정(application.yml)
- JWT 시크릿과 만료시간을 설정합니다.
- 운영환경에서는 환경변수로 JWT_SECRET을 주입하세요.
- 기본 DB는 개발 편의를 위해 H2 인메모리로 설정되어 있습니다. 운영 DB 사용 시 spring.datasource, JPA 설정을 교체하세요.
- Kafka는 환경변수 KAFKA_BOOTSTRAP_SERVERS 로 브로커 주소를 주입할 수 있습니다.
- MyBatis는 classpath*:mapper/**/*.xml 경로의 매퍼 XML을 자동 인식합니다.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:demo;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
mybatis:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
app:
  jwt:
    secret: ${JWT_SECRET:change-me-in-production}
    expiration-seconds: 3600
```

## 실행 방법
사용자가 직접 실행합니다.
1) Gradle Wrapper 생성(최초 1회)
- Windows PowerShell: gradle wrapper
2) 빌드 및 실행
- ./gradlew bootRun (Windows: .\gradlew.bat bootRun)
3) 확인
- Swagger UI: http://localhost:8080/swagger-ui
- 로그인: POST http://localhost:8080/api/auth/login  {"username":"user","password":"password"}
  - 응답 예시: {"accessToken":"...","refreshToken":"..."}
- 리프레시: POST http://localhost:8080/api/auth/refresh  {"refreshToken":"<로그인 응답의 refreshToken>"}
  - 응답 예시: {"accessToken":"...","refreshToken":"..."}
- 보호 엔드포인트: GET http://localhost:8080/api/secure/hello (Authorization: Bearer <accessToken>)

## 주의
- DEMO 사용자 및 비밀번호는 예시입니다. 실제 서비스에서는 사용자 저장소(DB/외부 IDP)와 연동하고, 권한/역할 관리를 구현하세요.
- CSRF는 API 특성상 비활성화되어 있습니다.
- 세션은 STATELESS 모드입니다.

## 추가 논의 필요(질문)
- 데이터베이스 선정: (MySQL/PostgreSQL/others?) 및 마이그레이션 도구(Flyway/Liquibase).
- 빌드/배포 환경: Docker/Docker Compose 필요 여부, CI/CD(예: GitHub Actions) 구성.
- 환경 분리: dev/stage/prod yml 분리 여부, 프로필 전략.
- 로깅: Logback 설정, 추적ID(correlation id), JSON 로깅 필요 여부.
- CORS 정책: 허용 도메인/메소드/헤더 정의.
- 모니터링/헬스체크: /actuator 필요 여부.
- 도메인 패키지 구조 합의: 계층형 vs 모듈러 패키징.
- 코드 스타일/품질: Checkstyle/Spotless, 테스트 프레임워크(JUnit5, Testcontainers) 범위.
