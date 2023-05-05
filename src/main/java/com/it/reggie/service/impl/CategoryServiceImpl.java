package com.it.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.CustomException;
import com.it.reggie.entity.Category;
import com.it.reggie.entity.Dish;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.mapper.CategoryMapper;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.DishService;
import com.it.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xwzStart
 * @create 2022-03-04 16:34
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private DishService dishService;

    @Override
    public void remove(Long id) {
        //查看当前分类是否关联了菜品
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        /*
        SELECT * from setmeal where category_id = 1499670719116701697

        List<Setmeal> list = setmealService.list(setmealWrapper);
        if(!list.isEmpty()){
            throw new CustomException("关联了相关分类,不能删除");
        }
        */
        // SELECT count(*) from setmeal where category_id = 1499670719116701697
        int setmealCount = setmealService.count(setmealWrapper);
        if(setmealCount > 0){
            throw new CustomException("该分类关联了相关菜品,不能删除的呦~");
        }
        //查看当前分类是否关联了套餐
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishWrapper);
        if(dishCount > 0){
            throw new CustomException("该分类关联了相关套餐,不能删除的呦~");
        }

        //删除
        super.removeById(id);
    }

}
