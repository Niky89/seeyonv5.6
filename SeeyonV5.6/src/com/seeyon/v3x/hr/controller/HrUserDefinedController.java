package com.seeyon.v3x.hr.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyCategory;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.util.CommonTools;

@CheckRoleAccess(roleTypes = { OrgConstants.Role_NAME.HrAdmin, OrgConstants.Role_NAME.SalaryAdmin })
public class HrUserDefinedController extends BaseController {
	private UserDefinedManager userDefinedManager;
	private String jsonView;
	private OrgManager orgManager;

	public OrgManager getOrgManager() {
		return this.orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
		
	}

	public String getJsonView() {
		return this.jsonView;
	}

	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}

	public UserDefinedManager getUserDefinedManager() {
		return this.userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/home");
	}

	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/homeEntry");
	}

	public ModelAndView initSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		User user = CurrentUser.get();

		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		if (settingType == 1) {
			mav = new ModelAndView("hr/userDefined/listCategory");
			List<PropertyCategory> categories = this.userDefinedManager.getCategorysByRemove(0, true);
			List<PropertyCategory> temp = this.userDefinedManager.getCategorysByRemove(0, false);
			CommonTools.addAllIgnoreEmpty(categories, temp);
			categories = CommonTools.pagenate(categories);
			mav.addObject("categories", categories);
		} else {
			WebProperty webProperty;
			if (settingType == 2) {
				mav = new ModelAndView("hr/userDefined/listProperty");
				List<PageProperty> properties = this.userDefinedManager.getPropertyByRemove(0, true);
				List<PageProperty> temp = this.userDefinedManager.getPropertyByRemove(0, false);
				CommonTools.addAllIgnoreEmpty(properties, temp);
				List<WebProperty> webProperties = new ArrayList<WebProperty>();
				for (PageProperty property : properties) {
					webProperty = new WebProperty();
					webProperty.setProperty_id(property.getId());
					webProperty.setPropertyName(property.getName());
					int type = Integer.parseInt(String.valueOf(property.getType()));
					if (type == 1) {
						webProperty.setType("hr.userDefined.type.integer.label");
					} else if (type == 2) {
						webProperty.setType("hr.userDefined.type.float.label");
					} else if (type == 3) {
						webProperty.setType("hr.userDefined.type.date.label");
					} else if (type == 4) {
						webProperty.setType("hr.userDefined.type.varchar.label");
					} else {
						webProperty.setType("hr.userDefined.type.text.label");
					}
					if (property.getNot_null() == 0) {
						webProperty.setNot_null("hr.userDefined.yes.label");
					} else {
						webProperty.setNot_null("hr.userDefined.no.label");
					}
					if (property.getCategory_id() != null) {
						webProperty.setCategory(
								this.userDefinedManager.getCategoryById(property.getCategory_id()).getName());
					}
					Long property_id = property.getId();
					List<PropertyLabel> propertyLabels = this.userDefinedManager
							.getPropertyLabelByPropertyId(property_id);
					for (PropertyLabel label : propertyLabels) {
						if (label.getLanguage().equals("zh_CN")) {
							webProperty.setLabelName_zh(label.getPropertyLabelValue());
						} else if (label.getLanguage().equals("en")) {
							webProperty.setLabelName_en(label.getPropertyLabelValue());
						}
					}
					webProperty.setSysFlag(property.isSysFlag());
					webProperties.add(webProperty);
				}
				webProperties = CommonTools.pagenate(webProperties);
				mav.addObject("properties", webProperties);
			} else {//settingType=3
				mav = new ModelAndView("hr/userDefined/listPage");
				List<Page> pages = new ArrayList();
				boolean isSalaryAdmin = this.orgManager.isRole(user.getId(), user.getLoginAccount(),
						OrgConstants.Role_NAME.SalaryAdmin.name(), new OrgConstants.MemberPostType[0]);
				boolean isHrAdmin = this.orgManager.isRole(user.getId(), user.getLoginAccount(),
						OrgConstants.Role_NAME.HrAdmin.name(), new OrgConstants.MemberPostType[0]);
				if ((isSalaryAdmin) && (isHrAdmin)) {
					List<Page> salaryPages = this.userDefinedManager.getPageByModelName("salary", false, true);
					if (CollectionUtils.isNotEmpty(salaryPages)) {
						pages.addAll(salaryPages);
					}
					Object staffPages = this.userDefinedManager.getPageByModelName("staff", false, true);
					if (CollectionUtils.isNotEmpty((Collection) staffPages)) {
						pages.addAll((Collection) staffPages);
					}
				} else if (isSalaryAdmin) {
					List<Page> salaryPages = this.userDefinedManager.getPageByModelName("salary", false, true);
					if (CollectionUtils.isNotEmpty(salaryPages)) {
						pages.addAll(salaryPages);
					}
				} else {
					List<Page> staffPages = this.userDefinedManager.getPageByModelName("staff", false, true);
					if (CollectionUtils.isNotEmpty(staffPages)) {
						pages.addAll(staffPages);
					}
				}
				List<WebProperty> webPages = new ArrayList<WebProperty>();
				for (Page page : pages) {
					WebProperty webPage = new WebProperty();
					webPage.setPage_id(page.getId());
					webPage.setPageName(page.getPageName());
					webPage.setMemo(page.getMemo());
					if (page.getModelName().equals("salary")) {
						webPage.setModelName("menu.hr.laborageMgr");
					} else {
						webPage.setModelName("menu.hr.staffinfoMgr");
					}
					int display = page.getPageDisplay();
					int repair = page.getRepair();
					if (display == 0) {
						webPage.setDisplay("hr.userDefined.yes.label");
					} else {
						webPage.setDisplay("hr.userDefined.no.label");
					}
					if (repair == 0) {
						webPage.setRepair("hr.userDefined.yes.label");
					} else {
						webPage.setRepair("hr.userDefined.no.label");
					}
					List<PageLabel> pageLabels = new ArrayList<PageLabel>();
					pageLabels = this.userDefinedManager.getPageLabelByPageId(page.getId());
					for (PageLabel label : pageLabels) {
						if (label.getLanguage().equals("zh_CN")) {
							webPage.setLabelName_zh(label.getPageLabelValue());
						} else if (label.getLanguage().equals("en")) {
							webPage.setLabelName_en(label.getPageLabelValue());
						}
					}
					Object properties = this.userDefinedManager.getPropertyByPageId(page.getId());
					if (((List) properties).size() > 0) {
						webPage.setProperty_id(Long.valueOf(Long.parseLong("1")));
					} else {
						webPage.setProperty_id(Long.valueOf(Long.parseLong("0")));
					}
					webPage.setSysFlag(page.isSysFlag());
					webPages.add(webPage);
				}
				webPages = CommonTools.pagenate(webPages);
				mav.addObject("pages", webPages);
			}
		}
		return mav;
	}

	public ModelAndView newCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/newCategory");
	}

	public ModelAndView addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (isSameName(request, "PropertyCategory", null)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		Long accountId = CurrentUser.get().getLoginAccount();
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		PropertyCategory category = new PropertyCategory();
		bind(request, category);
		category.setAccountId(accountId);
		this.userDefinedManager.addCategory(category);
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
				"parent.parent");
	}

	public ModelAndView viewCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updateCategory");
		boolean readonly = Boolean.parseBoolean(request.getParameter("readonly"));
		boolean disabled = Boolean.parseBoolean(request.getParameter("disabled"));
		Long category_id = (Long) CommonTools.parseStr2Ids(request, "category_id").get(0);
		PropertyCategory category = this.userDefinedManager.getCategoryById(category_id);
		mav.addObject("category", category);
		mav.addObject("readonly", Boolean.valueOf(readonly));
		mav.addObject("disabled", Boolean.valueOf(disabled));
		return mav;
	}

	public ModelAndView updateCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = Long.valueOf(Long.parseLong(request.getParameter("id")));
		if (isSameName(request, "PropertyCategory", id)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		PropertyCategory category = this.userDefinedManager.getCategoryById(id);
		bind(request, category);
		this.userDefinedManager.updateCategory(category);
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
				"parent.parent");
	}

	public ModelAndView destroyCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		List<Long> categoryIds = CommonTools.parseStr2Ids(request, "cIds");
		for (Long category_id : categoryIds) {
			if (this.userDefinedManager.getPropertyByCategoryId(category_id, 0).isEmpty()) {
				PropertyCategory pc = this.userDefinedManager.getCategoryById(category_id);
				if (pc != null) {
					pc.setRemove(1);
					this.userDefinedManager.updateCategory(pc);
				}
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}

	public ModelAndView newOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/newOption");
		List categories = this.userDefinedManager.getCategorysByRemove(0);
		mav.addObject("categories", categories);
		return mav;
	}

	public ModelAndView addOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (isSameName(request, "PageProperty", null)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		Long accountId = CurrentUser.get().getLoginAccount();
		PageProperty pageProperty = new PageProperty();
		pageProperty.setName(request.getParameter("propertyName"));
		pageProperty.setType(Long.valueOf(request.getParameter("type")));
		pageProperty.setOrdering(0);
		pageProperty.setNot_null(Integer.parseInt(request.getParameter("notNull")));
		pageProperty.setRemove(0);
		pageProperty.setLength(0);
		pageProperty.setCategory_id(Long.valueOf(Long.parseLong(request.getParameter("categoryId"))));
		pageProperty.setSysFlag(false);
		pageProperty.setAccountId(accountId);
		this.userDefinedManager.addPageProperty(pageProperty);

		String propertyLabel_en = request.getParameter("propertyLabel_en");
		List<Locale> locales = LocaleContext.getAllLocales();

		boolean isGovVersion = true;
		if ((isGovVersion) && (!locales.contains(Locale.ENGLISH.getLanguage()))) {
			locales.add(new Locale("en"));
		}
		int i = 0;
		int y = 0;
		for (Locale locale : locales) {
			PropertyLabel propertyLabel = new PropertyLabel();
			propertyLabel.setProperty_id(pageProperty.getId());
			if ((locale.getLanguage().equals(Locale.CHINESE.getLanguage())) && (i < 1)) {
				propertyLabel.setLanguage("zh_CN");
				propertyLabel.setPropertyLabelValue(pageProperty.getName());
				i++;
				this.userDefinedManager.addPropertyLabel(propertyLabel);
			}
			if ((locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) && (y < 1)) {
				propertyLabel.setLanguage("en");
				propertyLabel.setPropertyLabelValue(propertyLabel_en);
				y++;
				this.userDefinedManager.addPropertyLabel(propertyLabel);
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
				"parent.parent");
	}

	public ModelAndView viewOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updateOption");
		Long property_id = (Long) CommonTools.parseStr2Ids(request, "property_id").get(0);
		PageProperty property = new PageProperty();
		property = this.userDefinedManager.getPropertyById(property_id);
		List categories = new ArrayList();
		categories = this.userDefinedManager.getCategorysByRemove(0);
		List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(property.getId());
		String labelName_zh = "";
		String labelName_en = "";
		for (PropertyLabel label : propertyLabels) {
			if (label.getLanguage().equals("zh_CN")) {
				labelName_zh = label.getPropertyLabelValue();
			} else if (label.getLanguage().equals("en")) {
				labelName_en = label.getPropertyLabelValue();
			}
		}
		mav.addObject("labelName_zh", labelName_zh);
		mav.addObject("labelName_en", labelName_en);
		mav.addObject("property", property);
		mav.addObject("categories", categories);
		mav.addObject("readonly", Boolean.valueOf(Boolean.parseBoolean(request.getParameter("readonly"))));
		mav.addObject("disabled", Boolean.valueOf(Boolean.parseBoolean(request.getParameter("disabled"))));
		return mav;
	}

	public ModelAndView updateOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long property_id = Long.valueOf(Long.parseLong(request.getParameter("id")));
		if (isSameName(request, "PageProperty", property_id)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		PageProperty property = this.userDefinedManager.getPropertyById(property_id);
		property.setName(request.getParameter("propertyName"));
		property.setType(Long.valueOf(request.getParameter("type")));
		property.setNot_null(Integer.parseInt(request.getParameter("notNull")));
		property.setCategory_id(Long.valueOf(Long.parseLong(request.getParameter("categoryId"))));
		this.userDefinedManager.updatePageProperty(property);

		String propertyLabel_en = request.getParameter("propertyLabel_en");
		List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(property.getId());
		for (PropertyLabel label : propertyLabels) {
			if (label.getLanguage().equals("zh_CN")) {
				label.setPropertyLabelValue(property.getName());
				this.userDefinedManager.updatePropertyLabel(label);
			} else if (label.getLanguage().equals("en")) {
				label.setPropertyLabelValue(propertyLabel_en);
				this.userDefinedManager.updatePropertyLabel(label);
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
				"parent.parent");
	}

	public ModelAndView destroyOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		List<Long> propertyIds = CommonTools.parseStr2Ids(request, "oIds");
		List<Repository> repositorys = this.userDefinedManager.getPageByPropertyId(propertyIds.get(0));
		if (CollectionUtils.isNotEmpty(repositorys)) {
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script type='text/javascript'>");
			out.println("alert(parent.v3x.getMessage('HRLang.hr_userDefined_property_isempty_message'));");
			out.println("</script>");
			out.flush();
			return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
					"parent");
		}
		for (Long property_id : propertyIds) {
			PageProperty property = this.userDefinedManager.getPropertyById(property_id);
			property.setRemove(1);
			this.userDefinedManager.updatePageProperty(property);
			this.userDefinedManager.deletePagePropertiesByPropertyId(property_id);
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}

	public ModelAndView viewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updatePage");
		String page_id = request.getParameter("page_id");
		boolean isNew = page_id == null;
		Page page = null;
		List<PageProperty> uses = new ArrayList<PageProperty>();
		PageLabel pageLabel;
		if (isNew) {
			page = new Page(0, false, true);
			mav.addObject("isNew", Boolean.valueOf(true));
		} else {
			Long pageId = (Long) CommonTools.parseStr2Ids(page_id).get(0);
			page = this.userDefinedManager.getPageById(pageId);
			mav.addObject("readonly", Boolean.valueOf(Boolean.parseBoolean(request.getParameter("readonly"))));
			List<PageLabel> pageLabels = new ArrayList<PageLabel>();
			pageLabels = this.userDefinedManager.getPageLabelByPageId(pageId);
			String labelName_en = "";
			for (Iterator<PageLabel> localIterator1 = pageLabels.iterator(); localIterator1.hasNext();) {
				pageLabel = localIterator1.next();
				if (pageLabel.getLanguage().equals("en")) {
					labelName_en = pageLabel.getPageLabelValue();
				}
			}
			mav.addObject("labelName_en", labelName_en);

			uses = this.userDefinedManager.getPropertyByPageId(pageId);
		}
		List<Page> pages = this.userDefinedManager.getPageByRemove(0);
		List<WebProperty> webPages = new ArrayList<WebProperty>();
		for (Page pg : pages) {
			List<PageLabel> pgLabels = new ArrayList<PageLabel>();
			WebProperty webPage = new WebProperty();
			webPage.setModelName(pg.getModelName());
			pgLabels = this.userDefinedManager.getPageLabelByPageId(pg.getId());
			for (PageLabel label : pgLabels) {
				if (label.getLanguage().equals("en")) {
					webPage.setLabelName_en(label.getPageLabelValue());
				} else {
					webPage.setLabelName_zh(label.getPageLabelValue());
				}
				webPage.setPage_id(label.getPage_id());
			}
			webPages.add(webPage);
		}
		mav.addObject("webPages", webPages);

		List<PropertyCategory> categories = this.userDefinedManager.getCategorysByRemove(0);
		mav.addObject("categories", categories);
		mav.addObject("uses", uses);
		mav.addObject("page", page);

		return mav;
	}

	public ModelAndView updatePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		boolean isNew = Boolean.parseBoolean(request.getParameter("isNew"));
		Page page = null;
		int x;
		String pageLabel_en;
		if (isNew) {
			if (isSameName(request, "Page", null)) {
				super.rendJavaScript(response, "parent.isSameName();");
				return null;
			}
			page = new Page();
			bind(request, page);
			page.setPageNo(0);
			page.setRepair(0);
			page.setRemove(0);
			page.setAccountId(CurrentUser.get().getLoginAccount());
			page.setSysFlag(false);

			List<Page> pageList = null;
			if ("salary".equals(page.getModelName())) {
				pageList = this.userDefinedManager.getPageByModelName("salary", false, true);
			} else {
				pageList = this.userDefinedManager.getPageByModelName("staff", false, true);
			}
			if (CollectionUtils.isNotEmpty(pageList)) {
				Page topPage = (Page) pageList.get(pageList.size() - 1);
				page.setSort(topPage.getSort() + 1);
			}
			this.userDefinedManager.addPage(page);

			List<Locale> languages = LocaleContext.getAllLocales();

			boolean isGovVersion = true;
			if ((isGovVersion) && (!languages.contains(Locale.ENGLISH.getLanguage()))) {
				languages.add(new Locale("en"));
			}
			x = 0;
			int y = 0;
			for (Locale language : languages) {
				PageLabel pageLabel = new PageLabel();
				pageLabel.setPage_id(page.getId());
				if ((language.getLanguage().equals(Locale.CHINESE.getLanguage())) && (x < 1)) {
					pageLabel.setLanguage("zh_CN");
					pageLabel.setPageLabelValue(page.getPageName());
					this.userDefinedManager.addPageLabel(pageLabel);
					x++;
				}
				if ((language.getLanguage().equals(Locale.ENGLISH.getLanguage())) && (y < 1)) {
					pageLabel_en = request.getParameter("pageLabel_en");
					pageLabel.setLanguage("en");
					pageLabel.setPageLabelValue(pageLabel_en);
					this.userDefinedManager.addPageLabel(pageLabel);
					y++;
				}
			}
		} else {
			Long page_id = Long.valueOf(Long.parseLong(request.getParameter("page_id")));
			if (isSameName(request, "Page", page_id)) {
				super.rendJavaScript(response, "parent.isSameName();");
				return null;
			}
			page = this.userDefinedManager.getPageById(page_id);
			bind(request, page);
			this.userDefinedManager.updatePage(page);

			List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(page_id);
			for (PageLabel pageLabel : pageLabels) {
				if (pageLabel.getLanguage().equals("zh_CN")) {
					pageLabel.setPageLabelValue(page.getPageName());
					this.userDefinedManager.updatePageLabel(pageLabel);
				} else if (pageLabel.getLanguage().equals("en")) {
					pageLabel_en = request.getParameter("pageLabel_en");
					if (pageLabel_en != null) {
						pageLabel.setPageLabelValue(pageLabel_en);
						this.userDefinedManager.updatePageLabel(pageLabel);
					}
				}
			}
			this.userDefinedManager.deletePageProperties(page_id);
		}
		List<Long> pIds = CommonTools.parseStr2Ids(request, "pIds");
		if (pIds != null) {
			List<PageProperties> pagePropertiesList = new ArrayList<PageProperties>();
			int i = 0;
			for (Long property_id : pIds) {
				PageProperty property = this.userDefinedManager.getPropertyById(property_id);
				PageProperties pageProperties = new PageProperties();
				pageProperties.setIdIfNew();
				pageProperties.setPage(page);
				pageProperties.setPageProperty(property);
				pageProperties.setProperty_ordering(i++);
				pagePropertiesList.add(pageProperties);
			}
			this.userDefinedManager.addAllProperties(pagePropertiesList);
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType,
				"parent.parent");
	}

	public ModelAndView destroyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = 1;
		String str = request.getParameter("settingType");
		if (str != null) {
			settingType = Integer.parseInt(str);
		}
		int save = Integer.parseInt(request.getParameter("save"));
		List<Long> pageIds = CommonTools.parseStr2Ids(request, "pIds");
		if (save == 1) {
			this.userDefinedManager.deletePage(pageIds);
			for (Long page_id : pageIds) {
				this.userDefinedManager.deleteRespository(page_id);
			}
		} else {
			for (Long page_id : pageIds) {
				Page page = this.userDefinedManager.getPageById(page_id);
				page.setRemove(1);
				this.userDefinedManager.updatePage(page);
				this.userDefinedManager.deletePageProperties(page_id);
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}

	public ModelAndView changeCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("categoryId");
		List<PageProperty> properties = new ArrayList<PageProperty>();
		if ((category != null) && (!category.equals(""))) {
			Long categoryId = Long.valueOf(category);
			properties = this.userDefinedManager.findUnUsePropertyByCategoryId(categoryId, 0);
		}
		boolean isAjax = ServletRequestUtils.getRequiredBooleanParameter(request, "ajax");

		JSONArray jsonArray = new JSONArray();
		for (PageProperty property : properties) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("optionValue", property.getId().toString());
			jsonObject.putOpt("optionDisplay", Functions.toHTML(property.getName()));
			jsonArray.put(jsonObject);
		}
		String view = null;
		if (isAjax) {
			view = getJsonView();
		}
		return new ModelAndView(view, "json", jsonArray);
	}

	public ModelAndView isEmptyOfCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isAjax = ServletRequestUtils.getRequiredBooleanParameter(request, "ajax");
		List<Long> categoryIds = CommonTools.parseStr2Ids(request, "cIds");
		JSONObject jsonObject = new JSONObject();
		boolean isEmpty = true;
		for (Long category_id : categoryIds) {
			if (this.userDefinedManager.getPropertyByCategoryId(category_id, 0).size() > 0) {
				PropertyCategory category = this.userDefinedManager.getCategoryById(category_id);
				jsonObject.putOpt("categoryId", category_id);
				jsonObject.putOpt("categoryName", category.getName());
				isEmpty = false;
				jsonObject.putOpt("isEmpty", Boolean.valueOf(isEmpty));
				break;
			}
		}
		if (isEmpty) {
			jsonObject.putOpt("isEmpty", Boolean.valueOf(isEmpty));
		}
		String view = null;
		if (isAjax) {
			view = getJsonView();
		}
		return new ModelAndView(view, "json", jsonObject);
	}

	public ModelAndView pageOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Page> pages = this.userDefinedManager.getPageByModelName("salary", false, true);
		return new ModelAndView("hr/userDefined/pageOrder", "pages", pages);
	}

	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String str = request.getParameter("pageIds");
		if (str != null) {
			String[] strs = str.split("=");
			this.userDefinedManager.updatePageOrder(strs);
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=3", "parent");
	}

	private boolean isSameName(HttpServletRequest request, String type, Long id) {
		if ("PropertyCategory".equals(type)) {
			String categoryName = request.getParameter("name").trim();
			List<PropertyCategory> categorys = this.userDefinedManager.getCategorysByRemove(0);
			for (PropertyCategory category : categorys) {
				if ((!category.getId().equals(id)) && (category.getName().equals(categoryName))) {
					return true;
				}
			}
		} else if ("PageProperty".equals(type)) {
			String propertyName = request.getParameter("propertyName").trim();
			List<PageProperty> properties = this.userDefinedManager.getPropertyByRemove(0);
			for (PageProperty property : properties) {
				if ((!property.getId().equals(id)) && (property.getName().equals(propertyName))) {
					return true;
				}
			}
		} else if ("Page".equals(type)) {
			String pageName = request.getParameter("pageName").trim();
			String modelName = request.getParameter("modelName");
			List<Page> pages = this.userDefinedManager.getPageByModelName(modelName, false, true);
			for (Page page : pages) {
				if ((!page.getId().equals(id)) && (page.getPageName().equals(pageName))) {
					return true;
				}
			}
		}
		return false;
	}
}
