package com.feng.boot.admin.project.system.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.feng.boot.admin.project.system.permission.model.dto.PermissionDTO;
import com.feng.boot.admin.project.system.permission.model.entity.PermissionEntity;
import com.feng.boot.admin.project.system.permission.model.query.PermissionParams;
import com.feng.boot.admin.commons.utils.QueryWrapperUtils;
import com.feng.boot.admin.domain.service.impl.SuperBaseServiceImpl;
import com.feng.boot.admin.project.system.permission.mapper.IPermissionMapper;
import com.feng.boot.admin.project.system.permission.service.IPermissionService;
import com.feng.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 权限  服务实现类
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Service
public class PermissionServiceImpl extends SuperBaseServiceImpl<Long, PermissionParams, PermissionDTO, PermissionEntity, IPermissionMapper> implements IPermissionService {

    @Override
    @Nonnull
    public QueryWrapper<PermissionEntity> query(@Nonnull PermissionParams params) {
        QueryWrapper<PermissionEntity> query = QueryWrapperUtils.getQuery(params);
        if (Objects.nonNull(params.getMenuId())) {
            query.eq(PermissionEntity.MENU_ID, params.getMenuId());
        }
        if (StringUtils.isNotBlank(params.getName())) {
            query.like(PermissionEntity.NAME, params.getName());
        }
        if (StringUtils.isNotBlank(params.getPermission())) {
            query.eq(PermissionEntity.PERMISSION, params.getPermission());
        }
        return query;
    }

    @Override
    public List<PermissionEntity> findEnabledPermissionByIds(@Nonnull Collection<Long> ids) {
        Assert.notNull(ids, "权限id为空");
        LambdaQueryWrapper<PermissionEntity> queryWrapper = Wrappers.lambdaQuery(PermissionEntity.class)
                .in(PermissionEntity::getId, ids)
                .select(PermissionEntity::getId, PermissionEntity::getPermission);
        return super.list(queryWrapper);
    }
}
