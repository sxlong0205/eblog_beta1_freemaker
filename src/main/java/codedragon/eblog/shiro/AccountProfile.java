package codedragon.eblog.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Code Dragon
 * create at:  2020/7/8  22:03
 */
@Data
public class AccountProfile implements Serializable {
    private String username;    //用户名
    private String email;   //邮箱地址
    private String avatar;  //头像地址
    private String gender;  //用户性别
    private String sign;    //签名
    private Date created;   //更新时间
    private Long id;    //用户ID

    public String getSex() {
        return "0".equals(gender) ? "女" : "男";
    }
}
