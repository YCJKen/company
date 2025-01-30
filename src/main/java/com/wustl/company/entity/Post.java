package com.wustl.company.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("Posts")
public class Post {
    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;
    
    private Integer userId;
    
    private String content;
    
    private Integer likesCount;
    
    private Integer commentsCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private List<String> imageUrls;  // 图片URL列表
    
    @TableField(exist = false)
    private User user;  // 发帖用户信息
    
    @TableField(exist = false)
    private Boolean isLiked;  // 当前用户是否点赞
} 