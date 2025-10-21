# Project Guidelines

1. 답변규칙
 - 한글로 답변한다.
 - 명령어 실행은 사용자가 직접한다.

2. 아키텍처
    - DDD

3. 컨벤션
   1) DTO
        - 필드명을 camelCase로 한다.
        - lombok 사용
        - setter 지양
        - 명명규칙
          - Req#{서비스}Dto
    2) Entity
        - 필드명을 snake_case로 한다.
        - setter는 private으로 한다. (생성자에서만 사용)
    3) Controller
        - @RestController를 사용한다.
        - 메서드의 리턴 타입은 ResponseEntity<> 또는 String이다.
        - 모든 비지니스 로직은 Service에서 처리한다.
        - Swagger 작성
    4) Service
        - @Service를 사용한다.
    5) Repository
        - @Repository를 사용한다.
