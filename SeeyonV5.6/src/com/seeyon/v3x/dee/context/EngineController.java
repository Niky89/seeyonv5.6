package com.seeyon.v3x.dee.context;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.v3x.dee.DirectoryWatcher;
import com.seeyon.v3x.dee.Document;
import com.seeyon.v3x.dee.LicenceWatcher;
import com.seeyon.v3x.dee.Parameters;
import com.seeyon.v3x.dee.TransformContext;
import com.seeyon.v3x.dee.TransformException;
import com.seeyon.v3x.dee.TransformFactory;
import com.seeyon.v3x.dee.config.EngineConfig;
import com.seeyon.v3x.dee.config.EngineContext;
import com.seeyon.v3x.dee.util.FileUtil;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EngineController
{
  private static Log log = LogFactory.getLog(EngineController.class);
  private EngineContext context;
  private String configFilePath;
  private static EngineController constroller;
  private Set<String> licenceFlowIdList = new HashSet();
  private boolean isInner = false;
  private static boolean isInA8 = false;
  private static boolean deeEnabled = true;
  
  private EngineController()
  {
    this.configFilePath = TransformFactory.getInstance().getConfigFilePath("dee.xml");
  }
  
  private EngineController(String configFilePath)
  {
    this.configFilePath = configFilePath;
  }
  
  public static EngineController getInstance(String configFilePath)
  {
    if (constroller == null)
    {
      constroller = configFilePath == null ? new EngineController() : new EngineController(configFilePath);
      
      isInA8 = FileUtil.isA8Home();
      if (isInA8) {
        try
        {
          Object tmp = AppContext.getThreadContext("DEE.dee.enable");
          deeEnabled = (tmp != null) && (Boolean.TRUE.equals(tmp));
          if (deeEnabled) {
            constroller.deeLicenceRun();
          }
        }
        catch (Exception e)
        {
          log.error(e.getLocalizedMessage(), e);
        }
      }
    }
    return constroller;
  }
  
  public Object lookup(String name)
    throws TransformException
  {
    return getContext().lookup(name);
  }
  
  public Document executeFlow(String flowName)
    throws TransformException
  {
    return executeFlow(flowName, TransformFactory.getInstance().newDocument("root"));
  }
  
  public Document executeFlow(String flowName, Parameters params)
    throws TransformException
  {
    return executeFlow(flowName, TransformFactory.getInstance().newDocument("root"), params);
  }
  
  public Document executeFlow(String flowName, Document input)
    throws TransformException
  {
    return executeFlow(flowName, input, null);
  }
  
  public Document executeFlow(String flowName, Document input, Parameters params)
    throws TransformException
  {
    if (isInA8)
    {
      try
      {
        deeEnabled = 
          ((Boolean)Class.forName("com.seeyon.ctp.common.AppContext").getMethod("hasPlugin", new Class[] { String.class }).invoke(null, new Object[] { "dee" })).booleanValue();
      }
      catch (Exception e1)
      {
        log.error("系统错误：无法获取DEE插件状态", e1);
        throw new TransformException("系统错误：无法获取DEE插件状态", e1);
      }
      if (!deeEnabled)
      {
        log.error("系统错误：DEE插件已被禁用");
        throw new TransformException("系统错误：DEE插件已被禁用");
      }
    }
    Flow f = getContext().getFlowByName(flowName);
    if (f != null)
    {
      if (params == null) {
        params = new Parameters();
      }
      params.add("flowId", flowName);
      params.add("flow", f);
      TransformContext context = new TransformContextImpl(params, this.context);
      return f.execute(input, context, params);
    }
    throw new TransformException("not found:" + flowName);
  }
  
  private EngineContext getContext()
    throws TransformException
  {
    if (this.context == null)
    {
      EngineConfig config = EngineConfig.getInstance();
      try
      {
        long start = System.currentTimeMillis();
        log.debug("开始配置解析：");
        this.context = config.parse(this.configFilePath);
        log.debug("结束配置解析：耗时 " + (System.currentTimeMillis() - start) + " 毫秒");
      }
      catch (Throwable e)
      {
        throw new TransformException(e);
      }
    }
    return this.context;
  }
  
  public void refreshContext()
    throws Throwable
  {
    EngineConfig config = EngineConfig.getInstance();
    EngineContext context = config.parse();
    this.context = context;
  }
  
  private boolean hasLicence(String flowName)
    throws Exception
  {
    if (this.isInner) {
      return true;
    }
    if (getLicenceFlowIdList().contains(flowName)) {
      return true;
    }
    return false;
  }
  
  private void deeLicenceRun()
  {
    DirectoryWatcher lw = new LicenceWatcher(TransformFactory.getInstance().getHomeDirectory() + 
      File.separator + "licence", Pattern.compile("(?:.+\\.seeyonkey)"));
    Timer t2 = new Timer();
    t2.schedule(lw, 10000L, 60000L);
  }
  
  public Set<String> getLicenceFlowIdList()
  {
    return this.licenceFlowIdList;
  }
  
  public void setInner(boolean isInner)
  {
    this.isInner = isInner;
  }
}
