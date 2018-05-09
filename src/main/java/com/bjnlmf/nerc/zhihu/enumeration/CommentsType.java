package com.bjnlmf.nerc.zhihu.enumeration;

public enum CommentsType {
    comment("评论"),reply("回复");
    private String text;

    public String getText() {
        return text;
    }

    private CommentsType(String text) {
        this.text = text;
    }
}
