package com.seeyon.ctp.form.modules.formlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.seeyon.apps.template.manager.CollaborationTemplateManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumBean;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.common.po.filemanager.Attachment;
import com.seeyon.ctp.common.po.filemanager.V3XFile;
import com.seeyon.ctp.common.po.template.CtpTemplateCategory;
import com.seeyon.ctp.common.security.SecurityHelper;
import com.seeyon.ctp.common.supervise.manager.SuperviseManager;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormAuthViewBean;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormFieldComBean;
import com.seeyon.ctp.form.bean.FormFormulaBean;
import com.seeyon.ctp.form.bean.FormSerialNumberBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.bean.FormViewBean;
import com.seeyon.ctp.form.bean.Operation_BindEvent;
import com.seeyon.ctp.form.biz.manager.BizValidateManager;
import com.seeyon.ctp.form.biz.vo.BizValidateResultVO;
import com.seeyon.ctp.form.modules.bind.FormBindDesignManager;
import com.seeyon.ctp.form.modules.engin.relation.FormRelationEnums;
import com.seeyon.ctp.form.modules.engin.relation.FormRelationManager;
import com.seeyon.ctp.form.modules.serialNumber.SerialNumberManager;
import com.seeyon.ctp.form.modules.trigger.FormTriggerManager;
import com.seeyon.ctp.form.po.CtpFormula;
import com.seeyon.ctp.form.po.CtpFormulaCondition;
import com.seeyon.ctp.form.po.FormOwner;
import com.seeyon.ctp.form.po.FormRelation;
import com.seeyon.ctp.form.po.FormResource;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormManager;
import com.seeyon.ctp.form.util.Enums;
import com.seeyon.ctp.form.util.FormCharset;
import com.seeyon.ctp.form.util.FormUtil;
import com.seeyon.ctp.form.util.infopath.InfoPathObject;
import com.seeyon.ctp.form.util.infopath.InfoPath_xsl;
import com.seeyon.ctp.form.util.infopath.InfopathManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.privilege.manager.MenuCacheManager;
import com.seeyon.ctp.seeyonreport.manager.SeeyonReportManager;
import com.seeyon.ctp.seeyonreport.manager.SeeyonReportTemplateManager;
import com.seeyon.ctp.seeyonreport.po.ReportDataSet;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.StringUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.ZipUtil;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import com.seeyon.ctp.workflow.wapi.WorkflowApiManager;
import com.seeyon.v3x.dee.common.exportflow.ExportFlow;
import com.seeyon.v3x.dee.util.DesUtil;

import www.seeyon.com.biz.enums.BizOperationEnum;
import www.seeyon.com.utils.FileUtil;

public class FormListManager_lawyer_Impl extends FormListManagerImpl {
	private static final Logger logger = Logger.getLogger(FormListManager_lawyer_Impl.class);
	private FormCacheManager formCacheManager;
	private FileManager fileManager;
	private FormManager formManager;
	private TemplateManager templateManager;
	private SuperviseManager superviseManager;
	private CollaborationTemplateManager collaborationTemplateManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	private WorkflowApiManager wapi;
	private EnumManager enumManagerNew;
	private SerialNumberManager serialNumberManager;
	private FormTriggerManager formTriggerManager;
	private FormRelationManager formRelationManager;
	private SeeyonReportManager seeyonReportManager;
	private SeeyonReportTemplateManager seeyonReportTemplateManager;
	private FormBindDesignManager formBindDesignManager;
	private MenuCacheManager menuCacheManager;

	@Override
	public Map<String, Object> checkInfopath(final Long fileId, final String rootPath, final int formType,
			final long formId) throws BusinessException {
		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", false);
		Label_0551: {
			if (fileId != -1L) {
				if (formId != -1L) {
					final long userId = AppContext.currentUserId();
					final FormOwner formOwner = this.formCacheManager.getFormOwner(formId);
					final boolean hasRight = FormUtil.isRightEditForm(userId, formOwner);
					if (!hasRight) {
						result.put("msg", ResourceUtil.getString("form.edit.noright"));
						return result;
					}
				} else {
					final Enums.FormType type = Enums.FormType.getEnumByKey(formType);
					if (!Enums.FormType.canCreate(type)) {
						result.put("msg",
								"\u60a8\u65e0\u6743\u9650\u65b0\u5efa\u6b64\u7c7b\u578b\u7684\u8868\u5355\uff0c\u8bf7\u8054\u7cfb\u7cfb\u7edf\u7ba1\u7406\u5458\uff01");
						return result;
					}
					if (type.isCheckNum()) {
						final BizValidateManager manager = BizValidateManager.getInstance();
						final BizValidateResultVO resultVO = manager.preValidateBiz(BizOperationEnum.create_form_save);
						if (!resultVO.isSuccess()) {
							result.put("msg", ResourceUtil.getString(resultVO.getCode()));
							result.put("showUnFlowFormList", true);
							return result;
						}
					}
				}
				File file = this.fileManager.getFile(fileId, DateUtil.currentDate());
				file = this.fileManager.decryptionFile(file);
				final V3XFile v3 = this.fileManager.getV3XFile(fileId);
				final InfopathManager im = new InfopathManager();
				im.setFileManager(this.fileManager);
				im.setRootPath(rootPath);
				Label_0571: {
					try {
						final InfoPathObject xsf = im.parseXSN(file);
						final String check = this.validateInfopath(xsf, formType, formId);
						if (!"".equals(check)) {
							result.put("msg", check);
							return result;
						}
						for (int i = 0; i < xsf.getViewList().size(); ++i) {
							final InfoPath_xsl xsl = xsf.getViewList().get(i);
							xsl.covertContent(null);
						}
						if (formId == -1L) {
							final FormBean fb = im.getFormBean(xsf);
							final String fileName = v3.getFilename();
							fb.setFormName(fileName.substring(0, fileName.lastIndexOf(".")));
							AppContext.currentUserId();
							fb.setFormType(formType);
							final List<FormResource> resourceList = (List<FormResource>) fb
									.getExtraAttr("infoPathResource");
							final FormResource fr = new FormResource();
							fr.setId(fileId);
							fr.setFormId(fb.getId());
							fr.setResourceName("name.xsn");
							fr.setPropertyType(Enums.FormResourcePropertyTypeEnum.InfoPath.getKey());
							fr.setPropertyName("name.xsn");
							fr.setContent("name.xsn");
							resourceList.add(fr);
							fb.putExtraAttr("infoPathResource", (List) resourceList);
							this.formManager.addEditForm(fb);
						}
						break Label_0571;
					} catch (Exception e) {
						FormListManager_lawyer_Impl.logger.error((Object) e.getMessage(), (Throwable) e);
						result.put("msg", e.getMessage());
						return result;
					}
				}
				result.put("success", true);
				return result;
			}
		}
		result.put("msg", ResourceUtil.getString("form.fielddesign.cantfindfileid"));
		return result;
	}

