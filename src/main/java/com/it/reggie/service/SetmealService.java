package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author xwzStart
 * @create 2022-03-04 20:31
 */
public interface SetmealService extends IService<Setmeal> {


    void updateWithDish(SetmealDto setmealDto);

    //删除套餐
    void delWithDish(List<Long> ids);

    //添加套餐
    void saveWithDish(SetmealDto setmealDto);

    //根据id查询套餐
    SetmealDto getByIWithDish(Long id);
}
