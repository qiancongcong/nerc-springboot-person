package com.bjnlmf.nerc.zhihu.enumeration;

public enum BehaviorType {
    praise("点赞"),accusation("举报");
    private String text;

    public String getText() {
        return text;
    }

    private BehaviorType(String text) {
        this.text = text;
    }
}
