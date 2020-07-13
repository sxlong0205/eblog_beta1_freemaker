package codedragon.eblog.controller;

import cn.hutool.core.lang.Assert;
import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : Code Dragon
 * create at:  2020/7/10  22:48
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
    /**
     * @author: Code Dragon
     * @description: TODO
     * @date: 2020/7/10 22:55
     * @param id
     * @param rank 0表示取消，1表示操作
     * @param field
     * @return codedragon.eblog.common.lang.Result
     */
    @ResponseBody
    @PostMapping("/jie-set")
    public Result jetSet(Long id, Integer rank, String field) {

        Post post = postService.getById(id);
        Assert.notNull(post, "该帖子已被删除");

        if("delete".equals(field)) {
            postService.removeById(id);
            return Result.success();

        } else if("status".equals(field)) {
            post.setRecommend(rank == 1);

        }  else if("stick".equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }
}
