package com.bjnlmf.nerc.zhihu.pojo.message;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter@Setter
public class MessageQuery implements Serializable {
    private Long userID;            //当前登录用户ID
    private Integer pageNumber;     //当前页
    private Integer pageSize;       //页面容量
    private Integer rowStart;       //其实索引

    public void setRowStart() {
        this.rowStart = (this.pageNumber - 1) * this.pageSize;
    }
}
