package com.evan.evanapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evan.evanapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author EvanTheBoy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-08-18 22:07:18
* @Entity com.evan.evanapi.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<UserInterfaceInfo> listTopInvokedInterface(int limit);
}




