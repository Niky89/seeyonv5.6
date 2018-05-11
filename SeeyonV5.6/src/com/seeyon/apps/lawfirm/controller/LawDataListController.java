package com.seeyon.apps.lawfirm.controller;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.doc.manager.ContentTypeManager;
import com.seeyon.apps.doc.manager.DocAclManager;
import com.seeyon.apps.doc.manager.DocAclNewManager;
import com.seeyon.apps.doc.manager.DocHierarchyManager;
import com.seeyon.apps.doc.manager.DocLibManager;
import com.seeyon.apps.doc.manager.DocMetadataManager;
import com.seeyon.apps.doc.manager.DocMimeTypeManager;
import com.seeyon.apps.doc.manager.KnowledgeFavoriteManager;
import com.seeyon.apps.lawfirm.manager.LawDataListManager;
import com.seeyon.apps.lawfirm.manager.LawFormHelper;
import com.seeyon.apps.lawfirm.util.CommonUtil;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.content.mainbody.CtpContentAllBean;
import com.seeyon.ctp.common.content.mainbody.MainbodyManager;
import com.seeyon.ctp.common.content.mainbody.MainbodyType;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.ctpenum.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.DataContainer;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.common.po.template.CtpTemplate;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormAuthViewBean;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormBindAuthBean;
import com.seeyon.ctp.form.bean.FormBindBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormFieldComBean;
import com.seeyon.ctp.form.bean.FormQueryBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.bean.FormViewBean;
import com.seeyon.ctp.form.bean.SimpleObjectBean;
import com.seeyon.ctp.form.formreport.bo.FormReportBean;
import com.seeyon.ctp.form.formreport.manager.FormReportViewManager;
import com.seeyon.ctp.form.formreport.manager.ReportSaveManager;
import com.seeyon.ctp.form.modules.component.ComponentManager;
import com.seeyon.ctp.form.modules.engin.formula.FormulaEnums;
import com.seeyon.ctp.form.modules.query.FormQueryResultManager;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.form.service.FormService;
import com.seeyon.ctp.form.util.Enums;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.report.access.manager.AccessManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.ReqUtil;

