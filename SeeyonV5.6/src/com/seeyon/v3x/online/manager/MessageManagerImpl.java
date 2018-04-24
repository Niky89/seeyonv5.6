package com.seeyon.v3x.online.manager;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants.LoginUserState;
import com.seeyon.ctp.common.usermessage.UserMessageManager;
import com.seeyon.ctp.login.online.OnlineUser;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageManagerImpl
  implements MessageManager
{
  private static final Log log = LogFactory.getLog(MessageManagerImpl.class);
  private UserMessageManager userMessageManager;
  private AddressBookManager addressBookManager;
  private OrgManager orgManager;
  
  public AddressBookManager getAddressBookManager()
  {
    return this.addressBookManager;
  }
  
  public void setAddressBookManager(AddressBookManager addressBookManager)
  {
    this.addressBookManager = addressBookManager;
  }
  
  public OrgManager getOrgManager()
  {
    return this.orgManager;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public UserMessageManager getUserMessageManager()
  {
    return this.userMessageManager;
  }
  
  public void setUserMessageManager(UserMessageManager userMessageManager)
  {
    this.userMessageManager = userMessageManager;
  }
  
  public void updateSystemMessageState(long id)
  {
    try
    {
      this.userMessageManager.updateSystemMessageState(id);
    }
    catch (Exception e)
    {
      log.error("", e);
    }
  }
  
  public void updateSystemMessageStateByUser()
  {
    try
    {
      this.userMessageManager.updateSystemMessageStateByUser(AppContext.currentUserId());
    }
    catch (Exception e)
    {
      log.error("", e);
    }
  }
  
  public void updateSystemMessageStateByCategory(String category)
  {
    try
    {
      this.userMessageManager.updateSystemMessageStateByCategory(AppContext.currentUserId(), Integer.parseInt(category));
    }
    catch (Exception e)
    {
      log.error("", e);
    }
  }
  
  public String getTeamName(String type, Long id)
  {
    String teamName = "";
    if (id != null) {
      if ("2".equals(type)) {
        teamName = Functions.getDepartment(id).getName();
      } else if (("3".equals(type)) || ("4".equals(type)) || ("5".equals(type))) {
        teamName = Functions.getTeamName(id);
      }
    }
    return teamName;
  }
  
  public boolean isExist(String teamName)
  {
    User user = CurrentUser.get();
    return this.addressBookManager.isExist(4, teamName, user.getId(), user.getLoginAccount(), null);
  }
  
  public boolean isOnline(Long id)
  {
    OnlineUser onlineUser = Functions.getOnlineUser(id);
    if ((onlineUser != null) && (onlineUser.getState() != null) && (onlineUser.getState() == LoginUserState.online)) {
      return true;
    }
    return false;
  }
  
  public boolean isOwner(Long teamId)
    throws Exception
  {
    User user = CurrentUser.get();
    if (teamId != null)
    {
      V3xOrgTeam team = this.orgManager.getTeamById(teamId);
      return team.getOwnerId().equals(user.getId());
    }
    return false;
  }
}
