package com.evan.evanapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.evan.evanapi.annotation.AuthCheck;
import com.evan.evanapi.common.BaseResponse;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.common.ResultUtils;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.mapper.UserInterfaceInfoMapper;
import com.evan.evanapi.model.vo.InterfaceInfoVO;
import com.evan.evanapi.service.InterfaceInfoService;
import com.evan.evanapicommon.model.entity.InterfaceInfo;
import com.evan.evanapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @GetMapping("/top/invoked/interface")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokedInterface() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokedInterface(3);
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();

        // 根据interfaceInfoId得到一个map，简言之就是先把它给查出来，查出来放到一个list里面我们才能获取总调用次数即totalNum
        Map<Long, List<UserInterfaceInfo>> userInterfaceInfoMap = userInterfaceInfoList.stream().
                collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        queryWrapper.in("id", userInterfaceInfoMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        // 判断一下是否为空
        ThrowUtils.throwIf(CollectionUtils.isEmpty(list), ErrorCode.SYSTEM_ERROR);
        // 实现从interfaceInfo到interfaceInfoVO的转变
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            int totalNum = userInterfaceInfoMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList);
    }
}
