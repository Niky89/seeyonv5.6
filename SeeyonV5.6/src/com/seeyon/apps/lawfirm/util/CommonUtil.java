package com.seeyon.apps.lawfirm.util;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import java.util.ArrayList;
import java.util.List;

public class CommonUtil
{
  public static List<V3xOrgMember> getMembersByIds(List ids)
    throws BusinessException
  {
    List<V3xOrgMember> list = new ArrayList();
    OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
    for (Object obj : ids)
    {
      V3xOrgMember vom = orgManager.getMemberById(Long.valueOf(obj.toString()));
      list.add(vom);
    }
    return list;
  }
  
  public static String getMemberNamesByIds(List ids)
    throws BusinessException
  {
    String ret = "";
    OrgManager orgManager = (OrgManager)AppContext.getBean("orgManager");
    for (Object obj : ids) {
      if ((obj != null) && (!"".equals(obj.toString())))
      {
        V3xOrgMember vom = orgManager.getMemberById(Long.valueOf(obj.toString()));
        ret = StringHelper.concat(ret, vom.getName(), "、");
      }
    }
    return ret;
  }
}
