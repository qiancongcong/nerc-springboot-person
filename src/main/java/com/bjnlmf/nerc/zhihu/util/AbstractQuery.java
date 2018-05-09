package com.bjnlmf.nerc.zhihu.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstractQuery implements Serializable{
    private static final long serialVersionUID = 6208631971776512801L;
    //页码
    private Integer pageNumber;
    //页数
    private Integer pageSize;
    private Integer rowStart = 0;


    public Integer getRowStart() {
        return (getPageNumber() - 1) * pageSize;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = (pageNumber <= 0 ? 1 : pageNumber);
    }

    public void setPageSize(Integer pageSize) {
        if(pageSize != null){
            this.pageSize = (pageSize < 0 ? 0 : pageSize);
        }else{
            this.pageSize = pageSize;
        }
    }

}
