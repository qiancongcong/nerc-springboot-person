package com.bjnlmf.nerc.zhihu.pojo.comment;

import com.bjnlmf.nerc.zhihu.enumeration.BehaviorType;
import com.bjnlmf.nerc.zhihu.enumeration.CommentsType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CommentVO implements Serializable {

    private static final long serialVersionUID = -7670704752495019726L;

    private Long id;
    private Long userId; //回复这个评论的用户id	也用于对话的条件查询
    private Integer praise; //点赞数
    private Integer accusation; //被举报次数
    private Integer replyNum;   //回复次数
    private String commentsContent; //评论内容
    private Date createTime; //评论时间
    private CommentsType commentsType; //回复的类型
    private Long questionID;
    private Long answerID;
    private String commentsToBody; //回复对象的昵称
    private Long commentsToId; //回复对象的id（用于后期系统通知该对象）
    private String imageHead;
    private String nickName;
    private BehaviorType behaviorType;
    private Long dialogToCommentId;
    private String ids;
    private Boolean shielding;//是否屏蔽
    private String answerContent; // 回答的内容 （针对后台评论列表）
    private String createTimeStr; //评论时间
}
