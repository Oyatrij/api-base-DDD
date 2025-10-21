-- V2__Add_phone_to_users.sql
-- 사용자 테이블에 휴대폰 컬럼 추가 (선택사항)

ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP NULL;

-- 인덱스 추가
CREATE INDEX idx_phone ON users(phone);