package com.example.controller;

import com.example.common.R;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 进行文件的上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //配置的文件存储位置
    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件上传方法
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R uploda(MultipartFile file) {
        //file 是一个临时文件，需要转存，否则完成本次请求后会删除
        log.info(file.toString());

        //获得原始文件名后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir=new File(basePath);

        //判断当前目录是否存在
        if(!dir.exists()){

            //不存在，创建目录
            dir.mkdirs();
        }


        try {
        //放在指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //返回文件名称
        return R.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流，通过流入流读取文件能容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流，将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置格式
            response.setContentType("imag/jpeg");

            int len=0;
            byte[] bytes = new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
             outputStream.write(bytes,0,len);
             outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
