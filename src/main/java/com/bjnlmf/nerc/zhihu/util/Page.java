package com.bjnlmf.nerc.zhihu.util;

import java.io.Serializable;
import java.util.List;


public class Page<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = -8757201789012270912L;
    //当前页码
    private Integer number;
    //每页显示数量
    private Integer size;
    //页数
    private Integer pageTotal;
    //全部实体数
    private Integer entityTotal;
    //当页结果
    private List<T> data;

    public Page() {
    }

    public Page(Integer number, Integer size) {
        this.number = number;
        this.size = size;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSize() {
        return (size <=0 ? 1 : size);
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public Integer getEntityTotal() {
        return entityTotal;
    }

    public void setEntityTotal(Integer entityTotal) {
        this.entityTotal = (entityTotal == null ? 0 : entityTotal);
        this.entityTotal = (this.entityTotal < 0 ? 0 : this.entityTotal);
        pageTotal = (int) Math.ceil((double) this.entityTotal / (double) getSize());
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
