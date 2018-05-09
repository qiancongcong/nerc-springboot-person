package com.bjnlmf.nerc.zhihu.pojo.message;

import com.bjnlmf.nerc.zhihu.enumeration.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class MessageDO implements Serializable{
    private Long id;                    //主键ID
    private Long userID;                //用户ID
    private MessageType species;        //消息的种类
    private String affiliatedQuestion;  //关联问题或回答的消息主题(回答:问题头,评论:回答内容,回复:评论内容,采纳:回答内容)
    private Long affiliatedID;          //关联人的用户ID
    private Long questionId;            //关联问题的ID
    private Long answerId;              //关联回答ID
}
