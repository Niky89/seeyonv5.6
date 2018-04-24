package com.seeyon.v3x.hr.util.listener;

import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.event.AddAccountEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.hr.domain.RecordWorkingTime;
import com.seeyon.v3x.hr.manager.RecordManager;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;
import java.util.Calendar;

public class HrWorkTimeEventListener
{
  private WorkTimeSetManager workTimeSetManager;
  private RecordManager recordManager;
  
  public void setWorkTimeSetManager(WorkTimeSetManager workTimeSetManager)
  {
    this.workTimeSetManager = workTimeSetManager;
  }
  
  public void setRecordManager(RecordManager recordManager)
  {
    this.recordManager = recordManager;
  }
  
  @ListenEvent(event=AddAccountEvent.class)
  public void onAddAccount(AddAccountEvent evt)
    throws Exception
  {
    Calendar c = Calendar.getInstance();
    c.set(11, 0);
    c.set(12, 0);
    c.set(13, 0);
    c.set(14, 0);
    String year = String.valueOf(c.get(1));
    String month = String.valueOf(c.get(2) + 1);
    
    WorkTimeCurrency workTimeCurrency = this.workTimeSetManager.findComnWorkTimeSet(year, month, evt.getAccount().getId(), false);
    String[] beginTime = workTimeCurrency.getAmWorkTimeBeginTime().split(":");
    String[] endTime = workTimeCurrency.getPmWorkTimeEndTime().split(":");
    
    RecordWorkingTime workingTime = new RecordWorkingTime();
    workingTime.setBegin_hour(Integer.parseInt(beginTime[0]));
    workingTime.setBegin_minute(Integer.parseInt(beginTime[1]));
    workingTime.setEnd_hour(Integer.parseInt(endTime[0]));
    workingTime.setEnd_minute(Integer.parseInt(endTime[1]));
    workingTime.setAccountId(evt.getAccount().getId());
    this.recordManager.setWorkingTime(workingTime);
  }
}
