package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Employee;
import com.example.mapper.EmployeeMapper;
import com.example.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeServiceImp extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService  {


    @Autowired
    private EmployeeMapper employeeMapper;

    //分页查询
    public IPage<Employee> getPage(int currentPage,int pageSize,String name){
        //条件构造器
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        //添加条件
        qw.like(Strings.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        qw.orderByDesc(Employee::getUpdateTime);

        //分页构造器
        IPage page=new Page(currentPage,pageSize);

        page=employeeMapper.selectPage(page,qw);
        log.info("执行用户分页查询");
        return page;

    }


}
