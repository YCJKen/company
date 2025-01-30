package com.wustl.company.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wustl.company.dto.PostCreateDTO;
import com.wustl.company.entity.Post;
import com.wustl.company.entity.PostComment;
import com.wustl.company.dto.CommentCreateDTO;

import java.util.List;

public interface PostService {
    Post createPost(PostCreateDTO createDTO, Integer userId);
    IPage<Post> getPosts(Integer userId, Integer pageNum, Integer pageSize);
    void likePost(Long postId, Integer userId);
    void unlikePost(Long postId, Integer userId);
    void deletePost(Long postId, Integer userId);
    List<PostComment> getComments(Long postId, Integer userId);
    PostComment createComment(CommentCreateDTO createDTO, Integer userId);
    void deleteComment(Long commentId, Integer userId);
} 