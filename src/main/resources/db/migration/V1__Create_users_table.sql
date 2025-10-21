-- 회원 테이블 생성
CREATE TABLE user
(
    user_id                   INT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 번호',
    login_email               VARCHAR(512) NOT NULL COMMENT '이메일',
    provider                  VARCHAR(32)  NOT NULL COMMENT '소셜 로그인 제공자 (e.g., NAVER)',
    provider_id               VARCHAR(255) NOT NULL COMMENT '소셜 로그인 제공자의 고유 ID',
    nickname                  VARCHAR(50)  NOT NULL COMMENT '닉네임',
    profile_image_url         VARCHAR(2048) COMMENT '프로필 이미지 URL',
    user_role                 VARCHAR(32)  COMMENT '사용자 권한',
    quited                    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '탈퇴 여부',
    create_at                 DATETIME     COMMENT '생성 일시',
    update_at                 DATETIME     COMMENT '변경 일시',
    quit_at                   DATETIME     COMMENT '탈퇴 일시',
    UNIQUE KEY uk_member_provider (provider, provider_id),
    UNIQUE KEY uk_member_nickname (nickname)
) COMMENT '회원 테이블';