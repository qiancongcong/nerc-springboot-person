package com.bjnlmf.nerc.zhihu.pojo.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter@Setter
public class QuestionObjectDO implements Serializable {
    private String id;                      //主键Id
    private Long userID;                    //提问用户Id
    private Long questionTypeID;            //问题类型Id
    private String provinceCode;            //省
    private String cityCode;                //市
    private String areasCode;              //区县
    private String questionTitle;           //问题标题
    private String questionContent;         //问题内容
    private List<String> images;            //问题携带的图片
    private String imageIds;
    private Integer auditState;             //审核的状态 (1:审核通过，2:待审核，3:审核未通过)
    private Boolean shieldingTitle;                      //是否屏蔽标题
    private String shieldingTitleKeyWord;                //标题屏蔽关键字
    private Boolean shieldingContent;                     //是否屏蔽内容
    private String shieldingContentKeyWord;              //内容屏蔽关键字
}
