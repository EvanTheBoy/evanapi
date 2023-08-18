package com.evan.evanapi.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evan.evanapi.annotation.AuthCheck;
import com.evan.evanapi.common.*;
import com.evan.evanapi.constant.UserConstant;
import com.evan.evanapi.exception.BusinessException;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.evan.evanapi.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import com.evan.evanapi.model.dto.userInterfaceInfo.UserInterfaceInfoInvokeRequest;
import com.evan.evanapi.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.evan.evanapi.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import com.evan.evanapi.model.entity.UserInterfaceInfo;
import com.evan.evanapi.model.entity.UserInterfaceInfo;
import com.evan.evanapi.model.entity.User;
import com.evan.evanapi.model.enums.UserInterfaceInfoStatusEnum;
import com.evan.evanapi.model.vo.UserInterfaceInfoVO;
import com.evan.evanapi.service.UserInterfaceInfoService;
import com.evan.evanapi.service.UserService;
import com.evan.evanapiclientsdkv2.client.EvanApiClient;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserUserInterfaceInfoController {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private EvanApiClient evanApiClient;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserInterfaceInfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 上线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineUserInterfaceInfo(@RequestBody IdRequest idRequest) {
        // 判断用户id是否合法
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断用户是否存在
        long id = idRequest.getId();
        // 根据id从数据库中查找
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 判断接口是否可以被正常调用
        com.evan.evanapiclientsdkv2.model.User cUser = new com.evan.evanapiclientsdkv2.model.User();
        cUser.setUsername("evan");
        String username = evanApiClient.getUsernameByPost(cUser);
        ThrowUtils.throwIf(StringUtils.isBlank(username), ErrorCode.SYSTEM_ERROR, "接口调用异常");
        // 更新接口信息
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(id);
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineUserInterfaceInfo(@RequestBody IdRequest idRequest) {
        // 判断用户id是否合法
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断用户是否存在
        long id = idRequest.getId();
        // 根据id从数据库中查找
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新接口信息
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(id);
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 测试调用
     *
     * @param userInterfaceInfoInvokeRequest
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeUserInterfaceInfo(@RequestBody UserInterfaceInfoInvokeRequest userInterfaceInfoInvokeRequest
            , HttpServletRequest request) {
        // 判断用户id是否合法
        if (userInterfaceInfoInvokeRequest == null || userInterfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断用户是否存在
        long id = userInterfaceInfoInvokeRequest.getId();
        // 根据id从数据库中查找并校验其相关信息
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(oldUserInterfaceInfo.getStatus() != UserInterfaceInfoStatusEnum.ONLINE.getValue(),
                ErrorCode.PARAMS_ERROR, "接口已关闭");
        // 获取用户的请求参数
        String userParams = userInterfaceInfoInvokeRequest.getUserRequestParams();
        // 获取登录用户的两个key
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        EvanApiClient tempClient = new EvanApiClient(accessKey, secretKey);
        // 将用户的请求参数映射成json数据
        com.evan.evanapiclientsdkv2.model.User user = GSON.fromJson(userParams, com.evan.evanapiclientsdkv2.model.User.class);
        String username = tempClient.getUsernameByPost(user);
        return ResultUtils.success(username);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserInterfaceInfoVO> getUserInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVO(userInterfaceInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserInterfaceInfoVO>> listUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
            HttpServletRequest request) {
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<UserInterfaceInfoVO>> listMyUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage, request));
    }
    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<UserInterfaceInfoVO>> searchUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
            HttpServletRequest request) {
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.searchFromEs(userInterfaceInfoQueryRequest);
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage, request));
    }
}
