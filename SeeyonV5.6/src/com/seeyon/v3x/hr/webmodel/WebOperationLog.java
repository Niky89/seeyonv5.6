package com.seeyon.v3x.hr.webmodel;

import com.seeyon.ctp.common.po.operationlog.OperationLog;
import com.seeyon.v3x.hr.log.StaffTransferLog;

public class WebOperationLog
{
  private String staffName;
  private OperationLog operationLog;
  private StaffTransferLog staffTransferLog;
  private String operation;
  
  public StaffTransferLog getStaffTransferLog()
  {
    return this.staffTransferLog;
  }
  
  public void setStaffTransferLog(StaffTransferLog staffTransferLog)
  {
    this.staffTransferLog = staffTransferLog;
  }
  
  public String getOperation()
  {
    return this.operation;
  }
  
  public void setOperation(String operation)
  {
    this.operation = operation;
  }
  
  public OperationLog getOperationLog()
  {
    return this.operationLog;
  }
  
  public void setOperationLog(OperationLog operationLog)
  {
    this.operationLog = operationLog;
  }
  
  public String getStaffName()
  {
    return this.staffName;
  }
  
  public void setStaffName(String staffName)
  {
    this.staffName = staffName;
  }
}
