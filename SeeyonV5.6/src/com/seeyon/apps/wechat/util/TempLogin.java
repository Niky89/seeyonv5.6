package com.seeyon.apps.wechat.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.seeyon.ctp.login.AbstractLoginAuthentication;
import com.seeyon.ctp.login.LoginAuthenticationException;

public class TempLogin  extends AbstractLoginAuthentication{

	@Override
	public String[] authenticate(HttpServletRequest arg0, HttpServletResponse arg1)
			throws LoginAuthenticationException {
		
		return new String[]{"宋小龙","123456"};
	}

}
