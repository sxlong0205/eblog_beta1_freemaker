package codedragon.eblog.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Code Dragon
 * create at:  2020/7/8  20:52
 */
@Data
public class Result implements Serializable {
    //状态码 0代表成功 -1代表失败
    private int status;

    private String msg;

    //返回结果
    private Object data;

    private String action;

    //操作失败返回结果
    public static Result fail(String msg) {
        Result result = new Result();
        result.status = -1;
        result.msg = msg;
        result.data = null;
        return result;
    }


    //操作成功返回结果
    public static Result success(Object data) {
        return Result.success("操作成功", data);
    }
    public static Result success() {
        return Result.success("操作成功", null);
    }


    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.status = 0;
        result.msg = msg;
        result.data = data;
        return result;
    }

    //返回指定跳转页面
    public Result action(String action){
        this.action = action;
        return this;
    }
}
