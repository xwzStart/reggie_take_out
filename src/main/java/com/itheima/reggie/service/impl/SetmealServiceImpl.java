package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xwzStart
 * @create 2022-03-04 20:31
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 删除套餐
     * @param ids
     *
     * 先判断套餐是否为启用状态
     *
     * 删除套餐
     * 删除套餐相关菜品数据
     * ---操作了两张表,所以要开启事务---
     */
    @Override
    @Transactional
    public void delWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        //调用父类中的统计方法
        int count = this.count(setmealWrapper);
        //如果count大于0,说明含有正在售卖的套餐,则不能删除
        if(count > 0){
            throw new CustomException("含有正在售卖的套餐,您不能删除呦~");
        }
        //可以删除,根据页面传过来的ids删除
        setmealService.removeByIds(ids);

        //删除相关菜品数据
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.in(SetmealDish::getDishId,ids);
        this.remove(setmealWrapper);

        setmealDishService.remove(setmealDishWrapper);

    }

    /**
     * 添加套餐
     * @param setmealDto
     */

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存菜品信息
        this.save(setmealDto);

        //获取菜品信息id
        Long setmealDtoId = setmealDto.getId();

        //保存口味
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDtoId);
        }

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmealId = this.getById(id);

        //拷贝到dto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmealId,setmealDto);

        //查询套餐
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐基本信息
        this.updateById(setmealDto);

        //获取套餐id
        Long setmealDtoId = setmealDto.getId();

        //根据id删除套餐中的菜品信息
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(SetmealDish::getDishId,setmealDtoId);
        setmealDishService.remove(wrapper);

        //添加提交过来的套餐
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((setmealDishe)->{
            setmealDishe.setSetmealId(setmealDtoId);

            return setmealDishe;
        }).collect(Collectors.toList());

        //保存
        setmealDishService.saveBatch(setmealDishes);
    }
}
