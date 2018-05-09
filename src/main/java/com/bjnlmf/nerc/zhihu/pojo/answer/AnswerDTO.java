package com.bjnlmf.nerc.zhihu.pojo.answer;

import com.bjnlmf.nerc.zhihu.util.AbstractQuery;
import lombok.Data;

import java.util.List;

@Data
public class AnswerDTO  extends AbstractQuery {

    private Long id;
    private Long questionID;
    private Integer praise;
    private Boolean dateSort;
    private String answerContent;       //回答的内容
    private Long userID;                //回答者id,
    private List<String> images;        //回答的图片
    private String imageIds;
    private String startDate;           //开始时间
    private String endDate;             //结束时间
    private Integer answerState;        //回答状态
    private String keyword;             //查询关键字
    private String phone;               //根据账号查询关键字
    private Boolean shielding;           //是否屏蔽
    private String shieldingAnswerContent;//屏蔽后的内容

}
