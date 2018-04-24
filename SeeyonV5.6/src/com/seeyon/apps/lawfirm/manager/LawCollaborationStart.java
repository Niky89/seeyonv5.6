package com.seeyon.apps.lawfirm.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.collaboration.event.CollaborationStartEvent;
import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.template.CtpTemplate;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.po.FormRelationAuthority;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormService;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.mail.manager.MessageMailManager;

public class LawCollaborationStart {
	private static final Log log = LogFactory.getLog(LawCollaborationStart.class);
	private FormCacheManager formCacheManager;
	private ColManager colManager;
	private LawDataListManager lawDataListManager;
	private MessageMailManager messageMailManager;

	@ListenEvent(event = CollaborationStartEvent.class)
	public void onCollaborationStart(CollaborationStartEvent event) throws Exception {
		try {
			// 获取表单
			ColSummary summary = this.colManager.getColSummaryById(event.getSummaryId());
			if (summary.getTempleteId() == null) {
				return;
			}
			TemplateManager templeteManager = (TemplateManager) AppContext.getBean("templateManager");
			CtpTemplate template = templeteManager.getCtpTemplate(summary.getTempleteId());
			String templateNo = template.getTempleteNumber();// 表单编号

			String formCa = AppContext.getSystemProperty("lawfirm.formCa");
			String formJa = AppContext.getSystemProperty("lawfirm.formJa");
			String formLa = AppContext.getSystemProperty("lawfirm.formLa");
			String documentRegistrationRelatedFlow = AppContext
					.getSystemProperty("lawfirm.documentRegistrationRelatedFlow");
			String agreementToSignRelatedFlow = AppContext.getSystemProperty("lawfirm.agreementToSignRelatedFlow");
			if ((!StringHelper.containBySp(formCa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formJa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formLa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(documentRegistrationRelatedFlow, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(agreementToSignRelatedFlow, templateNo, ",").booleanValue())) {
				return;
			}
			long formAppid = summary.getFormAppid().longValue();
			long formRecord = summary.getFormRecordid().longValue();

			String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");

			FormCacheManager formCacheManager = (FormCacheManager) AppContext.getBean("formCacheManager");
			FormBean flowFormBean = formCacheManager.getForm(formAppid);
			Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);

			FormDataMasterBean masterBean = FormService.findDataById(formRecord, formAppid);
			long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
			FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
			FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
			String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
			Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
			String[] ajFields = { ((FormFieldBean) formFieldMap.get("公用-案件编号")).getName() };
			List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0, 99999,
					"", "");
			if (StringHelper.containBySp(formCa, templateNo, ",").booleanValue()) {
				String ajbhInFlow = (String) masterBean
						.getFieldValue(((FormFieldBean) flowFieldMap.get("案件编号")).getName());
				for (int i = 0; i < formDataMasterBean.size(); i++) {
					String ajbhInBaseino = (String) ((FormDataMasterBean) formDataMasterBean.get(i)).getAllDataMap()
							.get(ajFields[0]);
					if ((ajbhInBaseino != null) && (ajbhInBaseino.equals(ajbhInFlow))) {
						Long ajId = ((FormDataMasterBean) formDataMasterBean.get(i)).getId();
						StringBuffer sql = new StringBuffer("update " + ajxxMasterTableName + " set "
								+ ((FormFieldBean) formFieldMap.get("公用-案件状态")).getName() + "='撤案中' where ID= " + ajId);
						JDBCAgent dba = new JDBCAgent();
						dba.execute(sql.toString());
						dba.close();
						break;
					}
				}
			} else if (StringHelper.containBySp(formJa, templateNo, ",").booleanValue()) {
				String ajbhInFlow = (String) masterBean
						.getFieldValue(((FormFieldBean) flowFieldMap.get("案件编号")).getName());
				for (int i = 0; i < formDataMasterBean.size(); i++) {
					String ajbhInBaseino = (String) ((FormDataMasterBean) formDataMasterBean.get(i)).getAllDataMap()
							.get(ajFields[0]);
					if ((ajbhInBaseino != null) && (ajbhInBaseino.equals(ajbhInFlow))) {
						Long ajId = ((FormDataMasterBean) formDataMasterBean.get(i)).getId();
						StringBuffer sql = new StringBuffer("update " + ajxxMasterTableName + " set "
								+ ((FormFieldBean) formFieldMap.get("公用-案件状态")).getName() + "='结案中' where ID= " + ajId);
						JDBCAgent dba = new JDBCAgent();
						dba.execute(sql.toString());
						dba.close();
						break;
					}
				}
			}
		} catch (BusinessException be) {
			log.error(be.getMessage(), be);
			throw be;
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		try {
			List<Long> allUsers = new ArrayList();
			ColSummary summary = this.colManager.getColSummaryById(event.getSummaryId());
			if (summary.getTempleteId() == null) {
				return;
			}
			TemplateManager templeteManager = (TemplateManager) AppContext.getBean("templateManager");
			CtpTemplate template = templeteManager.getCtpTemplate(summary.getTempleteId());
			String templateNo = template.getTempleteNumber();

			String formCa = AppContext.getSystemProperty("lawfirm.formCa");
			String formJa = AppContext.getSystemProperty("lawfirm.formJa");
			String formLa = AppContext.getSystemProperty("lawfirm.formLa");
			String documentRegistrationRelatedFlow = AppContext
					.getSystemProperty("lawfirm.documentRegistrationRelatedFlow");
			String agreementToSignRelatedFlow = AppContext.getSystemProperty("lawfirm.agreementToSignRelatedFlow");
			if ((!StringHelper.containBySp(formCa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formJa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formLa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(documentRegistrationRelatedFlow, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(agreementToSignRelatedFlow, templateNo, ",").booleanValue())) {
				return;
			}
			long formAppid = summary.getFormAppid().longValue();
			long formRecord = summary.getFormRecordid().longValue();

			String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");

			FormCacheManager formCacheManager = (FormCacheManager) AppContext.getBean("formCacheManager");
			FormBean flowFormBean = formCacheManager.getForm(formAppid);
			Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);

			FormDataMasterBean masterBean = FormService.findDataById(formRecord, formAppid);
			if (StringHelper.containBySp(formLa, templateNo, ",").booleanValue()) {
				String sZbls = (String) masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("主办律师")).getName());
				String sAyr = (String) masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("案源人")).getName());
				List<Object> ayrs = masterBean.getDataList(((FormFieldBean) flowFieldMap.get("案源人")).getName());
				List cyryList = masterBean
						.getDataList(((FormFieldBean) flowFieldMap.get("参与承揽与经办人员及律师费分配-人员")).getName());
				List xzryList = masterBean.getDataList(((FormFieldBean) flowFieldMap.get("协助人员重复表-姓名")).getName());
				allUsers = LawFormHelper.getCaseInfoAllMembers(StringHelper.concat(sZbls, sAyr, ","), cyryList,
						xzryList);

				// 发送邮件
				for (Object o : ayrs) {
					// messageMailManager.sendMessageByMail(arg0, arg1, arg2);
				}

			} else {
				long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
				if (ajxxFormId == -1L) {
					return;
				}
				FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
				FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
				String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
				Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
				String[] ajFields = { ((FormFieldBean) formFieldMap.get("公用-案件编号")).getName() };
				List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0,
						99999, "", "");

