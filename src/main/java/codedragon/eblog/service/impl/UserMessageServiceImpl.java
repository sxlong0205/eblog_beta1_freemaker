package codedragon.eblog.service.impl;

import codedragon.eblog.entity.UserMessage;
import codedragon.eblog.mapper.UserMessageMapper;
import codedragon.eblog.service.UserMessageService;
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
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

}
