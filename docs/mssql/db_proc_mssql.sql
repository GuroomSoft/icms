-- 함수 선언

/* 코드 명칭 조회 */
CREATE FUNCTION FN_GET_CODE_NAME
(
	 @pCodeGroup NVARCHAR(10),
	 @pCode   NVARCHAR(20)
)
RETURNS nvarchar(200)
AS
BEGIN
	DECLARE @vCodeName nvarchar(200);

    SELECT @vCodeName = cd_nm
    FROM ST_CODE WITH(NOLOCK)
    WHERE cg_id = @pCodeGroup
      AND cd = @pCode;

    RETURN @vCodeName;
END;


/* 현재 시퀀스 값 조회 */
CREATE FUNCTION FN_CURRENT_VAL( @pSeqName VARCHAR(100) )
RETURNS NUMERIC(20, 0)
BEGIN
    DECLARE @vCurVal numeric(20, 0);

    SELECT @vCurVal = convert(numeric(20, 0), current_value)
    FROM sys.sequences WHERE name = @pSeqName ;

    RETURN @vCurVal;
END;

CREATE VIEW [dbo].[BV_ANNOUNCE_PRICE]
AS
select a.country_cd, dbo.FN_GET_CODE_NAME('C08', a.country_cd) AS country_nm, m.raw_material_cd,
       dbo.FN_GET_CODE_NAME('B01', m.raw_material_cd) AS raw_material_nm, m.steel_grade,
       a.material_cd, isnull(m.customer_mat_cd, '') AS customer_mat_cd, m.material_nm, a.bgn_date, a.end_date,
       a.bf_mat_unit_price, a.mat_unit_price, a.diff_mat_price,
       a.bf_scrap_price, a.scrap_price, a.diff_scrap_price, a.currency_unit,
       a.mp_remark, a.doc_no, b.doc_title,
       CONVERT(NVARCHAR(19), a.reg_dt, 120) as reg_dt, a.reg_uid, c.account_id AS reg_account_id,
       CONVERT(NVARCHAR(19), a.mod_dt, 120) AS mod_dt,  a.mod_uid, d.account_id AS mod_account_id
from BT_ANNOUNCE_PRICE_DTL a WITH(NOLOCK)
         join BT_ANNOUNCE_PRICE_DOC b WITH(NOLOCK) ON a.doc_no = b.doc_no and b.doc_status = 'Y'
         join MT_MATERIAL m WITH(NOLOCK) ON a.material_cd = m.material_cd
         left outer join ST_USERS c WITH(NOLOCK) ON a.reg_uid = c.user_uid
         left outer join ST_USERS d WITH(NOLOCK) ON a.mod_uid = d.user_uid
where end_date = '99991231'
go



-- =============================================
-- SP_CREATE_TEMPLATE_DOC
-- Author:		Nam Sang Jin
-- Create date: 2024-02-14
-- Description:	템플릿 문서 등록
-- =============================================
CREATE PROCEDURE [dbo].[SP_CREATE_TEMPLATE_DOC]
    @pDocTitle		NVARCHAR(200),
    @pDocFilename	NVARCHAR(200),
    @pWriterUid		NUMERIC(20, 0),
    @pDocPwd	    NVARCHAR(128),
    @pDocRemark     NVARCHAR(300),
    @pDocContent    NVARCHAR(MAX),
    @docNo			NVARCHAR(30) OUT,
    @errCode		INT OUT,
    @errMsg			VARCHAR(500) OUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @vNewDocNo NVARCHAR(30);

    DECLARE @MsId INT = 50001,
        @Severity int = -1,
        @State smallint = -1,
        @vProcName VARCHAR(100) = 'SP_CREATE_TEMPLATE_DOC';

    -- 1. 매개변수 검증
    SET @errCode = 0;
    SET @errMsg = '정상처리 하였습니다.';

    IF ( Len(ISNULL(@pDocTitle, '')) = 0 )
        BEGIN
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR] ', @vProcName, ' ', '매개변수가 유효하지 않습니다.');
            RAISERROR (@MsId, @Severity, @State, @errMsg);
        END;

    -- 문서번호 생성
    SELECT @vNewDocNo= CONCAT( CONVERT(NVARCHAR(4), GETDATE(), 112), '-', RIGHT(CONCAT('00000', CAST((ISNULL(RIGHT(MAX(doc_no), 6), 0)+1) as NVARCHAR(5))), 6) )
    FROM BT_TEMPLATE_DOC
    WHERE doc_no LIKE CONCAT(CONVERT(NVARCHAR(4), GETDATE(), 112), '-', '%');

    -- 문서등록
    INSERT INTO BT_TEMPLATE_DOC (
        doc_no, doc_title, doc_filename, write_dt, writer_uid, doc_status, doc_pwd, doc_remark, doc_content, reg_dt, reg_uid
    ) VALUES (
                 @vNewDocNo, @pDocTitle, @pDocFilename, getdate(), @pWriterUid, 'N', @pDocPwd, @pDocRemark, @pDocContent, getdate(), @pWriterUid
             );

    IF (@@ERROR <> 0)
        BEGIN
            GOTO ERROR_HANDLE;
        END;

    GOTO SUCCESS_HANDLE;

-- =======================================================================================
-- Handle error area
-- =======================================================================================
    SUCCESS_HANDLE:
    SET @errCode = 0;
    SET @errMsg = '정상처리 하였습니다.';
    SET @docNo = @vNewDocNo;
    RETURN(0);

    ERROR_HANDLE:
    SET @errCode = -1000;
    SET @errMsg = '문서 등록에 실패하였습니다.';
    RETURN(1);
END
go

-- =============================================
-- SP_UPDATE_TEMPLATE_DOC
-- Author:		Nam Sang Jin
-- Create date: 2024-02-14
-- Description:	템플릿 문서 등록
-- =============================================
CREATE PROCEDURE [dbo].[SP_UPDATE_TEMPLATE_DOC]
    @pDocNo			NVARCHAR(30),
    @pDocTitle		NVARCHAR(200),
    @pDocFilename	NVARCHAR(200),
    @pDocStatus     CHAR(1),
    @pDocPwd	    NVARCHAR(128),
    @pConfirmDt     DATETIME,
    @pDocRemark     NVARCHAR(300),
    @pDocContent    NVARCHAR(MAX),
    @pModUid        NUMERIC(20,0),
    @errCode		INT OUT,
    @errMsg			VARCHAR(500) OUT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @MsId INT = 50001,
        @Severity int = -1,
        @State smallint = -1,
        @vProcName VARCHAR(100) = 'SP_CREATE_TEMPLATE_DOC';

    -- 1. 매개변수 검증
    SET @errCode = 0;
    SET @errMsg = '정상처리 하였습니다.';

    IF ( Len(ISNULL(@pDocNo, '')) = 0 )
        BEGIN
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR] ', @vProcName, ' ', '매개변수가 유효하지 않습니다.');
            RAISERROR (@MsId, @Severity, @State, @errMsg);
        END;

    -- 문서 수정
    UPDATE BT_TEMPLATE_DOC
    SET mod_dt = GETDATE(),
        doc_title = @pDocTitle,
        doc_filename = @pDocFilename,
        doc_status = ISNULL(@pDocStatus, 'N'),
        doc_pwd = ISNULL(@pDocPwd, ''),
        confirm_dt = (CASE WHEN confirm_dt is null and ISNULL(@pDocStatus, 'N') = 'Y' THEN GETDATE() ELSE NULL END),
        doc_remark = isnull(@pDocRemark, ''),
        doc_content = isnull(@pDocContent, '')
    WHERE doc_no = @pDocNo;

    IF (@@ERROR <> 0)
        BEGIN
            GOTO ERROR_HANDLE;
        END;

    PRINT '업데이트 하였습니다.';
    GOTO SUCCESS_HANDLE;

