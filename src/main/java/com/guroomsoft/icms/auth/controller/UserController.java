package com.guroomsoft.icms.auth.controller;

import com.guroomsoft.icms.auth.dto.Group;
import com.guroomsoft.icms.auth.dto.GroupAuthority;
import com.guroomsoft.icms.auth.dto.GroupMember;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.service.UserService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.MenuService;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.CipherUtil;
import com.guroomsoft.icms.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "사용자, 사용자 그룹, 사용자그룹 멤버, 권한 관리 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final ResponseService responseService;
    private final UserService userService;
    private final MenuService menuService;
    private final AppConfigService appConfigService;

    @Operation(summary = "사용자 목록 조회", description = "로그인 사용자 목록 조회, Profile 정보가 같은 레벨의 속성으로 조회됨")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    public ListResult<User> findUsers(
            @Parameter(description = "조회조건", required = true) @RequestBody User cond)
    {
        List<User> resultSet = null;
        try {
            resultSet = userService.findUsers(cond);
            return responseService.getListResult(resultSet);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "내부 사용자 목록 조회", description = "내부 사용자 목록을 조회")
    @RequestMapping(value = "/employee", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findEmployee(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "부서코드") @RequestParam(required = false) String orgCd,
            @Parameter(description = "팀코드") @RequestParam(required = false) String teamCd)
    {
        User cond = new User();
        if (StringUtils.isNotBlank(corpCd)) cond.setCorpCd(corpCd);
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(orgCd)) cond.setOrgCd(orgCd);
        if (StringUtils.isNotBlank(teamCd)) cond.setTeamCd(teamCd);

        try {
            List<Map<String, Object>> resultSet = userService.findEmployee(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(new ArrayList<>());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }


    @Operation(summary = "로그인 사용자정보 조회", description = "로그인 사용자정보 조회")
    @RequestMapping(value = "/{accountId}", method = {RequestMethod.GET})
    public DataResult<User> findById(
            @Parameter(description = "사용자ID", required = true) @PathVariable(name="accountId") String accountId)
    {
        User user = null;
        try {
            user = userService.findByAccountId(accountId);
            return responseService.getDataResult(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "사용자아이디 검색", description = "협력사 사업자번호로 사용자 아이디를 검색")
    @RequestMapping(value = "/findUserId/{bizRegNo}", method = {RequestMethod.GET})
    public DataResult<Map<String, String>> getAccountIdByBizRegNo(
            @Parameter(description = "사업자등록번호", required = true) @PathVariable(name="bizRegNo") String bizRegNo)
    {
        try {
            Map<String, String> accountMap = userService.findAccountIdWithBizRegNo(StringUtils.replace(bizRegNo, "-", ""));
            return responseService.getDataResult(accountMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getDataResult(null);
        }
    }


    @Operation(summary = "로그인 사용자정보 조회", description = "User UID 기준 로그인 사용자정보 조회, Profile 정보도 조회")
    @RequestMapping(value = "/findUserByKey/{userUid}", method = {RequestMethod.GET})
    public DataResult<User> findByKey(
            @Parameter(description = "사용자UID", required = true) @PathVariable(name="userUid") Long userUid)
    {
        User result = null;
        try {
            result = userService.findUserByKey(userUid);
            return responseService.getDataResult(result);
        } catch ( CRequiredException e ) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "로그인 사용자 등록", description = "로그인 사용자 등록처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<User> registrationUser(
            @Parameter(description = "사용자정보", required = true) @RequestBody User user,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            User newUser = userService.createUser(user, Long.valueOf(reqUserUid));
            return responseService.getDataResult(newUser);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "로그인 사용자 정보 수정", description = "로그인 사용자 정보 수정처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public DataResult<User> modifyUser(
            @Parameter(description = "사용자정보", required = true) @RequestBody User user,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            user.setModUid(Long.valueOf(reqUserUid));
            User newUser = userService.modifyUser(user);
            return responseService.getDataResult(newUser);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "로그인 계정 영구 삭제 처리", description = "로그인 계정, Profile, 멤버 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeUserPermanent(
            @Parameter(description = "사용자정보", required = true) @RequestBody User user,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        if (user.getUserUid() == 1) {
            return responseService.getFailResult( -1000, "시스템 계정은 영구삭제 처리를 할 수 없습니다.");
        }

        try {
            int deleted = userService.removeUserPermanent(user.getUserUid());
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult( CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "로그인 사용자 권한 조회", description = "로그인 사용자의 시스템 권한 조회")
    @RequestMapping(value = "/authority/findResources/{accountId}", method = {RequestMethod.GET})
    public ListResult<GroupAuthority> findAuthorizedResourceById(
            @Parameter(description = "사용자ID", required = true) @PathVariable(name="accountId") String accountId)
    {
        User user = null;
        try {
            user = userService.findByAccountId(accountId);

            if (user == null) {
                return responseService.getListResult(null);
            }

            List<GroupAuthority> menus = menuService.findAuthorizedMenus(user.getUserUid(), Long.valueOf(0));
            return responseService.getListResult(menus);

        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }


    @Operation(summary = "로그인 사용자 암호 수정", description = "로그인 사용자 암호 수정처리 - 이전암호 확인")
    @RequestMapping(value = "/modifyPassword", method = {RequestMethod.POST})
    public CommonResult modifyUserPassword(
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "변경전 암호", required = true) @RequestParam String oldPassword,
            @Parameter(description = "변경후 암호", required = true) @RequestParam String newPassword,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            User user = userService.findUserByKey(userUid);
            if (user == null) {
                throw new CNotFoundException();
            }

            // 기존 암호 비교
            String oldEncryptPwd = CipherUtil.sha512encrypt(oldPassword);
            if (!oldEncryptPwd.equals(user.getAccountPwd())) {
                throw new CNotMatchException();
            }

            int updated = userService.modifyUserPassword(userUid, newPassword, reqUserUid);
            if (updated > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CNotFoundException e) {
            return responseService.getFailResult(
                    CNotFoundException.getCode(),
                    MessageUtil.getMessage(CNotFoundException.KEY_MSG, new String[]{"사용자 정보"}));
        } catch (CNotMatchException e) {
            return responseService.getFailResult(
                    CNotMatchException.getCode(),
                    MessageUtil.getMessage(CNotMatchException.KEY_MSG, new String[]{"암호"}));
        } catch (CRequiredException e) {
            return responseService.getFailResult( CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult( CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CInvalidPasswordException e) {
            return responseService.getFailResult( CInvalidPasswordException.getCode(), CInvalidPasswordException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "로그인 사용자 암호 변경", description = "로그인 사용자 암호 변경처리- 이전암호 확인 안함")
    @RequestMapping(value = "/changePassword", method = {RequestMethod.POST})
    public CommonResult changeUserPassword(
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "변경후 암호", required = true) @RequestParam String newPassword,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            User user = userService.findUserByKey(userUid);
            if (user == null) {
                throw new CNotFoundException();
            }

            int updated = userService.modifyUserPassword(userUid, newPassword, reqUserUid);
            if (updated > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CNotFoundException e) {
            return responseService.getFailResult(
                    CNotFoundException.getCode(),
                    MessageUtil.getMessage(CNotFoundException.KEY_MSG, new String[]{"사용자 정보"}));
        } catch (CNotMatchException e) {
            return responseService.getFailResult(
                    CNotMatchException.getCode(),
                    MessageUtil.getMessage(CNotMatchException.KEY_MSG, new String[]{"암호"}));
        } catch (CRequiredException e) {
            return responseService.getFailResult( CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult( CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "로그인 사용자 암호 초기화", description = "로그인 사용자 암호 초기화 처리")
    @RequestMapping(value = "/initializePassword", method = {RequestMethod.POST})
    public CommonResult initializeUserPassword(
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            User user = userService.findUserByKey(userUid);
            if (user == null) {
                throw new CNotFoundException();
            }
            // 설정된 초기화 암호
            String initPwd = StringUtils.defaultString(appConfigService.findAppConfigByKey(appConfigService.DEFAULT_INIT_PWD), "000000");

            int updated = userService.modifyUserPassword(userUid, initPwd, reqUserUid);
            if (updated > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CNotFoundException e) {
            return responseService.getFailResult(
                    CNotFoundException.getCode(),
                    MessageUtil.getMessage(CNotFoundException.KEY_MSG, new String[]{"사용자 정보"}));
        } catch (CNotMatchException e) {
            return responseService.getFailResult(
                    CNotMatchException.getCode(),
                    MessageUtil.getMessage(CNotMatchException.KEY_MSG, new String[]{"암호"}));
        } catch (CRequiredException e) {
            return responseService.getFailResult( CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult( CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "로그인 계정 비활성화 처리", description = "로그인 계정을 비활성화 처리")
    @RequestMapping(value = "/inactive", method = {RequestMethod.POST})
    public DataResult<User> inactiveUser(
            @Parameter(description = "사용자정보", required = true) @RequestBody User user,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            user.setModUid(Long.valueOf(reqUserUid));
            user.setActive(AppContant.CommonValue.NO.getValue());
            User newUser = userService.modifyUser(user);
            return responseService.getDataResult(newUser);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }


    /** 사용자 그룹 관련 */
    @Operation(summary = "사용자 그룹 목록 조회", description = "사용자 그룹 목록 조회")
    @RequestMapping(value = "/group", method = {RequestMethod.POST})
    public ListResult<Group> findGroups(
            @Parameter(description = "조회조건") @RequestBody Group cond)
    {
        try {
            List<Group> groups = userService.findGroups(cond);
            return responseService.getListResult(groups);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 조회(단건)", description = "사용자 그룹 조회(단건)")
    @RequestMapping(value = "/group/{grpUid}", method = {RequestMethod.GET})
    public DataResult<Group> findGroups(
            @Parameter(description = "조회조건") @PathVariable long grpUid)
    {
        try {
            Group group = userService.findGroupByKey(Long.valueOf(grpUid));
            return responseService.getDataResult(group);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    /** 사용자 그룹 관련 */
    @Operation(summary = "사용자 그룹 목록 조회", description = "사용자 그룹 목록 조회")
    @RequestMapping(value = "/group/helper", method = {RequestMethod.POST})
    public ListResult<Map<String, String>> findGroupsForHelper(
            @Parameter(description = "조회조건") @RequestBody Group cond)
    {
        try {
            List<Map<String, String>> groups = userService.findGroupForHelper(cond);
            return responseService.getListResult(groups);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 등록", description = "사용자 그룹 등록 후 그룹 권한의 자동 등록 처리됨")
    @RequestMapping(value = "/group/registration", method = {RequestMethod.POST})
    public DataResult<Group> registrationGroup(
            @Parameter(description = "그룹정보", required = true) @RequestBody Group group,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            group.setRegUid(Long.valueOf(reqUserUid));
            Group newItem = userService.createGroups(group);
            if (newItem != null && newItem.getGrpUid().longValue() > 0) {
                // 사용자 그룹 권한 자동 등록 처리
                userService.apppendGroupAuthority(newItem.getGrpUid(), Long.valueOf(reqUserUid));
            }
            return responseService.getDataResult(newItem);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 수정", description = "사용자 그룹 수정 처리")
    @RequestMapping(value = "/group/modify", method = {RequestMethod.POST})
    public DataResult<Group> modifyGroup(
            @Parameter(description = "그룹정보", required = true) @RequestBody Group group,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            Group updatedItem = userService.modifyGroups(group);
            return responseService.getDataResult(updatedItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 삭제", description = "사용자 그룹 삭제처리 - 그룹멤버 삭제, 그룹 권한 삭제")
    @RequestMapping(value = "/group/remove", method = {RequestMethod.POST})
    public CommonResult removeGroup(
            @Parameter(description = "그룹정보", required = true) @RequestParam long grpUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = userService.removeGroups(Long.valueOf(grpUid));
            if (deleted == 0) {
                return responseService.getFailResult();
            }
            return responseService.getSuccessResult();
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    /** 그룹 멤버 */
    @Operation(summary = "사용자 그룹 멤버 조회", description = "사용자 그룹 멤버 목록 조회 - 필수 파라미터 grpUid")
    @RequestMapping(value = "/group/member/{grpUid}", method = {RequestMethod.POST})
    public ListResult<User> findGroupMembers(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid)
    {
        try {
            List<User> members = userService.findGroupMember(grpUid);
            return responseService.getListResult(members);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 비멤버 조회", description = "사용자 그룹 비멤버 목록 조회")
    @RequestMapping(value = "/group/member/findGroupOtherMembers/{grpUid}", method = {RequestMethod.GET})
    public ListResult<User> findGroupNonMembers(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid)
    {
        try {
            List<User> members = userService.findGroupOtherMember(grpUid);
            return responseService.getListResult(members);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 멤버 등록", description = "사용자 그룹 멤버 단건 등록")
    @RequestMapping(value = "/group/member/registration", method = {RequestMethod.POST})
    public DataResult<GroupMember> registrationGroupMember(
            @Parameter(description = "그룹 UID", required = true) @RequestParam Long grpUid,
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            GroupMember data = new GroupMember();
            data.setUserUid(userUid);
            userService.removeGroupMember(data);    // 이전 그룹 정보 삭제

            data.setGrpUid(grpUid);
            data.setRegUid(Long.valueOf(reqUserUid));
            GroupMember newItem = userService.createGroupMember(data);
            return responseService.getDataResult(newItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 멤버 일괄 등록", description = "사용자 그룹에 등록한 userUid를 멀티로 전송하여 한번에 등록처리")
    @RequestMapping(value = "/group/member/registration/batch/{grpUid}", method = {RequestMethod.POST})
    @Transactional
    public CommonResult registrationMultiGroupMember(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid,
            @Parameter(description = "사용자 UID 목록", required = true) @RequestBody List<Long> newUsers,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            if ( (newUsers == null) || (newUsers.isEmpty()) ) {
                return responseService.getFailResult();
            }
            int inserted = 0;

            for(Long userUid : newUsers) {
                GroupMember data = new GroupMember();
                data.setUserUid(userUid);
                userService.removeGroupMember(data);    // 이전 그룹 정보 삭제

                data.setGrpUid(grpUid);
                data.setRegUid(Long.valueOf(reqUserUid));
                GroupMember newItem = userService.createGroupMember(data);
                if (newItem != null) inserted++;
            }

            if (inserted == newUsers.size()) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), String.format("요청건수 : %d 건, 등록건수 : %d", newUsers.size(), inserted));
            }
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult( CInvalidArgumentException.getCode(), e.getMessage(), null);

        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 멤버 멀티 삭제", description = "사용자 그룹 멤버 일괄 삭제처리")
    @RequestMapping(value = "/group/member/remove/batch/{grpUid}", method = {RequestMethod.POST})
    public CommonResult removeGroupMember(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid,
            @Parameter(description = "사용자 UID", required = true) @RequestBody List<Long> targetUsers)
    {
        try {
            if ( (targetUsers == null) || (targetUsers.isEmpty()) ) {
                return responseService.getFailResult();
            }
            int totalDeleted = 0;

            for(Long userUid : targetUsers) {
                GroupMember cond = new GroupMember();
                cond.setGrpUid(grpUid);
                cond.setUserUid(userUid);
                totalDeleted = totalDeleted + userService.removeGroupMember(cond);
            }

            if (totalDeleted == targetUsers.size()) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), String.format("요청건수 : %d 건, 삭제건수 : %d", targetUsers.size(), totalDeleted));
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "사용자 그룹 권한 목록 조회", description = "로그인 사용자가 소속된 그룹의 접근 가능한 메뉴 목록을 조회")
    @RequestMapping(value = "/group/authority/findGroupAuthority/{grpUid}", method = {RequestMethod.GET})
    public ListResult<GroupAuthority> findGroupAuthorities(
            @Parameter(description = "사용자 그룹 UID") @PathVariable long grpUid)
    {
        try {
            List<GroupAuthority> authorities = userService.findGroupAuthorityWithMenu(Long.valueOf(grpUid), Long.valueOf(0));
            return responseService.getListResult(authorities);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 모든 권한 목록 조회", description = "사용자 그룹 권한을 메뉴 마스터 기준으로 조회")
    @RequestMapping(value = "/group/authority/findGroupAuthorityAll/{grpUid}", method = {RequestMethod.GET})
    public ListResult<GroupAuthority> findGroupAuthorityAll(
            @Parameter(description = "사용자 그룹 UID") @PathVariable long grpUid)
    {
        try {
            List<GroupAuthority> authorities = userService.findGroupAuthorityAllMenu(Long.valueOf(grpUid), Long.valueOf(0));
            return responseService.getListResult(authorities);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 권한 저장", description = "사용자 그룹 권한 저장처리")
    @RequestMapping(value = "/group/authority/registration", method = {RequestMethod.POST})
    public DataResult<GroupAuthority> registrationAuthority(
            @Parameter(description = "권한설정정보", required = true) @RequestBody GroupAuthority data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            GroupAuthority newItem = userService.saveGroupAuthority(data);
            return responseService.getDataResult(newItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 권한 수정", description = "사용자 그룹 권한 수정처리")
    @RequestMapping(value = "/group/authority/modify", method = {RequestMethod.POST})
    public DataResult<GroupAuthority> modifyAuthority(
            @Parameter(description = "권한설정정보", required = true) @RequestBody GroupAuthority data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            GroupAuthority newItem = userService.saveGroupAuthority(data);
            return responseService.getDataResult(newItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "사용자 그룹 권한 일괄등록", description = "사용자 그룹 권한에 등록되지 않은 메뉴권한의 일괄 등록처리")
    @RequestMapping(value = "/group/authority/execInitialize", method = {RequestMethod.POST})
    public ListResult<GroupAuthority> initializeAuthority(
            @Parameter(description = "사용자 그룹 UID", required = true) @RequestParam long grpUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            userService.apppendGroupAuthority(Long.valueOf(grpUid), Long.valueOf(reqUserUid));

            List<GroupAuthority> authorities = userService.findGroupAuthorityWithMenu(Long.valueOf(grpUid), Long.valueOf(0));
            return responseService.getListResult(authorities);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "사용자 그룹 권한 복사", description = "현재 사용자 그룹으로 권한을 복사처리, from source to target group")
    @RequestMapping(value = "/group/authority/copyFrom", method = {RequestMethod.POST})
    public ListResult<GroupAuthority> copyAuthorityFromSourceGroup(
            @Parameter(description = "소스 사용자 그룹 UID", required = true) @RequestParam Long sourceGrpUid,
            @Parameter(description = "대상 사용자 그룹 UID", required = true) @RequestParam Long targetGrpUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            userService.replaceGroupAuthority(sourceGrpUid, targetGrpUid, Long.valueOf(reqUserUid));

            List<GroupAuthority> authorities = userService.findGroupAuthorityAllMenu(targetGrpUid, Long.valueOf(0));
            return responseService.getListResult(authorities);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }
}
