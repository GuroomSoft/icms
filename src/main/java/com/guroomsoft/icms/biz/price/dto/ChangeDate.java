package com.guroomsoft.icms.biz.price.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * Table Name:  가격변경이력상세
 *  HT_CHANGED_PRICE
 */
@Data
@Alias("changeDate")
public class ChangeDate {
    private String applyDate;
    private String afCol;
    private String diffCol;
}
