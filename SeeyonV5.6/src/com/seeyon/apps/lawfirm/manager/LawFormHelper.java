package com.seeyon.apps.lawfirm.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.lawfirm.config.CaseInfoParameterConfig;
import com.seeyon.apps.lawfirm.config.ConfigHelper;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.content.mainbody.CtpContentAllBean;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.content.CtpContentAll;
import com.seeyon.ctp.common.po.template.CtpTemplate;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormService;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;

public class LawFormHelper
{
  private static final Log log = LogFactory.getLog(LawFormHelper.class);
  
  public static Long getCustomInformationFormId()
  {
    return Long.valueOf(getAppFormIdByCode(AppContext.getSystemProperty("lawfirm.khxxFormCode")));
  }
  
  public static Long getCaseInformationFormId()
  {
    return Long.valueOf(getAppFormIdByCode(AppContext.getSystemProperty("lawfirm.ajxxFormCode")));
  }
  
  public static Long getDialylogFormId()
  {
    return Long.valueOf(getAppFormIdByCode(AppContext.getSystemProperty("lawfirm.journalformCode")));
  }
  
  public static long getAppFormIdByCode(String formCode)
  {
    FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
    FormBean formBean = null;
    try
    {
      formBean = formCacheManager.getFormByFormCode(formCode);
      if (formBean == null)
      {
        TemplateManager templeteManager = (TemplateManager)AppContext.getBean("templateManager");
        CtpTemplate template = templeteManager.getTempleteByTemplateNumber(formCode);
        if (template == null) {
          return -1L;
        }
        CtpContentAll content = (CtpContentAll)DBAgent.get(CtpContentAll.class, template.getBody());
        Long fid = content.getContentTemplateId();
        
        formBean = formCacheManager.getForm(fid.longValue());
      }
    }
    catch (BusinessException e)
    {
      log.error("", e);
      return -1L;
    }
    if (formBean != null)
    {
      long appformid = formBean.getId().longValue();
      return appformid;
    }
    return -1L;
  }
  
  public static Map<String, FormFieldBean> getFormFieldMap(FormBean fb)
  {
    Map<String, FormFieldBean> fieldMap = new HashMap();
    for (FormFieldBean ffb : fb.getAllFieldBeans()) {
      try
      {
        fieldMap.put(ffb.getDisplay(), ffb);
      }
      catch (Exception localException) {}
    }
    return fieldMap;
  }
  
