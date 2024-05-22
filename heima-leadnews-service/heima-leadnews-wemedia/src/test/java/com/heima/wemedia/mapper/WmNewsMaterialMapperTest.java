package com.heima.wemedia.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/22 00:51
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class WmNewsMaterialMapperTest {
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Test
    public void test(){

        List<Integer> list = new ArrayList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        wmNewsMaterialMapper.saveRelations(list,23, (short) 24);
    }
}
