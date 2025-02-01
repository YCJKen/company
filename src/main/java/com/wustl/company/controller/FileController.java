package com.wustl.company.controller;

import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.base-url}")
    private String baseUrl;
    
    @PostMapping
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Results.error("文件为空");
        }
        
        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    return Results.error("创建上传目录失败");
                }
            }
            
            // 生成新的文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File dest = new File(uploadDir, newFilename);
            file.transferTo(dest);
            
            // 返回相对路径
            return Results.success("/uploads/" + newFilename);
        } catch (IOException e) {
            return Results.error("文件上传失败");
        }
    }
} 