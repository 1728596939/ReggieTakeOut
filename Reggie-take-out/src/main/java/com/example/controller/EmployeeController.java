package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.R;
import com.example.entity.Employee;
import com.example.service.Impl.EmployeeServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImp employeeServiceImp;

    //登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.获取用户名密码，进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2根据页面提交的用户名查询数据库，查看是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeServiceImp.getOne(queryWrapper);
        //3.如果没有查询到查询结果，则返回失败
        if (emp == null) {
            return R.error("当前用户名不存在");
        }

        //4.如果密码错误，返回失败

        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误");
        }

        //5.查看状态，禁用返回失败
        if (emp.getStatus() != 1) {
            return R.error("账号被禁用");
        }

        //6.登录成功，将员工id存入Session并返回登录结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /*
     * 员工退出*/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除Session中保存的当前登录员工的ID
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    //添加员工
    @PostMapping
    public R save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());

        //设置初始密码，123456，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间，更新时间，创建人，即登录用户

        //已经使用注解自动填充字段
       // employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        Long emplID = (Long) request.getSession().getAttribute("employee");

        ////已经使用注解自动填充字段
        //employee.setCreateUser(emplID);
        //employee.setUpdateUser(emplID);

        boolean save = employeeServiceImp.save(employee);
        return R.success("新增员工成功");

    }


    //分页查询
    @GetMapping("/page")
    public R getPage(int page, int pageSize, String name) {
        log.info("查询页数为:{},查询条数为:{}", page, pageSize);

        //调用查询方法
        IPage<Employee> Ipage = employeeServiceImp.getPage(page, pageSize, name);
        //如果删除后，查询页数大于可查的最大页数，则查询最大页数
        if (page > Ipage.getPages()) {
            Ipage = employeeServiceImp.getPage((int) Ipage.getPages(), pageSize, name);
        }

        return R.success(Ipage);


    }


    /*
    * 状态更新*/
    @PutMapping
    public R updata(HttpServletRequest request,@RequestBody Employee employee){

        //通过Request获取当前登录用户，设置更新的账户
        employee.setUpdateUser((long)request.getSession().getAttribute("employee"));
        //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());

        employeeServiceImp.updateById(employee);

        return R.success("用户状态更新成功");
    }

    /*根据ID查用户*/
    @GetMapping("/{id}")
    public R getById(@PathVariable long id){
        log.info("根据id查询用户：{}",id);
        Employee employee = employeeServiceImp.getById(id);
        if(employee!=null){
        return R.success(employee);}
        return  R.error("没有查询到用户信息");
    }

}
