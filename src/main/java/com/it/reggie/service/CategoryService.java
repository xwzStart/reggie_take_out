package com.it.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.reggie.entity.Category;

/**
 * @author xwzStart
 * @create 2022-03-04 16:34
 */
public interface CategoryService extends IService<Category> {

    //根据id删除分类
    void remove(Long id);
}
