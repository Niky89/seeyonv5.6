package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class HrSalaryPassword
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -1370801708654933773L;
  private String salaryPassword;
  private Date updateDate;
  private Date createDate;
  private Long userId;
  
  public Date getCreateDate()
  {
    return this.createDate;
  }
  
  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }
  
  public String getSalaryPassword()
  {
    return this.salaryPassword;
  }
  
  public void setSalaryPassword(String salaryPassword)
  {
    this.salaryPassword = salaryPassword;
  }
  
  public Date getUpdateDate()
  {
    return this.updateDate;
  }
  
  public void setUpdateDate(Date updateDate)
  {
    this.updateDate = updateDate;
  }
  
  public Long getUserId()
  {
    return this.userId;
  }
  
  public void setUserId(Long userId)
  {
    this.userId = userId;
  }
}
