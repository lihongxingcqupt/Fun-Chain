package com.cqupt.art.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {
    private String msg;
    private Integer code;
    private Boolean success;
    private static final long serialVersionUID = 1L;

    private R() {
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        r.put("code", 200);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R().put("msg", "success").put("code", 200);
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode() {
        return (Integer) this.get("code");
    }

    public R setData(Object data) {
        put("data", data);
        return this;
    }

    //利用fastjson进行逆转
    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get("data");
        String json = JSON.toJSONString(data);
        T t = JSON.parseObject(json, typeReference);
        return t;
    }

    public <T> T getData(String key, TypeReference<T> typeReference) {
        Object data = get(key);    //默认是map
        String jsonString = JSON.toJSONString(data);
        T t = JSON.parseObject(jsonString, typeReference);
        return t;
    }
}
