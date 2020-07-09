package codedragon.eblog.service.impl;

import cn.hutool.crypto.SecureUtil;
import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.User;
import codedragon.eblog.mapper.UserMapper;
import codedragon.eblog.service.UserService;
import codedragon.eblog.shiro.AccountProfile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //注册功能实现
    @Override
    public Result register(User user) {
        //判断用户名邮箱是否已经被注册
        int count = this.count(new QueryWrapper<User>()
                .eq("email", user.getEmail())
                .or()
                .eq("username", user.getUsername()));
        if (count > 0) return Result.fail("用户名或邮箱已被占用");

        User temp = new User();
        temp.setUsername(user.getUsername());
        temp.setPassword(SecureUtil.md5(user.getPassword()));
        temp.setEmail(user.getEmail());

        temp.setCreated(new Date());
        temp.setAvatar("/res/images/avatar/default.png");
        temp.setPoint(0);
        temp.setVipLevel(0);
        temp.setCommentCount(0);
        temp.setPostCount(0);
        temp.setGender("0");
        this.save(temp);
        return Result.success();
    }

    @Override
    public AccountProfile login(String email, String password) {
        User user = this.getOne(new QueryWrapper<User>().eq("email", email));
        if (user == null)
            throw new UnknownAccountException();
        if (!user.getPassword().equals(password))
            throw new IncorrectCredentialsException();

        user.setLasted(new Date());
        this.updateById(user);
        AccountProfile profile = new AccountProfile();
        BeanUtils.copyProperties(user, profile);
        return profile;
    }
}











