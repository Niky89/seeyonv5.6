package com.seeyon.apps.lawfirm.config;

import java.util.List;

public class CaseInfoParameterConfig
{
  private String ajlb;
  private String ajlbsx;
  private int position;
  private List<String> jzmlList;
  
  public String getAjlb()
  {
    return this.ajlb;
  }
  
  public void setAjlb(String ajlb)
  {
    this.ajlb = ajlb;
  }
  
  public String getAjlbsx()
  {
    return this.ajlbsx;
  }
  
  public void setAjlbsx(String ajlbsx)
  {
    this.ajlbsx = ajlbsx;
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public void setPosition(int position)
  {
    this.position = position;
  }
  
  public List<String> getJzmlList()
  {
    return this.jzmlList;
  }
  
  public void setJzmlList(List<String> jzmlList)
  {
    this.jzmlList = jzmlList;
  }
}
