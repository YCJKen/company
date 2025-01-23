package com.wustl.company.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;      // 状态码
    private String message;    // 提示信息
    private T data;           // 数据体
} 