	private String validateInfopath(final InfoPathObject xsf, final int formType, final long formId) {
		final String checks = ResourceUtil.getString("form.fielddesign.fieldcheck");
		final StringBuffer sb = new StringBuffer(",");
		String fieldName = "";
		final String checkFileds = this.checkFieldCount(xsf);
		if (!"".equals(checkFileds)) {
			return checkFileds;
		}
		if (xsf.getViewList().size() > 1) {
			if (!AppContext.hasPlugin("formAdvanced")) {
				return ResourceUtil.getString("form.formcreate.isnotadvancedform.multiform.abel");
			}
			if (formType == Enums.FormType.baseInfo.getKey()) {
				return ResourceUtil.getString("form.fielddesign.baseolnyallowsigleview");
			}
			if (formType == Enums.FormType.planForm.getKey()) {
				return ResourceUtil.getString("form.formcreate.basedata.plan.nomultiview.label");
			}
		}
		if (xsf.getIntoxsd().getMasternamelst().size() == 0) {
			return ResourceUtil.getString("form.fielddesign.infopathnomaintable");
		}
		if (formId != -1L && this.checkDelFormView(xsf, formId)) {
			return ResourceUtil.getString("form.fielddesign.viewname.delete.label");
		}
		for (int i = 0; i < xsf.getIntoxsd().getMasternamelst().size(); ++i) {
			fieldName = xsf.getIntoxsd().getMasternamelst().get(i).toLowerCase();
			if (checks.contains("," + fieldName + ",")) {
				return ResourceUtil.getString("form.system.check");
			}
			if (sb.toString().contains("," + fieldName + ",")) {
				return ResourceUtil.getString("form.infopath.sameFiledName");
			}
			sb.append(fieldName).append(",");
		}
		for (int i = 0; i < xsf.getIntoxsd().getTablst().size(); ++i) {
			final Map ht = (Map) xsf.getIntoxsd().getSlavelst().get(i);
			final Object[] ob = ht.values().toArray();
			for (int j = 0; j < ob.length; ++j) {
				fieldName = ((String) ob[j]).toLowerCase();
				if (checks.contains("," + fieldName + ",")) {
					return ResourceUtil.getString("form.system.check");
				}
				if (sb.toString().contains("," + fieldName + ",")) {
					return ResourceUtil.getString("form.infopath.sameFiledName");
				}
				sb.append(fieldName).append(",");
			}
		}
		return "";
	}

	private boolean checkDelFormView(final InfoPathObject xsf, final long formId) {
		boolean isDelete = false;
		final FormBean fb = this.formCacheManager.getForm(formId);
		if (fb != null) {
			final List<FormViewBean> fvbList = fb.getFormViewList();
			if (fvbList != null) {
				for (final FormViewBean formViewBean : fvbList) {
					final String name = xsf.getViewFileCaption(formViewBean.getFormViewFileName());
					if (Strings.isBlank(name)) {
						isDelete = true;
						break;
					}
				}
			}
		}
		return isDelete;
	}

	private String checkFieldCount(final InfoPathObject xsf) {
		int count = xsf.getIntoxsd().getMasternamelst().size();
		for (int i = 0; i < xsf.getIntoxsd().getTablst().size(); ++i) {
			final Map ht = (Map) xsf.getIntoxsd().getSlavelst().get(i);
			count += ht.size();
		}
		return (count <= 950) ? "" : ResourceUtil.getString("form.fielddesign.infopathfiledlengthbigger");
	}

