package codedragon.eblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.User;
import codedragon.eblog.util.ValidationUtil;
import com.google.code.kaptcha.Producer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author : Code Dragon
 * create at:  2020/7/8  19:00
 */
@Controller
public class AuthController extends BaseController {

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

    @GetMapping("/capthca.jpg")
    public void kaptcha(HttpServletResponse resp) throws IOException {
        //验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        req.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);

        resp.setHeader("Cache-Control", "no-store, no-cache");
        resp.setContentType("image/jpeg");
        ServletOutputStream outputStream = resp.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String email, String password) {
        //校验用户名密码不为空
        if (StrUtil.isEmpty(email) || StrUtil.isBlank(password))
            return Result.fail("邮箱或密码不能为空");

        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));

        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException)
                return Result.fail("用户不存在");
            else if (e instanceof LockedAccountException)
                return Result.fail("用户被禁用");
            else if (e instanceof IncorrectCredentialsException)
                return Result.fail("密码错误");
            else
                return Result.fail("用户认证失败");
        }

        return Result.success().action("/");
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user, String repass, String vercode) {

        //校验用户输入是否合法
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors())
            return Result.fail(validResult.getErrors());

        //校验密码
        if (!user.getPassword().equals(repass))
            return Result.fail("两次输入密码不相同");

        //获取并校验验证码
        String capthca = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        System.out.println(capthca);
        if (vercode == null || !vercode.equalsIgnoreCase(capthca))
            return Result.fail("验证码输入不正确");


        //注册功能
        Result result = userService.register(user);
        return result.action("/login");
    }

    @GetMapping("/register")
    public String register() {
        return "/auth/reg";
    }

    //退出功能
    @RequestMapping("/user/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }
}










