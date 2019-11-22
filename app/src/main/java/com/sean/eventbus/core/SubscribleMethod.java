package com.sean.eventbus.core;

import java.lang.reflect.Method;

/**
 * Author WenPing
 * CreateTime 2019/11/21.
 * Description:获取注册类中的方法信息
 */
public class SubscribleMethod {

    //注册类包含的方法
    private Method mehtod;
    //注册类的线程类型
    private SubscribeMode threadMode;
    //注册类的参数类型
    private Class<?> eventType;

    //全参数的构造方法
    public SubscribleMethod(Method mehtod, SubscribeMode threadMode, Class<?> eventType) {
        this.mehtod = mehtod;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }
    //set、get方法
    public Method getMehtod() {
        return mehtod;
    }

    public void setMehtod(Method mehtod) {
        this.mehtod = mehtod;
    }

    public SubscribeMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(SubscribeMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
