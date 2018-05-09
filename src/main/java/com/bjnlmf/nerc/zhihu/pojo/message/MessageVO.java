package com.bjnlmf.nerc.zhihu.pojo.message;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public class MessageVO implements Serializable{
    private Long id;                        //之间ID
    private Long userID;                    //消息接受者ID
    private String nickName;                //消息发送人昵称
    private String species;                 //消息的种类
    private String stateContent;            //提醒模板
    private String affiliatedQuestion;      //关联问题抬头或回答的消息主题
    private Long affiliatedID;              //消息发送者ID
    private Long questionId;                //关联问题的ID
    private Long answerId;                  //关联回答的ID
    private Integer readState;              //读状态
    private String createTime;              //时间

}
