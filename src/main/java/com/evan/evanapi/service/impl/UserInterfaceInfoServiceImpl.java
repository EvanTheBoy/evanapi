package com.evan.evanapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evan.evanapi.model.entity.UserInterfaceInfo;
import com.evan.evanapi.service.UserInterfaceInfoService;
import com.evan.evanapi.mapper.UserInterfaceInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author EvanTheBoy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-08-18 22:07:18
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

}




