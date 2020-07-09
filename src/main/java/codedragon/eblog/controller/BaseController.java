package codedragon.eblog.controller;

import codedragon.eblog.service.CommentService;
import codedragon.eblog.service.PostService;
import codedragon.eblog.service.UserService;
import codedragon.eblog.shiro.AccountProfile;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    HttpServletRequest req;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 2);
        return new Page(pn, size);
    }

    //从Shiro中获取用户信息
    protected AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    //获取用户ID
    protected Long getProfileId(){
        return getProfile().getId();
    }
}
