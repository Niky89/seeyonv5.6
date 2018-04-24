package com.deheng.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.login.LoginControl;
import com.seeyon.ctp.util.Strings;

public class SimulationLogin {

	private LoginControl loginControl;

	public void login(HttpServletRequest request, HttpServletResponse response, String username, String password)
			throws BusinessException {
		this.setLoginControl();
		WeChatLoginHttpServletRequestWrapper req=(WeChatLoginHttpServletRequestWrapper)request;
		req.setParameter("login_username", username);
		req.setParameter("login_password", password);
		HttpSession session = request.getSession(false);
		String verifyCode = null;
		String oriToSign = null;

		String seed = null;
		if (session != null) {
			verifyCode = (String) session.getAttribute("login.VerifyCode");
			oriToSign = (String) session.getAttribute("ToSign");
			seed = (String) session.getAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY");
			try {
				session.invalidate();
			} catch (Throwable localThrowable1) {
			}
		}
		session = request.getSession(true);
		if (Strings.isNotBlank(verifyCode)) {
			session.setAttribute("login.VerifyCode", verifyCode);
		}
		if (Strings.isNotBlank(oriToSign)) {
			session.setAttribute("ToSign", oriToSign);
		}
		if (Strings.isNotBlank(seed)) {
			session.setAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY", seed);
		}
		AppContext.putThreadContext("THREAD_CONTEXT_SESSION_KEY", session);

		this.loginControl.transDoLogin(request, session, response);
	}

	public void setLoginControl() {
		LoginControl temp = (LoginControl)AppContext.getBean("loginControl");
		
		this.loginControl = temp;
	}


}
