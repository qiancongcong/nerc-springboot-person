package com.bjnlmf.nerc.zhihu.pojo.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public class QuestionTypeVO implements Serializable{
    private Long id;                    //主键Id
    private String classify;            //问题分类
    private Long parentId;              //父级类型ID
    private String parentClassify;      //父级分类
}
