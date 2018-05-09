package com.bjnlmf.nerc.zhihu.aspect;

import com.bjnlmf.nerc.zhihu.annotation.Behavior;
import com.bjnlmf.nerc.zhihu.annotation.BehaviorContent;
import com.bjnlmf.nerc.zhihu.pojo.BehaviorVO;
import com.bjnlmf.nerc.zhihu.pojo.UserVO;
import com.bjnlmf.nerc.zhihu.service.UserService;
import com.bjnlmf.nerc.zhihu.util.JsonUtil;
import com.bjnlmf.nerc.zhihu.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Component
public class BehaviorAspectBean {

    Logger logger = LoggerFactory.getLogger(BehaviorAspectBean.class);

    @Autowired
    private UserService userService;

    //指定一个切面
    //@Pointcut("execution(* com.bjnlmf.nerc.zhihu.controller.*.*(..))")
    @Pointcut("@annotation(com.bjnlmf.nerc.zhihu.annotation.Behavior)")
    public void executeService(){
    }


    @Before(value = "executeService()")
    //@AfterReturning(value = "executeService()",returning = "keys")
    public void addBehaviorLog(JoinPoint joinPoint) throws NoSuchMethodException {
        //拦截的实体类
        Object target = joinPoint.getTarget();
        //拦截的方法参数
        Object[] argsa = joinPoint.getArgs();
        //拦截的方法名称
        String methodName = joinPoint.getSignature().getName();
        //拦截的放参数类型
        Class[] parameterTypes = ((MethodSignature)joinPoint.getSignature()).getMethod().getParameterTypes();
        //获取拦截的方法
        Method method = target.getClass().getMethod(methodName, parameterTypes);

        BehaviorVO behaviorVO= new BehaviorVO();
        //获取方法上的Behavior注解的值
        if(method.isAnnotationPresent(Behavior.class))
        {
            Behavior behavior =  method.getAnnotation(Behavior.class);
            String u_act = behavior.u_act();
            behaviorVO.setU_act(u_act);
        }
        //获取方法参数上的所有注解
        Annotation[][]  annotations=   method.getParameterAnnotations();
        Map<List<String>,Integer> parameterMap = new HashMap<>();
        for (int i=0;i<annotations.length;i++){
            for (int j=0;j<annotations[i].length;j++){
                Annotation annotations1 = annotations[i][j];
                String name = annotations1.annotationType().getName();
                String behaviorContentName= BehaviorContent.class.getName();
                //判断此注解是否为BehaviorContent注解
                if(name.equals(behaviorContentName)){
                    //获取方法参数上BehaviorContent注解的值，并添加到parameterMap中
                    BehaviorContent behaviorContent = (BehaviorContent) annotations1;
                    if(!StringUtils.isEmpty(behaviorContent.userToken())){
                        List<String> places = new ArrayList<String>(Arrays.asList("userToken",behaviorContent.userToken()));
                        //记上此注解添加在第几个参数上
                        parameterMap.put(places,i);
                    }
                    if(!StringUtils.isEmpty(behaviorContent.act_cont())){
                        List<String> places = new ArrayList<String>(Arrays.asList("act_cont",behaviorContent.act_cont()));
                        parameterMap.put(places,i);
                    }
                    if(!StringUtils.isEmpty(behaviorContent.act_obj())){
                        List<String> places = new ArrayList<String>(Arrays.asList("act_obj",behaviorContent.act_obj()));
                        parameterMap.put(places,i);
                    }
                    if(!StringUtils.isEmpty(behaviorContent.u_ip())){
                        List<String> places = new ArrayList<String>(Arrays.asList("u_ip",behaviorContent.u_ip()));
                        parameterMap.put(places,i);
                    }
                }
            }
        }
        //获取对应注解的参数
        if(argsa!=null&&argsa.length>0){
            for (int i = 0 ; i<argsa.length ; i++){
                for (Map.Entry<List<String>,Integer> entry :parameterMap.entrySet()) {
                    //判断参数的位置与注解的位置是否是一致的，如果是则说明此参数为注解参数
                    if(entry.getValue().equals(i)){
                        if("userToken".equals(entry.getKey().get(0))){
                            if(argsa[i]!=null){
                                UserVO userVO = userService.queryByUserToken(argsa[i].toString());
                                behaviorVO.setU_id(userVO.getUserID().toString());
                            }
                        }else {
                            Object object = argsa[i];
                            if(object instanceof String || object instanceof Integer){
                                Object filedName = entry.getKey().get(0);
                                ReflectionUtils.setFieldValue(behaviorVO,filedName.toString(),argsa[i]);
                            }else if(object instanceof Map){
                                Object filedName = entry.getKey().get(1);
                                Object ip = ((Map) object).get(filedName);
                                ReflectionUtils.setFieldValue(behaviorVO,entry.getKey().get(0).toString(),ip);
                            }else if(object instanceof Optional){
                                //获取Optional类型中object的值，如果没有值就返回false
                                Object object1 = ((Optional) object).orElse("false");
                                //然后在次的判断object1的类型
                                if(object1 instanceof String && !object1.equals("false")){
                                    ReflectionUtils.setFieldValue(behaviorVO, entry.getKey().get(0).toString(), object1);
                                }else if(object1 instanceof List) {
                                    String ids = "";
                                    for (int j = 0; j < ((List) object1).size(); j++) {
                                        ids += ((List) object1).get(j) + ",";
                                    }
                                    if (!StringUtils.isEmpty(ids)) {
                                        ReflectionUtils.setFieldValue(behaviorVO, entry.getKey().get(0).toString(), ids);
                                    }
                                }
                            }else {
                                //反射的获取和添加数据
                                Object objectName = ReflectionUtils.invokeGetterMethod(object,entry.getKey().get(1).toString());
                                if(objectName!=null){
                                    ReflectionUtils.setFieldValue(behaviorVO,entry.getKey().get(0).toString(),objectName.toString());
                                }

                            }
                        }
                    }
                }
            }
        }
        //设置访问时间
        behaviorVO.setA_time(""+new Date().getTime());
        logger.info(JsonUtil.jsonify(behaviorVO));
    }

    @AfterReturning("executeService()")
    public void doAfterReturning(JoinPoint joinPoint){

        // 处理完请求，返回内容
        //logger.info("WebLogAspect.doAfterReturning()");

    }

}
