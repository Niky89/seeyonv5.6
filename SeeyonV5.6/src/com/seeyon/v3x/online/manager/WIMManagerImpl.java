package com.seeyon.v3x.online.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import com.seeyon.ctp.organization.manager.OrgManager;

public class WIMManagerImpl
  implements WIMManager
{
  private static final Log log = LogFactory.getLog(WIMManagerImpl.class);
  private OrgManager orgManager;
  
  public boolean isColRelativeTeam(long teamid)
    throws BusinessException
  {
    V3xOrgTeam orgTeam = this.orgManager.getTeamById(Long.valueOf(teamid));
    if (orgTeam == null) {
      return false;
    }
    return true;
  }
  
  public boolean existRelativeTeam(long summaryId)
    throws BusinessException
  {
    V3xOrgTeam orgTeam = getRelativeTeamByColId(summaryId);
    if (orgTeam == null) {
      return false;
    }
    return true;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public OrgManager getOrgManager()
  {
    return this.orgManager;
  }
  
  public List<V3xOrgTeam> getAllDiscussTeam(Long memberId)
    throws BusinessException
  {
    List<V3xOrgTeam> orgTeams = new ArrayList();
    List<V3xOrgTeam> teams = this.orgManager.getTeamsExceptPersonByMember(memberId);
    for (V3xOrgTeam v3xOrgTeam : teams) {
      if (OrgConstants.TEAM_TYPE.COLTEAM.ordinal() != v3xOrgTeam.getType()) {
        orgTeams.add(v3xOrgTeam);
      }
    }
    return orgTeams;
  }
  
  public V3xOrgTeam getRelativeTeamByColId(long summaryId)
    throws BusinessException
  {
    List<V3xOrgTeam> teams = this.orgManager.getTeamByType(OrgConstants.TEAM_TYPE.COLTEAM.ordinal(), AppContext.getCurrentUser().getLoginAccount());
    V3xOrgTeam orgTeam = null;
    for (V3xOrgTeam v3xOrgTeam : teams) {
      if (summaryId == v3xOrgTeam.getOwnerId().longValue())
      {
        orgTeam = v3xOrgTeam;
        break;
      }
    }
    return orgTeam;
  }
}
