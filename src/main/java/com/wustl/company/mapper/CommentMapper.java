package com.wustl.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wustl.company.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommentMapper extends BaseMapper<PostComment> {
} 