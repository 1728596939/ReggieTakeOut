package com.example.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//通用返回结果类
@Data
public class R<T> {

    private Integer code;//编码，1成功，0和其他表示失败

    private String msg;// 返回提示信息

    private T data;//返回数据

    private Map map = new HashMap();//返回动态数据

    //成功方法
    public static <T> R<T> success(T  object){
        R<T> r= new R<T>();
        r.data=object;
        r.code=1;
        return  r;
    }
    //错误方法
    public static <T> R<T> error(String msg){
        R<T> r=new R<T>();
        r.msg=msg;
        r.code=0;
        return r;
    }

    //操作map
    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
