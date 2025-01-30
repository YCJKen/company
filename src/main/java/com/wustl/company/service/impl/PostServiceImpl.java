package com.wustl.company.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wustl.company.dto.PostCreateDTO;
import com.wustl.company.entity.Post;
import com.wustl.company.entity.PostImage;
import com.wustl.company.entity.PostLike;
import com.wustl.company.entity.User;
import com.wustl.company.entity.PostComment;
import com.wustl.company.dto.CommentCreateDTO;
import com.wustl.company.exception.BusinessException;
import com.wustl.company.mapper.PostMapper;
import com.wustl.company.mapper.PostImageMapper;
import com.wustl.company.mapper.PostLikeMapper;
import com.wustl.company.mapper.UserMapper;
import com.wustl.company.mapper.CommentMapper;
import com.wustl.company.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final PostLikeMapper postLikeMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    
    @Override
    @Transactional
    public Post createPost(PostCreateDTO createDTO, Integer userId) {
        // 创建帖子
        Post post = Post.builder()
                .userId(userId)
                .content(createDTO.getContent())
                .likesCount(0)
                .commentsCount(0)
                .status(1)
                .build();
        
        postMapper.insert(post);
        
        // 保存图片
        if (createDTO.getImageUrls() != null && !createDTO.getImageUrls().isEmpty()) {
            IntStream.range(0, createDTO.getImageUrls().size())
                    .forEach(i -> {
                        PostImage image = PostImage.builder()
                                .postId(post.getPostId())
                                .imageUrl(createDTO.getImageUrls().get(i))
                                .imageOrder(i)
                                .build();
                        postImageMapper.insert(image);
                    });
            post.setImageUrls(createDTO.getImageUrls());
        }
        
        return post;
    }
    
    @Override
    public IPage<Post> getPosts(Integer userId, Integer pageNum, Integer pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        
        // 获取帖子列表
        IPage<Post> postPage = postMapper.selectPage(page, new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreatedAt));
        
        // 填充每个帖子的图片、点赞状态和用户信息
        postPage.getRecords().forEach(post -> {
            // 获取图片
            List<String> imageUrls = postImageMapper.selectList(new LambdaQueryWrapper<PostImage>()
                    .eq(PostImage::getPostId, post.getPostId())
                    .orderByAsc(PostImage::getImageOrder))
                    .stream()
                    .map(PostImage::getImageUrl)
                    .toList();
            post.setImageUrls(imageUrls);
            
            // 检查当前用户是否点赞
            PostLike like = postLikeMapper.selectOne(new LambdaQueryWrapper<PostLike>()
                    .eq(PostLike::getPostId, post.getPostId())
                    .eq(PostLike::getUserId, userId));
            post.setIsLiked(like != null);
            
            // 获取发布者信息
            User postUser = userMapper.selectById(post.getUserId());
            if (postUser != null) {
                postUser.setPassword(null);  // 清除敏感信息
                post.setUser(postUser);
            }
        });
        
        return postPage;
    }
    
    @Override
    @Transactional
    public void likePost(Long postId, Integer userId) {
        // 检查是否已点赞
        if (postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId)) > 0) {
            throw new BusinessException("已经点赞过了");
        }
        
        // 创建点赞记录
        PostLike like = PostLike.builder()
                .postId(postId)
                .userId(userId)
                .build();
        postLikeMapper.insert(like);
        
        // 更新帖子点赞数
        Post post = postMapper.selectById(postId);
        post.setLikesCount(post.getLikesCount() + 1);
        postMapper.updateById(post);
    }
    
    @Override
    @Transactional
    public void unlikePost(Long postId, Integer userId) {
        // 删除点赞记录
        int count = postLikeMapper.delete(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId));
        
        if (count > 0) {
            // 更新帖子点赞数
            Post post = postMapper.selectById(postId);
            post.setLikesCount(post.getLikesCount() - 1);
            postMapper.updateById(post);
        }
    }
    
    @Override
    public void deletePost(Long postId, Integer userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该帖子");
        }
        
        post.setStatus(0);  // 软删除
        postMapper.updateById(post);
    }

    @Override
    public List<PostComment> getComments(Long postId, Integer userId) {
        List<PostComment> comments = commentMapper.selectList(new LambdaQueryWrapper<PostComment>()
                .eq(PostComment::getPostId, postId)
                .eq(PostComment::getStatus, 1)
                .orderByDesc(PostComment::getCreatedAt));
        
        // 填充用户信息
        comments.forEach(comment -> {
            User commentUser = userMapper.selectById(comment.getUserId());
            if (commentUser != null) {
                commentUser.setPassword(null);
                comment.setUser(commentUser);
            }
        });
        
        return comments;
    }

    @Override
    @Transactional
    public PostComment createComment(CommentCreateDTO createDTO, Integer userId) {
        PostComment comment = PostComment.builder()
                .postId(createDTO.getPostId())
                .userId(userId)
                .content(createDTO.getContent())
                .parentId(createDTO.getParentId())
                .likesCount(0)
                .status(1)
                .build();
        
        commentMapper.insert(comment);
        
        // 更新帖子评论数
        Post post = postMapper.selectById(createDTO.getPostId());
        post.setCommentsCount(post.getCommentsCount() + 1);
        postMapper.updateById(post);
        
        // 设置用户信息
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            comment.setUser(user);
        }
        
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Integer userId) {
        PostComment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }
        
        comment.setStatus(0);
        commentMapper.updateById(comment);
        
        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        post.setCommentsCount(post.getCommentsCount() - 1);
        postMapper.updateById(post);
    }
} 