package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;
import java.util.Date;

public class StaffTransfer
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -8094227891152086564L;
  private StaffTransferType type;
  private Date refer_time;
  private Date deal_time;
  private Date transfer_time;
  private Long fromDepartment_id;
  private Long toDepartment_id;
  private Long fromPost_id;
  private Long toPost_id;
  private Long fromLevel_id;
  private Long toLevel_id;
  private int fromMember_type;
  private int toMember_type;
  private int fromMember_state;
  private int toMember_state;
  private int state;
  private String reason;
  private String deptOpinion;
  private String accOpinion;
  private Long member_id;
  
  public Date getDeal_time()
  {
    return this.deal_time;
  }
  
  public void setDeal_time(Date deal_time)
  {
    this.deal_time = deal_time;
  }
  
  public Long getFromDepartment_id()
  {
    return this.fromDepartment_id;
  }
  
  public void setFromDepartment_id(Long fromDepartment_id)
  {
    this.fromDepartment_id = fromDepartment_id;
  }
  
  public Long getFromLevel_id()
  {
    return this.fromLevel_id;
  }
  
  public void setFromLevel_id(Long fromLevel_id)
  {
    this.fromLevel_id = fromLevel_id;
  }
  
  public int getFromMember_state()
  {
    return this.fromMember_state;
  }
  
  public void setFromMember_state(int fromMember_state)
  {
    this.fromMember_state = fromMember_state;
  }
  
  public int getFromMember_type()
  {
    return this.fromMember_type;
  }
  
  public void setFromMember_type(int fromMember_type)
  {
    this.fromMember_type = fromMember_type;
  }
  
  public Long getFromPost_id()
  {
    return this.fromPost_id;
  }
  
  public void setFromPost_id(Long fromPost_id)
  {
    this.fromPost_id = fromPost_id;
  }
  
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
  
  public Date getRefer_time()
  {
    return this.refer_time;
  }
  
  public void setRefer_time(Date refer_time)
  {
    this.refer_time = refer_time;
  }
  
  public int getState()
  {
    return this.state;
  }
  
  public void setState(int state)
  {
    this.state = state;
  }
  
  public Long getToDepartment_id()
  {
    return this.toDepartment_id;
  }
  
  public void setToDepartment_id(Long toDepartment_id)
  {
    this.toDepartment_id = toDepartment_id;
  }
  
  public Long getToLevel_id()
  {
    return this.toLevel_id;
  }
  
  public void setToLevel_id(Long toLevel_id)
  {
    this.toLevel_id = toLevel_id;
  }
  
  public int getToMember_state()
  {
    return this.toMember_state;
  }
  
  public void setToMember_state(int toMember_state)
  {
    this.toMember_state = toMember_state;
  }
  
  public int getToMember_type()
  {
    return this.toMember_type;
  }
  
  public void setToMember_type(int toMember_type)
  {
    this.toMember_type = toMember_type;
  }
  
  public Long getToPost_id()
  {
    return this.toPost_id;
  }
  
  public void setToPost_id(Long toPost_id)
  {
    this.toPost_id = toPost_id;
  }
  
  public StaffTransferType getType()
  {
    return this.type;
  }
  
  public void setType(StaffTransferType type)
  {
    this.type = type;
  }
  
  public Date getTransfer_time()
  {
    return this.transfer_time;
  }
  
  public void setTransfer_time(Date transfer_time)
  {
    this.transfer_time = transfer_time;
  }
  
  public String getDeptOpinion()
  {
    return this.deptOpinion;
  }
  
  public void setDeptOpinion(String deptOpinion)
  {
    this.deptOpinion = deptOpinion;
  }
  
  public String getAccOpinion()
  {
    return this.accOpinion;
  }
  
  public void setAccOpinion(String accOpinion)
  {
    this.accOpinion = accOpinion;
  }
}
