package com.seeyon.v3x.online.manager;

public abstract interface MessageManager
{
  public abstract void updateSystemMessageState(long paramLong);
  
  public abstract void updateSystemMessageStateByUser();
  
  public abstract void updateSystemMessageStateByCategory(String paramString);
  
  public abstract String getTeamName(String paramString, Long paramLong);
  
  public abstract boolean isExist(String paramString);
  
  public abstract boolean isOnline(Long paramLong);
  
  public abstract boolean isOwner(Long paramLong)
    throws Exception;
}
