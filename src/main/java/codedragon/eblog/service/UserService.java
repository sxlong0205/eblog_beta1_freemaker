package codedragon.eblog.service;

import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.User;
import codedragon.eblog.shiro.AccountProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String email, String password);
}
