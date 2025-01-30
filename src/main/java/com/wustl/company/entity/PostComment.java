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
@TableName("PostComments")
public class PostComment {
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;
    
    private Long postId;
    
    private Integer userId;
    
    private String content;
    
    private Long parentId;
    
    private Integer likesCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private User user;  // 评论用户信息
    
    @TableField(exist = false)
    private Boolean isLiked;  // 当前用户是否点赞
} 