package codedragon.eblog.VO;

import codedragon.eblog.entity.UserMessage;
import lombok.Data;

/**
 * @author : Code Dragon
 * create at:  2020/7/9  20:38
 */
@Data
public class UserMessageVo extends UserMessage {
    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;
}
