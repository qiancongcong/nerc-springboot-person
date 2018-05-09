package com.bjnlmf.nerc.zhihu.Intercepter;

import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Prject: nmPerson
 * @Package: com.bjnlmf.nerc.zhihu.Intercepter
 * @Description: 自定义拦截器,限制上传图片大小
 * @author: cfQiao
 * @date: 2018/1/19 14:56
 */

@Component
public class FileUploadInterceptor extends HandlerInterceptorAdapter {
    //限制5M
    private long maxSize = 5*1024*1024;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否文件上传
        if(request!=null && ServletFileUpload.isMultipartContent(request)) {
            ServletRequestContext ctx = new ServletRequestContext(request);
            //获取上传文件尺寸大小
            long requestSize = ctx.contentLength();
            if (requestSize > maxSize) {
                //当上传文件大小超过指定大小限制后，模拟抛出MaxUploadSizeExceededException异常
               // throw new MaxUploadSizeExceededException(maxSize);
                throw new BusinessException("上传文件大小超过指定大小 5MB");
            }
        }
        return true;
    }
}
