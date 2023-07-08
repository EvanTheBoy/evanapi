package com.evan.evanapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evan.evanapi.model.entity.InterfaceInfo;

/**
* @author EvanTheBoy
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-07-07 21:30:23
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo info, boolean add);
}
