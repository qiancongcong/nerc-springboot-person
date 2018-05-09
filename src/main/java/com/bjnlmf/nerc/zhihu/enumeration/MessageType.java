package com.bjnlmf.nerc.zhihu.enumeration;

public enum MessageType {

    answer("回答"),comment("评论"),reply("回复"),adopt("采纳");

    private String text;

    public String getText() {
        return text;
    }

    private MessageType(String text) {
        this.text = text;
    }
}
