package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: Tong Ziyu
 * @Date: 2024/5/19 23:57
 */
@Data
public class WmNewsPageReqDto extends PageRequestDto {

    private Short status;

    private Date beginPubDate;

    private Date endPubDate;

    private Integer channelId;

    private String keyword;
}
