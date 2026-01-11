**1. DB ERD 구조도 입니다.**
<img width="1239" height="769" alt="image" src="https://github.com/user-attachments/assets/514bea9c-967b-49f6-8914-1da0ae1c50c8" />
링크 : https://www.erdcloud.com/d/RS4qAP2dZqLZNyZ3E

**2. DB ERD 설명입니다.**

1) OC_MEMBER (회원) : 회원 저장용 마스터 테이블입니다.
- 컬럼 설명:
. 회원ID(member_id) : PK로 설정하여, mysql auto_increment 기능으로 자동채번 합니다.
. 이메일(email): unique key로 설정하였습니다.
. 비밀번호(password) : 암호화된 값을 저장하도록 했습니다. (스프링 시큐리티 활용)
. 로그인 실패횟수(login_fail_cnt) : 로그잇 패스워드 입력 불일치 시, 실패횟수를 +카운팅하여 저장하는 용도입니다.


2) OC_HEALTH_RECORD_MASTER (헬스 레코드 마스터) : 오케어 헬스케어 레코드 마스터 테이블입니다.
- 테이블 관계 : OC_MEMBER (회원) 테이블과 1:1 관계입니다. (record_key를 사용자 유일키로 이해했습니다.)
- 컬럼 설명:
. 레코드 마스터ID(rc_master_id) : PK로 설정하여, mysql auto_increment 기능으로 자동채번 합니다.
. 레코드키(record_key) : unique key로 설정하였습니다. 
  -> PK로 잡지 않은 이유는, OC_HEALTH_RECORD_DETAIL테이블과의 조인시 성능, 데이터 등록시 auto_increment 가 더 나은 성능 등의 이유로 unique key로만 설정 했습니다.
. 레코드타입(record_type) : json 예시 > "type" 값을 저장합니다.
. 출처MODE(sc_mode) : json 예시 > data > "source" > "mode" 값을 저장합니다.
. 출처제품명(sc_product_name) : json 예시 > data > "source" > "product" > "name" 값을 저장합니다.
. 출처벤더명(sc_product_vender) : json 예시 > data > "source" "product" > "vender" 값을 저장합니다.
. 출처APP명(sc_app_name) : json 예시 > data > "source" > "name" 값을 저장합니다.
. 메모(memo) : json 예시 > data > "memo' 값을 저장합니다.
. 레코드총건수(record_total_cnt)와, 최종수정일시(lastupdate_dt)는 JSON key로는 없지만, 정보활용 및 검증에 이용하기 위해 별도로 구성해봤습니다.


3) OC_HEALTH_RECORD_DETAIL 헬스 레코드 상세 ( entries ) : 오케어 헬스케어 레코드 상세 테이블입니다.
- 테이블 관계 : OC_HEALTH_RECORD_MASTER 테이블과 1: N 관계입니다.
- 컬럼 설명: json 예시 > "entries" 리스트의 데이터를 저장합니다. (json key 값으로 식별 가능하도록 컬럼명을 각각 구성했습니다.)
- OC_MEMBER (회원) 테이블의 '회원ID', OC_HEALTH_RECORD_MASTER 테이블의 '레코드마스터ID' 컬럼을 참조할 수 있게 구성했습니다.(외래키로 잡진 않았습니다.)


