package com.bjnlmf.nerc.zhihu.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Behavior {

    //通过调用的接口区分出（ 提问 回答 评论 点赞 ) 这几种动作
    String u_act() default "";

}
