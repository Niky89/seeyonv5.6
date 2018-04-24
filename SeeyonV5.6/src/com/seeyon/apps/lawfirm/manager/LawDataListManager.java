package com.seeyon.apps.lawfirm.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.lawfirm.config.CaseInfoParameterConfig;
import com.seeyon.apps.lawfirm.util.StringHelper;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.ctpenumnew.manager.EnumManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.common.po.content.CtpContentAll;
import com.seeyon.ctp.common.po.ctpenumnew.CtpEnumItem;
import com.seeyon.ctp.common.po.template.CtpTemplate;
import com.seeyon.ctp.common.template.manager.TemplateManager;
import com.seeyon.ctp.form.bean.FormAuthViewBean;
import com.seeyon.ctp.form.bean.FormAuthViewFieldBean;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormBindAuthBean;
import com.seeyon.ctp.form.bean.FormBindBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormFieldBean;
import com.seeyon.ctp.form.bean.FormFormulaBean;
import com.seeyon.ctp.form.bean.FormTableBean;
import com.seeyon.ctp.form.bean.FormViewBean;
import com.seeyon.ctp.form.modules.engin.base.formData.FormDataDAO;
import com.seeyon.ctp.form.modules.engin.formula.FormulaUtil;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.form.service.FormService;
import com.seeyon.ctp.form.util.Enums;
import com.seeyon.ctp.form.util.FormUtil;
import com.seeyon.ctp.form.util.StringUtils;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.StringUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.annotation.AjaxAccess;

public class LawDataListManager
{
  private static final Log LOGGER = LogFactory.getLog(LawDataListManager.class);
  private FormCacheManager formCacheManager;
  private OrgManager orgManager;
  