	public FlipInfo designFormshow(FlipInfo fi, Map<String, Object> params) throws BusinessException {
		if (fi.getSortField() != null) {
			if ((fi.getSortField().contains("owner")) || (fi.getSortField().contains("orgAccountId"))) {
				fi.setSortField("fo." + fi.getSortField());
			} else {
				fi.setSortField("fd." + fi.getSortField());
			}
		}
		Long owners = Long.valueOf(AppContext.currentUserId());
		Object property = params.get("property");
		if ("disableForm".equals(property)) {
			params.put("ownerId", owners);
			params.put("useFlag", Integer.valueOf(Enums.FormUseFlagEnum.disabled.getKey()));
			params.put("orgAccountId", Long.valueOf(AppContext.currentAccountId()));
		} else if ("othersForm".equals(property)) {
			params.put("otherownerId", owners);
			params.put("useFlag", Integer.valueOf(Enums.FormUseFlagEnum.enabled.getKey()));
			params.put("orgAccountId", Long.valueOf(AppContext.currentAccountId()));
		} else if ("myForm".equals(property)) {
			params.put("ownerId", owners);
			params.put("useFlag", Integer.valueOf(Enums.FormUseFlagEnum.enabled.getKey()));
			params.put("orgAccountId", Long.valueOf(AppContext.currentAccountId()));
		} else if ("accountForm".equals(property)) {
			params.put("orgAccountId", AppContext.getCurrentUser().getAccountId());
			if (params.get("formtype") == null) {
				params.put("formtype", "1,2,3");
			}
			if (params.containsKey("ownerId")) {
				params.put("ownerId", params.get("ownerId"));
			}
			StringBuilder useFlag = new StringBuilder();
			useFlag.append(Enums.FormUseFlagEnum.disabled.getKey()).append(",")
					.append(Enums.FormUseFlagEnum.enabled.getKey());
			if (params.get("useFlag") == null) {
				params.put("useFlag", useFlag.toString());
			}
		} else if ("prepareForm".equals(property)) {
			params.put("useFlag", Integer.valueOf(Enums.FormUseFlagEnum.prepareForm.getKey()));
		} else if ("systemDisable".equals(property)) {
			params.put("useFlag", Enums.FormUseFlagEnum.systemDisabled.getKey() + ","
					+ Enums.FormUseFlagEnum.systemDisabledDisabled.getKey());
		}
		params.remove("property");
		List<Map<String, Object>> listMap = this.formCacheManager.getFormDefinitionDAO().selectByFlipInfo(fi, params);
		List<Long> dsFormIds = null;
		if (AppContext.hasPlugin("seeyonreport")) {
			dsFormIds = checkReportDatasetExists(listMap);
		}
		for (Map<String, Object> map : listMap) {
			map.put("formTypeName",
					Enums.FormType.getEnumByKey(Integer.parseInt(String.valueOf(map.get("formType")))).getValue());
			map.put("stateText",
					Enums.FormStateEnum.getEnumByKey(Integer.parseInt(map.get("state").toString())).getText());
			map.put("useFlag",
					Enums.FormUseFlagEnum.getEnumByKey(Integer.parseInt(map.get("useFlag").toString())).getText());
			map.put("createTime", DateUtil.formatDateTime((Date) map.get("createTime")));
			if (map.get("modifyTime") == null) {
				map.put("modifyTime", map.get("createTime"));
			} else {
				map.put("modifyTime", DateUtil.formatDateTime((Date) map.get("modifyTime")));
			}
			if (this.orgManager != null) {
				try {
					V3xOrgMember member = this.orgManager.getMemberById((Long) map.get("ownerId"));
					if (member != null) {
						map.put("ownerId", member.getName());
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
			Long formId = Long.valueOf(Long.parseLong(map.get("id").toString()));
			FormBean fb = this.formCacheManager.getForm(formId.longValue());
			map.put("isIPForm", Boolean.valueOf(fb.isInfoPathForm()));
			map.put("isPForm", Boolean.valueOf(fb.isPhoneForm()));
			map.put("showPIcon", Boolean.valueOf(fb.isPhoneFormORHasPhoneView()));
			CtpTemplateCategory ctc = this.templateManager
					.getCtpTemplateCategory(Long.valueOf(Long.parseLong(map.get("categoryId").toString())));
			if (ctc != null) {
				String categoryName = ctc.getName();
				String i18nName = ResourceUtil.getString(categoryName);
				map.put("categoryId", !StringUtil.checkNull(i18nName) ? i18nName : categoryName);
			} else {
				map.put("categoryId", "");
			}
			if ((dsFormIds == null) || (!dsFormIds.contains(formId))) {
				map.put("isExistsDataset", Boolean.valueOf(false));
			} else {
				map.put("isExistsDataset", Boolean.valueOf(true));
			}
		}
		fi.setData(listMap);
		return fi;
	}

	private List<Long> checkReportDatasetExists(List<Map<String, Object>> listMap) throws BusinessException {
		List<Long> formIdList = new ArrayList();
		for (Map<String, Object> map : listMap) {
			formIdList.add(Long.valueOf(Long.parseLong(map.get("id").toString())));
		}
		List<ReportDataSet> dataSetList = this.seeyonReportManager.getReportDataSetByFormId(formIdList);

		Object result = new ArrayList();
		for (ReportDataSet rds : dataSetList) {
			Long formId = rds.getFormId();
			if (!((List) result).contains(formId)) {
				((List) result).add(formId);
			}
		}
		return (List<Long>) result;
	}

	public String checkForm(String id1) throws BusinessException, SQLException {
		StringBuilder sb = new StringBuilder();
		StringBuilder relationError = new StringBuilder();
		if (id1.indexOf(",") > -1) {
			String[] id = id1.split(",");
			for (int i = 0; i < id.length; i++) {
				FormBean fb = this.formCacheManager.getForm(Long.valueOf(id[i]).longValue());
				if (fb != null) {
					List<FormTableBean> tb;

					tb = fb.getTableList();
					for (FormTableBean ftb : tb) {
						if (this.formCacheManager.getFormDataDAO().selectRecordCount(ftb.getTableName()) > 0) {
							sb.append("《" + fb.getFormName() + "》,");
							break;
						}
					}
				}
				if (this.formRelationManager.isExistRelation(Long.valueOf(id[i]))) {
					relationError.append(
							ResourceUtil.getString("form.formlist.fromcantdel1.js", "《" + fb.getFormName() + "》",
									this.formRelationManager.getRelationFormStr(Long.valueOf(id[i]))));
					relationError.append(";");
				}
			}
		} else {
			FormBean fb = this.formCacheManager.getForm(Long.valueOf(id1).longValue());
			if (fb != null) {
				List<FormTableBean> tb = fb.getTableList();
				for (FormTableBean ftb : tb) {
					if (this.formCacheManager.getFormDataDAO().selectRecordCount(ftb.getTableName()) > 0) {
						sb.append("《" + fb.getFormName() + "》");
						break;
					}
				}
			}
			if (this.formRelationManager.isExistRelation(Long.valueOf(id1))) {
				relationError.append(ResourceUtil.getString("form.formlist.fromcantdel1.js",
						"《" + fb.getFormName() + "》", this.formRelationManager.getRelationFormStr(Long.valueOf(id1))));
			}
		}
		String sbStr = sb.toString().indexOf(",") > 1 ? sb.substring(0, sb.toString().length() - 1) : sb.toString();
		return sbStr.length() > 0 ? ResourceUtil.getString("form.formlist.fromcantdel.js", sbStr)
				: relationError.toString();
	}

	public String[] getInfopath(Long id) throws BusinessException {
		Long fileid = Long.valueOf(-1L);
		List<FormResource> resourceList = this.formCacheManager.getFormResourceDAO().selectByFormId(id);
		for (FormResource fr : resourceList) {
			if (fr.getResourceName().contains(".xsn")) {
				fileid = fr.getId();
				break;
			}
		}
		if (fileid.longValue() == -1L) {
			AttachmentManager attachmentManager = (AttachmentManager) AppContext.getBean("attachmentManager");
			Object list = attachmentManager.getByReference(id);
			if ((list != null) && (!((List) list).isEmpty())) {
				Attachment attachment = (Attachment) ((List) list).get(0);
				fileid = attachment.getFileUrl();
			}
		}
		V3XFile v3x = this.fileManager.getV3XFile(fileid);
		String[] returnValue = new String[2];
		returnValue[0] = ("fileUpload.do?method=download&fileId=" + fileid + "&createDate="
				+ DateUtil.format(v3x.getCreateDate()) + "&v=" + SecurityHelper.digest(new Object[] { fileid })
				+ "&filename=");
		returnValue[1] = v3x.getFilename();
		return returnValue;
	}

	@CheckRoleAccess(roleTypes = { com.seeyon.ctp.organization.OrgConstants.Role_NAME.FormAdmin })
	public String[] exportForm(String ids) throws Exception {
		Map<String, String> map = downfile(ids);
		String[] returnValue = new String[2];
		returnValue[0] = ("fileUpload.do?method=download&fileId=" + (String) map.get("fileurl") + "&createDate="
				+ (String) map.get("createdate") + "&v=" + SecurityHelper.digest(new Object[] { map.get("fileurl") })
				+ "&filename=");
		returnValue[1] = ((String) map.get("filename"));
		return returnValue;
	}

	private Map<String, String> downfile(String formIds) throws Exception {
		Date myDate = new Date();
		Map<String, String> result = new HashMap();
		Map<String, String> flowMap = new HashMap();
		result.put("fileurl", String.valueOf(myDate.getTime()));
		result.put("createdate", DateUtil.format(myDate));

		String[] ids = formIds.split(",");

		String baseFolder = this.fileManager.getNowFolder(true);

		File paramFile = new File(baseFolder, "param.xml");
		Document paramFileDoc = DocumentHelper.createDocument();
		Element paramDocRoot = paramFileDoc.addElement("root");
		paramDocRoot.addAttribute("version", "V5.6");
		Element enumList = paramDocRoot.addElement("enumList");
		Element formList = paramDocRoot.addElement("FormList");

		List<File> formFileList = new ArrayList();
		for (int i = 0; i < ids.length; i++) {
			File folder = new File(baseFolder, "Form_" + (String) result.get("fileurl"));
			folder.mkdir();

			FormBean fb = this.formCacheManager.getForm(Long.valueOf(ids[i]).longValue());
			fb = (FormBean) fb.clone();

			String formName = FormCharset.getInstance().dBOut2JDK(fb.getFormName());
			if (i == 0) {
				if (ids.length == 1) {
					result.put("filename", formName + ".pak");
				} else {
					result.put("filename", formName + "等.pak");
				}
			}
			formFileList.add(downloadSingleFormResource(fb, folder, formList, enumList));
			if (AppContext.hasPlugin("dee")) {
				getDeeFlowIdByFormBean(fb, flowMap);
			}
			FormUtil.delDirectory(folder.getPath());
		}
		if (AppContext.hasPlugin("dee")) {
			File deeFile = exportDeeFlow(baseFolder, flowMap);
			if (deeFile != null) {
				formFileList.add(deeFile);
			}
		}
		OutputStream fout = null;
		PrintStream writer = null;
		try {
			fout = new FileOutputStream(paramFile);
			writer = new PrintStream(fout, false, FormCharset.getInstance().getJDKFile());
			writer.print(paramFileDoc.asXML());
			writer.flush();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
		formFileList.add(paramFile);

		File zipFile = new File(baseFolder, (String) result.get("fileurl"));
		ZipUtil.zip(formFileList, zipFile);
		for (File file : formFileList) {
			file.delete();
		}
		return result;
	}

	private String getXmlVal(String value) {
		return "<![CDATA[" + value + "]]";
	}

	private String getXmlContent(String value) {
		String val = value.substring(9, value.length() - 2);
		return val;
	}

	private void getDeeFlowIdByFormBean(FormBean fb, Map<String, String> flowMap) throws Exception {
		if (flowMap == null) {
			flowMap = new HashMap();
		}
		for (FormFieldBean ffb : fb.getAllFieldBeans()) {
			if ((ffb != null) && (ffb.getDeeTask() != null) && (ffb.getDeeTask().getId() != null)
					&& (!"".equals(ffb.getDeeTask().getId()))) {
				flowMap.put(ffb.getDeeTask().getId(), "");
			}
		}
		for (FormAuthViewBean favb : fb.getAllFormAuthViewBeans()) {
			if (favb != null) {
				for (Operation_BindEvent e : favb.getOperationBindEvent()) {
					if (e != null) {
						if (!"".equals(e.getTaskId())) {
							flowMap.put(e.getTaskId(), "");
						} else if ((e.getDeeTask() != null) && (e.getDeeTask().getId() != null)
								&& (!"".equals(e.getDeeTask().getId()))) {
							flowMap.put(e.getDeeTask().getId(), "");
						}
					}
				}
			}
		}
	}

	private File exportDeeFlow(final String baseFolder, final Map<String, String> flowMap) throws Exception {
		String flowIds = "";
		for (final Map.Entry<String, String> entry : flowMap.entrySet()) {
			if ("".equals(entry.getKey())) {
				continue;
			}
			flowIds = String.valueOf(flowIds) + entry.getKey() + ",";
		}
		if (!"".equals(flowIds)) {
			flowIds = flowIds.substring(0, flowIds.length() - 1);
		}
		if ("".equals(flowIds)) {
			return null;
		}
		final ExportFlow ef = new ExportFlow();
		final String xmlFlow = ef.doReader(flowIds).toString();
		if ("".equals(xmlFlow)) {
			return null;
		}
		OutputStream fout = null;
		PrintStream writer = null;
		final File deeFile = new File(baseFolder, "dee.xml");
		try {
			fout = new FileOutputStream(deeFile);
			writer = new PrintStream(fout, false, FormCharset.getInstance().getJDKFile());
			writer.print(xmlFlow);
			writer.flush();
		} catch (FileNotFoundException e) {
			FormListManager_lawyer_Impl.logger.error((Object) e.getMessage(), (Throwable) e);
		} catch (UnsupportedEncodingException e2) {
			FormListManager_lawyer_Impl.logger.error((Object) e2.getMessage(), (Throwable) e2);
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
		if (writer != null) {
			writer.close();
		}
		if (fout != null) {
			fout.close();
		}
		final List<File> deeFileList = new ArrayList<File>();
		deeFileList.add(deeFile);
		final File zipFile = new File(baseFolder, "dee.drp");
		ZipUtil.zip((Collection) deeFileList, zipFile);
		for (final File file : deeFileList) {
			file.delete();
		}
		final DesUtil desUtil = new DesUtil("drp_encrypt");
		final File newZipFile = new File(baseFolder, "encryptdee.drp");
		if (zipFile == null) {
			FormListManager_lawyer_Impl.logger.error((Object) "DEE\u538b\u7f29\u51fa\u9519dee.drp=null");
		}
		desUtil.encryptFile(zipFile.getAbsolutePath(), newZipFile.getAbsolutePath());
		zipFile.delete();
		if (newZipFile == null) {
			FormListManager_lawyer_Impl.logger.error((Object) "DEE\u52a0\u5bc6\u51fa\u9519encryptdee.drp=null");
		}
		return newZipFile;
	}

	private File downloadSingleFormResource(final FormBean fb, final File folder, final Element formList,
			final Element enumList) throws Exception {
		final Element formElement = formList.addElement("form");
		final Set<FormSerialNumberBean> serialNumberList = this.formCacheManager.getSerialNumberSateByFormId(fb.getId(),
				Enums.SerialNumberState.YES.getKey());
		for (final FormSerialNumberBean fsnb : serialNumberList) {
			final Element serialNumbeElement = formElement.addElement("flowid");
			serialNumbeElement.addAttribute("id", new StringBuilder().append(fsnb.getId()).toString());
			serialNumbeElement.addAttribute("name", fsnb.getVariableName());
			serialNumbeElement.addAttribute("digit", new StringBuilder().append(fsnb.getDigit()).toString());
			serialNumbeElement.addAttribute("prefix", fsnb.getPrefix());
			serialNumbeElement.addAttribute("suffix", fsnb.getSuffix());
			serialNumbeElement.addAttribute("texttimebehond", fsnb.getTextTimeBehind());
			serialNumbeElement.addAttribute("texttimefont", fsnb.getTextTimeFront());
			serialNumbeElement.addAttribute("accountid",
					new StringBuilder(String.valueOf(fsnb.getAccountId())).toString());
			serialNumbeElement.addAttribute("minvalue", new StringBuilder().append(fsnb.getMinValue()).toString());
			serialNumbeElement.addAttribute("rulereset", new StringBuilder().append(fsnb.getRuleReset()).toString());
			serialNumbeElement.addAttribute("serialnumberstep",
					new StringBuilder().append(fsnb.getSerialNumberStep()).toString());
			serialNumbeElement.addAttribute("state", new StringBuilder().append(fsnb.getState()).toString());
			serialNumbeElement.addAttribute("timedate", new StringBuilder().append(fsnb.getTimeDate()).toString());
		}
		final List<File> formFileList = new ArrayList<File>();
		final String formName = FormCharset.getInstance().dBOut2JDK(fb.getFormName());
		final File appZipFile = new File(folder.getParent(), fb.getId() + ".pak");
		formElement.addAttribute("name", this.getXmlVal(formName));
		formElement.addAttribute("id", String.valueOf(fb.getId()));
		formElement.addAttribute("type", String.valueOf(fb.getFormType()));
		formElement.addAttribute("start", String.valueOf(fb.getUseFlag()));
		formElement.addAttribute("categoryName",
				this.getXmlVal(this.templateManager.getCategorybyId(fb.getCategoryId()).getName()));
		final String fieldInfo = FormCharset.getInstance().dBOut2JDK(fb.getDataDefineXml());
		final StringBuffer sb = new StringBuffer("");
		for (final FormTableBean table : fb.getTableList()) {
			sb.append(table.getTableName()).append(",");
		}
		formElement.addAttribute("tableName", sb.toString());
		final File filedFile = new File(folder, String.valueOf(fb.getId()));
		OutputStream fout = null;
		PrintStream writer = null;
		try {
			fout = new FileOutputStream(filedFile);
			writer = new PrintStream(fout, false, FormCharset.getInstance().getJDKFile());
			writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + fieldInfo);
			writer.flush();
		} finally {
			if (writer != null) {
				writer.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
		if (writer != null) {
			writer.close();
		}
		if (fout != null) {
			fout.close();
		}
		formFileList.add(filedFile);
		final List<CtpFormula> cf_list = new ArrayList<CtpFormula>();
		final List<CtpFormulaCondition> formulaConditionList = fb.getFormulaConditionList();
		final Element conditionList = formElement.addElement("conditionList");
		if (Strings.isNotEmpty((Collection) formulaConditionList)) {
			for (final CtpFormulaCondition ctpFormulaCondition : formulaConditionList) {
				final Integer actionType = ctpFormulaCondition.getActionType();
				if (Enums.FormulaConditionType.FORM_CHECK.getKey() != actionType) {
					final Element formulaElement = conditionList.addElement("condition");
					formulaElement.addAttribute("id",
							new StringBuilder().append(ctpFormulaCondition.getId()).toString());
					formulaElement.addAttribute("serialNumber",
							new StringBuilder().append(ctpFormulaCondition.getSerialNumber()).toString());
					formulaElement.addAttribute("actionType",
							new StringBuilder().append(ctpFormulaCondition.getActionType()).toString());
					formulaElement.addAttribute("conditionId",
							new StringBuilder().append(ctpFormulaCondition.getCondtionId()).toString());
					CtpFormula formula = this.formCacheManager.getFormula(ctpFormulaCondition.getCondtionId());
					if (formula != null) {
						cf_list.add(formula);
					}
					formulaElement.addAttribute("actionId",
							new StringBuilder().append(ctpFormulaCondition.getActionId()).toString());
					formula = this.formCacheManager.getFormula(ctpFormulaCondition.getActionId());
					if (formula != null) {
						cf_list.add(formula);
					}
					formulaElement.addAttribute("actionId",
							new StringBuilder().append(ctpFormulaCondition.getActionId()).toString());
					formulaElement.addAttribute("keyword", ctpFormulaCondition.getKeyword());
					formulaElement.addAttribute("ownerId",
							new StringBuilder().append(ctpFormulaCondition.getOwnerId()).toString());
					formulaElement.addAttribute("fieldId",
							new StringBuilder().append(ctpFormulaCondition.getFieldId()).toString());
					formulaElement.addAttribute("sortNumber",
							new StringBuilder().append(ctpFormulaCondition.getSortNumber()).toString());
				}
			}
		}
		final Element formulaListElement = formElement.addElement("formulaList");
		FormFormulaBean formulaBean = null;
		if (Strings.isNotEmpty((Collection) cf_list)) {
			for (final CtpFormula cf : cf_list) {
				final Element formulaElement2 = formulaListElement.addElement("formula");
				formulaElement2.addAttribute("id", new StringBuilder().append(cf.getId()).toString());
				formulaElement2.addAttribute("formulatype", new StringBuilder().append(cf.getFormulaType()).toString());
				formulaBean = this.formCacheManager.loadFormFormulaBean(fb, cf.getId());
				formulaElement2.addAttribute("expression", this.getXmlVal(formulaBean.getFormulaForDisplay()));
				formulaElement2.addAttribute("OwnerType", new StringBuilder().append(cf.getOwnerType()).toString());
				formulaElement2.addAttribute("OwnerId", new StringBuilder().append(cf.getOwnerId()).toString());
				formulaElement2.addAttribute("Description", this.getXmlVal(cf.getDescription()));
			}
		}
		final List<FormResource> fr_list = this.formCacheManager.getFormResourceDAO().selectByFormId(fb.getId());
		boolean isOldVersionInfopath = true;
		for (final FormResource fr : fr_list) {
			final Element formResouceElement = formElement.addElement("resource");
			if (fr.getPropertyType() == Enums.FormResourcePropertyTypeEnum.DefaultInput.getKey()) {
				formResouceElement.addAttribute("type", String.valueOf(fr.getPropertyType()));
				formResouceElement.addAttribute("name", this.getXmlVal(fr.getResourceName()));
				formResouceElement.addAttribute("value", fr.getPropertyName());
				formResouceElement.addAttribute("fileName", String.valueOf(fr.getId()));
				final Document doc = DocumentHelper.parseText(FormCharset.getInstance().dBOut2JDK(fr.getContent()));
				final Element root = doc.getRootElement();
				for (final Object o : root.elements()) {
					final Element el = (Element) o;
					final String resourceFieldName = el.attributeValue("name");
					if (el.attribute("relationId") != null) {
						el.remove(el.attribute("relationId"));
					}
					final FormFieldBean fieldBean = fb
							.getFieldBeanByDisplay(resourceFieldName.substring(3, resourceFieldName.length()));
					if (FormFieldComBean.FormFieldComEnum.SELECT == fieldBean.getInputTypeEnum()
							|| FormFieldComBean.FormFieldComEnum.RADIO == fieldBean.getInputTypeEnum()) {
						final Long enumId = fieldBean.getEnumId();
						final List<Element> els2 = (List<Element>) enumList.selectNodes("enum[@id='" + enumId + "']");
						if (Strings.isEmpty((Collection) els2)) {
							final CtpEnumBean enumItem = this.enumManagerNew.getEnum(enumId);
							final Element enumElement = enumList.addElement("enum");
							enumElement.addAttribute("id", enumId.toString());
							enumElement.addAttribute("enumname", enumItem.getEnumname());
							Integer enumType = enumItem.getEnumtype();
							if (4 == enumType) {
								enumType = 0;
								el.addAttribute("formattype", "");
							}
							enumElement.addAttribute("enumtype", String.valueOf(enumType));
							enumElement.addAttribute("sortnumber", String.valueOf(enumItem.getSortnumber()));
							enumElement.addAttribute("description", enumItem.getDescription());
							final List<CtpEnumItem> items = (List<CtpEnumItem>) this.enumManagerNew
									.getFirstLevelItemsByEmumId(enumId);
							for (final CtpEnumItem ctpEnumItem : items) {
								final Element itemElement = enumElement.addElement("enumvalue");
								itemElement.addAttribute("id", String.valueOf(ctpEnumItem.getId()));
								itemElement.addAttribute("enumvalue", ctpEnumItem.getEnumvalue());
								itemElement.addAttribute("showvalue", ctpEnumItem.getShowvalue());
								itemElement.addAttribute("sort", String.valueOf(ctpEnumItem.getSortnumber()));
								itemElement.addAttribute("level", String.valueOf(ctpEnumItem.getLevelNum()));
								this.createChildEnumItemElement(itemElement, ctpEnumItem);
							}
						}
					} else if (FormFieldComBean.FormFieldComEnum.RELATIONFORM == fieldBean.getInputTypeEnum()) {
						el.addAttribute("type",
								this.convertRelationDefaultInputTypeInDownload(fieldBean.getFieldType()));
						formElement.addAttribute("importFormula", "false");
					} else if (FormFieldComBean.FormFieldComEnum.EXTEND_EXCHANGETASK == fieldBean.getInputTypeEnum()
							|| FormFieldComBean.FormFieldComEnum.EXTEND_QUERYTASK == fieldBean.getInputTypeEnum()) {
						if (!AppContext.hasPlugin("dee")) {
							el.addAttribute("type", FormFieldComBean.FormFieldComEnum.TEXT.getKey());
							el.remove(el.element("DeeTask"));
						}
					} else if (FormFieldComBean.FormFieldComEnum.RELATION == fieldBean.getInputTypeEnum()) {
						final FormRelation relation = fieldBean.getFormRelation();
						if (this.isRelationFormField(fieldBean, fb)) {
							el.addAttribute("type",
									this.convertRelationDefaultInputTypeInDownload(fieldBean.getFieldType()));
							fieldBean.setFormRelation(null);
							formElement.addAttribute("importFormula", "false");
						}
						if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_field
										.getKey()) {
							el.addAttribute("type",
									this.convertRelationDefaultInputTypeInDownload(fieldBean.getFieldType()));
							fieldBean.setFormRelation(null);
						}
					} else if (FormFieldComBean.FormFieldComEnum.OUTWRITE == fieldBean.getInputTypeEnum()
							&& FormFieldComBean.FormFieldComEnum.SELECT.getKey().equals(fieldBean.getFormatType())) {
						final Long enumId = fieldBean.getFormatEnumId();
						final List<Element> els2 = (List<Element>) enumList.selectNodes("enum[@id='" + enumId + "']");
						if (Strings.isEmpty((Collection) els2) && enumId != 0L) {
							final CtpEnumBean enumItem = this.enumManagerNew.getEnum(enumId);
							final Element enumElement = enumList.addElement("enum");
							enumElement.addAttribute("id", enumId.toString());
							enumElement.addAttribute("enumname", enumItem.getEnumname());
							enumElement.addAttribute("enumtype", String.valueOf(enumItem.getEnumtype()));
							enumElement.addAttribute("sortnumber", String.valueOf(enumItem.getSortnumber()));
							enumElement.addAttribute("description", enumItem.getDescription());
							final List<CtpEnumItem> items2 = (List<CtpEnumItem>) this.enumManagerNew
									.getFirstLevelItemsByEmumId(enumId);
							for (final CtpEnumItem ctpEnumItem2 : items2) {
								final Element itemElement2 = enumElement.addElement("enumvalue");
								itemElement2.addAttribute("id", String.valueOf(ctpEnumItem2.getId()));
								itemElement2.addAttribute("enumvalue", ctpEnumItem2.getEnumvalue());
								itemElement2.addAttribute("showvalue", ctpEnumItem2.getShowvalue());
								itemElement2.addAttribute("sort", String.valueOf(ctpEnumItem2.getSortnumber()));
								itemElement2.addAttribute("level", String.valueOf(ctpEnumItem2.getLevelNum()));
								this.createChildEnumItemElement(itemElement2, ctpEnumItem2);
							}
						}
					}
					if (fieldBean.getFormRelation() != null) {
						final FormRelation relation = fieldBean.getFormRelation();
						if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_member
										.getKey()) {
							el.addAttribute("refInputType", FormFieldComBean.FormFieldComEnum.EXTEND_MEMBER.getKey());
							el.addAttribute("refInputName", relation.getToRelationAttr());
							el.addAttribute("refInputAtt", relation.getViewAttr());
						} else if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_department
										.getKey()) {
							el.addAttribute("refInputType",
									FormFieldComBean.FormFieldComEnum.EXTEND_DEPARTMENT.getKey());
							el.addAttribute("refInputName", relation.getToRelationAttr());
							el.addAttribute("refInputAtt", relation.getViewAttr());
						} else if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_project
										.getKey()) {
							el.addAttribute("refInputType", FormFieldComBean.FormFieldComEnum.EXTEND_PROJECT.getKey());
							el.addAttribute("refInputName", relation.getToRelationAttr());
							el.addAttribute("refInputAtt", relation.getViewAttr());
						} else if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_multiEnum
										.getKey()) {
							el.addAttribute("refInputType", FormFieldComBean.FormFieldComEnum.SELECT.getKey());
							el.addAttribute("refInputName", relation.getToRelationAttr());
							el.addAttribute("refInputAtt", relation.getViewAttr());
						} else if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_dee
										.getKey()) {
							if (AppContext.hasPlugin("dee")) {
								el.addAttribute("refInputType",
										FormFieldComBean.FormFieldComEnum.EXTEND_EXCHANGETASK.getKey());
								el.addAttribute("refInputName", relation.getToRelationAttr());
								el.addAttribute("refInputAtt", relation.getViewAttr());
							} else {
								el.addAttribute("type", FormFieldComBean.FormFieldComEnum.TEXT.getKey());
							}
						} else if (relation != null && relation
								.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_map
										.getKey()) {
							el.addAttribute("refInputType", FormFieldComBean.FormFieldComEnum.MAP_LOCATE.getKey());
							el.addAttribute("refInputName", relation.getToRelationAttr());
							el.addAttribute("refInputAtt", relation.getViewAttr());
						} else {
							if (relation == null || relation
									.getToRelationAttrType() != FormRelationEnums.ToRelationAttrType.data_relation_imageEnum
											.getKey()) {
								continue;
							}
							el.addAttribute("type", FormFieldComBean.FormFieldComEnum.TEXT.getKey());
							el.remove(el.element("Enum"));
						}
					}
				}
				final String resourceContent = FormCharset.getInstance().dBOut2JDK(doc.asXML());
				final File resourceFile = new File(folder, String.valueOf(fr.getId()));
				try {
					fout = new FileOutputStream(resourceFile);
					writer = new PrintStream(fout, false, FormCharset.getInstance().getJDKFile());
					writer.print(resourceContent);
					writer.flush();
				} finally {
					if (writer != null) {
						writer.close();
					}
					if (fout != null) {
						fout.close();
					}
				}
				if (writer != null) {
					writer.close();
				}
				if (fout != null) {
					fout.close();
				}
				formFileList.add(resourceFile);
			} else if (fr.getResourceName().contains(".xsn")) {
				isOldVersionInfopath = false;
				final V3XFile v3x = this.fileManager.getV3XFile(fr.getId());
				final File xsn = this.fileManager.getFile(fr.getId(), v3x.getCreateDate());
				final File destFile = new File(folder, fr.getId() + ".xsn");
				destFile.createNewFile();
				FileUtil.copyFile(xsn, destFile);
				formFileList.add(destFile);
				formElement.addAttribute("attName", this.getXmlVal(v3x.getFilename()));
				formResouceElement.addAttribute("type", String.valueOf(fr.getPropertyType()));
				formResouceElement.addAttribute("name", fr.getResourceName());
				formResouceElement.addAttribute("value", fr.getPropertyName());
				formResouceElement.addAttribute("fileName", fr.getId() + ".xsn");
			} else {
				formResouceElement.addAttribute("type", String.valueOf(fr.getPropertyType()));
				formResouceElement.addAttribute("name", this.getXmlVal(fr.getResourceName()));
				formResouceElement.addAttribute("value", fr.getPropertyName());
				formResouceElement.addAttribute("fileName", String.valueOf(fr.getId()));
				String resourceContent2 = null;
				if (!AppContext.hasPlugin("dee")
						&& fr.getPropertyType() == Enums.FormResourcePropertyTypeEnum.Operation.getKey()) {
					final Document doc2 = DocumentHelper
							.parseText(FormCharset.getInstance().dBOut2JDK(fr.getContent()));
					final Element root2 = doc2.getRootElement();
					final Element operation = (Element) root2.selectSingleNode("/Operations/Operation");
					final Element eventBindList = operation.element("EventBindList");
					if (eventBindList != null) {
						final List<Element> eventBinds = (List<Element>) eventBindList.elements("EventBind");
						if (eventBinds != null) {
							int count = eventBinds.size();
							for (final Element eventBind : eventBinds) {
								final String taskType = eventBind.attributeValue("taskType");
								if ("dee".equalsIgnoreCase(taskType)) {
									eventBindList.remove(eventBind);
									--count;
								}
							}
							if (count == 0) {
								operation.remove(eventBindList);
							}
						}
					}
					final Element deeTaskRoot = operation.element("DeeTaskList");
					if (deeTaskRoot != null) {
						operation.remove(deeTaskRoot);
					}
					resourceContent2 = FormCharset.getInstance().dBOut2JDK(doc2.asXML());
				} else {
					resourceContent2 = FormCharset.getInstance().dBOut2JDK(fr.getContent());
				}
				final File resourceFile2 = new File(folder, String.valueOf(fr.getId()));
				try {
					fout = new FileOutputStream(resourceFile2);
					writer = new PrintStream(fout, false, FormCharset.getInstance().getJDKFile());
					writer.print(resourceContent2);
					writer.flush();
				} finally {
					if (writer != null) {
						writer.close();
					}
					if (fout != null) {
						fout.close();
					}
				}
				if (writer != null) {
					writer.close();
				}
				if (fout != null) {
					fout.close();
				}
				formFileList.add(resourceFile2);
			}
		}
		if (isOldVersionInfopath) {
			final AttachmentManager attachmentManager = (AttachmentManager) AppContext.getBean("attachmentManager");
			final List<Attachment> atts = (List<Attachment>) attachmentManager.getByReference(fb.getId());
			if (Strings.isNotEmpty((Collection) atts) && atts.size() == 1) {
				final V3XFile v3x2 = this.fileManager.getV3XFile(atts.get(0).getFileUrl());
				final File xsn2 = this.fileManager.getFile(v3x2.getId(), v3x2.getCreateDate());
				final File destFile2 = new File(folder, String.valueOf(UUIDLong.longUUID()) + ".xsn");
				destFile2.createNewFile();
				FileUtil.copyFile(xsn2, destFile2);
				formFileList.add(destFile2);
				final Element formResouceElement2 = formElement.addElement("resource");
				formElement.addAttribute("attName", this.getXmlVal(v3x2.getFilename()));
				formResouceElement2.addAttribute("type", "0");
				formResouceElement2.addAttribute("name", "name.xsn");
				formResouceElement2.addAttribute("value", "name.xsn");
				formResouceElement2.addAttribute("fileName", destFile2.getName());
			}
		}
		this.appLogManager.insertLog(AppContext.getCurrentUser(), AppLogAction.Form_ExportPak,
				new String[] { AppContext.currentUserName(), formName });
		ZipUtil.zip((Collection) formFileList, appZipFile);
		return appZipFile;
	}

