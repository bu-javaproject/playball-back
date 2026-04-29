-- 매치 테이블
CREATE TABLE match (
    match_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host_id BIGINT NOT NULL,

    title VARCHAR(100) NOT NULL,
    sport_type VARCHAR(30) NOT NULL
        CHECK (sport_type IN ('SOCCER', 'BASKETBALL', 'FUTSAL', 'BASEBALL')),
    
    max_players INT NOT NULL CHECK (max_players >= 2),
    match_date DATETIME NOT NULL,

    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    address VARCHAR(200),
    
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'FULL', 'CLOSED', 'DELETED')),

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_match_host
        FOREIGN KEY (host_id) REFERENCES member(member_id)
);

-- 내가 만든 매치 조회 사용
CREATE INDEX idx_match_host_id ON public_match(host_id);

-- soft delete로 모든 조회에 붙는 조건
CREATE INDEX idx_match_deleted_at ON public_match(deleted_at);

-- 날짜 기준 정렬/필터 조회 시 사용
CREATE INDEX idx_match_date ON public_match(match_date);

-- 위치 기반 조회
CREATE INDEX idx_match_location ON public_match(latitude, longitude);

-- 매치 관리 테이블
CREATE TABLE match_application (
    application_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '신청 ID',

    match_id BIGINT NOT NULL COMMENT '매치 ID',
    member_id BIGINT NOT NULL COMMENT '신청자 ID',

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
        COMMENT '신청 상태',

    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '신청 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '상태 변경 시간',

    CONSTRAINT fk_app_match
        FOREIGN KEY (match_id) REFERENCES public_match(match_id),

    CONSTRAINT fk_app_member
        FOREIGN KEY (member_id) REFERENCES member(member_id),

    CONSTRAINT uq_match_member UNIQUE (match_id, member_id)
);


-- 특정 경기의 신청자 조회
CREATE INDEX idx_app_match_id ON match_application(match_id);

-- 내가 신청한 경기 조회
CREATE INDEX idx_app_member_id ON match_application(member_id);

-- 경기의 신청자 중 대기중인 사람만 보기
CREATE INDEX idx_app_match_status ON match_application(match_id, status);

-- 참여 확정된 경기 목록
CREATE INDEX idx_app_member_status ON match_application(member_id, status);