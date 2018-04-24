package com.seeyon.v3x.online;

import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class OnlineAccountModel
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = 3708627939824649560L;
  private V3xOrgAccount account;
  private Long id;
  private Long superior;
  private String name;
  
  public V3xOrgAccount getAccount()
  {
    return this.account;
  }
  
  public void setAccount(V3xOrgAccount account)
  {
    this.account = account;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public Long getSuperior()
  {
    return this.superior;
  }
  
  public void setSuperior(Long superior)
  {
    this.superior = superior;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
}
