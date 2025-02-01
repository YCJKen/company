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
@TableName("UserFriends")
public class UserFriend {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Integer userId;
    
    private Integer friendId;
    
    private Integer status;  // 0-待确认，1-已确认，2-已拒绝
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private User friend;  // 好友信息
} 