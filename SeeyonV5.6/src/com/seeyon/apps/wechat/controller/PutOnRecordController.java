package com.seeyon.apps.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.deheng.login.WeixinWorkLoginAuthentication;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.login.LoginAuthenticationException;
import com.seeyon.ctp.login.controller.MainController;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;

public class PutOnRecordController extends BaseController {

	@NeedlessCheckLogin
	public ModelAndView conflictOfInterest(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException {
		request.setAttribute("appid", "wx119567e228b99787");
		request.setAttribute("secret", "dHqaS5hpdRi765hJDT9v2vCNU9ZRdiBWciO9SVM3K5I");
		// 模拟登陆
		// 判断是否登录 如果登录了可以访问
		WeixinWorkLoginAuthentication au = new WeixinWorkLoginAuthentication();
		try {
			String[] strs = au.authenticate(request, response);

			if (strs != null) {
				ModelAndView mav = new ModelAndView("plugin/putonrecord/conflictofinterest_mobile");
				return mav;
			} else {
				ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
				return mav;
			}
		} catch (LoginAuthenticationException e) {
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
			return mav;
		}

	}

	@NeedlessCheckLogin
	public ModelAndView queryKehu(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		// 判断是否登录 如果登录了可以访问
		request.setAttribute("appid", "wx119567e228b99787");
		request.setAttribute("secret", "g0vuF_yP_y4-CvG6-owgNkSwzg2u3ErKrv4KfLIr-qw");
		WeixinWorkLoginAuthentication au = new WeixinWorkLoginAuthentication();
		try {
			String[] strs = au.authenticate(request, response);

			if (strs != null) {
				ModelAndView mav = new ModelAndView("plugin/putonrecord/kehuQuery_mobile");
				return mav;
			} else {
				ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
				return mav;
			}
		} catch (LoginAuthenticationException e) {
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
			return mav;
		}

	}
	/**
	 * 新人入所指南
	 * @param request
	 * @param response
	 * @return
	 * @throws BusinessException
	 */
	@NeedlessCheckLogin
	public ModelAndView newCommer(HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		// 判断是否登录 如果登录了可以访问
		request.setAttribute("appid", "wx119567e228b99787");
		request.setAttribute("secret", "QDbQ7ImV5-BEEIsDQ4RBJJH9qHJktxjSzarPnpV75jU");
		WeixinWorkLoginAuthentication au = new WeixinWorkLoginAuthentication();
		try {
			String[] strs = au.authenticate(request, response);
System.out.println(strs);
			if (strs != null) {
				ModelAndView mav = new ModelAndView("plugin/newcommer/newcomerh");
				return mav;
			} else {
				ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
				return mav;
			}
		} catch (LoginAuthenticationException e) {
			e.printStackTrace();
			ModelAndView mav = new ModelAndView("plugin/putonrecord/NewFile");
			return mav;
		}

	}

}
