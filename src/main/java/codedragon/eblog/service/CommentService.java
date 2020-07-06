package codedragon.eblog.service;

import codedragon.eblog.VO.CommentVo;
import codedragon.eblog.entity.Comment;
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
public interface CommentService extends IService<Comment> {

    IPage<CommentVo> paging(Page page, Long postId, Long userId, String order);
}
