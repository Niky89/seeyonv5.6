package com.seeyon.apps.portal.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.space.manager.PortletEntityPropertyManager;
import com.seeyon.ctp.portal.util.Constants;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.util.hql.SearchInfo;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.util.CommonTools;

public class TopMessageManager {
	private NewsDataManager newsDataManager;
	private PortletEntityPropertyManager portletEntityPropertyManager;
	private NewsTypeManager newsTypeManager;
	private BulDataManager bulDataManager;
	private OrgManager orgManager;

	public NewsDataManager getNewsDataManager() {
		if (this.newsDataManager == null) {
			this.newsDataManager = ((NewsDataManager) AppContext.getBean("newsDataManager"));
		}
		return this.newsDataManager;
	}

	public PortletEntityPropertyManager getPortletEntityPropertyManager() {
		if (this.portletEntityPropertyManager == null) {
			this.portletEntityPropertyManager = ((PortletEntityPropertyManager) AppContext
					.getBean("portletEntityPropertyManager"));
		}
		return this.portletEntityPropertyManager;
	}

	public NewsTypeManager getNewsTypeManager() {
		if (this.newsTypeManager == null) {
			this.newsTypeManager = ((NewsTypeManager) AppContext.getBean("newsTypeManager"));
		}
		return this.newsTypeManager;
	}

	public BulDataManager getBulDataManager() {
		if (this.bulDataManager == null) {
			this.bulDataManager = ((BulDataManager) AppContext.getBean("bulDataManager"));
		}
		return this.bulDataManager;
	}

	public OrgManager getOrgManager() {
		if (this.orgManager == null) {
			this.orgManager = ((OrgManager) AppContext.getBean("orgManager"));
		}
		return this.orgManager;
	}

	@AjaxAccess
	public String getGroupNews(String fragmentId) {
		User user = AppContext.getCurrentUser();
		List<NewsData> list = null;
		List<NewsType> typeList = this.getNewsDataManager().getGroupAllTypeList();
		List<Long> selectTypeList = null;
		try {
			if (Strings.isNotBlank(fragmentId)) {
				String ordinal = "0";
				Map<String, String> preference = this.getPortletEntityPropertyManager()
						.getPropertys(Long.valueOf(Long.parseLong(fragmentId)), ordinal);
				String panelValue = "designated_value";
				if (Strings.isNotBlank(panelValue)) {
					String typeIds = (String) preference.get(panelValue);
					selectTypeList = CommonTools.parseStr2Ids(typeIds);
				}
			}
		} catch (Exception e) {
		}
		try {

			if (CollectionUtils.isNotEmpty(selectTypeList)) {
				typeList = new ArrayList();
				for (Long id : selectTypeList) {
					NewsType type = this.getNewsTypeManager().getById(id);
					if (type != null) {
						typeList.add(this.getNewsTypeManager().getById(id));
					}
				}
			}
			list = this.getNewsDataManager().groupFindByReadUser(user.getId(), typeList, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray ja = JSONArray.parseArray(JSONArray.toJSONString(list));
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			long l = (Long) jo.get("id");
			jo.put("idstr", String.valueOf(l));
		}

		return ja.toJSONString();
	}

	@AjaxAccess
	public String getGroupBull(String fragmentId,int typeid,String ordinal) {
		List<BulData> list = null;
		SearchInfo searchInfo = new SearchInfo();
		User user = AppContext.getCurrentUser();
		List<Long> selectTypeList = null;
		try {
			
			if (Strings.isNotBlank(fragmentId)) {
				//String ordinal = "0";
				Map<String, String> preference = this.getPortletEntityPropertyManager()
						.getPropertys(Long.valueOf(Long.parseLong(fragmentId)), ordinal);
				String panelValue = "designated_value";
				if (Strings.isNotBlank(panelValue)) {
					String typeIds = (String) preference.get(panelValue);
					selectTypeList = CommonTools.parseStr2Ids(typeIds);
				}
			}
		} catch (Exception e) {

		}
		Constants.SpaceType type=Constants.SpaceType.group;
		
		if(typeid==Constants.SpaceType.corporation.ordinal()){
			type=Constants.SpaceType.corporation;
		}
		
		list = this.getBulDataManager().findByReadUserForIndex(user, -1, selectTypeList, type,
				searchInfo);

		JSONArray ja = JSONArray.parseArray(JSONArray.toJSONString(list));
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo = ja.getJSONObject(i);
			long l = (Long) jo.get("id");
			jo.put("idstr", String.valueOf(l));
		}

		return ja.toJSONString();
	}
}
