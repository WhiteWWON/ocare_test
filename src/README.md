테이블 생성 스크립트 입니다.

-- 1. 회원 마스터 테이블 (OC_MEMBER)

CREATE TABLE `OC_MEMBER` (
    `member_id`      BIGINT          NOT NULL AUTO_INCREMENT             COMMENT '회원 고유 ID',
    `name`           VARCHAR(100)    NULL                                COMMENT '이름',
    `email`          VARCHAR(100)    NOT NULL                            COMMENT '이메일 (로그인 식별자)',
    `nickname`       VARCHAR(200)    NULL                                COMMENT '닉네임',
    `password`       VARCHAR(2000)   NOT NULL                            COMMENT '패스워드 (Bcrypt 암호화)',
    `last_login_dt`  DATETIME        NULL DEFAULT CURRENT_TIMESTAMP      COMMENT '최종 로그인 일시',
    `login_fail_cnt` INT(10)         NULL DEFAULT 0                      COMMENT '로그인 실패 횟수 (5회 초과 시 차단 로직용)',
    `created_dt`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '데이터 생성 일시',
    `created_id`     VARCHAR(50)     NOT NULL                            COMMENT '생성자 ID',
    `updated_dt`     DATETIME        NULL                                COMMENT '데이터 수정 일시',
    `updated_id`     VARCHAR(50)     NULL                                COMMENT '수정자 ID',
    
    /* [설계 포인트] 
       1. N,000만 건 규모에서 이메일 중복 체크 및 로그인을 위해 UK(Unique Key) 설정하였습니다.
       2. MySQL 파티셔닝 제약(UK에 파티션 키 포함 필수)으로 인해, 
          로그인 무결성을 DB 레벨에서 보장하기 위해 회원 테이블은 파티셔닝을 적용하지 않았습니다.
    */
    PRIMARY KEY (`member_id`),                                            
    UNIQUE KEY `UK_MEMBER_EMAIL` (`email`),                               
    INDEX `IDX_MEMBER_NICKNAME` (`nickname`),                             
    INDEX `IDX_MEMBER_CREATED` (`created_dt`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 2. 헬스 레코드 등록 마스터 (OC_HEALTH_RECORD_MASTER)

CREATE TABLE `OC_HEALTH_RECORD_MASTER` (
    `rc_master_id`      BIGINT          NOT NULL AUTO_INCREMENT            COMMENT '마스터 고유 ID',
    `member_id`         BIGINT          NOT NULL                           COMMENT '회원ID (OC_MEMBER 참조)',
    `record_key`        VARCHAR(100)    NOT NULL                           COMMENT '레코드 고유 키 (UUID 등)',
    `record_type`       VARCHAR(1)      NOT NULL                           COMMENT '레코드 타입 (S:steps 등)',
    `sc_mode`           INT(2)          NULL                               COMMENT '출처 모드',
    `sc_product_name`   VARCHAR(20)     NULL                               COMMENT '출처 기기명',
    `sc_product_vender` VARCHAR(20)     NULL                               COMMENT '출처 제조사',
    `sc_app_name`       VARCHAR(20)     NULL                               COMMENT '출처 앱 이름',
    `record_total_cnt`  INT             NULL DEFAULT 0                     COMMENT '포함된 상세 데이터 총 개수',
    `lastupdate_dt`     DATETIME        NOT NULL                           COMMENT '클라이언트 최종 수정 일시',
    `created_dt`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '데이터 생성 일시',
    `created_id`        VARCHAR(50)     NOT NULL                           COMMENT '생성자 ID',
    `updated_dt`        DATETIME        NULL                               COMMENT '데이터 수정 일시',
    `updated_id`        VARCHAR(50)     NULL                               COMMENT '수정자 ID',

    /* [설계 포인트]
       1. PK 및 UK에 member_id를 포함하여 파티셔닝 제약을 해결함과 동시에 유저별 데이터 정합성을 고려했습니다.
       2. IDX_MEMBER_TYPE_UPDATE: 특정 유저의 최신 레코드 등록 내역을 빠르게 조회하기 위한 복합 인덱스입니다.
    */
    PRIMARY KEY (`rc_master_id`, `member_id`),
    UNIQUE KEY `UK_RECORD_KEY_MEMBER` (`record_key`, `member_id`),
    INDEX `IDX_MEMBER_TYPE_UPDATE` (`member_id`, `record_type`, `lastupdate_dt` DESC)
) 
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

/* [파티셔닝] 
   1. N,000만 명의 업로드 세션을 분산하기 위해 member_id 기준 HASH 파티셔닝을 적용합니다.
   2. 64개 파티션은 각 파티션당 인덱스 크기를 적절히 유지하여 메모리(Buffer Pool) 효율을 극대화합니다.
*/
PARTITION BY HASH(member_id)
PARTITIONS 64;



-- 3. 헬스 레코드 상세 정보 (OC_HEALTH_RECORD_DETAIL)

CREATE TABLE `OC_HEALTH_RECORD_DETAIL` (
    `rc_detail_id`   BIGINT          NOT NULL AUTO_INCREMENT COMMENT '상세 레코드 고유 ID',
    `rc_master_id`   BIGINT          NOT NULL                COMMENT '마스터 테이블 참조 ID',
    `member_id`      BIGINT          NOT NULL                COMMENT '회원 고유 ID (조회 최적화용 역정규화)',
    `steps`          DECIMAL         NOT NULL                COMMENT '걸음수',
    `period_from`    DATETIME(3)     NOT NULL                COMMENT '측정 시작 일시 (밀리초 정밀도)',
    `period_to`      DATETIME(3)     NOT NULL                COMMENT '측정 종료 일시 (밀리초 정밀도)',
    `distance_unit`  VARCHAR(10)     NULL                    COMMENT '거리 단위 (km, m 등)',
    `distance_value` DOUBLE          NULL                    COMMENT '거리 값',
    `calories_unit`  VARCHAR(10)     NULL                    COMMENT '칼로리 단위 (kcal 등)',
    `calories_value` DOUBLE          NULL                    COMMENT '칼로리 값',
    `created_dt`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '시스템 등록 일시',
    `created_id`     VARCHAR(50)     NOT NULL                COMMENT '등록자 ID',
    `updated_dt`     DATETIME        NULL                    COMMENT '시스템 수정 일시',
    `updated_id`     VARCHAR(50)     NULL                    COMMENT '수정자 ID',

    /* [PK 및 파티셔닝 전략]
       - 파티션 키인 period_from을 PK에 포함하여 일별 파티셔닝이 가능하도록 구성했습니다.
    */
    PRIMARY KEY (`rc_detail_id`, `period_from`),
	
	/* [핵심] UNIQUE INDEX 추가 
     마스터ID + 시작시간(+필요시 종료시간) 조합으로 중복 적재를 방지
	*/
	UNIQUE KEY `UK_DETAIL_DUPLICATE` (`rc_master_id`, `period_from`, `period_to`),

    /* [인덱스 전략]
       1. IDX_DETAIL_MASTER_PERIOD: 마스터 테이블과 조인하여 이어서 저장(Resume)하거나, 특정 등록 세션의 데이터를 조회할 때 사용합니다.
       2. IDX_DETAIL_MEMBER_PERIOD: 특정 회원의 기간별 걸음수 합산/통계 조회 시 사용될 인덱스입니다.
    */
    INDEX `IDX_DETAIL_MASTER_PERIOD` (`rc_master_id`, `period_from`),
    INDEX `IDX_DETAIL_MEMBER_PERIOD` (`member_id`, `period_from`)
) 
ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci

/* [파티셔닝] 
   1. RANGE 파티셔닝: 일일 수백억 건의 데이터를 관리한다는 가정으로 고려했습니다.
   2. p_max: 자동 파티션 생성 프로시저가 동작하기 전까지 데이터가 유실되지 않도록 보장하는 안전장치입니다.
*/
PARTITION BY RANGE (TO_DAYS(period_from)) (
    PARTITION p20250107 VALUES LESS THAN (TO_DAYS('2025-01-08')),
    PARTITION p20250108 VALUES LESS THAN (TO_DAYS('2025-01-09')),
	PARTITION p20250109 VALUES LESS THAN (TO_DAYS('2025-01-10')),
	PARTITION p20250110 VALUES LESS THAN (TO_DAYS('2025-01-11')),
	PARTITION p20250111 VALUES LESS THAN (TO_DAYS('2025-01-12')),
	PARTITION p20250112 VALUES LESS THAN (TO_DAYS('2025-01-13')),
	PARTITION p20250113 VALUES LESS THAN (TO_DAYS('2025-01-14')),
	PARTITION p20250114 VALUES LESS THAN (TO_DAYS('2025-01-15')),
	PARTITION p20250115 VALUES LESS THAN (TO_DAYS('2025-01-16')),
	PARTITION p20250116 VALUES LESS THAN (TO_DAYS('2025-01-17')),
	PARTITION p20250117 VALUES LESS THAN (TO_DAYS('2025-01-18')),
	PARTITION p20250118 VALUES LESS THAN (TO_DAYS('2025-01-19')),
	PARTITION p20250119 VALUES LESS THAN (TO_DAYS('2025-01-20')),
	PARTITION p20250120 VALUES LESS THAN (TO_DAYS('2025-01-21')),
	PARTITION p20250121 VALUES LESS THAN (TO_DAYS('2025-01-22')),
	PARTITION p20250122 VALUES LESS THAN (TO_DAYS('2025-01-23')),
	PARTITION p20250123 VALUES LESS THAN (TO_DAYS('2025-01-24')),
	PARTITION p20250124 VALUES LESS THAN (TO_DAYS('2025-01-25')),
	PARTITION p20250125 VALUES LESS THAN (TO_DAYS('2025-01-26')),
	PARTITION p20250126 VALUES LESS THAN (TO_DAYS('2025-01-27')),
	PARTITION p20250127 VALUES LESS THAN (TO_DAYS('2025-01-28')),
	PARTITION p20250128 VALUES LESS THAN (TO_DAYS('2025-01-29')),
	PARTITION p20250129	VALUES LESS THAN (TO_DAYS('2025-01-30')),
    /*프로시저를 통해 자동 생성될 미래 파티션의 예비 저장소*/
    PARTITION p_max VALUES LESS THAN MAXVALUE 
);



--ERD 입니다.
<img width="1324" height="752" alt="image" src="https://github.com/user-attachments/assets/33b2ebbb-d551-4bf4-baa8-83d02d57526e" />
