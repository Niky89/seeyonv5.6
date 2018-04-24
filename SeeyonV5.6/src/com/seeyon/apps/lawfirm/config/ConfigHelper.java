package com.seeyon.apps.lawfirm.config;

import com.seeyon.apps.lawfirm.manager.LawFormHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigHelper
{
  private static final Log log = LogFactory.getLog(ConfigHelper.class);
  
  public static List<CaseInfoParameterConfig> getCaseInfoParameterConfigs()
  {
    List<CaseInfoParameterConfig> list = new ArrayList();
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxcspzFormCode");
      long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
      FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
      FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
      String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
      Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
      String[] ajFields = { ((FormFieldBean)formFieldMap.get("案件类别")).getName(), 
        ((FormFieldBean)formFieldMap.get("案件类别缩写")).getName(), 
        ((FormFieldBean)formFieldMap.get("页签显示顺序")).getName() };
      List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0, 99999, "", "");
      for (int i = 0; i < formDataMasterBean.size(); i++) {
        try
        {
          String ajlb = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[0]);
          String ajlbsx = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[1]);
          String yqxssx = ((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[2]).toString();
          ajlb = ((FormFieldBean)formFieldMap.get("案件类别")).getDisplayValue(ajlb)[1].toString();
          CaseInfoParameterConfig caseInfoParameterConfig = new CaseInfoParameterConfig();
          caseInfoParameterConfig.setAjlb(ajlb);
          caseInfoParameterConfig.setAjlbsx(ajlbsx);
          caseInfoParameterConfig.setPosition(Integer.parseInt(yqxssx));
          
          FormDataMasterBean masterBean = FormService.findDataById(((FormDataMasterBean)formDataMasterBean.get(i)).getId().longValue(), ajxxFormId);
          
          List jzmlList = masterBean.getDataList(((FormFieldBean)formFieldMap.get("卷宗目录")).getName());
          caseInfoParameterConfig.setJzmlList(jzmlList);
          list.add(caseInfoParameterConfig);
        }
        catch (Exception ex)
        {
          log.error("", ex);
        }
      }
      return list;
    }
    catch (Exception ex)
    {
      log.error("", ex);
    }
    return list;
  }
}
