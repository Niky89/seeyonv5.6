package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class PropertyLabel
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = 3883277197961931538L;
  private String language;
  private String propertyLabelValue;
  private Long property_id;
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public void setLanguage(String language)
  {
    this.language = language;
  }
  
  public Long getProperty_id()
  {
    return this.property_id;
  }
  
  public void setProperty_id(Long property_id)
  {
    this.property_id = property_id;
  }
  
  public String getPropertyLabelValue()
  {
    return this.propertyLabelValue;
  }
  
  public void setPropertyLabelValue(String propertyLabelValue)
  {
    this.propertyLabelValue = propertyLabelValue;
  }
}
