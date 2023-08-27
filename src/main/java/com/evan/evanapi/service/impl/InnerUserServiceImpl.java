package com.evan.evanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.mapper.UserMapper;
import com.evan.evanapicommon.model.entity.User;
import com.evan.evanapicommon.service.InnerUserService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public User getInvokedUser(String accessKey) {
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(accessKey), ErrorCode.PARAMS_ERROR);
        // 操作
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
