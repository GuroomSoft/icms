/* 시스템 구성 정보 */
INSERT INTO ST_CONFIG (env_key, env_value, category, env_remark, reg_uid)
VALUES ('default-user-group', '1', '보안', '계정등록시 가입되는 기본 사용자 보안 그룹 설정, 정수값 형식의 그룹 UID 값으로 설정', 1);
INSERT INTO ST_CONFIG (env_key, env_value, category, env_remark, reg_uid)
VALUES ('default-user-role', 'USER', '보안', '기본 사용자 역할, "USER", "MANAGER", "PARTNER", "SYSADMIN" 중 하나의 값으로 설정', 1);
INSERT INTO ST_CONFIG (env_key, env_value, category, env_remark, reg_uid)
VALUES ('auth-duration', '5', '보안', '사용자 확인 유효시간 분단위의 정수값으로 설정', 1);
INSERT INTO ST_CONFIG (env_key, env_value, category, env_remark, reg_uid)
VALUES ('default-init-pwd', 'test1234', '보안', '암호 초기화시 사용되는 암호를 6자리이상의 암호를 설정', 1);
INSERT INTO ST_CONFIG (env_key, env_value, category, env_remark, reg_uid)
VALUES ('rowcount-per-page', '50', '일반', '페이지당 기본 조회 건수', 1);



/* 로그인 사용자 */
INSERT INTO ST_USERS (USER_UID,ACCOUNT_ID,ACCOUNT_NAME,ACCOUNT_PWD,ACCOUNT_NICKNAME,ACCOUNT_TYPE,PWD_HASH_VALUE,PWD_EXPIRED_DT,ACTIVE,EMAIL,VERIFIED_EMAIL,MOBILE,VERIFIED_MOBILE,ACCOUNT_LOCKED,ACCOUNT_EXPIRED_DT,ACCOUNT_TEL,LAST_LOGIN_IP,LAST_LOGIN_DT,plant_cd,ORG_CD,TEAM_CD,BP_CD,REG_DT,REG_UID)
VALUES (1,'admin','시스템관리자','64fcc6f6bc7a815041b4db51f00f4bea8e51c13b27f422da0a8522c94641c7e483c3f17b28d0a59add0c8a44a4e4fc1dd3a9ea48bad8cf5b707ac0f44a5f3536','관리자','I',NULL,NULL,'Y',NULL,'N',NULL,'N','N',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,getdate(),1);

/* 사용자 Role */
INSERT INTO ST_USER_ROLES (USER_UID, USER_ROLE, REG_DT)
VALUES(1, 'SYSADMIN', getdate());

/* 사용자 그룹 */
INSERT INTO ST_AUTH_GROUPS (GRP_UID, GRP_NAME, GRP_REMARK, USE_AT, REG_DT, REG_UID)
VALUES(1, '일반 사용자', '계정등록시 가입되는 기본 사용자 그룹', 'Y', getdate(), 1);
INSERT INTO ST_AUTH_GROUPS (GRP_UID, GRP_NAME, GRP_REMARK, USE_AT, REG_DT, REG_UID)
VALUES(2, '시스템관리자 그룹', '시스템 관리자 사용자 그룹', 'Y', getdate(), 1);
INSERT INTO ST_AUTH_GROUPS (GRP_UID, GRP_NAME, GRP_REMARK, USE_AT, REG_DT, REG_UID)
VALUES(3, '협력사 사용자 그룹', '협력사 사용자 그룹', 'Y', getdate(), 1);

/* 사용자 그룹 멤버 */
INSERT INTO ST_AUTH_GROUP_MEMBER (GRP_UID,USER_UID,USE_AT,REG_DT,REG_UID)
VALUES (2, 1, 'Y', getdate(),0);

/* 게시물 분류 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('C01', '게시물분류', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
('C01', '10', '일반', '일반업무관련 공지', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
('C01', '99', '기타', '기타', 'Y' , 'Y', 'Y' , 10 , getdate() , 0)
;

/* 일반적인 코드 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES
    ('C02', '예아니오', '', 'Y', 'Y', getdate(), 0),
    ('C03', '사용유무', '', 'Y', 'Y', getdate(), 0),
    ('C04', '노출여부', '', 'Y', 'Y', getdate(), 0),
    ('C05', 'BP구분', '', 'Y', 'Y', getdate(), 0),
    ('C06', '부서코드', '', 'Y', 'Y', getdate(), 0),
    ('C07', '팀코드', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('C02', 'Y', '예', '예', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C02', 'N', '아니오', '아니오', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C03', 'Y', '사용', '사용', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C03', 'N', '미사용', '미사용', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C04', 'Y', '표시', '표시', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C04', 'N', '노출안함', '노출안함', 'Y' , 'Y', 'Y' , 10 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('C05', 'C', '고객', null, 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C05', 'V', '공급업체', null, 'N' , 'Y', 'Y' , 20 , getdate() , 0);

-- add_data1 컬럼에 소속 플랜트 코드를 등록해서 사용
-- 부서

-- 팀 ( upper_cd 컬럼에 부서 코드를 지정)
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, upper_cd, CD_REMARK, add_data1, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('C07', '10', '원가1팀', null, null, '1000', 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C07', '20', '원가2팀', null, null, '1000', 'N' , 'Y', 'Y' , 20 , getdate() , 0);

-- 국가코드
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('C08', '국가코드', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, upper_cd, CD_REMARK, add_data1, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('C08', 'KR', '대한민국', null, null, null, 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C08', 'US', '미국', null, null, null, 'N' , 'Y', 'Y' , 20 , getdate() , 0),
    ('C08', 'CN', '중국', null, null, null, 'N' , 'Y', 'Y' , 30 , getdate() , 0),
    ('C08', 'IN', '인도', null, null, null, 'N' , 'Y', 'Y' , 40 , getdate() , 0),
    ('C08', 'ID', '인도네시아', null, null, null, 'N' , 'Y', 'Y' , 50 , getdate() , 0),
    ('C08', 'TR', '튀르키예', null, null, null, 'N' , 'Y', 'Y' , 60 , getdate() , 0),
    ('C08', 'PL', '폴란드', null, null, null, 'N' , 'Y', 'Y' , 70 , getdate() , 0),
    ('C08', 'CZ', '체코', null, null, null, 'N' , 'Y', 'Y' , 80 , getdate() , 0),
    ('C08', 'SK', '슬로바키아', null, null, null, 'N' , 'Y', 'Y' , 90 , getdate() , 0);


/* 문서 상태 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('C09', '문서상태', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, upper_cd, CD_REMARK, add_data1, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('C09', 'N', '미결', null, null, null, 'Y' , 'Y', 'Y' , 10 , getdate() , 0),
    ('C09', 'Y', '확정', null, null, null, 'N' , 'Y', 'Y' , 20 , getdate() , 0);

/* 사용자 유형 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('S01', '계정유형', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S01', 'I', '내부사용자', '조직 내부 사용자', 'Y' , 'Y', 'Y' , 10 , getdate() , 0);
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S01', 'E', '외부사용자', '외부 사용자', 'N' , 'Y', 'Y' , 20 , getdate() , 0);
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S01', 'S', '시스템사용자', '시스템 사용자', 'N' , 'Y', 'Y' , 30 , getdate() , 0);

/* 코드 그룹 목록 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('S03', '사용자 Role', '', 'Y', 'Y', getdate(), 0);

/* 시스템 공통코드 */
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S03', 'USER', '일반사용자', '일반 사용자', 'Y', 'Y' , 'Y' , 10 , getdate() , 0);
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S03', 'MANAGER', '부서관리자', '부서 관리자', 'N', 'Y' , 'Y' , 30 , getdate() , 0);
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S03', 'PARTNER', '협력업체', '외부 협력업체 사용자', 'N', 'Y' , 'Y' , 100 , getdate() , 0);
INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES('S03', 'SYSADMIN', '시스템 관리자', '시스템 관리자', 'N', 'Y' , 'Y' , 999 , getdate() , 0);

