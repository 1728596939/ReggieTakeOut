package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Employee;
import org.apache.ibatis.annotations.Mapper;


//mybatis=plus

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}
