package com.evan.evanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.exception.BusinessException;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.mapper.UserInterfaceInfoMapper;
import com.evan.evanapi.service.UserInterfaceInfoService;
import com.evan.evanapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author EvanTheBoy
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2023-08-18 22:07:18
 */
@Service
@Slf4j
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验时，两个id都不能为空
        if (add) {
            ThrowUtils.throwIf(userInterfaceInfo.getId() <= 0 || userInterfaceInfo.getUserId() <= 0
                    || userInterfaceInfo.getInterfaceInfoId() <= 0, ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        ThrowUtils.throwIf(userInterfaceInfo.getLeftNum() < 0, ErrorCode.PARAMS_ERROR, "剩余调用次数不能小于0");
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断
        ThrowUtils.throwIf(interfaceInfoId <= 0 || userId <= 0, ErrorCode.PARAMS_ERROR);
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.gt("leftNum", 0); // leftNum要大于0
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }
}
