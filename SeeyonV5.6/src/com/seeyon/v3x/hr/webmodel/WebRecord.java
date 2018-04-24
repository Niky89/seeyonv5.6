package com.seeyon.v3x.hr.webmodel;

import com.seeyon.v3x.hr.domain.Record;

public class WebRecord
{
  private String name;
  private String department;
  private Record record;
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public Record getRecord()
  {
    return this.record;
  }
  
  public void setRecord(Record record)
  {
    this.record = record;
  }
  
  public String getDepartment()
  {
    return this.department;
  }
  
  public void setDepartment(String department)
  {
    this.department = department;
  }
}
