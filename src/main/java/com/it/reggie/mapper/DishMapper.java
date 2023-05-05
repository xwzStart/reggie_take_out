package com.it.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xwzStart
 * @create 2022-03-04 16:19
 */

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
