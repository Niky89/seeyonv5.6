package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class PageProperty
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -5233589750592581689L;
  private String name;
  private Long type;
  private int length;
  private int ordering;
  private String defaultValue;
  private int not_null;
  private int remove;
  private Long category_id;
  private Long accountId;
  private boolean sysFlag;
  
  public String getDefaultValue()
  {
    return this.defaultValue;
  }
  
  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  public int getRemove()
  {
    return this.remove;
  }
  
  public void setRemove(int remove)
  {
    this.remove = remove;
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  public void setLength(int length)
  {
    this.length = length;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public int getNot_null()
  {
    return this.not_null;
  }
  
  public void setNot_null(int not_null)
  {
    this.not_null = not_null;
  }
  
  public Long getCategory_id()
  {
    return this.category_id;
  }
  
  public void setCategory_id(Long category_id)
  {
    this.category_id = category_id;
  }
  
  public int getOrdering()
  {
    return this.ordering;
  }
  
  public void setOrdering(int ordering)
  {
    this.ordering = ordering;
  }
  
  public Long getType()
  {
    return this.type;
  }
  
  public void setType(Long type)
  {
    this.type = type;
  }
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
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
