package com.wustl.company.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
public class PostCreateDTO {
    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "内容长度不能超过1000字")
    private String content;
    
    private List<String> imageUrls;
} 