package com.evan.evanapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evan.evanapi.common.ErrorCode;
import com.evan.evanapi.constant.CommonConstant;
import com.evan.evanapi.exception.BusinessException;
import com.evan.evanapi.exception.ThrowUtils;
import com.evan.evanapi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.evan.evanapi.model.dto.interfaceInfo.InterfaceInfoEsDTO;
import com.evan.evanapi.model.entity.*;
import com.evan.evanapicommon.model.entity.User;
import com.evan.evanapi.mapper.InterfaceInfoMapper;
import com.evan.evanapi.model.vo.InterfaceInfoVO;
import com.evan.evanapi.model.vo.UserVO;
import com.evan.evanapi.service.InterfaceInfoService;
import com.evan.evanapi.service.UserService;
import com.evan.evanapi.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author EvanTheBoy
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-07-07 21:30:23
*/
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService{
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private UserService userService;

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
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 2000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        String description = interfaceInfoQueryRequest.getDescription();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String method = interfaceInfoQueryRequest.getMethod();
        String name = interfaceInfoQueryRequest.getName();
        String url = interfaceInfoQueryRequest.getUrl();
        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
        Long id = interfaceInfoQueryRequest.getId();
        Long userId = interfaceInfoQueryRequest.getUserId();
        Integer status = interfaceInfoQueryRequest.getStatus();
        // 拼接查询条件
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("name", description);
        }
        // 以下只需要模糊查询即可, 即不需要用户输入完整的内容, 后台自动实现匹配即可
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.like(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.like(StringUtils.isNotBlank(requestHeader), "requestHeader", requestHeader);
        queryWrapper.like(StringUtils.isNotBlank(responseHeader), "responseHeader", responseHeader);
        // 以下是必须用户输入完整的内容才可查询到的
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfo> searchFromEs(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        Long id = interfaceInfoQueryRequest.getId();
        String description = interfaceInfoQueryRequest.getDescription();
        String name = interfaceInfoQueryRequest.getName();
        String method = interfaceInfoQueryRequest.getMethod();
        String url = interfaceInfoQueryRequest.getUrl();
        String requestHeader = interfaceInfoQueryRequest.getRequestHeader();
        String responseHeader = interfaceInfoQueryRequest.getResponseHeader();
        Long userId = interfaceInfoQueryRequest.getUserId();
        // es 起始页为 0
        long current = interfaceInfoQueryRequest.getCurrent() - 1;
        long pageSize = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 按描述检索
        if (StringUtils.isNotBlank(description)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("name", description));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按名称检索
        if (StringUtils.isNotBlank(name)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("name", name));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按请求类型检索
        if (StringUtils.isNotBlank(method)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("method", method));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按请求地址检索
        if (StringUtils.isNotBlank(url)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("url", url));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按请求头检索
        if (StringUtils.isNotBlank(requestHeader)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("requestHeader", requestHeader));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按响应头检索
        if (StringUtils.isNotBlank(responseHeader)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("responseHeader", responseHeader));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<InterfaceInfoEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, InterfaceInfoEsDTO.class);
        Page<InterfaceInfo> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<InterfaceInfo> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<InterfaceInfoEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> interfaceInfoIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<InterfaceInfo> interfaceInfoList = baseMapper.selectBatchIds(interfaceInfoIdList);
            if (interfaceInfoList != null) {
                Map<Long, List<InterfaceInfo>> idInterfaceInfoMap = interfaceInfoList.stream()
                        .collect(Collectors.groupingBy(InterfaceInfo::getId));
                interfaceInfoIdList.forEach(interfaceInfoId -> {
                    if (idInterfaceInfoMap.containsKey(interfaceInfoId)) {
                        resourceList.add(idInterfaceInfoMap.get(interfaceInfoId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(interfaceInfoId), InterfaceInfoEsDTO.class);
                        log.info("delete interfaceInfo {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
        long interfaceInfoId = interfaceInfo.getId();
        // 1. 关联查询用户信息
        Long userId = interfaceInfo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        interfaceInfoVO.setUser(userVO);
        return interfaceInfoVO;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = interfaceInfoList.stream().map(InterfaceInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
            Long userId = interfaceInfo.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            interfaceInfoVO.setUser(userService.getUserVO(user));
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }
}
