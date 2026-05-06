-- ---------------------------------------------------------------------
-- 1. notification : 알림 본체 (1 row = 1 알림)
-- ---------------------------------------------------------------------
CREATE TABLE notification (
                              notification_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
                              member_id       BIGINT       NOT NULL,
                              notice_type     VARCHAR(30)  NOT NULL
                                  CHECK (notice_type IN (
                                                         'MATCH_FOUND',
                                                         'APPLICATION_REJECTED',
                                                         'MATCH_REMINDER',
                                                         'MATCH_CANCELLED',
                                                         'RATING_REQUEST',
                                                         'SYSTEM_NOTICE'
                                      )),
                              title           VARCHAR(100) NOT NULL,
                              content         VARCHAR(500) NOT NULL,
                              is_read         TINYINT(1)   NOT NULL DEFAULT 0,
                              target_type     VARCHAR(20)  NULL,
                              target_id       BIGINT       NULL,
                              created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- 내 알림 최신순 조회용
CREATE INDEX idx_notification_member_created
    ON notification(member_id, created_at DESC);

-- 안 읽은 알림 카운트 (ex: 빨간 점 N개)
CREATE INDEX idx_notification_member_unread
    ON notification(member_id, is_read);

-- ---------------------------------------------------------------------
-- 2. notification_setting : 사용자별 알림 ON/OFF (1:1)
-- ---------------------------------------------------------------------
CREATE TABLE notification_setting (
                                      member_id  BIGINT     NOT NULL PRIMARY KEY,
                                      enabled    TINYINT(1) NOT NULL DEFAULT 1,
                                      updated_at DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP,

                                      FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 3. device_token : FCM 푸시 토큰 (1 사용자 = N 기기)
-- ---------------------------------------------------------------------
CREATE TABLE device_token (
                              device_token_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
                              member_id       BIGINT       NOT NULL,
                              token           VARCHAR(255) NOT NULL UNIQUE,
                              platform        VARCHAR(10)  NOT NULL
                                  CHECK (platform IN ('ANDROID', 'IOS', 'WEB')),
                              created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,

                              FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);

-- 한 사용자의 모든 기기 토큰 조회
CREATE INDEX idx_device_token_member ON device_token(member_id);

-- ---------------------------------------------------------------------
-- 4. compliment : 칭찬 1건 (rater → ratee, 1 경기당 1회)
-- ---------------------------------------------------------------------
CREATE TABLE compliment (
                            compliment_id BIGINT       AUTO_INCREMENT PRIMARY KEY,
                            match_id      BIGINT       NOT NULL,
                            rater_id      BIGINT       NOT NULL,
                            ratee_id      BIGINT       NOT NULL,
                            comment       VARCHAR(200),
                            created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT chk_no_self_compliment CHECK (rater_id <> ratee_id),
                            FOREIGN KEY (rater_id) REFERENCES member(member_id) ON DELETE CASCADE,
                            FOREIGN KEY (ratee_id) REFERENCES member(member_id) ON DELETE CASCADE,
    -- 매치 도메인 안정화 후 활성화:
    -- FOREIGN KEY (match_id) REFERENCES `match`(match_id) ON DELETE CASCADE,
                            UNIQUE KEY uk_compliment_match_rater_ratee (match_id, rater_id, ratee_id)
);

-- 받은 칭찬 조회용
CREATE INDEX idx_compliment_ratee ON compliment(ratee_id);

-- ---------------------------------------------------------------------
-- 5. compliment_tag : 한 칭찬에 붙은 태그들 (1:N)
-- ---------------------------------------------------------------------
CREATE TABLE compliment_tag (
                                compliment_tag_id BIGINT      AUTO_INCREMENT PRIMARY KEY,
                                compliment_id     BIGINT      NOT NULL,
                                tag               VARCHAR(20) NOT NULL
                                    CHECK (tag IN (
                                                   'MANNERS',
                                                   'SKILL',
                                                   'PUNCTUAL',
                                                   'PASSIONATE',
                                                   'MOOD_MAKER'
                                        )),

                                FOREIGN KEY (compliment_id) REFERENCES compliment(compliment_id) ON DELETE CASCADE,
                                UNIQUE KEY uk_compliment_tag (compliment_id, tag)
);

-- ---------------------------------------------------------------------
-- 6. member_compliment_summary : 회원별 태그별 누적 카운트 (캐시)
-- ---------------------------------------------------------------------
CREATE TABLE member_compliment_summary (
                                           member_id  BIGINT      NOT NULL,
                                           tag        VARCHAR(20) NOT NULL
                                               CHECK (tag IN (
                                                              'MANNERS',
                                                              'SKILL',
                                                              'PUNCTUAL',
                                                              'PASSIONATE',
                                                              'MOOD_MAKER'
                                                   )),
                                           count      INT         NOT NULL DEFAULT 0,
                                           updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                               ON UPDATE CURRENT_TIMESTAMP,

                                           PRIMARY KEY (member_id, tag),
                                           FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
);