-- =======================================================================================
-- Handle error area
-- =======================================================================================
    SUCCESS_HANDLE:
    SET @errCode = 0;
    SET @errMsg = '업데이트 하였습니다.';
    RETURN(0);

    ERROR_HANDLE:
    SET @errCode = -1000;
    SET @errMsg = '문서 수정에 실패하였습니다.';
    RETURN(1);
END
go


-- =============================================
-- SP_CREATE_TEMPLATE_DTL
-- Author:		NAM SANG JIN
-- Create date: 2024.02.14
-- Description:	템플릿 문서 세부 항목 등록
-- =============================================
CREATE PROCEDURE [dbo].[SP_CREATE_TEMPLATE_DTL]
    @docNo			nvarchar(30),
    @plantCd		nvarchar(20),
    @bpCd			nvarchar(20),
    @docOrder		nvarchar(10),
    @carModel		nvarchar(20),
    @pcsItemNo		nvarchar(50),
    @pcsItemNm		nvarchar(300),
    @baseDate       nvarchar(10),
    @curItemPrice   money,
    @partType		nvarchar(10),
    @partTypeNm     nvarchar(10),
    @pcsSubItemBp	nvarchar(20),
    @subItemNo		nvarchar(50),
    @subItemNm		nvarchar(300),
    @rawMaterialCd  nvarchar(20),
    @rawMaterialNm  nvarchar(20),
    @materialCd     nvarchar(20),
    @materialNm     nvarchar(200),
    @us             numeric(10,0),
    @steelGrade     nvarchar(20),
    @mSpec          nvarchar(50),
    @mType          nvarchar(50),
    @thickThick     numeric(10,2),		-- 두께/두께
    @widthOuter     numeric(10,2),		-- 가로 / 외경
    @heightInLen    numeric(10,2),		-- 세로 / 투입길이
    @blWidth        numeric(10,2),
    @blLength       numeric(10,2),
    @blCavity       numeric(10,2),
    @netWeight      numeric(20,3),		-- Net 중량
    @specificGravity numeric(10,3),		-- 비중
    @slittLossRate  numeric(10,3),		-- SLITT'G LOSS 율
    @toLossRate     numeric(10,3),		-- (T/O) LOSS율
    @inputWeight    numeric(10,3),
    @bfConsignedPrice  money,
    @afConsignedPrice  money,
    @bfCnsgnMatPrice   money,
    @afCnsgnMatPrice   money,
    @bfScrapUnitPrice  money,
    @afScrapUnitPrice  money,
    @scrapWeight       numeric(20,4),
    @scrapRecoveryRate numeric(20,2),
    @bfScrapPrice      money,
    @afScrapPrice      money,
    @bfPartMatCost     money,
    @afPartMatCost     money,
    @addBaseDate       nvarchar(10),
    @addDocOrder       nvarchar(10),
    @regUid            numeric(20,0),
    @tdSeq             numeric(20,0) out,
    @errCode		   int out,
    @errMsg			   varchar(500) out
