package com.example.springbootdemo.jsonDemon.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ShortJson {
    public static void getString(Method method , Class clazz, Object o, StringBuilder sb) {
        try {
            sb.append(method.invoke(clazz.cast(o), null).toString() + ",");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