/* 부품구분 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('B02', '부품구분', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B02', 'DD', '직개발', '해당 END부품 업체 직개발부품(END부품업체와 SUB단품업체 동일)', 'N' , 'Y' , 'Y' , 10 , getdate() , 0),
    ('B02', 'DT', '직거래', 'SUB부품 中 dsc 업체선정하여 직거래 중인 부품(END부품업체와 SUB부품업체 상이)', 'N' , 'Y' , 'Y' , 15 , getdate() , 0),
    ('B02', 'RP', '사급', 'SUB부품 中 dsc 개발하여 사급중인 부품(END부품업체와 SUB부품업체 상이)', 'N' , 'Y' ,'Y' , 50 , getdate() , 0);

/* 차종코드 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('B03', '차종', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', '0', '공통차장', '', 'N' , 'Y' , 'Y' , 1 , getdate() , 0),
    ('B03', '12S1P', '12S1P', '', 'N' , 'Y' ,'Y' , 2 , getdate() , 0),
    ('B03', '660B', '660B북미', '', 'N' , 'Y' ,'Y' , 3 , getdate() , 0),
    ('B03', 'A1', '스타렉스 A1', '', 'N' , 'Y' ,'Y' , 4 , getdate() , 0),
    ('B03', 'A1T', 'A1T', '', 'N' , 'Y' ,'Y' , 5 , getdate() , 0),
    ('B03', 'AA', 'AA', '', 'N' , 'Y' ,'Y' , 6 , getdate() , 0),
    ('B03', 'AC3', 'AC3', '', 'N' , 'Y' ,'Y' , 7 , getdate() , 0),
    ('B03', 'AD', '아반떼 AD', '', 'N' , 'Y' ,'Y' , 8 , getdate() , 0),
    ('B03', 'ADA', '아반떼 AD북미', '', 'N' , 'Y' ,'Y' , 9 , getdate() , 0),
    ('B03', 'ADC', 'ADC', '', 'N' , 'Y' ,'Y' , 10 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'ADI', 'ADI', '', 'N' , 'Y' , 'Y' , 11 , getdate() , 0),
    ('B03', 'AE', '아이오닉', '', 'N' , 'Y' ,'Y' , 12 , getdate() , 0),
    ('B03', 'AG', '아슬란', '', 'N' , 'Y' ,'Y' , 13 , getdate() , 0),
    ('B03', 'AH', '그레이스', '', 'N' , 'Y' ,'Y' , 14 , getdate() , 0),
    ('B03', 'AH2', 'AH2', '', 'N' , 'Y' ,'Y' , 15 , getdate() , 0),
    ('B03', 'AHT', 'AHT', '', 'N' , 'Y' ,'Y' , 16 , getdate() , 0),
    ('B03', 'AI', 'AI', '', 'N' , 'Y' ,'Y' , 17 , getdate() , 0),
    ('B03', 'AI3', 'AI3', '', 'N' , 'Y' ,'Y' , 18 , getdate() , 0),
    ('B03', 'AM', 'AM', '', 'N' , 'Y' ,'Y' , 19 , getdate() , 0),
    ('B03', 'AUDI48AH', 'AUDI48AH', '', 'N' , 'Y' ,'Y' , 20 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'AUM', 'AUM', '', 'N' , 'Y' , 'Y' , 21 , getdate() , 0),
    ('B03', 'AUT', 'PORTER', '', 'N' , 'Y' ,'Y' , 22 , getdate() , 0),
    ('B03', 'AX', '경CUV(사용금지)', '', 'N' , 'Y' ,'Y' , 23 , getdate() , 0),
    ('B03', 'AX1', '경CUV', '', 'N' , 'Y' ,'Y' , 24 , getdate() , 0),
    ('B03', 'B515', 'B515', '', 'N' , 'Y' ,'Y' , 25 , getdate() , 0),
    ('B03', 'BA', 'BA', '', 'N' , 'Y' ,'Y' , 26 , getdate() , 0),
    ('B03', 'BC', '리오', '', 'N' , 'Y' ,'Y' , 27 , getdate() , 0),
    ('B03', 'BC3', 'BC3', '', 'N' , 'Y' ,'Y' , 28 , getdate() , 0),
    ('B03', 'BD', 'K3', '', 'N' , 'Y' ,'Y' , 29 , getdate() , 0),
    ('B03', 'BDC', 'K3중국', '', 'N' , 'Y' ,'Y' , 30 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'BDM', 'BDM', '', 'N' , 'Y' ,'Y' , 31 , getdate() , 0),
    ('B03', 'BDPE', 'BD 페이스리프트', '', 'N' , 'Y' ,'Y' , 32 , getdate() , 0),
    ('B03', 'BE', 'BE', '', 'N' , 'Y' ,'Y' , 33 , getdate() , 0),
    ('B03', 'BH', '제네시스(BH)', '', 'N' , 'Y' ,'Y' , 34 , getdate() , 0),
    ('B03', 'BI3', 'BI3', '', 'N' , 'Y' ,'Y' , 35 , getdate() , 0),
    ('B03', 'BK', '제네시스 쿠페', '', 'N' , 'Y' ,'Y' , 36 , getdate() , 0),
    ('B03', 'BL', '소렌토BL', '', 'N' , 'Y' ,'Y' , 37 , getdate() , 0),
    ('B03', 'BL7M', 'BL7M', '', 'N' , 'Y' ,'Y' , 38 , getdate() , 0),
    ('B03', 'BN7I', 'BN7I', '', 'N' , 'Y' ,'Y' , 39 , getdate() , 0),
    ('B03', 'C', 'C-CAR', '', 'N' , 'Y' ,'Y' , 40 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'C2', 'C2', '', 'N' , 'Y' ,'Y' , 41 , getdate() , 0),
    ('B03', 'C234', 'C234', '', 'N' , 'Y' ,'Y' , 42 , getdate() , 0),
    ('B03', 'CB', 'CB', '', 'N' , 'Y' ,'Y' , 43 , getdate() , 0),
    ('B03', 'CD', 'CD', '', 'N' , 'Y' ,'Y' , 44 , getdate() , 0),
    ('B03', 'CD 5DR', 'CD 5DR', '', 'N' , 'Y' ,'Y' , 45 , getdate() , 0),
    ('B03', 'CD CUV', 'CD CUV', '', 'N' , 'Y' ,'Y' , 46 , getdate() , 0),
    ('B03', 'CD-PHEV', 'CD-PHEV', '', 'N' , 'Y' ,'Y' , 47 , getdate() , 0),
    ('B03', 'CD-SB', 'CD-SB', '', 'N' , 'Y' ,'Y' , 48 , getdate() , 0),
    ('B03', 'CDE', 'CDE', '', 'N' , 'Y' ,'Y' , 49 , getdate() , 0),
    ('B03', 'CE', 'CE', '', 'N' , 'Y' ,'Y' , 50 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'CF', 'CF', '', 'N' , 'Y' ,'Y' , 51 , getdate() , 0),
    ('B03', 'CF-C', 'CF-C', '', 'N' , 'Y' ,'Y' , 52 , getdate() , 0),
    ('B03', 'CH2', 'CH2', '', 'N' , 'Y' ,'Y' , 53 , getdate() , 0),
    ('B03', 'CK', '스팅어', '', 'N' , 'Y' ,'Y' , 54 , getdate() , 0),
    ('B03', 'CKPE', '스팅어', '', 'N' , 'Y' ,'Y' , 55 , getdate() , 0),
    ('B03', 'CL4', 'CL4', '', 'N' , 'Y' ,'Y' , 56 , getdate() , 0),
    ('B03', 'CM', '싼타페 CM', '', 'N' , 'Y' ,'Y' , 57 , getdate() , 0),
    ('B03', 'CMA', '싼타페 CM북미', '', 'N' , 'Y' ,'Y' , 58 , getdate() , 0),
    ('B03', 'CN7', '아반떼', '', 'N' , 'Y' ,'Y' , 59 , getdate() , 0),
    ('B03', 'CN7C', 'CN7C', '', 'N' , 'Y' ,'Y' , 60 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'CN7CPE', 'CN7CPE', '', 'N' , 'Y' ,'Y' , 61 , getdate() , 0),
    ('B03', 'CT', '프레지오', '', 'N' , 'Y' ,'Y' , 62 , getdate() , 0),
    ('B03', 'CV', 'EV6', '', 'N' , 'Y' ,'Y' , 63 , getdate() , 0),
    ('B03', 'DA', 'DA', '', 'N' , 'Y' ,'Y' , 64 , getdate() , 0),
    ('B03', 'DE', '니로 HEV', '', 'N' , 'Y' ,'Y' , 65 , getdate() , 0),
    ('B03', 'DE PBV', '니로 PBV', '', 'N' , 'Y' ,'Y' , 66 , getdate() , 0),
    ('B03', 'DH', '제네시스(DH)', '', 'N' , 'Y' ,'Y' , 67 , getdate() , 0),
    ('B03', 'DL3', 'DL3', '', 'N' , 'Y' ,'Y' , 68 , getdate() , 0),
    ('B03', 'DL3A', 'K5북미', '', 'N' , 'Y' ,'Y' , 69 , getdate() , 0),
    ('B03', 'DL3C', 'DL3C', '', 'N' , 'Y' ,'Y' , 70 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'DM', '싼타페 DM', '', 'N' , 'Y' ,'Y' , 71 , getdate() , 0),
    ('B03', 'DMC', 'DMC', '', 'N' , 'Y' ,'Y' , 72 , getdate() , 0),
    ('B03', 'DMI', '싼타페 DM인도', '', 'N' , 'Y' ,'Y' , 73 , getdate() , 0),
    ('B03', 'DN8', 'DN8 소나타', '', 'N' , 'Y' ,'Y' , 74 , getdate() , 0),
    ('B03', 'DN8C', 'DN8C', '', 'N' , 'Y' ,'Y' , 75 , getdate() , 0),
    ('B03', 'DN8N', 'DN8 스포츠', '', 'N' , 'Y' ,'Y' , 76 , getdate() , 0),
    ('B03', 'DS', '카스타', '', 'N' , 'Y' ,'Y' , 77 , getdate() , 0),
    ('B03', 'E063', 'E063', '', 'N' , 'Y' ,'Y' , 78 , getdate() , 0),
    ('B03', 'E603', 'E603', '', 'N' , 'Y' ,'Y' , 79 , getdate() , 0),
    ('B03', 'EA', '에쿠스', '', 'N' , 'Y' ,'Y' , 80 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'EDV', 'EDV', '', 'N' , 'Y' ,'Y' , 81 , getdate() , 0),
    ('B03', 'EF', 'EF 소나타', '', 'N' , 'Y' ,'Y' , 82 , getdate() , 0),
    ('B03', 'EFC', 'EFC', '', 'N' , 'Y' ,'Y' , 83 , getdate() , 0),
    ('B03', 'EFI', 'EFI', '', 'N' , 'Y' ,'Y' , 84 , getdate() , 0),
    ('B03', 'EH', 'EH', '', 'N' , 'Y' ,'Y' , 85 , getdate() , 0),
    ('B03', 'EJ', 'EJ', '', 'N' , 'Y' ,'Y' , 86 , getdate() , 0),
    ('B03', 'EK', 'EK', '', 'N' , 'Y' ,'Y' , 87 , getdate() , 0),
    ('B03', 'EL', 'EL', '', 'N' , 'Y' ,'Y' , 88 , getdate() , 0),
    ('B03', 'EN', '베라크루즈', '', 'N' , 'Y' ,'Y' , 89 , getdate() , 0),
    ('B03', 'EP', 'EP', '', 'N' , 'Y' ,'Y' , 90 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'FA', '프라이드(FA)', '', 'N' , 'Y' ,'Y' , 91 , getdate() , 0),
    ('B03', 'FB', '셰피아', '', 'N' , 'Y' ,'Y' , 92 , getdate() , 0),
    ('B03', 'FC', '라비타', '', 'N' , 'Y' ,'Y' , 93 , getdate() , 0),
    ('B03', 'FCT', 'FCT', '', 'N' , 'Y' ,'Y' , 94 , getdate() , 0),
    ('B03', 'FD', 'FD', '', 'N' , 'Y' ,'Y' , 95 , getdate() , 0),
    ('B03', 'FDC', 'FDC', '', 'N' , 'Y' ,'Y' , 96 , getdate() , 0),
    ('B03', 'FDE', 'FDE', '', 'N' , 'Y' ,'Y' , 97 , getdate() , 0),
    ('B03', 'FDH', 'FDH', '', 'N' , 'Y' ,'Y' , 98 , getdate() , 0),
    ('B03', 'FE', '넥쏘', '', 'N' , 'Y' ,'Y' , 99 , getdate() , 0),
    ('B03', 'FF', '포텐샤', '', 'N' , 'Y' ,'Y' , 100 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'FG', '포텐샤 엔터프라이즈', '', 'N' , 'Y' ,'Y' , 101 , getdate() , 0),
    ('B03', 'FH', '엘란', '', 'N' , 'Y' ,'Y' , 102 , getdate() , 0),
    ('B03', 'FI', '카니발 FI 차이나', '', 'N' , 'Y' ,'Y' , 103 , getdate() , 0),
    ('B03', 'FJ', '카렌스', '', 'N' , 'Y' ,'Y' , 104 , getdate() , 0),
    ('B03', 'FK', '레토나', '', 'N' , 'Y' ,'Y' , 105 , getdate() , 0),
    ('B03', 'FL', '카니발 FL', '', 'N' , 'Y' ,'Y' , 106 , getdate() , 0),
    ('B03', 'FM', '스포티지 FM', '', 'N' , 'Y' ,'Y' , 107 , getdate() , 0),
    ('B03', 'FN', '타우너 웨건', '', 'N' , 'Y' ,'Y' , 108 , getdate() , 0),
    ('B03', 'FO', '트라제 XG', '', 'N' , 'Y' ,'Y' , 109 , getdate() , 0),
    ('B03', 'FP', '프레지오', '', 'N' , 'Y' ,'Y' , 110 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'FR', '봉고 프론티어', '', 'N' , 'Y' ,'Y' , 111 , getdate() , 0),
    ('B03', 'FS', 'MD SPORTS', '', 'N' , 'Y' ,'Y' , 112 , getdate() , 0),
    ('B03', 'FT', 'CERES', '', 'N' , 'Y' ,'Y' , 113 , getdate() , 0),
    ('B03', 'FU', 'FRONTIER 1.3/1.', '', 'N' , 'Y' ,'Y' , 114 , getdate() , 0),
    ('B03', 'FV', '아벨라', '', 'N' , 'Y' ,'Y' , 115 , getdate() , 0),
    ('B03', 'FW', 'TOWNER TRK', '', 'N' , 'Y' ,'Y' , 116 , getdate() , 0),
    ('B03', 'FX', '슈마', '', 'N' , 'Y' ,'Y' , 117 , getdate() , 0),
    ('B03', 'G02A', 'G02A', '', 'N' , 'Y' ,'Y' , 118 , getdate() , 0),
    ('B03', 'G2', '크레도스', '', 'N' , 'Y' ,'Y' , 119 , getdate() , 0),
    ('B03', 'G7', 'G7', '', 'N' , 'Y' ,'Y' , 120 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'GB', 'GB', '', 'N' , 'Y' ,'Y' , 121 , getdate() , 0),
    ('B03', 'GC', 'GC', '', 'N' , 'Y' ,'Y' , 122 , getdate() , 0),
    ('B03', 'GC7', 'GC7', '', 'N' , 'Y' ,'Y' , 123 , getdate() , 0),
    ('B03', 'GD', 'FD 후속', '', 'N' , 'Y' ,'Y' , 124 , getdate() , 0),
    ('B03', 'GDE', 'GDE', '', 'N' , 'Y' ,'Y' , 125 , getdate() , 0),
    ('B03', 'GH', '오피러스', '', 'N' , 'Y' ,'Y' , 126 , getdate() , 0),
    ('B03', 'GK', '투스카니', '', 'N' , 'Y' ,'Y' , 127 , getdate() , 0),
    ('B03', 'GL3', 'K7', '', 'N' , 'Y' ,'Y' , 128 , getdate() , 0),
    ('B03', 'GMNA', 'GMNA', '', 'N' , 'Y' ,'Y' , 129 , getdate() , 0),
    ('B03', 'GN7', '그랜져GN7', '', 'N' , 'Y' ,'Y' , 130 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'GR', 'GR', '', 'N' , 'Y' ,'Y' , 131 , getdate() , 0),
    ('B03', 'GS', 'GS', '', 'N' , 'Y' ,'Y' , 132 , getdate() , 0),
    ('B03', 'GSB', 'GSB', '', 'N' , 'Y' ,'Y' , 133 , getdate() , 0),
    ('B03', 'H1', '마르샤', '', 'N' , 'Y' ,'Y' , 134 , getdate() , 0),
    ('B03', 'H247', 'H247', '', 'N' , 'Y' ,'Y' , 135 , getdate() , 0),
    ('B03', 'HA', 'HA', '', 'N' , 'Y' ,'Y' , 136 , getdate() , 0),
    ('B03', 'HB', '브라질', '', 'N' , 'Y' ,'Y' , 137 , getdate() , 0),
    ('B03', 'HBC', 'HBC', '', 'N' , 'Y' ,'Y' , 138 , getdate() , 0),
    ('B03', 'HC', 'HC', '', 'N' , 'Y' ,'Y' , 139 , getdate() , 0),
    ('B03', 'HCI', 'HCI', '', 'N' , 'Y' ,'Y' , 140 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'HCR', 'HCR', '', 'N' , 'Y' ,'Y' , 141 , getdate() , 0),
    ('B03', 'HD', '아반떼 HD', '', 'N' , 'Y' ,'Y' , 142 , getdate() , 0),
    ('B03', 'HDC', 'HDC', '', 'N' , 'Y' ,'Y' , 143 , getdate() , 0),
    ('B03', 'HG', '그랜져 HG', '', 'N' , 'Y' ,'Y' , 144 , getdate() , 0),
    ('B03', 'HI', 'EQ900', '', 'N' , 'Y' ,'Y' , 145 , getdate() , 0),
    ('B03', 'HM', '모하비', '', 'N' , 'Y' ,'Y' , 146 , getdate() , 0),
    ('B03', 'HM PE', 'HM PE', '', 'N' , 'Y' ,'Y' , 147 , getdate() , 0),
    ('B03', 'HP', '테라칸', '', 'N' , 'Y' ,'Y' , 148 , getdate() , 0),
    ('B03', 'HR', 'HR', '', 'N' , 'Y' ,'Y' , 149 , getdate() , 0),
    ('B03', 'IA', 'IA', '', 'N' , 'Y' ,'Y' , 150 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'IB', 'IB', '', 'N' , 'Y' ,'Y' , 151 , getdate() , 0),
    ('B03', 'ID', 'ID', '', 'N' , 'Y' ,'Y' , 152 , getdate() , 0),
    ('B03', 'IG', '그랜져 IG', '', 'N' , 'Y' ,'Y' , 153 , getdate() , 0),
    ('B03', 'IK', 'G70', '', 'N' , 'Y' ,'Y' , 154 , getdate() , 0),
    ('B03', 'IK S/B', 'G70유럽', '', 'N' , 'Y' ,'Y' , 155 , getdate() , 0),
    ('B03', 'IKS/B', 'G70유럽', '', 'N' , 'Y' ,'Y' , 156 , getdate() , 0),
    ('B03', 'ISTAN', '이스타나', '', 'N' , 'Y' ,'Y' , 157 , getdate() , 0),
    ('B03', 'J2', '아반떼 J2', '', 'N' , 'Y' ,'Y' , 158 , getdate() , 0),
    ('B03', 'J2C', '티뷰론', '', 'N' , 'Y' ,'Y' , 159 , getdate() , 0),
    ('B03', 'JB', '프라이드 JB', '', 'N' , 'Y' ,'Y' , 160 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'JC', 'JC', '', 'N' , 'Y' ,'Y' , 161 , getdate() , 0),
    ('B03', 'JF', 'K5 신형', '', 'N' , 'Y' ,'Y' , 162 , getdate() , 0),
    ('B03', 'JFC', 'JFC', '', 'N' , 'Y' ,'Y' , 163 , getdate() , 0),
    ('B03', 'JK', 'MD 2DR', '', 'N' , 'Y' ,'Y' , 164 , getdate() , 0),
    ('B03', 'JK1', 'GV70', '', 'N' , 'Y' ,'Y' , 165 , getdate() , 0),
    ('B03', 'JKA', 'GV70 북미', '', 'N' , 'Y' ,'Y' , 166 , getdate() , 0),
    ('B03', 'JM', '투싼 JM', '', 'N' , 'Y' ,'Y' , 167 , getdate() , 0),
    ('B03', 'JMC', 'JMC', '', 'N' , 'Y' ,'Y' , 168 , getdate() , 0),
    ('B03', 'JMF', 'JM FUEL CELL', '', 'N' , 'Y' ,'Y' , 169 , getdate() , 0),
    ('B03', 'JS', '벨로스타', '', 'N' , 'Y' ,'Y' , 170 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'JSN', '벨로스타 북미', '', 'N' , 'Y' ,'Y' , 171 , getdate() , 0),
    ('B03', 'JW', '제네시스EV', '', 'N' , 'Y' ,'Y' , 172 , getdate() , 0),
    ('B03', 'JX1', 'JX1', '', 'N' , 'Y' ,'Y' , 173 , getdate() , 0),
    ('B03', 'JX1PE', 'GV80', '', 'N' , 'Y' ,'Y' , 174 , getdate() , 0),
    ('B03', 'KC', 'KC', '', 'N' , 'Y' ,'Y' , 175 , getdate() , 0),
    ('B03', 'KH', 'K9(KH)', '', 'N' , 'Y' ,'Y' , 176 , getdate() , 0),
    ('B03', 'KM', '스포티지 KM', '', 'N' , 'Y' ,'Y' , 177 , getdate() , 0),
    ('B03', 'KS', 'KS', '', 'N' , 'Y' ,'Y' , 178 , getdate() , 0),
    ('B03', 'KU', 'KU', '', 'N' , 'Y' ,'Y' , 179 , getdate() , 0),
    ('B03', 'KY', 'KY', '', 'N' , 'Y' ,'Y' , 180 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'KY 23MY', 'KY 23MY', '', 'N' , 'Y' ,'Y' , 181 , getdate() , 0),
    ('B03', 'L02B', 'L02B', '', 'N' , 'Y' ,'Y' , 182 , getdate() , 0),
    ('B03', 'L1', 'L1', '', 'N' , 'Y' ,'Y' , 183 , getdate() , 0),
    ('B03', 'L233', 'L233', '', 'N' , 'Y' ,'Y' , 184 , getdate() , 0),
    ('B03', 'L38', '삼성 LM SERIES', '', 'N' , 'Y' ,'Y' , 185 , getdate() , 0),
    ('B03', 'L43', 'L43', '', 'N' , 'Y' ,'Y' , 186 , getdate() , 0),
    ('B03', 'L47', 'L47', '', 'N' , 'Y' ,'Y' , 187 , getdate() , 0),
    ('B03', 'L663', 'L663', '', 'N' , 'Y' ,'Y' , 188 , getdate() , 0),
    ('B03', 'LC', '베르나', '', 'N' , 'Y' ,'Y' , 189 , getdate() , 0),
    ('B03', 'LCI', 'LCI', '', 'N' , 'Y' ,'Y' , 190 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'LCT', 'LCT', '', 'N' , 'Y' ,'Y' , 193 , getdate() , 0),
    ('B03', 'LD', 'LD', '', 'N' , 'Y' ,'Y' , 192 , getdate() , 0),
    ('B03', 'LF', 'LF 소나타', '', 'N' , 'Y' ,'Y' , 194 , getdate() , 0),
    ('B03', 'LFC', 'LFC', '', 'N' , 'Y' ,'Y' , 191 , getdate() , 0),
    ('B03', 'LM', '투싼 LM', '', 'N' , 'Y' ,'Y' , 195 , getdate() , 0),
    ('B03', 'LMC', 'LMC', '', 'N' , 'Y' ,'Y' , 196 , getdate() , 0),
    ('B03', 'LX', '그랜져 LX', '', 'N' , 'Y' ,'Y' , 197 , getdate() , 0),
    ('B03', 'LX2', 'LX2', '', 'N' , 'Y' ,'Y' , 198 , getdate() , 0),
    ('B03', 'LZ', '에쿠스(LZ)', '', 'N' , 'Y' ,'Y' , 199 , getdate() , 0),
    ('B03', 'M2', '산타모', '', 'N' , 'Y' ,'Y' , 120 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'MA', 'MA', '', 'N' , 'Y' ,'Y' , 121 , getdate() , 0),
    ('B03', 'MC', 'MC', '', 'N' , 'Y' ,'Y' , 122 , getdate() , 0),
    ('B03', 'MCC', 'MCC', '', 'N' , 'Y' ,'Y' , 123 , getdate() , 0),
    ('B03', 'MCI', 'MCI', '', 'N' , 'Y' ,'Y' , 124 , getdate() , 0),
    ('B03', 'MCT', 'MCT', '', 'N' , 'Y' ,'Y' , 125 , getdate() , 0),
    ('B03', 'MD', '아반떼 MD', '', 'N' , 'Y' ,'Y' , 126 , getdate() , 0),
    ('B03', 'MDC', 'MDC', '', 'N' , 'Y' ,'Y' , 127 , getdate() , 0),
    ('B03', 'MDI', 'MDI', '', 'N' , 'Y' ,'Y' , 128 , getdate() , 0),
    ('B03', 'ME1', 'ME1', '', 'N' , 'Y' ,'Y' , 129 , getdate() , 0),
    ('B03', 'MEA', '', '', 'N' , 'Y' ,'Y' , 130 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'MEB41B', 'MEB41B', '', 'N' , 'Y' ,'Y' , 131 , getdate() , 0),
    ('B03', 'MG', '로체', '', 'N' , 'Y' ,'Y' , 132 , getdate() , 0),
    ('B03', 'MI', 'MI', '', 'N' , 'Y' ,'Y' , 133 , getdate() , 0),
    ('B03', 'MQ4', '쏘렌토', '', 'N' , 'Y' ,'Y' , 134 , getdate() , 0),
    ('B03', 'MQ4A', 'MQ4A', '', 'N' , 'Y' ,'Y' , 135 , getdate() , 0),
    ('B03', 'MS', 'EF A BODY', '', 'N' , 'Y' ,'Y' , 136 , getdate() , 0),
    ('B03', 'MSC', 'MSC', '', 'N' , 'Y' ,'Y' , 137 , getdate() , 0),
    ('B03', 'MV1', 'EV9', '', 'N' , 'Y' ,'Y' , 138 , getdate() , 0),
    ('B03', 'MVA', 'MVA', '', 'N' , 'Y' ,'Y' , 139 , getdate() , 0),
    ('B03', 'MX', '아토즈', '', 'N' , 'Y' ,'Y' , 140 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'MX5', 'MX5', '', 'N' , 'Y' ,'Y' , 141 , getdate() , 0),
    ('B03', 'MXI', '산트로', '', 'N' , 'Y' ,'Y' , 142 , getdate() , 0),
    ('B03', 'MXL', '비스토', '', 'N' , 'Y' ,'Y' , 143 , getdate() , 0),
    ('B03', 'NC', 'DM L/BODY', '', 'N' , 'Y' ,'Y' , 144 , getdate() , 0),
    ('B03', 'NE', '아이오닉5', '', 'N' , 'Y' ,'Y' , 145 , getdate() , 0),
    ('B03', 'NEA PE', 'NEA PE', '', 'N' , 'Y' ,'Y' , 146 , getdate() , 0),
    ('B03', 'NF', 'NF 소나타', '', 'N' , 'Y' ,'Y' , 147 , getdate() , 0),
    ('B03', 'NFA', 'NF 소나타 북미', '', 'N' , 'Y' ,'Y' , 148 , getdate() , 0),
    ('B03', 'NFC', 'NFC', '', 'N' , 'Y' ,'Y' , 149 , getdate() , 0),
    ('B03', 'NL', 'NL', '', 'N' , 'Y' ,'Y' , 150 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'NP', 'NP', '', 'N' , 'Y' ,'Y' , 151 , getdate() , 0),
    ('B03', 'NQ5', '스포티지후속(QL)', '', 'N' , 'Y' ,'Y' , 152 , getdate() , 0),
    ('B03', 'NQ5A', 'NQ5A', '', 'N' , 'Y' ,'Y' , 153 , getdate() , 0),
    ('B03', 'NQ5E', 'NQ5E', '', 'N' , 'Y' ,'Y' , 154 , getdate() , 0),
    ('B03', 'NU', 'NU', '', 'N' , 'Y' ,'Y' , 155 , getdate() , 0),
    ('B03', 'NU2', 'NU2', '', 'N' , 'Y' ,'Y' , 156 , getdate() , 0),
    ('B03', 'NX4', 'NX4', '', 'N' , 'Y' ,'Y' , 157 , getdate() , 0),
    ('B03', 'NX4A', 'NX4A', '', 'N' , 'Y' ,'Y' , 158 , getdate() , 0),
    ('B03', 'NX4C', 'NX4C', '', 'N' , 'Y' ,'Y' , 159 , getdate() , 0),
    ('B03', 'NX4E', 'NX4E', '', 'N' , 'Y' ,'Y' , 160 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'NX4E HEV', 'NX4E HEV', '', 'N' , 'Y' ,'Y' , 161 , getdate() , 0),
    ('B03', 'NX4OB', 'NX4OB', '', 'N' , 'Y' ,'Y' , 162 , getdate() , 0),
    ('B03', 'OE', 'OE', '', 'N' , 'Y' ,'Y' , 163 , getdate() , 0),
    ('B03', 'ON', '텔루라이드', '', 'N' , 'Y' ,'Y' , 164 , getdate() , 0),
    ('B03', 'OS', '코나', '', 'N' , 'Y' ,'Y' , 165 , getdate() , 0),
    ('B03', 'OSC', 'OSC', '', 'N' , 'Y' ,'Y' , 166 , getdate() , 0),
    ('B03', 'OSE', 'OSE', '', 'N' , 'Y' ,'Y' , 167 , getdate() , 0),
    ('B03', 'OSE EV', 'OSE EV', '', 'N' , 'Y' ,'Y' , 168 , getdate() , 0),
    ('B03', 'P2', '포니', '', 'N' , 'Y' ,'Y' , 169 , getdate() , 0),
    ('B03', 'P5', 'P5', '', 'N' , 'Y' ,'Y' , 170 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'P504', 'P504', '', 'N' , 'Y' ,'Y' , 171 , getdate() , 0),
    ('B03', 'P702', 'P702', '', 'N' , 'Y' ,'Y' , 172 , getdate() , 0),
    ('B03', 'P702 C', 'P702 C', '', 'N' , 'Y' ,'Y' , 173 , getdate() , 0),
    ('B03', 'P702C', 'P702C', '', 'N' , 'Y' ,'Y' , 174 , getdate() , 0),
    ('B03', 'PA', 'PA', '', 'N' , 'Y' ,'Y' , 175 , getdate() , 0),
    ('B03', 'PB', 'PB', '', 'N' , 'Y' ,'Y' , 176 , getdate() , 0),
    ('B03', 'PBD', 'PBD', '', 'N' , 'Y' ,'Y' , 177 , getdate() , 0),
    ('B03', 'PBI', 'PBI', '', 'N' , 'Y' ,'Y' , 178 , getdate() , 0),
    ('B03', 'PBT', 'PBT', '', 'N' , 'Y' ,'Y' , 179 , getdate() , 0),
    ('B03', 'PD', 'I30', '', 'N' , 'Y' ,'Y' , 180 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'PDE', 'PDE', '', 'N' , 'Y' ,'Y' , 181 , getdate() , 0),
    ('B03', 'PDE PE', 'PDE PE', '', 'N' , 'Y' ,'Y' , 182 , getdate() , 0),
    ('B03', 'PDE5DR', 'PDE5DR', '', 'N' , 'Y' ,'Y' , 183 , getdate() , 0),
    ('B03', 'PDE5DRHEV', 'PDE5DRHEV', '', 'N' , 'Y' ,'Y' , 184 , getdate() , 0),
    ('B03', 'PDEFB', 'PDEFB', '', 'N' , 'Y' ,'Y' , 185 , getdate() , 0),
    ('B03', 'PDEFBHEV', 'PDEFBHEV', '', 'N' , 'Y' ,'Y' , 186 , getdate() , 0),
    ('B03', 'PDEN', 'PDEN', '', 'N' , 'Y' ,'Y' , 187 , getdate() , 0),
    ('B03', 'PDEN PE', 'PDEN PE', '', 'N' , 'Y' ,'Y' , 188 , getdate() , 0),
    ('B03', 'PDENLE', 'PDENLE', '', 'N' , 'Y' ,'Y' , 189 , getdate() , 0),
    ('B03', 'PE', 'EV', '', 'N' , 'Y' ,'Y' , 190 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'PF', 'PF', '', 'N' , 'Y' ,'Y' , 191 , getdate() , 0),
    ('B03', 'PFC', 'PFC', '', 'N' , 'Y' ,'Y' , 192 , getdate() , 0),
    ('B03', 'PS', '소올', '', 'N' , 'Y' ,'Y' , 193 , getdate() , 0),
    ('B03', 'PU', '봉고3', '', 'N' , 'Y' ,'Y' , 194 , getdate() , 0),
    ('B03', 'PU201', 'PU201', '', 'N' , 'Y' ,'Y' , 195 , getdate() , 0),
    ('B03', 'PY1B', 'PY1B', '', 'N' , 'Y' ,'Y' , 196 , getdate() , 0),
    ('B03', 'QA', '갤로퍼', '', 'N' , 'Y' ,'Y' , 197 , getdate() , 0),
    ('B03', 'QC', 'QC', '', 'N' , 'Y' ,'Y' , 198 , getdate() , 0),
    ('B03', 'QE', 'QE', '', 'N' , 'Y' ,'Y' , 199 , getdate() , 0),
    ('B03', 'QF', 'K5 QF북미', '', 'N' , 'Y' ,'Y' , 200 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'QL', '스포티지 R', '', 'N' , 'Y' ,'Y' , 201 , getdate() , 0),
    ('B03', 'QLC', '스포티지 차이나', '', 'N' , 'Y' ,'Y' , 202 , getdate() , 0),
    ('B03', 'QLE', 'QLE', '', 'N' , 'Y' ,'Y' , 203 , getdate() , 0),
    ('B03', 'QLEPE', 'QLEPE', '', 'N' , 'Y' ,'Y' , 204 , getdate() , 0),
    ('B03', 'QM', 'QM', '', 'N' , 'Y' ,'Y' , 205 , getdate() , 0),
    ('B03', 'QMC', 'QMC', '', 'N' , 'Y' ,'Y' , 206 , getdate() , 0),
    ('B03', 'QSE', 'QSE', '', 'N' , 'Y' ,'Y' , 207 , getdate() , 0),
    ('B03', 'QX', '베뉴', '', 'N' , 'Y' ,'Y' , 208 , getdate() , 0),
    ('B03', 'QX1I', 'QX1I', '', 'N' , 'Y' ,'Y' , 209 , getdate() , 0),
    ('B03', 'QX1I LHD', 'QX1I LHD', '', 'N' , 'Y' ,'Y' , 210 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'QXI', 'QXI', '', 'N' , 'Y' ,'Y' , 211 , getdate() , 0),
    ('B03', 'QXI PE', 'QXI PE', '', 'N' , 'Y' ,'Y' , 212 , getdate() , 0),
    ('B03', 'QXI-PE', 'QXI-PE', '', 'N' , 'Y' ,'Y' , 213 , getdate() , 0),
    ('B03', 'QY', 'QY', '', 'N' , 'Y' ,'Y' , 214 , getdate() , 0),
    ('B03', 'QY 5P', 'QY5P', '', 'N' , 'Y' ,'Y' , 215 , getdate() , 0),
    ('B03', 'QY 7P', 'QY 7P', '', 'N' , 'Y' ,'Y' , 216 , getdate() , 0),
    ('B03', 'QY PE', 'QY PE', '', 'N' , 'Y' ,'Y' , 217 , getdate() , 0),
    ('B03', 'R0', 'R0', '', 'N' , 'Y' ,'Y' , 218 , getdate() , 0),
    ('B03', 'R020', 'R020', '', 'N' , 'Y' ,'Y' , 219 , getdate() , 0),
    ('B03', 'R1S', 'R1S', '', 'N' , 'Y' ,'Y' , 220 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'R1T', 'R1T', '', 'N' , 'Y' ,'Y' , 221 , getdate() , 0),
    ('B03', 'R233', 'R233', '', 'N' , 'Y' ,'Y' , 222 , getdate() , 0),
    ('B03', 'RB', '베르나 후속', '', 'N' , 'Y' ,'Y' , 223 , getdate() , 0),
    ('B03', 'RBC', 'RBC', '', 'N' , 'Y' ,'Y' , 224 , getdate() , 0),
    ('B03', 'RC', 'RC', '', 'N' , 'Y' ,'Y' , 225 , getdate() , 0),
    ('B03', 'RCC', 'RCC', '', 'N' , 'Y' ,'Y' , 226 , getdate() , 0),
    ('B03', 'RG3', 'G80', '', 'N' , 'Y' ,'Y' , 227 , getdate() , 0),
    ('B03', 'RJ1', 'K9(RJ1)', '', 'N' , 'Y' ,'Y' , 228 , getdate() , 0),
    ('B03', 'RJ1PE', 'K9', '', 'N' , 'Y' ,'Y' , 229 , getdate() , 0),
    ('B03', 'RP', '카렌스', '', 'N' , 'Y' ,'Y' , 230 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'RS4', 'G90', '', 'N' , 'Y' ,'Y' , 231 , getdate() , 0),
    ('B03', 'RT64', 'RT64', '', 'N' , 'Y' ,'Y' , 232 , getdate() , 0),
    ('B03', 'S2', '스펙트라', '', 'N' , 'Y' ,'Y' , 233 , getdate() , 0),
    ('B03', 'S401', 'S401', '', 'N' , 'Y' ,'Y' , 234 , getdate() , 0),
    ('B03', 'SA', 'SA', '', 'N' , 'Y' ,'Y' , 235 , getdate() , 0),
    ('B03', 'SAMPLE', 'SAMPLE', '', 'N' , 'Y' ,'Y' , 236 , getdate() , 0),
    ('B03', 'SC', 'SC', '', 'N' , 'Y' ,'Y' , 237 , getdate() , 0),
    ('B03', 'SG2', '니로', '', 'N' , 'Y' ,'Y' , 238 , getdate() , 0),
    ('B03', 'SJ', 'SJ', '', 'N' , 'Y' ,'Y' , 239 , getdate() , 0),
    ('B03', 'SK3', '쏘울', '', 'N' , 'Y' ,'Y' , 240 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'SL', '스포티지', '', 'N' , 'Y' ,'Y' , 241 , getdate() , 0),
    ('B03', 'SLC', 'SLC', '', 'N' , 'Y' ,'Y' , 242 , getdate() , 0),
    ('B03', 'SM', '싼타페 SM', '', 'N' , 'Y' ,'Y' , 243 , getdate() , 0),
    ('B03', 'SME', 'SM EV', '', 'N' , 'Y' ,'Y' , 244 , getdate() , 0),
    ('B03', 'SP', 'SP', '', 'N' , 'Y' ,'Y' , 245 , getdate() , 0),
    ('B03', 'SP2', '셀토스', '', 'N' , 'Y' ,'Y' , 246 , getdate() , 0),
    ('B03', 'SP2C', 'SP2C', '', 'N' , 'Y' ,'Y' , 247 , getdate() , 0),
    ('B03', 'SP2I', 'SP2I', '', 'N' , 'Y' ,'Y' , 248 , getdate() , 0),
    ('B03', 'SP2I PE', 'SP2I PE', '', 'N' , 'Y' ,'Y' , 249 , getdate() , 0),
    ('B03', 'SP2I PE CCB', 'SP2I PE CCB', '', 'N' , 'Y' ,'Y' , 250 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'SQ', 'SQ', '', 'N' , 'Y' ,'Y' , 251 , getdate() , 0),
    ('B03', 'SR', 'SR', '', 'N' , 'Y' ,'Y' , 252 , getdate() , 0),
    ('B03', 'SU2', 'SU2', '', 'N' , 'Y' ,'Y' , 253 , getdate() , 0),
    ('B03', 'SU2 ID', 'SU2 ID', '', 'N' , 'Y' ,'Y' , 254 , getdate() , 0),
    ('B03', 'SU2I', 'SU2I', '', 'N' , 'Y' ,'Y' , 255 , getdate() , 0),
    ('B03', 'SU2I LWB PE', 'SU2I LWB PE', '', 'N' , 'Y' ,'Y' , 256 , getdate() , 0),
    ('B03', 'SX2', '코나', '', 'N' , 'Y' ,'Y' , 257 , getdate() , 0),
    ('B03', 'SX2E', 'SX2E', '', 'N' , 'Y' ,'Y' , 258 , getdate() , 0),
    ('B03', 'SX2ID', 'SX2ID', '', 'N' , 'Y' ,'Y' , 259 , getdate() , 0),
    ('B03', 'TAM', '레이', '', 'N' , 'Y' ,'Y' , 260 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'TB', '클릭', '', 'N' , 'Y' ,'Y' , 261 , getdate() , 0),
    ('B03', 'TBI', 'TBI', '', 'N' , 'Y' ,'Y' , 262 , getdate() , 0),
    ('B03', 'TC', 'TC', '', 'N' , 'Y' ,'Y' , 263 , getdate() , 0),
    ('B03', 'TCC', 'TCC', '', 'N' , 'Y' ,'Y' , 264 , getdate() , 0),
    ('B03', 'TD', '포르테', '', 'N' , 'Y' ,'Y' , 265 , getdate() , 0),
    ('B03', 'TF', 'K5(TF)', '', 'N' , 'Y' ,'Y' , 266 , getdate() , 0),
    ('B03', 'TFC', 'TFC', '', 'N' , 'Y' ,'Y' , 267 , getdate() , 0),
    ('B03', 'TG', '그랜져TG', '', 'N' , 'Y' ,'Y' , 268 , getdate() , 0),
    ('B03', 'TL', '투싼 TL', '', 'N' , 'Y' ,'Y' , 269 , getdate() , 0),
    ('B03', 'TLC', 'TLC', '', 'N' , 'Y' ,'Y' , 270 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'TLE', 'TLE', '', 'N' , 'Y' ,'Y' , 271 , getdate() , 0),
    ('B03', 'TM', '싼타페 TM', '', 'N' , 'Y' ,'Y' , 272 , getdate() , 0),
    ('B03', 'TMC', 'TMC', '', 'N' , 'Y' ,'Y' , 273 , getdate() , 0),
    ('B03', 'TQ', '스타렉스 후속', '', 'N' , 'Y' ,'Y' , 274 , getdate() , 0),
    ('B03', 'TQ PE', '스타렉스 리무진', '', 'N' , 'Y' ,'Y' , 275 , getdate() , 0),
    ('B03', 'TR8', 'TR8', '', 'N' , 'Y' ,'Y' , 276 , getdate() , 0),
    ('B03', 'TU', '프론티어', '', 'N' , 'Y' ,'Y' , 277 , getdate() , 0),
    ('B03', 'U554', 'U554', '', 'N' , 'Y' ,'Y' , 278 , getdate() , 0),
    ('B03', 'UB', '프라이드 UB', '', 'N' , 'Y' ,'Y' , 279 , getdate() , 0),
    ('B03', 'UC', 'UC', '', 'N' , 'Y' ,'Y' , 280 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'UC 5D', 'UC 5D', '', 'N' , 'Y' ,'Y' , 281 , getdate() , 0),
    ('B03', 'UD', 'MD USA', '', 'N' , 'Y' ,'Y' , 282 , getdate() , 0),
    ('B03', 'UM', '쏘렌토 UM', '', 'N' , 'Y' ,'Y' , 283 , getdate() , 0),
    ('B03', 'UMA', '쏘렌토 UM 북미', '', 'N' , 'Y' ,'Y' , 284 , getdate() , 0),
    ('B03', 'UN', 'UN', '', 'N' , 'Y' ,'Y' , 285 , getdate() , 0),
    ('B03', 'US4', '스타렉스', '', 'N' , 'Y' ,'Y' , 286 , getdate() , 0),
    ('B03', 'V GEN2', 'V GEN2', '', 'N' , 'Y' ,'Y' , 287 , getdate() , 0),
    ('B03', 'VF', 'YF웨건', '', 'N' , 'Y' ,'Y' , 288 , getdate() , 0),
    ('B03', 'VG', 'K7(VG)', '', 'N' , 'Y' ,'Y' , 289 , getdate() , 0),
    ('B03', 'VI', '에쿠스(VI)', '', 'N' , 'Y' ,'Y' , 290 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'VN41S', 'ID BUZZ', '', 'N' , 'Y' ,'Y' , 291 , getdate() , 0),
    ('B03', 'VQ', 'VQ', '', 'N' , 'Y' ,'Y' , 292 , getdate() , 0),
    ('B03', 'W2', 'W2', '', 'N' , 'Y' ,'Y' , 293 , getdate() , 0),
    ('B03', 'W201', 'W201', '', 'N' , 'Y' ,'Y' , 294 , getdate() , 0),
    ('B03', 'X0', 'X0', '', 'N' , 'Y' ,'Y' , 295 , getdate() , 0),
    ('B03', 'X02A', 'X02A', '', 'N' , 'Y' ,'Y' , 296 , getdate() , 0),
    ('B03', 'X1', 'X1', '', 'N' , 'Y' ,'Y' , 297 , getdate() , 0),
    ('B03', 'X11M', 'X11M', '', 'N' , 'Y' ,'Y' , 298 , getdate() , 0),
    ('B03', 'X2', 'X-2', '', 'N' , 'Y' ,'Y' , 299 , getdate() , 0),
    ('B03', 'X3', 'X-3', '', 'N' , 'Y' ,'Y' , 300 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'X3I', 'X3I', '', 'N' , 'Y' ,'Y' , 301 , getdate() , 0),
    ('B03', 'XD', '아반떼 XD', '', 'N' , 'Y' ,'Y' , 302 , getdate() , 0),
    ('B03', 'XDC', 'XDC', '', 'N' , 'Y' ,'Y' , 303 , getdate() , 0),
    ('B03', 'XDI', 'XDI', '', 'N' , 'Y' ,'Y' , 304 , getdate() , 0),
    ('B03', 'XG', '그랜져 XG', '', 'N' , 'Y' ,'Y' , 305 , getdate() , 0),
    ('B03', 'XM', '쏘렌토 XM', '', 'N' , 'Y' ,'Y' , 306 , getdate() , 0),
    ('B03', 'XMA', '쏘렌토 XM북미', '', 'N' , 'Y' ,'Y' , 307 , getdate() , 0),
    ('B03', 'Y1', 'Y1', '', 'N' , 'Y' ,'Y' , 308 , getdate() , 0),
    ('B03', 'Y3', '소나타 2', '', 'N' , 'Y' ,'Y' , 309 , getdate() , 0),
    ('B03', 'YC', 'YC', '', 'N' , 'Y' ,'Y' , 310 , getdate() , 0)
;

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B03', 'YD', 'YD', '', 'N' , 'Y' ,'Y' , 311 , getdate() , 0),
    ('B03', 'YDC', 'YDC', '', 'N' , 'Y' ,'Y' , 312 , getdate() , 0),
    ('B03', 'YDM', 'YDM', '', 'N' , 'Y' ,'Y' , 313 , getdate() , 0),
    ('B03', 'YF', 'YF 소나타', '', 'N' , 'Y' ,'Y' , 314 , getdate() , 0),
    ('B03', 'YFC', 'YFC', '', 'N' , 'Y' ,'Y' , 315 , getdate() , 0),
    ('B03', 'YFI', 'YFI', '', 'N' , 'Y' ,'Y' , 316 , getdate() , 0),
    ('B03', 'YG', 'K7(YG)', '', 'N' , 'Y' ,'Y' , 317 , getdate() , 0),
    ('B03', 'YN', 'YN', '', 'N' , 'Y' ,'Y' , 318 , getdate() , 0),
    ('B03', 'YP', '카니발(YP)', '', 'N' , 'Y' ,'Y' , 319 , getdate() , 0),
    ('B03', 'Z9', 'Z9', '', 'N' , 'Y' ,'Y' , 320 , getdate() , 0)
;

/* 재질구분 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('B01', '재질구분', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B01', '10', '철판,스크랩', '', 'N' , 'Y' , 'Y' , 10 , getdate() , 0),
    ('B01', '15', '파이프', '', 'N' , 'Y' , 'Y' , 15 , getdate() , 0),
    ('B01', '20', '플라스틱', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B01', '25', 'HW선재', '', 'N' , 'Y' , 'Y' , 25 , getdate() , 0),
    ('B01', '30', '특수강', '', 'N' , 'Y' ,'Y' , 30 , getdate() , 0),
    ('B01', '35', '시트선재', '', 'N' , 'Y' ,'Y' , 35 , getdate() , 0),
    ('B01', '40', '전기동', '', 'N' , 'Y' ,'Y' , 40 , getdate() , 0),
    ('B01', '45', '알루미늄', '', 'N' , 'Y' ,'Y' , 45 , getdate() , 0);

/* 강종 */
INSERT INTO ST_CODE_GROUP (CG_ID, CG_NM, CG_REMARK, DISPLAY_AT, USE_AT, REG_DT, REG_UID)
VALUES('B05', '강종', '', 'Y', 'Y', getdate(), 0);

