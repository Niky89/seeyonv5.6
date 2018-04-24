package com.seeyon.apps.wechat.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

import org.springframework.web.servlet.ModelAndView;

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;

public class CheckWeChatRequestController extends BaseController{
	private String token="wMKSBOpIhLR2zD0bgTUb1fNNd1f6";
	private String encodingAESKey="M1Jsnv455h6wAuzMiv9MTagrnyieb7RLXTKpl4uPMVH";
	private String corpid="wx119567e228b99787";
	//http://221.0.200.194:886/seeyon/putonrecord/wechatcheckrequest.do
	@NeedlessCheckLogin
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		// 获取参数
		String msg_signature=null;
		String timestamp=null;
		String nonce=null;
		String echostr=null;
		String deechostr=null;
		
		try {
			msg_signature = request.getParameter("msg_signature");
			nonce=request.getParameter("nonce");
			timestamp = request.getParameter("timestamp");
			echostr = request.getParameter("echostr");
			WXBizMsgCrypt de=new WXBizMsgCrypt(token, encodingAESKey, corpid);
			deechostr=de.VerifyURL(msg_signature, timestamp, nonce, echostr);
		} catch (AesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(deechostr);
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().write(deechostr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
