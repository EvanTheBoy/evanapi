package com.evan.evanapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.mapper.InterfaceInfoMapper;
import com.evan.evanapicommon.model.entity.InterfaceInfo;
import com.evan.evanapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(url) || StringUtils.isBlank(method), ErrorCode.PARAMS_ERROR);
        // 操作
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
