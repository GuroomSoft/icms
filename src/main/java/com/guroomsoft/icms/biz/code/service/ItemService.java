package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.biz.code.dao.ItemDAO;
import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Item;
import com.guroomsoft.icms.biz.code.dto.ItemReq;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.common.exception.CDatabaseException;
import com.guroomsoft.icms.sap.JcoClient;
import com.guroomsoft.icms.util.AppContant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private static String RFC_NAME = "ZMM_ICMS_EXPORT_MASTER_S";       // 품목 I/F
    private static String RFC_IN_PLANTS = "I_WERKS";            // RFC IMPORT 플랜트 코드 목록
    private static String RFC_TABLE_NAME = "T_HEADER";          // RFC TABLE NAME
    private final JcoClient jcoClient;

    private final PlantDAO plantDAO;
    private final ItemDAO itemDAO;
    /**
     * 품목 목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Item> findItem(ItemReq cond) throws Exception
    {
        try {
            List<Item> resultSet = itemDAO.selectItem(cond);
            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    @Transactional(readOnly = true)
    public int getTotalItemCount(ItemReq cond) throws Exception
    {
        try {
            return itemDAO.getTotalItemCount(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    public int downloadItemFromSap(LinkedHashMap<String, Object> params, Long reqUserUid) throws Exception
    {
        try {
            jcoClient.getFunction(RFC_NAME);
            LinkedHashMap<String, Object> impParams = new LinkedHashMap<>();
            if (params.containsKey("plantCd")) {
                impParams.put(RFC_IN_PLANTS, params.get("plantCd"));        // 공급업체코드
            }

            log.info(impParams.toString());
            jcoClient.setImportParam(impParams);
            jcoClient.runRunction();
            ArrayList<Map<String, Object>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

            // convert to object list
            ArrayList<Item> dataRows = convertToItem(dataSet);

            // save DB
            return loadToDB(dataRows, reqUserUid);
        } catch (Exception e) {
            log.error("👉 Fail to get jco function");
            log.error(e.getMessage());
        }
        return 0;
    }

    public ArrayList<Item> convertToItem(ArrayList<Map<String, Object>> dataSet)
    {
        if (dataSet == null || dataSet.isEmpty()) {
            return null;
        }

        ArrayList<Item> resultList = new ArrayList<>();
        for (Map<String, Object> dataItem : dataSet)
        {
            Item dataRow = new Item();
            dataRow.setIfSeq((String)dataItem.get("ZIFSEQ"));                                   // 인터페이스 번호     S0000000000000000075
            dataRow.setIfType(StringUtils.defaultString((String)dataItem.get("ZIFTYPE")));      // 인터페이스 유형     TEST
            dataRow.setPlantCd((String)dataItem.get("WERKS"));      // 플랜트 코드
            dataRow.setItemNo((String)dataItem.get("MATNR"));       // 자재번호
            dataRow.setItemNm((String)dataItem.get("MAKTX"));           // 자재내역
            dataRow.setUnit((String)dataItem.get("MEINS"));         // 기본단위
            dataRow.setCarModel((String)dataItem.get("ZZPRJT"));    // 차종
            dataRow.setUseAt((String)dataItem.get("ZUSEID"));       // 사용여부
            dataRow.setIfResult(StringUtils.defaultString((String)dataItem.get("ZIF_RESULT")));    // 메시지 유형       S : 성공 /  E : 오류
            dataRow.setIfMessage(StringUtils.defaultString((String)dataItem.get("ZIF_MESSAGE")));  // 메시지
            resultList.add(dataRow);
        }

            return resultList;
    }

    public int loadToDB(ArrayList<Item> dataRows, Long reqUserUid)
    {
        int totalUpdated = 0;
        Long userUid = 1L;
        if (reqUserUid != null) {
            userUid = reqUserUid;
        }
        for (Item item : dataRows)
        {
            int t = 0;
            try {
                item.setRegUid(userUid);
                // 유효성 체크
                if (StringUtils.isBlank(item.getPlantCd()) || StringUtils.isBlank(item.getItemNo())
                        || StringUtils.isBlank(item.getItemNm()) )
                {
                    log.debug(">>> SKIP {}", item.toString() );
                    continue;
                }

                log.debug(item.toString());

                int updated = itemDAO.mergeItem2(item);
                if (updated > 0) {
                    totalUpdated++;
                }
            } catch (Exception e1) {
                log.error("👉 fail to save {}", item);
            }
        }
        return totalUpdated;
    }

    /**
     * SAP 로부터 품목 정보 적재
     * 06시 실행
     */
    @Transactional
    @Scheduled(cron = "0 0 4 * * *")
    public void scheduleDownloadPurchaseFromSAP()
    {
        try{
            List<Plant> plantList = getPlantList("KR");
            for (Plant item : plantList) {
                List<String> plants = new ArrayList<>();
                plants.add(item.getPlantCd());

                LinkedHashMap<String, Object> params = new LinkedHashMap<>();
                params.put("plants", plants);

                downloadItemFromSap(params, null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 플랜트 목록 조회
     * @return
     */
    private List<Plant> getPlantList(String countryCd)
    {
        Plant cond = new Plant();
        if (StringUtils.isNotBlank(countryCd)) cond.setPlantCountry(countryCd);
        else cond.setPlantCountry("KR");

        cond.setUseAt(AppContant.CommonValue.YES.getValue());
        try {
            return plantDAO.selectPlant(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
