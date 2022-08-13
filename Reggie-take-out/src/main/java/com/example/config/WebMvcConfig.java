package com.example.config;

import com.example.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
/*
* 设置静态资源映射
*
* */
//
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/static/front/");
    }



    /*扩展Mvc消息转换器
    *用来处理前端返回id值 精度缺失问题，设置转换器后，可将id 从long类型转换为字符串类型，再返回给前端
    **/
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        log.info("扩展消息转换器。。。。。");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层用jackson将java转化为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
       //将上面的消息转换器对象追加到mvc框架转换器集合中
        converters.add(0,messageConverter);
    }
}
