package com.evan.evanapi.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基本id请求类型
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}