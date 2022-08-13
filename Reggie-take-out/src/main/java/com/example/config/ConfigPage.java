package com.example.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//分页查询配置类

@Configuration
public class ConfigPage {

        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor(){
            MybatisPlusInterceptor mpi = new MybatisPlusInterceptor();
            mpi.addInnerInterceptor(new PaginationInnerInterceptor());
            return mpi;
        }
    }

