package com.fr.web;

import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.base.IconManager;
import com.fr.base.ModuleContext;
import com.fr.general.FRLogger;
import com.fr.general.RegistEditionException;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.record.DBRecordManager;
import com.fr.script.Calculator;
import com.fr.stable.Consts;
import com.fr.web.core.A.o;
import com.fr.web.core.ErrorHandler;
import com.fr.web.core.ErrorHandlerHelper;
import com.fr.web.core.ReportDispatcher;
import com.fr.web.core.ServerEnv;
import com.fr.web.core.gzip.GZIPResponseWrapper;
import com.fr.web.utils.WebUtils;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class BaseServlet
  extends HttpServlet
{
  private static final long serialVersionUID = 2204797189775876731L;
  private static String reportName;
  
  public void init(ServletConfig paramServletConfig)
    throws ServletException
  {
    super.init(paramServletConfig);
    J2EEContainer.init(paramServletConfig);
    ServletContext localServletContext = getServletContext();
    try
    {
      if (FRContext.getCurrentEnv() == null) {
        FRContext.setCurrentEnv(new ServerEnv(localServletContext));
      }
      FRContext.getLogger().initRecord(new DBRecordManager());
      FRContext.getLogger().setLogLevel(ConfigManager.getInstance().getServerLogLevel());
    }
    catch (Exception localException)
    {
      FRContext.getLogger().error(localException.getMessage());
    }
    localServletContext.setAttribute(getClass().getName(), this);
    ModuleContext.startModule(moduleToStart());
  }
  
  public abstract String moduleToStart();
  
  public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws IOException, ServletException
  {
    if (Consts.WEB_APP_NAME == null)
    {
      Consts.WEB_APP_NAME = paramHttpServletRequest.getContextPath();
      if (Consts.WEB_APP_NAME.startsWith("/")) {
        Consts.WEB_APP_NAME = Consts.WEB_APP_NAME.substring(1);
      }
    }
    saveRequestContext(paramHttpServletRequest);
    GZIPResponseWrapper localGZIPResponseWrapper = null;
    try
    {
      String str = paramHttpServletRequest.getHeader("accept-encoding");
      if ((ConfigManager.getInstance().isSupportGzip()) && (!"false".equals(paramHttpServletRequest.getParameter("gzip"))) && (str != null) && (str.indexOf("gzip") != -1) && (Browser.resolve(paramHttpServletRequest).supportGzip())) {
        localGZIPResponseWrapper = new GZIPResponseWrapper(paramHttpServletResponse);
      }
      if (localGZIPResponseWrapper != null) {
        paramHttpServletResponse = localGZIPResponseWrapper;
      }
      paramHttpServletResponse.addHeader("P3P", "CP=CAO PSA OUR");
      ReportDispatcher.dealWithRequest(paramHttpServletRequest, paramHttpServletResponse);
    }
    catch (RegistEditionException localRegistEditionException)
    {
      if (localRegistEditionException.isAjax())
      {
        PrintWriter localPrintWriter = WebUtils.createPrintWriter(paramHttpServletResponse);
        JSONObject localJSONObject = new JSONObject();
        try
        {
          localJSONObject.put("exception", "FAILPASS");
          if (localRegistEditionException.getFUNC() != null) {
            localJSONObject.put("func", localRegistEditionException.getFUNC().toString());
          }
        }
        catch (JSONException localJSONException)
        {
          localJSONException.printStackTrace();
        }
        localPrintWriter.write(localJSONObject.toString());
        localPrintWriter.flush();
        localPrintWriter.close();
        return;
      }
      FRContext.getLogger().errorWithServerLevel(localRegistEditionException.getMessage(), localRegistEditionException);
      ErrorHandlerHelper.getErrorHandler().error(paramHttpServletRequest, paramHttpServletResponse, localRegistEditionException);
    }
    catch (Exception localException)
    {
      FRContext.getLogger().errorWithServerLevel(localException.getMessage(), localException);
      ErrorHandlerHelper.getErrorHandler().error(paramHttpServletRequest, paramHttpServletResponse, localException);
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      FRContext.getLogger().errorWithServerLevel(localOutOfMemoryError.getMessage(), localOutOfMemoryError);
      ErrorHandlerHelper.getErrorHandler().error(paramHttpServletRequest, paramHttpServletResponse, localOutOfMemoryError);
      System.gc();
    }
    finally
    {
      if (localGZIPResponseWrapper != null) {
        localGZIPResponseWrapper.finishResponse();
      }
      Calculator.clearThreadSavedParameter();
    }
  }
  
  public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse)
    throws ServletException, IOException
  {
    doGet(paramHttpServletRequest, paramHttpServletResponse);
  }
  
  public static void saveRequestContext(HttpServletRequest paramHttpServletRequest)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramHttpServletRequest.getScheme());
    localStringBuffer.append("://");
    localStringBuffer.append("127.0.0.1");
    localStringBuffer.append(':');
    localStringBuffer.append(paramHttpServletRequest.getServerPort());
    Calculator.setThreadSavedParameter("servletURL", WebUtils.createServletURL(paramHttpServletRequest));
    Calculator.setThreadSavedParameter("session_heart_beat", Boolean.toString(ConfigManager.getInstance().isSendHeartBeat()));
    Calculator.setThreadSavedParameter("serverURL", localStringBuffer);
    Calculator.setThreadSavedParameter("serverSchema", paramHttpServletRequest.getScheme());
    Calculator.setThreadSavedParameter("serverName", "127.0.0.1");
    Calculator.setThreadSavedParameter("serverPort", Integer.toString(paramHttpServletRequest.getServerPort()));
    Calculator.setThreadSavedParameter("contextPath", paramHttpServletRequest.getContextPath());
    String str = WebUtils.getReportTitleFromRequest(paramHttpServletRequest);
    reportName = str == null ? reportName : str;
    Calculator.setThreadSavedParameter("reportName", reportName);
  }
  
  public long getLastModified(HttpServletRequest paramHttpServletRequest)
  {
    String str1 = WebUtils.getHTTPRequestParameter(paramHttpServletRequest, "resource");
    if (str1 != null) {
      return o.D().A(str1);
    }
    String str2 = WebUtils.getHTTPRequestParameter(paramHttpServletRequest, "op");
    if ("toolbar_icon".equalsIgnoreCase(str2)) {
      return IconManager.getLastModified();
    }
    return -1L;
  }
}
