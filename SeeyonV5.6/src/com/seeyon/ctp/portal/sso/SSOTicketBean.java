package com.seeyon.ctp.portal.sso;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager.TicketInfo;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.LightWeightEncoder;
import com.seeyon.ctp.util.Strings;
import java.net.URLEncoder;

public class SSOTicketBean
{
  public static SSOTicketManager.TicketInfo getTicketInfo(String ticket)
  {
    SSOTicketManager.TicketInfo info = SSOTicketManager.getInstance().getTicketInfo(ticket);
    if ((info != null) && (info.getMemberId() == -1L)) {
      try
      {
        long memberId = ((OrgManager)AppContext.getBean("orgManager")).getMemberByLoginName(info.getUsername()).getId().longValue();
        info.setMemberId(memberId);
      }
      catch (Exception localException) {}
    }
    return info;
  }
  
  private static String URLPrefix = null;
  
  private static String getURLPrefix()
  {
    if ((URLPrefix == null) || ("".equals(URLPrefix)))
    {
      String a8InternetSiteURL = SystemEnvironment.getInternetSiteURL();
      if (Strings.isBlank(a8InternetSiteURL))
      {
        URLPrefix = "";
      }
      else
      {
        String contextPath = SystemEnvironment.getContextPath();
        if (contextPath == null) {
          contextPath = "/seeyon";
        }
        URLPrefix = a8InternetSiteURL + contextPath + "/thirdpartyController.do?method=access";
      }
    }
    return URLPrefix;
  }
  
  public static String makeURLOfSSOTicket(String ticket, String path)
  {
    if ((Strings.isBlank(path)) || (path.toLowerCase().startsWith("javascript:"))) {
      return null;
    }
    String _URLPrefix = getURLPrefix();
    if (Strings.isBlank(_URLPrefix)) {
      return null;
    }
    StringBuffer url = new StringBuffer();
    
    url.append(_URLPrefix);
    
    String enc = LightWeightEncoder.encodeString("C=" + ticket + "&P=" + URLEncoder.encode(path));
    url.append("&enc=" + URLEncoder.encode(enc));
    
    return url.toString();
  }
}
