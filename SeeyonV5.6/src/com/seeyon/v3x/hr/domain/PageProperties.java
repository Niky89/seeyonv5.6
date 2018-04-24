package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class PageProperties
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = 3314694978375516851L;
  private int property_ordering;
  private Page page;
  private PageProperty pageProperty;
  
  public Page getPage()
  {
    return this.page;
  }
  
  public void setPage(Page page)
  {
    this.page = page;
  }
  
  public PageProperty getPageProperty()
  {
    return this.pageProperty;
  }
  
  public void setPageProperty(PageProperty pageProperty)
  {
    this.pageProperty = pageProperty;
  }
  
  public int getProperty_ordering()
  {
    return this.property_ordering;
  }
  
  public void setProperty_ordering(int property_ordering)
  {
    this.property_ordering = property_ordering;
  }
}
