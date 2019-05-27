package com.seeyon.apps.lawfirm.manager;

import com.seeyon.apps.collaboration.event.CollaborationFinishEvent;
import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.doc.manager.DocHierarchyManager;
import com.seeyon.apps.doc.po.DocResourcePO;
import com.seeyon.apps.doc.util.Constants;
import com.seeyon.apps.lawfirm.config.CaseInfoParameterConfig;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.template.CtpTemplate;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormService;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.services.document.DocumentService;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LawCollaborationFinished {
	private static final Log log = LogFactory.getLog(LawCollaborationFinished.class);
	private ColManager colManager;
	private DocHierarchyManager docHierarchyManager;
	private LawDataListManager lawDataListManager;
	private DocumentService documentService;

	public DocumentService getDocumentService() {
		return this.documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public LawDataListManager getLawDataListManager() {
		return this.lawDataListManager;
	}

	public void setLawDataListManager(LawDataListManager lawDataListManager) {
		this.lawDataListManager = lawDataListManager;
	}

	public DocHierarchyManager getDocHierarchyManager() {
		return this.docHierarchyManager;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}

	public ColManager getColManager() {
		return this.colManager;
	}

	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	@ListenEvent(event = CollaborationFinishEvent.class)
	public void onLawFinishedEvent(CollaborationFinishEvent event) throws Exception {
		try {
			ColSummary summary = this.colManager.getColSummaryById(event.getSummaryId());
			if (summary.getTempleteId() == null) {
				return;
			}
			TemplateManager templeteManager = (TemplateManager) AppContext.getBean("templateManager");
			CtpTemplate template = templeteManager.getCtpTemplate(summary.getTempleteId());
			String templateNo = template.getTempleteNumber();
			if (StringHelper.isNullOrEmpty(templateNo).booleanValue()) {
				return;
			}
			String formCa = AppContext.getSystemProperty("lawfirm.formCa");
			String formJa = AppContext.getSystemProperty("lawfirm.formJa");
			String formLa = AppContext.getSystemProperty("lawfirm.formLa");
			if ((!StringHelper.containBySp(formCa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formJa, templateNo, ",").booleanValue())
					&& (!StringHelper.containBySp(formLa, templateNo, ",").booleanValue())) {
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
			if (ajxxFormId == -1L) {
				return;
			}
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
								+ ((FormFieldBean) formFieldMap.get("公用-案件状态")).getName() + "='已撤案' where ID= " + ajId);
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
								+ ((FormFieldBean) formFieldMap.get("公用-案件状态")).getName() + "='已结案' where ID= " + ajId);
						JDBCAgent dba = new JDBCAgent();
						dba.execute(sql.toString());
						dba.close();

						FormDataMasterBean masterBean2 = FormService.findDataById(
								((FormDataMasterBean) formDataMasterBean.get(i)).getId().longValue(), ajxxFormId);
						Object oAjbh = masterBean2.getAllDataMap()
								.get(((FormFieldBean) formFieldMap.get("公用-案件编号")).getName());
						if (oAjbh == null) {
							return;
						}
						String ajbh = oAjbh.toString();

						String sZbls = "";
						if (masterBean2
								.getFieldValue(((FormFieldBean) formFieldMap.get("公用-主办律师")).getName()) != null) {
							sZbls = masterBean2.getFieldValue(((FormFieldBean) formFieldMap.get("公用-主办律师")).getName())
									.toString();
						}
						List cyryList = masterBean2
								.getDataList(((FormFieldBean) formFieldMap.get("公用-参与承揽与经办人员及律师费分配-人员")).getName());
						List xzryList = masterBean2
								.getDataList(((FormFieldBean) formFieldMap.get("公用-协助人员重复表-姓名")).getName());
						int zblsCount = 0;
						if (!StringHelper.isNullOrEmpty(sZbls).booleanValue()) {
							zblsCount = sZbls.split(",").length;
						}
						List<Long> caseInfoAllMembers = LawFormHelper.getCaseInfoAllMembers(sZbls, cyryList, xzryList);

						String fullPath2 = "案件文档\\[" + ajbh + "]" + "\\电子档案（PDF）";
						Long fid2 = LawDocHelper.findFolderByFullPath(fullPath2, null, null, null, null, null, null,
								null);
						if (fid2 == null || fid2 == -1 || fid2 == 0) {
							break;
						}
						LawDocHelper.addLawGrant(fid2, Constants.ACCOUNT_LIB_TYPE.byteValue(), caseInfoAllMembers,
								zblsCount, Boolean.valueOf(true));

						break;
					}
				}
			} else if (StringHelper.containBySp(formLa, templateNo, ",").booleanValue()) {
				log.info("立案流程结束事件:" + event.getAffairId());
				log.info("ColFinished.....00001");
				DocResourcePO dr = null;
				OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");

				Object objAjbh = masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("案件编号")).getName());
				log.info("ColFinished.....00002 " + objAjbh);
				// if ((objAjbh != null) &&
				// (!StringHelper.isNullOrEmpty(objAjbh.toString()).booleanValue()))
				// {
				// return;
				// }
				log.info("ColFinished.....00003");

				String bgsId = masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("办公室")).getName()).toString();
				log.info("ColFinished.....00004 " + bgsId);
				String dq = orgManager.getDepartmentById(Long.valueOf(bgsId)).getCode();
				log.info("ColFinished.....00005 " + dq);
				String lb = ((FormFieldBean) flowFieldMap.get("案件类别-1")).getDisplayValue(
						masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("案件类别-1")).getName()))[1].toString();
				log.info("ColFinished.....00006 " + lb);
				String lasj = masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("立案时间")).getName()).toString();
				log.info("ColFinished.....00007 " + lasj);
				String newAjbh = objAjbh.toString();// this.lawDataListManager.getAjbh(dq,
													// lb, lasj);
				// try
				// {
				// log.info("ColFinished.....00008 " +
				// CurrentUser.get().getName() + "," + templateNo + "," +
				// summary.getSubject() + "," + newAjbh);
				// }
				// catch (Exception ex)
				// {
				// log.error("", ex);
				// }
				// masterBean.addFieldValue(((FormFieldBean)flowFieldMap.get("案件编号")).getName(),
				// newAjbh);
				log.info("ColFinished.....00009 ");
				List wtrList = masterBean.getDataList(((FormFieldBean) flowFieldMap.get("委托人重复表-委托人")).getName());
				log.info("ColFinished.....00010");
				String newAjmc = this.lawDataListManager.getAjmc(wtrList, lb);
				log.info("ColFinished.....00011 " + newAjmc);
				masterBean.addFieldValue(((FormFieldBean) flowFieldMap.get("案件名称")).getName(), newAjmc);
				log.info("ColFinished.....00012");
				try {
					FormService.saveOrUpdateFormData(masterBean, flowFormBean.getId());
					log.info("ColFinished.....00013");
				} catch (Exception ex) {
					log.error("", ex);
				}
				log.info("ColFinished.....00014");
				String ajName = "[" + newAjbh + "]"
						+ (String) masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("案件名称")).getName());
				log.info("ColFinished.....00015 " + ajName);
				String parentName = "案件文档";

				String sZbls = masterBean.getFieldValue(((FormFieldBean) flowFieldMap.get("主办律师")).getName())
						.toString();
				log.info("ColFinished.....00016 " + sZbls);
				List cyryList = masterBean
						.getDataList(((FormFieldBean) flowFieldMap.get("参与承揽与经办人员及律师费分配-人员")).getName());
				log.info("ColFinished.....00017 ");
				List xzryList = masterBean.getDataList(((FormFieldBean) flowFieldMap.get("协助人员重复表-姓名")).getName());
				log.info("ColFinished.....00018 ");
				List<Long> caseInfoAllMembers = LawFormHelper.getCaseInfoAllMembers(sZbls, cyryList, xzryList);
				log.info("ColFinished.....00019 ");
				int zblsCount = 0;
				if (!StringHelper.isNullOrEmpty(sZbls).booleanValue()) {
					zblsCount = sZbls.split(",").length;
				}
				log.info("ColFinished.....00020 ");

				long domainId = orgManager.getMemberById((Long) caseInfoAllMembers.get(0)).getOrgAccountId()
						.longValue();
				log.info("ColFinished.....00021 ");
				try {
					long docLibId = 0L;
					String hqlDocLib = "SELECT d.id FROM DocLibPO d WHERE d.type=0 AND d.name='" + parentName
							+ "' AND d.domainId=" + domainId;

					DBAgent dba = new DBAgent();
					log.info("ColFinished.....00022 ");
					List<Long> lstDocId = DBAgent.find(hqlDocLib);
					log.info("ColFinished.....00023 ");
					if ((lstDocId != null) && (!lstDocId.isEmpty())) {
						docLibId = ((Long) lstDocId.get(0)).longValue();
					}
					long parentId = 0L;
					log.info("ColFinished.....00024");
					String hqlResource = "SELECT docR.id from DocResourcePO  docR where docR.docLibId = " + docLibId
							+ " and docR.frType = 31 and  docR.frName='" + parentName + "'";
					log.info("ColFinished.....00025 " + hqlResource);
					List<Long> lstparentId = DBAgent.find(hqlResource);
					log.info("ColFinished.....00026");
					if ((lstparentId != null) && (!lstparentId.isEmpty())) {
						parentId = ((Long) lstparentId.get(0)).longValue();
					}
					log.info("ColFinished.....00027 " + parentId);

					DocResourcePO parent = this.docHierarchyManager.getDocResourceById(Long.valueOf(parentId));
					log.info("ColFinished.....00028 ");
					if (parent == null) {
						log.error("新文件夹的父文件不存在!");
					} else if (this.docHierarchyManager.deeperThanLimit(parent)) {
						log.error("新文件夹超出树节点最大范围：" + this.docHierarchyManager.getFolderLevelLimit());
					}
					Boolean isDuplicate = Boolean.valueOf(false);
					List<DocResourcePO> listFolders = this.docHierarchyManager.findFoldersWithOutAcl(parent.getId());
					log.info("ColFinished.....00029");
					for (DocResourcePO dp : listFolders) {
						if (ajName.equals(dp.getFrName())) {
							isDuplicate = Boolean.valueOf(true);
							break;
						}
					}
					log.info("ColFinished.....00030");
					if (!isDuplicate.booleanValue()) {
						try {
							log.info("ColFinished.....00031");
							dr = this.docHierarchyManager.createCommonFolderWithoutAcl(ajName, Long.valueOf(parentId),
									(Long) caseInfoAllMembers.get(0), false, true, true);
							log.info("ColFinished.....00032");
						} catch (Exception ex) {
							log.error(ex);
						}
						log.info("ColFinished.....00033");
//开启一条新线程来执行授权。希望能同现在执行内容分离
						new AclThread(dr, caseInfoAllMembers, zblsCount);

						log.info("ColFinished.....00034");
						CaseInfoParameterConfig cfg = LawFormHelper.getCaseInfoParameterConfigByCatalog(lb);

						log.info("ColFinished.....00035");
						for (int i = cfg.getJzmlList().size() - 1; i >= 0; i--) {
							String _folder = (String) cfg.getJzmlList().get(i);
							log.info("ColFinished.....00036");
							this.docHierarchyManager.createCommonFolderWithoutAcl(_folder, dr.getId(),
									(Long) caseInfoAllMembers.get(0), false, true, true);
							log.info("ColFinished.....00037");
						}
						long newId = dr.getId().longValue();
						log.info("新建文档夹的id" + newId);
					}
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
			return;
		} catch (BusinessException be) {
			log.error(be.getMessage(), be);
			throw be;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw ex;
		}
	}

	class AclThread implements Runnable {
		DocResourcePO dr=null;
		List<Long> caseInfoAllMembers =null;
		int zblsCount = 0;
		public AclThread(DocResourcePO dr,List<Long> caseInfoAllMembers,int zblsCount) {
			this.dr=dr;
			this.caseInfoAllMembers=caseInfoAllMembers;
			this.zblsCount=zblsCount;
		}
		@Override
		public void run() {
			try {
				LawDocHelper.addLawGrant(dr.getId(), Constants.ACCOUNT_LIB_TYPE.byteValue(),
						caseInfoAllMembers, zblsCount, Boolean.valueOf(false));
			} catch (BusinessException e) {
				e.printStackTrace();
			}


		}
	}
}