  public static Map getFormData(long formApp, long recordId)
    throws Exception
  {
    FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
    FormBean fb = formCacheManager.getForm(formApp);
    Map<String, Object> fieldMap = new HashMap();
    FormDataMasterBean fdmb = FormService.findDataById(recordId, formApp);
    for (FormFieldBean ffb : fb.getAllFieldBeans()) {
      try
      {
        if (ffb.getEnumId() == 0L)
        {
          fieldMap.put(ffb.getDisplay(), fdmb.getFieldValue(ffb.getName()));
        }
        else
        {
          String displayValue = ffb.getDisplayValue(fdmb.getFieldValue(ffb.getName()))[1].toString();
          fieldMap.put(ffb.getDisplay(), displayValue);
        }
      }
      catch (Exception ex)
      {
        log.error("", ex);
      }
    }
    return fieldMap;
  }
  //验证立案的 多视图标签
  public static Boolean isAllowCaseInfoTabDisplay(CtpContentAllBean bean, int indexParam)
  {
    Boolean indexParamInConfig = Boolean.valueOf(false);
    try
    {
      if (bean.getContentDataId() == null) {
        return Boolean.valueOf(true);
      }
      LawDataListManager lawDataListManager = (LawDataListManager)AppContext.getBean("lawDataListManager");
      Map<String, Object> fieldValueMap = getFormData(bean.getContentTemplateId().longValue(), bean.getContentDataId().longValue());
      String ajlb = fieldValueMap.get("公用-案件类别-1").toString();
      if (StringHelper.isNullOrEmpty(ajlb).booleanValue()) {
        return Boolean.valueOf(true);
      }
      for (CaseInfoParameterConfig ajxxTabDisplayRule : ConfigHelper.getCaseInfoParameterConfigs()) {
        if (indexParam == ajxxTabDisplayRule.getPosition())
        {
          indexParamInConfig = Boolean.valueOf(true);
          if (StringHelper.containBySp(ajxxTabDisplayRule.getAjlb(), ajlb, ",").booleanValue()) {
            return Boolean.valueOf(true);
          }
        }
      }
      return Boolean.valueOf(!indexParamInConfig.booleanValue());
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return Boolean.valueOf(true);
  }
  
  //验证结案的 多视图标签
  public static Boolean isAllowCaseInfoTabDisplayJA(CtpContentAllBean bean, int indexParam,String[] modules)
  {
	  Boolean indexParamInConfig = Boolean.valueOf(false);
	    try
	    {
	      if (bean.getContentDataId() == null) {
	        return Boolean.valueOf(true);
	      }
	      LawDataListManager lawDataListManager = (LawDataListManager)AppContext.getBean("lawDataListManager");
	      Map<String, Object> fieldValueMap = getFormData(bean.getContentTemplateId().longValue(), bean.getContentDataId().longValue());
	      if(fieldValueMap==null){
	    	  return true;
	      }
	      String ajlb = fieldValueMap.get("案件类别-1")==null?"":fieldValueMap.get("案件类别-1").toString();
	      if (StringHelper.isNullOrEmpty(ajlb).booleanValue()) {
	        return Boolean.valueOf(true);
	      }
	      for (CaseInfoParameterConfig ajxxTabDisplayRule : LawFormHelper.getCaseInfoParameterConfigs()) {//用的同一个类下面的 没用配置类里的 方便修改
	    	  
	        if (indexParam == ajxxTabDisplayRule.getPosition())
	        {
	          indexParamInConfig = Boolean.valueOf(true);
	          if (StringHelper.containBySp(ajxxTabDisplayRule.getAjlb(), ajlb, ",").booleanValue()) {
	            return Boolean.valueOf(true);
	          }
	        }
	      }
	      return Boolean.valueOf(!indexParamInConfig.booleanValue());
	    }
	    catch (Exception ex)
	    {
	      log.error("", ex);
	    }
	    return Boolean.valueOf(true);
  }
 
  /**
   * 获取相关人员
   * @param zbls
   * @param cyryList
   * @param xzryList
   * @return
   */
  public static List<Long> getCaseInfoAllMembers(String zbls, List cyryList, List xzryList)
  {
    List<Long> list = new ArrayList();
    if (!StringHelper.isNullOrEmpty(zbls).booleanValue())
    {
      String[] arrayOfString;
      int j = (arrayOfString = zbls.split(",")).length;
      for (int i = 0; i < j; i++)
      {
        String s = arrayOfString[i];
        if (!StringHelper.isNullOrEmpty(s).booleanValue())
        {
          Long val = Long.valueOf(s);
          if (!list.contains(val))
          {
            list.add(val);
            if (list.size() == 1) {
              list.add(val);
            }
          }
        }
      }
    }
    for (Object obj : cyryList) {
      if (obj != null)
      {
        Long val = Long.valueOf(obj.toString());
        if (!list.contains(val))
        {
          list.add(val);
          if (list.size() == 1) {
            list.add(val);
          }
        }
      }
    }
    for (Object obj : xzryList) {
      if (obj != null)
      {
        Long val = Long.valueOf(obj.toString());
        if (!list.contains(val))
        {
          list.add(val);
          if (list.size() == 1) {
            list.add(val);
          }
        }
      }
    }
    return list;
  }
  
  public static Boolean isZbls(Long lawformId, Long lawformRecordId)
  {
    try
    {
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      long ajxxFormId = getAppFormIdByCode(ajxxFormCode);
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      FormBean flowFormBean = formCacheManager.getForm(ajxxFormId);
      Map<String, FormFieldBean> flowFieldMap = getFormFieldMap(flowFormBean);
      FormDataMasterBean masterBean = FormService.findDataById(lawformRecordId.longValue(), lawformId.longValue());
      String sZbls = masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("公用-主办律师")).getName()).toString();
      return StringHelper.containBySp(sZbls, String.valueOf(AppContext.currentUserId()), ",");
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return Boolean.valueOf(false);
  }
  
