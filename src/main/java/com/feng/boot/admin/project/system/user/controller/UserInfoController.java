package com.feng.boot.admin.project.system.user.controller;


import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.Log;
import com.feng.boot.admin.annotation.PreAuth;
import com.feng.boot.admin.exceptions.BusinessException;
import com.feng.boot.admin.security.model.User;
import com.feng.boot.admin.security.utils.SecurityUtils;
import com.feng.boot.admin.commons.enums.ResponseStatusEnum;
import com.feng.boot.admin.domain.controller.SuperSimpleBaseController;
import com.feng.boot.admin.domain.result.Result;
import com.feng.boot.admin.domain.result.R;
import com.feng.boot.admin.project.system.user.model.dto.UserInfoDTO;
import com.feng.boot.admin.project.system.user.model.entity.UserInfoEntity;
import com.feng.boot.admin.project.system.user.model.query.UserInfoParams;
import com.feng.boot.admin.project.system.user.model.vo.UserAccount;
import com.feng.boot.admin.project.system.user.service.IUserAccountService;
import com.feng.boot.admin.project.system.user.service.IUserInfoService;
import com.feng.boot.admin.project.system.user.service.impl.UserInfoServiceImpl;
import com.feng.commons.json.exceptions.JsonException;
import com.feng.commons.lang.StringUtils;
import com.feng.commons.spring.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息  前端控制器
 *
 * @author bing_huang
 * @since 3.0.0
 */
@RestController
@RequestMapping("/api/v3/system/user/info")
@PreAuth("user")
@ClassDescribe("用户管理")
public class UserInfoController extends SuperSimpleBaseController<Long, UserInfoDTO, UserInfoParams, UserInfoEntity> {
    private final IUserInfoService service;

    public UserInfoController(IUserInfoService service) {
        super(service);
        this.service = service;
    }

    /**
     * 获取用户详情
     *
     * @param id 用户id
     * @return 用户详情(不包含相关信息)
     */
    @GetMapping("/{id}")
    public Result<UserInfoDTO> getUserInfoById(@PathVariable("id") Long id) {
        UserInfoEntity entity = service.getById(id);
        UserInfoDTO info = BeanUtils.transformFrom(entity, UserInfoDTO.class);
        return R.success(info);
    }

    /**
     * 获取当前请求认证的用户
     *
     * @param request 请求
     * @return 用户详情(不包含相关信息)
     */
    @GetMapping
    public Result<UserInfoDTO> getCurrentInfo(HttpServletRequest request) {
        User user = SecurityUtils.getCurrentUser();
        UserInfoDTO info = BeanUtils.transformFrom(user, UserInfoDTO.class);
        return R.success(info);
    }

    /**
     * 修改密码
     *
     * @param id      用户id
     * @param account 账号信息
     * @return 是否成功
     */
    @PostMapping("/update/password/{id}")
    public Result<String> updatePassword(@PathVariable("id") Long id, @RequestBody @Validated UserAccount account) {
        String newPassword = account.getNewPassword();
        String newPassword2 = account.getNewPassword2();
        if (!StringUtils.equals(newPassword, newPassword2)) {
            R.result(ResponseStatusEnum.USER_PASSWORD_V_FAIL, "确认密码错误");
        }
        IUserAccountService accountService = ((UserInfoServiceImpl) service.getThis()).getAccountService();
        accountService.updatePassword(id, account.getOldPassword(), newPassword);
        return R.success("修改成功");
    }

    /**
     * 重置密码
     *
     * @param id 用户id
     * @return 是否成功
     */
    @GetMapping("/rest/password/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR','user;rest:password')")
    @Log(value = "重置密码")
    public Result<String> restPassword(@PathVariable("id") Long id) throws JsonException {
        UserInfoEntity entity = service.getById(id);
        if (entity.getIsAdmin() == 1) {
            throw new BusinessException("超级管理员无法重置");
        }
        service.restPassword(id);
        return R.success("重置成功");

    }
}

