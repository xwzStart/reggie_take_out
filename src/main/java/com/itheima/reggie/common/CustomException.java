package com.itheima.reggie.common;

/**
 * @author xwzStart
 * @create 2022-03-04 18:31
 *
 * 自定义业务异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
