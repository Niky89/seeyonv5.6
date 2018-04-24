package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class PostChange
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = 5388639072787156625L;
  private Date start_time;
  private Date end_time;
  private String post_name;
  private String organization;
  private String wordnumber;
  private Long member_id;
  
  public String getOrganization()
  {
    return this.organization;
  }
  
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }
  
  public Long getMember_id()
  {
    return this.member_id;
  }
  
  public void setMember_id(Long member_id)
  {
    this.member_id = member_id;
  }
  
  public Date getEnd_time()
  {
    return this.end_time;
  }
  
  public void setEnd_time(Date end_time)
  {
    this.end_time = end_time;
  }
  
  public String getPost_name()
  {
    return this.post_name;
  }
  
  public void setPost_name(String post_name)
  {
    this.post_name = post_name;
  }
  
  public Date getStart_time()
  {
    return this.start_time;
  }
  
  public void setStart_time(Date start_time)
  {
    this.start_time = start_time;
  }
  
  public String getWordnumber()
  {
    return this.wordnumber;
  }
  
  public void setWordnumber(String wordnumber)
  {
    this.wordnumber = wordnumber;
  }
}
