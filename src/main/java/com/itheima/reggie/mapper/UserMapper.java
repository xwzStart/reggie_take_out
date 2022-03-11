package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xwzStart
 * @create 2022-03-09 15:56
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
