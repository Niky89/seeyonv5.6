package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class Repository
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -1174521263033517685L;
  private Long memberId;
  private Long f1;
  private Double f2;
  private Date f3;
  private String f4;
  private String f5;
  private Long property_id;
  private Long page_id;
  private Long operation_id;
  private Date createTime;
  private int ordering;
  
  public int getOrdering()
  {
    return this.ordering;
  }
  
  public void setOrdering(int ordering)
  {
    this.ordering = ordering;
  }
  
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
  public Long getOperation_id()
  {
    return this.operation_id;
  }
  
  public void setOperation_id(Long operation_id)
  {
    this.operation_id = operation_id;
  }
  
  public Long getPage_id()
  {
    return this.page_id;
  }
  
  public void setPage_id(Long page_id)
  {
    this.page_id = page_id;
  }
  
  public Long getProperty_id()
  {
    return this.property_id;
  }
  
  public void setProperty_id(Long property_id)
  {
    this.property_id = property_id;
  }
  
  public Long getF1()
  {
    return this.f1;
  }
  
  public void setF1(Long f1)
  {
    this.f1 = f1;
  }
  
  public Double getF2()
  {
    return this.f2;
  }
  
  public void setF2(Double f2)
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
  
  public Long getMemberId()
  {
    return this.memberId;
  }
  
  public void setMemberId(Long memberId)
  {
    this.memberId = memberId;
  }
}
