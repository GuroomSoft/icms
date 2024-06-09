package com.guroomsoft.icms.util;

/**
 * 어플리케이션 상수정의
 */
public class AppContant {
    // 디폴트 공시단가 종료일
    public static final String DEFAULT_APPLY_END_DATE = "99991231";
    // 디폴트 구매단위
    public static final String DEFAULT_PURCHASE_UNIT = "EA";

    public static final String APP_ID = "ICMS";
    public static final String PREFIX_BEARER="Bearer ";
    public static final String REQ_PARAM_USER_UID = "reqUserUid";
    public static final String REQ_PARAM_ACCOUNT_ID = "reqAccountId";

    public static final String REQ_PARAM_CORP_CD = "reqCorpCd";
    public static final String REQ_PARAM_PLANT_CD = "reqPlantCd";
    public static final String REQ_PARAM_ORG_CD = "reqOrgCd";
    public static final String REQ_PARAM_TEAM_CD = "reqTeamCd";
    public static final String REQ_PARAM_BP_CD = "reqBpCd";

    public enum UserRoles {
        USER("USER"),               // 사용자
        LEADER("LEADER"),           // 업무관리자
        MANAGER("MANAGER"),         // 부서관리자
        DIRECTOR("DIRECTOR"),       // 의사결정자
        PARTNER("PARTNER"),         // 파트너사
        SYSADMIN("SYSADMIN")        // 시스템 관리자
        ;

        String value;
        UserRoles(String value) { this.value = value; }

        public String getValue() { return this.value; }
    }

    public enum UserType {
        INTERNAL("I"),          // 내부사용자
        EXTERNAL("E"),          // 외부사용자
        SYSTEM("S");            // 시스템사용자

        String value;
        UserType(String value) { this.value = value; }

        public String getValue() { return this.value; }
    }

    // 부품구분
    public enum PartType {
        DEVELOPMENT("DD"),          // 직개발
        TRANSACTION("DT"),          // 직거래
        RESALE("RP");               // 사급

        String value;
        PartType(String value) { this.value = value; }

        public String getValue() { return this.value; }
    }

    /**
     * 프로그램 공통적으로 사용되는 상수값
     */
    public enum CommonValue {
        YES("Y"),
        NO("N"),
        ACTIVE("1"),
        INACTIVE("0")
        ;
        String value;

        CommonValue(String value) { this.value = value; }
        public String getValue() { return this.value; }
    }

    /**
     * Code Group value
     */
    public enum CodeGroupValue {
        MATERIAL_TYPE("B01"),
        PART_TYPE("B02"),
        CAR_TYPE("B03"),
        STEEL_GRADE("B05"),
        BBS_TYPE("C01"),
        YES_NO("C02"),
        USE_AT("C03"),
        DISPLAY_AT("C04"),
        BP_TYPE("C05"),
        DEPT("C06"),
        TEAM("C07"),
        COUNTRY("C08"),
        DOC_STATUS("C09"),
        ACCOUNT_TYPE("S01"),
        USER_ROLE("S03")
        ;
        String value;

        CodeGroupValue(String value) { this.value = value; }
        public String getValue() { return this.value; }
    }
}
