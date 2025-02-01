package com.wustl.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("Messages")
public class Message {
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;
    
    private Integer senderId;
    
    private Integer receiverId;
    
    private String content;
    
    private Integer status;  // 0-未读，1-已读
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(exist = false)
    private User sender;  // 发送者信息
} 