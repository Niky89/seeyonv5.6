package com.seeyon.v3x.hr.conf;

public class TempleteConfig
{
  private String key;
  private String name;
  private String fName;
  
  public String getFName()
  {
    return this.fName;
  }
  
  public void setFName(String name)
  {
    this.fName = name;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getKey()
  {
    return this.key;
  }
  
  public void setKey(String key)
  {
    this.key = key;
  }
}
