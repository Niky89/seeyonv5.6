package com.seeyon.v3x.hr.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.dao.RecordDao;
import com.seeyon.v3x.hr.domain.Record;
import com.seeyon.v3x.hr.domain.RecordState;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.worktimeset.dao.WorkSetDao;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;

public class RecordManagerImpl
  implements RecordManager
{
  private static final Log logger = LogFactory.getLog(RecordManagerImpl.class);
  private RecordDao recordDao;
  private SystemConfig systemConfig;
  private OrgManager orgManager;
  private WorkSetDao workSetDao;
  
  public void setSystemConfig(SystemConfig systemConfig)
  {
    this.systemConfig = systemConfig;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public void setRecordDao(RecordDao recordDao)
  {
    this.recordDao = recordDao;
  }
  
  public RecordDao getRecordDao()
  {
    return this.recordDao;
  }
  
  public WorkSetDao getWorkSetDao()
  {
    return this.workSetDao;
  }
  
  public void setWorkSetDao(WorkSetDao workSetDao)
  {
    this.workSetDao = workSetDao;
  }
  
  private Map<Integer, RecordState> recordStateCache = new HashMap();
  private WorkTimeSetManager workTimeSetManager;
  
  public void init()
  {
    List<RecordState> states = this.recordDao.getAllRecordState();
    for (RecordState state : states) {
      this.recordStateCache.put(Integer.valueOf(state.getId()), state);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("打卡状态记录缓存加载完毕...");
    }
  }
  
  private Date getMinCardDate()
  {
    String hql = "select min(begin_work_time) from " + Record.class.getCanonicalName();
    List<Date> result = this.recordDao.find(hql, -1, -1, null, new Object[0]);
    return CollectionUtils.isNotEmpty(result) ? (Date)result.get(0) : null;
  }
  
  public static void main(String[] args)
  {
    Date d1 = Datetimes.parseDate("2011-07-26");
    Date d2 = Datetimes.parseDate("2011-06-26");
    
    int minus = (int)Datetimes.minusDay(d1, d2);
    Date d3 = Datetimes.addDate(d2, minus);
    System.out.println(d3);
  }
  
  public void addRecords4NoCardTillnow()
  {
    String ci = this.systemConfig.get("card_enable");
    boolean cardEnabled = (ci != null) && ("enable".equals(ci));
    if (!cardEnabled) {
      return;
    }
    Date minCardDate = getMinCardDate();
    if (minCardDate == null) {
      return;
    }
    minCardDate = Datetimes.getTodayFirstTime(minCardDate);
    Date today = Datetimes.getTodayFirstTime();
    int minus = (int)Datetimes.minusDay(today, minCardDate);
    for (int i = 0; i < minus;)
    {
      Date d = Datetimes.addDate(minCardDate, i);
      try
      {
        try
        {
          addRecords4NoCard4Day(d);
        }
        catch (com.seeyon.ctp.common.exceptions.BusinessException e)
        {
          logger.error("", e);
        }
        i++;
      }
      catch (Exception e)
      {
        logger.error("为" + Datetimes.formatDate(d) + "未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
      }
    }
  }
  
  public void addRecords4NoCard()
  {
    try
    {
      Date yesterday = Datetimes.addDate(new Date(), -1);
      Calendar time = Calendar.getInstance();
      time.setTime(yesterday);
      
      addRecords4NoCard4Day(yesterday);
    }
    catch (com.seeyon.v3x.common.exceptions.BusinessException e)
    {
      logger.error("获取组织模型信息时出现异常：", e);
    }
    catch (Exception e)
    {
      logger.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
    }
  }
  
  public WorkTimeSetManager getWorkTimeSetManager()
  {
    return this.workTimeSetManager;
  }
  
  public void setWorkTimeSetManager(WorkTimeSetManager workTimeSetManager)
  {
    this.workTimeSetManager = workTimeSetManager;
  }
  
  private void addRecords4NoCard4Day(Date someDay)
    throws com.seeyon.v3x.common.exceptions.BusinessException, com.seeyon.ctp.common.exceptions.BusinessException
  {
    Calendar time = Calendar.getInstance();
    time.setTime(someDay);
    int year = time.get(1);
    Integer month = Integer.valueOf(time.get(2) + 1);
    Integer day = Integer.valueOf(time.get(5));
    int dayWeek = time.get(7);
    
    RecordState noCard = (RecordState)this.recordStateCache.get(Integer.valueOf(3));
    
    String begin_hour = "0";String begin_minute = "0";String end_hour = "0";String end_minute = "0";
    
    String ci = this.systemConfig.get("card_enable");
    boolean cardEnabled = (ci != null) && ("enable".equals(ci));
    if (cardEnabled)
    {
      List<V3xOrgAccount> accounts = this.orgManager.getAllAccounts();
      if (CollectionUtils.isEmpty(accounts)) {
        return;
      }
      for (V3xOrgAccount account : accounts) {
        if ((account != null) && (account.isValid()))
        {
          Long accountId = account.getId();
          
          RecordWorkingTime workTime = this.recordDao.getRecordWorkingTime(accountId);
          if (workTime != null)
          {
            begin_hour = String.valueOf(workTime.getBegin_hour());
            end_hour = String.valueOf(workTime.getEnd_hour());
            
            begin_minute = workTime.getBegin_minute() < 10 ? "0" + workTime.getBegin_minute() : 
              String.valueOf(workTime.getBegin_minute());
            end_minute = workTime.getEnd_minute() < 10 ? "0" + workTime.getEnd_minute() : 
              String.valueOf(workTime.getEnd_minute());
          }
          boolean isWorkDay = true;
          try
          {
            Map<String, WorkTimeCurrency> workTimeMap = this.workTimeSetManager.findComnWorkDaySet(Integer.valueOf(year), accountId, false, month);
            isWorkDay = workTimeMap.get(String.valueOf(dayWeek - 1)) != null;
          }
          catch (Exception e)
          {
            logger.error("", e);
          }
          String newMonth = month.toString();
          String newDay = day.toString();
          if (month.intValue() < 10) {
            newMonth = "0" + month.toString();
          }
          if (day.intValue() < 10) {
            newDay = "0" + day.toString();
          }
          String dateNum = year + "/" + newMonth + "/" + newDay;
          List<WorkTimeSpecial> workTimeSpecials = this.workSetDao.isWorkDayInSpecial(accountId, dateNum);
          boolean hasSpecials = CollectionUtils.isNotEmpty(workTimeSpecials);
          if (((!hasSpecials) || ("0".equals(((WorkTimeSpecial)workTimeSpecials.get(0)).getIsRest()))) && (
            (hasSpecials) || (isWorkDay)))
          {
            if (logger.isDebugEnabled()) {
              logger.debug("为" + Datetimes.formatDate(someDay) + "未进行签到、签退操作的员工，插入未打卡考勤记录开始");
            }
            List<V3xOrgMember> members = this.orgManager.getAllMembers(accountId);
            if (CollectionUtils.isNotEmpty(members))
            {
              Map<Long, V3xOrgMember> map = new HashMap();
              Set<Long> memberIds = new HashSet();
              for (V3xOrgMember member : members) {
                if ((member != null) && (member.isValid()) && (member.getOrgAccountId().equals(account.getId())))
                {
                  map.put(member.getId(), member);
                  memberIds.add(member.getId());
                }
              }
              List<Long> staffIdsHavingRecord = getMemberIdsHavingCardRecord(
                Datetimes.getTodayLastTime(someDay), accountId);
              if (CollectionUtils.isNotEmpty(staffIdsHavingRecord)) {
                memberIds.removeAll(staffIdsHavingRecord);
              }
              if (CollectionUtils.isNotEmpty(memberIds))
              {
                Object records = new ArrayList(memberIds.size());
                for (Long memberId : memberIds)
                {
                  Record record = new Record();
                  record.setNewId();
                  record.setState(noCard);
                  record.setAccountId(accountId);
                  record.setStaffer_id(memberId);
                  
                  V3xOrgMember member = (V3xOrgMember)map.get(memberId);
                  record.setDep_id(member.getOrgDepartmentId());
                  
                  record.setBegin_work_time(Datetimes.getTodayLastTime(someDay));
                  record.setEnd_work_time(Datetimes.getTodayFirstTime(someDay));
                  record.setBegin_hour(begin_hour);
                  record.setBegin_minute(begin_minute);
                  record.setEnd_hour(end_hour);
                  record.setEnd_minute(end_minute);
                  record.setYear(year);
                  record.setMonth(month.intValue());
                  record.setDay(day.intValue());
                  record.setRemark(Constants.getI18N("hr.nocard.remark", new Object[] { member.getName(), 
                    String.valueOf(year), month, day }));
                  record.setIsWorkDay(1);
                  ((List)records).add(record);
                }
                this.recordDao.savePatchAll((Collection)records);
                if (logger.isDebugEnabled()) {
                  logger.debug("为单位：" + account.getName() + "[id=" + accountId + "]，" + 
                    Datetimes.formatDate(someDay) + "未打卡员工插入未打卡记录完毕!");
                }
              }
            }
          }
        }
      }
      logger.info("为" + Datetimes.formatDate(someDay) + "未进行签到、签退操作的员工，插入未打卡考勤记录结束");
    }
  }
  
  private List<Long> getMemberIdsHavingCardRecord(Date date, Long accountId)
  {
    String hql = "select staffer_id from " + 
      Record.class.getCanonicalName() + 
      " where accountId=? and ((begin_work_time>? and begin_work_time<=?) or (end_work_time >? and end_work_time<=?))";
    Date first = Datetimes.getTodayFirstTime(date);
    Date last = Datetimes.getTodayLastTime(date);
    return this.recordDao.find(hql, -1, -1, null, new Object[] { accountId, first, last, first, last });
  }
  
  public void addRecord(Record record)
  {
    record.setIdIfNew();
    this.recordDao.save(record);
  }
  
  public void updateRecord(String remark, String signOutIP)
    throws Exception
  {
    Long staffid = CurrentUser.get().getId();
    Calendar time = Calendar.getInstance();
    Record rc = this.recordDao.getRecord(staffid, time.getTime());
    rc.setRemark(remark);
    rc.setSignOutIP(signOutIP);
    RecordState rs = new RecordState();
    int eth;
    int etm;
    if (rc.getId() == null)
    {
       eth = Integer.parseInt(getEndHour());
       etm = Integer.parseInt(getEndMinute());
      rc.setBegin_hour(getBeginHour());
      rc.setBegin_minute(getBeginMinute());
      rc.setEnd_hour(getEndHour());
      rc.setEnd_minute(getEndMinute());
    }
    else
    {
      eth = Integer.parseInt(rc.getEnd_hour());
      etm = Integer.parseInt(rc.getEnd_minute());
    }
    if (rc.getId() == null)
    {
      if (isWorkDay(time.getTime())) {
        rc.setIsWorkDay(1);
      } else {
        rc.setIsWorkDay(0);
      }
      Long depid = CurrentUser.get().getDepartmentId();
      rc.setStaffer_id(staffid);
      rc.setDep_id(depid);
      rc.setEnd_work_time(time.getTime());
      rc.setYear(time.get(1));
      rc.setMonth(time.get(2) + 1);
      rc.setDay(time.get(5));
      if ((time.get(11) < eth) || (
        (time.get(11) == eth) && (time.get(12) < etm))) {
        rs.setId(8);
      } else {
        rs.setId(1);
      }
      rc.setState(rs);
      rc.setAccountId(CurrentUser.get().getAccountId());
      addRecord(rc);
    }
    else
    {
      Calendar bt = Calendar.getInstance();
      int bth = Integer.parseInt(rc.getBegin_hour());
      int btm = Integer.parseInt(rc.getBegin_minute());
      int state = 0;
      if (rc.getBegin_work_time() == null)
      {
        if ((time.get(11) < eth) || (
          (time.get(11) == eth) && (time.get(12) < etm)))
        {
          state = 8;
          rs.setId(state);
          rc.setState(rs);
          rc.setEnd_work_time(time.getTime());
          this.recordDao.update(rc);
        }
        else
        {
          state = 1;
          rs.setId(state);
          rc.setState(rs);
          rc.setEnd_work_time(time.getTime());
          this.recordDao.update(rc);
        }
      }
      else
      {
        bt.setTime(rc.getBegin_work_time());
        if (bt.get(11) <= bth)
        {
          if (((bt.get(11) == bth ? 1 : 0) & (bt.get(12) > btm ? 1 : 0)) == 0) {}
        }
        else
        {
          if (time.get(11) >= eth)
          {
            if (((time.get(11) == eth ? 1 : 0) & (time.get(12) < etm ? 1 : 0)) == 0) {}
          }
          else
          {
            state = 6;
            rs.setId(state);
            rc.setState(rs);
            rc.setEnd_work_time(time.getTime());
            this.recordDao.update(rc); return;
          }
          if (time.get(11) <= eth) {
            if (((time.get(11) == eth ? 1 : 0) & (time.get(12) >= etm ? 1 : 0)) == 0) {
              return;
            }
          }
          state = 4;
          rs.setId(state);
          rc.setState(rs);
          rc.setEnd_work_time(time.getTime());
          this.recordDao.update(rc); return;
        }
        if (bt.get(11) >= bth)
        {
          if (((bt.get(11) == bth ? 1 : 0) & (bt.get(12) <= btm ? 1 : 0)) == 0) {}
        }
        else
        {
          if (time.get(11) >= eth)
          {
            if (((time.get(11) == eth ? 1 : 0) & (time.get(12) < etm ? 1 : 0)) == 0) {}
          }
          else
          {
            state = 5;
            rs.setId(state);
            rc.setState(rs);
            rc.setEnd_work_time(time.getTime());
            this.recordDao.update(rc); return;
          }
          if (time.get(11) <= eth)
          {
            if (((time.get(11) == eth ? 1 : 0) & (time.get(12) >= etm ? 1 : 0)) == 0) {}
          }
          else
          {
            state = 7;
            rs.setId(state);
            rc.setState(rs);
            rc.setEnd_work_time(time.getTime());
            this.recordDao.update(rc);
          }
        }
      }
    }
  }
  
  public Record getRecord(Long staffid, Date time)
    throws Exception
  {
    Record rc = this.recordDao.getRecord(staffid, time);
    return rc;
  }
  
  public String getBeginHour()
    throws Exception
  {
    return this.recordDao.getBeginHour();
  }
  
  public String getBeginMinute()
    throws Exception
  {
    return this.recordDao.getBeginMinute();
  }
  
  public String getEndHour()
    throws Exception
  {
    return this.recordDao.getEndHour();
  }
  
  public String getEndMinute()
    throws Exception
  {
    return this.recordDao.getEndMinute();
  }
  
  public List<Record> getAllRecord(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    return this.recordDao.getRecord(staffid, fromTime, toTime);
  }
  
  public List<Record> getNoBeginCardStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 1;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getNoBeginCardStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 1;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getNoEndCardStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 2;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getNoEndCardStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 2;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public int getNoCardStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    return this.recordDao.getNoCardTimesByIdNew(staffid, fromTime, toTime);
  }
  
  public Map<Long, Integer> getNoCardStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    return this.recordDao.getNoCardTimesByIdNewGroupByMemberId(fromTime, toTime);
  }
  
  public List<Record> getComeLateStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 4;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getComeLateStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 4;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getLeaveEarlyStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 5;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 5;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getBothStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 6;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getBothStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 6;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getNormalStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 7;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getNormalStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 7;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getNoBeginCardLeaveEarlyStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 8;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getNoBeginCardLeaveEarlyStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 8;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public List<Record> getComeLateNoEndCardStatisticById(Long staffid, Date fromTime, Date toTime)
    throws Exception
  {
    int state = 9;
    return this.recordDao.getStatisticByIdAndState(staffid, fromTime, toTime, state);
  }
  
  public Map<Long, Integer> getComeLateNoEndCardStatisticByIdGroupByMemberId(Date fromTime, Date toTime)
    throws Exception
  {
    int state = 9;
    return this.recordDao.getStatisticByIdAndStateGroupByMemberId(fromTime, toTime, state);
  }
  
  public void setWorkingTime(RecordWorkingTime workingTime)
    throws Exception
  {
    this.recordDao.setWorkingTime(workingTime);
  }
  
  public Record getRecordById(Long id)
    throws Exception
  {
    return this.recordDao.getRecordById(id);
  }
  
  public List<Record> getAllStaffRecords(Date time)
    throws Exception
  {
    return this.recordDao.findAllRecords(time);
  }
  
  public List<Record> getAllStaffRecord(Date fromTime, Date toTime)
    throws Exception
  {
    return this.recordDao.findAllStaffRecord(fromTime, toTime);
  }
  
  public List<Record> getAllStaffRecordByPage(Date fromTime, Date toTime)
    throws Exception
  {
    return this.recordDao.findAllStaffRecordByPage(fromTime, toTime, Pagination.getFirstResult(), 
      Pagination.getMaxResults());
  }
  
  public List<Record> getAll()
    throws Exception
  {
    return this.recordDao.findallRecords();
  }
  
  public List<Record> getRecordByState(int state)
    throws Exception
  {
    return this.recordDao.findRecordByState(state);
  }
  
  public List<Record> getAllStaffRecords(Date time, int state)
    throws Exception
  {
    return this.recordDao.findAllRecords(time, state);
  }
  
  public List<Record> getAllStaffRecord(Date fromTime, Date toTime, int state)
    throws Exception
  {
    return this.recordDao.findAllStaffRecord(fromTime, toTime, state);
  }
  
  public List<Record> getAdvancedQuery(String fromTime, String toTime, String departmentIds, int state, String personIds)
    throws Exception
  {
    Date fTime = Datetimes.parse(fromTime, "yyyy-MM-dd");
    Date tTime = Datetimes.parse(toTime, "yyyy-MM-dd");
    Set<Long> depIds = new HashSet();
    List<Long> pIds = new ArrayList();
    if (Strings.isNotBlank(departmentIds))
    {
      String[] deps = departmentIds.split(",");
      for (int i = 0; i < deps.length; i++) {
        if (deps[i].contains("|1"))
        {
          String[] deptIdStrs = deps[i].split("\\|");
          depIds.add(Long.valueOf(Long.parseLong(deptIdStrs[0])));
        }
        else
        {
          List<V3xOrgDepartment> orgdeptList = this.orgManager.getChildDepartments(Long.valueOf(Long.parseLong(deps[i])), false);
          for (V3xOrgDepartment v3xOrgDepartment : orgdeptList) {
            depIds.add(v3xOrgDepartment.getId());
          }
          depIds.add(Long.valueOf(Long.parseLong(deps[i])));
        }
      }
    }
    if (Strings.isNotBlank(personIds))
    {
      String[] pId = personIds.split(",");
      for (int i = 0; i < pId.length; i++) {
        pIds.add(Long.valueOf(Long.parseLong(pId[i])));
      }
    }
    return this.recordDao.advancedQuery(fTime, tTime, state, new ArrayList(depIds), pIds);
  }
  
  public void deleteAttendance(int monthsAgo)
  {
    List<Long> deptIds = new ArrayList();
    deleteAttendance(monthsAgo, deptIds);
  }
  
  public void deleteAttendance(int monthsAgo, List<Long> deptIds)
  {
    Calendar now = Calendar.getInstance();
    int year = now.get(1);
    int month = now.get(2) + 1;
    this.recordDao.deleteAttendance(year, month, monthsAgo, deptIds);
  }
  
  public boolean isWorkDay(Date date)
    throws Exception
  {
    Calendar time = Calendar.getInstance();
    time.setTime(date);
    int year = time.get(1);
    Integer month = Integer.valueOf(time.get(2) + 1);
    Integer day = Integer.valueOf(time.get(5));
    int dayWeek = time.get(7);
    Long orgAccountId = CurrentUser.get().getAccountId();
    boolean isWorkDay = this.workSetDao.isWorkDayInCurrency(orgAccountId, String.valueOf(year), 
      String.valueOf(dayWeek - 1));
    
    String newMonth = month.toString();
    String newDay = day.toString();
    if (month.intValue() < 10) {
      newMonth = "0" + month.toString();
    }
    if (day.intValue() < 10) {
      newDay = "0" + day.toString();
    }
    String dateNum = year + "/" + newMonth + "/" + newDay;
    List<WorkTimeSpecial> workTimeSpecials = this.workSetDao.isWorkDayInSpecial(orgAccountId, dateNum);
    if (((workTimeSpecials.size() > 0) && (!"0".equals(((WorkTimeSpecial)workTimeSpecials.get(0)).getIsRest()))) || (
      (workTimeSpecials.isEmpty()) && (!isWorkDay)))
    {
      if (!this.workSetDao.isSetInCurrency(orgAccountId))
      {
        Map<String, WorkTimeCurrency> workTimeMap = this.workTimeSetManager.findComnWorkDaySet(Integer.valueOf(year), OrgConstants.GROUPID, true, month);
        return workTimeMap.get(String.valueOf(dayWeek - 1)) != null;
      }
      return false;
    }
    return true;
  }
  
  public List<Record> getAllStaffRecordsDept(Date fromtime, Date totime, List<Long> departmentIds)
    throws Exception
  {
    return this.recordDao.findAllStaffRecordsDept(fromtime, totime, departmentIds);
  }
  
  public List<Record> getAllStaffRecordsDeptByPage(Boolean page, Date fromtime, Date totime, List<Long> departmentIds)
    throws Exception
  {
    return this.recordDao.findAllStaffRecordsDeptByPage(page, fromtime, totime, departmentIds);
  }
}
