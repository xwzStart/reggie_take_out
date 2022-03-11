package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
 * @author xwzStart
 * @create 2022-03-05 11:18
 *
 * 文件上传/下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //file是一个临时文件,需要转存到指定位置,否则本次请求完成后临时文会被删除

        //获取初始名称的后缀名
        String originalFilename = file.getOriginalFilename();
        String endName = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用 UUID 重新生成文件名,避免文件名重复导致文件覆盖
        String fileName = UUID.randomUUID().toString() + endName;

        //创建文件目录
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }



    @GetMapping("/download")
    public void  download(String name, HttpServletResponse response){

        try {
            //通过输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //通过输出流写文件
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应回去的文件类型
            response.setContentType("image/jpeg");

            //流的拷贝定义数组,加快读写速度

            // IOUtils.copy(fileInputStream,outputStream);

            int len = 0;
            byte[] bytes = new byte[1024];

            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关流
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
