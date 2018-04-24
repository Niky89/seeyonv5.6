package com.seeyon.v3x.hr.domain;

public class StaffTransferType
{
  private int id;
  private String type_name;
  
  public StaffTransferType() {}
  
  public StaffTransferType(int id, String type_name)
  {
    setId(id);
    setType_name(type_name);
  }
  
  public StaffTransferType(StaffTransferType staffTransferType)
  {
    this(staffTransferType.getId(), staffTransferType.getType_name());
  }
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getType_name()
  {
    return this.type_name;
  }
  
  public void setType_name(String type_name)
  {
    this.type_name = type_name;
  }
}
