package com.guroomsoft.icms.common.dao;

import com.guroomsoft.icms.auth.dto.GroupAuthority;
import com.guroomsoft.icms.common.dto.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :
 *  ST_MENU
 */
@Mapper
public interface MenuDAO {
    List<Menu> selectMenus(Menu cond) throws Exception;
    List<GroupAuthority> selectAuthAuthoritiesByUser(Long userUid, Long parentMenuUid) throws Exception;
    Menu selectMenuByKey(Long menuUid) throws Exception;
    int insertMenu(Menu data) throws Exception;
    int updateMenu(Menu data) throws Exception;
    int deleteMenu(Long menuUid) throws Exception;

}
