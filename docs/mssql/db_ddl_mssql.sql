CREATE SEQUENCE CERT_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE USER_SEQ
    AS NUMERIC(20, 0)
    START WITH 100
    INCREMENT BY 1;

CREATE SEQUENCE GROUP_SEQ
    AS NUMERIC(20, 0)
    START WITH 100
    INCREMENT BY 1;

CREATE SEQUENCE TOKEN_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE BBS_IA_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE BBS_EA_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE BBS_PA_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

-- 협력사 품목 순번
CREATE SEQUENCE PARTNER_ITEM_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

-- 구매단가 순번
CREATE SEQUENCE PURCHASE_PRICE_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

-- 사급매출단가 순번
CREATE SEQUENCE CONSIGNED_PRICE_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

-- 사급매출단가 순번
CREATE SEQUENCE PRICE_CHANGE_SEQ
    AS NUMERIC(20, 0)
    START WITH 1
    INCREMENT BY 1;

-- 사용자 리프레시 토큰 정보
CREATE TABLE ST_REFRESH_TOKEN
(
    token_id NUMERIC(20, 0) NOT NULL,
    user_uid NUMERIC(20, 0) NOT NULL,
    token    VARCHAR(1024) NULL,
    PRIMARY KEY (token_id)
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '리프레시토큰', 'schema', 'dbo', 'table', 'ST_REFRESH_TOKEN';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '토크ID', 'schema', 'dbo', 'table', 'ST_REFRESH_TOKEN', 'column', 'token_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자 UID', 'schema', 'dbo', 'table', 'ST_REFRESH_TOKEN', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '리프레시토큰', 'schema', 'dbo', 'table', 'ST_REFRESH_TOKEN', 'column', 'token';


/* 시스템 환경정보 KEY-VALUE */
CREATE TABLE ST_CONFIG(
    env_key             NVARCHAR(50)   NOT NULL,
    env_value           NVARCHAR(200)  NOT NULL,
    category            NVARCHAR(30)       NULL,
    env_remark          NVARCHAR(MAX)     NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    PRIMARY KEY (env_key)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '시스템설정정보', 'schema', 'dbo', 'table', 'ST_CONFIG';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '키', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'env_key';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '설정값', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'env_value';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '분류', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'category';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '항목설명', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'env_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'ST_CONFIG', 'column', 'reg_uid';


/* 회원가입 본인 인증 */
-- DROP TABLE ST_IDENTIFY_VERIFICATION;

CREATE TABLE ST_IDENTIFY_VERIFICATION(
    cert_seq            NUMERIC(20, 0) NOT NULL,
    account_id          NVARCHAR(128) NOT NULL,
    user_email          VARCHAR(128) NULL,
    user_phone          VARCHAR(20) NULL,
    cert_code           VARCHAR(10) NOT NULL,
    expired_dt          DATETIME NOT NULL,
    check_at            CHAR(1) DEFAULT 'N' NOT NULL,
    PRIMARY KEY (cert_seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회원가입 본인인증 인증코드', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '순번', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'cert_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정ID', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'account_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수신자메일주소', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'user_email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수신자핸드폰번호', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'user_phone';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '인증코드', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'cert_code';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '인증만료시간', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'expired_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '인증확인여부', 'schema', 'dbo', 'table', 'ST_IDENTIFY_VERIFICATION', 'column', 'check_at';


/* 코드 그룹정보 */
CREATE TABLE ST_CODE_GROUP(
    cg_id               NVARCHAR(10) NOT NULL,
    cg_nm               NVARCHAR(50) NOT NULL,
    cg_remark           NVARCHAR(200)  NULL,
    display_at          CHAR(1) DEFAULT 'Y' NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    PRIMARY KEY (cg_id)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공통코드 그룹', 'schema', 'dbo', 'table', 'ST_CODE_GROUP';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '코드그룹ID', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'cg_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '명칭', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'cg_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'cg_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '노출여부 Y/N', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'display_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부 Y/N', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자 UID', 'schema', 'dbo', 'table', 'ST_CODE_GROUP', 'column', 'reg_uid';



/* 시스템 공통 코드정보 */
CREATE TABLE ST_CODE(
    cg_id               NVARCHAR(10) NOT NULL,
    cd                  NVARCHAR(20) NOT NULL,
    cd_nm               NVARCHAR(200) NOT NULL,
    upper_cd            NVARCHAR(20) NULL,
    cd_remark           NVARCHAR(200) NULL,
    add_data1           NVARCHAR(100) NULL,
    add_data2           NVARCHAR(100) NULL,
    add_data3           NVARCHAR(100) NULL,
    default_at          CHAR(1) DEFAULT 'Y' NOT NULL,
    display_at          CHAR(1) DEFAULT 'Y' NOT NULL,
    display_ord         NUMERIC(5,0) DEFAULT 999 NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (cg_id, cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공통코드 그룹', 'schema', 'dbo', 'table', 'ST_CODE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '코드그룹ID', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'cg_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '코드', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '명칭', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'cd_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '상위코드', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'upper_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'cd_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '추가값1', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'add_data1';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '추가값2', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'add_data2';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '추가값3', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'add_data3';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '디폴트여부', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'default_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '노출여부', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'display_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '정렬순서', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'display_ord';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록UID', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정UID', 'schema', 'dbo', 'table', 'ST_CODE', 'column', 'mod_uid';

/* 로그인사용자 정보 */
-- DROP TABLE ST_USERS;
CREATE TABLE ST_USERS
(
    user_uid            NUMERIC(20,0)  NOT NULL,
    account_id          NVARCHAR(128)  NOT NULL,
    account_name        NVARCHAR(128)  NOT NULL,
    account_pwd         NVARCHAR(512)  NOT NULL,
    account_nickname    NVARCHAR(128),
    account_type        CHAR(1) DEFAULT 'I',
    pwd_hash_value      NVARCHAR(512),
    pwd_expired_dt      DATE,
    active              CHAR(1) DEFAULT 'Y' NOT NULL,
    email               NVARCHAR(128),
    verified_email      CHAR(1) DEFAULT 'N',
    mobile              NVARCHAR(128),
    verified_mobile     CHAR(1) DEFAULT 'N',
    account_locked      CHAR(1) DEFAULT 'N',
    account_expired_dt  DATE,
    account_tel         NVARCHAR(20),
    last_login_ip       NVARCHAR(30),
    last_login_dt       DATETIME DEFAULT getdate() NULL,
    corp_cd             NVARCHAR(20),
    plant_cd             NVARCHAR(20),
    org_cd              NVARCHAR(20),
    team_cd             NVARCHAR(20),
    bp_cd               NVARCHAR(20),
    user_sign           NVARCHAR(MAX),
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
     PRIMARY KEY (user_uid)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '로그인 사용자', 'schema', 'dbo', 'table', 'ST_USERS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정 UID', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '로그인 계정', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자명', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '암호', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_pwd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '별명', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_nickname';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정유형', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '초기화 암호해시값', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'pwd_hash_value';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '암호만료일시', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'pwd_expired_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '활성화여부', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'active';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이메일주소', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이메일 검증여부', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'verified_email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '핸드폰번호', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'mobile';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '핸드폰 번호 검증여부', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'verified_mobile';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정잠금여부', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_locked';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정만료일시', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_expired_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정 전화번호', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'account_tel';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '최근 로그인 주소', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'last_login_ip';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '최근 로그인 시간', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'last_login_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업장코드', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'corp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부서코드', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'org_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '팀코드', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'team_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자서명', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'user_sign';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록UID', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정UID', 'schema', 'dbo', 'table', 'ST_USERS', 'column', 'mod_uid';

CREATE TABLE MT_EFORM_MEMBER
(
    ef_id           NVARCHAR(50) NOT NULL,          -- 이메일 또는 난수
    account_id      NVARCHAR(50) NOT NULL,
    ef_name         NVARCHAR(30)     NULL,
    is_refused      NVARCHAR(1) DEFAULT 'N' NULL,
    is_invited      NVARCHAR(1) DEFAULT 'N' NULL,
    is_withdrawal   NVARCHAR(1) DEFAULT 'N' NULL,
    is_expired      NVARCHAR(1) DEFAULT 'N' NULL,
    is_deleted      NVARCHAR(1) DEFAULT 'N' NULL,
    is_enabled      NVARCHAR(1) DEFAULT 'Y' NULL,
    reg_dt          DATETIME DEFAULT getdate() NOT NULL,
    PRIMARY KEY (ef_id)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 멤버', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 ID', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'ef_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 계정ID', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'account_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 사용자명', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'ef_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '초대거절여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_refused';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '초대여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_invited';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '멤버탈퇴여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_withdrawal';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '초대만료여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_expired';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '탈퇴여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_deleted';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '활성화여부', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'is_enabled';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_EFORM_MEMBER', 'column', 'reg_dt';


/* 사용자 권한 정보 */
-- DROP TABLE ST_USER_ROLES;
CREATE TABLE ST_USER_ROLES(
    user_uid            NUMERIC(20, 0)  NOT NULL,
    user_role           NVARCHAR(128) DEFAULT 'USER' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    PRIMARY KEY (user_uid, user_role)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자역할', 'schema', 'dbo', 'table', 'ST_USER_ROLES';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '계정 UID', 'schema', 'dbo', 'table', 'ST_USER_ROLES', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자 ROLE', 'schema', 'dbo', 'table', 'ST_USER_ROLES', 'column', 'user_role';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_USER_ROLES', 'column', 'reg_dt';

/* 사용자 그룹 */
--DROP TABLE ST_AUTH_GROUPS;

CREATE TABLE ST_AUTH_GROUPS (
    grp_uid             NUMERIC(20, 0) NOT NULL,
    grp_name            NVARCHAR(100) NOT NULL,
    grp_remark          NVARCHAR(200) NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (grp_uid)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자그룹', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '그룹ID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'grp_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '그룹명', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'grp_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'grp_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUPS', 'column', 'mod_uid';

/* 그룹 멤버 */
CREATE TABLE ST_AUTH_GROUP_MEMBER(
     grp_uid            NUMERIC(20, 0) NOT NULL,
     user_uid           NUMERIC(20, 0)  NOT NULL,
     use_at             CHAR(1) DEFAULT 'Y' NOT NULL ,
     reg_dt             DATETIME DEFAULT getdate() NOT NULL,
     reg_uid            NUMERIC(20, 0)  NOT NULL,
     mod_dt             DATETIME NULL,
     mod_uid            NUMERIC(20, 0)  NULL,
     PRIMARY KEY (user_uid, grp_uid)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자그룹멤버', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '그룹UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'grp_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부(Y/N)', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정UID', 'schema', 'dbo', 'table', 'ST_AUTH_GROUP_MEMBER', 'column', 'mod_uid';

/* 시스템 리소스 */
CREATE TABLE ST_MENU (
    menu_uid            NUMERIC(20, 0)  NOT NULL,
    menu_nm             NVARCHAR(128) NOT NULL,
    menu_short_nm       NVARCHAR(128) NOT NULL,
    menu_cd             NVARCHAR(128) NULL,
    parent_uid          NUMERIC(20, 0)  DEFAULT 0 NULL,
    menu_icon           NVARCHAR(128) NULL,
    menu_lvl            NUMERIC(3, 0)  DEFAULT 1 NULL,
    menu_type           CHAR(1) DEFAULT 'M' NULL,
    menu_path           NVARCHAR(512) NULL,
    redirect_path       NVARCHAR(512) NULL,
    display_at          CHAR(1) DEFAULT 'Y' NOT NULL ,
    display_ord         NUMERIC(10, 0)  DEFAULT 999 NOT NULL,
    menu_remark         NVARCHAR(1024) NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (menu_uid)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '시스템메뉴', 'schema', 'dbo', 'table', 'ST_MENU';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴 UID', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴명', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴명', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_short_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴코드', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부모메뉴 UID', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'parent_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴아이콘', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_icon';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴레벨', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_lvl';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴유형 S-Section, D: 디렉토리, M-메뉴', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴 URL 경로', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_path';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴 리타이렉트 경로', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'redirect_path';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '표시여부(Y/N)', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'display_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '표시순서', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'display_ord';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴설명', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'menu_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부(Y/N)', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'ST_MENU', 'column', 'mod_uid';


/* 그룹 권한 */
CREATE TABLE ST_GROUP_AUTHORITY(
    grp_uid             NUMERIC(20, 0) NOT NULL,
    menu_uid            NUMERIC(20, 0)  NOT NULL,
    view_perm_at        CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_perm_at         CHAR(1) DEFAULT 'Y' NOT NULL,
    mod_perm_at         CHAR(1) DEFAULT 'Y' NOT NULL,
    del_perm_at         CHAR(1) DEFAULT 'Y' NOT NULL,
    exec_perm_at        CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (grp_uid, menu_uid)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자그룹 권한', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '그룹 ID', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'grp_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메뉴 UID', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'menu_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '조회권한(Y/N)', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'view_perm_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록권한(Y/N)', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'reg_perm_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정권한(Y/N)', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'mod_perm_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '삭제권한(Y/N)', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'del_perm_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '실행권한(Y/N)', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'exec_perm_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'ST_GROUP_AUTHORITY', 'column', 'mod_uid';


/* Remember Me 자동 로그인 정보 */
CREATE TABLE ST_PERSISTENT_LOGINS(
    series              NVARCHAR(64) NOT NULL,
    user_uid            NUMERIC(20, 0)  NOT NULL,
    token               VARCHAR(256) NOT NULL,
    last_used           DATETIME DEFAULT getdate() NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'Remember Me 자동 로그인 정보', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '시리즈', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS', 'column', 'series';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자UID', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '인증토큰', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS', 'column', 'token';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '최근사용일시', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS', 'column', 'last_used';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'ST_PERSISTENT_LOGINS', 'column', 'reg_dt';

/*
 회사정보 * 플랜트
 1. 경주 : 1100
    3. 광주 : 1170
    7. 경주1공장 : 1110
    8. 연암공장 : 1150
 2. 아산 : 1200
 4. 영인 : 1300
 5. 동탄 : 1000
 6. 물류센타 : 1900

 */
/* 회사 정보 */
CREATE TABLE MT_CORPORATION (
    corp_cd             NVARCHAR(20)   NOT NULL ,
    corp_nm             NVARCHAR(100)  NOT NULL ,
    corp_short_nm       NVARCHAR(100)  NULL,
    corp_country        NVARCHAR(10)   DEFAULT 'KR' NULL,
    biz_reg_no          NVARCHAR(16)   NULL,
    post_no             NVARCHAR(10)   NULL ,
    corp_addr           NVARCHAR(500)  NULL ,
    corp_addr_dtl       NVARCHAR(500)  NULL ,
    ceo_nm              NVARCHAR(100)  NULL,
    biz_type            NVARCHAR(100)  NULL ,
    biz_item            NVARCHAR(200)  NULL ,
    corp_tel_no         NVARCHAR(20)   NULL ,
    corp_email          NVARCHAR(50)   NULL ,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (corp_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업장정보', 'schema', 'dbo', 'table', 'MT_CORPORATION';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업자ID', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업장명', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회사 약식명', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_short_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업자등록번호', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'biz_reg_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '우편번호', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'post_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기본주소', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_addr';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '상세주소', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_addr_dtl';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표자', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'ceo_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '업태', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'biz_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '종목', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'biz_item';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표전화번호', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_tel_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이메일', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'corp_email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부-Y/N', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_CORPORATION', 'column', 'mod_uid';

/* 플랜트 정보  */
CREATE TABLE MT_PLANT
(
    plant_cd            NVARCHAR(20)   NOT NULL ,
    plant_nm            NVARCHAR(100)  NOT NULL ,
    corp_cd             NVARCHAR(20)   NOT NULL ,
    plant_country       NVARCHAR(10)   NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (plant_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트정보', 'schema', 'dbo', 'table', 'MT_PLANT';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'MT_PLANT', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트명', 'schema', 'dbo', 'table', 'MT_PLANT', 'column', 'plant_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회사코드', 'schema', 'dbo', 'table', 'MT_PLANT', 'column', 'corp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '국가', 'schema', 'dbo', 'table', 'MT_PLANT', 'column', 'plant_country';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'MT_PLANT', 'column', 'use_at';

/*
    1. 공급사 정보
        조건 : 플랜트
        플랜드 / 회사코드 기준으로 관리
    2. 고객사 정보
        회사코드 기준으로 관리

    - 담당자 정보는 없음
    - 외국기업의 영우 사업자번호 9999999999(10자리)로 등록
    - 주소는 하나의 값으로 전달됨 (기본주소 + 상세주소)
*/
/* 협력사 정보 */
CREATE TABLE MT_BIZ_PARTNER (
    bp_cd               NVARCHAR(20)  NOT NULL,
    bp_nm               NVARCHAR(100) NOT NULL,
    bp_tax_nm           NVARCHAR(100)     NULL,
    biz_reg_no          NVARCHAR(20)  NULL,
    ceo_nm              NVARCHAR(100)  NULL,
    post_no             NVARCHAR(10)  NULL,
    bp_adrs             NVARCHAR(1000) NULL,
    bp_email            NVARCHAR(20)  NULL,
    bp_tel_no           NVARCHAR(20)  NULL,
    bp_remark           NVARCHAR(500)  NULL,
    contact_nm          NVARCHAR(50)  NULL,
    contact_email       NVARCHAR(50)  NULL,
    contact_mobile      NVARCHAR(50)  NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    display_at          CHAR(1) DEFAULT 'Y' NOT NULL,
    display_ord         NUMERIC(5,0) DEFAULT 999 NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (bp_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사명', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사 정식명', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_tax_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사업자번호', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'biz_reg_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표자', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'ceo_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '우편번호', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'post_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '주소', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_adrs';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표메일주소', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표전화번호', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_tel_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'bp_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '담당자명', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'contact_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '담당자이메일', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'contact_email';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '담당자핸드폰', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'contact_mobile';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '표시여부', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'display_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '표시순서', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'display_ord';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_BIZ_PARTNER', 'column', 'mod_uid';

-- 회사 및 플랜트 관리 BP
CREATE TABLE MT_BP_RELATION
(
    corp_cd             NVARCHAR(20)    NOT NULL,
    plant_cd            NVARCHAR(20)    NOT NULL,
    bp_cd               NVARCHAR(20)    NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    if_seq              NVARCHAR(20)    NULL,
    if_type             NVARCHAR(4)     NULL,
    if_result           NVARCHAR(1)     NULL,
    if_message          NVARCHAR(100)   NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME        NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (corp_cd, plant_cd, bp_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회사 및 플랜트 관리 BP', 'schema', 'dbo', 'table', 'MT_BP_RELATION';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회사코드', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'corp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF번호', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'if_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF유형', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'if_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF결과', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'if_result';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF메시지', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'if_message';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_BP_RELATION', 'column', 'mod_uid';


-- 고객사 정보
CREATE TABLE MT_CUSTOMER_RELATION
(
    corp_cd             NVARCHAR(20)    NOT NULL,
    bp_cd               NVARCHAR(20)    NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    if_seq              NVARCHAR(20)    NULL,
    if_type             NVARCHAR(4)     NULL,
    if_result           NVARCHAR(1)     NULL,
    if_message          NVARCHAR(100)   NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME        NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (corp_cd, bp_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '고객사', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '회사코드', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'corp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BP코드', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF번호', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'if_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF유형', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'if_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF결과', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'if_result';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'IF메시지', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'if_message';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_CUSTOMER_RELATION', 'column', 'mod_uid';

/* 고개사 */
CREATE VIEW MV_CUSTOMER
AS
select a.corp_cd, a.bp_cd, b.bp_nm, b.bp_tax_nm, b.biz_reg_no, b.ceo_nm, b.post_no, b.bp_adrs, b.bp_email, b.bp_tel_no,
       b.bp_remark, b.contact_nm, b.contact_email, b.contact_mobile, b.display_at, b.display_ord,
       a.use_at, dbo.FN_GET_CODE_NAME('C03', a.use_at) AS use_at_nm,
       a.if_seq, a.if_type, a.if_result, a.if_message,
       CONVERT(NVARCHAR(19), a.reg_dt, 120) AS reg_dt, a.reg_uid,
       CONVERT(NVARCHAR(19), a.mod_dt, 120) AS mod_dt,
       a.mod_uid
from MT_CUSTOMER_RELATION a
         join MT_BIZ_PARTNER b ON a.bp_cd = b.bp_cd;

/* 공급사 */
CREATE VIEW MV_SUPPLIER
AS
select a.corp_cd, c.corp_nm, a.plant_cd, d.plant_nm,
       a.bp_cd, b.bp_nm, b.bp_tax_nm, b.biz_reg_no, b.ceo_nm, b.post_no, b.bp_adrs, b.bp_email, b.bp_tel_no,
       b.bp_remark, b.contact_nm, b.contact_email, b.contact_mobile, b.display_at, b.display_ord,
       a.use_at, dbo.FN_GET_CODE_NAME('C03', a.use_at) AS use_at_nm,
       a.if_seq, a.if_type, a.if_result, a.if_message,
       CONVERT(NVARCHAR(19), a.reg_dt, 120) AS reg_dt, a.reg_uid,
       CONVERT(NVARCHAR(19), a.mod_dt, 120) AS mod_dt,
       a.mod_uid
from MT_BP_RELATION a WITH(NOLOCK)
         join MT_BIZ_PARTNER b WITH(NOLOCK) ON a.bp_cd = b.bp_cd
         join MT_PLANT d WITH(NOLOCK) ON a.plant_cd = d.plant_cd
         join MT_CORPORATION c WITH(NOLOCK) ON a.corp_cd = c.corp_cd;


/* 담당자별 협력사 */
CREATE TABLE MT_EMP_BP
(
    user_uid            NUMERIC(20,0)   NOT NULL,
    bp_cd               NVARCHAR(20)    NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (user_uid, bp_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '담당BP', 'schema', 'dbo', 'table', 'MT_EMP_BP';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용자UID', 'schema', 'dbo', 'table', 'MT_EMP_BP', 'column', 'user_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BP코드', 'schema', 'dbo', 'table', 'MT_EMP_BP', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_EMP_BP', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_EMP_BP', 'column', 'reg_uid';


/* 첨부파일 */
-- DROP TABLE CT_FILES;
CREATE TABLE CT_FILES (
    atch_file_id        NVARCHAR(100) NOT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (atch_file_id)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일', 'schema', 'dbo', 'table', 'CT_FILES';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일ID', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'atch_file_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부-Y/N', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'CT_FILES', 'column', 'mod_uid';

/* 첨부파일상세 */
CREATE TABLE CT_FILE_DTL (
   atch_file_id         NVARCHAR(100)  NOT NULL,
   file_sn              NUMERIC(3, 0)    NOT NULL,
   file_store_path      NVARCHAR(1024) NULL,
   relative_path        NVARCHAR(1024) NULL,
   store_file_nm        NVARCHAR(512)  NULL,
   original_file_nm     NVARCHAR(512)  NULL,
   file_extension       NVARCHAR(20)   NULL,
   file_size            NUMERIC(20, 0) DEFAULT 0,
   use_at               CHAR(1) DEFAULT 'Y' NOT NULL,
   reg_dt               DATETIME DEFAULT getdate() NOT NULL,
   reg_uid              NUMERIC(20, 0)  NOT NULL,
   mod_dt               DATETIME NULL,
   mod_uid              NUMERIC(20, 0)  NULL,
   PRIMARY KEY (atch_file_id,file_sn)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일상세', 'schema', 'dbo', 'table', 'CT_FILE_DTL';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일ID', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'atch_file_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일순번', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'file_sn';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '저장경로', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'file_store_path';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '루트제외 경로', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'relative_path';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '저장된 첨부파일명', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'store_file_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '오리지널 첨부파일명', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'original_file_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '파일확장자', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'file_extension';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '파일크기', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'file_size';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부-Y/N', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'CT_FILE_DTL', 'column', 'mod_uid';


/* (삭제예정)시퀀스 정보 */
CREATE TABLE ST_SEQUENCE (
   seq_cd   varchar(128) NOT NULL,
   seq_val  NUMERIC(20, 0) NOT NULL,
   PRIMARY KEY (seq_cd)
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기본 시퀀스', 'schema', 'dbo', 'table', 'ST_SEQUENCE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '시퀀스코드명', 'schema', 'dbo', 'table', 'ST_SEQUENCE', 'column', 'seq_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '시퀀스', 'schema', 'dbo', 'table', 'ST_SEQUENCE', 'column', 'seq_val';

-- 게시판
--DROP TABLE CT_BBS;

CREATE TABLE CT_BBS (
    bbs_id              NVARCHAR(20)    NOT NULL,
    ntt_uid             NUMERIC(20, 0)  NOT NULL,
    ntt_category        NVARCHAR(10)    DEFAULT 'N0' NOT NULL,
    ntt_subject         NVARCHAR(300)   NOT NULL,
    ntt_content         NVARCHAR(MAX)       NULL ,
    read_count          NUMERIC(20, 0) DEFAULT 0 NULL,
    top_lock_at         CHAR(1) DEFAULT 'N' NULL ,
    answer_at           CHAR(1) DEFAULT 'N' NULL ,
    parent_ntt_uid      NUMERIC(20, 0) DEFAULT 0 NULL,
    answer_lvl          NUMERIC(20, 0) DEFAULT 0 NULL,
    ntt_pwd             NVARCHAR(512) DEFAULT NULL,
    sort_order          NUMERIC(20, 0) DEFAULT 9999 NULL,
    write_dt            NVARCHAR(30) DEFAULT CONVERT(NVARCHAR(19), GETDATE(), 120) NULL,
    writer_id           NVARCHAR(128) DEFAULT NULL,
    pub_begin_date      NVARCHAR(10) DEFAULT CONVERT(NVARCHAR(10), GETDATE(), 23) NULL,
    pub_end_date        NVARCHAR(10) DEFAULT '9999-12-31',
    atch_file_id        NVARCHAR(50) DEFAULT NULL,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (bbs_id, ntt_uid)
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시판', 'schema', 'dbo', 'table', 'CT_BBS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시판 ID', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'bbs_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시물 ID', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'ntt_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시물 분류(C01)', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'ntt_category';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '제목', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'ntt_subject';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '내용', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'ntt_content';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '조회수', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'read_count';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '상단고정여부-Y/N', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'top_lock_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시물 답변가능여부-Y/N', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'answer_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부모게시물UID', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'parent_ntt_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '답글 레벨', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'answer_lvl';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '답글 암호', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'ntt_pwd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '정렬순서', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'sort_order';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성일시', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'write_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성자 계정ID', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'writer_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시시작일', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'pub_begin_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '게시종료일', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'pub_end_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '첨부파일 ID', 'schema', 'dbo', 'table', 'CT_BBS',  'column', 'atch_file_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사용여부-Y/N', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'use_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'CT_BBS', 'column', 'mod_uid';


/* 환율정보 */
CREATE TABLE HT_EXCHANGE_RATE (
    std_date            NVARCHAR(20) NOT NULL,
    cur_unit            NVARCHAR(10) NOT NULL,
    cur_nm              NVARCHAR(100) NULL,
    ttb                 NVARCHAR(50) NULL,
    tts                 NVARCHAR(50) NULL,
    deal_bas_r          NVARCHAR(50) NULL,
    bkpr                NVARCHAR(50) NULL,
    yy_efee_r           NVARCHAR(50) NULL,
    ten_dd_efee_r       NVARCHAR(50) NULL,
    kftc_deal_bas_r     NVARCHAR(50) NULL,
    kftc_bkpr           NVARCHAR(50) NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0) NOT NULL,
    PRIMARY KEY (std_date, cur_unit)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '환율정보', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기준일', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'std_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화코드', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'cur_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '국가/통화명', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'cur_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전신환(송금) 받으실때', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'ttb';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전신환(송금) 보내실때', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'tts';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매매 기준율', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'deal_bas_r';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '장부가격', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'bkpr';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '년환가료율', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'yy_efee_r';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '10일환가료율', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'ten_dd_efee_r';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '서울외국환중개 매매기준율', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'kftc_deal_bas_r';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '서울외국환중개 장부가격', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'kftc_bkpr';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE', 'column', 'reg_uid';

/* 환율 일자 */
CREATE TABLE HT_EXCHANGE_RATE_DATE (
    seq                 INT NOT NULL,
    std_date            NVARCHAR(20) NOT NULL,
    PRIMARY KEY (seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '마지막환율일자', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE_DATE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '순번', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE_DATE', 'column', 'seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '영업일', 'schema', 'dbo', 'table', 'HT_EXCHANGE_RATE_DATE', 'column', 'std_date';

/* 통화단위 */
CREATE TABLE MT_EXCHANGE_UNIT (
   exchg_unit            NVARCHAR(10) NOT NULL,
   exchg_nm              NVARCHAR(100) NULL,
   default_at            CHAR(1) DEFAULT 'N' NOT NULL,
   display_at            CHAR(1) DEFAULT 'N' NOT NULL,
   PRIMARY KEY (exchg_unit)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위', 'schema', 'dbo', 'table', 'MT_EXCHANGE_UNIT';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위', 'schema', 'dbo', 'table', 'MT_EXCHANGE_UNIT', 'column', 'exchg_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위명', 'schema', 'dbo', 'table', 'MT_EXCHANGE_UNIT', 'column', 'exchg_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '디폴트여부', 'schema', 'dbo', 'table', 'MT_EXCHANGE_UNIT', 'column', 'default_at';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '항목표시여부', 'schema', 'dbo', 'table', 'MT_EXCHANGE_UNIT', 'column', 'display_at';

/* 원소재마스터  */
CREATE TABLE MT_MATERIAL (
    material_cd         NVARCHAR(100)   NOT NULL ,
    material_nm         NVARCHAR(200)   NOT NULL ,
    raw_material_cd     NVARCHAR(50)        NULL ,
    steel_grade         NVARCHAR(50)        NULL ,
    customer_mat_cd     NVARCHAR(100)       NULL ,
    remark              NVARCHAR(500)       NULL ,
    use_at              CHAR(1) DEFAULT 'Y' NOT NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (material_cd, material_nm)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재마스터', 'schema', 'dbo', 'table', 'MT_MATERIAL';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재코드', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'raw_material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '유형(강종)', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'steel_grade';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '고객사재질코드', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'customer_mat_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_MATERIAL', 'column', 'mod_uid';



/* 원소재 공시단가 문서 */
/*
 * 문서번호 형식 : 년도 4자리 + 구분자('-') + 6자리순번
 * 문서상태 : 미결, 확정, 취소
 * 문서제목 : [부서명 또는 팀명] + 파일명
 * 작성자 : 로그인 사용자 UID
 */
CREATE TABLE BT_ANNOUNCE_PRICE_DOC (
    doc_no              NVARCHAR(30)    NOT NULL ,
    doc_title           NVARCHAR(100)   NOT NULL ,
    country_cd          NVARCHAR(10)    DEFAULT 'KR' NOT NULL,
    writer_uid          NUMERIC(20, 0)      NULL,
    writer_dt           DATETIME DEFAULT getdate() NULL,
    announced_date      NVARCHAR(20)        NULL ,
    doc_status          CHAR(1) DEFAULT 'N' NOT NULL ,
    confirm_dt          DATETIME NULL,
    doc_remark          NVARCHAR(300)       NULL ,
    doc_content         NVARCHAR(MAX)       NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (doc_no)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재 공시단가 문서', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서명', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'doc_title';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '국가코드', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'country_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성자', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'writer_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'writer_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '고시일(기준일)', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'announced_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서상태', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'doc_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'doc_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'JSON형식 문서 내용', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'doc_content';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '확정일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'confirm_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DOC', 'column', 'mod_uid';



/* 원소재 공시단가 상세 */
CREATE TABLE BT_ANNOUNCE_PRICE_DTL (
    doc_no              NVARCHAR(30)    NOT NULL ,
    ap_seq              NUMERIC(10,0)   NOT NULL ,
    material_cd         NVARCHAR(100)   NOT NULL ,
    material_nm         NVARCHAR(200)   NOT NULL ,
    bgn_date            NVARCHAR(10)    DEFAULT '19000101' NOT NULL ,
    end_date            NVARCHAR(10)    DEFAULT '99991231' NOT NULL ,
    bf_mat_unit_price   NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    mat_unit_price      NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    diff_mat_price      NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    bf_scrap_price      NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    scrap_price         NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    diff_scrap_price    NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    currency_unit       NVARCHAR(10) DEFAULT 'KRW' NOT NULL,
    country_cd          NVARCHAR(10) DEFAULT 'KR' NOT NULL,
    mp_remark           NVARCHAR(300)    NULL ,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (doc_no, ap_seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재 공시단가 상세', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '순번', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'ap_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '적용시작일', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'bgn_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '적용종료일', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'end_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변경전 원소재단가', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'bf_mat_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재단가', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'mat_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재단가차액', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'diff_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변경전 스크랩단가', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'bf_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '스크랩단가', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '스크랩단가 차액', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'diff_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '금액단위', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'currency_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '국가', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'country_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'mp_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_ANNOUNCE_PRICE_DTL', 'column', 'mod_uid';

CREATE TABLE MT_ITEM_T (
     plant_cd                NVARCHAR(10)                NOT NULL,
     item_no                 NVARCHAR(50)                NOT NULL,
     item_nm                 NVARCHAR(300)                   NULL,
     item_unit               NVARCHAR(50)                    NULL,
     car_model               NVARCHAR(50)                    NULL,
     use_at                  NVARCHAR(20)                    NULL,
     if_seq                  NVARCHAR(20) NULL,
     if_type                 NVARCHAR(4) NULL,
     if_result               NVARCHAR(1) NULL,
     if_message              NVARCHAR(100) NULL,
     PRIMARY KEY (plant_cd, item_no)
);

/* 품목 마스터 */
CREATE TABLE MT_ITEM (
    item_no                 NVARCHAR(50)                NOT NULL,
    item_nm                 NVARCHAR(300)               NOT NULL,
    material_cd             NVARCHAR(100)                   NULL,
    material_nm             NVARCHAR(200)                   NULL ,
    reg_dt                  DATETIME DEFAULT getdate()  NOT NULL,
    reg_uid                 NUMERIC(20, 0)              NOT NULL,
    mod_dt                  DATETIME                    NULL,
    mod_uid                 NUMERIC(20, 0)              NULL,
    PRIMARY KEY (item_no)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '품번마스터', 'schema', 'dbo', 'table', 'MT_ITEM';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '품번', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '품명', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'item_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'MT_ITEM', 'column', 'mod_uid';

-- 템플릿 문서
CREATE TABLE BT_TEMPLATE_DOC
(
    doc_no              NVARCHAR(30)    NOT NULL ,
    doc_title           NVARCHAR(200)   NOT NULL ,
    doc_filename        NVARCHAR(200)       NULL ,
    write_dt            DATETIME DEFAULT getdate() NOT NULL,
    writer_uid          NUMERIC(20, 0)      NULL,
    doc_status          CHAR(1) DEFAULT 'N' NOT NULL ,
    doc_pwd             NVARCHAR(128)       NULL ,
    confirm_dt          DATETIME NULL,
    doc_remark          NVARCHAR(300)       NULL ,
    doc_content         NVARCHAR(MAX)       NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME            NULL,
    mod_uid             NUMERIC(20, 0)      NULL,
    PRIMARY KEY (doc_no)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '템플릿 문서', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_title';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서파일명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_filename';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'write_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성자UID', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'writer_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서상태', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서암호', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_pwd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '확정일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'confirm_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서내용', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'doc_content';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DOC', 'column', 'mod_uid';


-- 템플릿 문서 상세 정보
-- 원소재가 철판/파이프 시 비중은 7.85 SLITT'G LOSS 율 0.42. LOSS 율 0.6
CREATE TABLE BT_TEMPLATE_DTL
(
    doc_no              NVARCHAR(30)    NOT NULL,
    td_seq              NUMERIC(20, 0)  NOT NULL,
    plant_cd            NVARCHAR(20)    NOT NULL ,
    bp_cd               NVARCHAR(20)    NOT NULL,
    doc_order           NVARCHAR(10)        NULL,
    car_model           NVARCHAR(20)    NOT NULL,
    pcs_item_no         NVARCHAR(50)    NOT NULL,
    pcs_item_nm         NVARCHAR(300)       NULL,
    base_date           NVARCHAR(10)        NULL,
    cur_item_price      MONEY           DEFAULT 0 NULL,
    part_type           NVARCHAR(10)    NOT NULL,
    part_type_nm        NVARCHAR(10)        NULL,
    pcs_sub_item_bp     NVARCHAR(20)        NULL,
    sub_item_no         NVARCHAR(50)    NOT NULL,
    sub_item_nm         NVARCHAR(300)       NULL,
    raw_material_cd     NVARCHAR(20)        NULL,
    raw_material_nm     NVARCHAR(20)        NULL,
    material_cd         NVARCHAR(20)        NULL,
    material_nm         NVARCHAR(200)       NULL,
    us                  NUMERIC(10, 0)  DEFAULT 1 NOT NULL,
    steel_grade         NVARCHAR(20)        NULL,
    m_spec              NVARCHAR(50)        NULL,
    m_type              NVARCHAR(50)        NULL,
    thick_thick         NUMERIC(20, 5)  DEFAULT 0 NULL,
    width_outer         NUMERIC(20, 5)  DEFAULT 0 NULL,
    height_in_len       NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_width            NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_length           NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_cavity           NUMERIC(20, 5)  DEFAULT 0 NULL,
    net_weight          NUMERIC(20, 10)  DEFAULT 0 NULL,
    specific_gravity    NUMERIC(10, 3)  DEFAULT 0 NULL,
    slitt_loss_rate     NUMERIC(10, 3)  DEFAULT 0 NULL,
    to_loss_rate        NUMERIC(10, 3)  DEFAULT 0 NULL,
    input_weight        NUMERIC(20, 10)  DEFAULT 0 NULL,
    bf_consigned_price  MONEY           DEFAULT 0 NULL,
    af_consigned_price  MONEY           DEFAULT 0 NULL,
    diff_consigned_price  MONEY           DEFAULT 0 NULL,
    bf_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    af_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    diff_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    bf_scrap_unit_price MONEY           DEFAULT 0 NULL,
    af_scrap_unit_price MONEY           DEFAULT 0 NULL,
    diff_scrap_unit_price MONEY           DEFAULT 0 NULL,
    scrap_weight        NUMERIC(20, 4)  DEFAULT 0 NULL,
    scrap_recovery_rate NUMERIC(20, 2)  DEFAULT 0 NULL,
    bf_scrap_price      MONEY           DEFAULT 0 NULL,
    af_scrap_price      MONEY           DEFAULT 0 NULL,
    diff_scrap_price    MONEY           DEFAULT 0 NULL,
    bf_part_mat_cost    MONEY           DEFAULT 0 NULL,
    af_part_mat_cost    MONEY           DEFAULT 0 NULL,
    diff_part_mat_cost    MONEY           DEFAULT 0 NULL,
    mat_admin_rate      NUMERIC(10, 2)  DEFAULT 0 NULL,
    os_mat_admin_rate   NUMERIC(10, 2)  DEFAULT 0 NULL,
    changed_amount      MONEY           DEFAULT 0 NULL,
    writer_id           NVARCHAR(50)              NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (doc_no, td_seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '템플릿 문서 상세', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '항목순번', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'td_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트 코드', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공급 협력사 BP코드', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차수', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'doc_order';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매입품번', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'pcs_item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매입품명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'pcs_item_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가일자', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'base_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'cur_item_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품구분(B02)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'part_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품구분명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'part_type_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급부품협력사코드', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'pcs_sub_item_bp';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB품번', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'sub_item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB품명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'sub_item_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차종', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'car_model';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재코드', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'raw_material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'raw_material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'US', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'us';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '강종', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'steel_grade';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-SPEC', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'm_spec';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-TYPE', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'm_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '두께/두께', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'thick_thick';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가로/외경', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'width_outer';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '세로/투입길이', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'height_in_len';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 가로', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bl_width';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 세로', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bl_length';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL CAVITY', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bl_cavity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'NET 중량(KG)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'net_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비중', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'specific_gravity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SLITT LOSS율(%)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'slitt_loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'TO LOSS율(%)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'to_loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '투입중량(Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'input_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이전사급단가(이전/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bf_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이후사급단가(이후/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'af_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급단가차액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'diff_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이전/매)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bf_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이후/매)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'af_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비 차액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'diff_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이전/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bf_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이후/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'af_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가 차액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'diff_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 중량(Kg/EA)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'scrap_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 회수율(%)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'scrap_recovery_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이전/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bf_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이후/Kg)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'af_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격 차액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'diff_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이전)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'bf_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이후)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'af_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비 차액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'diff_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재관비율(%)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '외주재관비율(%)', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'os_mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변동금액', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'changed_amount';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성자', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'writer_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_TEMPLATE_DTL', 'column', 'mod_uid';


/*
 * 공급업체 사급(매입)단가
 * PURCHASE_PRICE_SEQ 을 Key로 사용
 */
EXEC sp_rename 'HT_PURCHASE_PRICE', 'HT_PURCHASE_PRICE_O';

CREATE TABLE HT_PURCHASE_PRICE
(
    plant_cd            NVARCHAR(20) NOT NULL,
    pur_org             NVARCHAR(20) NOT NULL,
    bp_cd               NVARCHAR(20) NOT NULL,
    mat_cd              NVARCHAR(50) NOT NULL,
    pur_price           NUMERIC(20,4) DEFAULT 0 NOT NULL,
    origin_pur_price    NUMERIC(20,10) DEFAULT 0 NOT NULL,
    cur_unit            NVARCHAR(10) DEFAULT 'KRW' NOT NULL,
    price_unit          NUMERIC(20,0) DEFAULT 1 NOT NULL,
    purchase_unit       NVARCHAR(10) DEFAULT 'EA' NOT NULL,
    price_status        NVARCHAR(10) DEFAULT '10' NULL,
    bgn_valid_date      NVARCHAR(10) NULL,
    if_seq              NVARCHAR(20) NULL,
    if_type             NVARCHAR(4) NULL,
    if_result           NVARCHAR(1) NULL,
    if_message          NVARCHAR(100) NULL,
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)  NULL,
    PRIMARY KEY (plant_cd, pur_org, bp_cd, mat_cd)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사 매입단가', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '구매조직(1000 내수, 1100 외자)', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'pur_org';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '구매업체 BP코드', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '자재코드', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'mat_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'cur_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격단위 적용 구매단가', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'pur_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격단위 미적용 구매단가', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'origin_pur_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격단위', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'price_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '주문단위', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'purchase_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '단가상태', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'price_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '효력시작일', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'bgn_valid_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 번호', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'if_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 단위', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'if_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 결과', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'if_result';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 메시지', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'if_message';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'HT_PURCHASE_PRICE', 'column', 'mod_uid';

/*
 * 판매처 매출사급단가단가
 * CONSIGNED_PRICE_SEQ 을 Key로 사용
 */
drop TABLE HT_CONSIGNED_PRICE;

CREATE TABLE HT_CONSIGNED_PRICE
(
    cp_seq                      NUMERIC(20,0)   NOT NULL,
    plant_cd                    NVARCHAR(20)    NOT NULL,
    sales_org                   NVARCHAR(4)         NULL,
    bp_cd                       NVARCHAR(20)    NOT NULL,
    mat_cd                      NVARCHAR(50)    NOT NULL,
    consigned_price             NUMERIC(20,4)   DEFAULT 0 NOT NULL,
    origin_consigned_price      NUMERIC(20,4)   DEFAULT 0 NOT NULL,
    cur_unit                    NVARCHAR(10)    DEFAULT 'KRW' NOT NULL,
    price_unit                  NUMERIC(20,0)   DEFAULT 1 NOT NULL,
    sales_unit                  NVARCHAR(10)    DEFAULT 'EA' NOT NULL,
    bgn_valid_date              NVARCHAR(10)        NULL,
    end_valid_date              NVARCHAR(10)        NULL,
    if_seq                      NVARCHAR(20)        NULL,
    if_type                     NVARCHAR(4)         NULL,
    if_result                   NVARCHAR(1)         NULL,
    if_message                  NVARCHAR(100)       NULL,
    reg_dt                      DATETIME DEFAULT getdate() NOT NULL,
    reg_uid                     NUMERIC(20, 0)  NOT NULL,
    mod_dt                      DATETIME            NULL,
    mod_uid                     NUMERIC(20, 0)      NULL,
    PRIMARY KEY (cp_seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사 매입단가', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매출사급단가 SEQ', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'cp_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '영업조직', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'sales_org';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '판매처BP코드', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '자재코드', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'mat_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격결정단위 작용 사급단가', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격결정단위 미적용 사급단가', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'origin_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'cur_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격결정단위(배수)', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'price_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '출고단위(EA,...)', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'sales_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '효력시작일(형식: 2019.01.01)', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'bgn_valid_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '효력종료일(형식: 2019.01.01)', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'end_valid_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 번호', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'if_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 단위', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'if_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 결과', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'if_result';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'I/F 메시지', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'if_message';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'HT_CONSIGNED_PRICE', 'column', 'mod_uid';


-- 비철금속 시세정보
/*
 	 *  <th>Cu</th> 8,552.5
	 *  <th>Al</th> 2,209.0
	 *  <th>Zn</th> 2,501.5
	 *  <th>Pb</th> 2,125.0
	 *  <th>Ni</th> 17,845.0
	 *  <th>Sn</th> 27,485.0
 */
CREATE TABLE HT_MARKET_PRICE_NONFERROUS
(
    mn_date                 NVARCHAR(20)    NOT NULL,
    cu_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    al_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    zn_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    pb_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    ni_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    sn_prc                  NUMERIC(20, 2)  DEFAULT 0 NOT NULL,
    reg_dt                  DATETIME DEFAULT getdate() NOT NULL,
    reg_uid                 NUMERIC(20, 0)  NOT NULL
    PRIMARY KEY (mn_date)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비철금속 LME 시세', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기준일', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'mn_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '구리', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'cu_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '알루미늄', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'al_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '아연', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'zn_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '납', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'pb_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '니켈', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'ni_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '주석', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'sn_prc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_NONFERROUS', 'column', 'reg_uid';

-- 철강 시세

drop TABLE HT_MARKET_PRICE_FERROUS;

CREATE TABLE HT_MARKET_PRICE_FERROUS
(
    mn_date                 NVARCHAR(20)    NOT NULL,
    item                    NVARCHAR(100)       NULL,
    unit                    NVARCHAR(100)       NULL,
    market                  NVARCHAR(100)       NULL,
    futures                 NVARCHAR(100)       NULL,
    price                   NVARCHAR(100)       NULL,
    prev_week_avg           NVARCHAR(100)       NULL,
    prev_month_avg          NVARCHAR(100)       NULL,
    prev_date_amt           NVARCHAR(100)       NULL,
    prev_date_rate          NVARCHAR(100)       NULL,
    prev_week_amt           NVARCHAR(100)       NULL,
    prev_week_rate          NVARCHAR(100)       NULL,
    prev_month_amt          NVARCHAR(100)       NULL,
    prev_month_rate         NVARCHAR(100)       NULL,
    reg_dt                  DATETIME DEFAULT getdate() NOT NULL
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비철금속 LME 시세', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기준일', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'mn_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '품목', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'item';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '단위', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '거래시장(조건)', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'market';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현물/선물', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'futures';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전주평균', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_week_avg';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전월평균', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_month_avg';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전일대비차액', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_date_amt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전일비', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_date_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전주대비차액', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_week_amt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전주비', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_week_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전월대비차액', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_month_amt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전월비', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'prev_month_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_MARKET_PRICE_FERROUS', 'column', 'reg_dt';

EXEC sp_rename 'HT_CHANGED_PRICE', 'HT_CHANGED_PRICE_O';

CREATE TABLE HT_CHANGED_PRICE
(
    doc_no              NVARCHAR(30)    NOT NULL ,
    ref_doc_no          NVARCHAR(30)        NULL ,
    announced_date      NVARCHAR(30)    NOT NULL ,
    plant_cd            NVARCHAR(20)    NOT NULL ,
    bp_cd               NVARCHAR(20)    NOT NULL,
    doc_order           NVARCHAR(10)        NULL,
    car_model           NVARCHAR(20)    NOT NULL,
    pcs_item_no         NVARCHAR(50)    NOT NULL,
    base_date           NVARCHAR(10)        NULL,
    cur_item_price      MONEY           DEFAULT 0 NULL,
    part_type           NVARCHAR(10)    NOT NULL,
    sub_item_bp_cd      NVARCHAR(20)        NULL,
    sub_item_no         NVARCHAR(50)    NOT NULL,
    raw_material_cd     NVARCHAR(20)        NULL,
    apply_date          NVARCHAR(10)        NULL,
    material_cd         NVARCHAR(20)        NULL,
    material_nm         NVARCHAR(200)       NULL,
    us                  NUMERIC(10, 0)  DEFAULT 1 NOT NULL,
    steel_grade         NVARCHAR(20)        NULL,
    m_spec              NVARCHAR(50)        NULL,
    m_type              NVARCHAR(50)        NULL,
    thick_thick         NUMERIC(20, 8)  DEFAULT 0 NULL,
    width_outer         NUMERIC(20, 8)  DEFAULT 0 NULL,
    height_in_len       NUMERIC(20, 8)  DEFAULT 0 NULL,
    bl_width            NUMERIC(20, 8)  DEFAULT 0 NULL,
    bl_length           NUMERIC(20, 8)  DEFAULT 0 NULL,
    bl_cavity           NUMERIC(20, 8)  DEFAULT 0 NULL,
    net_weight          NUMERIC(20, 10)  DEFAULT 0 NULL,
    specific_gravity    NUMERIC(10, 8)  DEFAULT 0 NULL,
    slitt_loss_rate     NUMERIC(10, 8)  DEFAULT 0 NULL,
    loss_rate           NUMERIC(10, 8)  DEFAULT 0 NULL,
    input_weight        NUMERIC(20, 10)  DEFAULT 0 NULL,
    bf_consigned_price  MONEY           DEFAULT 0 NULL,
    af_consigned_price  MONEY           DEFAULT 0 NULL,
    diff_consigned_price  MONEY           DEFAULT 0 NULL,
    bf_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    af_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    diff_cnsgn_mat_price  MONEY           DEFAULT 0 NULL,
    bf_scrap_unit_price MONEY           DEFAULT 0 NULL,
    af_scrap_unit_price MONEY           DEFAULT 0 NULL,
    diff_scrap_unit_price MONEY           DEFAULT 0 NULL,
    scrap_weight        NUMERIC(20, 8)  DEFAULT 0 NULL,
    scrap_recovery_rate NUMERIC(20, 4)  DEFAULT 0 NULL,
    bf_scrap_price      MONEY           DEFAULT 0 NULL,
    af_scrap_price      MONEY           DEFAULT 0 NULL,
    diff_scrap_price    MONEY           DEFAULT 0 NULL,
    bf_part_mat_cost    MONEY           DEFAULT 0 NULL,
    af_part_mat_cost    MONEY           DEFAULT 0 NULL,
    diff_part_mat_cost    MONEY         DEFAULT 0 NULL,
    mat_admin_rate      NUMERIC(10, 2)  DEFAULT 0 NULL,
    os_mat_admin_rate   NUMERIC(10, 2)  DEFAULT 0 NULL,
    changed_amount      MONEY           DEFAULT 0 NULL,
    changed_status      nvarchar(1)     default 'N' NULL,
    sub_item_count      numeric(10,0)   default 0,
    completed_cnt       int             default 0,
    incompleted_cnt     int             default 0,
    error_cnt           int             default 0,
    total_changed_amt   money           default 0,
    new_purchase_amt    money           default 0,
    reg_dt              DATETIME DEFAULT getdate() NULL,
    reg_uid             NUMERIC(20, 0)   NULL,
    mod_dt              DATETIME NULL,
    mod_uid             NUMERIC(20, 0)   NULL
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경이력', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '참조 문서번호', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'ref_doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기준일(고시일)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'announced_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차수', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'doc_order';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차종', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'car_model';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매입품번', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'pcs_item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가일자', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'base_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'cur_item_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품구분(B02)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'part_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급부품협력사코드', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'sub_item_bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB품번', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'sub_item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재코드', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'raw_material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '적용일', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'apply_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'US', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'us';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '강종', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'steel_grade';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-SPEC', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'm_spec';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-TYPE', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'm_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '두께/두께', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'thick_thick';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가로/외경', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'width_outer';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '세로/투입길이', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'height_in_len';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 가로', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bl_width';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 세로', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bl_length';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL CAVITY', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bl_cavity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'NET 중량(KG)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'net_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비중', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'specific_gravity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SLITT LOSS율(%)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'slitt_loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'LOSS율(%)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '투입중량(Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'input_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이전사급단가(이전/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bf_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이후사급단가(이후/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'af_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급단가차액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'diff_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이전/매)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bf_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이후/매)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'af_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비 차액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'diff_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이전/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bf_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이후/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'af_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가 차액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'diff_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 중량(Kg/EA)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'scrap_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 회수율(%)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'scrap_recovery_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이전/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bf_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이후/Kg)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'af_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격 차액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'diff_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이전)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'bf_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이후)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'af_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비 차액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'diff_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재관비율(%)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '외주재관비율(%)', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'os_mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변동금액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'changed_amount';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경상태', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'changed_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB품목수', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'sub_item_count';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변경완료건수', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'completed_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '미완료건수', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'incompleted_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '에러건수', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'error_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변경금액합계', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'total_changed_amt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '새로운 매입금액', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'new_purchase_amt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'HT_CHANGED_PRICE', 'column', 'mod_uid';

EXEC sp_rename 'BT_PRICE_CHANGE', 'BT_PRICE_CHANGE_O';


-- 가격합의서
CREATE TABLE BT_PRICE_CHANGE
(
    doc_no              NVARCHAR(30)    NOT NULL,       -- 문서번호
    plant_cd            NVARCHAR(20)    NOT NULL,       -- 플랜트코드
    bp_cd               NVARCHAR(20)    NOT NULL,       -- 협력사코드
    announced_date      NVARCHAR(20)        NULL,       -- 고시일(기준일)
    agreement_date      NVARCHAR(20)        NULL,       -- 합의일자
    org_cd              NVARCHAR(20)        NULL,       -- 부서코드
    team_cd             NVARCHAR(20)        NULL,       -- 팀코드
    writer_id           NVARCHAR(128)       NULL,       -- 담당자ID
    doc_remark          NVARCHAR(300)       NULL,
    doc_status          NVARCHAR(10) DEFAULT 'P' NOT NULL ,  -- 문서상태
    confirm_dt          DATETIME            NULL ,      -- 확정일시
    approval_status     NVARCHAR(10) DEFAULT 'BG' NULL ,      -- 결재상태
    eform_doc_id        NVARCHAR(50)        NULL,       -- 이폼사인 문서ID
    ref_doc_no          NVARCHAR(50)        NULL,       -- 참조 공시단가문서번호
    reg_dt              DATETIME DEFAULT getdate() NOT NULL,
    reg_uid             NUMERIC(20, 0)  NOT NULL,
    mod_dt              DATETIME            NULL,
    mod_uid             NUMERIC(20, 0)      NULL,
    PRIMARY KEY (doc_no)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경문서', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '기준일(고시일)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'announced_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '합의일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'agreement_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부서코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'org_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '팀코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'team_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '작성자ID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'writer_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'doc_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서상태', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'doc_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '확정일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'confirm_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '전자합의서문서번호', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'eform_doc_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '참조 공시단가 문서번호', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'ref_doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE', 'column', 'mod_uid';

/**
  가격 변경 상세
 */
CREATE TABLE BT_PRICE_CHANGE_DTL
(
    doc_no                  NVARCHAR(30)    NOT NULL,
    pcd_seq                 NUMERIC(10, 0)  NOT NULL,
    plant_cd                NVARCHAR(20)    NOT NULL ,
    bp_cd                   NVARCHAR(20)    NOT NULL,
    pc_ord                  NVARCHAR(10)        NULL,
    car_model               NVARCHAR(20)    NOT NULL,
    item_no                 NVARCHAR(50)    NOT NULL,
    cur_price_date          NVARCHAR(10)        NULL,
    cur_item_price          MONEY           DEFAULT 0 NULL,
    part_type               NVARCHAR(10)    NOT NULL,
    sub_bp_cd               NVARCHAR(20)        NULL,
    sub_item_no             NVARCHAR(50)    NOT NULL,
    apply_date              NVARCHAR(10)        NULL,
    raw_material_cd         NVARCHAR(20)        NULL,
    material_cd             NVARCHAR(20)        NULL,
    material_nm             NVARCHAR(200)       NULL,
    us                      NUMERIC(10, 0)  DEFAULT 1 NOT NULL,
    steel_grade             NVARCHAR(20)        NULL,
    m_spec                  NVARCHAR(50)        NULL,
    m_type                  NVARCHAR(50)        NULL,
    thick_thick             NUMERIC(20, 5)  DEFAULT 0 NULL,
    width_outer             NUMERIC(20, 5)  DEFAULT 0 NULL,
    height_in_len           NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_width                NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_length               NUMERIC(20, 5)  DEFAULT 0 NULL,
    bl_cavity               NUMERIC(20, 5)  DEFAULT 0 NULL,
    net_weight              NUMERIC(20, 10) DEFAULT 0 NULL,
    specific_gravity        NUMERIC(10, 3)  DEFAULT 0 NULL,
    slitt_loss_rate         NUMERIC(10, 3)  DEFAULT 0 NULL,
    loss_rate               NUMERIC(10, 3)  DEFAULT 0 NULL,
    input_weight            NUMERIC(20, 10) DEFAULT 0 NULL,
    bf_consigned_price      MONEY           DEFAULT 0 NULL,
    af_consigned_price      MONEY           DEFAULT 0 NULL,
    diff_consigned_price    MONEY           DEFAULT 0 NULL,
    bf_cnsgn_mat_price      MONEY           DEFAULT 0 NULL,
    af_cnsgn_mat_price      MONEY           DEFAULT 0 NULL,
    diff_cnsgn_mat_price    MONEY           DEFAULT 0 NULL,
    bf_scrap_unit_price     MONEY           DEFAULT 0 NULL,
    af_scrap_unit_price     MONEY           DEFAULT 0 NULL,
    diff_scrap_unit_price   MONEY           DEFAULT 0 NULL,
    scrap_weight            NUMERIC(20, 4)  DEFAULT 0 NULL,
    scrap_recovery_rate     NUMERIC(20, 2)  DEFAULT 0 NULL,
    bf_scrap_price          MONEY           DEFAULT 0 NULL,
    af_scrap_price          MONEY           DEFAULT 0 NULL,
    diff_scrap_price        MONEY           DEFAULT 0 NULL,
    bf_part_mat_cost        MONEY           DEFAULT 0 NULL,
    af_part_mat_cost        MONEY           DEFAULT 0 NULL,
    diff_part_mat_cost      MONEY           DEFAULT 0 NULL,
    mat_admin_rate          NUMERIC(10, 2)  DEFAULT 0 NULL,
    os_mat_admin_rate       NUMERIC(10, 2)  DEFAULT 0 NULL,
    changed_amount          MONEY           DEFAULT 0 NULL,
    changed_status          NVARCHAR(1) DEFAULT 'N' NULL,
    reg_dt                  DATETIME DEFAULT getdate() NOT NULL,
    reg_uid                 NUMERIC(20, 0)  NOT NULL,
    mod_dt                  DATETIME            NULL,
    mod_uid                 NUMERIC(20, 0)      NULL,
    PRIMARY KEY (doc_no, pcd_seq)
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경 상세', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '항목순번', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'pcd_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트 코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공급 협력사 BP코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차수', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'pc_ord';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차종', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'car_model';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매입품번', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가일자', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'cur_price_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'cur_item_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품구분(B02)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'part_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급부품협력사코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'sub_bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB품번', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'sub_item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '적용일', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'apply_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원소재코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'raw_material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'material_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재질명', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'material_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'US', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'us';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '강종', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'steel_grade';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-SPEC', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'm_spec';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'M-TYPE', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'm_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '두께/두께', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'thick_thick';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가로/외경', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'width_outer';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '세로/투입길이', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'height_in_len';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 가로', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bl_width';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL 세로', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bl_length';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BL CAVITY', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bl_cavity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'NET 중량(KG)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'net_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비중', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'specific_gravity';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SLITT LOSS율(%)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'slitt_loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'LOSS율(%)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'loss_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '투입중량(Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'input_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이전사급단가(이전/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bf_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이후사급단가(이후/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'af_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급단가차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'diff_consigned_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이전/매)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bf_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비(이후/매)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'af_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '사급재료비 차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'diff_cnsgn_mat_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이전/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bf_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가(이후/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'af_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 단가 차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'diff_scrap_unit_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 중량(Kg/EA)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'scrap_weight';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 회수율(%)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'scrap_recovery_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이전/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bf_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격(이후/Kg)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'af_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SCRAP 가격 차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'diff_scrap_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이전)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'bf_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비(이후)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'af_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '부품 재료비 차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'diff_part_mat_cost';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '재관비율(%)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '외주재관비율(%)', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'os_mat_admin_rate';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변동금액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'changed_amount';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경상태', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'changed_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_DTL', 'column', 'mod_uid';



/**
  가격변경 요약
 */
drop TABLE BT_PRICE_CHANGE_ITEM;

CREATE TABLE BT_PRICE_CHANGE_ITEM
(
    doc_no                  NVARCHAR(30)    NOT NULL,
    pci_seq                 NUMERIC(10, 0)  NOT NULL,
    plant_cd                NVARCHAR(20)    NOT NULL,
    car_model               NVARCHAR(20)        NULL,
    bp_cd                   NVARCHAR(20)    NOT NULL,
    item_no                 NVARCHAR(50)    NOT NULL,
    base_date               NVARCHAR(10)    NOT NULL,
    apply_date              NVARCHAR(10)    NOT NULL,
    cur_price               MONEY           DEFAULT 0 NULL,
    af_price                MONEY           DEFAULT 0 NULL,
    diff_amount             MONEY           DEFAULT 0 NULL,
    pcs_unit                NVARCHAR(10)    DEFAULT 'EA' NULL,
    currency_unit           NVARCHAR(10)    DEFAULT 'KRW' NULL,
    sub_item_cnt            INT             DEFAULT 0 NULL,
    completed_cnt           INT             DEFAULT 0 NULL,
    incompleted_cnt         INT             DEFAULT 0 NULL,
    error_cnt                INT             DEFAULT 0 NULL,
    cpi_remark              NVARCHAR(300)       NULL,
    reg_dt                  DATETIME DEFAULT getdate() NOT NULL,
    reg_uid                 NUMERIC(20, 0)  NOT NULL,
    mod_dt                  DATETIME            NULL,
    mod_uid                 NUMERIC(20, 0)      NULL,
    PRIMARY KEY (doc_no, pci_seq)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경 요약', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '항목순번', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'pci_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차종', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'car_model';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '매입품번', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'item_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현재가일자', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'base_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '적용일', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'apply_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '현매입금액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'cur_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '변경금액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'af_price';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '차액', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'diff_amount';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '구매단위', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'pcs_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '통화단위', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'currency_unit';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'SUB부품수', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'sub_item_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '처리완료건수', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'completed_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '미완료건수', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'incompleted_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '미완료건수', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'error_cnt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '비고', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'cpi_remark';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록자UID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'reg_uid';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'mod_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자UID', 'schema', 'dbo', 'table', 'BT_PRICE_CHANGE_ITEM', 'column', 'mod_uid';

EXEC sp_rename 'HT_AGREEMENT', 'HT_AGREEMENT_O';

CREATE TABLE HT_AGREEMENT
(
    doc_no              NVARCHAR(30)    NOT NULL,       -- 합의서 번호
    plant_cd            NVARCHAR(20)    NOT NULL,       -- 플랜트코드
    announced_date      NVARCHAR(10)        NULL,       -- 공시단가 등록일
    sender_team         NVARCHAR(50)        NULL,       -- 발송인 팀명
    sender              NVARCHAR(50)        NULL,       -- 발송인
    bp_cd               NVARCHAR(20)        NULL,       -- BP코드
    bp_nm               NVARCHAR(100)       NULL,       -- BP명
    ceo_nm              NVARCHAR(50)        NULL,       -- 대표자명
    agr_desc            NVARCHAR(1000)      NULL,       -- 특이사항
    agr_detail          NVARCHAR(MAX)       NULL,       -- 상세내용 이미지 BASE64 코드값
    agr_file            NVARCHAR(MAX)       NULL,       -- 상세내용 첨부파일 BASE64 코드값
    src_doc_no          NVARCHAR(30)        NULL,       -- 가격변경 문서번호
    eform_doc_id        NVARCHAR(50)        NULL,       -- 이폼사인 문서ID
    eform_doc_name      NVARCHAR(200)       NULL,       -- 이폼사인 문서명
    eform_status        NVARCHAR(30)        NULL,       -- 이폼사인 현재상태코드
    request_dt          NVARCHAR(20)        NULL,       -- 요청일시
    complete_dt         NVARCHAR(20)        NULL,       -- 완료일시
    last_chg_dt         NVARCHAR(20)        NULL,       -- 최근변경일시
    reg_dt              DATETIME DEFAULT getdate() NULL,
    reg_id              NVARCHAR(50)        NULL,
    primary key (doc_no)
);
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경합의서', 'schema', 'dbo', 'table', 'HT_AGREEMENT';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서번호', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공시단가등록일', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'announced_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '발송 팀명', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'sender_team';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '발송자명', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'sender';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BP코드', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'BP명', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'bp_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대표자명', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'ceo_nm';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '요약정보', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'agr_desc';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '상세정보이미지', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'agr_detail';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '상세정보첨부파일', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'agr_file';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '원본문서번호', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'src_doc_no';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 문서ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'eform_doc_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 문서명', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'eform_doc_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 현재상태코드', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'eform_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '요청일시', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'request_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '최근변경일시', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'last_chg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'reg_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT', 'column', 'reg_id';

-- 이폼사인 웹훅 로그
--
-- EXEC sp_rename 'HT_AGREEMENT_STATUS_LOG', 'HT_AGREEMENT_STATUS_LOG_O';

CREATE TABLE HT_AGREEMENT_STATUS_LOG
(
    log_seq             BIGINT IDENTITY(1,1) PRIMARY KEY,
    webhook_id          NVARCHAR(50)    NOT NULL,       -- Webhook ID
    webhook_name        NVARCHAR(100)       NULL,       -- webhook 명
    company_id          NVARCHAR(50)        NULL,       -- 이폼사인 회사ID
    event_type          NVARCHAR(20)        NULL,       -- 이벤트 유형 document / document_action / read_document_pdf
    doc_id              NVARCHAR(50)        NULL,       -- 문서ID
    doc_title           NVARCHAR(100)       NULL,       -- 문서명  ( ex : 가격합의서_1100_200171_20240401 )
    template_id         NVARCHAR(50)        NULL,       -- 템플릿 ID
    template_name       NVARCHAR(100)       NULL,       -- 템플릿명
    template_version    NVARCHAR(20)        NULL,       -- 템플릿버전
    workflow_seq        INT DEFAULT 0 NULL,   -- 워크플로어 순번
    history_id          NVARCHAR(50)        NULL,       -- 이력ID
    doc_status          NVARCHAR(50)        NULL,       -- 문서상태
    editor_id           NVARCHAR(50)        NULL,       -- 수정자ID
    update_date         BIGINT              NULL,       -- 수정일시 - 숫자값
    update_dt           NVARCHAR(30)        NULL,       -- 수정일시
    plant_cd            NVARCHAR(20)        NULL,       -- 플랜트코드
    announced_date      NVARCHAR(8)         NULL,       -- 공시단가 등록일
    bp_cd               NVARCHAR(20)        NULL,       -- BP코드
    mass_job_request_id NVARCHAR(50)        NULL,       -- 대량발송ID
    comment             NVARCHAR(200)       NULL,       -- 작성메모
    reg_dt              DATETIME DEFAULT getdate() NULL -- 등록일시
);

EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '가격변경합의서 상태로그, 이폼사인 웹훅 로그', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '로그순번', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'log_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'Webhook ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'webhook_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', 'webhook 명', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'webhook_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이폼사인 회사ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'company_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이벤트 유형 document / document_action / read_document_pdf', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'event_type';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'doc_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서명', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'doc_title';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '템플릿 ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'template_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '템플릿명', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'template_name';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '템플릿버전', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'template_version';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '워크플로어 순번', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'workflow_seq';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '이력ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'history_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '문서상태', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'doc_status';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정자ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'editor_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시 - 숫자값', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'update_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '수정일시', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'update_dt';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '플랜트코드', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'plant_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '협력사코드', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'bp_cd';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '공시단가등록일', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'announced_date';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '대량발송요청ID', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'mass_job_request_id';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '메모사항', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'comment';
EXEC ICMS.sys.sp_addextendedproperty 'MS_Description', '등록일시', 'schema', 'dbo', 'table', 'HT_AGREEMENT_STATUS_LOG', 'column', 'reg_dt';