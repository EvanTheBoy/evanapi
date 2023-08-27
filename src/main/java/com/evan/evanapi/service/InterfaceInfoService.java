package com.evan.evanapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.evan.evanapicommon.model.entity.InterfaceInfo;
import com.evan.evanapi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.evan.evanapi.model.vo.InterfaceInfoVO;
import javax.servlet.http.HttpServletRequest;

/**
* @author EvanTheBoy
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-07-07 21:30:23
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param interfaceInfo
     * @param request
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param interfaceInfoPage
     * @param request
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);
}
