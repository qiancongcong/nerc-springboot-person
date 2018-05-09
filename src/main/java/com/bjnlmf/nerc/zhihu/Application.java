package com.bjnlmf.nerc.zhihu;

import com.bjnlmf.nerc.zhihu.Intercepter.AdminAuthInterceptor;
import com.bjnlmf.nerc.zhihu.filter.AntiSqlInJectionFilter;
import com.bjnlmf.nerc.zhihu.filter.CorsFilter;
import com.bjnlmf.nerc.zhihu.filter.HttpServletRequestCrossFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@EnableTransactionManagement
@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		CorsFilter httpBasicFilter = new CorsFilter();
		registrationBean.setFilter(httpBasicFilter);
		List<String> urlPatterns = new ArrayList<>();
		urlPatterns.add("/*");
		registrationBean.setUrlPatterns(urlPatterns);
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean testFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new AntiSqlInJectionFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("paramName", "paramValue");
		registration.setName("AntiSqlInJectionFilter");
		registration.setOrder(2);
		return registration;
	}

	@Bean
	public FilterRegistrationBean filterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new HttpServletRequestCrossFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("paramName", "paramValue");
		registration.setName("HttpServletRequestCrossFilter");
		registration.setOrder(3);
		return registration;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(adminAuthInterceptor());
		super.addInterceptors(registry);
	}

	@Bean //将自定义拦截器注册到spring bean中
	public  AdminAuthInterceptor adminAuthInterceptor(){
		return new AdminAuthInterceptor();
	}
}
