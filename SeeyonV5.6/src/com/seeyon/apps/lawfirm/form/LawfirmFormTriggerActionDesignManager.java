package com.seeyon.apps.lawfirm.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormTriggerActionBean;
import com.seeyon.ctp.form.bean.FormTriggerBean;
import com.seeyon.ctp.form.modules.trigger.FormTriggerActionDesignManager;
import com.seeyon.ctp.form.util.Enums;
import com.seeyon.ctp.form.util.FormTriggerUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;

public class LawfirmFormTriggerActionDesignManager
  extends FormTriggerActionDesignManager
{
  private static final Logger log = Logger.getLogger(LawfirmFormTriggerActionDesignManager.class);
  
  public String getId()
  {
    return "lawfirmFormTrigger";
  }
  
  public Integer getSort()
  {
    return Integer.valueOf(9);
  }
  
  public String geti18nName()
  {
    return "律所案件信息更新";
  }
  
  public boolean needHighFormPlugin()
  {
    return false;
  }
  
  public boolean canUse4FormType(Enums.FormType type)
  {
    return true;
  }
  
  public String getActionTypeManagerName()
  {
    return "lawfirmActionTypeManager";
  }
  
  public FormTriggerActionBean getActionFromXML(Element aelement, FormTriggerBean triggerBean)
    throws BusinessException
  {
    log.info("初始化触发动作===" + getName());
    FormTriggerActionBean actionBean = FormTriggerUtil.getActionFromXML(aelement, triggerBean);
    actionBean.setActionManager(this);
    return actionBean;
  }
  
  public String getActionXMLFromActionBean(FormTriggerActionBean actionBean, int aSpace, boolean needFormula)
    throws BusinessException
  {
    log.info("存储触发动作===" + getName());
    return FormTriggerUtil.getActionXMLFromActionBean(actionBean, aSpace, needFormula);
  }
  
  public FormTriggerActionBean getActionFromMap(Map<String, String> map)
    throws BusinessException
  {
    FormTriggerActionBean actionBean = new FormTriggerActionBean();
    String actionId = (String)map.get("actionId");
    actionBean.setId(Long.valueOf(Strings.isBlank(actionId) ? UUIDLong.longUUID() : Long.parseLong(actionId)));
    String type = (String)map.get("actionType");
    actionBean.setType(type);
    
    actionBean.setActionManager(this);
    log.info("新建触发动作===" + getName());
    return actionBean;
  }
  
  public Map<String, Object> getParamMap(FormTriggerActionBean bean, FormBean fb)
    throws BusinessException
  {
    Map actionMap = new HashMap();
    actionMap.put("actionId", bean.getId());
    actionMap.put("actionType", bean.getType());
    
    log.info("修改触发动作===" + getName());
    return actionMap;
  }
  
  public FormTriggerActionBean clone(FormTriggerActionBean newActionBean, FormTriggerActionBean oldActionBean)
    throws CloneNotSupportedException
  {
    return clone(newActionBean, oldActionBean, oldActionBean.getFormTriggerBean());
  }
  
  public FormTriggerActionBean clone(FormTriggerActionBean newActionBean, FormTriggerActionBean oldActionBean, FormTriggerBean triggerBean)
    throws CloneNotSupportedException
  {
    FormTriggerActionBean formTriggerActionBean = newActionBean;
    
    formTriggerActionBean.setId(oldActionBean.getId());
    formTriggerActionBean.setFormTriggerBean(triggerBean);
    return formTriggerActionBean;
  }
  
  public String validateFormTriggerActionField(String fieldName, String newInputType, FormTriggerActionBean actionBean, FormTriggerBean triggerBean, FormBean fb)
    throws BusinessException
  {
    return "1";
  }
}
