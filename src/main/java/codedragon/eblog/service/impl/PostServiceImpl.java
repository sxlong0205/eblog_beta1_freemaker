package codedragon.eblog.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import codedragon.eblog.VO.PostVo;
import codedragon.eblog.entity.Post;
import codedragon.eblog.mapper.PostMapper;
import codedragon.eblog.service.PostService;
import codedragon.eblog.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {


    @Autowired
    PostMapper postMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {
        if (level == null) level = -1;

        QueryWrapper<Post> wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);

        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<Post> wrapper) {
        return postMapper.selectOnePost(wrapper);
    }

    //初始化本周排行
    @Override
    public void initWeekRank() {
        //获取7天内的文章
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7))
                .select("id, title, user_id, comment_count, view_count, created"));

        //初始化文章的总评论量
        for (Post post : posts) {
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            redisUtil.zSet(key, post.getId(), post.getCommentCount());
            //7天后自动过期(15日发表，7-(18-15)=4)
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;//有效时间，单位为秒
            redisUtil.expire(key, expireTime);

            //缓存文章的一些基本信息(id, 标题，评论数量，作者)
            this.hashCachePostIdAndTitle(post, expireTime);
        }

        //做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        String currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr ? 1 : -1);
        Post post = this.getById(postId);

        //7天后自动过期(15日发表，7-(18-15)=4)
        long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60;//有效时间，单位为秒

        //缓存这篇文章的基本信息
        this.hashCachePostIdAndTitle(post, expireTime);

        //重新做并集
        this.zunionAndStoreLast7DayForWeekRank();
    }

    /**
     * @param vo
     * @return void
     * @author: Code Dragon
     * @description: 刷新页面增加浏览数
     * @date: 2020/7/6 21:27
     */
    @Override
    public void putViewCount(PostVo vo) {
        String key = "rank:post:" + vo.getId();

        //1，从缓存中获取viewcount
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");

        //2. 如果没有，先从实体获取，再加一
        if (viewCount != null) {
            vo.setViewCount(viewCount + 1);
        } else {
            vo.setViewCount(vo.getViewCount() + 1);
        }

        //3. 同步到缓存
        redisUtil.hset(key, "post:viewCount", vo.getViewCount());
    }

    /**
     * @param
     * @return void
     * @author: Code Dragon
     * @description: 本周合并评论数量操作
     * @date: 2020/7/6 20:15
     */
    private void zunionAndStoreLast7DayForWeekRank() {
        String currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        List<String> otherKey = new ArrayList<>();
        String destKey = "week:rank";
        for (int i = -6; i < 0; i++) {
            String temp = "day:rank:" + DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);
            otherKey.add(temp);
        }
        redisUtil.zUnionAndStore(currentKey, otherKey, destKey);
    }

    /**
     * @param post
     * @param expireTime
     * @return void
     * @author: Code Dragon
     * @description: 缓存文章基本信息
     * @date: 2020/7/6 19:00
     */
    private void hashCachePostIdAndTitle(Post post, long expireTime) {
        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if (!hasKey) {
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
            redisUtil.hset(key, "post:viewCount", post.getViewCount(), expireTime);
        }
    }


}
