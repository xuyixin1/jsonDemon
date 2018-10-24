package com.example.springbootdemo.jsonDemon;

import com.example.springbootdemo.jsonDemon.json.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDemon {
    private static final String GET = "get";
    private static final String SET = "set";
    private static  void  getAllFields(List<MethodInfo> infos ,Class clazz){
        try{
            if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class){
                getAllFields(infos,clazz.getSuperclass());
            }
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                if (Modifier.isStatic(field.getModifiers())){
                    continue;
                }
                MethodInfo info = new MethodInfo();
                String annoName = "";
                if (field.isAnnotationPresent(JsonField.class)){
                    annoName = field.getDeclaredAnnotation(JsonField.class).name();
                }
                String  filedName =  field.getName();
                info.setField(field);
                info.setFieldName(filedName);
                info.setAnnoName(annoName);
                String getMethodName = getMethodName(info.getFieldName());
                Method getMethod = clazz.getMethod(getMethodName,null);
                String setMethodName = setMethodName(info.getFieldName());
                Method setMethod = clazz.getMethod(setMethodName,new Class[]{info.getField().getType()});
                info.setSetMethod(setMethod);
                info.setSetMethodName(setMethodName);
                info.setGetMethod(getMethod);
                info.setGetMethodName(getMethodName);
                infos.add(info);
            }

        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static String getJson(List<MethodInfo> infos, Class clazz, Object o){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (MethodInfo info : infos) {
            if (info.getAnnoName().isEmpty()){
                sb.append("\"" + info.getAnnoName() + "\"" + ":");
            }else{
                sb.append("\"" + info.getFieldName() + "\"" + ":");
            }
            Type type = info.getField().getType();
            if (type.toString().equals("int")|| type.toString().equals("class java.lang.Integer")){
               IntegerJson.getString(info.getGetMethod(), clazz, o,sb);
            } else if (type.toString().equals("long")|| type.toString().equals("class java.lang.Long")) {
                LongJson.getString(info.getGetMethod(), clazz, o,sb);
            } else if (type.toString().equals("short")|| type.toString().equals("class java.lang.Short")) {
               ShortJson.getString(info.getGetMethod(), clazz, o,sb);
            }else{
                StringJson.getString(info.getGetMethod(), clazz, o,sb);
            }
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return  sb.toString();
    }
    private static String getMethodName(String name){
        return  GET + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static String setMethodName(String name){
        return SET + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String getJson(Object o){
        Class clazz = o.getClass();
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(infos,clazz);
        String json = getJson(infos,clazz,o);
        return json;
    }



    public static <T> T getClass(String json ,Class<T> clazz){
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(infos,clazz);
        T t = getClass(json,infos,clazz);
        return t;
    }

    private static  <T> T getClass(String json, List<MethodInfo> infos, Class<T> clazz){
        T t = null;
        try {
            t = clazz.newInstance();
            for (MethodInfo info : infos) {
                String fieldName ;
                if (info.getAnnoName().isEmpty()){
                   fieldName = info.getFieldName();
                } else {
                    fieldName = info.getAnnoName();
                }
                Type type = info.getField().getType();
                if (type.toString().equals("int")|| type.toString().equals("class java.lang.Integer")){
                    IntegerJson.setClass(info.getSetMethod(),t,json,fieldName);
                } else if (type.toString().equals("long")|| type.toString().equals("class java.lang.Long")) {
                    LongJson.setClass(info.getSetMethod(),t,json,fieldName);
                } else if (type.toString().equals("short")|| type.toString().equals("class java.lang.Short")) {
                    ShortJson.setClass(info.getSetMethod(),t,json,fieldName);
                } else if (type.toString().equals("byte")|| type.toString().equals("class java.lang.Byte")){
                    ByteJson.setClass(info.getSetMethod(),t,json,fieldName);
                }else if (type.toString().equals("class java.lang.String")){
                    StringJson.setClass(info.getSetMethod(),t,json,fieldName);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }catch (InstantiationException e) {
            e.printStackTrace();
        }
        finally{
            return t;
        }
    }


    public static void main(String[] args){
        String json  = getJson(new Test("a",10,15));
        System.out.print(json);
        String s = "{\"a\":\"a\",\"int\":10,\"Integer\":10}";
        Test t = getClass(s,Test.class);
    }

}
