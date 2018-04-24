package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Salary
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -4924784399678149782L;
  private BigDecimal salaryOriginally;
  private BigDecimal salaryActually;
  private BigDecimal salaryBusiness;
  private BigDecimal salaryBasic;
  private BigDecimal bonus;
  private BigDecimal fund;
  private BigDecimal insurance;
  private BigDecimal incomeTax;
  private Long staffId;
  private Long creatorId;
  private Date createdTimestamp;
  private Date modifiedTimestamp;
  private String status;
  private int year;
  private int month;
  private String name;
  private Long accountId;
  private boolean hasAttachment = false;
  
  public boolean isHasAttachment()
  {
    return this.hasAttachment;
  }
  
  public void setHasAttachment(boolean hasAttachment)
  {
    this.hasAttachment = hasAttachment;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public int getMonth()
  {
    return this.month;
  }
  
  public void setMonth(int month)
  {
    this.month = month;
  }
  
  public int getYear()
  {
    return this.year;
  }
  
  public void setYear(int year)
  {
    this.year = year;
  }
  
  public String getStatus()
  {
    return this.status;
  }
  
  public void setStatus(String status)
  {
    this.status = status;
  }
  
  public Date getCreatedTimestamp()
  {
    return this.createdTimestamp;
  }
  
  public void setCreatedTimestamp(Date createdTimestamp)
  {
    this.createdTimestamp = createdTimestamp;
  }
  
  public Long getCreatorId()
  {
    return this.creatorId;
  }
  
  public void setCreatorId(Long creatorId)
  {
    this.creatorId = creatorId;
  }
  
  public Date getModifiedTimestamp()
  {
    return this.modifiedTimestamp;
  }
  
  public void setModifiedTimestamp(Date modifiedTimestamp)
  {
    this.modifiedTimestamp = modifiedTimestamp;
  }
  
  public BigDecimal getSalaryOriginally()
  {
    return this.salaryOriginally;
  }
  
  public void setSalaryOriginally(BigDecimal salaryOriginally)
  {
    this.salaryOriginally = salaryOriginally;
  }
  
  public BigDecimal getSalaryActually()
  {
    return this.salaryActually;
  }
  
  public void setSalaryActually(BigDecimal salaryActually)
  {
    this.salaryActually = salaryActually;
  }
  
  public BigDecimal getSalaryBusiness()
  {
    return this.salaryBusiness;
  }
  
  public void setSalaryBusiness(BigDecimal salaryBusiness)
  {
    this.salaryBusiness = salaryBusiness;
  }
  
  public BigDecimal getSalaryBasic()
  {
    return this.salaryBasic;
  }
  
  public void setSalaryBasic(BigDecimal salaryBasic)
  {
    this.salaryBasic = salaryBasic;
  }
  
  public BigDecimal getBonus()
  {
    return this.bonus;
  }
  
  public void setBonus(BigDecimal bonus)
  {
    this.bonus = bonus;
  }
  
  public BigDecimal getFund()
  {
    return this.fund;
  }
  
  public void setFund(BigDecimal fund)
  {
    this.fund = fund;
  }
  
  public BigDecimal getIncomeTax()
  {
    return this.incomeTax;
  }
  
  public void setIncomeTax(BigDecimal incomeTax)
  {
    this.incomeTax = incomeTax;
  }
  
  public Long getStaffId()
  {
    return this.staffId;
  }
  
  public void setStaffId(Long staffId)
  {
    this.staffId = staffId;
  }
  
  public BigDecimal getInsurance()
  {
    return this.insurance;
  }
  
  public void setInsurance(BigDecimal insurance)
  {
    this.insurance = insurance;
  }
  
  public Long getAccountId()
  {
    return this.accountId;
  }
  
  public void setAccountId(Long accountId)
  {
    this.accountId = accountId;
  }
}
