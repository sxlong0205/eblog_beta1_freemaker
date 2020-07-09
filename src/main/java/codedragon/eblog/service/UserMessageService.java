package codedragon.eblog.service;

import codedragon.eblog.entity.UserMessage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
public interface UserMessageService extends IService<UserMessage> {

    IPage paging(Page page, QueryWrapper<UserMessage> wrapper);
}
