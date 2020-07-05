package codedragon.eblog.service;

import codedragon.eblog.VO.PostVo;
import codedragon.eblog.entity.Post;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */

public interface PostService extends IService<Post> {

    IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

}
