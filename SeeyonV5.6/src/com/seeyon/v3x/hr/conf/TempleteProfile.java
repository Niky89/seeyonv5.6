package com.seeyon.v3x.hr.conf;

import java.util.List;

public class TempleteProfile
{
  private List<TempleteConfig> templateConfigs;
  
  public List<TempleteConfig> getTempleteConfigs()
  {
    return this.templateConfigs;
  }
  
  public void setTempleteConfigs(List<TempleteConfig> templateConfigs)
  {
    this.templateConfigs = templateConfigs;
  }
}
