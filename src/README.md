1. DB ERD 구조도 입니다.
<img width="1239" height="769" alt="image" src="https://github.com/user-attachments/assets/514bea9c-967b-49f6-8914-1da0ae1c50c8" />
링크 : https://www.erdcloud.com/d/RS4qAP2dZqLZNyZ3E

2. DB ERD 설명입니다.

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
