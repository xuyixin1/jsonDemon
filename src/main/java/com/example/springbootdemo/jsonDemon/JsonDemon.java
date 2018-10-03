package com.example.springbootdemo.jsonDemon;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDemon {
    public static final String GET = "get";
    public static final String SET = "set";
    public static  void  getAllFields(HashMap<String,Field> hashmap ,Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
           String name =  field.getName();
           if (Modifier.isStatic(field.getModifiers())){
               continue;
            }
           if(!hashmap.containsKey(name)){
               hashmap.put(name,field);
           }
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class){
           getAllFields(hashmap,clazz.getSuperclass());
        }
    }

    public static void getMethod(List<MethodInfo> infos , HashMap<String,Field> hashmap , Class clazz){
           try {
               for(String name : hashmap.keySet()) {
                   String getMethodName = getMethodName(name);
                   Method getMethod = clazz.getMethod(getMethodName, new Class[]{});
                   String setMethodName = setMethodName(name);
                   Method setMethod = clazz.getMethod(setMethodName,new Class[]{hashmap.get(name).getType()});
                   MethodInfo info = new MethodInfo();
                   info.setField(hashmap.get(name));
                   info.setFieldName(name);
                   info.setSetMethod(setMethod);
                   info.setSetMethodName(setMethodName);
                   info.setGetMethod(getMethod);
                   info.setGetMethodName(getMethodName);
                   infos.add(info);
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
                sb.append("\"" + info.getFieldName() + "\"" + ":");
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
        HashMap<String,Field> hashmap = new   HashMap<>(8);
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(hashmap,clazz);
        getMethod(infos,hashmap,clazz);
        String json = getJson(infos,clazz,o);
        return json;
    }

    public static void main(String[] args){
        String json  = getJson(new Test("a","10"));
        System.out.print(json);
        String s = "{\"a\":\"a\",\"b\":\"10\"}";
        Test t = getClass(s,Test.class);
    }

    public static <T> T getClass(String json ,Class<T> clazz){
        HashMap<String,Field> hashmap = new   HashMap<>(8);
        List<MethodInfo> infos = new ArrayList<>(8);
        getAllFields(hashmap,clazz);
        getMethod(infos,hashmap,clazz);
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
                info.getSetMethod().invoke(t, value);
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
