package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/21 16:40
 */
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 批量插入关系
     * @param materialIds
     * @param newsId
     * @param type
     */
    void saveRelations(@Param("materialIds") List<Integer> materialIds,
                       @Param("newId") Integer newsId,
                       @Param("type") Short type);
}
