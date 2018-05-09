package com.bjnlmf.nerc.zhihu.pojo;

import com.bjnlmf.nerc.zhihu.enumeration.BehaviorType;
import lombok.Data;

import java.io.Serializable;

@Data
public class BehaviorDO implements Serializable {
    private static final long serialVersionUID = -4188511849339916874L;

    private Long id;
    private Long userID;
    private BehaviorType behaviorType;
    private Long answerID;
    private Long commentID;
}
