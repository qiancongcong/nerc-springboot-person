package com.bjnlmf.nerc.zhihu.advice;

import com.bjnlmf.nerc.zhihu.exception.BusinessException;
import com.bjnlmf.nerc.zhihu.util.ResponseJson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.function.Consumer;

@ControllerAdvice
public class GlobeExceptionHandler {

    public final static String TOKEN_ERROR = "此token在缓存中没有数据";
    public final static String ILLEGAL_ERROR = "不是合法的请求";
    public final static String FLUSH_TOO_MUCH_ERROR = "您刷新的太频繁喽!";

    private Logger logger = LoggerFactory.getLogger(GlobeExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseJson<String> RuntimeHandler(RuntimeException ex){

        if (StringUtils.contains(ex.getMessage(),"BusinessException")) {

            String message = StringUtils.substring(
                    ex.getMessage(),
                    StringUtils.indexOf(ex.getMessage(),"BusinessException") + 19,
                    StringUtils.indexOf(ex.getMessage(),"\n"));
            return BusinessHandler(new BusinessException(message));

        } else {
            loggerException(ex);
            return ResponseJson.failed("系统内部错误" + ex.getMessage());
        }

    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseJson<String> BusinessHandler(BusinessException ex){

        loggerException(ex);

        if (StringUtils.contains(ex.getMessage(),GlobeExceptionHandler.TOKEN_ERROR)) {
            return ResponseJson.unauthentication(ex.getMessage());
        } else {
            return ResponseJson.businessFailed(ex.getMessage());
        }


    }

    private void loggerException(RuntimeException ex){

        logger.error("ThreadId {}, 错误类名 {} , 错误提示 {}",
                Thread.currentThread().getId(),ex.getClass().toString(),ex.getMessage());

        Consumer<StackTraceElement> logTrack =
                (t) -> logger.error("ThreadId {}, Track : 类名 {}, 方法名 {}，行数 {}",
                        Thread.currentThread().getId(),
                        t.getClassName(),t.getMethodName(),t.getLineNumber());

        if (ex instanceof BusinessException) {
            logTrack.accept(ex.getStackTrace()[0]);
        } else {
            for (StackTraceElement t : ex.getStackTrace()) {
                logTrack.accept(t);
            }
        }
    }
}
