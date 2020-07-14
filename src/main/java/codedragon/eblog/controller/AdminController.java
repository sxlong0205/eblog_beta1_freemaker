package codedragon.eblog.controller;

import cn.hutool.core.lang.Assert;
import codedragon.eblog.VO.PostVo;
import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.Post;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * @param id
     * @param rank  0表示取消，1表示操作
     * @param field
     * @return codedragon.eblog.common.lang.Result
     * @author: Code Dragon
     * @description: TODO
     * @date: 2020/7/10 22:55
     */
    @ResponseBody
    @PostMapping("/jie-set")
    public Result jetSet(Long id, Integer rank, String field) {

        Post post = postService.getById(id);
        Assert.notNull(post, "该帖子已被删除");

        if ("delete".equals(field)) {
            postService.removeById(id);
            return Result.success();

        } else if ("status".equals(field)) {
            post.setRecommend(rank == 1);

        } else if ("stick".equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }


    @ResponseBody
    @PostMapping("/initEsData")
    public Result initEsData() {

        int size = 10000;
        Page page = new Page();
        page.setSize(size);

        long total = 0;

        for (int i = 0; i < 1000; i++) {
            page.setCurrent(i);
            IPage<PostVo> paging = postService.paging(page, null, null, null, null, null);

            int num = searchService.initEsData(paging.getRecords());

            total += num;

            //当一页查不出10000条的时候，说明是最后一夜了
            if (paging.getRecords().size() < size)
                break;
        }

        return Result.success("ES索引初始化成功，共" + total + "条记录！", null);
    }
}
