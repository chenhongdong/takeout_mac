package com.zuoxi.takeout.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("返回结果")
public class R<T> implements Serializable {
    @ApiModelProperty("编码")
    private Integer code;   // 编码，1成功，其他失败

    @ApiModelProperty("错误信息")
    private String msg; // 信息

    @ApiModelProperty("数据")
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
