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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final PostImageMapper postImageMapper;
    private final PostLikeMapper postLikeMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Override
    @Transactional
    public Post createPost(PostCreateDTO createDTO, Integer userId) {
        log.info("Creating post with content: {} and images: {}", createDTO.getContent(), createDTO.getImageUrls());
        
        // 创建帖子
        Post post = Post.builder()
                .userId(userId)
                .content(createDTO.getContent())
                .likesCount(0)
                .commentsCount(0)
                .status(1)  // 设置为正常状态
                .build();
        
        // 先保存帖子，获取帖子ID
        postMapper.insert(post);
        
        // 保存图片到数据库
        List<String> savedImageUrls = new ArrayList<>();
        if (createDTO.getImageUrls() != null && !createDTO.getImageUrls().isEmpty()) {
            log.info("Processing {} images", createDTO.getImageUrls().size());
            
            for (int i = 0; i < createDTO.getImageUrls().size(); i++) {
                String imageUrl = createDTO.getImageUrls().get(i);
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    // 移除 URL 中的域名部分，只保存相对路径
                    String relativeUrl = imageUrl;
                    if (imageUrl.contains("localhost:8080")) {
                        relativeUrl = imageUrl.substring(imageUrl.indexOf("/uploads"));
                    }
                    
                    log.info("Saving image with URL: {}", relativeUrl);
                    
                    PostImage image = PostImage.builder()
                            .postId(post.getPostId())
                            .imageUrl(relativeUrl)
                            .imageOrder(i)
                            .build();
                    
                    postImageMapper.insert(image);
                    savedImageUrls.add(relativeUrl);
                }
            }
        }
        
        // 设置发帖用户信息
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            post.setUser(user);
        }
        
        // 设置已保存的图片URL列表
        post.setImageUrls(savedImageUrls);
        log.info("Post created with ID: {} and {} images", post.getPostId(), savedImageUrls.size());
        
        return post;
    }
    
    @Override
    public IPage<Post> getPosts(Integer userId, Integer pageNum, Integer pageSize) {
        // 分页查询帖子
        Page<Post> page = new Page<>(pageNum, pageSize);
        IPage<Post> postPage = postMapper.selectPage(page, new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreatedAt));
        
        // 填充每个帖子的附加信息
        postPage.getRecords().forEach(post -> {
            // 查询帖子图片
            List<PostImage> images = postImageMapper.selectList(
                new LambdaQueryWrapper<PostImage>()
                    .eq(PostImage::getPostId, post.getPostId())
                    .orderByAsc(PostImage::getImageOrder)
            );
            
            // 设置图片URL列表
            post.setImageUrls(images.stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList()));
            
            // 设置发帖用户信息
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                user.setPassword(null);
                post.setUser(user);
            }
            
            // 查询当前用户是否点赞
            if (userId != null) {
                PostLike like = postLikeMapper.selectOne(
                    new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getPostId, post.getPostId())
                        .eq(PostLike::getUserId, userId)
                );
                post.setIsLiked(like != null);
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