package com.bjnlmf.nerc.zhihu.pojo.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter@Getter
public class QuestionQuery implements Serializable{
    private Integer pageNumber;         //当前页
    private Integer pageSize;           //页面容量
    private Integer rowStart;           //每页开始索引
    private Long userID;                //用户ID
    private String  sortField;          //排序字段(createTime:时间,hot:热度)
    private Integer sortOrder;          //排序方式(1:正序,0:倒序)
    private Integer answerState;        //回答状态
    private List<String> questionTypeIds; //筛选类型ID集合(选择不限时传父级类型ID)
    private Long questionTypeId;     //筛选类型ID
    private String ids;
    private String keyword;             //查询关键字
    private String startDate;           //开始时间
    private String endDate;             //结束时间
    private Boolean weatherThumb;       //是否有图
    private String phone;               //疑问账号
    private Long parentId;            //问题父类型ID

    public void setRowStart() {
        this.rowStart = (this.pageNumber - 1) * this.pageSize;
    }
}
