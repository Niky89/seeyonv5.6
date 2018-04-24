package com.seeyon.apps.putonrecord.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.putonrecord.manager.AnJianQueryManager;
import com.seeyon.ctp.common.controller.BaseController;

/**
 * 
 * @author zxj 案件信息查询
 */
public class AnJianQueryController extends BaseController {

	private AnJianQueryManager anjianmanager;

	// 案件信息查询
	public ModelAndView ajMessageQuery(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/ajMessageQuery");
		return mav;
	}

	// 导出excel
	public ModelAndView dcExcel(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/ajMessageQuery");
		try {
			response.getWriter();
			anjianmanager.dcExcel(request, response);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public AnJianQueryManager getAnjianmanager() {
		return anjianmanager;
	}

	public void setAnjianmanager(AnJianQueryManager anjianmanager) {
		this.anjianmanager = anjianmanager;
	}

}
