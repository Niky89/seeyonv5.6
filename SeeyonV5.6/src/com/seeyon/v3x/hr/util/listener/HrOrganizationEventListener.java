package com.seeyon.v3x.hr.util.listener;

import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.event.AddAccountEvent;
import com.seeyon.ctp.util.annotation.ListenEvent;
import com.seeyon.v3x.hr.manager.UserDefinedManager;

public class HrOrganizationEventListener
{
  private UserDefinedManager userDefinedManager;
  
  public void setUserDefinedManager(UserDefinedManager userDefinedManager)
  {
    this.userDefinedManager = userDefinedManager;
  }
  
  @ListenEvent(event=AddAccountEvent.class)
  public void onAddAccount(AddAccountEvent evt)
    throws Exception
  {
    this.userDefinedManager.initHrData(evt.getAccount().getId());
  }
}
