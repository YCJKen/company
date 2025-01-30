package com.wustl.company.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wustl.company.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PostMapper extends BaseMapper<Post> {
} 