package com.chlian.mqservice.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 封装作为微服务的响应实体类，结构尽量简单，如若有复杂的数据返回。请放在data字段里。若不符合需求，继承本类去扩展。
 */
public class RestResult<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Object data = new HashMap<>();//数据，默认hashMap，返回json为 {}

    private String code;  //响应状态

    private String msg;  //响应信息

    public class Status {
        public static final String SUCCUESS = "1";
        public static final String FAILED = "0";
    }

    public RestResult() {

    }

    public RestResult(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResult<?> success() {
        return success(new HashMap<>());
    }

    public RestResult<T> success(Object data) {
        this.success(data, "请求成功");
        return this;
    }

    public RestResult<T> success(Object data, String message) {
        this.code = Status.SUCCUESS;
        this.msg = message;
        this.data = data;
        return this;
    }

    public RestResult<?> error() {
        return error("请求失败");
    }

    public RestResult<?> error(String msg) {
        this.code = Status.FAILED;
        this.msg = msg == null || "".equals(msg) ? "server internal error" : msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
