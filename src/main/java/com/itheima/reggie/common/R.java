package com.itheima.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xwzStart
 * @create 2022-03-02 16:59
 */

/**
 * 通用的返回结果,服务端响应的数据最终都会响应成此对象
 * @param <T>
 */
@Data
public class R<T> {
    private Integer code; // 1代表成功,0或其他数字代表失败
    private String msg; // 错误信息
    private T data; // 数据
    private Map map = new HashMap(); // 动态数据


    /*
    R result = new R()
    result.code(1);
    result.data(数据);
    R.success(数据)
     */
    public static<T> R<T> success(T object){
        R<T> r = new R<T>();
        r.code = 1;
        r.data = object;//不用写r.msg,因为成功就不会有错误提示
        return r;
    }
    public static<T> R<T> error(String msg){
        R r = new R();
        r.code = 0;
        r.msg = msg; //不用写r.data,因为登陆失败不会有数据显示
        return r;
    }
    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}