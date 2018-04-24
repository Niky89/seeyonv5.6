package com.seeyon.v3x.hr.webmodel;

import java.math.BigDecimal;
import java.util.Date;

public class WebProperty
{
  private Long property_id;
  private Long page_id;
  private Long repository_id;
  private String propertyName;
  private String pageName;
  private String labelName_zh;
  private String pageName_zh;
  private String labelName_en;
  private String pageName_en;
  private String type;
  private String not_null;
  private String category;
  private String memo;
  private String modelName;
  private String display;
  private String repair;
  private int propertyType;
  private int pageNo;
  private Long f1;
  private BigDecimal f2;
  private Date f3;
  private String f4;
  private String f5;
  private boolean sysFlag;
  
  public Long getF1()
  {
    return this.f1;
  }
  
  public void setF1(Long f1)
  {
    this.f1 = f1;
  }
  
  public BigDecimal getF2()
  {
    return this.f2;
  }
  
  public void setF2(BigDecimal f2)
  {
    this.f2 = f2;
  }
  
  public Date getF3()
  {
    return this.f3;
  }
  
  public void setF3(Date f3)
  {
    this.f3 = f3;
  }
  
  public String getF4()
  {
    return this.f4;
  }
  
  public void setF4(String f4)
  {
    this.f4 = f4;
  }
  
  public String getF5()
  {
    return this.f5;
  }
  
  public void setF5(String f5)
  {
    this.f5 = f5;
  }
  
  public int getPropertyType()
  {
    return this.propertyType;
  }
  
  public void setPropertyType(int propertyType)
  {
    this.propertyType = propertyType;
  }
  
  public String getDisplay()
  {
    return this.display;
  }
  
  public void setDisplay(String display)
  {
    this.display = display;
  }
  
  public String getModelName()
  {
    return this.modelName;
  }
  
  public void setModelName(String modelName)
  {
    this.modelName = modelName;
  }
  
  public String getMemo()
  {
    return this.memo;
  }
  
  public void setMemo(String memo)
  {
    this.memo = memo;
  }
  
  public Long getPage_id()
  {
    return this.page_id;
  }
  
  public void setPage_id(Long page_id)
  {
    this.page_id = page_id;
  }
  
  public String getPageName()
  {
    return this.pageName;
  }
  
  public void setPageName(String pageName)
  {
    this.pageName = pageName;
  }
  
  public String getPageName_en()
  {
    return this.pageName_en;
  }
  
  public void setPageName_en(String pageName_en)
  {
    this.pageName_en = pageName_en;
  }
  
  public String getPageName_zh()
  {
    return this.pageName_zh;
  }
  
  public void setPageName_zh(String pageName_zh)
  {
    this.pageName_zh = pageName_zh;
  }
  
  public String getCategory()
  {
    return this.category;
  }
  
  public void setCategory(String category)
  {
    this.category = category;
  }
  
  public String getNot_null()
  {
    return this.not_null;
  }
  
  public void setNot_null(String not_null)
  {
    this.not_null = not_null;
  }
  
  public int getPageNo()
  {
    return this.pageNo;
  }
  
  public void setPageNo(int pageNo)
  {
    this.pageNo = pageNo;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public String getLabelName_en()
  {
    return this.labelName_en;
  }
  
  public void setLabelName_en(String labelName_en)
  {
    this.labelName_en = labelName_en;
  }
  
  public String getLabelName_zh()
  {
    return this.labelName_zh;
  }
  
  public void setLabelName_zh(String labelName_zh)
  {
    this.labelName_zh = labelName_zh;
  }
  
  public String getPropertyName()
  {
    return this.propertyName;
  }
  
  public void setPropertyName(String propertyName)
  {
    this.propertyName = propertyName;
  }
  
  public Long getProperty_id()
  {
    return this.property_id;
  }
  
  public void setProperty_id(Long property_id)
  {
    this.property_id = property_id;
  }
  
  public Long getRepository_id()
  {
    return this.repository_id;
  }
  
  public void setRepository_id(Long repository_id)
  {
    this.repository_id = repository_id;
  }
  
  public String getRepair()
  {
    return this.repair;
  }
  
  public void setRepair(String repair)
  {
    this.repair = repair;
  }
  
  public boolean isSysFlag()
  {
    return this.sysFlag;
  }
  
  public void setSysFlag(boolean sysFlag)
  {
    this.sysFlag = sysFlag;
  }
}
