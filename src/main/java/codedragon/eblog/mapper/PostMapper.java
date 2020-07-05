package codedragon.eblog.mapper;

import codedragon.eblog.entity.Post;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
@Component
public interface PostMapper extends BaseMapper<Post> {

    IPage selectPosts(Page page,@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