  public OrgManager getOrgManager()
  {
    return this.orgManager;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public FormCacheManager getFormCacheManager()
  {
    return this.formCacheManager;
  }
  
  public void setFormCacheManager(FormCacheManager formCacheManager)
  {
    this.formCacheManager = formCacheManager;
  }
  @AjaxAccess
  public String getFieldByFormId(String formIds)
  {
    String fields = "";
    long formId = Long.parseLong(formIds);
    FormBean formBean = this.formCacheManager.getForm(formId);
    FormFieldBean fieldBean = null;
    if (formBean != null)
    {
      fieldBean = formBean.getFieldBeanByDisplay("委托人重复表-委托人");
      if (fieldBean != null) {
        fields = fieldBean.getName();
      }
      fieldBean = null;
      try
      {
        fieldBean = formBean.getFieldBeanByDisplay("对方当事人重复表-对方当事人");
        fields = fields + "," + fieldBean.getName();
      }
      catch (Exception localException) {}
    }
    return fields;
  }
  @AjaxAccess
  public String getPostNameByPostId(long postId)
  {
    String postName = "";
    try
    {
      postName = this.orgManager.getPostById(Long.valueOf(postId)).getName();
    }
    catch (BusinessException e)
    {
      LOGGER.error("", e);
    }
    return postName;
  }
  @AjaxAccess
  public FlipInfo queryLawDataList(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    Long formId = Long.valueOf(
      Long.parseLong((String)params.get("formId")));
    String givenPerson = (String)params.get("givenPerson");
    String otherPerson = (String)params.get("otherPerson");
    if (StringHelper.isNullOrEmpty(givenPerson).booleanValue()) {
      givenPerson = "UNKNOWNPERSON";
    }
    if (StringHelper.isNullOrEmpty(otherPerson).booleanValue()) {
      otherPerson = "UNKNOWNPERSON";
    }
    String allPerson = "";
    String sAllPsns = StringHelper.concat(givenPerson, otherPerson, ",");
    if (!StringHelper.isNullOrEmpty(sAllPsns).booleanValue())
    {
      String[] psns = sAllPsns.split(",");
      String[] arrayOfString1;
      int j = (arrayOfString1 = psns).length;
      for (int i = 0; i < j; i++)
      {
        String psn = arrayOfString1[i];
        if (!StringHelper.isNullOrEmpty(psn).booleanValue())
        {
          if (!allPerson.equals("")) {
            allPerson = allPerson + ",";
          }
          allPerson = allPerson + "'" + psn + "'";
        }
      }
    }
    FormBean formBean = null;
    formBean = this.formCacheManager.getForm(formId.longValue());
    FormTableBean masterTableBean = formBean.getMasterTableBean();
    String tableName = masterTableBean.getTableName();
    Object flowFieldMap = LawFormHelper.getFormFieldMap(formBean);
    if (formBean != null) {
      try
      {
        String ajlbFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-案件类别-1")).getName();
        String ajmcFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-案件名称")).getName();
        String ajztFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-案件状态")).getName();
        String wtrFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-委托人重复表-委托人")).getName();
        String dfdsrFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-对方当事人重复表-对方当事人")).getName();
        String zblsFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-主办律师")).getName();
        String bgsFld = ((FormFieldBean)((Map)flowFieldMap).get("公用-办公室")).getName();
        
        String wtrTableName = ((FormFieldBean)((Map)flowFieldMap).get("公用-委托人重复表-委托人")).getOwnerTableName();
        String dfdsrTableName = ((FormFieldBean)((Map)flowFieldMap).get("公用-对方当事人重复表-对方当事人")).getOwnerTableName();
        
        String sql = "select f.id,c.showvalue ajlb,f." + ajmcFld + " ajmc,f." + ajztFld + " ajzt,'' wtr,'' dfdsr," + zblsFld + " zbls,o.name" + 
          " from " + masterTableBean.getTableName() + " f,org_unit o,ctp_enum_item c " + 
          " where CAST(c.id as VARCHAR(200))=f." + ajlbFld + 
          " and o.id=f." + bgsFld + 
          " and (f.id in(select s1.formmain_id from " + wtrTableName + 
          " s1 where s1." + wtrFld + " in (" + allPerson + ")) or " + 
          " f.id in(select s2.formmain_id from " + dfdsrTableName + 
          " s2 where s2." + dfdsrFld + " in (" + allPerson + ")))  order by f.field0091 desc";
        
        System.out.println("利冲检查sql=========" + sql.toString());
        JDBCAgent dba = new JDBCAgent();
        try
        {
          flipInfo = dba.findByPaging(sql.toString(), flipInfo);
          for (Object obj : flipInfo.getData())
          {
            Map map = (Map)obj;
            
            String zbls = LawFormHelper.getPersonNames(map.get("zbls").toString());
            List list = null;
            dba.execute("select " + wtrFld + " from " + wtrTableName + " where formmain_id= " + map.get("id").toString());
            list = dba.resultSetToList();
            String wtr = StringHelper.joinList(list, "、");
            
            dba.execute("select " + dfdsrFld + " from " + dfdsrTableName + " where formmain_id= " + map.get("id").toString());
            list = dba.resultSetToList();
            String dfdsr = StringHelper.joinList(list, "、");
            
            map.put("zbls", zbls);
            map.put("wtr", wtr);
            map.put("dfdsr", dfdsr);
          }
          dba.close();
        }
        catch (SQLException e)
        {
          LOGGER.info("数据库操作异常", e);
        }
        LOGGER.info("不存在该表单");
      }
      catch (Exception e)
      {
        LOGGER.info("查询案件管理异常" + e);
      }
    }
    return flipInfo;
  }
  @AjaxAccess
  public FlipInfo getFormMasterDataListByFormId(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    Long formId = Long.valueOf((String)params.get("formId"));
    Long formTemplateId = null;
    if (!StringHelper.isNullOrEmpty((String)params.get("formTemplateId")).booleanValue()) {
      formTemplateId = Long.valueOf((String)params.get("formTemplateId"));
    }
    FormBean formBean = this.formCacheManager.getForm(formId.longValue());
    List subTables = formBean.getSubTableBean();
    FormTableBean masterTableBean = formBean.getMasterTableBean();
    List<FormFieldBean> allFields = formBean.getAllFieldBeans();
    FormBindBean bindBean = formBean.getBind();
    List bindAuthList = null;
    if (formBean.getFormType() == Enums.FormType.baseInfo.getKey())
    {
      bindAuthList = new ArrayList();
      bindAuthList.add((FormBindAuthBean)bindBean.getUnFlowTemplateMap().values().iterator().next());
    }
    else
    {
      bindAuthList = bindBean.getUnflowFormBindAuthByUserId(Long.valueOf(AppContext.currentUserId()));
    }
    FormBindAuthBean firstFormBindAuthBean = null;
    StringBuffer whereSb = new StringBuffer("");
    StringBuffer subTablewhereSb = new StringBuffer("");
    
    StringBuffer userCondition = new StringBuffer("");
    int i = 0;
    do
    {
      FormBindAuthBean bindAuth = (FormBindAuthBean)bindAuthList.get(i);
      if (i == 0) {
        firstFormBindAuthBean = bindAuth;
      }
      FormFormulaBean formulaBean = bindAuth.getFormFormulaBean();
      if (formulaBean != null)
      {
        if (Strings.isNotBlank(userCondition.toString())) {
          userCondition.append(" or ");
        }
        userCondition.append(FormulaUtil.procReplaceNull(formulaBean.getExecuteFormulaForSQL()));
      }
      else
      {
        userCondition = new StringBuffer("");
        break;
      }
      i++;
    } while (i < bindAuthList.size());
    FormBindAuthBean bindAuth;
    if (!StringUtil.checkNull(userCondition.toString()))
    {
      for (FormFieldBean fieldBean : allFields)
      {
        int index = userCondition.indexOf(fieldBean.getName());
        if (index != -1) {
          userCondition = userCondition.replace(index, index + fieldBean.getName().length(), 
            fieldBean.getOwnerTableName() + "." + fieldBean.getName());
        }
      }
      whereSb.append(" (");
      whereSb.append(userCondition);
      whereSb.append(")");
    }
    List pa = new ArrayList();
    
    Iterator ite = params.keySet().iterator();
    while (ite.hasNext())
    {
      String key = (String)ite.next();
      if ((params.get(key) != null) && (!Strings.isBlank(String.valueOf(params.get(key))))) {
        if ((!"sortStr".equals(key)) && (!"formId".equals(key)) && (!"formTemplateId".equals(key)))
        {
          StringBuffer conditionSb = new StringBuffer("");
          if ("highquery".equals(key))
          {
            List tempList = new ArrayList();
            Object temO = params.get(key);
            if ((temO instanceof Map)) {
              tempList.add((Map)temO);
            } else {
              tempList.addAll((List)temO);
            }
            Object[] o = FormUtil.getSQLStr(tempList, formBean, false);
            pa = (List)o[1];
            conditionSb.append(o[0]);
          }
          else
          {
            try
            {
              conditionSb.append(getQuerySql4Condition(formBean, pa, key, params.get(key)));
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
          if ((!StringUtil.checkNull(whereSb.toString().trim())) && 
            (!StringUtil.checkNull(conditionSb.toString().trim()))) {
            whereSb.append(" and ");
          }
          if (!StringUtil.checkNull(conditionSb.toString().trim())) {
            whereSb.append(conditionSb);
          }
        }
      }
    }
    String[] showFields = firstFormBindAuthBean.getFieldStr4SQL();
    String sortFields = firstFormBindAuthBean.getSortStr(masterTableBean.getTableName());
    String[] fieldStrs = Enums.MasterTableField.getKeys();
    for (int j = 0; j < fieldStrs.length; j++) {
      fieldStrs[j] = (masterTableBean.getTableName() + "." + fieldStrs[j]);
    }
    for (int j = 0; j < showFields.length; j++) {
      if (showFields[j].indexOf(".") == -1) {
        showFields[j] = (masterTableBean.getTableName() + "." + showFields[j]);
      }
    }
    Set<String> fieldSet = new LinkedHashSet();
    fieldSet.addAll(Arrays.asList(showFields));
    fieldSet.addAll(Arrays.asList(fieldStrs));
    StringBuffer from = new StringBuffer(" ");
    
    sortFields = " order by " + (Strings.isBlank(sortFields) ? "" : new StringBuilder(String.valueOf(sortFields)).append(",").toString());
    if (!sortFields.contains("start_date")) {
      sortFields = sortFields + masterTableBean.getTableName() + ".start_date,";
    }
    sortFields = sortFields + masterTableBean.getTableName() + ".id";
    
    String[] fields = new String[fieldSet.size()];
    int j = 0;
    for (String s : fieldSet) {
      fields[(j++)] = s;
    }
    String fieldNames = StringUtils.arrayToString(fields);
    StringBuffer sql = new StringBuffer("select DISTINCT ");
    sql.append(fieldNames);
    sql.append(" from ").append(masterTableBean.getTableName());
    for (FormFieldBean fieldBean : allFields) {
      if ((!fieldBean.isConstantField()) && (!fieldBean.isMasterField()))
      {
        if ((from.indexOf(fieldBean.getOwnerTableName()) == -1) && (sql.indexOf(fieldBean.getName()) != -1)) {
          from.append(",").append(fieldBean.getOwnerTableName());
        }
        if ((from.indexOf(fieldBean.getOwnerTableName()) == -1) && (whereSb.indexOf(fieldBean.getName()) != -1)) {
          from.append(",").append(fieldBean.getOwnerTableName());
        }
        if ((from.indexOf(fieldBean.getOwnerTableName()) == -1) && (sortFields.indexOf(fieldBean.getName()) != -1)) {
          from.append(",").append(fieldBean.getOwnerTableName());
        }
      }
    }
    sql.append(from);
    for (int k = 0; k < subTables.size(); k++) {
      if (!((FormTableBean)subTables.get(k)).isMainTable()) {
        if ((sql.indexOf(((FormTableBean)subTables.get(k)).getTableName()) != -1) && 
          (subTablewhereSb.indexOf(((FormTableBean)subTables.get(k)).getTableName()) == -1))
        {
          subTablewhereSb.append(" (").append(((FormTableBean)subTables.get(k)).getTableName()).append(".").append(Enums.SubTableField.formmain_id.getKey()).append("=").append(formBean.getMasterTableBean().getTableName()).append(".").append(Enums.MasterTableField.id.getKey()).append(") ");
          subTablewhereSb.append(" and ");
        }
      }
    }
    int andIndex = subTablewhereSb.lastIndexOf("and ");
    if (andIndex != -1) {
      subTablewhereSb = subTablewhereSb.replace(andIndex, andIndex + "and ".length(), "");
    }
    if (!StringUtil.checkNull(subTablewhereSb.toString().trim())) {
      whereSb = subTablewhereSb.append(" and ").append(whereSb);
    }
    String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
    long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
    String cyryTableName;
    if (formBean.getId().longValue() == ajxxFormId)
    {
      Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(formBean);
      String cyryFieldName = ((FormFieldBean)flowFieldMap.get("公用-参与承揽与经办人员及律师费分配-人员")).getName();
      String xzryFieldName = ((FormFieldBean)flowFieldMap.get("公用-协助人员重复表-姓名")).getName();
      cyryTableName = ((FormFieldBean)flowFieldMap.get("公用-参与承揽与经办人员及律师费分配-人员")).getOwnerTableName();
      String xzryTableName = ((FormFieldBean)flowFieldMap.get("公用-协助人员重复表-姓名")).getOwnerTableName();
      String ajztFieldName = ((FormFieldBean)flowFieldMap.get("公用-案件状态")).getName();
      String tbrFieldName = ((FormFieldBean)flowFieldMap.get("公用-填报人")).getName();
      String _where = "((" + masterTableBean.getTableName() + ".id in (select " + cyryTableName + "." + 
        Enums.SubTableField.formmain_id.getKey() + " from " + cyryTableName + 
        " where " + cyryTableName + "." + cyryFieldName + "='" + AppContext.currentUserId() + "')" + 
        " or " + 
        masterTableBean.getTableName() + ".id in (select " + xzryTableName + "." + 
        Enums.SubTableField.formmain_id.getKey() + " from " + xzryTableName + 
        " where " + xzryTableName + "." + xzryFieldName + "='" + AppContext.currentUserId() + "')" + 
        " or " + masterTableBean.getTableName() + "." + tbrFieldName + "='" + AppContext.currentUserId() + "'" + 
        ") AND (" + masterTableBean.getTableName() + "." + ajztFieldName + " NOT IN ('已撤案','已结案') OR " + 
        masterTableBean.getTableName() + "." + ajztFieldName + " IS NULL))";
      if (whereSb.length() == 0) {
        whereSb.append(_where);
      } else {
        whereSb.append(" and " + _where);
      }
    }
    if (!StringUtil.checkNull(whereSb.toString().trim()))
    {
      sql.append(" where ");
      sql.append(FormUtil.changeAndAddNullWhereSql(whereSb.toString()));
    }
    sql.append(sortFields);
    FormDataDAO formDataDAO = (FormDataDAO)AppContext.getBean("formDataDAO");
    
    LOGGER.info("查询语句=======" + sql.toString());
    
    formDataDAO.selectMasterDataList(flipInfo, masterTableBean, sql.toString().replace("<> null", "is not null"), pa);
    
    List<Map> formDataMasterList = flipInfo.getData();
    for (Map lineValues : formDataMasterList)
    {
      Iterator it = lineValues.keySet().iterator();
      Map addedMap = new HashMap();
      while (it.hasNext())
      {
        String key = (String)it.next();
        FormFieldBean fieldBean = formBean.getFieldBeanByName(key);
        if (fieldBean != null)
        {
          Object value = lineValues.get(key);
          try
          {
            Object[] objs = (Object[])null;
            if (fieldBean == null) {
              continue;
            }
            fieldBean = fieldBean.findRealFieldBean();
            if (fieldBean == null) {
              continue;
            }
            objs = fieldBean.getDisplayValue(value);
            lineValues.put(key, objs[1]);
          }
          catch (Exception e)
          {
            throw new BusinessException(e);
          }
        }
        Enums.MasterTableField me = Enums.MasterTableField.getEnumByKey(key);
        if (me != null) {
          fieldBean = me.getFormFieldBean();
        }
        if (fieldBean != null)
        {
          Object value = lineValues.get(key);
          try
          {
            Object[] objs = (Object[])null;
            objs = fieldBean.getDisplayValue(value);
            lineValues.put(key, value);
            
            addedMap.put(key.replace("_", ""), objs[1]);
          }
          catch (Exception e)
          {
            throw new BusinessException(e);
          }
        }
      }
      lineValues.putAll(addedMap);
    }
    return flipInfo;
  }
  @AjaxAccess
  private String getQuerySql4Condition(FormBean formBean, List pa, String key, Object values)
    throws Exception
  {
    StringBuffer conditionSb = new StringBuffer("");
    if ((values instanceof List))
    {
      List v = (List)values;
      if ((v.size() == 2) && ((Strings.isNotBlank((String)v.get(0))) || (Strings.isNotBlank((String)v.get(1)))))
      {
        String mValue = (String)v.get(0);
        String maxValue = (String)v.get(1);
        if (Strings.isNotBlank(mValue))
        {
          Object mo = mValue.length() < 12 ? DateUtil.parseTimestamp(mValue + " 00:00:00", "yyyy-MM-dd HH:mm:ss") : DateUtil.parseTimestamp(mValue, "yyyy-MM-dd HH:mm");
          conditionSb.append(" ").append(key).append(" >= ? ");
          pa.add(mo);
        }
        if (Strings.isNotBlank(maxValue))
        {
          if (Strings.isNotBlank(mValue)) {
            conditionSb.append("and");
          }
          Object mo = mValue.length() < 12 ? DateUtil.parseTimestamp(maxValue + " 23:59:59", "yyyy-MM-dd HH:mm:ss") : DateUtil.parseTimestamp(maxValue, "yyyy-MM-dd HH:mm");
          conditionSb.append(" ").append(key).append(" <= ? ");
          pa.add(mo);
        }
      }
    }
    else if ((values instanceof Map))
    {
      Map v = (Map)values;
      List list = new ArrayList();
      list.add(v);
      Object[] o = FormUtil.getSQLStr(list, formBean, false);
      if (!"".equals(o[0]))
      {
        pa.addAll((List)o[1]);
        conditionSb.append(o[0]);
      }
    }
    else
    {
      String v = String.valueOf(values);
      conditionSb.append(" ").append(key).append(" <= ? ");
      pa.add(v);
    }
    return conditionSb.toString();
  }
  @AjaxAccess
  public FlipInfo queryJournalDataList(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    Long formId = Long.valueOf(
      Long.parseLong((String)params.get("formId")));
    String lawCode = (String)params.get("lawCode");
    FormBean formBean = null;
    formBean = this.formCacheManager.getForm(formId.longValue());
    Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(formBean);
    if (formBean != null)
    {
      FormTableBean masterTableBean = formBean.getMasterTableBean();
      String tableName = masterTableBean.getTableName();
      StringBuffer sql = new StringBuffer("select f.state,");
      sql.append(" f.id,c.showvalue,o.name,f." + ((FormFieldBean)flowFieldMap.get("案件名称")).getName() + " ajmc " + 
        ",f." + ((FormFieldBean)flowFieldMap.get("客户名称")).getName() + " klmc " + 
        ",f." + ((FormFieldBean)flowFieldMap.get("工作时间")).getName() + " gzsj " + 
        ",f." + ((FormFieldBean)flowFieldMap.get("工作描述")).getName() + " gzms ");
      sql.append(" from  " + tableName + " f,org_member o,ctp_enum_item c");
      sql.append(" where f." + ((FormFieldBean)flowFieldMap.get("案件编号")).getName() + 
        "='" + lawCode + "' and  f." + ((FormFieldBean)flowFieldMap.get("记录人")).getName() + 
        "=o.id and f." + ((FormFieldBean)flowFieldMap.get("日志类型")).getName() + 
        "=CAST(c.id as VARCHAR(200)) and c.showvalue='业务日志'");
      System.out.println("查找工作日誌sql=========" + sql.toString());
      JDBCAgent dba = new JDBCAgent();
      try
      {
        flipInfo = dba.findByPaging(sql.toString(), flipInfo);
        dba.close();
      }
      catch (SQLException e)
      {
        LOGGER.info("查询工作日志数据库表异常");
      }
    }
    return flipInfo;
  }
  @AjaxAccess
  public FlipInfo queryNewJournalDataList(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    Long formId = Long.valueOf(
      Long.parseLong((String)params.get("formId")));
    String lawCode = (String)params.get("lawCode");
    FormBean formBean = null;
    formBean = this.formCacheManager.getForm(formId.longValue());
    Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(formBean);
    if (formBean != null)
    {
      FormTableBean masterTableBean = formBean.getMasterTableBean();
      String tableName = masterTableBean.getTableName();
      StringBuffer sql = new StringBuffer("select f.state,");
      sql.append(" f.id,c.showvalue,o.name,f." + ((FormFieldBean)flowFieldMap.get("案件名称")).getName() + " ajmc " + 
        ",p.name klmc " + 
        ",f." + ((FormFieldBean)flowFieldMap.get("工作时间")).getName() + " gzsj " + 
        ",f." + ((FormFieldBean)flowFieldMap.get("工作描述")).getName() + " gzms ");
      sql.append(" from  " + tableName + " f,org_member o,org_member p,ctp_enum_item c");
      sql.append(" where f." + ((FormFieldBean)flowFieldMap.get("案件编号")).getName() + 
        "='" + lawCode + "' and  f." + ((FormFieldBean)flowFieldMap.get("记录人")).getName() + 
        "=o.id and f."+((FormFieldBean)flowFieldMap.get("交办人")).getName()+"=p.id and f." + ((FormFieldBean)flowFieldMap.get("日志类型")).getName() + 
        "=CAST(c.id as VARCHAR(200)) and c.showvalue='业务日志'");
      System.out.println("查找工作日誌sql=========" + sql.toString());
      JDBCAgent dba = new JDBCAgent();
      try
      {
    	  System.out.println(sql.toString());
        flipInfo = dba.findByPaging(sql.toString(), flipInfo);
        dba.close();
      }
      catch (SQLException e)
      {
        LOGGER.info("查询工作日志数据库表异常");
      }
    }
    return flipInfo;
  }
  @AjaxAccess
  private Long getFlowFormId(String templateCode)
  {
    TemplateManager templeteManager = (TemplateManager)AppContext.getBean("templateManager");
    CtpTemplate template = templeteManager.getTempleteByTemplateNumber(templateCode);
    if (template == null) {
      return Long.valueOf(-1L);
    }
    CtpContentAll content = (CtpContentAll)DBAgent.get(CtpContentAll.class, template.getBody());
    return content.getContentTemplateId();
  }
  
  @AjaxAccess 
  public FlipInfo getDocumentRegistrationRelatedFlow(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    String sql = "select (select field0008 from formmain_0660 fm2 where fm2.id=max(c.form_recordid)) wsbh,max(a.subject) subject, max(m.name) name, max(a.create_date) createdate,max(c.finish_date) finishdate,max(c.process_id) processid, max(c.id) summaryid,max(a.id) affairid from ctp_affair a,org_member m,col_summary c where a.sender_id=m.id and a.object_id=c.id and a.state<>1 and a.state<>5";
    String where = "";
    
    FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
    String lawCode = (String)params.get("lawCode");
    String documentRegistrationRelatedFlow = AppContext.getSystemProperty("lawfirm.documentRegistrationRelatedFlow");
    String[] arrayOfString;
    int j = (arrayOfString = documentRegistrationRelatedFlow.split(",")).length;
    for (int i = 0; i < j; i++)
    {
      String templateNo = arrayOfString[i];
      if (!StringHelper.isNullOrEmpty(templateNo).booleanValue())
      {
        Long formAppId = getFlowFormId(templateNo);
        if (!formAppId.equals(Long.valueOf(-1L)))
        {
          FormBean flowFormBean = formCacheManager.getForm(formAppId.longValue());
          Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);
          where = StringHelper.concat(where, "c.form_recordid in (select id from " + flowFormBean.getMasterTableBean().getTableName() + " where " + flowFormBean.getMasterTableBean().getTableName() + "." + ((FormFieldBean)formFieldMap.get("案件编号")).getName() + "='" + lawCode + "')", " OR ");
        }
      }
    }
    sql = StringHelper.concat(sql, "(" + where + ")", " AND ");
    
    sql = sql + " group by a.object_id order by finishDate";
    System.out.println("查找文书登记相关流程sql=========" + sql.toString());
    JDBCAgent dba = new JDBCAgent();
    try
    {
      flipInfo = dba.findByPaging(sql.toString(), flipInfo);
      
      dba.close();
    }
    catch (SQLException e)
    {
      LOGGER.info("查询文书登记相关流程数据库表异常");
    }
    return flipInfo;
  }
  @AjaxAccess
  public FlipInfo getAgreementToSignRelatedFlow(FlipInfo flipInfo, Map<String, Object> params)
    throws Exception
  {
    String sql = "select max(a.subject) subject, max(m.name) name, max(a.create_date) createdate,max(c.finish_date) finishdate,max(c.process_id) processid, max(c.id) summaryid,max(a.id) affairid from ctp_affair a,org_member m,col_summary c where a.sender_id=m.id and a.object_id=c.id and a.state<>1 and a.state<>5";
    String where = "";
    
    FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
    String lawCode = (String)params.get("lawCode");
    String agreementToSignRelatedFlow = AppContext.getSystemProperty("lawfirm.agreementToSignRelatedFlow");
    String[] arrayOfString;
    int j = (arrayOfString = agreementToSignRelatedFlow.split(",")).length;
    for (int i = 0; i < j; i++)
    {
      String templateNo = arrayOfString[i];
      if (!StringHelper.isNullOrEmpty(templateNo).booleanValue())
      {
        Long formAppId = getFlowFormId(templateNo);
        if (!formAppId.equals(Long.valueOf(-1L)))
        {
          FormBean flowFormBean = formCacheManager.getForm(formAppId.longValue());
          Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);
          where = StringHelper.concat(where, "c.form_recordid in (select id from " + flowFormBean.getMasterTableBean().getTableName() + " where " + flowFormBean.getMasterTableBean().getTableName() + "." + ((FormFieldBean)formFieldMap.get("案件编号")).getName() + "='" + lawCode + "')", " OR ");
        }
      }
    }
    sql = StringHelper.concat(sql, "(" + where + ")", " AND ");
    
    sql = sql + " group by a.object_id order by finishDate";
    System.out.println("查找协议盖章相关流程sql=========" + sql.toString());
    JDBCAgent dba = new JDBCAgent();
    try
    {
      flipInfo = dba.findByPaging(sql.toString(), flipInfo);
      dba.close();
    }
    catch (SQLException e)
    {
      LOGGER.info("查询协议盖章相关流程数据库表异常");
    }
    return flipInfo;
  }
  @AjaxAccess
  public long getAppFormIdByCode(String formCode)
  {
    return LawFormHelper.getAppFormIdByCode(formCode);
  }
  @AjaxAccess
  public String getAjbh(String dq, String lb, String sj)
  {
    try
    {
      int maxXh = 0;
      
      CaseInfoParameterConfig cfg = LawFormHelper.getCaseInfoParameterConfigByCatalog(lb);
      lb = cfg.getAjlbsx();
      
      String preStr = dq + lb + sj.substring(0, 4);
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
      FormBean ajxxFormBean = this.formCacheManager.getForm(ajxxFormId);
      FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
      String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
      Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
      String[] ajFields = { ((FormFieldBean)formFieldMap.get("公用-案件编号")).getName() };
      List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0, 99999, "", "");
      for (int i = 0; i < formDataMasterBean.size(); i++)
      {
        String ajbh = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[0]);
        try
        {
          if ((ajbh != null) && (ajbh.startsWith(preStr)))
          {
            ajbh = ajbh.substring(preStr.length());
            
            int _i = 0;
            try
            {
              _i = Integer.parseInt(ajbh);
            }
            catch (Exception ex)
            {
              LOGGER.error("", ex);
            }
            if (_i > maxXh) {
              maxXh = _i;
            }
          }
        }
        catch (Exception ex)
        {
          LOGGER.error("", ex);
        }
      }
      maxXh++;
      String _maxXh = maxXh+"";
      while (_maxXh.length() < 4) {
        _maxXh = "0" + _maxXh;
      }
      return preStr + _maxXh;
    }
    catch (Exception ex) {}
    return "";
  }
  @AjaxAccess
  public String getAjmc(List wtrList, String lb)
  {
    String ret = "";
    for (Object obj : wtrList) {
      if (obj != null) {
        ret = StringHelper.concat(ret, obj.toString(), "、");
      }
    }
    ret = StringHelper.concat(ret, lb, "-");
    return ret;
  }
  @AjaxAccess
  public String getKhbh()
  {
    try
    {
      int maxXh = 0;
      
      V3xOrgDepartment dept = this.orgManager.getDepartmentById(
        this.orgManager.getMemberById(Long.valueOf(AppContext.currentUserId())).getOrgDepartmentId());
      
      String pPath = dept.getPath().substring(0, 12);
      V3xOrgDepartment pDept = this.orgManager.getDepartmentByPath(pPath);
      
      String preStr = pDept.getCode();
      String khxxFormCode = AppContext.getSystemProperty("lawfirm.khxxFormCode");
      long khxxFormId = LawFormHelper.getAppFormIdByCode(khxxFormCode);
      FormBean khxxFormBean = this.formCacheManager.getForm(khxxFormId);
      Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(khxxFormBean);
      String[] khFields = { ((FormFieldBean)formFieldMap.get("客户编码")).getName() };
      List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(khxxFormId, khFields, 0, 99999, "", "");
      for (int i = 0; i < formDataMasterBean.size(); i++)
      {
        String ajbh = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(khFields[0]);
        try
        {
          if ((ajbh != null) && (ajbh.startsWith(preStr)))
          {
            ajbh = ajbh.substring(preStr.length());
            
            int _i = Integer.parseInt(ajbh);
            if (_i > maxXh) {
              maxXh = _i;
            }
          }
        }
        catch (Exception localException1) {}
      }
      maxXh++;
      String _maxXh = maxXh+"";
      while (_maxXh.length() < 5) {
        _maxXh = "0" + _maxXh;
      }
      return preStr + _maxXh;
    }
    catch (Exception ex) {}
    return "";
  }
  @AjaxAccess
  public String getCaseInfoRelationData(String ajbh)
  {
    try
    {
      JDBCAgent dba = new JDBCAgent();
      String ajxxFormCode = AppContext.getSystemProperty("lawfirm.ajxxFormCode");
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      
      long ajxxFormId = LawFormHelper.getAppFormIdByCode(ajxxFormCode);
      if (ajxxFormId == -1L) {
        return "";
      }
      FormBean ajxxFormBean = formCacheManager.getForm(ajxxFormId);
      FormTableBean ajxxMasterTableBean = ajxxFormBean.getMasterTableBean();
      String ajxxMasterTableName = ajxxMasterTableBean.getTableName();
      Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(ajxxFormBean);
      String[] ajFields = { ((FormFieldBean)formFieldMap.get("公用-案件编号")).getName() };
      List<FormDataMasterBean> formDataMasterBean = FormService.findMasterDataList(ajxxFormId, ajFields, 0, 99999, "", "");
      for (int i = 0; i < formDataMasterBean.size(); i++)
      {
        String ajbhInBaseino = (String)((FormDataMasterBean)formDataMasterBean.get(i)).getAllDataMap().get(ajFields[0]);
        if ((ajbhInBaseino != null) && 
          (ajbhInBaseino.equals(ajbh)))
        {
          String sAllSubData = "";
          List<FormTableBean> tableList = ajxxFormBean.getTableList();
          String ret = 
            "{\"selectArray\":[{\"masterDataId\":\"" + 
            
            ((FormDataMasterBean)formDataMasterBean.get(i)).getId() + "\"," + 
            "\"subData\":[";
          for (FormTableBean tableBean : tableList)
          {
            String sSubdata = "";
            if (!tableBean.isMainTable())
            {
              sSubdata = "{\"tableName\":\"" + tableBean.getTableName() + "\",\"dataIds\":[";
              
              dba.execute("select id from " + tableBean.getTableName() + " where formmain_id= " + ((FormDataMasterBean)formDataMasterBean.get(i)).getId());
              List list = dba.resultSetToList();
              sSubdata = sSubdata + StringHelper.joinList(list, "\"", "\"", ",");
              sSubdata = sSubdata + "]}";
              sAllSubData = StringHelper.concat(sAllSubData, sSubdata, ",");
            }
          }
          ret = ret + sAllSubData;
          return ret + 
            "]}],\"toFormId\":\"" + 
            
            ajxxFormId + "\"}";
        }
      }
      return "";
    }
    catch (Exception ex)
    {
      LOGGER.error("", ex);
    }
    return "";
  }
  @AjaxAccess
  public String getCaseRelationAjmcField(String formId)
  {
    FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
    FormBean formBean = formCacheManager.getForm(Long.valueOf(formId).longValue());
    Map<String, FormFieldBean> formFieldMap = LawFormHelper.getFormFieldMap(formBean);
    return ((FormFieldBean)formFieldMap.get("案件名称")).getName();
  }
  @AjaxAccess
  public String getAjlb(String formId, String id)
  {
    try
    {
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      FormBean flowFormBean = formCacheManager.getForm(Long.valueOf(formId).longValue());
      FormDataMasterBean masterBean = FormService.findDataById(Long.valueOf(id).longValue(), Long.valueOf(formId).longValue());
      Map<String, FormFieldBean> flowFieldMap = LawFormHelper.getFormFieldMap(flowFormBean);
      return ((FormFieldBean)flowFieldMap.get("公用-案件类别-1")).getDisplayValue(masterBean.getFieldValue(((FormFieldBean)flowFieldMap.get("公用-案件类别-1")).getName()))[1].toString();
    }
    catch (Exception ex)
    {
      LOGGER.error("", ex);
    }
    return "";
  }
  @AjaxAccess
  public Boolean isLaFlow(String templateId)
  {
    try
    {
      String formLa = AppContext.getSystemProperty("lawfirm.formLa");
      TemplateManager templeteManager = (TemplateManager)AppContext.getBean("templateManager");
      CtpTemplate template = templeteManager.getCtpTemplate(Long.valueOf(templateId));
      String templateNo = template.getTempleteNumber();
      if (StringHelper.isNullOrEmpty(templateNo).booleanValue()) {
        return Boolean.valueOf(false);
      }
      return StringHelper.containBySp(formLa, templateNo, ",");
    }
    catch (Exception ex)
    {
      LOGGER.error("", ex);
    }
    return Boolean.valueOf(false);
  }
  
  private String canEditIsAgreeFieldDisplayNames(Long templateId)
    throws BusinessException
  {
    String formLa = AppContext.getSystemProperty("lawfirm.formLa");
    String formJa = AppContext.getSystemProperty("lawfirm.formJa");
    String formCa = AppContext.getSystemProperty("lawfirm.formCa");
    TemplateManager templeteManager = (TemplateManager)AppContext.getBean("templateManager");
    CtpTemplate template = templeteManager.getCtpTemplate(templateId);
    String templateNo = template.getTempleteNumber();
    if (StringHelper.containBySp(formLa, templateNo, ",").booleanValue()) {
      return "审批意见-主任-是否同意,审批意见-部门负责人是否同意,审批意见-主管合伙人是否同意,审批意见-团队合伙人是否同意,审批意见-本所负责人是否同意,审批意见-立案审查人是否同意";
    }
    if (StringHelper.containBySp(formJa, templateNo, ",").booleanValue()) {
      return "是否同意-部门负责人,是否同意-主管合伙人";
    }
    if (StringHelper.containBySp(formCa, templateNo, ",").booleanValue()) {
      return "是否同意-部门负责人,是否同意-主管合伙人,是否同意-立案审查人";
    }
    return "";
  }
  @AjaxAccess
  public String getCanEditIsAgreeFields(String affairId)
  {
    try
    {
      String ret = "";
      AffairManager affairManager = (AffairManager)AppContext.getBean("affairManager");
      FormCacheManager formCacheManager = (FormCacheManager)AppContext.getBean("formCacheManager");
      CtpAffair affair = affairManager.get(Long.valueOf(affairId));
      Long formAppId = affair.getFormAppId();
      Long authId = affair.getFormOperationId();
      Long templateId = affair.getTempleteId();
      if (templateId == null) {
        return "";
      }
      String fieldDisplayNames = canEditIsAgreeFieldDisplayNames(templateId);
      if (StringHelper.isNullOrEmpty(fieldDisplayNames).booleanValue()) {
        return "";
      }
      FormBean formBean = formCacheManager.getForm(formAppId.longValue());
      for (FormViewBean fvb : formBean.getFormViewList())
      {
        FormAuthViewBean favb = fvb.getAuthorizaton(authId.longValue());
        if (favb != null)
        {
          List<FormAuthViewFieldBean> list = favb.getFormAuthorizationFieldList();
          for (FormAuthViewFieldBean favfb : list) {
            if (Enums.FieldAccessType.edit.getKey().equals(favfb.getAccess()))
            {
              String[] arrayOfString;
              int j = (arrayOfString = fieldDisplayNames.split(",")).length;
              for (int i = 0; i < j; i++)
              {
                String fieldDisplayName = arrayOfString[i];
                if ((!StringHelper.isNullOrEmpty(fieldDisplayName).booleanValue()) && (fieldDisplayName.equals(favfb.getFormFieldBean().getDisplay()))) {
                  ret = StringHelper.concat(ret, favfb.getFieldName(), ",");
                }
              }
            }
          }
        }
      }
      return ret;
    }
    catch (Exception ex)
    {
      LOGGER.error("", ex);
    }
    return "";
  }
  @AjaxAccess
  public String getEnumItemShowValue(String id)
  {
    try
    {
      EnumManager enumManager = (EnumManager)AppContext.getBean("enumManagerNew");
      CtpEnumItem enumItem = enumManager.getCtpEnumItem(Long.valueOf(id));
      return enumItem.getShowvalue();
    }
    catch (BusinessException ex) {}
    return "";
  }
}
