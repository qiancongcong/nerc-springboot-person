package com.bjnlmf.nerc.zhihu.pojo.area;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter@Getter
public class AreaVO implements Serializable{
    private Long id;         //主键ID
    private String areaName; //区域名称
    private String areaCode; //区域编码
    private Long parentId;   //父级地区ID
}
