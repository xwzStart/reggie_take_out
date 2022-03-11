package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author xwzStart
 * @create 2022-03-04 20:31
 */
public interface SetmealService extends IService<Setmeal> {

    //删除套餐
    void delWithDish(List<Long> ids);

    //添加套餐
    void saveWithDish(SetmealDto setmealDto);
}
