package com.seeyon.v3x.hr.manager;

import com.seeyon.v3x.hr.domain.StaffInfo;
import java.util.List;

public abstract interface StatisticManager
{
  public abstract List<StaffInfo> getAllStaffInfo();
  
  public abstract List<StaffInfo> getAllStaffInfoByAccountId(Long paramLong);
  
  public abstract List<StaffInfo> getStaffByMemIds(List<Long> paramList);
}
