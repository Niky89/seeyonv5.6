package com.seeyon.v3x.system.util;

import com.seeyon.ctp.common.po.applog.AppLog;
import java.util.Date;

public class WebAppLog
{
  private Long Id;
  private String user;
  private String account;
  private String depment;
  private String actionType;
  private String actionDesc;
  private String ipAddress;
  private String modelName;
  private Date actionTime;
  private AppLog appLog;
  
  public String getAccount()
  {
    return this.account;
  }
  
  public void setAccount(String account)
  {
    this.account = account;
  }
  
  public String getActionType()
  {
    return this.actionType;
  }
  
  public void setActionType(String actionType)
  {
    this.actionType = actionType;
  }
  
  public String getDepment()
  {
    return this.depment;
  }
  
  public void setDepment(String depment)
  {
    this.depment = depment;
  }
  
  public Long getId()
  {
    return this.Id;
  }
  
  public void setId(Long id)
  {
    this.Id = id;
  }
  
  public String getIpAddress()
  {
    return this.ipAddress;
  }
  
  public void setIpAddress(String ipAddress)
  {
    this.ipAddress = ipAddress;
  }
  
  public String getModelName()
  {
    return this.modelName;
  }
  
  public void setModelName(String modelName)
  {
    this.modelName = modelName;
  }
  
  public String getUser()
  {
    return this.user;
  }
  
  public void setUser(String user)
  {
    this.user = user;
  }
  
  public String getActionDesc()
  {
    return this.actionDesc;
  }
  
  public void setActionDesc(String actionDesc)
  {
    this.actionDesc = actionDesc;
  }
  
  public Date getActionTime()
  {
    return this.actionTime;
  }
  
  public void setActionTime(Date actionTime)
  {
    this.actionTime = actionTime;
  }
  
  public AppLog getAppLog()
  {
    return this.appLog;
  }
  
  public void setAppLog(AppLog appLog)
  {
    this.appLog = appLog;
  }
}
