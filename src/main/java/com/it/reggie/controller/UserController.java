package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xwzStart
 * @create 2022-03-09 16:11
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> send(HttpSession session, @RequestBody User user){
        String phone = user.getPhone();
        //判断手机号是否为空
        if(StringUtils.isNotEmpty(phone)){
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("传智健康","SMS_
            // 175485149",phone,code);

            //将生成的验证码存入session,用于后台判断
            //session.setAttribute(phone,code);

            //将生成的验证码存入redis
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);



            return R.success("登录成功");
        }

        return R.error("登陆失败```");
    }

    /**
     * 用户登录
     * @param
     * @return
     *
     * {"phone":"13612341234","code":"2336"}
     * 接收参数不能使用User,因为User表中没有code属性,所以使用Map集合(键值对)
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获取手机号及验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session获取验证码
        //Object sessionCode = session.getAttribute(phone);

        //从redis中获取验证码
        Object sessionCode = redisTemplate.opsForValue().get(phone);

        //进行比对
        if(sessionCode != null && sessionCode.equals(code)){
            //匹配,登录成功
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);

            //检测是不是新用户
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                //保存
                userService.save(user);
            }
            //登录成功后,存入session,要不然会被拦截
            session.setAttribute("user", user.getId());

            //如果登陆成功,删除redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败....");
    }
}
