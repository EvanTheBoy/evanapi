package com.evan.evanapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.exception.BusinessException;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.model.entity.InterfaceInfo;
import com.evan.evanapi.mapper.InterfaceInfoMapper;
import com.evan.evanapi.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author EvanTheBoy
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-07-07 21:30:23
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService{
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String description = interfaceInfo.getDescription();
        String name = interfaceInfo.getName();
        String method = interfaceInfo.getMethod();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String url = interfaceInfo.getUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description, method, requestHeader, responseHeader,
                    url), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名字过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }
}




