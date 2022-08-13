package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.R;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.SMSUtils;
import com.example.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;


    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        //号码不为空，进行发送验证码操作
        if (StringUtils.isNotEmpty(phone)) {

            //生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("本次手机登录验证码为：{}", code);
            //调用阿里云提供的短信服务API
            //  SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

            //需要将生成的验证码保存到Session ,以便于验证码校验
            session.setAttribute(phone, code);

            return R.success("短信发送成功");

        }

        return R.error("短信发送失败");
    }


    /**
     * 移动端验证登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody Map map, HttpSession session) {
        //这里也可以是用UserDto实体类来封装 手机号和验证码
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取前端传来的验证码
        String code = map.get("code").toString();

        //获取生成并保存到Session中的验证码
        String codeSession = session.getAttribute(phone).toString();

        //比对验证码
        if(codeSession!=null  && codeSession.equals(code)){
            //比对成功，说明登录成功

            //判断手机号码是否在表里，不在 说明是新用户，自动注册存储
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user= userService.getOne(userLambdaQueryWrapper);
            if(user==null){
                //说明不存在，存储
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            //登录成功 将用户id存入Session并返回登录结果
           session.setAttribute("user",user.getId() );
            return R.success(user);
        }

        return R.error("验证码错误，请重新输入");
    }

}
