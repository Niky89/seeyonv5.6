package com.seeyon.v3x.hr.manager;

import com.seeyon.v3x.hr.domain.StaffTransfer;
import com.seeyon.v3x.hr.domain.StaffTransferType;
import java.util.List;

public abstract interface StaffTransferManager
{
  public abstract List<StaffTransfer> getStaffTransfer()
    throws Exception;
  
  public abstract void addTransfer(StaffTransfer paramStaffTransfer)
    throws Exception;
  
  public abstract void updateTransfer(StaffTransfer paramStaffTransfer)
    throws Exception;
  
  public abstract StaffTransfer getStaffTransferById(Long paramLong)
    throws Exception;
  
  public abstract List<StaffTransfer> getTransferTypeStaffTransfer()
    throws Exception;
  
  public abstract List<StaffTransfer> getDimissionTypeStaffTransfer()
    throws Exception;
  
  public abstract void deleteTransfer(Long paramLong)
    throws Exception;
  
  public abstract StaffTransferType getStaffTransferTypeById(int paramInt)
    throws Exception;
}
