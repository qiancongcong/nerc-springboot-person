package com.bjnlmf.nerc.zhihu.pojo.operation;

import com.bjnlmf.nerc.zhihu.enumeration.OperationType;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class OperationLogVO {
    private String id;                      //主键Id
    private Long userId;                    //操作用户ID
    private Long relationId;                //操作对象ID
    private String createTime;              //操作时间
    private OperationType operationType;    //操作对象类型
    private String nickName;                //操作人昵称
}
