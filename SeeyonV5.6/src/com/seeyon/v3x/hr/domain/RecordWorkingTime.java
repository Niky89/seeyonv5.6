package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class RecordWorkingTime
  extends BaseModel
{
  private static final long serialVersionUID = -5131393458214331156L;
  private Long id;
  private int begin_hour;
  private int begin_minute;
  private int end_hour;
  private int end_minute;
  private Long accountId;
  
  public int getBegin_hour()
  {
    return this.begin_hour;
  }
  
  public void setBegin_hour(int begin_hour)
  {
    this.begin_hour = begin_hour;
  }
  
  public int getBegin_minute()
  {
    return this.begin_minute;
  }
  
  public void setBegin_minute(int begin_minute)
  {
    this.begin_minute = begin_minute;
  }
  
  public int getEnd_hour()
  {
    return this.end_hour;
  }
  
  public void setEnd_hour(int end_hour)
  {
    this.end_hour = end_hour;
  }
  
  public int getEnd_minute()
  {
    return this.end_minute;
  }
  
  public void setEnd_minute(int end_minute)
  {
    this.end_minute = end_minute;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
  }
}
