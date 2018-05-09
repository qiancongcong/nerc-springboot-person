package com.bjnlmf.nerc.zhihu.pojo.question;

import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.pojo.image.ImageDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter@Setter
public class QuestionVO implements Serializable {
    private  Long id;                                  //主键id
    private  String ids;                                 //问题ID
    private  Long userID;                              //提问用户的id
    private UserVO user;                              //创建问题的用户
    private  String questionTitle;                     //问题标题
    private  String questionContent;                   //问题内容
    private  Long questionTypeID;                      //问题类型ID
    private  List<String> questionTag;                 //问题标签,标签为数组，分别为大栏目，分类，着陆标签
    private  Integer praise;                            //点赞数
    private  Integer answerNum;                         //回答数
    private  String answerState;                       //回答状态(1已回答 0未回答 2 删除)
    private  Integer readNum;                           //浏览数目（问题详情被访问数）
    private  List<ImageDTO> contentImg;                  //问题携带的图片
    private  String createTime;                         //提问时间
    private  String provinceCode;                       //省
    private  String provinceText;
    private  String cityCode;                           //市
    private  String cityText;
    private  String countyCode;                         //区县
    private  String phone;                              //提问账号
    private  String countyText;
    private  String position;                           //提问者的位置（省市区）
    private  Integer accusation;                        //被举报的次数
    private  Integer checkstate;                        //审核的状态 1 审核通过，2，待审核，3审核未通过。
    private Boolean shieldingTitle;                      //是否屏蔽标题
    private String shieldingTitleKeyWord;                //标题屏蔽关键字
    private Boolean shieldingContent;                     //是否屏蔽内容
    private String shieldingContentKeyWord;              //内容屏蔽关键字
    private  Boolean images;                            //是否有图片 true 有
}