4) OC_HEALTH_DAILY_STAT(일별 레코드 집계) : 회원별 일별 레코드 집계 저장용 테이블입니다. 
-> 각 회원들에게 일별 집계 데이터를 조회 API 에 사용하기 위한 용도입니다. 
(OC_HEALTH_RECORD_DETAIL 로도 제공이 가능하지만, 회원수가 증가하고, 트래픽이 증가할 것을 대비하여 별도 구성을 생각했습니다.)
- 테이블 관계 : OC_MEMBER (회원) 테이블과 1: N 관계입니다.
- 컬럼 설명: 레코드일자(record_date)와 레코드키(record_key) 2개 컬럼을 PK로 구성했습니다.
. 걸음수일합계(steps_daily_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,걸음수(steps) 합계 값을 저장합니다.
. 거리일합계(distance_daily_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,거리값(distance_value) 합계 값을 저장합니다.
. 칼로리일합계(calories_daily_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,칼로리값(calrories_value) 합계 값을 저장합니다.


5) OC_HEALTH_MONTHLY_STAT(월별 레코드 집계) : 회원별 월별 레코드 집계 저장용 테이블입니다. 
-> 각 회원들에게 월별 집계 데이터를 조회 API 에 사용하기 위한 용도입니다. 
(OC_HEALTH_RECORD_DETAIL 또는 OC_HEALTH_MONTHLY_STAT 로도 제공이 가능하지만, 회원수가 증가하고, 트래픽이 증가할 것을 대비하여 별도 구성을 생각했습니다.)
- 테이블 관계 : OC_MEMBER (회원) 테이블과 1: N 관계입니다.
- 컬럼 설명: 레코드월(record_month)와 레코드키(record_key) 2개 컬럼을 PK로 구성했습니다.
. 걸음수월합계(steps_month_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,걸음수(steps) 합계 값을 저장합니다.
. 거리월합계(distance_month_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,거리값(distance_value) 합계 값을 저장합니다.
. 칼로리월합계(calories_month_sum) : OC_HEALTH_RECORD_DETAIL 테이블의 회원별,일자별,칼로리값(calrories_value) 합계 값을 저장합니다.





**3. DB DDL 스크립트입니다.  (파티션 구성 및 인덱스 설정 내용 덧붙였습니다.)**
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
    `steps`          decimal(10,0)   NOT NULL                COMMENT '걸음수',
    `period_from`    DATETIME(3)     NOT NULL                COMMENT '측정 시작 일시 (밀리초 정밀도)',
    `period_to`      DATETIME(3)     NOT NULL                COMMENT '측정 종료 일시 (밀리초 정밀도)',
    `distance_unit`  VARCHAR(10)     NULL                    COMMENT '거리 단위 (km, m 등)',
    `distance_value` decimal(15,5)   NULL                    COMMENT '거리 값',
    `calories_unit`  VARCHAR(10,2)   NULL                    COMMENT '칼로리 단위 (kcal 등)',
    `calories_value` decimal(10,0)   NULL                    COMMENT '칼로리 값',
    `created_dt`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '시스템 등록 일시',
    `created_id`     VARCHAR(50)     NOT NULL                COMMENT '등록자 ID',
    `updated_dt`     DATETIME        NULL                    COMMENT '시스템 수정 일시',
    `updated_id`     VARCHAR(50)     NULL                    COMMENT '수정자 ID',

    /* [PK 및 파티셔닝]
       - 파티션 키인 period_from을 PK에 포함하여 일별 파티셔닝이 가능하도록 구성했습니다.
    */
    PRIMARY KEY (`rc_detail_id`, `period_from`),
	
	/* UNIQUE INDEX 추가 
     마스터ID + 시작시간(+필요시 종료시간) 조합으로 중복 적재를 방지
	*/
	UNIQUE KEY `UK_DETAIL_DUPLICATE` (`rc_master_id`, `period_from`, `period_to`),

    /* [인덱스]
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


-- 4. OC_HEALTH_DAILY_STAT(일별 레코드 집계)
CREATE TABLE `oc_health_daily_stat` (
  `member_id` bigint NOT NULL,
  `record_date` date NOT NULL,
  `record_key` varchar(100) NOT NULL,
  `steps_daily_sum` decimal(15,0) NOT NULL DEFAULT '0',
  `distance_daily_sum` decimal(20,5) NOT NULL DEFAULT '0.00000',
  `calories_daily_sum` decimal(15,2) NOT NULL DEFAULT '0.00',
  `updated_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`member_id`,`record_date`,`record_key`),
  CONSTRAINT `FK_STAT_MEMBER` FOREIGN KEY (`member_id`) REFERENCES `oc_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='일별 레코드 집계';


-- 5. OC_HEALTH_MONTHLY_STAT(월별 레코드 집계)
CREATE TABLE `oc_health_monthly_stat` (
  `record_month` char(7) NOT NULL COMMENT '레코드월 (YYYY-MM)',
  `record_key` varchar(100) NOT NULL COMMENT '레코드키',
  `member_id` bigint NOT NULL COMMENT '회원ID',
  `steps_month_sum` decimal(20,0) NOT NULL DEFAULT '0' COMMENT '걸음수월합계',
  `distance_month_sum` decimal(25,5) NOT NULL DEFAULT '0.00000' COMMENT '거리월합계',
  `calories_month_sum` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '칼로리월합계',
  `updated_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  `updated_id` varchar(50) DEFAULT NULL COMMENT '수정ID',
  PRIMARY KEY (`record_month`,`record_key`,`member_id`),
  KEY `FK_MONTHLY_STAT_MEMBER` (`member_id`),
  CONSTRAINT `FK_MONTHLY_STAT_MEMBER` FOREIGN KEY (`member_id`) REFERENCES `oc_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='월별 레코드 집계';
