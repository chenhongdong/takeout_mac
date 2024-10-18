package com.zuoxi.takeout.common;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;   // 编码，1成功，其他失败

    private String msg; // 信息

    private T data;    // 数据

    /**
     * 成功结果
     * @param object
     * @return
     * @param <T>
     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.code = 1;
        r.data = object;
        return r;
    }

    /**
     * 失败结果
     * @param msg
     * @return
     * @param <T>
     */
    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }
}
