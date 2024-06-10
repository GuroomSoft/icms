# ICMS 개발환경

### 개발환경 Spec

| 구분           | 버전                                        | 설명                                |
|--------------|-------------------------------------------|-----------------------------------|
| `JDK`        | 17                                        ||
| `SpringBoot` | 3.0.2                                     |                                   |
| `개발툴`        | IntelliJ IDEA 2022.1.1 (Ultimate Edition) |                                   |
| `개발 DB`      | SQL Server 2019                           | 윈도우                               |
| `운영 DB`      | SQL Server 2022                           | 윈도우                               |
| `SAPJCO`     | SAPJCO3                                   | 윈도우, 별도의 라이브러리(DLL)가 설치되어 있어야 한다. |


### 데이터베이스 및 SAP 연결정보
* 어플리케이션 프로퍼티 파일 참조

### 주요 프로퍼티 정보
| 구분                    | 설명               | 비고      |
|-----------------------|------------------|---------|
| `jco.client`          | SAP 연동을 위한 연결정보 설정 ||
| `springdoc.api-docs`  | SWAGGER 형식의 API 정보를 제공하기 위한 설정 |         |
| `file.upload-root-dir` | 파일 업로드 저장 위치 정보  | 프로파일 정보에 따라 다르게 지정되어야 한다. |
| `file.preview-path-pattern` | 이미지 보기 URL 경로 설정 | Spring Security white list 추가 |
| `file.download-path-pattern` | 파일 다운로드를 위한 URL 경로 설정 | Spring Security white list 추가 |

### 주요 Denpendency
* `mssql-jdbc` SQL Server Driver
* `jjwt-api`, `jjwt-impl`, `jjwt-jackson`  JWT 관련
* `commons-lang3`, `commons-collections` Apache Common 라이브러리
* `log4jdbc-log4j2-jdbc4.1` SQL 관련 로그 출력
* `spring-boot-starter-data-redis` Redis 연동을 위한 라이브러리
* `spring-boot-starter-mail` Spring Boot 메일 지원 
* `json-simple` JSON 객체 지원 라이브러리
* `poi`,`poi-ooxml`,`poi-ooxml-schemas` POI 지원 라이브러리  
* `okhttp` HTTP Connection 라이브러리 
* `itext`,`html2pdf`,`font-asian`,`layout`,`forms`,`svg`,`sign`,`styled-xml-parser`,`io` PDF 관련 라이브러리

### 소스 디렉토리 설명
* `auth` 사용자 인증, 사용자, 사용자 그룹, 권한 관리
* `biz` 업무관련
* `common` 프로젝트 공통 모듈
* `config` 시스템 Config 관련 모듈 
* `http` Http Request, Response 관련 모듈
* `mail` SMTP 관련 모듈
* `sap` SAP 연동을 위한 JCO Wrapping 모듈
* `security` Spring Security Configuration 모듈
* `util` 시스템 Utility 모듈

### 주요 Resource 디렉토리 설명
* `fonts` PDF 지원 폰트 
* `i18n` 국제화 관련 메시지 파일
* `mappers` iBatis SQL 파일
* `templates` 타임리프 템플릿 파일
