package com.seeyon.apps.wechat.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;
import com.seeyon.ctp.util.json.JSONUtil;

public class QueryPeapleController extends BaseController {
	private AttachmentManager attachmentManager;// 临时文件上传

	@NeedlessCheckLogin
	public ModelAndView showPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("plugin/wechat/peapleshowpage");

		return mav;
	}

	@NeedlessCheckLogin
	public ModelAndView queryAllPeapleJson(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedWriter bw = new BufferedWriter(response.getWriter());
			Map<String, String> map = new HashMap<String, String>();
			map.put("123123", "aaaa");
			map.put("2222", "试试中文");
			bw.write(JSONObject.toJSONString(map));
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@NeedlessCheckLogin
	public ModelAndView saveAttacFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedWriter bw = new BufferedWriter(response.getWriter());
			String s = this.attachmentManager.create(ApplicationCategoryEnum.collaboration, Long.valueOf(4L),
					Long.valueOf(5L), request);
			s += JSONUtil.toJSONString(this.attachmentManager.getByReference(Long.valueOf(4L)));
			bw.write(s);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

}
