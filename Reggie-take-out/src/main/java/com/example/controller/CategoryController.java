package com.example.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.R;
import com.example.entity.Category;
import com.example.entity.Employee;
import com.example.service.CategoryService;
import com.example.service.Impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/*分类管理*/
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;


    //新增分类
    @PostMapping
    public R save(@RequestBody Category category) {

        categoryServiceImpl.save(category);

        return R.success("新增分类成功");

    }


    //分页查询
    @GetMapping("/page")
    public R getPage(int page, int pageSize) {

        log.info("查询页数为:{},查询条数为:{}", page, pageSize);

        //调用查询方法
        IPage<Category> Ipage = categoryServiceImpl.getPage(page, pageSize);
        //如果删除后，查询页数大于可查的最大页数，则查询最大页数
        if (page > Ipage.getPages()) {
            Ipage = categoryServiceImpl.getPage((int) Ipage.getPages(), pageSize);
        }

        return R.success(Ipage);

    }

    //删除分类
    @DeleteMapping
    public R deleteById(Long id ){
        log.info("删除分类，id为：{}",id);
        categoryServiceImpl.remove(id);

        return R.success("分类删除成功");

    }



    /**
     *  修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R update(@RequestBody Category category){
        log.info("修改分类，修改的ID为{}，分类名称为：{}",category.getId(),category.getName());
        categoryServiceImpl.updateById(category);
        return R.success("修改分类成功");

    }

    //添加菜品时的分类查询
    @GetMapping("/list")
    public R getList(Category category){

       return  categoryServiceImpl.getType(category);

    }

}
