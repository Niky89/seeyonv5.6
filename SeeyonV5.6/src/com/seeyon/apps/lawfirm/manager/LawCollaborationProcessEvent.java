package com.seeyon.apps.lawfirm.manager;

import com.seeyon.apps.collaboration.event.CollaborationProcessEvent;
import com.seeyon.apps.collaboration.manager.ColManager;
import com.seeyon.apps.collaboration.po.ColSummary;
import com.seeyon.apps.doc.manager.DocHierarchyManager;
import com.seeyon.apps.doc.po.DocResourcePO;
import com.seeyon.apps.doc.util.Constants;
import com.seeyon.apps.lawfirm.config.CaseInfoParameterConfig;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.affair.CtpAffair;
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
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.services.document.DocumentService;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LawCollaborationProcessEvent
{
  private static final Log log = LogFactory.getLog(LawCollaborationProcessEvent.class);
  private ColManager colManager;
  private DocHierarchyManager docHierarchyManager;
  private LawDataListManager lawDataListManager;
  private DocumentService documentService;
  
  public DocumentService getDocumentService()
  {
    return this.documentService;
  }
  
  public void setDocumentService(DocumentService documentService)
  {
    this.documentService = documentService;
  }
  
  public LawDataListManager getLawDataListManager()
  {
    return this.lawDataListManager;
  }
  
  public void setLawDataListManager(LawDataListManager lawDataListManager)
  {
    this.lawDataListManager = lawDataListManager;
  }
  
  public DocHierarchyManager getDocHierarchyManager()
  {
    return this.docHierarchyManager;
  }
  
  public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager)
  {
    this.docHierarchyManager = docHierarchyManager;
  }
  
  public ColManager getColManager()
  {
    return this.colManager;
  }
  
  public void setColManager(ColManager colManager)
  {
    this.colManager = colManager;
  }
  
  @ListenEvent(event=CollaborationProcessEvent.class)
  public void onLawProcessEvent(CollaborationProcessEvent event)
    throws Exception
  {
    try
    {
      log.info("立案流程审批事件:" + event.getSource().getClass().getName());
      
      ColSummary summary = this.colManager.getColSummaryById(event.getSummaryId());
      if (summary.getTempleteId() == null) {
        return;
      }
      TemplateManager templeteManager = (TemplateManager)AppContext.getBean("templateManager");
      CtpTemplate template = templeteManager.getCtpTemplate(summary.getTempleteId());
      String templateNo = template.getTempleteNumber();
      if (StringHelper.isNullOrEmpty(templateNo).booleanValue()) {
        return;
      }
      AffairManager affairManager = (AffairManager)AppContext.getBean("affairManager");
      Long affairId = event.getAffairId();
      CtpAffair affair = affairManager.get(affairId);
      System.out.println("流程审批时间,节点权限:" + affair.getNodePolicy());
      if (!"vouch".equals(affair.getNodePolicy())) {
        return;
      }
      String formCa = AppContext.getSystemProperty("lawfirm.formCa");
      String formJa = AppContext.getSystemProperty("lawfirm.formJa");
      String formLa = AppContext.getSystemProperty("lawfirm.formLa");
      if ((!StringHelper.containBySp(formCa, templateNo, ",").booleanValue()) && 
        (!StringHelper.containBySp(formJa, templateNo, ",").booleanValue()) && 
        (!StringHelper.containBySp(formLa, templateNo, ",").booleanValue())) {
        return;
      }
      long formAppid = summary.getFormAppid().longValue();
      long formRecord = summary.getFormRecordid().longValue();
      
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
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
      String[] ajFields = { ((FormFieldBean)formFieldMap.get("公用-案件编号")).getName() };
      List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0, 99999, "", "");
      if (StringHelper.containBySp(formCa, templateNo, ",").booleanValue())
      {
        String ajbhInFlow = (String)masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("案件编号")).getName());
        for (int i = 0; i < formDataMasterBean.size(); i++)
        {
          String ajbhInBaseino = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[0]);
          if ((ajbhInBaseino != null) && 
            (ajbhInBaseino.equals(ajbhInFlow)))
          {
            Long ajId = ((FormDataMasterBean)formDataMasterBean.get(i)).getId();
            StringBuffer sql = new StringBuffer("update " + ajxxMasterTableName + " set " + 
              ((FormFieldBean)formFieldMap.get("公用-案件状态")).getName() + "='已撤案' where ID= " + ajId);
            JDBCAgent dba = new JDBCAgent();
            dba.execute(sql.toString());
            dba.close();
            break;
          }
        }
      }
      else if (StringHelper.containBySp(formJa, templateNo, ",").booleanValue())
      {
        String ajbhInFlow = (String)masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("案件编号")).getName());
        for (int i = 0; i < formDataMasterBean.size(); i++)
        {
          String ajbhInBaseino = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[0]);
          if ((ajbhInBaseino != null) && 
            (ajbhInBaseino.equals(ajbhInFlow)))
          {
            Long ajId = ((FormDataMasterBean)formDataMasterBean.get(i)).getId();
            StringBuffer sql = new StringBuffer("update " + ajxxMasterTableName + " set " + 
              ((FormFieldBean)formFieldMap.get("公用-案件状态")).getName() + "='已结案' where ID= " + ajId);
            JDBCAgent dba = new JDBCAgent();
            dba.execute(sql.toString());
            dba.close();
            break;
          }
        }
      }
      else if (StringHelper.containBySp(formLa, templateNo, ",").booleanValue())
      {
    	  System.out.println("进入立案 事件---------------------------------------------------------------------------------------------");
        DocResourcePO dr = null;
        OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
        
        String bgsId = masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("办公室")).getName()).toString();
        String dq = orgManager.getDepartmentById(Long.valueOf(bgsId)).getCode();
        String lb = ((FormFieldBean)flowFieldMap.get("案件类别-1")).getDisplayValue(masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("案件类别-1")).getName()))[1].toString();
        String lasj = masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("立案时间")).getName()).toString();
        String newAjbh = masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("案件编号")).getName()).toString();//this.lawDataListManager.getAjbh(dq, lb, lasj);
        System.out.println("进入立案 事件---------------------------------------------------------------------------------------------"+newAjbh);
        //masterBean.addFieldValue(((FormFieldBean)flowFieldMap.get("案件编号")).getName(), newAjbh);
        try
        {
          FormService.saveOrUpdateFormData(masterBean, flowFormBean.getId());
        }
        catch (Exception ex)
        {
          log.error("", ex);
        }
        
        String ajName = "[" + newAjbh + "]" + (String)masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("案件名称")).getName());
        System.out.println("进入立案 事件---------------------------------------------------------------------------------------------"+ajName);

        String parentName = "案件文档";
        
        String sZbls = masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("主办律师")).getName()).toString();
        List cyryList = masterBean.getDataList(((FormFieldBean)flowFieldMap.get("参与承揽与经办人员及律师费分配-人员")).getName());
        List xzryList = masterBean.getDataList(((FormFieldBean)flowFieldMap.get("协助人员重复表-姓名")).getName());
        List<Long> caseInfoAllMembers = LawFormHelper.getCaseInfoAllMembers(sZbls, cyryList, xzryList);
        int zblsCount = 0;
        if (!StringHelper.isNullOrEmpty(sZbls).booleanValue()) {
          zblsCount = sZbls.split(",").length;
        }
        long domainId = orgManager.getMemberById((Long)caseInfoAllMembers.get(0)).getOrgAccountId().longValue();
        try
        {
        	//找出文档id感觉应该是律师那个什么文件夹的
          long docLibId = 0L;
          String hqlDocLib = "SELECT d.id FROM DocLibPO d WHERE d.type=0 AND d.name='" + parentName + "' AND d.domainId=" + domainId;
          
          DBAgent dba = new DBAgent();
          List<Long> lstDocId = DBAgent.find(hqlDocLib);
          if ((lstDocId != null) && (!lstDocId.isEmpty())) {
            docLibId = ((Long)lstDocId.get(0)).longValue();
          }
          long parentId = 0L;
          String hqlResource = "SELECT docR.id from DocResourcePO  docR where docR.docLibId = " + docLibId + " and docR.frType = 31 and  docR.frName='" + parentName + "'";
          List<Long> lstparentId = DBAgent.find(hqlResource);
          if ((lstparentId != null) && (!lstparentId.isEmpty())) {
            parentId = ((Long)lstparentId.get(0)).longValue();
          }
          DocResourcePO parent = this.docHierarchyManager.getDocResourceById(Long.valueOf(parentId));
         //报错无法创建.
          if (parent == null) {
            log.error("新文件夹的父文件不存在!");
          } else if (this.docHierarchyManager.deeperThanLimit(parent)) {
            log.error("新文件夹超出树节点最大范围：" + 
              this.docHierarchyManager.getFolderLevelLimit());
          }
          Boolean isDuplicate = Boolean.valueOf(false);
          List<DocResourcePO> listFolders = this.docHierarchyManager.findFoldersWithOutAcl(parent.getId());
          for (DocResourcePO dp : listFolders) {
            if (ajName.equals(dp.getFrName()))
            {
              isDuplicate = Boolean.valueOf(true);
              break;
            }
          }
          if (!isDuplicate.booleanValue())
          {
            try
            {
            	//创建一个文件夹,所有用户为caseInfoallmembers 的第一个
              dr = this.docHierarchyManager.createCommonFolderWithoutAcl(ajName, 
                Long.valueOf(parentId), (Long)caseInfoAllMembers.get(0), false, 
                true, true);
            }
            catch (Exception ex)
            {
              log.error(ex);
              
              dr = this.docHierarchyManager.createCommonFolderWithoutAcl(ajName + UUIDLong.absLongUUID(), 
                Long.valueOf(parentId), (Long)caseInfoAllMembers.get(0), false, 
                true, true);
            }
            //授权,
            LawDocHelper.addLawGrant(dr.getId(), Constants.ACCOUNT_LIB_TYPE.byteValue(), caseInfoAllMembers, zblsCount,true);
            
            //根据案件类别获取创建文件夹的名称
            CaseInfoParameterConfig cfg = LawFormHelper.getCaseInfoParameterConfigByCatalog(lb);
            for (int i = cfg.getJzmlList().size() - 1; i >= 0; i--)
            {
              String _folder = (String)cfg.getJzmlList().get(i);
              this.docHierarchyManager.createCommonFolderWithoutAcl(_folder, 
                dr.getId(), (Long)caseInfoAllMembers.get(0), false, 
                true, true);
            }
            long newId = dr.getId().longValue();
            log.info("新建文档夹的id" + newId);
          }
        }
        catch (Exception ex)
        {
          log.error("", ex);
        }
      }
      return;
    }
    catch (BusinessException be)
    {
      log.error(be.getMessage(), be);
      throw be;
    }
    catch (Exception ex)
    {
      log.error(ex.getMessage());
      throw ex;
    }
  }
}
