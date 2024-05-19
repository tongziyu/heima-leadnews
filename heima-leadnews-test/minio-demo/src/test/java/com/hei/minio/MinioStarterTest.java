package com.hei.minio;

import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/18 22:33
 */
@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinioStarterTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void test1() throws FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("/Users/tongziyu/Desktop/list.html");

        fileStorageService.uploadHtmlFile("article","list3.html",fileInputStream);
    }

}
