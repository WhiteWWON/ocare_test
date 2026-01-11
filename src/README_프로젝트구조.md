**#프로젝트 프로그램 구성**

1. 헥사고날 아키텍쳐 방식 구성 : 추후 1000만명 이상의 대용량 트래픽 구성을 가정하여, 결합도가 느스한 헥사고날로 생각해 보았습니다.

2. 데이터 처리 방식 : JPA와 mybatis를 혼용했습니다.

3. 개발된 API 기능:
- 공통 URL : http://localhost:8080
1) /api/auth/signUp (회원가입)
- 패스워드 암호화 적용

2) /api/auth/login (로그인)
- JWT 토큰 + Redis 저장(refresh 토큰) 방식으로 개발
- 비밀번호 5회 오류시 잠김 기능 개발

3) /api/auth/logout (로그아웃)
- AccessToken 토큰 유효기간 만료시, 블래릭스트로 등록하여 보안에 대비했습니다.

4) /api/v1/health/bulk-record (JSON 레코드 대량 저장)
- 저장시 mybatis를 사용, mysql DUPLICATE KEY UPDATE 쿼리를 사용하여, 데이터 중복체크 로직을 간소화했습니다.

5) /api/v1/health/getMyRecords (저장된 레코드 조회) 
- 실제 과제 양식대로 DAILY, MONTHLY 형태로까진 못했고, 저장된 레코드를 조회하는 기능까지는 만들어봤습니다.

6) /api/auth/dummy (더미 컨트롤러) : 필터와 인터셉터 기능 테스트용으로 만들었습니다.

7) 필터: 로그인 토큰 인증 기능을 개발
   인터셉터: 필터 통과 후 컨트롤러 호출 전, 필터에서 인증여부를 체크하고, 비즈니스 컨트롤러를 호출하도록 개발
   -> 레코드 저장 API 호출 시, 로그인 필수



4. 프로그램 구조도 입니다.
<img width="685" height="836" alt="image" src="https://github.com/user-attachments/assets/c5838db4-454e-4bd5-aafd-4398cd3c3c6c" />

		
	
역할 추가 설명
Kafka 수신	adapter/in/kafka/HealthRecordConsumer.java	Kafka Topic에서 헬스 데이터를 수신하여 Service로 전달
-> 구현을 계획해 보았으나, 일정상 못했습니다.
벌크 저장 로직	application/service/HealthRecordService.java	마스터/상세 데이터를 묶어 트랜잭션을 관리하고 저장 명령
MyBatis 인터페이스	adapter/out/persistence/HealthRecordMapper.java	SQL 실행을 위한 Java 매퍼 인터페이스
실제 SQL 쿼리	src/main/resources/mapper/HealthRecordMapper.xml	insertDetailBatch 등 복잡한 벌크 쿼리가 담긴 파일
DB 처리 어댑터	adapter/out/persistence/HealthRecordPersistenceAdapter.java	HealthRecordPort를 구현하며 MyBatis 매퍼를 호출



** 회사 업무와 개인 일정 사유로, kafka 연동 + DAILY,MONTHLY 집계 조회 기능은 개발을 하지 못하였습니다.
만약 면접 기회가 주어진다면, 면접 당일 전까지 최대한 완성본으로 찾아뵙도록 하겠습니다. (무조건 현 상태보단 완성도를 높여 가겠습니다.)

