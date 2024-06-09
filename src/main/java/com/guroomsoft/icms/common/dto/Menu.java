package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * Table :
 *  ST_MENU
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("menu")
public class Menu extends BaseDTO {
    @Schema(description = "메뉴 UID")
    @JsonProperty("menuUid")
    private Long menuUid;

    @Schema(description = "메뉴명")
    @JsonProperty("menuNm")
    private String menuNm;

    @Schema(description = "메뉴 약식명")
    @JsonProperty("menuShortNm")
    private String menuShortNm;

    @Schema(description = "메뉴코드")
    @JsonProperty("menuCd")
    private String menuCd;

    @Schema(description = "부모메뉴 UID")
    @JsonProperty("parentUid")
    private Long parentUid;

    @Schema(description = "메뉴아이콘")
    @JsonProperty("menuIcon")
    private String menuIcon;

    @Schema(description = "메뉴레벨")
    @JsonProperty("menuLvl")
    private Integer menuLvl;

    @Schema(description = "메뉴유형(S: Section, D: Directory, M: Menu)")
    @JsonProperty("menuType")
    private String menuType;

    @Schema(description = "Redirect 경로")
    @JsonProperty("redirectPath")
    private String redirectPath;

    @Schema(description = "노출여부")
    @JsonProperty("displayAt")
    private String displayAt;

    @Schema(description = "표시순서")
    @JsonProperty("displayOrd")
    private Integer displayOrd;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "경로")
    @JsonProperty("menuPath")
    private String menuPath;

    @Schema(description = "메뉴설명")
    @JsonProperty("menuRemark")
    private String menuRemark;

    @Schema(description = "Sub Menu")
    @JsonProperty("childMenu")
    private List<Menu> chileMenu;

}
