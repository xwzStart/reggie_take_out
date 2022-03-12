package com.itheima.reggie.common;

/**
 * @author xwzStart
 * @create 2022-03-04 13:54
 *
 * 基于ThreadLocal封装工具类,用于保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     *
     * 555555
     * 设置id
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
