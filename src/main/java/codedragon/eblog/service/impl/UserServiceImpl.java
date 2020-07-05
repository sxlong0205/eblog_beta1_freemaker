package codedragon.eblog.service.impl;

import codedragon.eblog.entity.User;
import codedragon.eblog.mapper.UserMapper;
import codedragon.eblog.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
