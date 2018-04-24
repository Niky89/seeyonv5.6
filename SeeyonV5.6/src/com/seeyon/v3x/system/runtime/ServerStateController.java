package com.seeyon.v3x.system.runtime;

import com.seeyon.ctp.common.ServerState;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.util.annotation.CheckRoleAccess;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@CheckRoleAccess(roleTypes={com.seeyon.ctp.organization.OrgConstants.Role_NAME.SystemAdmin})
public class ServerStateController
  extends BaseController
{
  public ModelAndView index(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView mv = new ModelAndView("sysMgr/runTime/server");
    
    ServerState instance = ServerState.getInstance();
    if (instance.isShutdown())
    {
      mv.addObject("isShutdown", Boolean.valueOf(true));
      mv.addObject("comment", instance.getComment());
      mv.addObject("minute", Integer.valueOf(instance.getMinute()));
      mv.addObject("autoExit", Boolean.valueOf(instance.isAutoExit()));
    }
    return mv;
  }
  
  public ModelAndView doChanageState(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    Integer minute = Integer.valueOf(Integer.parseInt(request.getParameter("minute")));
    String comment = request.getParameter("comment");
    boolean autoExit = request.getParameterValues("autoExit") != null;
    if (comment == null) {
      comment = "";
    }
    ServerState.getInstance().setStateShutdown(minute.intValue(), comment, autoExit);
    
    return super.redirectModelAndView("/serverState.do?method=index");
  }
}
