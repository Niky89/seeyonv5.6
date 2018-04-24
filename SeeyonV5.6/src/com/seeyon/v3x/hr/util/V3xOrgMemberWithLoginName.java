package com.seeyon.v3x.hr.util;

import com.seeyon.ctp.organization.po.OrgMember;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class V3xOrgMemberWithLoginName
  extends OrgMember
{
  private static final long serialVersionUID = 1L;
  private static Log LOG = LogFactory.getLog(V3xOrgMemberWithLoginName.class);
  private OrgMember member;
  private String fullPath;
  
  public V3xOrgMemberWithLoginName() {}
  
  public V3xOrgMemberWithLoginName(OrgMember member, String fullPath)
  {
    this.member = member;
    this.fullPath = fullPath;
    this.member.setName(myLoginName());
    copyProperties(this.member);
  }
  
  public OrgMember getMember()
  {
    return this.member;
  }
  
  public void setMember(OrgMember member)
  {
    this.member = member;
  }
  
  public void setFullPath(String fullPath)
  {
    this.fullPath = fullPath;
  }
  
  public String getFullPath()
  {
    return this.fullPath;
  }
  
  private String myLoginName()
  {
    return null;
  }
  
  private void copyProperties(OrgMember member)
  {
    try
    {
      PropertyUtils.copyProperties(this, member);
    }
    catch (IllegalAccessException e)
    {
      LOG.error("", e);
    }
    catch (InvocationTargetException e)
    {
      LOG.error("", e);
    }
    catch (NoSuchMethodException e)
    {
      LOG.error("", e);
    }
  }
  
  public OrgMember getOrgmMember()
  {
    return this.member;
  }
}
