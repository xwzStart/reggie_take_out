package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xwzStart
 * @create 2022-03-01 17:14
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