import edu.emory.mathcs.backport.java.util.Arrays;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class LawDataListController extends BaseController {
	private static final Log log = LogFactory.getLog(LawDataListController.class);
	private final String preUrl = "apps/report/form/queryReport/";
	private FormCacheManager formCacheManager;
	private MainbodyManager ctpMainbodyManager;
	private FormManager formManager;
	private FormReportViewManager formReportViewManager;
	private AccessManager reportAccessManager;
	private ReportSaveManager reportSaveManager;
	private DocLibManager docLibManager;
	private DocHierarchyManager docHierarchyManager;
	private DocMimeTypeManager docMimeTypeManager;
	private DocAclManager docAclManager;
	private KnowledgeFavoriteManager knowledgeFavoriteManager;
	private ContentTypeManager contentTypeManager;
	private SpaceManager spaceManager;
	private DocMetadataManager docMetadataManager;
	private FileManager fileManager;
	private ColManager colManager;
	private OrgManager orgManager;
	private EnumManager enumManager;
	private DocAclNewManager docAclNewManager;

	public KnowledgeFavoriteManager getKnowledgeFavoriteManager() {
		return this.knowledgeFavoriteManager;
	}

	public void setKnowledgeFavoriteManager(KnowledgeFavoriteManager knowledgeFavoriteManager) {
		this.knowledgeFavoriteManager = knowledgeFavoriteManager;
	}

	public ContentTypeManager getContentTypeManager() {
		return this.contentTypeManager;
	}

	public void setContentTypeManager(ContentTypeManager contentTypeManager) {
		this.contentTypeManager = contentTypeManager;
	}

	public SpaceManager getSpaceManager() {
		return this.spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public DocMetadataManager getDocMetadataManager() {
		return this.docMetadataManager;
	}

	public void setDocMetadataManager(DocMetadataManager docMetadataManager) {
		this.docMetadataManager = docMetadataManager;
	}

	public FileManager getFileManager() {
		return this.fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public ColManager getColManager() {
		return this.colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public OrgManager getOrgManager() {
		return this.orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public EnumManager getEnumManager() {
		return this.enumManager;
	}

	public void setEnumManager(EnumManager enumManager) {
		this.enumManager = enumManager;
	}

	public DocAclNewManager getDocAclNewManager() {
		return this.docAclNewManager;
	}

	public void setDocAclNewManager(DocAclNewManager docAclNewManager) {
		this.docAclNewManager = docAclNewManager;
	}

	public DocMimeTypeManager getDocMimeTypeManager() {
		return this.docMimeTypeManager;
	}

	public void setDocMimeTypeManager(DocMimeTypeManager docMimeTypeManager) {
		this.docMimeTypeManager = docMimeTypeManager;
	}

	public DocAclManager getDocAclManager() {
		return this.docAclManager;
	}

	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}

	public DocHierarchyManager getDocHierarchyManager() {
		return this.docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public DocLibManager getDocLibManager() {
		return this.docLibManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	public FormManager getFormManager() {
		return this.formManager;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public FormReportViewManager getFormReportViewManager() {
		return this.formReportViewManager;
	}

	public void setFormReportViewManager(FormReportViewManager formReportViewManager) {
		this.formReportViewManager = formReportViewManager;
	}

	public AccessManager getReportAccessManager() {
		return this.reportAccessManager;
	}

	public void setReportAccessManager(AccessManager reportAccessManager) {
		this.reportAccessManager = reportAccessManager;
	}

	public ReportSaveManager getReportSaveManager() {
		return this.reportSaveManager;
	}

	public void setReportSaveManager(ReportSaveManager reportSaveManager) {
		this.reportSaveManager = reportSaveManager;
	}

	public FormCacheManager getFormCacheManager() {
		return this.formCacheManager;
	}

	public void setFormCacheManager(FormCacheManager formCacheManager) {
		this.formCacheManager = formCacheManager;
	}

	public MainbodyManager getCtpMainbodyManager() {
		return this.ctpMainbodyManager;
	}

	public void setCtpMainbodyManager(MainbodyManager ctpMainbodyManager) {
		this.ctpMainbodyManager = ctpMainbodyManager;
	}

	public ModelAndView queryLawDataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plugin/lawfirm/lawDataList");
		String formIds = ReqUtil.getString(request, "formId");
		String givenPerson = ReqUtil.getString(request, "givenPerson");
		String otherPerson = ReqUtil.getString(request, "otherPerson");
		mav.addObject("givenPerson", givenPerson);
		mav.addObject("formId", formIds);
		mav.addObject("otherPerson", otherPerson);
		FormBean formBean = null;
		formBean = this.formCacheManager.getForm(Long.parseLong(formIds));
		mav.addObject("moduleType",

				Integer.valueOf(formBean.getFormType() == Enums.FormType.baseInfo.getKey()
						? ModuleType.unflowBasic.getKey() : ModuleType.unflowInfo.getKey()));

		String rightId = "";
		for (FormViewBean tempFormViewBean : formBean.getFormViewList()) {
			if (!"案件收支".equals(tempFormViewBean.getFormViewName())) {
				List<FormAuthViewBean> favbs = tempFormViewBean
						.getFormAuthViewBeanListByType(Enums.FormAuthorizationType.show);
				if (favbs.size() > 0) {
					rightId = StringHelper.concat(rightId,
							tempFormViewBean.getId() + "." + ((FormAuthViewBean) favbs.get(0)).getId(), "|");
				}
			}
		}
		mav.addObject("firstRightId", rightId);

		LawDataListManager lawDataListManager = (LawDataListManager) AppContext.getBean("lawDataListManager");
		Object param = new HashMap();
		((Map) param).put("formId", formIds);
		((Map) param).put("givenPerson", givenPerson);
		((Map) param).put("otherPerson", otherPerson);
		FlipInfo flipInfo = lawDataListManager.queryLawDataList(new FlipInfo(), (Map) param);
		flipInfo.setParams((Map) param);
		request.setAttribute("ffmytable", flipInfo);
		return mav;
	}

	public ModelAndView getFormMasterDataList(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		ModelAndView view = new ModelAndView("plugin/lawfirm/journalLawDataList");
		Map<String, String[]> params = request.getParameterMap();

		Long formId = ParamUtil.getLong(params, "formId");
		String lawCode = ReqUtil.getString(request, "lawCode");
		String lawformId = ReqUtil.getString(request, "lawformId");
		String formtype = ReqUtil.getString(request, "formtype");
		if (formtype.equalsIgnoreCase("new")) {
			view = new ModelAndView("plugin/lawfirm/newjournalLawDataList");
		}
		view.addObject("lawCode", lawCode);
		view.addObject("lawformId", lawformId);
		FormBean cloneForm = null;
		FormBean formBean = this.formCacheManager.getForm(formId.longValue());
		long userId = AppContext.currentUserId();
		long formTemplateId = ((FormBindAuthBean) formBean.getBind().getUnflowFormBindAuthByUserId(Long.valueOf(userId))
				.get(0)).getId().longValue();
		view.addObject("formType", Integer.valueOf(formBean.getFormType()));
		String type = request.getParameter("type") == null ? "baseInfo" : request.getParameter("type");
		try {
			cloneForm = (FormBean) formBean.clone();
			cloneForm.getFormViewList().clear();
			cloneForm.setTriggerConfigMap(null);
			cloneForm.setBind(null);
			List<FormTableBean> tables = cloneForm.getTableList();
			for (FormTableBean table : tables) {
				table.delAllFormField();
			}
		} catch (CloneNotSupportedException e) {
			log.error(e.getMessage(), e);
		}
		List<FormFieldBean> searchFieldList = new ArrayList();
		Object localObject1;
		String toRelationAttr;
		if (type.equals("baseInfo")) {
			view.addObject("formTemplateId", Long.valueOf(formTemplateId));
			FormBindAuthBean bindAuthBean = formBean.getBind().getFormBindAuthBean(String.valueOf(formTemplateId));
			if (bindAuthBean != null) {
				if (!bindAuthBean.checkRight(AppContext.currentUserId())) {
					throw new BusinessException(ResourceUtil.getString("form.showAppFormData.noright"));
				}
				view.addObject("templateName", bindAuthBean.getName());
				Object searchFields = bindAuthBean.getSearchFieldList();
				List<SimpleObjectBean> showFields = bindAuthBean.getShowFieldList();
				List<SimpleObjectBean> orderByFields = bindAuthBean.getOrderByList();
				if (formBean.getFormType() == Enums.FormType.baseInfo.getKey()) {
					FormViewBean tempFormViewBean = (FormViewBean) formBean.getFormViewList().get(0);
					List<FormAuthViewBean> addAuths = tempFormViewBean
							.getFormAuthViewBeanListByType(Enums.FormAuthorizationType.add);
					view.addObject("newFormAuth",
							tempFormViewBean.getId() + "." + ((FormAuthViewBean) addAuths.get(0)).getId());

					view.addObject("editAuth",
							tempFormViewBean.getId() + "." + ((FormAuthViewBean) addAuths.get(0)).getId());
					List<FormAuthViewBean> formShowAuths = tempFormViewBean
							.getFormAuthViewBeanListByType(Enums.FormAuthorizationType.show);

					view.addObject("firstRightId", ((FormAuthViewBean) formShowAuths.get(0)).getId());
				} else {
					String newauth = bindAuthBean.getNewFormAuth();
					view.addObject("newFormAuth", bindAuthBean.getNewFormAuth());

					String editauth = bindAuthBean.getUpdateFormAuth();
					view.addObject("editAuth", bindAuthBean.getUpdateFormAuth());

					view.addObject("chooseAuth", "false");
					if ((newauth != null) && (newauth.split("[.]").length > 1) && (editauth.split("[.]").length > 1)) {
						FormAuthViewBean newAuth = formBean
								.getAuthViewBeanById(Long.valueOf(Long.parseLong(newauth.split("[.]")[1])));
						FormAuthViewBean editAuth = formBean
								.getAuthViewBeanById(Long.valueOf(Long.parseLong(editauth.split("[.]")[1])));
						if ((newAuth.getConditionFormAuthViewBeanList().size() > 0) ||

								(editAuth.getConditionFormAuthViewBeanList().size() > 0)) {
							view.addObject("chooseAuth", "true");
						}
					}
					String showAuth = bindAuthBean.getShowFormAuth();
					if ((showAuth != null) && (!showAuth.trim().equals(""))) {
						if (showAuth.substring(showAuth.length() - 1, showAuth.length()).equals("|")) {
							showAuth = showAuth.substring(0, showAuth.length() - 1);
						}
						List<String> showAuths = new ArrayList();
						String[] showAuthStrs = showAuth.split("[|]");
						int j = (showAuthStrs).length;
						localObject1 = java.util.Arrays.asList(showAuthStrs).iterator();
						for (int i = 0; i < j; i++) {
							String str = showAuthStrs[i].toString();
							showAuths.add(str);
						}
						view.addObject("showAuth", showAuths);
					}
					view.addObject("firstRightId", showAuth);
				}
				view.addObject("showFields", getTheadStr(showFields, formBean));

				view.addObject("sortStr", getSortStr(orderByFields));

				searchFieldList = formBean.getFormType() == Enums.FormType.baseInfo.getKey()
						? getSearchField(showFields, formBean) : getSearchField((List) searchFields, formBean);

				List<SimpleObjectBean> authList = bindAuthBean.getAuthList();
				view.addObject("authList", authList);
				List<FormQueryBean> queryList = formBean.getFormQueryList();
				List<FormQueryBean> queryList2 = new ArrayList();
				for (int i = 0; i < queryList.size(); i++) {
					if (FormService.checkRight(Enums.FormModuleAuthModuleType.Query,
							((FormQueryBean) queryList.get(i)).getId().longValue(),
							Long.valueOf(AppContext.currentUserId()), formId)) {
						queryList2.add((FormQueryBean) queryList.get(i));
					}
				}
				view.addObject("queryList", queryList2);
				List<FormReportBean> reportList = formBean.getFormReportList();
				List<FormReportBean> reportList2 = new ArrayList();
				for (int i = 0; i < reportList.size(); i++) {
					if (FormService.checkRight(Enums.FormModuleAuthModuleType.Report,
							((FormReportBean) reportList.get(i)).getReportDefinition().getId().longValue(),
							Long.valueOf(AppContext.currentUserId()), formId)) {
						reportList2.add((FormReportBean) reportList.get(i));
					}
				}
				view.addObject("reportList", reportList2);
			}
		} else if (type.equals("formRelation")) {
			FormBindBean bindBean = formBean.getBind();
			Object bindAuthList = null;
			if (formBean.getFormType() == Enums.FormType.baseInfo.getKey()) {
				bindAuthList = new ArrayList();
				((List) bindAuthList)
						.add((FormBindAuthBean) bindBean.getUnFlowTemplateMap().values().iterator().next());
			} else {
				bindAuthList = bindBean.getUnflowFormBindAuthByUserId(Long.valueOf(AppContext.currentUserId()));
			}
			Long fromFormId = ParamUtil.getLong(params, "fromFormId");
			String fromRelationAttr = ParamUtil.getString(params, "fromRelationAttr");
			toRelationAttr = ParamUtil.getString(params, "toRelationAttr");
			FormBean fromFormBean = this.formCacheManager.getForm(fromFormId.longValue());
			FormFieldBean fromFieldBean = fromFormBean.getFieldBeanByName(fromRelationAttr);
			FormFieldBean toFieldBean = formBean.getFieldBeanByName(toRelationAttr);
			String fromFieldType = fromFieldBean.isMasterField() ? "m" : "s";
			String toFieldType = toFieldBean.isMasterField() ? "m" : "s";
			view.addObject("relationInitParam", fromFieldType + toFieldType);
			if (((List) bindAuthList).size() > 0) {
				int i = 0;
				for (localObject1 = ((List) bindAuthList).iterator(); ((Iterator) localObject1).hasNext();) {
					FormBindAuthBean template = (FormBindAuthBean) ((Iterator) localObject1).next();
					if (i == 0) {
						i++;
						List<SimpleObjectBean> showFields = template.getShowFieldList();

						view.addObject("showFields", getTheadStr(showFields, formBean));
						List<SimpleObjectBean> orderByFields = template.getOrderByList();
						view.addObject("sortStr", getSortStr(orderByFields));

						searchFieldList = getSearchField(showFields, formBean);
						if (formBean.getFormType() == Enums.FormType.baseInfo.getKey()) {
							view.addObject("firstRightId",

									((FormAuthViewBean) ((FormViewBean) formBean.getFormViewList().get(0))
											.getFormAuthViewBeanListByType(Enums.FormAuthorizationType.show).get(0))
													.getId());
						} else {
							String showAuth = template.getShowFormAuth();
							if ((showAuth != null) && (!showAuth.trim().equals(""))) {
								if (showAuth.substring(showAuth.length() - 1, showAuth.length()).equals("|")) {
									showAuth = showAuth.substring(0, showAuth.length() - 1);
								}
							}
							List<String> showAuths = new ArrayList();
							String[] showAuthStrs = showAuth.split("[|]");
							String[] arrayOfString1;
							int m = (arrayOfString1 = showAuthStrs).length;
							for (int k = 0; k < m; k++) {
								String str = arrayOfString1[k];
								showAuths.add(str);
							}
							view.addObject("showAuth", showAuths);
							view.addObject("firstRightId", showAuth);
						}
					}
				}
			} else {
				throw new BusinessException(ResourceUtil.getString("form.showAppFormData.noright"));
			}
			view.addObject("templateName", ParamUtil.getString(params, "templateName"));
		}
		view.addObject("searchFields", searchFieldList);
		List<DataContainer> commonSearchFields = new ArrayList();
		if (searchFieldList.size() > 0) {
			ComponentManager componentManager = (ComponentManager) AppContext.getBean("componentManager");
			for (FormFieldBean fb : searchFieldList) {
				fb = fb.findRealFieldBean();
				DataContainer o = new DataContainer();
				commonSearchFields.add(o);
				o.put("id", fb.getName());
				o.put("name", fb.getName());
				o.put("value", fb.getName());
				o.put("text", fb.getDisplay());
				o.put("type", "datemulti");
				if (fb.getInputTypeEnum() == FormFieldComBean.FormFieldComEnum.EXTEND_DATETIME) {
					if ((fb.getName().equalsIgnoreCase(Enums.MasterTableField.start_date.getKey()))
							|| (fb.getName().equalsIgnoreCase(Enums.MasterTableField.modify_date.getKey()))) {
						o.put("dateTime", Boolean.valueOf(false));
						o.put("ifFormat", "%Y-%m-%d");
					} else {
						o.put("dateTime", Boolean.valueOf(true));
						o.put("minuteStep", Integer.valueOf(1));
					}
				} else if (fb.getInputTypeEnum() == FormFieldComBean.FormFieldComEnum.EXTEND_DATE) {
					o.put("dateTime", Boolean.valueOf(false));
					o.put("ifFormat", "%Y-%m-%d");
				} else if (fb.getInputTypeEnum() == FormFieldComBean.FormFieldComEnum.RADIO) {
					o.put("type", "customPanel");
					o.put("readonly", "readonly");
					o.put("panelWidth", Integer.valueOf(270));
					o.put("panelHeight", Integer.valueOf(200));
					List<String[]> htmls = componentManager.getFormConditionHTML(formId,
							Arrays.asList(new String[] { fb.getName() }), null, null);
					String op = ((String[]) htmls.get(0))[0].replace("<select", "<select style='display:none;' ");
					o.put("customHtml", op + ((String[]) htmls.get(0))[1]);
				} else {
					o.put("type", "custom");
					List<String[]> htmls = componentManager.getFormConditionHTML(formId,
							Arrays.asList(new String[] { fb.getName() }), null, null);
					String op = ((String[]) htmls.get(0))[0].replace("<select", "<select style='display:none;' ");
					o.put("customHtml", op + ((String[]) htmls.get(0))[1]);
				}
			}
		}
		DataContainer dc = new DataContainer();
		dc.add("commonSearchFields", commonSearchFields);
		view.addObject("commonSearchFields", dc.getJson());
		view.addObject("moduleType", Integer.valueOf(formBean.getFormType() == Enums.FormType.baseInfo.getKey()
				? ModuleType.unflowBasic.getKey() : ModuleType.unflowInfo.getKey()));
		view.addObject("formId", formBean.getId());
		view.addObject("toFormBean", cloneForm.toJSON());
		view.addObject("type", type);
		view.addObject("formType", Integer.valueOf(formBean.getFormType()));
		view.addObject("currentUserId", Long.valueOf(AppContext.currentUserId()));

		LawDataListManager lawDataListManager = (LawDataListManager) AppContext.getBean("lawDataListManager");
		Map param = new HashMap();
		param.put("formId", String.valueOf(formId));
		param.put("lawCode", lawCode);
		FlipInfo flipInfo = null;
		if (formtype.equalsIgnoreCase("new")) {
			flipInfo=lawDataListManager.queryNewJournalDataList(new FlipInfo(), param);
		} else {
			flipInfo=lawDataListManager.queryJournalDataList(new FlipInfo(), param);
		}
		flipInfo.setParams(param);
		request.setAttribute("ffmytable", flipInfo);

		if (formtype.equalsIgnoreCase("new")) {
			view.addObject("isZbls", false);
		} else {
			String lawformRecordId = ReqUtil.getString(request, "lawformRecordId");
			Boolean isZbls = LawFormHelper.isZbls(Long.valueOf(lawformId), Long.valueOf(lawformRecordId));
			view.addObject("isZbls", isZbls);
		}
		FormBean gzrzFormBean = this.formCacheManager.getForm(formId.longValue());
		Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(gzrzFormBean);

		String ajbhFieldName = ((FormFieldBean) formFieldMap.get("案件编号")).getName();
		view.addObject("ajbhFieldName", ajbhFieldName);

		return view;
	}

	public ModelAndView documentRegistration(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		ModelAndView view = new ModelAndView("plugin/lawfirm/documentRegistration");
		view.addObject("lawCode", request.getParameter("lawCode"));

		List<CtpTemplate> templates = new ArrayList();
		TemplateManager templeteManager = (TemplateManager) AppContext.getBean("templateManager");
		String documentRegistrationRelatedFlow = AppContext
				.getSystemProperty("lawfirm.documentRegistrationRelatedFlow");
		String[] arrayOfString;
		int j = (arrayOfString = documentRegistrationRelatedFlow.split(",")).length;
		for (int i = 0; i < j; i++) {
			String templateNo = arrayOfString[i];
			if (!StringHelper.isNullOrEmpty(templateNo).booleanValue()) {
				CtpTemplate template = templeteManager.getTempleteByTemplateNumber(templateNo);
				if (template != null) {
					templates.add(template);
				}
			}
		}
		view.addObject("templates", templates);

		return view;
	}

	public ModelAndView agreementToSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		ModelAndView view = new ModelAndView("plugin/lawfirm/agreementToSign");
		view.addObject("lawCode", request.getParameter("lawCode"));

		List<CtpTemplate> templates = new ArrayList();
		TemplateManager templeteManager = (TemplateManager) AppContext.getBean("templateManager");
		String agreementToSignRelatedFlow = AppContext.getSystemProperty("lawfirm.agreementToSignRelatedFlow");
		String[] arrayOfString;
		int j = (arrayOfString = agreementToSignRelatedFlow.split(",")).length;
		for (int i = 0; i < j; i++) {
			String templateNo = arrayOfString[i];
			if (!StringHelper.isNullOrEmpty(templateNo).booleanValue()) {
				CtpTemplate template = templeteManager.getTempleteByTemplateNumber(templateNo);
				if (template != null) {
					templates.add(template);
				}
			}
		}
		view.addObject("templates", templates);

		return view;
	}

	private String getTheadStr(List<SimpleObjectBean> showFields, FormBean formBean) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		int i = 0;

		String display = "";
		String fieldName = "";
		for (SimpleObjectBean showField : showFields) {
			if (!"".equals(showField.getName())) {
				String[] strs = showField.getName().split("[.]");
				if (strs.length == 1) {
					fieldName = strs[0];
					display = FormulaEnums.SystemDataField.getEnumByKey(fieldName).getText();
					fieldName = fieldName.replaceAll("_", "");
				} else {
					fieldName = strs[1];
					FormFieldBean field = formBean.getFieldBeanByName(fieldName);
					if (field != null) {
						display = field.getDisplay();
					} else {
						display = showField.getName();
					}
				}
				if (showField.getValue().indexOf("(") != -1) {
					display = showField.getValue().substring(showField.getValue().indexOf("(") + 1,
							showField.getValue().indexOf(")"));
				} else {
					display = showField.getValue();
				}
				sb.append("{");
				sb.append("\"").append("display\":\"").append(display).append("\",");
				sb.append("\"").append("name\":\"").append(fieldName).append("\",");
				sb.append("\"").append("sortable\":\"").append("false").append("\",");
				sb.append("\"").append("align\":\"").append("center").append("\"");
				if (i == showFields.size() - 1) {
					sb.append("}");
				} else {
					sb.append("},");
				}
				i++;
			}
		}
		sb.append("]");
		return sb.toString();
	}

	private List<FormFieldBean> getSearchField(List<SimpleObjectBean> showFields, FormBean formBean) {
		List<FormFieldBean> list = new ArrayList();
		FormFieldBean field = null;
		for (SimpleObjectBean showField : showFields) {
			String display = showField.getValue();
			int index1 = display.indexOf("(");
			if (index1 != -1) {
				display = display.substring(index1 + 1);
			}
			int index2 = display.indexOf(")");
			if (index2 != -1) {
				display = display.substring(0, index2);
			}
			if (showField.getName().indexOf(".") > -1) {
				String[] strs = showField.getName().split("[.]");
				field = formBean.getFieldBeanByName(strs[1]);
				if ((field == null) || (field.getInputTypeEnum() == null)) {
					Enums.MasterTableField mtf = Enums.MasterTableField.getEnumByKey(strs[1]);
					if (mtf == null) {
						continue;
					}
					field = mtf.getFormFieldBean();
				}
				if (field.getInputTypeEnum().isMultiOrg()) {
					continue;
				}
			} else {
				Enums.MasterTableField mtf = Enums.MasterTableField.getEnumByKey(showField.getName());
				if (mtf == null) {
					continue;
				}
				field = mtf.getFormFieldBean();
			}
			try {
				field = (FormFieldBean) field.clone();
			} catch (CloneNotSupportedException e) {
				log.error("克隆失败", e);
			}
			field.setDisplay(display);
			list.add(field);
		}
		return list;
	}

	private String getSortStr(List<SimpleObjectBean> orderByFields) {
		String sortStr = "";
		if ((orderByFields != null) && (orderByFields.size() > 0)) {
			for (SimpleObjectBean orderField : orderByFields) {
				String[] strs = orderField.getName().split("[.]");
				if (strs.length < 2) {
					strs = new String[] { strs[0], orderField.getName() };
				}
				if (sortStr.trim().equals("")) {
					sortStr = strs[1] + " " + orderField.getValue();
				} else {
					sortStr = sortStr + "," + strs[1] + " " + orderField.getValue();
				}
			}
		}
		return sortStr;
	}

	public ModelAndView newUnFlowFormData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		ModelAndView view = new ModelAndView("plugin/lawfirm/newUnFlowFormData");
		Map<String, String[]> params = request.getParameterMap();
		Long contentAllId = ParamUtil.getLong(params, "contentAllId");
		String rightId = ParamUtil.getString(params, "rightId");
		Long viewId = ParamUtil.getLong(params, "viewId");
		Long formId = ParamUtil.getLong(params, "formId");
		Long formTemplateId = ParamUtil.getLong(params, "formTemplateId");
		String moduleType = ParamUtil.getString(params, "moduleType");
		String isNew = ParamUtil.getString(params, "isNew");
		String lawformId = ParamUtil.getString(params, "lawformId");
		String lawCode = ParamUtil.getString(params, "lawCode");
		view.addObject("contentAllId", contentAllId);
		view.addObject("rightId", rightId);
		view.addObject("viewId", viewId);
		view.addObject("formId", formId);
		view.addObject("formTemplateId", formTemplateId);
		view.addObject("moduleType", moduleType);
		view.addObject("isNew", isNew);
		view.addObject("lawformId", lawformId);
		view.addObject("lawCode", lawCode);
		return view;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView content = new ModelAndView("plugin/lawfirm/content");
		try {
			Map params = request.getParameterMap();
			String isNew = ParamUtil.getString(params, "isNew", false);
			long moduleId = ParamUtil.getLong(params, "moduleId", Long.valueOf(-1L), true).longValue();

			int moduleType = ParamUtil.getInt(params, "moduleType", true).intValue();
			ModuleType mType = ModuleType.getEnumByKey(moduleType);
			if (mType == null) {
				throw new BusinessException("moduleType is not validate!");
			}
			String lawCodes = "";
			String lawNames = "";
			String customName = "";
			String lawformId = ParamUtil.getString(params, "lawformId");
			String lawCode = ParamUtil.getString(params, "lawCode");

			FormCacheManager formCacheManager = (FormCacheManager) AppContext.getBean("formCacheManager");
			FormBean flowFormBean = formCacheManager.getForm(Long.valueOf(lawformId).longValue());
			Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);

			String[] fields = { ((FormFieldBean) flowFieldMap.get("公用-案件编号")).getName(),
					((FormFieldBean) flowFieldMap.get("公用-案件名称")).getName() };

			List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(Long.parseLong(lawformId),
					fields, 0, 99999, "", "");
			for (int i = 0; i < formDataMasterBean.size(); i++) {
				lawCodes = (String) ((FormDataMasterBean) formDataMasterBean.get(i)).getAllDataMap().get(fields[0]);
				if ((lawCodes != null) && (lawCodes.equals(lawCode))) {
					lawNames = (String) ((FormDataMasterBean) formDataMasterBean.get(i)).getFieldValue(fields[1]);

					break;
				}
			}
			content.addObject("lawCodes", lawCodes);
			content.addObject("lawNames", lawNames);
			content.addObject("customName", customName);

			Long formId = ParamUtil.getLong(params, "formId");
			FormBean gzrzFormBean = formCacheManager.getForm(formId.longValue());
			Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(gzrzFormBean);

			String ajbhFieldName = ((FormFieldBean) formFieldMap.get("案件编号")).getName();
			String ajmcFieldName = ((FormFieldBean) formFieldMap.get("案件名称")).getName();
			String kssjFieldName = ((FormFieldBean) formFieldMap.get("开始时间")).getName();
			String jssjFieldName = ((FormFieldBean) formFieldMap.get("结束时间")).getName();
			String gzsjFieldName = ((FormFieldBean) formFieldMap.get("工作时间")).getName();

			content.addObject("ajbhFieldName", ajbhFieldName);
			content.addObject("ajmcFieldName", ajmcFieldName);
			content.addObject("kssjFieldName", kssjFieldName);
			content.addObject("jssjFieldName", jssjFieldName);
			content.addObject("gzsjFieldName", gzsjFieldName);

			String rightId = ParamUtil.getString(params, "rightId", "", false);

			Integer indexParam = ParamUtil.getInt(params, "indexParam", 0);

			int viewState = ParamUtil.getInt(params, "viewState", 2).intValue();

			Long fromCopy = ParamUtil.getLong(params, "fromCopy", Long.valueOf(-1L), false);

			List contentList = null;
			CtpContentAllBean contentAll = null;
			if ((isNew == null) || ("false".equals(isNew.trim()))) {
				contentList = this.ctpMainbodyManager.transContentViewResponse(mType, Long.valueOf(moduleId),
						Integer.valueOf(viewState), rightId, indexParam, fromCopy);
				if ((contentList == null) || (contentList.size() == 0)) {
					throw new BusinessException("该正文不存在,moduleId=" + moduleId);
				}
				contentAll = (CtpContentAllBean) contentList.get(0);
			} else {
				int contentType = ParamUtil.getInt(params, "contentType", true).intValue();
				MainbodyType cType = MainbodyType.getEnumByKey(contentType);
				if (cType == null) {
					throw new BusinessException("contentType is not validate!");
				}
				contentAll = this.ctpMainbodyManager.transContentNewResponse(mType, Long.valueOf(moduleId), cType,
						rightId);
				contentList = new ArrayList();
				contentList.add(contentAll);
			}
			content.addObject("viewState", Integer.valueOf(viewState));
			content.addObject("contentList", contentList);
			content.addObject("formJson", contentAll.getExtraMap().get("formJson"));
			content.addObject("indexParam", indexParam);
		} catch (BusinessException be) {
			log.error(be.getMessage(), be);
			throw be;
		}
		return content;
	}

	public ModelAndView exporttoExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		LawDataListManager lawDataListManager = (LawDataListManager) AppContext.getBean("lawDataListManager");
		Map params = request.getParameterMap();
		Long formId = ParamUtil.getLong(params, "formId");
		String lawCode = ReqUtil.getString(request, "lawCode");
		Map param = new HashMap();
		param.put("formId", String.valueOf(formId));
		param.put("lawCode", lawCode);
		FlipInfo flipInfo = new FlipInfo();
		flipInfo.setSize(99999);
		flipInfo = lawDataListManager.queryJournalDataList(flipInfo, param);
		flipInfo.setParams(param);

		int rowNo = 0;
		response.setContentType("application/x-msdownload; charset=UTF-8");
		String filedisplay = "工作日志.xls";

		filedisplay = new String(filedisplay.getBytes(), "iso8859-1");
		response.addHeader("Content-disposition", "attachment;filename=\"" + filedisplay + "\"");
		OutputStream os = response.getOutputStream();
		WritableWorkbook wf = Workbook.createWorkbook(os);
		WritableSheet ws_doc = wf.createSheet("工作日志", 0);
		ws_doc.addCell(new Label(0, rowNo, "案件名称"));
		ws_doc.addCell(new Label(1, rowNo, "客户名称"));
		ws_doc.addCell(new Label(2, rowNo, "工作描述"));
		ws_doc.addCell(new Label(3, rowNo, "工作时间"));
		ws_doc.addCell(new Label(4, rowNo, "记录人"));
		ws_doc.setColumnView(0, 40);
		ws_doc.setColumnView(1, 20);
		ws_doc.setColumnView(2, 20);
		ws_doc.setColumnView(3, 20);
		ws_doc.setColumnView(4, 20);
		for (Object obj : flipInfo.getData()) {
			Map map = (Map) obj;
			rowNo++;
			if (map.get("ajmc") != null) {
				ws_doc.addCell(new Label(0, rowNo, map.get("ajmc").toString()));
			}
			if (map.get("klmc") != null) {
				ws_doc.addCell(new Label(1, rowNo, map.get("klmc").toString()));
			}
			if (map.get("gzms") != null) {
				ws_doc.addCell(new Label(2, rowNo, map.get("gzms").toString()));
			}
			if (map.get("gzsj") != null) {
				ws_doc.addCell(new Label(3, rowNo, map.get("gzsj").toString()));
			}
			if (map.get("name") != null) {
				ws_doc.addCell(new Label(4, rowNo, map.get("name").toString()));
			}
		}
		wf.write();
		wf.close();
		os.flush();
		os.close();
		return null;
	}

	public ModelAndView exportQueryResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormQueryResultManager formQueryResultManager = (FormQueryResultManager) AppContext
				.getBean("formQueryResultManager");
		Map m = ParamUtil.getJsonParams();
		FlipInfo flipInfo = formQueryResultManager.getQueryResult(null, m, true);

		int rowNo = 0;
		response.setContentType("application/x-msdownload; charset=UTF-8");
		String filedisplay = "财务报表.xls";

		filedisplay = new String(filedisplay.getBytes(), "iso8859-1");
		response.addHeader("Content-disposition", "attachment;filename=\"" + filedisplay + "\"");
		OutputStream os = response.getOutputStream();
		WritableWorkbook wf = Workbook.createWorkbook(os);
		WritableSheet ws_doc = wf.createSheet("财务报表", 0);
		ws_doc.addCell(new Label(0, rowNo, "序号"));
		ws_doc.addCell(new Label(1, rowNo, "案件编号"));
		ws_doc.addCell(new Label(2, rowNo, "立案时间"));
		ws_doc.addCell(new Label(3, rowNo, "立案审查时间"));
		ws_doc.addCell(new Label(4, rowNo, "部门"));
		ws_doc.addCell(new Label(5, rowNo, "经办律师"));
		ws_doc.addCell(new Label(6, rowNo, "标的额"));
		ws_doc.addCell(new Label(7, rowNo, "委托人"));
		ws_doc.addCell(new Label(8, rowNo, "对方当事人"));
		ws_doc.addCell(new Label(9, rowNo, "应收代理费"));
		ws_doc.addCell(new Label(10, rowNo, "收费时间"));
		ws_doc.addCell(new Label(11, rowNo, "实收额"));
		ws_doc.addCell(new Label(12, rowNo, "差旅费"));
		ws_doc.addCell(new Label(13, rowNo, "代收费"));
		ws_doc.addCell(new Label(14, rowNo, "计收费"));
		ws_doc.addCell(new Label(15, rowNo, "案件类型（第二级）"));
		ws_doc.addCell(new Label(16, rowNo, "介绍方（案源人"));
		ws_doc.addCell(new Label(17, rowNo, "展业费比例（10%，20%，30%）"));
		ws_doc.addCell(new Label(18, rowNo, "展业费及使用人比例"));
		ws_doc.addCell(new Label(19, rowNo, "协议收费时间"));
		ws_doc.addCell(new Label(20, rowNo, "案件简况"));
		ws_doc.addCell(new Label(21, rowNo, "案件类型（第三级）"));

		ws_doc.setColumnView(0, 10);
		ws_doc.setColumnView(1, 20);
		ws_doc.setColumnView(2, 20);
		ws_doc.setColumnView(3, 20);
		ws_doc.setColumnView(4, 20);
		ws_doc.setColumnView(5, 20);
		ws_doc.setColumnView(6, 20);
		ws_doc.setColumnView(7, 20);
		ws_doc.setColumnView(8, 20);
		ws_doc.setColumnView(9, 20);
		ws_doc.setColumnView(10, 20);
		ws_doc.setColumnView(11, 20);
		ws_doc.setColumnView(12, 20);
		ws_doc.setColumnView(13, 20);
		ws_doc.setColumnView(14, 20);
		ws_doc.setColumnView(15, 20);
		ws_doc.setColumnView(16, 20);
		ws_doc.setColumnView(17, 20);
		ws_doc.setColumnView(18, 20);
		ws_doc.setColumnView(19, 20);
		ws_doc.setColumnView(20, 20);
		ws_doc.setColumnView(21, 20);

		String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
		long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
		FormCacheManager formCacheManager = (FormCacheManager) AppContext.getBean("formCacheManager");
		OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");

		FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
		FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
		String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
		Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
		for (Object obj : flipInfo.getData()) {
			Map map = (Map) obj;
			rowNo++;

			Long id = Long.valueOf(map.get("id").toString());
			FormDataMasterBean mb = FormService.findDataById(id.longValue(), ajxxFormId);

			ws_doc.addCell(new Label(0, rowNo, rowNo + ""));

			Object fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案件编号")).getName());
			if (fv != null) {
				ws_doc.addCell(new Label(1, rowNo, fv.toString()));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-立案时间")).getName());
			if (fv != null) {
				ws_doc.addCell(new Label(2, rowNo, fv.toString()));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-审批意见-立案审查人审查时间")).getName());
			if (fv != null) {
				ws_doc.addCell(new Label(3, rowNo, fv.toString()));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-所属部门")).getName());
			if (fv != null) {
				ws_doc.addCell(
						new Label(4, rowNo, orgManager.getDepartmentById(Long.valueOf(fv.toString())).getName()));
			}
			List list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-参与承揽与经办人员及律师费分配-人员")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(5, rowNo, CommonUtil.getMemberNamesByIds(list)));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("诉讼与仲裁-案件标的额")).getName());
			if (fv != null) {
				ws_doc.addCell(new Label(6, rowNo, fv.toString()));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-委托人重复表-委托人")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(7, rowNo, StringHelper.joinList(list, "、")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-对方当事人重复表-对方当事人")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(8, rowNo, StringHelper.joinList(list, "、")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-计费金额重复表-应收代理费")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(9, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("案件收支-客户收费记录重复表-收费时间")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(10, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("案件收支_客户收费记录重复表_实收费")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(11, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("案件收支_客户收费记录重复表_差旅费")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(12, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("案件收支_客户收费记录重复表_代理费")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(13, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("案件收支_客户收费记录重复表_计收费")).getName());
			if ((list != null) && (list.size() > 0)) {
				ws_doc.addCell(new Label(14, rowNo, StringHelper.joinList(list, "\r\n")));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案件类别-2")).getName());
			if (fv != null) {
				if (!"".equals(fv.toString())) {
					CtpEnumItem item = (CtpEnumItem) DBAgent.get(CtpEnumItem.class, Long.valueOf(fv.toString()));
					if (item != null) {
						ws_doc.addCell(new Label(15, rowNo, item.getShowvalue()));
					}
				}
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案源人")).getName());
			if (fv != null) {
				String _allVal = "";
				String[] arrayOfString;
				int j = (arrayOfString = fv.toString().split(",")).length;
				for (int i = 0; i < j; i++) {
					String _str = arrayOfString[i];
					if (!StringHelper.isNullOrEmpty(_str).booleanValue()) {
						_allVal = StringHelper.concat(_allVal, orgManager.getMemberById(Long.valueOf(_str)).getName(),
								"、");
					}
				}
				ws_doc.addCell(new Label(16, rowNo, _allVal));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-类别")).getName());
			if (fv != null) {
				CtpEnumItem item = (CtpEnumItem) DBAgent.get(CtpEnumItem.class, Long.valueOf(fv.toString()));
				ws_doc.addCell(new Label(17, rowNo, item.getShowvalue()));
			}
			list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-展业费使用分配重复表-人员")).getName());
			List list2 = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-展业费使用分配重复表-展业费比例")).getName());
			String str = "";
			for (int i = 0; i < list.size(); i++) {
				String row = "";
				try {
					if ((list.get(i) != null) && (!"".equals(list.get(i).toString()))) {
						row = orgManager.getMemberById(Long.valueOf(list.get(i).toString())).getName();
					}
				} catch (Exception ex) {
					this.logger.error("", ex);
				}
				if (list2.get(i) != null) {
					row = row + ":" + list2.get(i).toString();
				}
				str = StringHelper.concat(str, row, "、");
			}
			ws_doc.addCell(new Label(18, rowNo, str));

			list = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-计费金额重复表-协议收费时间-开始")).getName());
			list2 = mb.getDataList(((FormFieldBean) formFieldMap.get("公用-计费金额重复表-协议收费时间-结束")).getName());
			str = "";
			for (int i = 0; i < list.size(); i++) {
				try {
					String row = "";
					if (list.get(i) != null) {
						Timestamp ts = (Timestamp) list.get(i);
						row = DateUtil.format(new Date(ts.getTime()));
					}
					if (list2.get(i) != null) {
						Timestamp ts2 = (Timestamp) list2.get(i);
						row = row + "至" + DateUtil.format(new Date(ts2.getTime()));
					}
					if (!StringHelper.isNullOrEmpty(row).booleanValue()) {
						str = StringHelper.concat(str, row, "、");
					}
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
			if (!"".equals(str)) {
				ws_doc.addCell(new Label(19, rowNo, str));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案件简况")).getName());
			if (fv != null) {
				ws_doc.addCell(new Label(20, rowNo, fv.toString()));
			}
			fv = mb.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案件类别-3")).getName());
			if (fv != null) {
				CtpEnumItem item = (CtpEnumItem) DBAgent.get(CtpEnumItem.class, Long.valueOf(fv.toString()));
				if (item != null) {
					ws_doc.addCell(new Label(21, rowNo, item.getShowvalue()));
				}
			}
		}
		wf.write();
		wf.close();
		os.flush();
		os.close();
		return null;
	}
}