				String ajbhInFlow = (String) masterBean
						.getFieldValue(((FormFieldBean) flowFieldMap.get("案件编号")).getName());
				for (int i = 0; i < formDataMasterBean.size(); i++) {
					String ajbhInBaseino = (String) ((FormDataMasterBean) formDataMasterBean.get(i)).getAllDataMap()
							.get(ajFields[0]);
					if ((ajbhInBaseino != null) && (ajbhInBaseino.equals(ajbhInFlow))) {
						FormDataMasterBean masterBean2 = FormService.findDataById(
								((FormDataMasterBean) formDataMasterBean.get(i)).getId().longValue(), ajxxFormId);
						String sZbls = (String) masterBean2
								.getFieldValue(((FormFieldBean) formFieldMap.get("公用-主办律师")).getName());
						String sAyr = (String) masterBean2
								.getFieldValue(((FormFieldBean) formFieldMap.get("公用-案源人")).getName());
						List cyryList = masterBean2
								.getDataList(((FormFieldBean) formFieldMap.get("公用-参与承揽与经办人员及律师费分配-人员")).getName());
						List xzryList = masterBean
								.getDataList(((FormFieldBean) formFieldMap.get("公用-协助人员重复表-姓名")).getName());
						allUsers = LawFormHelper.getCaseInfoAllMembers(StringHelper.concat(sZbls, sAyr, ","), cyryList,
								xzryList);
						break;
					}
				}
			}
			String moduleId = summary.getId().toString();
			String moduleType = "1";
			Integer mTypei = Integer.valueOf(Integer.parseInt(moduleType));

			List colRelationAuthoritys = new ArrayList();
			for (int i = 0; i < allUsers.size(); i++) {
				if (i != 0) {
					Long uid = (Long) allUsers.get(i);
					FormRelationAuthority colRelationAuthority = new FormRelationAuthority();
					colRelationAuthority.setIdIfNew();
					colRelationAuthority.setModuleId(Long.valueOf(Long.parseLong(moduleId)));
					colRelationAuthority.setModuleType(mTypei);
					colRelationAuthority.setUserId(uid);
					colRelationAuthority.setUserType(Integer.valueOf(0));
					colRelationAuthoritys.add(colRelationAuthority);
				}
			}
			DBAgent.saveAll(colRelationAuthoritys);
		} catch (Exception ex) {
			log.error("", ex);
		}
	}

	public FormCacheManager getFormCacheManager() {
		return this.formCacheManager;
	}

	public void setFormCacheManager(FormCacheManager formCacheManager) {
		this.formCacheManager = formCacheManager;
	}

	public ColManager getColManager() {
		return this.colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	public LawDataListManager getLawDataListManager() {
		return this.lawDataListManager;
	}

	public void setLawDataListManager(LawDataListManager lawDataListManager) {
		this.lawDataListManager = lawDataListManager;
	}

	public MessageMailManager getMessageMailManager() {
		return messageMailManager;
	}

	public void setMessageMailManager(MessageMailManager messageMailManager) {
		this.messageMailManager = messageMailManager;
	}

}