AS
BEGIN
    SET NOCOUNT ON;
    declare @newTdSeq int = 0,
        @partTypeNmVal  nvarchar(10) = '',
        @rawMaterialNmVal  nvarchar(20) = '',
        @materialNmVal     nvarchar(200) = '',
        @inputWeightVal numeric(10,3) = 0,
        @scrapWeightVal numeric(10,3) = 0,
        @matAdminRateVal numeric(10,2) = 0,
        @osMatAdminRateVal numeric(10,2) = 0,
        @specificGravityVal numeric(10,3) = 0,
        @slittLossRateVal numeric(10,3) = 0,
        @toLossRateVal numeric(10,3) = 0,
        @scrapRecoveryRateVal numeric(20,2) = 0
    ;
    -- 필수항목
    -- 부품구분 : DD 직개발 DT 직거래 RP 사급

    -- 디폴트 값
    --   소재			비중		SLITTG LOSS(%)		LOSS(%)
    -- 10(철판)			7.85		0.42				0.6
    -- 15(파이프)		7.85		-					2.0

    -- scrap 회수율 : 철판/스크랩 은 99 고정 그 외에는 0으로 설정

    -- 1. 매개변수 검증
    if ( len(trim(isnull(@docNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [문서번호] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@plantCd, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [플랜트코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@bpCd, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [협력사코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@pcsItemNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [매입품번]', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@subItemNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [SUB 품번] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if　( UPPER(ISNULL(@partType, '')) NOT IN ('DD', 'DT', 'RP' ) )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [부품구분] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( (UPPER(trim(@partType)) = 'RP' ) and (len(trim(@pcsSubItemBp)) = 0) )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [사급품 협력사코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if (@us = 0)
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [US] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end;


    -- 공통
    -- 부품코드 체크
    SELECT @partTypeNmVal = cd_nm
    FROM ST_CODE
    WHERE cg_id = 'B02'
      AND cd_nm = TRIM(@partType);

    IF ( TRIM(@partType) IN ('DD', 'DT') )
        BEGIN
            -- 직거래, 직개발 인경우
            -- 소재코드 확인
            IF TRIM(ISNULL(@rawMaterialCd, '')) = ''
                BEGIN
                    SET @errCode = -1000;
                    SET @errMsg = CONCAT('[ERROR]', ' [소재코드] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            IF TRIM(ISNULL(@materialCd, '')) = ''
                BEGIN
                    SET @errCode = -1000;
                    SET @errMsg = CONCAT('[ERROR]', ' [재질코드] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            IF TRIM(ISNULL(@rawMaterialNm, '')) = ''
                BEGIN
                    SELECT @rawMaterialNmVal = cd_nm
                    FROM ST_CODE
                    WHERE cg_id = 'B01'
                      AND cd_nm = TRIM(@rawMaterialCd);
                END
            ELSE
                BEGIN
                    SET @rawMaterialNmVal = @rawMaterialNm;
                END;

            IF TRIM(ISNULL(@materialNm, '')) = ''
                BEGIN
                    SELECT @materialNmVal = material_nm
                    FROM MT_MATERIAL
                    WHERE material_cd = TRIM(@materialCd);
                END
            ELSE
                BEGIN
                    SET @materialNmVal = @materialNm;
                END;


            -- 철판 / 파이프 제외한 나머지 부품은 NET 중량 및 LOSS를 반영한 투입중량 작성
            IF ( (@netWeight = 0) or ( (@rawMaterialCd NOT IN ('10', '15')) and (@inputWeight = 0) ) )
                BEGIN
                    PRINT '철판 / 파이프 제외한 나머지 부품은 NET 중량 및 LOSS를 반영한 투입중량';

                    set @errCode = -1000;
                    set @errMsg = CONCAT('[ERROR]', ' [NET 중량 or 투입중량] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            if ( (@rawMaterialCd = '10') and (@blCavity = 0) )
                begin
                    set @errCode = -1000;
                    set @errMsg = CONCAT('[ERROR]', ' [BL_CAVITY] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                end;
        END;

    IF ( TRIM(@partType) = 'RP' )
        BEGIN
            -- 사급인 경우
            SET @rawMaterialNmVal = Trim(isnull(@rawMaterialNm, ''));
            SET @materialNmVal = Trim(isnull(@materialNm, ''));
            SET @inputWeightVal = 0;
            SET @scrapWeightVal = 0;
            SET @matAdminRateVal = 0;
            SET @osMatAdminRateVal = 1.0;
            SET @specificGravityVal = 0;
            SET @slittLossRateVal = 0;
            SET @toLossRateVal = 0;
            SET @scrapRecoveryRateVal = 0;
        END
    ELSE
        BEGIN
            IF ( TRIM(@partType) = 'DT' )
                BEGIN
                    -- 직거래 재관비율 & 외주재관비율
                    SET @matAdminRateVal = 2.0;
                    SET @osMatAdminRateVal = 1.0;
                END
            ELSE
                BEGIN
                    SET @matAdminRateVal = 2.0;
                    SET @osMatAdminRateVal = 0;
                END;

            -- 재질별 계산식
            if (@rawMaterialCd = '10')
                begin
                    PRINT '-- 철판 / 스크랩 ';
                    PRINT '1. 투입중량 = (두께 * BL 가로 * BL세로 * 비중 * (1 + (SLITTG LOSS율 + ROSS 율)) / 1000000) / BL CAVITY ';

                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.42;
                    SET @toLossRateVal = 0.6;
                    SET @scrapRecoveryRateVal = 99;

                    -- 철/스크랩 투입중량
                    set @inputWeightVal = (@thickThick * @blWidth * @blLength * @specificGravityVal * ( 1.0 + (@slittLossRateVal+@toLossRateVal) / 100.0 ) / 1000000.) / @blCavity ;

                    PRINT '2. 사급 재료비 = 투입중량  * 사급 단가 * (1 + T/O LOSS율) / CAVITY';
                    PRINT '3. SCRAP 중량 = 투입중량 - (NET 중량 * CAVITY)';
                    set @scrapWeightVal = @inputWeightVal - @netWeight;

                    PRINT '4. SCRAP 가격 = (SCRAP 중량 * SCRAP 단가 * SCRAP 회수율) / CAVITY';
                    PRINT '5. 부품 재료비 = 사급 재료비 - SCRAP 가격';
                    PRINT '6. 변동 금액 = (이후 부품재료비 - 이전 부품재료비) * (1 + 재관비율) * U/S';
                end
            else if (@rawMaterialCd = '15')
                begin
                    PRINT '-- 파이프';
                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 2.0;
                    SET @scrapRecoveryRateVal = 0;

                    PRINT '1. 투입중량 = 3.14(π) * ((외경 - 두께) * 두께 * (투입길이+3)*(1 + LOSS율/100) * 비중 / 1000000) ';
                    -- 투입중량　계산　
                    set @inputWeightVal = PI() * (@widthOuter - @thickThick) * @thickThick * (@heightInLen + 3.0) * (1.0 + @toLossRateVal / 100.0 ) * 7.85 / 1000000.0;

                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가 * (1 + 재관비율)';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * U/S';
                end
            else if (@rawMaterialCd = '20')
                begin
                    PRINT '-- 플라스틱 ';
                    PRINT '1. 투입중량 = NET 중량 * (1 + T/O LOSS율) ';
                    SET @scrapRecoveryRateVal = 0;
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    -- set @inputWeightVal = (@netWeight * 1.0) * (1.0 + ((@toLossRate * 1.0) / 100.0) );
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1+재관비율) * U/S';
                end
            else if (@rawMaterialCd = '25')
                begin
                    PRINT '-- H/W 선재';
                    PRINT '1. 투입 중량 : NET 중량 * (1 + T/O LOSS율)';
                    SET @scrapRecoveryRateVal = 0;
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    -- set @inputWeightVal = (@netWeight * 1.0) * (1.0 + ((@toLossRate*1.0) / 100.0) );
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1+재관비율) * U/S';
                end
            else if (@rawMaterialCd = '30')
                begin
                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0.0;

                    PRINT '-- 특수강';
                    PRINT '1. 투입 중량 : NET 중량 * (1 + T/O LOSS율/100)';
                    -- set @inputWeightVal = @netWeight;
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1 + 재관비율) * U/S';
                end
            else if (@rawMaterialCd = '35')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 시트선재';
                    PRINT '-- 1. 변동 금액 : NET 중량 * (이후 사급단가 - 이전 사급단가) * U/S';
                end

            else if (@rawMaterialCd = '40')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 전기동';
                    PRINT '1. 사급 재료비 : (U/S * 사급 단가) * (1 + T/O LOSS율)';
                    PRINT '2. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1 + 재관비율)';
                end
            else if (@rawMaterialCd = '45')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 알루미늄';
                    PRINT '1. 변동 금액 : NET 중량 * (이후 사급단가 - 이전 사급단가) * (1 + 재관비율 ) * U/S';
                end
            else
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    SET @scrapRecoveryRateVal = 0;
                    SET @inputWeightVal = @inputWeight;
                    SET @scrapWeightVal = isnull(@scrapWeight, 0);
                end;
        END;

    -- 항목 순번
    SELECT @newTdSeq = ISNULL(MAX(td_seq), 0) + 1
    FROM BT_TEMPLATE_DTL WITH(NOLOCK)
    WHERE doc_no = @docNo;

    -- 등록 처리
    INSERT INTO BT_TEMPLATE_DTL (
        doc_no, td_seq, plant_cd, bp_cd, doc_order, car_model, pcs_item_no, pcs_item_nm, base_date,
        cur_item_price, part_type, part_type_nm, pcs_sub_item_bp, sub_item_no, sub_item_nm, raw_material_cd, raw_material_nm,
        material_cd, material_nm, us, steel_grade, m_spec, m_type, thick_thick, width_outer, height_in_len,
        bl_width, bl_length, bl_cavity, net_weight,
        specific_gravity, slitt_loss_rate, to_loss_rate, input_weight,
        bf_consigned_price, af_consigned_price, bf_cnsgn_mat_price, af_cnsgn_mat_price,
        bf_scrap_unit_price, af_scrap_unit_price, scrap_weight, scrap_recovery_rate, bf_scrap_price, af_scrap_price,
        bf_part_mat_cost, af_part_mat_cost, mat_admin_rate, os_mat_admin_rate, add_base_date, add_doc_order, reg_dt, reg_uid
    ) VALUES (
                 @docNo, @newTdSeq, @plantCd, @bpCd, @docOrder, @carModel, @pcsItemNo, @pcsItemNm, @baseDate,
                 ISNULL(@curItemPrice, 0), @partType, @partTypeNmVal, @pcsSubItemBp, @subItemNo, @subItemNm, @rawMaterialCd, ISNULL(@rawMaterialNmVal, ''),
                 ISNULL(@materialCd, ''), ISNULL(@materialNmVal, ''), @us, ISNULL(@steelGrade, ''), ISNULL(@mSpec, ''), ISNULL(@mType, ''), @thickThick, @widthOuter, @heightInLen,
                 @blWidth, @blLength, @blCavity, @netWeight,
                 @specificGravityVal, @slittLossRateVal, @toLossRateVal, @inputWeightVal,
                 @bfConsignedPrice, @afConsignedPrice, @bfCnsgnMatPrice, @afCnsgnMatPrice,
                 @bfScrapUnitPrice, @afScrapUnitPrice, @scrapWeightVal, @scrapRecoveryRate, @bfScrapPrice, @afScrapPrice,
                 @bfPartMatCost, @afPartMatCost, @matAdminRateVal,@osMatAdminRateVal, @addBaseDate, @addDocOrder, getdate(), @regUid
             );

    IF (@@ERROR <> 0)
        BEGIN
            PRINT '에러핸들로 이동합니다.';

            SET @errCode = -1;
            SET @errMsg = '등록에 실패하였습니다.';

            GOTO ERROR_HANDLE;
        END;

    IF (LEN(TRIM(ISNULL(@materialCd, ''))) > 3) AND (LEN(TRIM(ISNULL(@materialNm, ''))) > 3)
        BEGIN
            -- 재질마스터 업데이트
            MERGE INTO MT_MATERIAL AS a
            USING (
                SELECT TRIM(ISNULL(@materialCd, '')) AS material_cd,
                       TRIM(ISNULL(@materialNm, '')) AS material_nm,
                       TRIM(ISNULL(@steelGrade, '')) AS steel_grade,
                       TRIM(ISNULL(@rawMaterialCd, '')) AS raw_material_cd,
                       TRIM(ISNULL(@rawMaterialNmVal, '')) AS raw_material_nm
            ) b ON (a.material_cd = b.material_cd)
            WHEN MATCHED THEN
                UPDATE SET
                           material_nm = b.material_nm,
                           steel_grade = b.steel_grade,
                           raw_material_cd = b.raw_material_cd,
                           mod_dt = GETDATE(),
                           mod_uid = @regUid
            WHEN NOT MATCHED THEN
                INSERT (material_cd, material_nm, raw_material_cd, steel_grade, use_at, reg_dt, reg_uid)
                VALUES (b.material_cd, b.material_nm, b.raw_material_cd, b.steel_grade, 'Y', GETDATE(), @regUid);
        END;

    -- 품목 정보 업데이트
    MERGE INTO MT_ITEM AS tgt
    USING (
        SELECT a.item_no, a.item_nm, MAX(a.material_cd) AS material_cd
        FROM (
                 SELECT TRIM(@pcsItemNo) AS item_no,
                        TRIM(@pcsItemNm) AS item_nm,
                        '' AS material_cd
                 UNION ALL
                 SELECT TRIM(@subItemNo) AS item_no,
                        TRIM(@subItemNm) AS item_nm,
                        TRIM(@materialCd) AS material_cd
             ) a
        GROUP BY a.item_no, a.item_nm
    ) AS src ON (tgt.item_no=src.item_no)
    WHEN MATCHED THEN
        UPDATE SET
                   tgt.item_nm  = src.item_nm,
                   tgt.material_cd = src.material_cd,
                   tgt.mod_dt   = GETDATE(),
                   tgt.mod_uid  = @regUid
    WHEN NOT MATCHED THEN
        INSERT (item_no, item_nm, material_cd, reg_dt, reg_uid)
        VALUES (src.item_no, src.item_nm, src.material_cd, GETDATE(), @regUid);


    GOTO SUCCESS_HANDLE;

-- =======================================================================================
-- Handle error area
-- =======================================================================================
    SUCCESS_HANDLE:
    SET @tdSeq = @newTdSeq;

    SET @errCode = 0;
    SET @errMsg = '업데이트 하였습니다.';
    RETURN(0);

    ERROR_HANDLE:
    RETURN(1);
END
go


-- =============================================
-- Procedure Name : SP_UPDATE_TEMPLATE_DTL
-- Author:		NAM SANG JIN
-- Create date: 2024.02.14
-- Description:	템플릿 문서 세부 항목 수정
-- =============================================
CREATE PROCEDURE [dbo].[SP_UPDATE_TEMPLATE_DTL]
    @docNo			nvarchar(30),
    @tdSeq          numeric(20,0),
    @plantCd		nvarchar(20),
    @bpCd			nvarchar(20),
    @docOrder		nvarchar(10),
    @carModel		nvarchar(20),
    @pcsItemNo		nvarchar(50),
    @pcsItemNm		nvarchar(300),
    @baseDate       nvarchar(10),
    @curItemPrice   money,
    @partType		nvarchar(10),
    @partTypeNm     nvarchar(10),
    @pcsSubItemBp	nvarchar(20),
    @subItemNo		nvarchar(50),
    @subItemNm		nvarchar(300),
    @rawMaterialCd  nvarchar(20),
    @rawMaterialNm  nvarchar(20),
    @materialCd     nvarchar(20),
    @materialNm     nvarchar(200),
    @us             numeric(10,0),
    @steelGrade     nvarchar(20),
    @mSpec          nvarchar(50),
    @mType          nvarchar(50),
    @thickThick     numeric(10,2),		-- 두께/두께
    @widthOuter     numeric(10,2),		-- 가로 / 외경
    @heightInLen    numeric(10,2),		-- 세로 / 투입길이
    @blWidth        numeric(10,2),
    @blLength       numeric(10,2),
    @blCavity       numeric(10,2),
    @netWeight      numeric(20,3),		-- Net 중량
    @specificGravity numeric(10,3),
    @slittLossRate  numeric(10,3),
    @toLossRate     numeric(10,3),		-- T/O LOSS율
    @inputWeight    numeric(10,3),
    @bfConsignedPrice  money,
    @afConsignedPrice  money,
    @bfCnsgnMatPrice   money,
    @afCnsgnMatPrice   money,
    @bfScrapUnitPrice  money,
    @afScrapUnitPrice  money,
    @scrapWeight       numeric(20,4),
    @scrapRecoveryRate numeric(20,2),
    @bfScrapPrice      money,
    @afScrapPrice      money,
    @bfPartMatCost     money,
    @afPartMatCost     money,
    @matAdminRate      numeric(10,2),	-- 재관비율(%)
    @osMatAdminRate    numeric(10,2),	-- 외주재관비율(%)
    @addBaseDate       nvarchar(10),	-- 추가기준일
    @addDocOrder       nvarchar(10),	-- 추가차수
    @modUid            numeric(20,0),
    @errCode		   int out,
    @errMsg			   varchar(500) out
AS
BEGIN
    SET NOCOUNT ON;
    declare @partTypeNmVal  nvarchar(10) = '',
        @rawMaterialNmVal  nvarchar(20) = '',
        @materialNmVal     nvarchar(200) = '',
        @inputWeightVal numeric(10,3) = 0,
        @scrapWeightVal numeric(10,3) = 0,
        @matAdminRateVal numeric(10,2) = 0,
        @osMatAdminRateVal numeric(10,2) = 0,
        @specificGravityVal numeric(10,3) = 0,
        @slittLossRateVal numeric(10,3) = 0,
        @toLossRateVal numeric(10,3) = 0,
        @scrapRecoveryRateVal numeric(20,2) = 0
    ;
    -- 필수항목
    -- 부품구분 : DD 직개발 DT 직거래 RP 사급

    -- 디폴트 값
    --   소재			비중		SLITTG LOSS(%)		LOSS(%)
    -- 10(철판)			7.85		0.42				0.6
    -- 15(파이프)		7.85		-					2.0

    -- 1. 매개변수 검증
    if ( len(trim(isnull(@docNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [문서번호] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@plantCd, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [플랜트코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@bpCd, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [협력사코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@pcsItemNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [매입품번]', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( len(trim(isnull(@subItemNo, ''))) = 0 )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [SUB 품번] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if　( UPPER(ISNULL(@partType, '')) NOT IN ('DD', 'DT', 'RP' ) )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [부품구분] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if ( (UPPER(trim(@partType)) = 'RP' ) and (len(trim(@pcsSubItemBp)) = 0) )
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [사급품 협력사코드] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end
    else if (@us = 0)
        begin
            SET @errCode = -1000;
            SET @errMsg = CONCAT('[ERROR]', ' [US] ', '매개변수가 유효하지 않습니다.');
            GOTO ERROR_HANDLE;
        end;


    -- 부품코드 체크
    SELECT @partTypeNmVal = cd_nm
    FROM ST_CODE
    WHERE cg_id = 'B02'
      AND cd_nm = TRIM(@partType);

    IF ( TRIM(@partType) IN ('DD', 'DT') )
        BEGIN
            -- 직거래, 직개발 인경우
            -- 소재코드 확인
            IF TRIM(ISNULL(@rawMaterialCd, '')) = ''
                BEGIN
                    SET @errCode = -1000;
                    SET @errMsg = CONCAT('[ERROR]', ' [소재코드] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            IF TRIM(ISNULL(@materialCd, '')) = ''
                BEGIN
                    SET @errCode = -1000;
                    SET @errMsg = CONCAT('[ERROR]', ' [재질코드] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            IF TRIM(ISNULL(@rawMaterialNm, '')) = ''
                BEGIN
                    SELECT @rawMaterialNmVal = cd_nm
                    FROM ST_CODE
                    WHERE cg_id = 'B01'
                      AND cd_nm = TRIM(@rawMaterialCd);
                END
            ELSE
                BEGIN
                    SET @rawMaterialNmVal = @rawMaterialNm;
                END;

            IF TRIM(ISNULL(@materialNm, '')) = ''
                BEGIN
                    SELECT @materialNmVal = material_nm
                    FROM MT_MATERIAL
                    WHERE material_cd = TRIM(@materialCd);
                END
            ELSE
                BEGIN
                    SET @materialNmVal = @materialNm;
                END;


            -- 철판 / 파이프 제외한 나머지 부품은 NET 중량 및 LOSS를 반영한 투입중량 작성
            IF ( (@netWeight = 0) or ( (@rawMaterialCd NOT IN ('10', '15')) and (@inputWeight = 0) ) )
                BEGIN
                    PRINT '철판 / 파이프 제외한 나머지 부품은 NET 중량 및 LOSS를 반영한 투입중량';

                    set @errCode = -1000;
                    set @errMsg = CONCAT('[ERROR]', ' [NET 중량 or 투입중량] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                END;

            if ( (@rawMaterialCd = '10') and (@blCavity = 0) )
                begin
                    set @errCode = -1000;
                    set @errMsg = CONCAT('[ERROR]', ' [BL_CAVITY] ', '매개변수가 유효하지 않습니다.');
                    GOTO ERROR_HANDLE;
                end;
        END;

    IF ( TRIM(@partType) = 'RP' )
        BEGIN
            -- 사급인 경우
            SET @rawMaterialNmVal = Trim(isnull(@rawMaterialNm, ''));
            SET @materialNmVal = Trim(isnull(@materialNm, ''));
            SET @inputWeightVal = 0;
            SET @scrapWeightVal = 0;
            SET @matAdminRateVal = 0;
            SET @osMatAdminRateVal = 1.0;
            SET @specificGravityVal = 0;
            SET @slittLossRateVal = 0;
            SET @toLossRateVal = 0;
            SET @scrapRecoveryRateVal = 0;
        END
    ELSE
        BEGIN
            IF ( TRIM(@partType) = 'DT' )
                BEGIN
                    -- 직거래 재관비율 & 외주재관비율
                    SET @matAdminRateVal = 2.0;
                    SET @osMatAdminRateVal = 1.0;
                END
            ELSE
                BEGIN
                    SET @matAdminRateVal = 2.0;
                    SET @osMatAdminRateVal = 0;
                END;

            -- 재질별 계산식
            if (@rawMaterialCd = '10')
                begin
                    PRINT '-- 철판 / 스크랩 ';
                    PRINT '1. 투입중량 = (두께 * BL 가로 * BL세로 * 비중 * (1 + (SLITTG LOSS율 + ROSS 율)) / 1000000) / BL CAVITY ';

                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.42;
                    SET @toLossRateVal = 0.6;
                    SET @scrapRecoveryRateVal = 99;

                    -- 철/스크랩 투입중량
                    set @inputWeightVal = (@thickThick * @blWidth * @blLength * @specificGravityVal * ( 1.0 + (@slittLossRateVal+@toLossRateVal) / 100.0 ) / 1000000.) / @blCavity ;

                    PRINT '2. 사급 재료비 = 투입중량  * 사급 단가 * (1 + T/O LOSS율) / CAVITY';
                    PRINT '3. SCRAP 중량 = 투입중량 - (NET 중량 * CAVITY)';
                    set @scrapWeightVal = @inputWeightVal - @netWeight;

                    PRINT '4. SCRAP 가격 = (SCRAP 중량 * SCRAP 단가 * SCRAP 회수율) / CAVITY';
                    PRINT '5. 부품 재료비 = 사급 재료비 - SCRAP 가격';
                    PRINT '6. 변동 금액 = (이후 부품재료비 - 이전 부품재료비) * (1 + 재관비율) * U/S';
                end
            else if (@rawMaterialCd = '15')
                begin
                    PRINT '-- 파이프';
                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 2.0;
                    SET @scrapRecoveryRateVal = 0;

                    PRINT '1. 투입중량 = 3.14(π) * ((외경 - 두께) * 두께 * (투입길이+3)*(1 + LOSS율/100) * 비중 / 1000000) ';
                    -- 투입중량　계산　
                    set @inputWeightVal = PI() * (@widthOuter - @thickThick) * @thickThick * (@heightInLen + 3.0) * (1.0 + @toLossRateVal / 100.0 ) * 7.85 / 1000000.0;

                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가 * (1 + 재관비율)';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * U/S';
                end
            else if (@rawMaterialCd = '20')
                begin
                    PRINT '-- 플라스틱 ';
                    PRINT '1. 투입중량 = NET 중량 * (1 + T/O LOSS율) ';
                    SET @scrapRecoveryRateVal = 0;
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    -- set @inputWeightVal = (@netWeight * 1.0) * (1.0 + ((@toLossRate * 1.0) / 100.0) );
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1+재관비율) * U/S';
                end
            else if (@rawMaterialCd = '25')
                begin
                    PRINT '-- H/W 선재';
                    PRINT '1. 투입 중량 : NET 중량 * (1 + T/O LOSS율)';
                    SET @scrapRecoveryRateVal = 0;
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    -- set @inputWeightVal = (@netWeight * 1.0) * (1.0 + ((@toLossRate*1.0) / 100.0) );
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1+재관비율) * U/S';
                end
            else if (@rawMaterialCd = '30')
                begin
                    SET @specificGravityVal = 7.85;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0.0;

                    PRINT '-- 특수강';
                    PRINT '1. 투입 중량 : NET 중량 * (1 + T/O LOSS율/100)';
                    -- set @inputWeightVal = @netWeight;
                    set @inputWeightVal = @inputWeight;
                    PRINT '2. 사급 재료비 : 투입 중량 * 사급 단가';
                    PRINT '3. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1 + 재관비율) * U/S';
                end
            else if (@rawMaterialCd = '35')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 시트선재';
                    PRINT '-- 1. 변동 금액 : NET 중량 * (이후 사급단가 - 이전 사급단가) * U/S';
                end

            else if (@rawMaterialCd = '40')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 전기동';
                    PRINT '1. 사급 재료비 : (U/S * 사급 단가) * (1 + T/O LOSS율)';
                    PRINT '2. 변동 금액 : (이후 사급재료비 - 이전 사급재료비) * (1 + 재관비율)';
                end
            else if (@rawMaterialCd = '45')
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;
                    SET @scrapRecoveryRateVal = 0;

                    set @inputWeightVal = @inputWeight;

                    PRINT '-- 알루미늄';
                    PRINT '1. 변동 금액 : NET 중량 * (이후 사급단가 - 이전 사급단가) * (1 + 재관비율 ) * U/S';
                end
            else
                begin
                    SET @specificGravityVal = 0.0;
                    SET @slittLossRateVal = 0.0;
                    SET @toLossRateVal = 0.0;

                    SET @scrapRecoveryRateVal = 0;
                    SET @inputWeightVal = @inputWeight;
                    SET @scrapWeightVal = isnull(@scrapWeight, 0);
                end;
        END;

    -- 4. 상세항목 등록
    UPDATE BT_TEMPLATE_DTL
    SET
        plant_cd = @plantCd,
        bp_cd = @bpCd,
        doc_order = @docOrder,
        car_model = @carModel,
        pcs_item_no = @pcsItemNo,
        pcs_item_nm = @pcsItemNm,
        part_type = @partType,
        part_type_nm = @partTypeNmVal,
        pcs_sub_item_bp = @pcsSubItemBp,
        sub_item_no = @subItemNo,
        sub_item_nm = @subItemNm,
        raw_material_cd = @rawMaterialCd,
        raw_material_nm = @rawMaterialNmVal,
        material_cd = @materialCd,
        material_nm = @materialNmVal,
        us = @us,
        steel_grade = @steelGrade,
        m_spec = @mSpec,
        m_type = @mType,
        thick_thick = isnull(@thickThick, 0),
        width_outer = isnull(@widthOuter, 0),
        height_in_len = isnull(@heightInLen, 0),
        bl_width = isnull(@blWidth, 0),
        bl_length = isnull(@blLength, 0),
        bl_cavity = isnull(@blCavity, 0),
        net_weight = isnull(@netWeight, 0),
        specific_gravity = @specificGravityVal,
        slitt_loss_rate = @slittLossRateVal,
        to_loss_rate = @toLossRateVal,
        input_weight = @inputWeightVal,
        scrap_weight = isnull(@scrapWeightVal, 0),
        scrap_recovery_rate = isnull(@scrapRecoveryRateVal, 0),
        mat_admin_rate = isnull(@matAdminRate, 1),
        os_mat_admin_rate = isnull(@osMatAdminRate, 1),
        add_base_date = @addBaseDate,
        add_doc_order = @addDocOrder,
        mod_dt = GETDATE(),
        mod_uid = @modUid
    WHERE doc_no = @docNo AND td_seq = @tdSeq;

    IF (@@ERROR <> 0)
        BEGIN
            print '에러핸들로 이동합니다.';

            SET @errCode = -1;
            SET @errMsg = '등록에 실패하였습니다.';

            GOTO ERROR_HANDLE;
        END;

    IF (LEN(TRIM(ISNULL(@materialCd, ''))) > 3) AND (LEN(TRIM(ISNULL(@materialNm, ''))) > 3)
        BEGIN
            -- 재질마스터 업데이트
            MERGE INTO MT_MATERIAL AS a
            USING (
                SELECT TRIM(ISNULL(@materialCd, '')) AS material_cd,
                       TRIM(ISNULL(@materialNm, '')) AS material_nm,
                       TRIM(ISNULL(@steelGrade, '')) AS steel_grade,
                       TRIM(ISNULL(@rawMaterialCd, '')) AS raw_material_cd,
                       TRIM(ISNULL(@rawMaterialNmVal, '')) AS raw_material_nm
            ) b ON (a.material_cd = b.material_cd)
            WHEN MATCHED THEN
                UPDATE SET
                           material_nm = b.material_nm,
                           steel_grade = b.steel_grade,
                           raw_material_cd = b.raw_material_cd,
                           mod_dt = GETDATE(),
                           mod_uid = @modUid
            WHEN NOT MATCHED THEN
                INSERT (material_cd, material_nm, raw_material_cd, steel_grade, use_at, reg_dt, reg_uid)
                VALUES (b.material_cd, b.material_nm, b.raw_material_cd, b.steel_grade, 'Y', GETDATE(), @modUid);
        END;

    -- 품목 정보 업데이트
    MERGE INTO MT_ITEM AS tgt
    USING (
        SELECT a.item_no, a.item_nm, MAX(a.material_cd) AS material_cd
        FROM (
                 SELECT TRIM(@pcsItemNo) AS item_no,
                        TRIM(@pcsItemNm) AS item_nm,
                        '' AS material_cd
                 UNION ALL
                 SELECT TRIM(@subItemNo) AS item_no,
                        TRIM(@subItemNm) AS item_nm,
                        TRIM(@materialCd) AS material_cd
             ) a
        GROUP BY a.item_no, a.item_nm
    ) AS src ON (tgt.item_no=src.item_no)
    WHEN MATCHED THEN
        UPDATE SET
                   tgt.item_nm  = src.item_nm,
                   tgt.material_cd = src.material_cd,
                   tgt.mod_dt   = GETDATE(),
                   tgt.mod_uid  = @modUid
    WHEN NOT MATCHED THEN
        INSERT (item_no, item_nm, material_cd, reg_dt, reg_uid)
        VALUES (src.item_no, src.item_nm, src.material_cd, GETDATE(), @modUid);

    GOTO SUCCESS_HANDLE;

    -- =======================================================================================
-- Handle error area
-- =======================================================================================

    SUCCESS_HANDLE:
    SET @errCode = 0;
    SET @errMsg = '업데이트 하였습니다.';
    RETURN(0);

    ERROR_HANDLE:
    RETURN(1);
END
go



-- =============================================
-- Procedure Name:		SP_UPDATE_APPLY_END_DATE_FOR_ANNOUNCE_PRICE
-- Author:		남상진
-- Create date: 2024.01.19
-- Description:	마지막 재질 공시단가 적용 종료일 업데이트
-- =============================================
CREATE PROCEDURE [dbo].[SP_UPDATE_APPLY_END_DATE_FOR_ANNOUNCE_PRICE]
    @pCountryCd NVARCHAR(10),
    @pMaterialCd NVARCHAR(30),
    @pNewBeginDate NVARCHAR(10),
    @errCode INT OUT,
    @errMsg  VARCHAR(500) OUT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @vPrevBeginDate NVARCHAR(10);
    DECLARE @vPrevEndDate NVARCHAR(10);

    -- 1. 매개변수 검증
    SET @errCode = 0;
    SET @errMsg = '정상처리 하였습니다.';

    PRINT '마지막 재질 공시단가 적용 종료일 업데이트';

    IF ( (Len(ISNULL(@pCountryCd, '')) = 0) OR (Len(ISNULL(@pMaterialCd, '')) = 0) OR (Len(ISNULL(@pNewBeginDate, '')) = 0))
        BEGIN
            SET @errCode = -1000;
            SET @errMsg = '[ERROR] 매개변수가 유효하지 않습니다.';
            GOTO ERROR_HANDLE;
        END;

    -- 직전 적용 시작일(가격변동일)
    SELECT @vPrevBeginDate = MAX(a.bgn_date)
    FROM BT_ANNOUNCE_PRICE_DTL a WITH(NOLOCK)
             JOIN BT_ANNOUNCE_PRICE_DOC b WITH(NOLOCK) ON a.doc_no = b.doc_no
    WHERE a.country_cd = @pCountryCd
      AND a.material_cd = @pMaterialCd
      AND a.bgn_date < @pNewBeginDate
      AND b.doc_status = 'Y';

    -- 최근 적용일 체크
    IF ISNULL(@vPrevBeginDate, '') = ''
        BEGIN
            PRINT '최근 적용일이 존재하지 않습니다.';
            SET @errCode = -1001;
            SET @errMsg = '업데이트할 데이터가 존재하지 않습니다.';
            GOTO ERROR_HANDLE;
        END
    ELSE
        BEGIN
            PRINT '최근 공시단가 종료 적용일자 업데이트';
            -- 최근 공시단가 종료 적용일자 업데이트
            SET @vPrevEndDate = CONVERT(NVARCHAR(10), DATEADD(DAY, -1, @pNewBeginDate), 23);

            UPDATE BT_ANNOUNCE_PRICE_DTL
            SET end_date = @vPrevEndDate
            WHERE country_cd = @pCountryCd
              AND material_cd = @pMaterialCd
              AND bgn_date = @vPrevBeginDate;
        END;

    GOTO SUCCESS_HANDLE;
    SUCCESS_HANDLE:
    SET @errCode = 0;
    SET @errMsg = '업데이트 하였습니다.';
    RETURN(0);

    ERROR_HANDLE:
    RETURN(1);
END
go


-- =============================================
-- Procedure Name :		SP_CLOSE_ANNOUNCE_PRICE_DOC
-- Author:		NAM SANG JIN
-- Create date: 2024-01-19
-- Description:	공시단가문서 마감 처리
-- =============================================
CREATE PROCEDURE [dbo].[SP_CLOSE_ANNOUNCE_PRICE_DOC]
    @pDocNo  NVARCHAR(30),
    @pRequestUserUid NUMERIC(20, 0),
    @errCode INT OUT,
    @errMsg  VARCHAR(500) OUT
AS
BEGIN
    -- SET NOCOUNT ON added to prevent extra result sets from
    -- interfering with SELECT statements.
    SET NOCOUNT ON;
    DECLARE @errorCode INT,
        @errorMessage VARCHAR(500),
        @countryCd  NVARCHAR(10),
        @materialCd NVARCHAR(30),
        @bgnDate NVARCHAR(10),
        @endDate NVARCHAR(10),
        @bfMatUnitPrice NUMERIC(20,2),
        @matUnitPrice NUMERIC(20,2),
        @diffMatPrice NUMERIC(20,2),
        @bfScrapPrice NUMERIC(20,2),
        @scrapPrice NUMERIC(20,2),
        @diffScrapPrice NUMERIC(20,2);

    PRINT '공시단가문서 마감 처리';

    -- 1. 매개변수 검증
    SET @errCode = 0;
    SET @errMsg = '정상처리 하였습니다.';

    IF (ISNULL(Trim(@pDocNo), '') = '')
        BEGIN
            PRINT '매개변수가 유효하지 않습니다';
            SET @errCode = -1000;
            SET @errMsg = '[ERROR] 매개변수가 유효하지 않습니다.';
            GOTO ERROR_HANDLE;
        END;

    -- 2. 재질 공시 단가에 대한 처리
    BEGIN
        DECLARE announce_price_cur CURSOR FOR
            SELECT material_cd, bgn_date, end_date, bf_mat_unit_price, mat_unit_price, diff_mat_price,
                   bf_scrap_price, scrap_price, diff_scrap_price, country_cd
            FROM BT_ANNOUNCE_PRICE_DTL WITH(NOLOCK)
            WHERE doc_no = @pDocNo
            ORDER BY material_cd, bgn_date;

        OPEN announce_price_cur;

        FETCH NEXT FROM announce_price_cur
            INTO @materialCd, @bgnDate, @endDate, @bfMatUnitPrice, @matUnitPrice, @diffMatPrice,
                @bfScrapPrice, @scrapPrice, @diffScrapPrice, @countryCd;

        WHILE @@FETCH_STATUS = 0
            BEGIN
                PRINT '직전 공시단가 정보 업데이트';
                -- 직전 공시단가 정보 업데이트
                EXEC dbo.SP_UPDATE_APPLY_END_DATE_FOR_ANNOUNCE_PRICE @countryCd, @materialCd, @bgnDate, @errorCode, @errorMessage;
                IF @errorCode < 0
                    BEGIN
                        PRINT '직전 공시단가 정보 업데이트를 실패하였습니다.';
                        SET @errCode = -1000;
                        SET @errMsg = '[ERROR] 직전 공시단가 정보 업데이트를 실패하였습니다.';
                        GOTO ERROR_HANDLE;
                    END;

                -- 다음행
                FETCH NEXT FROM announce_price_cur
                    INTO  @materialCd, @bgnDate, @endDate, @bfMatUnitPrice, @matUnitPrice, @diffMatPrice,
                        @bfScrapPrice, @scrapPrice, @diffScrapPrice, @countryCd;
            END;

        CLOSE announce_price_cur;
        DEALLOCATE announce_price_cur;
    END;

    PRINT '공시단가 문서 상태 확정 처리' + @pDocNo;
    -- 3. 공시단가 문서 상태 확정 처리
    UPDATE BT_ANNOUNCE_PRICE_DOC
    SET doc_status = 'Y'
      , confirm_dt = GETDATE()
      , mod_dt = GETDATE()
      , mod_uid = @pRequestUserUid
    WHERE doc_no = @pDocNo;

    GOTO SUCCESS_HANDLE;

-- =======================================================================================
-- Handle error area
-- =======================================================================================
    SUCCESS_HANDLE:
    SET @errCode = 0;
    SET @errMsg = '업데이트 하였습니다.';
    RETURN(0);

    ERROR_HANDLE:
    RETURN(1);
END
go

-- =============================================
-- Function Name : FN_GET_LAST_ANNOUNCE_BEGIN_DATE
-- Author:		남상진
-- Create date: 2024.01.19
-- Description:	마지막 적용일 조회
-- =============================================
CREATE FUNCTION FN_GET_LAST_ANNOUNCE_BEGIN_DATE
(
    @pCountryCd NVARCHAR(10),
    @pMaterialCd NVARCHAR(30),
    @pNewBeginDate NVARCHAR(10)
)
    RETURNS NVARCHAR(10)
AS
BEGIN
    DECLARE @vResultVal NVARCHAR(10);

    SET @vResultVal = '';

    IF ( (Len(ISNULL(@pCountryCd, '')) = 0) OR (Len(ISNULL(@pMaterialCd, '')) = 0) )
        BEGIN
            RETURN @vResultVal;
        END;

    -- 직전 적용일(가격변동일)
    SELECT @vResultVal = MAX(a.bgn_date)
    FROM BT_ANNOUNCE_PRICE_DTL a WITH(NOLOCK)
             JOIN BT_ANNOUNCE_PRICE_DOC b WITH(NOLOCK) ON a.doc_no = b.doc_no
    WHERE a.country_cd = @pCountryCd
      AND a.material_cd = @pMaterialCd
      AND a.bgn_date < @pNewBeginDate
      AND b.doc_status = 'Y';

    RETURN @vResultVal;
END
go


-- =============================================
-- Function Name : FN_GET_PREV_ANNOUNCE_APPLY_DATE
-- =============================================
CREATE FUNCTION FN_GET_PREV_ANNOUNCE_APPLY_DATE (
    @pDocNo NVARCHAR(30),
    @pCountryCd NVARCHAR(10),
    @pMaterialCd NVARCHAR(30)
)
    RETURNS NVARCHAR(10)
BEGIN
    DECLARE @vResultVal NVARCHAR(10);

    SET @vResultVal = '';

    IF ( (Len(ISNULL(@pDocNo, '')) = 0) OR (Len(ISNULL(@pCountryCd, '')) = 0) OR (Len(ISNULL(@pMaterialCd, '')) = 0) )
        BEGIN
            RETURN @vResultVal;
        END;

    SELECT @vResultVal = MAX(bgn_date)
    FROM BT_ANNOUNCE_PRICE_DTL
    WHERE doc_no != @pDocNo
      AND country_cd = @pCountryCd
      AND material_cd = @pMaterialCd;

    RETURN @vResultVal;
END
go

-- =============================================
-- Function Name : FN_BEFORE_ANNOUNCE_DATE
-- Author:		Nam Sangjin
-- Create date: 2024-01-20
-- Description:	직전 공시단가 적용일
-- =============================================
CREATE FUNCTION FN_BEFORE_ANNOUNCE_DATE
(  )
    RETURNS TABLE
        AS
        RETURN
            (
                SELECT a.country_cd, a.material_cd, MAX(a.bgn_date) AS bgn_date
                FROM BT_ANNOUNCE_PRICE_DTL a WITH(NOLOCK)
                         JOIN BT_ANNOUNCE_PRICE_DOC b ON a.doc_no = b.doc_no AND b.doc_status = 'Y'
                WHERE a.end_date != '2999-12-31'
                GROUP BY a.country_cd, a.material_cd
            )
go

-- =============================================
-- Function Name : FN_BEFORE_ANNOUNCE_PRICE
-- Author:		Nam Sangjin
-- Create date: 2024-01-20
-- Description:	직전 공시단가
-- =============================================
CREATE FUNCTION FN_BEFORE_ANNOUNCE_PRICE
( )
    RETURNS TABLE
        AS
        RETURN
            (
                select  a.country_cd, a.material_cd, a.mat_unit_price, a.scrap_price, a.currency_unit
                from FN_BEFORE_ANNOUNCE_DATE() as w
                         join BT_ANNOUNCE_PRICE_DTL a ON
                            w.country_cd = a.country_cd
                        and w.material_cd = a.material_cd
                        and w.bgn_date = a.bgn_date
            )
go

