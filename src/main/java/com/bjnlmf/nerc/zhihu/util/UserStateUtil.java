package com.bjnlmf.nerc.zhihu.util;

import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;

public class UserStateUtil {

    public static void checkUserState(UserVO userVO){
        if(userVO.getState()==null||!userVO.getState().equals(1)){
            throw new BusinessException("此账户已被禁言,解禁请联系客服。");
        }
    }
}
