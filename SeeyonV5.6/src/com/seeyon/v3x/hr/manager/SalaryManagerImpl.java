package com.seeyon.v3x.hr.manager;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.i18n.ResourceBundleUtil;
import com.seeyon.ctp.common.usermessage.MessageContent;
import com.seeyon.ctp.common.usermessage.MessageReceiver;
import com.seeyon.ctp.common.usermessage.UserMessageManager;
import com.seeyon.ctp.organization.po.OrgMember;
import com.seeyon.ctp.util.LightWeightEncoder;
import com.seeyon.v3x.hr.dao.HrSalaryPasswordDao;
import com.seeyon.v3x.hr.dao.SalaryDao;
import com.seeyon.v3x.hr.domain.HrSalaryPassword;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.webmodel.SalaryMessageBO;
import com.seeyon.v3x.util.SQLWildcardUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SalaryManagerImpl
  implements SalaryManager
{
  private static final Log log = LogFactory.getLog(SalaryManagerImpl.class);
  private SalaryDao salaryDao;
  private HrSalaryPasswordDao hrSalaryPasswordDao;
  private UserMessageManager userMessageManager;
  
  public SalaryDao getSalaryDao()
  {
    return this.salaryDao;
  }
  
  public void setSalaryDao(SalaryDao salaryDao)
  {
    this.salaryDao = salaryDao;
  }
  
  public List findAllStaffSalary()
  {
    return this.salaryDao.findAllStaffSalary();
  }
  
  public List<Salary> findAllAccountStaffSalary(Long accountId, String condition, String textfield, String textfield1, String textfield2, String textfield3)
  {
    Map<String, Object> params = new HashMap();
    StringBuilder hql = new StringBuilder();
    hql.append("select s from " + Salary.class.getName() + " as s ");
    if ("salaryDept".equals(condition)) {
      hql.append(" , " + OrgMember.class.getName() + " as m ");
    }
    hql.append(" where s.accountId = :accountId ");
    params.put("accountId", accountId);
    if (StringUtils.isNotBlank(condition)) {
      if ("staffName".equals(condition))
      {
        if (!textfield.equals(""))
        {
          hql.append(" and s.name like :name ");
          params.put("name", "%" + SQLWildcardUtil.escape(textfield) + "%");
        }
      }
      else if ("salaryDate".equals(condition))
      {
        if ((!textfield.equals("")) && (!textfield1.equals("")))
        {
          hql.append(" and year >= :fromYear and month >= :fromMonth ");
          params.put("fromYear", Integer.valueOf(NumberUtils.toInt(textfield)));
          params.put("fromMonth", Integer.valueOf(NumberUtils.toInt(textfield1)));
        }
        if ((!textfield3.equals("")) && (!textfield2.equals("")))
        {
          hql.append(" and year <= :toYear and month <= :toMonth ");
          params.put("toYear", Integer.valueOf(NumberUtils.toInt(textfield2)));
          params.put("toMonth", Integer.valueOf(NumberUtils.toInt(textfield3)));
        }
      }
      else if ("salaryDept".equals(condition))
      {
        hql.append(" and s.staffId=m.id ");
        if (!textfield.equals(""))
        {
          hql.append(" and m.orgDepartmentId = :departmentId ");
          params.put("departmentId", Long.valueOf(NumberUtils.toLong(textfield)));
        }
      }
    }
    hql.append(" and s.creatorId = :creatorId");
    
    hql.append(" order by s.year desc, s.month desc, s.staffId");
    params.put("creatorId", Long.valueOf(AppContext.currentUserId()));
    return this.salaryDao.find(hql.toString(), params, new Object[0]);
  }
  
  public void exportSalary(List<Salary> sList, List<Salary> uList, List<Repository> repositories)
    throws com.seeyon.v3x.common.exceptions.BusinessException
  {
    this.salaryDao.exportSalary(sList, uList, repositories);
  }
  
  public void addSalary(Salary salary)
  {
    this.salaryDao.save(salary);
  }
  
  public void removeSalaryByIds(List<Long> salaryIds)
  {
    this.salaryDao.deleteSalaryByIds(salaryIds);
  }
  
  public Salary findSalaryById(Long id)
  {
    return this.salaryDao.findSalaryById(id);
  }
  
  public void updateSalary(Salary salary)
  {
    this.salaryDao.update(salary);
  }
  
  public List findSalaryByStaffId(Long staffId)
  {
    return this.salaryDao.findSalaryByStaffId(staffId);
  }
  
  public List findSalaryByStaffId(Long staffId, boolean pagination)
  {
    return this.salaryDao.findSalaryByStaffId(staffId, pagination);
  }
  
  public Salary getSalaryByStaffNameAndDate(String staffName, int year, int month)
    throws Exception
  {
    return this.salaryDao.findSalaryByStaffNameAndDate(staffName, year, month);
  }
  
  public List getSalaryByTime(Long staffId, int fromYear, int fromMonth, int toYear, int toMonth)
    throws Exception
  {
    return this.salaryDao.findSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth);
  }
  
  public List getSalaryByTime(Long staffId, int fromYear, int fromMonth, int toYear, int toMonth, boolean pagination)
    throws Exception
  {
    return this.salaryDao.findSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth, pagination);
  }
  
  public List getSalaryByName(String name)
  {
    return this.salaryDao.findSalaryByName(name);
  }
  
  public List getSalaryByName(String name, boolean isPaginate)
  {
    return this.salaryDao.findSalaryByName(name, isPaginate);
  }
  
  public List getAllStaffSalarysByDate(int fromYear, int fromMonth, int toYear, int toMonth)
    throws Exception
  {
    return this.salaryDao.findAllSalaryByDate(fromYear, fromMonth, toYear, toMonth);
  }
  
  public List getAllStaffSalarysByDate(int fromYear, int fromMonth, int toYear, int toMonth, boolean isPaginate)
    throws Exception
  {
    return this.salaryDao.findAllSalaryByDate(fromYear, fromMonth, toYear, toMonth, isPaginate);
  }
  
  public List getSalaryByBasic(float fromSalary, float toSalary)
  {
    return this.salaryDao.findSalaryByBasic(fromSalary, toSalary);
  }
  
  public List getSalaryByBasic(float fromSalary, float toSalary, boolean isPaginate)
  {
    return this.salaryDao.findSalaryByBasic(fromSalary, toSalary, isPaginate);
  }
  
  public List getSalaryByActually(float fromSalary, float toSalary)
  {
    return this.salaryDao.findSalaryByActually(fromSalary, toSalary);
  }
  
  public List getSalaryByActually(float fromSalary, float toSalary, boolean isPaginate)
  {
    return this.salaryDao.findSalaryByActually(fromSalary, toSalary, isPaginate);
  }
  
  public boolean hasSalaryPasswordRecord(Long userId)
  {
    if (this.hrSalaryPasswordDao.getSalaryRecordUniq(userId.longValue()) == null) {
      return false;
    }
    return true;
  }
  
  public HrSalaryPassword getSalaryPasswordRecordUniq(long userId)
  {
    return this.hrSalaryPasswordDao.getSalaryRecordUniq(userId);
  }
  
  public void setSalaryPasswordRecord(Long userId, String password)
    throws Exception
  {
    if (hasSalaryPasswordRecord(userId))
    {
      log.info("存在这个人员的记录" + userId);
      return;
    }
    HrSalaryPassword hrSalaryPassword = new HrSalaryPassword();
    hrSalaryPassword.setIdIfNew();
    hrSalaryPassword.setUserId(userId);
    hrSalaryPassword.setSalaryPassword(LightWeightEncoder.encodeString(password));
    hrSalaryPassword.setCreateDate(new Date());
    hrSalaryPassword.setUpdateDate(new Date());
    this.hrSalaryPasswordDao.save(hrSalaryPassword);
  }
  
  public HrSalaryPasswordDao getHrSalaryPasswordDao()
  {
    return this.hrSalaryPasswordDao;
  }
  
  public void setHrSalaryPasswordDao(HrSalaryPasswordDao hrSalaryPasswordDao)
  {
    this.hrSalaryPasswordDao = hrSalaryPasswordDao;
  }
  
  public boolean checkPassWord(Long userId, String password)
    throws Exception
  {
    HrSalaryPassword hrSalaryPassword = getSalaryPasswordRecordUniq(userId.longValue());
    if (hrSalaryPassword == null)
    {
      log.error("没有这个人员的记录：" + userId);
      return false;
    }
    String stdPwd = hrSalaryPassword.getSalaryPassword();
    if ((stdPwd != null) && (stdPwd.equals(LightWeightEncoder.encodeString(password)))) {
      return true;
    }
    return false;
  }
  
  public boolean updatePassWord(Long userId, String password)
    throws Exception
  {
    HrSalaryPassword hrSalaryPassword = getSalaryPasswordRecordUniq(userId.longValue());
    if (hrSalaryPassword == null)
    {
      log.error("没有这个人员的记录：" + userId);
      return false;
    }
    hrSalaryPassword.setSalaryPassword(LightWeightEncoder.encodeString(password));
    hrSalaryPassword.setUpdateDate(new Date());
    try
    {
      this.hrSalaryPasswordDao.update(hrSalaryPassword);
    }
    catch (Exception e)
    {
      log.error("更新出现问题", e);
      return false;
    }
    return true;
  }
  
  public boolean updatePassWord(String password, String members)
    throws Exception
  {
    String hql = "update HrSalaryPassword hsp set hsp.salaryPassword=:password where hsp.userId in (:ids)";
    Map<String, Object> nameMap = new HashMap();
    nameMap.put("password", LightWeightEncoder.encodeString(password));
    String[] membersIds = members.split(",");
    List<Long> list = new ArrayList();
    String[] arrayOfString1;
    int j = (arrayOfString1 = membersIds).length;
    for (int i = 0; i < j; i++)
    {
      String s = arrayOfString1[i];
      list.add(Long.valueOf(s));
    }
    nameMap.put("ids", list);
    try
    {
      this.hrSalaryPasswordDao.bulkUpdate(hql, nameMap, new Object[0]);
    }
    catch (Exception e)
    {
      log.error("更新出现问题", e);
      return false;
    }
    return true;
  }
  
  public void addAllSalary(List<Salary> salaryList)
    throws Exception
  {
    this.salaryDao.savePatchAll(salaryList);
  }
  
  public int getfindSlary(Long staffId, String date)
  {
    String[] strs = date.split("-");
    int year = Integer.parseInt(strs[0]);
    int monty = Integer.parseInt(strs[1]);
    return this.salaryDao.getstaffIdcount(staffId, year, monty);
  }
  
  public String findSlaryBystaffidAndDate(Long staffId, String date)
  {
    if (StringUtils.isNotBlank(date))
    {
      String[] strs = date.split("-");
      int year = Integer.parseInt(strs[0]);
      int monty = Integer.parseInt(strs[1]);
      return this.salaryDao.getStaffNameByStaffidAndDate(staffId, year, monty);
    }
    return "";
  }
  
  public void sendSalaryMessage(String state, String sendKey)
  {
    if ("true".equals(state))
    {
      List<SalaryMessageBO> salaryMessageList = (List)AppContext.getSessionContext(sendKey);
      if (!salaryMessageList.isEmpty()) {
        for (SalaryMessageBO smb : salaryMessageList)
        {
          MessageContent content = MessageContent.get(ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources", 
            "hr.salary.send.message", new Object[] { smb.getSalaryDate() }), new Object[0]);
          MessageReceiver receivers = MessageReceiver.get(Long.valueOf(-1L), smb.getUserId().longValue(), "message.link.hr.salary.open", new Object[] { Integer.valueOf(1) });
          try
          {
            this.userMessageManager.sendSystemMessage(content, ApplicationCategoryEnum.hr, 8L, receivers, new Object[0]);
          }
          catch (com.seeyon.ctp.common.exceptions.BusinessException e)
          {
            log.error("工资发消息失败：" + e);
          }
        }
      }
    }
    AppContext.removeThreadContext(sendKey);
  }
  
  public void setUserMessageManager(UserMessageManager userMessageManager)
  {
    this.userMessageManager = userMessageManager;
  }
}
