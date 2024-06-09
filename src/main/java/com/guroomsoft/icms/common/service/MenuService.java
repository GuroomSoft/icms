package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.GroupAuthority;
import com.guroomsoft.icms.common.dao.MenuDAO;
import com.guroomsoft.icms.common.dto.Menu;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.util.AppContant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuDAO menuDAO;
    private final UserDAO userDAO;

    /**
     * 시스템 메뉴 유형
     */
    public enum MenuType {
        SECTION("S"),          // 메뉴구분자
        DIRECTORY("D"),          // 폴더
        MENU("M");            // 메뉴

        String value;
        MenuType(String value) { this.value = value; }

        public String getValue() { return this.value; }
    }


    @Transactional(readOnly = true)
    public List<Menu> findMenus(Menu cond)
    {
        try {
            return menuDAO.selectMenus(cond);
        } catch (Exception e) {
            log.error("{} ::: findMenus", this.getClass().getSimpleName());
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<Menu> findAllMenus(Long parentUid) throws Exception {
        List<Menu> menus = new ArrayList<>();

        Menu cond = new Menu();
        cond.setParentUid(parentUid);

        menus = menuDAO.selectMenus(cond);

        if (menus == null) return null;

        for (Menu menu : menus) {
            if (menu.getMenuType().equals("S") || menu.getMenuType().equals("D") ) {
                List<Menu> child = findAllMenus(menu.getMenuUid());
                menu.setChileMenu( child );
            }
        }
        return menus;
    }

    @Transactional(readOnly = true)
    @Cacheable("permissionInfo")
    public List<GroupAuthority> findAuthorizedMenus(Long userUid, Long parentMenuUid) throws Exception {
        List<GroupAuthority> menus = menuDAO.selectAuthAuthoritiesByUser(userUid, parentMenuUid);
        if (menus == null) {
            return null;
        }

        for (GroupAuthority menu : menus) {
            if (menu.getMenuType().equals("S") || menu.getMenuType().equals("D") ) {
                List<GroupAuthority> child = findAuthorizedMenus(userUid, menu.getMenuUid());
                menu.setChild(child);
            }
        }
        return menus;
    }

    @Transactional(readOnly = true)
    @Cacheable("permissionInfo")
    public List<GroupAuthority> findAuthorizedMenusByPlain(Long userUid, Long parentMenuUid) throws Exception {
        return menuDAO.selectAuthAuthoritiesByUser(userUid, parentMenuUid);
    }

    public boolean isValidNewMenu(Menu data) {
        if (data == null) return false;
        if ( (data.getMenuUid() != null) && (data.getMenuUid().longValue() > 0) ) return false;
        if (StringUtils.isBlank(data.getMenuNm())) return false;
        if (StringUtils.isBlank(data.getMenuCd())) return false;

        return true;
    }

    public Menu setDefaultMenuValue(Menu data) {
        if (data.getParentUid() == null) data.setParentUid(Long.valueOf(0));
        if (StringUtils.isBlank(data.getMenuShortNm())) data.setMenuShortNm(data.getMenuNm());
        if (data.getMenuLvl() == null) data.setMenuLvl(1);
        if (StringUtils.isBlank(data.getMenuType())) data.setMenuType(MenuService.MenuType.MENU.getValue());
        if (StringUtils.isBlank(data.getDisplayAt())) data.setDisplayAt(AppContant.CommonValue.YES.getValue());
        if (data.getDisplayOrd() == null) data.setDisplayOrd(9999);

        return data;
    }

    /**
     * 메뉴 등록
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public Menu createMenu(@NonNull Menu data) throws Exception{
        if (!isValidNewMenu(data)) {
            throw new CInvalidArgumentException();
        }

        Menu newMenu = setDefaultMenuValue(data);
        int inserted = menuDAO.insertMenu(newMenu);

        if (inserted == 1) {
            return newMenu;
        } else {
            return null;
        }
    }

    public boolean isValidMenu(Menu data) {
        if (data == null) return false;
        if (data.getMenuUid() == null || data.getMenuUid().longValue() == 0) return false;

        return true;
    }

    @Transactional
    public Menu modifyMenu(@NonNull Menu data) throws Exception{
        if (!isValidMenu(data)) {
            throw new CRequiredException();
        }
        int updated = menuDAO.updateMenu(data);
        if (updated == 1) {
            return data;
        }
        return null;
    }

    /**
     * 메뉴 삭제
     * @param menuUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeMenu(@NonNull Long menuUid) throws Exception{
        if ( (menuUid == null) || (menuUid.longValue() == 0) ) {
            throw new CRequiredException();
        }

        int deleted = menuDAO.deleteMenu(menuUid);
        if (deleted > 0) {
            // 그룹 권한 삭제
            userDAO.deleteGroupAuthority(null, menuUid);
        }
        return deleted;
    }


}
