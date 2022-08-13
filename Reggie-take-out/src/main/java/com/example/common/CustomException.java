package com.example.common;
/**
 * 功能描述
        *自定义业务异常，在删除分类时，若当前分类关联了菜品或套餐，则处理
         * @return
        * @author Anna.
        * @date
        */
public class CustomException extends  RuntimeException{

    public CustomException(String message){
        super(message);
    }
}
