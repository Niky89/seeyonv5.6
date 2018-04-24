package com.seeyon.v3x.hr.webmodel;

public class SalaryMessageBO
{
  private Long userId;
  private String salaryDate;
  
  public SalaryMessageBO() {}
  
  public SalaryMessageBO(Long userId, String salaryDate)
  {
    this.userId = userId;
    this.salaryDate = salaryDate;
  }
  
  public Long getUserId()
  {
    return this.userId;
  }
  
  public void setUserId(Long userId)
  {
    this.userId = userId;
  }
  
  public String getSalaryDate()
  {
    return this.salaryDate;
  }
  
  public void setSalaryDate(String salaryDate)
  {
    this.salaryDate = salaryDate;
  }
}
