package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xwzStart
 * @create 2022-03-05 15:42
 *
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> add(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        wrapper.orderByAsc(Dish::getSort);
        //执行分页查询
        dishService.page(pageInfo, wrapper);

        Page<DishDto> dishDtoPage = new Page<>();
        //将dish数据拷贝到dishDto中,除了records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //创建dish和dishDto两个集合,用于拷贝records
        List<Dish> dishs = pageInfo.getRecords();
        List<DishDto> dishDtos = new ArrayList<>();

        //遍历dishs,获取其中每一个dish
        for (Dish dish : dishs) {
            DishDto dishDto = new DishDto();
            //将dish对象的属性拷贝到dishDto
            BeanUtils.copyProperties(dish,dishDto);

            //获取dishDto的getCategoryId
            Long dishDtoId = dishDto.getCategoryId();

            //因为dish表中含有categoryId,调用categoryService,得到对应的category表中的id
            Category categoryID = categoryService.getById(dishDtoId);

            //根据categoryID获取对应的分类名字
            String categoryIDName = categoryID.getName();
            dishDto.setCategoryName(categoryIDName);
            dishDtos.add(dishDto);
        }
        //将dishDtos封装到 Page<DishDto>
        dishDtoPage.setRecords(dishDtos);


        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateByIdWithFlavor(dishDto);

        return R.success("更新成功");
    }

    /**
     * 批量修改状态
     * @param index
     * @param ids
     * @return
     */
    @PutMapping("/status/{index}")
    public R<String> updateStatus(@PathVariable int index,@RequestParam List<Long> ids){
        //stream流遍历所有id,批量修改状态
        ids.stream().map((id)->{
            //获取菜品id
            Dish dishId = dishService.getById(id);
            if(dishId.getStatus() != index){
                dishId.setStatus(index);
            }
            dishService.updateById(dishId);
            return dishId;

        }).collect(Collectors.toList());
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delDish(@RequestParam List<Long> ids){
        dishService.removeByIds(ids);
        return R.success("删除成功");
    }




    /**
     * 根据条件查询对应的菜品信息
     * @param dish
     * @return
     */
    @GetMapping("/list")
   /* private R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        wrapper.eq(Dish::getStatus,1);

        //排序
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }*/
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
