package com.bjnlmf.nerc.zhihu.filter;

import com.bjnlmf.nerc.zhihu.util.HttpUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpServletRequestCrossFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper(httpServletRequest);
        String ip = HttpUtil.getRealIP(httpServletRequest.getHeader("X-Forwarded-For"));
        httpServletRequestWrapper.putHeader("ip",ip);
        filterChain.doFilter(httpServletRequestWrapper,httpServletResponse);
    }
}
