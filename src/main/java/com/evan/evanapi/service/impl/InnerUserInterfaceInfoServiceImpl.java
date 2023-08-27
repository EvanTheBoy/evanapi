package com.evan.evanapi.service.impl;

import com.evan.evanapi.service.UserInterfaceInfoService;
import com.evan.evanapicommon.model.entity.UserInterfaceInfo;
import com.evan.evanapicommon.service.InnerUserInterfaceInfoService;

import javax.annotation.Resource;

public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, add);
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}
