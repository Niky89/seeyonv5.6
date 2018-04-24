package com.seeyon.v3x.hr.webmodel;

public class WebLeaveForm
{
  private Long id;
  private String name;
  private String applicationDate;
  private String leaveType;
  private String fromDate;
  private String endDate;
  private String days;
  private String reason;
  
  public String getApplicationDate()
  {
    return this.applicationDate;
  }
  
  public void setApplicationDate(String applicationDate)
  {
    this.applicationDate = applicationDate;
  }
  
  public String getDays()
  {
    return this.days;
  }
  
  public void setDays(String days)
  {
    this.days = days;
  }
  
  public String getEndDate()
  {
    return this.endDate;
  }
  
  public void setEndDate(String endDate)
  {
    this.endDate = endDate;
  }
  
  public String getFromDate()
  {
    return this.fromDate;
  }
  
  public void setFromDate(String fromDate)
  {
    this.fromDate = fromDate;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public String getLeaveType()
  {
    return this.leaveType;
  }
  
  public void setLeaveType(String leaveType)
  {
    this.leaveType = leaveType;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getReason()
  {
    return this.reason;
  }
  
  public void setReason(String reason)
  {
    this.reason = reason;
  }
}
