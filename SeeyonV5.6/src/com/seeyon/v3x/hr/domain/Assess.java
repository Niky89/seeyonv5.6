package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class Assess
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -7785173971041325482L;
  private Date begin_date;
  private Date end_date;
  private String assess_name;
  private String organization;
  private String assess_result;
  private String assess_content;
  private Long member_id;
  
  public String getAssess_name()
  {
    return this.assess_name;
  }
  
  public void setAssess_name(String assess_name)
  {
    this.assess_name = assess_name;
  }
  
  public Long getMember_id()
  {
    return this.member_id;
  }
  
  public void setMember_id(Long member_id)
  {
    this.member_id = member_id;
  }
  
  public String getOrganization()
  {
    return this.organization;
  }
  
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }
  
  public Date getBegin_date()
  {
    return this.begin_date;
  }
  
  public void setBegin_date(Date begin_date)
  {
    this.begin_date = begin_date;
  }
  
  public Date getEnd_date()
  {
    return this.end_date;
  }
  
  public void setEnd_date(Date end_date)
  {
    this.end_date = end_date;
  }
  
  public String getAssess_content()
  {
    return this.assess_content;
  }
  
  public void setAssess_content(String assess_content)
  {
    this.assess_content = assess_content;
  }
  
  public String getAssess_result()
  {
    return this.assess_result;
  }
  
  public void setAssess_result(String assess_result)
  {
    this.assess_result = assess_result;
  }
}
