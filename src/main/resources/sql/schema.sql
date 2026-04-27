
-- 1. member (회원가입)
CREATE TABLE member (
    member_id        BIGINT       AUTO_INCREMENT PRIMARY KEY,
    kakao_id         BIGINT       UNIQUE,
    email            VARCHAR(100) UNIQUE,
    nickname         VARCHAR(20)  UNIQUE,
    name             VARCHAR(50),
    phone            VARCHAR(20),
    gender           VARCHAR(2),
    age              INT,
    profile_image    VARCHAR(500),
    skill_level      VARCHAR(20)  DEFAULT 'BEGINNER',
    preferred_position VARCHAR(30),
    latitude         DOUBLE,
    longitude        DOUBLE,
    address          VARCHAR(200),
    role             VARCHAR(10)  DEFAULT 'USER',
    signup_completed TINYINT(1)   DEFAULT 0,
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. favorite_sport (종목 여러 개 선택 가능)
CREATE TABLE favorite_sport (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    sport_type       VARCHAR(30)  NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
    UNIQUE KEY uk_member_sport (member_id, sport_type)
);

-- 3. refresh_token (로그인 유지용)
CREATE TABLE refresh_token (
    token_id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    token            VARCHAR(255) NOT NULL UNIQUE,
    expiry_date      DATETIME     NOT NULL,
    created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);