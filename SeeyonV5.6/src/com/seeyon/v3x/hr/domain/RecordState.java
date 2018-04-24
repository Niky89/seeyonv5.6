package com.seeyon.v3x.hr.domain;

public class RecordState
{
  private int id;
  private String state_name;
  private String trueName;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getState_name()
  {
    return this.state_name;
  }
  
  public void setState_name(String state_name)
  {
    this.state_name = state_name;
  }
  
  public String getTrueName()
  {
    return this.trueName;
  }
  
  public void setTrueName(String trueName)
  {
    this.trueName = trueName;
  }
}
