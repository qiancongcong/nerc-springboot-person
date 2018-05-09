package com.bjnlmf.nerc.zhihu.pojo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class UserManageQuery implements Serializable {
    private Integer pageNumber = 1;         //当前页
    private Integer pageSize = 10;           //页面容量
    private Integer rowStart = 0;           //每页开始索引
    private Long id;
    private Long userID;
    private String nickName; // 用户昵称
    private Date createTime; // 创建时间
    private Integer state; // 状态
    private String startTimeStr; // 开始时间
    private String endTimeStr; // 结束时间

    public void setRowStart() {
        this.rowStart = (this.pageNumber - 1) * this.pageSize;
    }
}
