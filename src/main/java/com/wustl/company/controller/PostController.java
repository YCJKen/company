package com.wustl.company.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wustl.company.common.Result;
import com.wustl.company.common.Results;
import com.wustl.company.dto.PostCreateDTO;
import com.wustl.company.entity.Post;
import com.wustl.company.entity.PostComment;
import com.wustl.company.dto.CommentCreateDTO;
import com.wustl.company.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    
    @PostMapping
    public Result<Post> createPost(@Valid @RequestBody PostCreateDTO createDTO, 
                                 HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        Post post = postService.createPost(createDTO, userId);
        return Results.success(post);
    }
    
    @GetMapping
    public Result<IPage<Post>> getPosts(HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer userId = (Integer) request.getAttribute("userId");
        IPage<Post> posts = postService.getPosts(userId, pageNum, pageSize);
        return Results.success(posts);
    }
    
    @PostMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        postService.likePost(postId, userId);
        return Results.success();
    }
    
    @DeleteMapping("/{postId}/like")
    public Result<Void> unlikePost(@PathVariable Long postId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        postService.unlikePost(postId, userId);
        return Results.success();
    }
    
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        postService.deletePost(postId, userId);
        return Results.success();
    }
    
    @GetMapping("/{postId}/comments")
    public Result<List<PostComment>> getComments(@PathVariable Long postId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<PostComment> comments = postService.getComments(postId, userId);
        return Results.success(comments);
    }
    
    @PostMapping("/comments")
    public Result<PostComment> createComment(@Valid @RequestBody CommentCreateDTO createDTO, 
                                      HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        PostComment comment = postService.createComment(createDTO, userId);
        return Results.success(comment);
    }
    
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        postService.deleteComment(commentId, userId);
        return Results.success();
    }
} 