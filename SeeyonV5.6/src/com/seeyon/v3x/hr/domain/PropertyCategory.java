package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class PropertyCategory
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -994563509753155331L;
  private String name;
  private String memo;
  private Long accountId;
  private int remove;
  private boolean sysFlag;
  
  public int getRemove()
  {
    return this.remove;
  }
  
  public void setRemove(int remove)
  {
    this.remove = remove;
  }
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
  }
  
  public String getMemo()
  {
    return this.memo;
  }
  
  public void setMemo(String memo)
  {
    this.memo = memo;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public boolean isSysFlag()
  {
    return this.sysFlag;
  }
  
  public void setSysFlag(boolean sysFlag)
  {
    this.sysFlag = sysFlag;
  }
}
