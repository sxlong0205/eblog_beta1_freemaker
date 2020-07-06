package codedragon.eblog.template;

import codedragon.eblog.common.templates.DirectiveHandler;
import codedragon.eblog.common.templates.TemplateDirective;
import codedragon.eblog.service.PostService;
import codedragon.eblog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : Code Dragon
 * create at:  2020/7/6  20:55
 */
@Component
public class HotsTemplate extends TemplateDirective {
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "hots";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String key = "week:rank";
        Set<ZSetOperations.TypedTuple> typedTuples = redisUtil.getZSetRank(key, 0, 6);
        List<Map> hotPosts = new ArrayList<>();

        for (ZSetOperations.TypedTuple typedTuple : typedTuples) {
            Map<String, Object> map = new HashMap<>();
            Object value = typedTuple.getValue();
            String postKey = "rank:post" + value;

            map.put("id", value);
            map.put("title", redisUtil.hget(postKey, "post:title"));
            map.put("commentCount", typedTuple.getScore());

            hotPosts.add(map);
        }
        handler.put(RESULTS, hotPosts).render();
    }
}
