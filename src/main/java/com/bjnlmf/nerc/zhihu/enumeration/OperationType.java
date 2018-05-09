package com.bjnlmf.nerc.zhihu.enumeration;

public enum OperationType {
    questions("提问"),answer("回答");
    private String text;

    public String getText() {
        return text;
    }

    private OperationType(String text) {
        this.text = text;
    }
}
