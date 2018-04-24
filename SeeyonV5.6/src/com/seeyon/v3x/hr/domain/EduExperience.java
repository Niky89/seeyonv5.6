package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class EduExperience
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -1333370780089113072L;
  private Date start_time;
  private Date end_time;
  private String organization;
  private String certificate_name;
  private Long member_id;
  //客开 sxl
  private String  expiry_date;//有效期
  
  
  public String getExpiry_date() {
	return expiry_date;
}

public void setExpiry_date(String expiry_date) {
	this.expiry_date = expiry_date;
}

public Date getEnd_time()
  {
    return this.end_time;
  }
  
  public void setEnd_time(Date end_time)
  {
    this.end_time = end_time;
  }
  
  public String getOrganization()
  {
    return this.organization;
  }
  
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }
  
  public Date getStart_time()
  {
    return this.start_time;
  }
  
  public void setStart_time(Date start_time)
  {
    this.start_time = start_time;
  }
  
  public Long getMember_id()
  {
    return this.member_id;
  }
  
  public void setMember_id(Long member_id)
  {
    this.member_id = member_id;
  }
  
  public String getCertificate_name()
  {
    return this.certificate_name;
  }
  
  public void setCertificate_name(String certificate_name)
  {
    this.certificate_name = certificate_name;
  }
}
