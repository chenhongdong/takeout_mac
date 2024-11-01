package com.zuoxi.takeout.controller;

import com.zuoxi.takeout.common.R;
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
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${assets.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 原文件 如abc.jpg
        String originalFilename = file.getOriginalFilename();
        // 后缀 .jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 文件名 xxx-xx-xxx.jpg
        String filename = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);
        // 目录不存在需要创建
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 输入流，读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(basePath + name);

            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ( (len = fileInputStream.read(bytes)) != -1) {
                // 输出流，写回浏览器
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            // 关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
