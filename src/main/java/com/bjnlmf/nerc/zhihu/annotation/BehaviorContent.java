package com.bjnlmf.nerc.zhihu.annotation;


import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BehaviorContent {

    String userToken() default "";
    String u_ip() default "";

    //通过调用的接口区分出（ 提问的内容 对提问的回答 回答和评论）动作的对象
    String act_obj() default "";

    //通过调用的接口区分出（ 提问内容 回答的内容 评论内容）动作的内容
    String act_cont() default "";
}
