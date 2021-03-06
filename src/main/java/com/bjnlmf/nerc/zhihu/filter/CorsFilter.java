package com.bjnlmf.nerc.zhihu.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");

        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");

        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");

        httpServletResponse.setHeader("Access-Control-Max-Age", "0");

        httpServletResponse.setHeader("Access-Control-Allow-Headers", "__setXHR_,appVer,deviceId,sign,X-Forwarded-For,clientType,deviceType,os,osVer,lonlat,userToken,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");

        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        httpServletResponse.setHeader("XDomainRequestAllowed","1");

        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