INSERT INTO ST_CODE(CG_ID, CD, CD_NM, CD_REMARK, DEFAULT_AT, DISPLAY_AT, USE_AT, DISPLAY_ORD, REG_DT, REG_UID)
VALUES
    ('B05', 'CR', 'CR', '', 'N' , 'Y' , 'Y' , 15 , getdate() , 0),
    ('B05', 'PO', 'PO', '', 'N' , 'Y' ,'Y' , 50 , getdate() , 0),
    ('B05', '30', '합금강판', '', 'N' , 'Y' ,'Y' , 35 , getdate() , 0),
    ('B05', '40', '연강선', '', 'N' , 'Y' ,'Y' , 45 , getdate() , 0),
    ('B05', '50', '경강선', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B05', 'HR', 'HR', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B05', 'HW', 'HW', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B05', 'PA', 'PA+ABS', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B05', 'PA6', 'PA6', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0),
    ('B05', 'PA66', 'PA66', '', 'N' , 'Y' , 'Y' , 20 , getdate() , 0);

/* 메뉴 등록정보 */
INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (1,'대시보드','대시보드', 'dashboard',0,NULL,1,'M','/dashboard',NULL,'Y',1,'Y', getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (10,'내정보', '내정보','mypage', 0,NULL, 1,'M','/mypage',NULL,'Y',999,'Y',getdate(),0);


INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (20,'시스템관리','시스템관리','manage-system',0,NULL,1,'D',NULL,NULL,'Y',10,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (21,'시스템환경설정','시스템환경설정','config-system',20,NULL,2,'M','/system/setting',NULL,'Y',12,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (22,'시스템메뉴관리','시스템메뉴','manage-menu',2,NULL,20,'M','/sysem/menu',NULL,'Y',14,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (23,'사용자관리','사용자','manage-user', 20,NULL,2,'M','/system/user',NULL,'Y',16,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (24,'사용자그룹관리','사용자그룹','manage-user-group', 20,NULL,2,'M','/system/user-group',NULL,'Y',18,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (25,'공통코드관리','공통코드','manage-common-code', 20,NULL,2,'M','/system/common-code',NULL,'Y',20,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (26,'사내 공지사항관리', '사내 공지사항관리','manage-internal-notice', 20,NULL,2,'M','/system/notice/internal',NULL,'Y',22,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (27,'협력사 공지사항관리', '협력사 공지사항관리','manage-partner-notice', 20,NULL,2,'M','/system/notice/partner',NULL,'Y',24,'Y',getdate(),0);


INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (40,'기준정보', '기준정보','manage-code', 0,NULL, 1,'D',NULL,NULL,'Y',30,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (41,'협력업체', '협력업체','manage-partner', 40,NULL, 2,'M','/master/partner',NULL,'Y',32,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (42,'품목정보', '품목정보','manage-item', 40,NULL, 2,'M','/master/item',NULL,'Y',34,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (43,'협력사 품목정보', '협력사 품목정보','manage-partner-item', 40,NULL, 2,'M','/master/partner-item',NULL,'Y',36,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (44,'품목연결관리', '품목연결관리','manage-link-item', 40,NULL, 2,'M','/master/link-item',NULL,'Y',38,'Y',getdate(),0);


INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (15,'가격변동관리', '가격변동관리','manage-change-price', 0,NULL, 1,'D',NULL,NULL,'Y',50,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (16,'원소재 공시단가', '원소재 공시단가','manage-matrial-unit-price', 15,NULL, 2,'M','/price/matrial/unit-price',NULL,'Y',52,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (17,'최종매입단가', '최종매입단가','manage-purchase-price', 15, NULL, 2,'M','/price/purchase-price',NULL,'Y',54,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (18,'납품단가변경관리', '납품단가변경관리','manage-price-adjustment', 15, NULL, 2,'M','/price/price-adjustment',NULL,'Y',56,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (19,'가격협정서', '가격협정서','price-adjustment-agreement', 15, NULL, 2,'M','/price/adjustment-agreement',NULL,'Y',58,'Y',getdate(),0);


INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (30,'커뮤니티', '커뮤니티','community', 0, NULL, 1,'D',NULL,NULL,'Y',50,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (31,'내부 공지사항', '내부공지사항','notice-internal', 30,NULL, 2,'M','/community/notice/internal',NULL,'Y',52,'Y',getdate(),0);

INSERT INTO ST_MENU (MENU_UID,MENU_NM,MENU_SHORT_NM,MENU_CD,PARENT_UID,MENU_ICON,MENU_LVL,MENU_TYPE,MENU_PATH,REDIRECT_PATH,DISPLAY_AT,DISPLAY_ORD,USE_AT,REG_DT,REG_UID)
VALUES (32,'협력사 공지사항', '협력사 공지사항','notice-partner', 30,NULL, 2,'M','/community/notice/partner',NULL,'Y',54,'Y',getdate(),0);

/* 회사 정보 */
INSERT INTO MT_CORPORATION (corp_cd, corp_nm, reg_uid)
values
('1000', '동탄', 1),
('1100', '경주', 1),
('1200', '아산', 1),
('1300', '영인', 1),
('1900', '물류센터', 1);

/* 플랜트 */
INSERT INTO MT_PLANT
(plant_cd, plant_nm, use_at, reg_dt, reg_uid)
VALUES
    ('1000', '동탄', 'Y', getdate(), 0),
    ('1100', '경주', 'Y', getdate(), 0),
    ('1110', '경주1공장', 'Y', getdate(), 0),
    ('1150', '연암공장', 'Y', getdate(), 0),
    ('1170', '광주', 'Y', getdate(), 0),
    ('1200', '아산', 'Y', getdate(), 0),
    ('1300', '영인', 'Y', getdate(), 0),
    ('1900', '물류센타', 'Y', getdate(), 0)
;


