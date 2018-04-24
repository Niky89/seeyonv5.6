package com.seeyon.ctp.portal.portlet.controller;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.portal.po.PortalDeskCate;
import com.seeyon.ctp.portal.po.PortalHotspot;
import com.seeyon.ctp.portal.po.PortalTemplate;
import com.seeyon.ctp.portal.portlet.manager.DeskCollaborationManager;
import com.seeyon.ctp.portal.portlet.manager.DeskManager;
import com.seeyon.ctp.util.StringUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.json.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;

public class DeskTopController extends BaseController {
	private final String DefaultSkinPath = "harmony";
	DeskManager deskManager;
	DeskCollaborationManager deskCollaborationManager;

	public void setDeskCollaborationManager(DeskCollaborationManager deskCollaborationManager) {
		this.deskCollaborationManager = deskCollaborationManager;
	}

	public void setDeskManager(DeskManager deskManager) {
		this.deskManager = deskManager;
	}

	public ModelAndView desktopIndex(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException {
		List<PortalDeskCate> deskCateList = this.deskManager.getDeskCate();

		User user = AppContext.getCurrentUser();
		String skinPathKey = request.getParameter("skinPathKey");
		String istd = request.getParameter("istd");// 跳转第二级页面
		String isnr = request.getParameter("isnr");// 跳转第三级页面
		String iszy = request.getParameter("iszy");// 跳转第四级页面
		String typeid = request.getParameter("typeid");
		if (Strings.isBlank(skinPathKey)) {
			skinPathKey = getSkinPathKey(user);
		}
		List<String> metroCategoryList = new ArrayList();
		metroCategoryList.add("PortletCategory");
		metroCategoryList.add("BusinessAppCategory");
		metroCategoryList.add("ThirdPartyProducts");
		if ((AppContext.hasPlugin("u8")) || (AppContext.hasPlugin("nc"))) {
			metroCategoryList.add("PortletAppCategory");
		}
		ModelAndView mv = null;
		// 判断跳转页面--------------------------------------
		if (istd != null && !"".equals(istd)) {
			if (istd.toLowerCase().equals("true")) {
				mv = new ModelAndView("raw:/main/frames/desktop/index1.jsp");
			} else {
				mv = new ModelAndView("raw:/main/frames/desktop/index.jsp");
			}
		} else {
			if (isnr != null && !"".equals(isnr)) {
				if (isnr.toLowerCase().equals("true")) {
					mv = new ModelAndView("raw:/main/frames/desktop/index2.jsp");
					mv.addObject("typeid", typeid);
				} else {
					mv = new ModelAndView("raw:/main/frames/desktop/index.jsp");
				}
			} else {
				if (iszy != null && !"".equals(iszy)) {
					if (iszy.toLowerCase().equals("true")) {
						mv = new ModelAndView("raw:/main/frames/desktop/index3.jsp");
						mv.addObject("typeid", typeid);
					} else {
						mv = new ModelAndView("raw:/main/frames/desktop/index.jsp");
					}
				} else {
					mv = new ModelAndView("raw:/main/frames/desktop/index.jsp");
				}
			}
		}

		// 判断跳转页面==========================================end

		mv.addObject("metroCategoryList", metroCategoryList);
		mv.addObject("deskCateList", JSONUtil.toJSONString(deskCateList));

		mv.addObject("skinPathKey", skinPathKey);
		return mv;
	}

	public ModelAndView shortcutSectionAdd(HttpServletRequest request, HttpServletResponse response)
			throws BusinessException {
		ModelAndView mv = new ModelAndView("ctp/portal/desktopSection/shortcutSectionAdd");
		User user = AppContext.getCurrentUser();
		String skinPathKey = request.getParameter("skinPathKey");
		if (Strings.isBlank(skinPathKey)) {
			skinPathKey = getSkinPathKey(user);
		}
		List<String> metroCategoryList = new ArrayList();
		metroCategoryList.add("PortletCategory");
		if ((AppContext.hasPlugin("u8")) || (AppContext.hasPlugin("nc"))) {
			metroCategoryList.add("PortletAppCategory");
		}
		metroCategoryList.add("BusinessAppCategory");

		String fragmentId = request.getParameter("fragmentId");

		mv.addObject("metroCategoryList", metroCategoryList);
		mv.addObject("skinPathKey", skinPathKey);
		mv.addObject("fragmentId", fragmentId);
		return mv;
	}

	private String getSkinPathKey(User user) {
		if (user != null) {
			List<PortalTemplate> templates = user.getTemplates();
			if (CollectionUtils.isNotEmpty(templates)) {
				PortalTemplate temp = (PortalTemplate) templates.get(0);
				List<PortalHotspot> hotspots = temp.getPortalHotspots();
				if (CollectionUtils.isNotEmpty(hotspots)) {
					return ((PortalHotspot) hotspots.get(0)).getExt10();
				}
				return "harmony";
			}
		}
		return "harmony";
	}
}
