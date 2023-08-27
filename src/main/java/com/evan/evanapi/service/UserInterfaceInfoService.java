package com.evan.evanapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evan.evanapicommon.model.entity.UserInterfaceInfo;


/**
* @author EvanTheBoy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-08-18 22:07:18
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    boolean invokeCount(long interfaceInfoId, long userId);
}
