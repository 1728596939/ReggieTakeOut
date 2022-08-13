package com.example.filter;


import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.example.common.BaseContext;
import com.example.common.R;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * 检查用户是否登录的过滤器
 * */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")

public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;



        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //定义URI,不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",//后台登录
                "/employee/logout",//后台退登
                "/backend/**",//后台资源
                "/front/**",//移动端资源
                "/common/**",//通用类资源
                "/user/sendMsg",//移动端发送信息
                "/user/login",//移动端登录

        };

        //2.判断本次请求是否需要处理
        //调用封装的check方法
        boolean check = check(requestURI, urls);
        log.info("check:{}",check);

        //3.如果不需要处理，直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        //4，1判断登录状态，如果已经登录，则直接放行
          if(request.getSession().getAttribute("employee")!=null){

              log.info("用户已经登录，id为{}",request.getSession().getAttribute("employee"));
               Long empId=(long) request.getSession().getAttribute("employee");

               //调用方法，把id传入threadLocal
              BaseContext.setCurrentId(empId);
              filterChain.doFilter(request, response);
              return;
          }
        //4，2 判断移动端登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){

            log.info("用户已经登录，id为{}",request.getSession().getAttribute("user"));
            Long userId=(long) request.getSession().getAttribute("user");

            //调用方法，把id传入threadLocal
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }


        log.info("用户未登录");
        //5.如果没有登录，就返回登录界面
            //跳转已经在前端JS文件中编写，返回JSON数据，通过数据流向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
          return;




    }

    //检查本次请求是否需要放行
    public boolean check(String requestURI, String[] urls) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);//进行匹配
            if (match) return true;
        }
        return false;


    }
}
