package com.bjnlmf.nerc.zhihu.enumeration;

public enum ImageType {
    question("问题"),answer("回答"),imageHead("头像"),logo("企业LOGO");
    private String text;

    public String getText() {
        return text;
    }

    private ImageType(String text) {
        this.text = text;
    }
}
