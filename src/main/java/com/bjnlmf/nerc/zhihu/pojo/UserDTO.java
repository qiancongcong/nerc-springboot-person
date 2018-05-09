package com.bjnlmf.nerc.zhihu.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 2836187044523277442L;

    private String userName;
    private String passWord;
}
