package com.zuoxi.takeout.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentUid(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentUid() {
        return threadLocal.get();
    }
}