	private void createChildEnumItemElement(final Element root, final CtpEnumItem enumItem) throws BusinessException {
		final List<Long> ids = new ArrayList<Long>();
		ids.add(enumItem.getId());
		final List<CtpEnumItem> items = (List<CtpEnumItem>) this.enumManagerNew.getEnumItemByRootId((List) ids);
		if (Strings.isNotEmpty((Collection) items)) {
			for (final CtpEnumItem item : items) {
				final Element itemElement = root.addElement("enumvalue");
				itemElement.addAttribute("id", String.valueOf(item.getId()));
				itemElement.addAttribute("enumvalue", item.getEnumvalue());
				itemElement.addAttribute("showvalue", item.getShowvalue());
				itemElement.addAttribute("parentid", String.valueOf(item.getParentId()));
				itemElement.addAttribute("rootid", String.valueOf(item.getRootId()));
				itemElement.addAttribute("sort", String.valueOf(item.getSortnumber()));
				itemElement.addAttribute("level", String.valueOf(item.getLevelNum()));
				this.createChildEnumItemElement(itemElement, item);
			}
		}
	}

	private String convertRelationDefaultInputTypeInDownload(final String fieldType) {
		if (Enums.FieldType.TIMESTAMP.getKey().equals(fieldType)) {
			return FormFieldComBean.FormFieldComEnum.EXTEND_DATE.getKey();
		}
		if (Enums.FieldType.DATETIME.getKey().equals(fieldType)) {
			return FormFieldComBean.FormFieldComEnum.EXTEND_DATETIME.getKey();
		}
		if (Enums.FieldType.LONGTEXT.getKey().equals(fieldType)) {
			return FormFieldComBean.FormFieldComEnum.TEXTAREA.getKey();
		}
		return FormFieldComBean.FormFieldComEnum.TEXT.getKey();
	}

	private boolean isRelationFormField(final FormFieldBean fieldBean, final FormBean fb) {
		final FormRelation fr = fieldBean.getFormRelation();
		if (fieldBean.getInputTypeEnum() == FormFieldComBean.FormFieldComEnum.RELATIONFORM) {
			return true;
		}
		if (fr != null
				&& fr.getToRelationAttrType() == FormRelationEnums.ToRelationAttrType.data_relation_field.getKey()) {
			final FormFieldBean ffb = fb.getFieldBeanByName(fr.getToRelationAttr());
			return this.isRelationFormField(ffb, fb);
		}
		return fieldBean.getInputTypeEnum() == FormFieldComBean.FormFieldComEnum.RELATIONFORM;
	}
}
