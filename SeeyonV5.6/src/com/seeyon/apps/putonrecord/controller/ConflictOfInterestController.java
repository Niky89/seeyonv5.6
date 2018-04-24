package com.seeyon.apps.putonrecord.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.seeyon.apps.putonrecord.manager.ConflictOfInterestManager;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.util.StringUtil;

/**
 * 
 * @author sxl 利益冲突检测控制器类
 */
public class ConflictOfInterestController extends BaseController {

	
	private ConflictOfInterestManager coimanager;
	
	//客户查询
	public ModelAndView kehuQuery(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/kehuQuery");
		return mav;
	}
	//案件信息查询
	public ModelAndView ajMessageQuery(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/ajMessageQuery");
		return mav;
	}

	// 显示利益冲突页面
	public ModelAndView showPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/conflictofinterest");
		return mav;
	}

	// 获取所有委托人
	public ModelAndView getAllPrincipal(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/putonrecord/JsonObjectResult");
		
		String param =request.getParameter("param")==null?"":request.getParameter("param").toString(); 
		if (StringUtil.checkNull(param)) {
			param = "";
		}
		List<Map<String, String>> result = coimanager.getAllPrincipal(param);
		String rs = JSONArray.toJSONString(result);
		mav.addObject("objname", "wtalljson");
		mav.addObject("objval", rs);
		return mav;
	}

	public ConflictOfInterestManager getCoimanager() {
		return coimanager;
	}

	public void setCoimanager(ConflictOfInterestManager coimanager) {
		this.coimanager = coimanager;
	}

}
