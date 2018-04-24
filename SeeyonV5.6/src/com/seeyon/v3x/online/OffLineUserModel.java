package com.seeyon.v3x.online;

import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.v3x.common.domain.BaseModel;
import java.io.Serializable;

public class OffLineUserModel
  extends BaseModel
  implements Serializable
{
  private static final long serialVersionUID = -1865680433155881293L;
  private Long id;
  private String name;
  private String departmentName;
  private String postName;
  private Long loginAccountId;
  
  public OffLineUserModel() {}
  
  public OffLineUserModel(V3xOrgMember member, V3xOrgPost post, V3xOrgDepartment dept)
  {
    this.id = member.getId();
    this.name = member.getName();
    if (post != null) {
      this.postName = post.getName();
    }
    this.departmentName = dept.getName();
    this.loginAccountId = dept.getOrgAccountId();
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getDepartmentName()
  {
    return this.departmentName;
  }
  
  public void setDepartmentName(String departmentName)
  {
    this.departmentName = departmentName;
  }
  
  public String getPostName()
  {
    return this.postName;
  }
  
  public void setPostName(String postName)
  {
    this.postName = postName;
  }
  
  public Long getLoginAccountId()
  {
    return this.loginAccountId;
  }
  
  public void setLoginAccountId(Long loginAccountId)
  {
    this.loginAccountId = loginAccountId;
  }
}
