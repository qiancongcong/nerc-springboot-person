package com.bjnlmf.nerc.zhihu.pojo.answer;

import com.bjnlmf.nerc.zhihu.enumeration.BehaviorType;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AnswerVO implements Serializable {
    private static final long serialVersionUID = -6899761410045741864L;

    private Long id;                     //主键id
    private Long userID;                 //回答者id,
    private Long questionID;             //问题的id
    private String questionTitle;        //问题title
    private String questionTime;         //问题提问时间
    private String questionerImageHead;  //提问者头像
    private String questionerNickName;   //提问者昵称
    private String questionArea;         //问题地址(省)
    private Integer praise;              //点赞数
    private String answerContent;        //回答的内容
    private String createTime;             //回答时间
    private Integer accusation;          //被举报次数
    private Integer answerGet;           //是否被采纳(采纳后同步更改当前用户采纳数)
    private List<ImageDTO> images;         //回答的图片
    private String imageHead;            //用户头像
    private String nickName;             //用户昵称
    private Integer commentNum;          //评论数
    private BehaviorType behaviorType;

    private Boolean shielding;           //是否屏蔽

    private String shieldingAnswerContent;//屏蔽后的内容

    private Integer deleted;             //回答状态
    private String classify;             //问题类型

    private String ids;                  //删除问题ID

    private String phone;                //登陆账号

}
