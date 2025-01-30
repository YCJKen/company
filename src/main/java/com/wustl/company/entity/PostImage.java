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
@TableName("PostImages")
public class PostImage {
    @TableId(value = "image_id", type = IdType.AUTO)
    private Long imageId;
    
    private Long postId;
    
    private String imageUrl;
    
    private Integer imageOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 