package com.sean.eventbus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author WenPing
 * CreateTime 2019/11/21.
 * Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    SubscribeMode SUBSCRIBE_MODE() default SubscribeMode.Posting;
}
