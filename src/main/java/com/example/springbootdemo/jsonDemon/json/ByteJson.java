package com.example.springbootdemo.jsonDemon.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteJson {
    public static void  getString(Method method , Class clazz, Object o, StringBuilder sb) {
        try {
            sb.append(method.invoke(clazz.cast(o), null).toString() + ",");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> void setClass(Method method, T t, String json, String fieldName ){
        String value =  getFieldNameValue(json, fieldName);
        try {
            method.invoke(t, Byte.parseByte(value));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static String getFieldNameValue(String json,String fieldName){
        int index = json.indexOf(fieldName);
        if (index == -1){
            return "";
        }
        String value = json.substring(index);
        value = value.substring(getCharacterPosition(value,":",1) + 1,getCharacterPosition(value,",",1));
        return value;

    }

    public static int getCharacterPosition(String matString,String string,int n){
        Matcher slashMatcher = Pattern.compile(string).matcher(matString);
        int mIdx = 0;
        while(slashMatcher.find())
        {
            mIdx++;
            if(mIdx == n){
                break;
            }
        }
        return slashMatcher.start();
    }
}
