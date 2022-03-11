package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author xwzStart
 * @create 2022-03-04 11:16
 *
 * --自动填充字段--
 *      createTime-updateTime-createUser-updateUser
 *
 * 方式一:定义BaseContext类,ThreadLocal(同一个线程之间共享数据),
 *       在拦截器中将session中的员工id存储到ThreadLocal对象中,然后通过类名.的方式调用
 *
 * 方式二:直接注入session,获取当前登录的员工id
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private HttpSession session;

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
//        metaObject.setValue("createUser",session.getAttribute("employee"));
//        metaObject.setValue("updateUser",session.getAttribute("employee"));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        metaObject.setValue("updateTime",LocalDateTime.now());
//        metaObject.setValue("updateUser",session.getAttribute("employee"));
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
