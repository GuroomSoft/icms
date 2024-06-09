package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Item;
import com.guroomsoft.icms.biz.code.dto.ItemReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 품번 정보
 *  MT_ITEM
 *
 */
@Mapper
public interface ItemDAO {
    /* MT_ITEM */
    List<Item> selectItem(ItemReq cond) throws Exception;
    Item selectItemByKey(String itemNo) throws Exception;
    int getTotalItemCount(ItemReq cond) throws Exception;

    int mergeItem(Item data) throws Exception;
    int mergeItem2(Item data) throws Exception;
    int deleteItem(String itemNo) throws Exception;

}
