package codedragon.eblog.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import codedragon.eblog.VO.UserMessageVo;
import codedragon.eblog.common.lang.Result;
import codedragon.eblog.entity.Post;
import codedragon.eblog.entity.User;
import codedragon.eblog.entity.UserMessage;
import codedragon.eblog.shiro.AccountProfile;
import codedragon.eblog.util.UploadUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Code Dragon
 * create at:  2020/7/8  23:00
 */
@Controller
public class UserController extends BaseController {
    @Autowired
    UploadUtil uploadUtil;

    //实现个人主页功能
    @GetMapping("/user/home")
    public String home() {
        User user = userService.getById(getProfileId());
        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        req.setAttribute("user", user);
        req.setAttribute("posts", posts);
        return "/user/home";
    }

    //用户设置页面
    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        req.setAttribute("user", user);
        return "/user/set";
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {

        //更新用户头像
        if (StrUtil.isNotBlank(user.getAvatar())) {
            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());
            userService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(temp.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }

        if (StrUtil.isBlank(user.getUsername()))
            return Result.fail("昵称不能为空");
        int count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId())
        );

        //用户名不能重复
        if (count > 0)
            return Result.fail("该昵称已被占用");

        //将用户更新的值入库
        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        //更新Shiro中的用户信息
        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());

        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set#info");
    }

    //用户上传头像
    @ResponseBody
    @PostMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.type_avatar, file);
    }


    //实现用户更改密码功能
    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowpass, String pass, String repass) {
        if (!pass.equals(repass)) {
            return Result.fail("两次密码不相同");
        }

        User user = userService.getById(getProfileId());

        String nowPassMd5 = SecureUtil.md5(nowpass);
        if (!nowPassMd5.equals(user.getPassword())) {
            return Result.fail("密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set#pass");
    }

    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    //我的消息
    @GetMapping("/user/message")
    public String message() {
        IPage<UserMessageVo> page = userMessageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .orderByDesc("created"));

        //把消息改成已读状态
        List<Long> ids = new ArrayList<>();
        for (UserMessageVo messageVo : page.getRecords()) {
            if (messageVo.getStatus() == 0) {
                ids.add(messageVo.getId());
            }
        }
        //批量改成已读
        userMessageService.updateToReaded(ids);

        req.setAttribute("pageData", page);
        return "/user/message";
    }

    //我的发布
    @ResponseBody
    @GetMapping("/user/public")
    public Result userP() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        return Result.success(page);
    }

    //我的收藏
    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection() {
        IPage id = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id", "select post_id from user_collection where user_id = " + getProfileId())
        );
        return Result.success(id);
    }

    //删除评论
    @ResponseBody
    @PostMapping("/message/remove")
    public Result msgRemove(Long id, @RequestParam(defaultValue = "false") Boolean all) {
        boolean remove = userMessageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id)
        );

        return remove ? Result.success(null) : Result.fail("删除失败");
    }

    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums() {
        int count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );

        return MapUtil.builder("status", 0).put("count", count).build();
    }
}










