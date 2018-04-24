package com.deheng.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class WeChatLoginFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest http_req = (HttpServletRequest)arg0;
		//判断一下来自微信的才走这个过滤器
		String ua = http_req.getHeader("user-agent").toLowerCase();
		if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
			WeChatLoginHttpServletRequestWrapper req=new WeChatLoginHttpServletRequestWrapper(http_req, http_req.getParameterMap());
			arg2.doFilter(req, arg1);
		}else{
			arg2.doFilter(arg0, arg1); 
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
