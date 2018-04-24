package com.seeyon.v3x.hr.webmodel;

import com.seeyon.v3x.hr.domain.StaffTransferType;
import java.util.Date;

public class WebStaffTransfer
{
  private Long id;
  private String code;
  private String name;
  private Long member_id;
  private int transferType;
  private int state;
  private Date refer_time;
  private Date deal_time;
  private Date transfer_time;
  private String fromDepartment_name;
  private String toDepartment_name;
  private String fromLevel_name;
  private String toLevel_name;
  private String fromPost_name;
  private String toPost_name;
  private int fromMember_type;
  private int fromMember_state;
  private StaffTransferType staffTransferType;
  private String fromAccount_name;
  private String toAccount_name;
  
  public String getFromAccount_name()
  {
    return this.fromAccount_name;
  }
  
  public void setFromAccount_name(String fromAccount_name)
  {
    this.fromAccount_name = fromAccount_name;
  }
  
  public String getToAccount_name()
  {
    return this.toAccount_name;
  }
  
  public void setToAccount_name(String toAccount_name)
  {
    this.toAccount_name = toAccount_name;
  }
  
  public StaffTransferType getStaffTransferType()
  {
    return this.staffTransferType;
  }
  
  public void setStaffTransferType(StaffTransferType staffTransferType)
  {
    this.staffTransferType = staffTransferType;
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
  
  public String getFromDepartment_name()
  {
    return this.fromDepartment_name;
  }
  
  public void setFromDepartment_name(String fromDepartment_name)
  {
    this.fromDepartment_name = fromDepartment_name;
  }
  
  public String getFromLevel_name()
  {
    return this.fromLevel_name;
  }
  
  public void setFromLevel_name(String fromLevel_name)
  {
    this.fromLevel_name = fromLevel_name;
  }
  
  public String getFromPost_name()
  {
    return this.fromPost_name;
  }
  
  public void setFromPost_name(String fromPost_name)
  {
    this.fromPost_name = fromPost_name;
  }
  
  public String getToDepartment_name()
  {
    return this.toDepartment_name;
  }
  
  public void setToDepartment_name(String toDepartment_name)
  {
    this.toDepartment_name = toDepartment_name;
  }
  
  public String getToLevel_name()
  {
    return this.toLevel_name;
  }
  
  public void setToLevel_name(String toLevel_name)
  {
    this.toLevel_name = toLevel_name;
  }
  
  public String getToPost_name()
  {
    return this.toPost_name;
  }
  
  public void setToPost_name(String toPost_name)
  {
    this.toPost_name = toPost_name;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public Date getDeal_time()
  {
    return this.deal_time;
  }
  
  public void setDeal_time(Date deal_time)
  {
    this.deal_time = deal_time;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public Long getMember_id()
  {
    return this.member_id;
  }
  
  public void setMember_id(Long member_id)
  {
    this.member_id = member_id;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
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
  
  public int getTransferType()
  {
    return this.transferType;
  }
  
  public void setTransferType(int transferType)
  {
    this.transferType = transferType;
  }
  
  public Date getTransfer_time()
  {
    return this.transfer_time;
  }
  
  public void setTransfer_time(Date transfer_time)
  {
    this.transfer_time = transfer_time;
  }
}
