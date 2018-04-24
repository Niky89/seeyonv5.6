package com.seeyon.apps.portal.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.service.V5FormService;

public class TopMessageController extends BaseController {
	public ModelAndView getAllMessage(HttpServletRequest request, HttpServletResponse response) {
		// 获取表单数据
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		List<FormDataMasterBean> dataList;
		try {
			// 根据fromID 获取表单数据
			dataList = V5FormService.findMasterDataList(-751812509343988285L, null, 1, 9999, null, null);

			for (FormDataMasterBean bean : dataList) {
				Map<String, String> rmap = new HashMap<String, String>();
				// 获取主表数据
				// 字段为field0001，field0002，field0003
				String f1 = bean.getDataList("field0001").get(0).toString();
				String f2 = bean.getDataList("field0002").get(0).toString();
				rmap.put("标题", f1);
				rmap.put("内容", f2);
				resultList.add(rmap);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 response.setContentType("text/html;charset=UTF-8");
		// 生成列表
		try {
			BufferedWriter bw = new BufferedWriter(response.getWriter());
			bw.write(JSONArray.toJSONString(resultList));
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 返回json
		return null;
	}
}
