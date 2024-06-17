package org.example.gobang.model;

public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        Result result = new Result();
        result.code = 200;
        result.msg = "";
        result.data = data;
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.code = -1;
        result.msg = msg;
        return result;
    }

    public static Result error(String msg, Object data) {
        Result result = new Result();
        result.code = -1;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result unLogin(String msg) {
        Result result = new Result();
        result.code = -2;
        result.msg = msg;
        return result;
    }
}
