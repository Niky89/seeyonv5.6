package com.seeyon.v3x.online.manager;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgTeam;
import java.util.List;

public abstract interface WIMManager
{
  public abstract V3xOrgTeam getRelativeTeamByColId(long paramLong)
    throws BusinessException;
  
  public abstract boolean isColRelativeTeam(long paramLong)
    throws BusinessException;
  
  public abstract boolean existRelativeTeam(long paramLong)
    throws BusinessException;
  
  public abstract List<V3xOrgTeam> getAllDiscussTeam(Long paramLong)
    throws BusinessException;
}
