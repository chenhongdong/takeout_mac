package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.entity.User;
import com.zuoxi.takeout.service.UserService;
import com.zuoxi.takeout.utils.SMSUtils;
import com.zuoxi.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code: {}", code);
            // 调用阿里云提供的短信服务Api完成发送短信
//            SMSUtils.sendMessage("外卖", "", phone, code);
            // 需要将生成的验证码保存到Session中
            // session.setAttribute(phone, code);
            // 将验证码保存到redis中，5分钟后失效
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        }

        return R.success("验证码发送成功");
    }


    /**
     * 登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        // 获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        // 从session里取出存入的code
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            // 新用户就自动完成注册，添加到user表
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }
}



















