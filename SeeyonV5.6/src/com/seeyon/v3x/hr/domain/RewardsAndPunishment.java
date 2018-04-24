package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class RewardsAndPunishment
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -2650181549855388728L;
  private Date time;
  private int type;
  private String reason;
  private String content;
  private String organization;
  private Long member_id;
  
  public Long getMember_id()
  {
    return this.member_id;
  }
  
  public void setMember_id(Long member_id)
  {
    this.member_id = member_id;
  }
  
  public String getReason()
  {
    return this.reason;
  }
  
  public void setReason(String reason)
  {
    this.reason = reason;
  }
  
  public Date getTime()
  {
    return this.time;
  }
  
  public void setTime(Date time)
  {
    this.time = time;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setType(int type)
  {
    this.type = type;
  }
  
  public String getOrganization()
  {
    return this.organization;
  }
  
  public void setOrganization(String organization)
  {
    this.organization = organization;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public void setContent(String content)
  {
    this.content = content;
  }
}
