package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.DishDto;
import com.it.reggie.entity.Dish;

/**
 * @author xwzStart
 * @create 2022-03-04 16:35
 */
public interface DishService extends IService<Dish> {

    //新增菜品,同时加入菜品对应的口味数据,需要操作两张表,dish 和 dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //根据id修改菜品信息
    void updateByIdWithFlavor(DishDto dishDto);

    void getById();
}
