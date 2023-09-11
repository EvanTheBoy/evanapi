package com.evan.evanapi.model.vo;

import com.evan.evanapicommon.model.entity.InterfaceInfo;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class InterfaceInfoVO extends InterfaceInfo implements Serializable {
    private final static Gson GSON = new Gson();
    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 包装类转对象
     *
     * @param interfaceInfoVO
     * @return
     */
    public static InterfaceInfo voToObj(InterfaceInfoVO interfaceInfoVO) {
        if (interfaceInfoVO == null) {
            return null;
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoVO, interfaceInfo);
        return interfaceInfo;
    }

    /**
     * 对象转包装类
     *
     * @param interfaceInfo
     * @return
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
}
