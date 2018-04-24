package com.seeyon.v3x.hr.log;

import com.seeyon.v3x.hr.domain.StaffTransferType;

public class StaffTransferLog
{
  private String staffName;
  private StaffTransferType staffTransferType;
  
  public StaffTransferLog() {}
  
  public StaffTransferLog(String staffName, StaffTransferType staffTransferType)
  {
    this.staffName = staffName;
    this.staffTransferType = staffTransferType;
  }
  
  public String getStaffName()
  {
    return this.staffName;
  }
  
  public void setStaffName(String staffName)
  {
    this.staffName = staffName;
  }
  
  public StaffTransferType getStaffTransferType()
  {
    return this.staffTransferType;
  }
  
  public void setStaffTransferType(StaffTransferType staffTransferType)
  {
    this.staffTransferType = staffTransferType;
  }
}
