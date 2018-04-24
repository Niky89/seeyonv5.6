package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class Record
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -4051323757814867124L;
  private Long staffer_id;
  private Long dep_id;
  private RecordState state;
  private Date begin_work_time;
  private Date end_work_time;
  private int year;
  private int month;
  private int day;
  private String begin_hour;
  private String begin_minute;
  private String end_hour;
  private String end_minute;
  private String remark;
  private Long accountId;
  private String signInIP;
  private String signOutIP;
  private int isWorkDay;
  
  public String getSignInIP()
  {
    return this.signInIP;
  }
  
  public void setSignInIP(String signInIP)
  {
    this.signInIP = signInIP;
  }
  
  public String getSignOutIP()
  {
    return this.signOutIP;
  }
  
  public void setSignOutIP(String signOutIP)
  {
    this.signOutIP = signOutIP;
  }
  
  public Date getBegin_work_time()
  {
    return this.begin_work_time;
  }
  
  public void setBegin_work_time(Date begin_work_time)
  {
    this.begin_work_time = begin_work_time;
  }
  
  public Long getDep_id()
  {
    return this.dep_id;
  }
  
  public void setDep_id(Long dep_id)
  {
    this.dep_id = dep_id;
  }
  
  public Date getEnd_work_time()
  {
    return this.end_work_time;
  }
  
  public void setEnd_work_time(Date end_work_time)
  {
    this.end_work_time = end_work_time;
  }
  
  public int getMonth()
  {
    return this.month;
  }
  
  public void setMonth(int month)
  {
    this.month = month;
  }
  
  public String getRemark()
  {
    return this.remark;
  }
  
  public void setRemark(String remark)
  {
    this.remark = remark;
  }
  
  public Long getStaffer_id()
  {
    return this.staffer_id;
  }
  
  public void setStaffer_id(Long staffer_id)
  {
    this.staffer_id = staffer_id;
  }
  
  public RecordState getState()
  {
    return this.state;
  }
  
  public void setState(RecordState state)
  {
    this.state = state;
  }
  
  public int getYear()
  {
    return this.year;
  }
  
  public void setYear(int year)
  {
    this.year = year;
  }
  
  public int getDay()
  {
    return this.day;
  }
  
  public void setDay(int day)
  {
    this.day = day;
  }
  
  public String getBegin_hour()
  {
    return this.begin_hour;
  }
  
  public void setBegin_hour(String begin_hour)
  {
    this.begin_hour = begin_hour;
  }
  
  public String getBegin_minute()
  {
    return this.begin_minute;
  }
  
  public void setBegin_minute(String begin_minute)
  {
    this.begin_minute = begin_minute;
  }
  
  public String getEnd_hour()
  {
    return this.end_hour;
  }
  
  public void setEnd_hour(String end_hour)
  {
    this.end_hour = end_hour;
  }
  
  public String getEnd_minute()
  {
    return this.end_minute;
  }
  
  public void setEnd_minute(String end_minute)
  {
    this.end_minute = end_minute;
  }
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
  }
  
  public int getIsWorkDay()
  {
    return this.isWorkDay;
  }
  
  public void setIsWorkDay(int isWorkDay)
  {
    this.isWorkDay = isWorkDay;
  }
}
