package com.bjnlmf.nerc.zhihu.Intercepter;

import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.util.HttpClientUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminAuthInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AdminAuthInterceptor.class);

    @Value("${merc.path}")
    private  String path;

    //自定义拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        if (StringUtils.contains(request.getRequestURI(), "/admin/")) {
            String userToken = request.getHeader("userToken");
            String string = "";
            try {
                string = HttpClientUtil.get(path+"/rest/v1/security/check_token?userToken="+userToken,600000,600000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject result = JSONObject.fromObject(string);
            if(result!=null) {
                //判断是否登录成功
                Object object = result.get("retCode");
                if (object != null) {
                    if (!object.toString().equals("200")) {
                        throw new BusinessException(result.get("message").toString());
                    }
                    result = result.getJSONObject("data");
                    if (!"administrator".equals(result.get("userType").toString())) {
                        throw new BusinessException("没有权限");
                    }
                }
            }
        }

        return true;

    }

}
