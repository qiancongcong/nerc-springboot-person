package com.bjnlmf.nerc.zhihu.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionObjectDTO implements Serializable {
    private static final long serialVersionUID = -6079106363318011142L;

    private  String questionTitle; //问题标题
    private  String praise; //点赞数
    private  String questionDate; //提问时间
}
