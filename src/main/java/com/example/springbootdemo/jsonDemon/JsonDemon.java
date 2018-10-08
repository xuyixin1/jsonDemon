package com.example.springbootdemo.jsonDemon;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDemon {
    public static final String GET = "get";
    public static final String SET = "set";
    public static  void  getAllFields(List<MethodInfo> infos ,Class clazz){
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
           infos.add(info);
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class){
           getAllFields(infos,clazz.getSuperclass());
        }
    }

    public static void getMethod(List<MethodInfo> infos , Class clazz){
           try {
               for(MethodInfo info : infos) {
                   String getMethodName = getMethodName(info.getFieldName());
                   Method getMethod = clazz.getMethod(getMethodName,null);
                   String setMethodName = setMethodName(info.getFieldName());
                   Method setMethod = clazz.getMethod(setMethodName,new Class[]{info.getField().getType()});
                   info.setSetMethod(setMethod);
                   info.setSetMethodName(setMethodName);
                   info.setGetMethod(getMethod);
                   info.setGetMethodName(getMethodName);
               }
           } catch (NoSuchMethodException e) {
               e.printStackTrace();
           }
    }

    public static String getJson(List<MethodInfo> infos, Class clazz, Object o){
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("{");
            for (MethodInfo info : infos) {
                if (info.getAnnoName()!= ""){
                    sb.append("\"" + info.getAnnoName() + "\"" + ":");
                }else{
                    sb.append("\"" + info.getFieldName() + "\"" + ":");
                }
                sb.append("\"" + info.getGetMethod().invoke(clazz.cast(o),null).toString()+ "\",");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
       return  sb.toString().substring(0,sb.length()-1) + "}";
    }
    public static String getMethodName(String name){
        return  GET + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String setMethodName(String name){
        return SET + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String getJson(Object o){
        Class clazz = o.getClass();
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(infos,clazz);
        getMethod(infos,clazz);
        String json = getJson(infos,clazz,o);
        return json;
    }

    public static void main(String[] args){
        String json  = getJson(new Test("a",10,15));
        System.out.print(json);
        String s = "{\"a\":\"a\",\"b\":\"10\",\"c\":\"10\"}";
        Test t = getClass(s,Test.class);
    }

    public static <T> T getClass(String json ,Class<T> clazz){
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(infos,clazz);
        getMethod(infos,clazz);
        T t = getClass(json,infos,clazz);
        return t;
    }

    public static  <T> T getClass(String json, List<MethodInfo> infos, Class<T> clazz){
        T t = null;
        try {
            t = clazz.newInstance();
            for (MethodInfo info : infos) {
                String fieldName = info.getFieldName();
                String value = getFieldNameValue(json, fieldName);
                Type type = info.getField().getType();
                if (type.toString().equals("int")|| type.toString().equals("class java.lang.Integer")){
                    info.getSetMethod().invoke(t, Integer.parseInt(value));
                } else if (type.toString().equals("long")|| type.toString().equals("class java.lang.Long")) {
                    info.getSetMethod().invoke(t, Long.parseLong(value));
                } else if (type.toString().equals("short")|| type.toString().equals("class java.lang.Short")) {
                    info.getSetMethod().invoke(t, Short.parseShort(value));
                } else if (type.toString().equals("byte")|| type.toString().equals("class java.lang.Byte")){
                    info.getSetMethod().invoke(t, Byte.parseByte(value));
                }else{
                    info.getSetMethod().invoke(t, value);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        finally{
            return t;
        }
    }

    public static String getFieldNameValue(String json,String fieldName){
        int index = json.indexOf(fieldName);
        if (index == -1){
            return "";
        }
        String value = json.substring(index);
        value = value.substring(getCharacterPosition(value,"\"",2) + 1,getCharacterPosition(value,"\"",3));
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
