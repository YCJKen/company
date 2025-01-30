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
@TableName("PostLikes")
public class PostLike {
    @TableId(value = "like_id", type = IdType.AUTO)
    private Long likeId;
    
    private Long postId;
    
    private Integer userId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 