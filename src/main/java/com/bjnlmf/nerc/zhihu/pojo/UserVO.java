package com.bjnlmf.nerc.zhihu.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    private Long id;
    private Long userID;
    private String userToken;                   //福坤返回的用户token
    private String phone;                       //手机号
    private String agent;
    private String imageHead;                   //用户头像
    private String nickName;                    //用户昵称
    private Integer answerTotal;                //回答总数
    private Integer answerGet;                  //回答被采纳数
    private Integer obtainBrowsedNum;           //获得浏览数
    private Integer obtainPraiseNum;            //获得赞数量
    private Date createTime; // 创建时间
    private String createTimeStr; // 创建时间str
    private Integer state; // 状态
    private Integer questionCount; // 提问数
    private Integer answerCount; // 回答数
}
