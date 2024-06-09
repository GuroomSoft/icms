package com.guroomsoft.icms.biz.material.dao;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import com.guroomsoft.icms.biz.material.dto.AnnouncePriceDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table : 원소재 공시단가 관련 테이블
 * BT_ANNOUNCE_PRICE_DOC    공시단가 문서
 * BT_ANNOUNCE_PRICE_DTL    공시단가 상세
 * BT_ANNOUNCE_PRICE_LAST   최종 공시단가
 * BT_ANNOUNCE_PRICE_BF     직전 공시단가
 */
@Mapper
public interface AnnouncePriceDAO {
    /* BT_ANNOUNCE_PRICE_DOC */
    int insertAnnouncePriceDoc(AnnouncePrice data) throws Exception;
    int updateAnnouncePriceDoc(AnnouncePrice data) throws Exception;
    int updateDocStatus(AnnouncePrice data) throws Exception;
    int deleteAnnouncePriceDoc(String docNo) throws Exception;
    List<AnnouncePrice> selectAnnouncePriceDoc(Map<String, String> cond) throws Exception;
    AnnouncePrice selectAnnouncePriceDocByKey(String docNo) throws Exception;
    AnnouncePrice selectAnnouncePriceDocByKeySimple(String docNo) throws Exception;

    /* BT_ANNOUNCE_PRICE_DTO */
    int insertAnnouncePriceDtl(AnnouncePriceDetail data) throws Exception;

    int insertMultiAnnouncePriceDtl(List<AnnouncePriceDetail> data) throws Exception;
    int updateAnnouncePriceDtl(AnnouncePriceDetail data) throws Exception;
    int deleteAnnouncePriceDtl(AnnouncePriceDetail param) throws Exception;
    List<AnnouncePriceDetail> selectAnnouncePriceDtl(AnnouncePriceDetail cond) throws Exception;
    AnnouncePriceDetail selectAnnouncePriceDtlByKey(AnnouncePriceDetail cond) throws Exception;
    List<AnnouncePriceDetail> selectAnnouncePriceDtlSimple(AnnouncePriceDetail cond) throws Exception;
    List<AnnouncePriceDetail> selectAnnouncePriceDtlInOthers(AnnouncePriceDetail cond) throws Exception;

    Map<String, Object> closeAnnouncePriceDoc(Map<String, Object> params) throws Exception;
    Map<String, Object> cancelAnnouncePriceDoc(Map<String, Object> params) throws Exception;

    List<Map<String, Object>> selectAnnouncePriceLast(AnnouncePriceDetail cond) throws Exception;
    List<AnnouncePriceDetail> selectAnnouncePriceHistory(String countryCd, String materialCd, String materialNm) throws Exception;
    int getNewerMaterialCount(AnnouncePriceDetail cond) throws Exception;
    List<Map<String, String>> selectAnnouncedDate() throws Exception;
    List<String> selectApplyDate(String docNo)  throws Exception;
}
