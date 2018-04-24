package com.seeyon.v3x.hr.webmodel;

import com.seeyon.v3x.hr.domain.Salary;

public class WebSalary
{
  private Salary salary;
  private Long orgDepartmentId;
  private String yearMonth;
  
  public Salary getSalary()
  {
    return this.salary;
  }
  
  public void setSalary(Salary salary)
  {
    this.salary = salary;
  }
  
  public Long getOrgDepartmentId()
  {
    return this.orgDepartmentId;
  }
  
  public void setOrgDepartmentId(Long orgDepartmentId)
  {
    this.orgDepartmentId = orgDepartmentId;
  }
  
  public String getYearMonth()
  {
    return this.yearMonth;
  }
  
  public void setYearMonth(String yearMonth)
  {
    this.yearMonth = yearMonth;
  }
}
