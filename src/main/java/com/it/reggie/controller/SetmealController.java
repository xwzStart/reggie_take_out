package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;


import com.it.reggie.dto.SetmealDto;
import com.it.reggie.entity.Category;
import com.it.reggie.entity.Setmeal;
import com.it.reggie.service.CategoryService;
import com.it.reggie.service.SetmealService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xwzStart
 * @create 2022-03-07 19:28
 *
 * http://localhost:9091/doc.html  查看接口文档
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    @CacheEvict(value ="setmealCache",allEntries = true)
    @ApiOperation(value = "新增套餐接口")
    public R<String> save(@RequestBody SetmealDto SetmealDto){
        setmealService.saveWithDish(SetmealDto);
        return R.success("新增成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页接口")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页显示条数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })*/
    public R<Page> getByPage(int page, int pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        //构造分页构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null,Setmeal::getName,name);

        setmealService.page(setmealPage,wrapper);

        Page<SetmealDto> SetmealDto = new Page<>();
        //拷贝
        BeanUtils.copyProperties(setmealPage,SetmealDto,"records");

        List<Setmeal> setmeals = setmealPage.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();

        for (Setmeal setmeal : setmeals) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String idName = category.getName();
            setmealDto.setCategoryName(idName);
            setmealDtos.add(setmealDto);
        }
        SetmealDto.setRecords(setmealDtos);

        return R.success(SetmealDto);

    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */

    @DeleteMapping
    @ApiOperation(value = "删除套餐接口")
    public R<String> delSetmeal(@RequestParam List<Long> ids){
        setmealService.delWithDish(ids);
        return R.success("删除成功~");

    }

    /**
     * 根据条件查询套餐数据
     * 注解Cacheable,先从redis查询数据,没有的话再去数据库查
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache" ,key = "#setmeal.categoryId +'_' +#setmeal.status")
    @ApiOperation(value = "条件查询套餐数据")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("id:{}",id);
        SetmealDto byIWithDish = setmealService.getByIWithDish(id);
        return R.success(byIWithDish);
    }

    /**
     * 根据id修改套餐信息
     * CacheEvict注解,清理缓存---allEntries = true意思是清理当前分类下的所有缓存数据
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value ="setmealCache",allEntries = true)
    public R<String> updateSetmeal(@RequestBody  SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }

    //修改状态
    @PutMapping("/status/{index}")
    public R<String> updateStatus(@PathVariable int index,@RequestParam List<Long> ids){
        ids.stream().map((id)->{
            Setmeal setmealId = setmealService.getById(id);
            if(setmealId.getStatus() != index){
                setmealId.setStatus(index);
            }
            setmealService.updateById(setmealId);

            return setmealId;
        }).collect(Collectors.toList());



        return R.success("修改成功");
    }

}
