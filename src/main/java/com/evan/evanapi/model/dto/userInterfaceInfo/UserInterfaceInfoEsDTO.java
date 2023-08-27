package com.evan.evanapi.model.dto.userInterfaceInfo;


import com.evan.evanapicommon.model.entity.UserInterfaceInfo;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInterfaceInfoEsDTO implements Serializable {
    //TODO
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 主键
     */
    private Long id;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    private static final Gson GSON = new Gson();

    /**
     * 对象转包装类
     *
     * @param userInterfaceInfo
     * @return
     */
    public static UserInterfaceInfoEsDTO objToDto(UserInterfaceInfo userInterfaceInfo) {
        if (userInterfaceInfo == null) {
            return null;
        }
        UserInterfaceInfoEsDTO userInterfaceInfoEsDTO = new UserInterfaceInfoEsDTO();
        BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoEsDTO);
        return userInterfaceInfoEsDTO;
    }

    /**
     * 包装类转对象
     *
     * @param userInterfaceInfoEsDTO
     * @return
     */
    public static UserInterfaceInfo dtoToObj(UserInterfaceInfoEsDTO userInterfaceInfoEsDTO) {
        if (userInterfaceInfoEsDTO == null) {
            return null;
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoEsDTO, userInterfaceInfo);
        return userInterfaceInfo;
    }
}
