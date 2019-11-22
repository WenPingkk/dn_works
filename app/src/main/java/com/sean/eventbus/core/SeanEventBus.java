package com.sean.eventbus.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author WenPing
 * CreateTime 2019/11/21.
 * Description:
 */
public class SeanEventBus {

    private static SeanEventBus instance;

    public static SeanEventBus getDefault() {
        if (instance == null) {
            synchronized (SeanEventBus.class) {
                if (instance == null) {
                    instance = new SeanEventBus();
                }
            }
        }
        return instance;
    }

    private Map<Object, List<SubscribleMethod>> cachedMethod;

    private Handler handler;

    private ExecutorService executorService;


    public SeanEventBus() {
        this.cachedMethod = new HashMap<>();
        this.handler = new Handler(Looper.getMainLooper());
        this.executorService = Executors.newCachedThreadPool();
    }

    //注册
    public void register(Object subscriber) {
        List<SubscribleMethod> methods = cachedMethod.get(subscriber);
        if (methods == null) {
            methods = getSubscribeMethods(subscriber);
            cachedMethod.put(subscriber, methods);
        }

    }

    /**
     * 获取满足条件的
     * 用@subscribe 注解的方法
     *
     * @param subscriber
     * @return
     */
    private List<SubscribleMethod> getSubscribeMethods(Object subscriber) {
        ArrayList<SubscribleMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();
        while (aClass != null) {
            //在系统包的类中，不做遍历
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation == null) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("参数只能有一个");
                }

                SubscribeMode threadMode = annotation.SUBSCRIBE_MODE();
                SubscribleMethod subscribleMethod = new SubscribleMethod(method, threadMode, parameterTypes[0]);
                list.add(subscribleMethod);
            }
            //why?
            aClass = aClass.getSuperclass();
        }
        return list;
    }

    public void unRegister(Object subscriber) {
        List<SubscribleMethod> methods = cachedMethod.get(subscriber);
        if (methods != null) {
            cachedMethod.remove(subscriber);
        }
    }

    public void post(final Object object) {
        Set<Object> set = cachedMethod.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            //获取key == next，value == methods
            final Object next = iterator.next();
            List<SubscribleMethod> methods = cachedMethod.get(next);
            for (final SubscribleMethod method : methods) {
                if (method.getEventType().isAssignableFrom(object.getClass())) {
                    switch (method.getThreadMode()) {
                        case Main:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(method, next, object);
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(method, next, object);
                                    }
                                });
                            }
                            break;
                        case Background:
                        case Async:
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(method, next, object);
                                    }
                                });
                            } else {
                                invoke(method, next, object);
                            }
                            break;

                        case Posting:
                            break;

                    }
                }
            }
        }
    }

    private void invoke(SubscribleMethod subscribleMethod, Object next, Object object) {
        Method mehtod = subscribleMethod.getMehtod();
        try {
            mehtod.invoke(next, object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
