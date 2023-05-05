package com.it.reggie.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author xwzStart
 * @create 2022-03-03 16:38
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 5555
     * 添加重复异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //先获取异常种类
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // Duplicate entry '123' for key 'idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String name = split[2] + "这个名字已经被人使用啦~";
            return R.error(name);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class) //先获取异常种类
    public R<String> customException(CustomException ex){

        return R.error(ex.getMessage());
    }
}
