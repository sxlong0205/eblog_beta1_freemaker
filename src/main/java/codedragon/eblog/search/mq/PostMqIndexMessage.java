package codedragon.eblog.search.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : Code Dragon
 * create at:  2020/7/14  10:22
 */
@Data
@AllArgsConstructor
public class PostMqIndexMessage implements Serializable {
    public final static String CREATE_OR_UPDATE = "create_update";
    public final static String REMOVE = "remove";

    private Long postId;
    private String type;
}
