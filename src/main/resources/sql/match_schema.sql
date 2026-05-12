-- ----------------------------------------------------------
-- 1. match : 경기 본체
-- ----------------------------------------------------------
CREATE TABLE `match` (
                         match_id    BIGINT       AUTO_INCREMENT PRIMARY KEY,
                         host_id     BIGINT       NULL          COMMENT '주최자 (탈퇴 시 NULL)',

                         title       VARCHAR(100) NOT NULL      COMMENT '경기 제목',
                         sport_type  VARCHAR(30)  NOT NULL      COMMENT '종목'
        CHECK (sport_type IN ('SOCCER', 'BASKETBALL', 'FUTSAL', 'BASEBALL')),

                         max_players INT          NOT NULL      COMMENT '최대 인원'
        CHECK (max_players >= 2),
                         match_date  DATETIME     NOT NULL      COMMENT '경기 일시',

                         latitude    DOUBLE       NOT NULL,
                         longitude   DOUBLE       NOT NULL,
                         address     VARCHAR(200),

                         status      VARCHAR(20)  NOT NULL DEFAULT 'OPEN'    COMMENT '경기 상태'
        CHECK (status IN ('OPEN', 'FULL', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),

                         created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_match_host
                             FOREIGN KEY (host_id) REFERENCES member(member_id)
                                 ON DELETE SET NULL
);


-- 내가 만든 매치 조회용
CREATE INDEX idx_match_host_id   ON `match`(host_id);

-- 날짜 기준 정렬/필터
CREATE INDEX idx_match_date      ON `match`(match_date);

-- 위치 기반 조회
CREATE INDEX idx_match_location  ON `match`(latitude, longitude);

-- 상태별 필터 (예: OPEN 만 보기)
CREATE INDEX idx_match_status    ON `match`(status);


-- ----------------------------------------------------------
-- 2. match_application : 경기 신청
-- ----------------------------------------------------------
CREATE TABLE match_application (
                                   application_id BIGINT      AUTO_INCREMENT PRIMARY KEY COMMENT '신청 ID',
                                   match_id       BIGINT      NOT NULL                   COMMENT '매치 ID',
                                   member_id      BIGINT      NOT NULL                   COMMENT '신청자 ID',

                                   status         VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '신청 상태'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),

                                   applied_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       COMMENT '신청 시간',
                                   updated_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       ON UPDATE CURRENT_TIMESTAMP
                                COMMENT '상태 변경 시간',

                                   CONSTRAINT fk_app_match
                                       FOREIGN KEY (match_id) REFERENCES `match`(match_id)
                                           ON DELETE CASCADE,
                                   CONSTRAINT fk_app_member
                                       FOREIGN KEY (member_id) REFERENCES member(member_id)
                                           ON DELETE CASCADE,
                                   CONSTRAINT uq_match_member UNIQUE (match_id, member_id)
);


-- 특정 경기의 신청자 조회
CREATE INDEX idx_app_match_id      ON match_application(match_id);

-- 내가 신청한 경기 조회
CREATE INDEX idx_app_member_id     ON match_application(member_id);

-- 경기의 대기중 신청자 조회
CREATE INDEX idx_app_match_status  ON match_application(match_id, status);

-- 내 참여 확정 경기 조회
CREATE INDEX idx_app_member_status ON match_application(member_id, status);