  public static String getAjbhFieldName()
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      long ajxxFormId = getAppFormIdByCode(ajxxFormCode);
      FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
      Map<String, FormFieldBean> formFieldMap = getFormFieldMap(ajxxFormBean);
      return ((FormFieldBean)formFieldMap.get("公用-案件编号")).getName();
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return "";
  }
  
  public static String getAjmcFieldName()
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      long ajxxFormId = getAppFormIdByCode(ajxxFormCode);
      FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
      Map<String, FormFieldBean> formFieldMap = getFormFieldMap(ajxxFormBean);
      return ((FormFieldBean)formFieldMap.get("公用-案件名称")).getName();
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return "";
  }
  
  public static String getPersonNames(String personIds)
  {
    String ret = "";
    if (StringHelper.isNullOrEmpty(personIds).booleanValue()) {
      return "";
    }
    OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
    String[] ids = personIds.split(",");
    String[] arrayOfString1;
    int j = (arrayOfString1 = ids).length;
    for (int i = 0; i < j; i++)
    {
      String id = arrayOfString1[i];
      if (!StringHelper.isNullOrEmpty(id).booleanValue()) {
        try
        {
          V3xOrgMember vom = orgManager.getMemberById(Long.valueOf(id));
          ret = StringHelper.concat(ret, vom.getName(), "、");
        }
        catch (Exception ex)
        {
          log.error("", ex);
        }
      }
    }
    return ret;
  }
  
  public static CaseInfoParameterConfig getCaseInfoParameterConfigByCatalog(String ajlb)
  {
    List<CaseInfoParameterConfig> list = ConfigHelper.getCaseInfoParameterConfigs();
    for (CaseInfoParameterConfig cfg : list) {
      if (ajlb.equals(cfg.getAjlb())) {
        return cfg;
      }
    }
    return null;
  }
  
  public static String getKssjFieldName()
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String gzrzFormCode = AppContext.getSystemProperty("lawfirm.journalformCode");
      long gzrzFormId = getAppFormIdByCode(gzrzFormCode);
      FormBean gzrzFormBean = formCacheManager.getForm(gzrzFormId);
      Map<String, FormFieldBean> formFieldMap = getFormFieldMap(gzrzFormBean);
      
      return ((FormFieldBean)formFieldMap.get("开始时间")).getName();
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return "";
  }
  
  public static String getJssjFieldName()
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String gzrzFormCode = AppContext.getSystemProperty("lawfirm.journalformCode");
      long gzrzFormId = getAppFormIdByCode(gzrzFormCode);
      FormBean gzrzFormBean = formCacheManager.getForm(gzrzFormId);
      Map<String, FormFieldBean> formFieldMap = getFormFieldMap(gzrzFormBean);
      
      return ((FormFieldBean)formFieldMap.get("结束时间")).getName();
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return "";
  }
  
  public static String getGzsjFieldName()
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String gzrzFormCode = AppContext.getSystemProperty("lawfirm.journalformCode");
      long gzrzFormId = getAppFormIdByCode(gzrzFormCode);
      FormBean gzrzFormBean = formCacheManager.getForm(gzrzFormId);
      Map<String, FormFieldBean> formFieldMap = getFormFieldMap(gzrzFormBean);
      
      return ((FormFieldBean)formFieldMap.get("工作时间")).getName();
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return "";
  }
  
  public static List<CaseInfoParameterConfig> getCaseInfoParameterConfigs()
  {
    List<CaseInfoParameterConfig> list = new ArrayList<CaseInfoParameterConfig>();
    CaseInfoParameterConfig cpc1=new CaseInfoParameterConfig();
    cpc1.setAjlb("非诉讼");
    cpc1.setPosition(0);
    CaseInfoParameterConfig cpc2=new CaseInfoParameterConfig();
    cpc2.setAjlb("刑事");
    cpc2.setPosition(1);
    CaseInfoParameterConfig cpc3=new CaseInfoParameterConfig();
    cpc3.setAjlb("诉讼仲裁");
    cpc3.setPosition(2);
    CaseInfoParameterConfig cpc4=new CaseInfoParameterConfig();
    cpc4.setAjlb("法律顾问");
    cpc4.setPosition(3);
    list.add(cpc1);
    list.add(cpc2);
    list.add(cpc3);
    list.add(cpc4);
    
    return list;
  }
  
}
