package codedragon.eblog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CodeDragon
 * @since 2020-07-05
 */
@Controller
public class PostController extends BaseController {
    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id) {
        req.setAttribute("currentCategoryId", id);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {
        return "post/detail";
    }
}
