package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.po.operationlog.OperationLog;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HqlSearchHelper
{
  private static final V3xOrgMemberWithLoginName OrgMember = null;
  private static Log LOG = LogFactory.getLog(HqlSearchHelper.class);
  
  public static List<V3xOrgMember> highSearchMember(String se, String de, String le, String po, SearchManager searchManager, OrgManagerDirect orgManager, PrincipalManager principalManager, boolean includeDisabled)
  {
    User user = CurrentUser.get();
    StringBuffer strbuf = new StringBuffer();
    Map<String, Object> param = new HashMap();
    strbuf.append("select a.id,a.name,a.sortId,a.code,a.orgDepartmentId,a.orgLevelId,a.orgPostId,a.type,a.state from " + 
      OrgMember.class.getName() + " a where a.orgAccountId ='" + user.getLoginAccount() + 
      "' and a.deleted!='1' and a.admin='0' and a.assigned=1 and a.internal = '1' ");
    if (!includeDisabled)
    {
      strbuf.append("and a.enabled=:enabled");
      param.put("enabled", Boolean.TRUE);
    }
    if ((de != null) && (!de.equals("")))
    {
      strbuf.append(" and a.orgDepartmentId=:timestamp");
      param.put("timestamp", Long.valueOf(de));
    }
    if ((le != null) && (!le.equals("")))
    {
      strbuf.append(" and a.orgLevelId=:timestamp1");
      param.put("timestamp1", Long.valueOf(le));
    }
    if ((po != null) && (!po.equals("")))
    {
      strbuf.append(" and a.orgPostId=:timestamp2");
      param.put("timestamp2", Long.valueOf(po));
    }
    if ((se != null) && (!se.equals("")) && (Integer.valueOf(se).intValue() != -1))
    {
      strbuf.append(" and a.extAttr11=:timestamp3");
      param.put("timestamp3", Integer.valueOf(se));
    }
    List<Object[]> myliost = searchManager.searchByHql(strbuf.toString(), param);
    List<V3xOrgMember> mes = toV3xOrgmembers(myliost);
    Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
    for (V3xOrgMember m : mes) {
      try
      {
        String loginName = (String)idLoginNameMap.get(m.getId());
        if (Strings.isNotBlank(loginName)) {
          m.setLoginName(m, loginName);
        } else {
          LOG.error("人员的登录名不存在。" + m.getId());
        }
      }
      catch (Exception localException)
      {
        LOG.error("人员的登录名不存在。" + m.getId());
      }
    }
    return mes;
  }
  
  public static List<V3xOrgMember> highSearchMember(String de, String le, String po, String se, String st, String pol, String ma, String fT1, String tT1, String fT2, String tT2, SearchManager searchManager, OrgManagerDirect orgManager, PrincipalManager principalManager, boolean includeDisabled)
  {
    new ArrayList(0);
    User user = CurrentUser.get();
    StringBuffer strbuf = new StringBuffer();
    Map<String, Object> param = new HashMap();
    strbuf.append("select a.id,a.name,a.sortId,a.code,a.orgDepartmentId,a.orgLevelId,a.orgPostId,a.type,a.state from " + OrgMember.class.getName() + " a," + 
      StaffInfo.class.getName() + " c  where a.id=c.org_member_id and  a.orgAccountId ='" + user.getAccountId() + "' and a.deleted='0' and a.admin='0' and a.assigned=1 and a.internal = '1' ");
    if (!includeDisabled)
    {
      strbuf.append("and a.enabled=:enabled");
      param.put("enabled", Boolean.TRUE);
    }
    if ((de != null) && (!de.equals("")))
    {
      strbuf.append(" and a.orgDepartmentId=:textfield");
      param.put("textfield", Long.valueOf(de));
    }
    if ((le != null) && (!le.equals("")))
    {
      strbuf.append(" and a.orgLevelId=:textfield1");
      param.put("textfield1", Long.valueOf(le));
    }
    if ((po != null) && (!po.equals("")))
    {
      strbuf.append(" and a.orgPostId=:textfield2");
      param.put("textfield2", Long.valueOf(po));
    }
    if ((se != null) && (!se.equals("")) && (Integer.valueOf(se).intValue() != -1))
    {
      strbuf.append(" and c.sex=:textfield3");
      param.put("textfield3", se);
    }
    if ((st != null) && (!st.equals("")) && (Integer.valueOf(st).intValue() != -1))
    {
      strbuf.append(" and c.edu_level=:textfield4");
      param.put("textfield4", Integer.valueOf(st));
    }
    if ((pol != null) && (!pol.equals("")) && (Integer.valueOf(pol).intValue() != -1))
    {
      strbuf.append(" and c.political_position=:textfield5");
      param.put("textfield5", Integer.valueOf(pol));
    }
    if ((ma != null) && (!ma.equals("")) && (Integer.valueOf(ma).intValue() != -1))
    {
      strbuf.append(" and c.marriage=:textfield6");
      param.put("textfield6", Integer.valueOf(ma));
    }
    Date fromTime = new Date();
    if ((fT1 != null) && (!fT1.equals(""))) {
      fromTime = Datetimes.parse(fT1, "yyyy-MM-dd");
    }
    Date toTime = new Date();
    if ((tT1 != null) && (!tT1.equals(""))) {
      toTime = Datetimes.parse(tT1, "yyyy-MM-dd");
    }
    if ((fT1 != null) && (!fT1.equals("")) && (tT1 != null) && (!tT1.equals("")))
    {
      strbuf.append(" and ( a.extAttr21 >= :textfield7 and a.extAttr21 <= :textfield8 )");
      param.put("textfield7", fromTime);
      param.put("textfield8", toTime);
    }
    else if ((fT1 != null) && (!fT1.equals("")))
    {
      strbuf.append(" and ( a.extAttr21 >= :textfield7 )");
      param.put("textfield7", fromTime);
    }
    else if ((tT1 != null) && (!tT1.equals("")))
    {
      strbuf.append(" and ( a.extAttr21 <= :textfield8 )");
      param.put("textfield8", toTime);
    }
    Date fromTime2 = new Date();
    if ((fT2 != null) && (!fT2.equals(""))) {
      fromTime2 = Datetimes.parse(fT2, "yyyy-MM-dd");
    }
    Date toTime2 = new Date();
    if ((tT2 != null) && (!tT2.equals(""))) {
      toTime2 = Datetimes.parse(tT2, "yyyy-MM-dd");
    }
    if ((fT2 != null) && (!fT2.equals("")) && (tT2 != null) && (!tT2.equals("")))
    {
      strbuf.append(" and ( c.work_starting_date >= :textfield9 and c.work_starting_date <= :textfield0 )");
      param.put("textfield9", fromTime2);
      param.put("textfield0", toTime2);
    }
    else if ((fT2 != null) && (!fT2.equals("")))
    {
      strbuf.append(" and ( c.work_starting_date >= :textfield9 )");
      param.put("textfield9", fromTime2);
    }
    else if ((tT2 != null) && (!tT2.equals("")))
    {
      strbuf.append(" and ( c.work_starting_date <= :textfield0 )");
      param.put("textfield0", toTime2);
    }
    List<Object[]> mylost = searchManager.searchByHql(strbuf.toString(), param);
    List<V3xOrgMember> mes = toV3xOrgmembers(mylost);
    Map<Long, String> idLoginNameMap = principalManager.getMemberIdLoginNameMap();
    for (V3xOrgMember m : mes) {
      try
      {
        String loginName = (String)idLoginNameMap.get(m.getId());
        if (loginName != null) {
          m.setLoginName(m, loginName);
        } else {
          LOG.error("人员的登录名不存在。" + m.getId());
        }
      }
      catch (Exception localException)
      {
        LOG.error("人员的登录名不存在。" + m.getId());
      }
    }
    return mes;
  }
  
  public static List<OperationLog> searchLog(String condition, String textfield, String textfield1, Long subObjectId, SearchManager searchManager)
  {
    List<OperationLog> operationLogs = new ArrayList();
    boolean isTextfield = (Strings.isNotBlank(textfield)) || (Strings.isNotBlank(textfield1));
    if ((Strings.isNotBlank(condition)) && (isTextfield))
    {
      Long accountId = CurrentUser.get().getLoginAccount();
      StringBuffer strbuf = new StringBuffer("");
      Map<String, Object> param = new HashMap();
      strbuf.append("select a from " + OperationLog.class.getName() + " a where ");
      strbuf.append("a.objectId=:accountId and ");
      param.put("accountId", accountId);
      if (condition.equals("memberId"))
      {
        strbuf.append("a.memberId=:textfield");
        param.put("textfield", Long.valueOf(textfield));
      }
      else if (condition.equals("moduleId"))
      {
        strbuf.append("a.moduleId=:textfield");
        param.put("textfield", Integer.valueOf(Integer.parseInt(textfield)));
      }
      else if (condition.equals("objectId"))
      {
        strbuf.append("a.objectId=:textfield");
        param.put("textfield", Long.valueOf(textfield));
      }
      else if (condition.equals("subObjectId"))
      {
        strbuf.append("a.subObjectId=:textfield");
        param.put("textfield", Long.valueOf(textfield));
      }
      else if (condition.equals("actionType"))
      {
        if (textfield.equals("hr.staffInfo.operation.add.label"))
        {
          strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
          param.put("textfield1", "hr.staffInfo.operation.add.label");
          param.put("textfield2", "hr.staffInfo.other.add.label");
        }
        else if (textfield.equals("hr.staffInfo.operation.delete.label"))
        {
          strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
          param.put("textfield1", "hr.staffInfo.operation.delete.label");
          param.put("textfield2", "hr.staffInfo.other.delete.label");
        }
        else if (textfield.equals("hr.staffInfo.operation.update.label"))
        {
          strbuf.append("(a.actionType=:textfield1 or a.actionType=:textfield2)");
          param.put("textfield1", "hr.staffInfo.operation.update.label");
          param.put("textfield2", "hr.staffInfo.other.update.label");
        }
      }
      else if (condition.equals("actionAll"))
      {
        strbuf.append("a.actionType=:textfield1 or a.actionType=:textfield2 or a.actionType=:textfield3");
        param.put("textfield1", "hr.staffInfo.operation.add.label");
        param.put("textfield2", "hr.staffInfo.operation.delete.label");
        param.put("textfield3", "hr.staffInfo.operation.update.label");
      }
      else if (condition.equals("actionName"))
      {
        strbuf.append("a.memberId=:textfield and (a.actionType=:textfield1 or a.actionType=:textfield2 or a.actionType=:textfield3 or a.actionType=:textfield4 or a.actionType=:textfield5 or a.actionType=:textfield6)");
        param.put("textfield", Long.valueOf(textfield));
        param.put("textfield1", "hr.staffInfo.operation.add.label");
        param.put("textfield2", "hr.staffInfo.operation.delete.label");
        param.put("textfield3", "hr.staffInfo.operation.update.label");
        
        param.put("textfield4", "hr.staffInfo.other.add.label");
        param.put("textfield5", "hr.staffInfo.other.delete.label");
        param.put("textfield6", "hr.staffInfo.other.update.label");
      }
      else if (condition.equals("actionTime"))
      {
        Date fromTime = null;
        Date toTime = null;
        if (Strings.isNotBlank(textfield)) {
          fromTime = Datetimes.parse(textfield, "yyyy-MM-dd");
        }
        if (Strings.isNotBlank(textfield1)) {
          toTime = Datetimes.parse(textfield1, "yyyy-MM-dd");
        }
        Calendar toCal = Calendar.getInstance();
        if (toTime != null)
        {
          toCal.setTime(toTime);
          toCal.add(5, 1);
        }
        if ((fromTime != null) && (toTime != null))
        {
          strbuf.append("(a.actionTime >= :fromTime and a.actionTime <= :toTime) and subObjectId=:subObjectId");
          param.put("fromTime", fromTime);
          param.put("toTime", toCal.getTime());
          param.put("subObjectId", subObjectId);
        }
        else if ((fromTime != null) && (toTime == null))
        {
          Date from = Datetimes.parse(textfield + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
          Datetimes.addDate(from, 1);
          
          strbuf.append("a.actionTime >= :fromTime  and subObjectId=:subObjectId");
          param.put("fromTime", from);
          param.put("subObjectId", subObjectId);
        }
        else if ((toTime != null) && (fromTime == null))
        {
          Date from = Datetimes.parse(textfield1 + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
          Date end = Datetimes.addDate(from, 1);
          strbuf.append(" a.actionTime <= :toTime and subObjectId=:subObjectId");
          param.put("toTime", end);
          param.put("subObjectId", subObjectId);
        }
      }
      else if (condition.equals("remoteIp"))
      {
        strbuf.append("a.remoteIp=:textfield");
        param.put("textfield", textfield);
      }
      if (condition != null) {
        strbuf.append(" order by a.actionTime desc");
      }
      operationLogs = searchManager.searchByHql(strbuf.toString(), param);
    }
    return operationLogs;
  }
  
  public static List<V3xOrgMember> toV3xOrgmembers(List<Object[]> myliost)
  {
    List<V3xOrgMember> mes = new ArrayList();
    if ((myliost != null) && (myliost.size() > 0)) {
      for (Object[] obj : myliost)
      {
        V3xOrgMember v = new V3xOrgMember();
        if (obj[0] != null) {
          v.setId(Long.valueOf(obj[0].toString()));
        }
        if (obj[1] != null) {
          v.setName(obj[1].toString());
        }
        if (obj[2] != null) {
          v.setSortId(Long.valueOf(obj[2].toString()));
        }
        if (obj[3] != null) {
          v.setCode(obj[3].toString());
        }
        if (obj[4] != null) {
          v.setOrgDepartmentId(Long.valueOf(obj[4].toString()));
        }
        if (obj[5] != null) {
          v.setOrgLevelId(Long.valueOf(obj[5].toString()));
        }
        if (obj[6] != null) {
          v.setOrgPostId(Long.valueOf(obj[6].toString()));
        }
        if (obj[7] != null) {
          v.setType(Integer.valueOf(Integer.parseInt(obj[7].toString())));
        }
        if (obj[8] != null) {
          v.setState(Integer.parseInt(obj[8].toString()));
        }
        mes.add(v);
      }
    }
    return mes;
  }
}
