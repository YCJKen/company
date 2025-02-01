package com.wustl.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTO {
    @NotNull(message = "接收者ID不能为空")
    private Integer receiverId;
    
    @NotBlank(message = "消息内容不能为空")
    private String content;
} 