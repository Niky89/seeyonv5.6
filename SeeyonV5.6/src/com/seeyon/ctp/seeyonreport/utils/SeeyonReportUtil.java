package com.seeyon.ctp.seeyonreport.utils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.cluster.ClusterConfigBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.report.utils.ReportDBUtils;
import com.seeyon.ctp.seeyonreport.enums.ReportDataSetEnum;
import com.seeyon.ctp.seeyonreport.vo.ReportTemplateTreeVo;
import com.seeyon.ctp.seeyonreport.vo.ReportTemplateVo;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.util.XMLCoder;

public class SeeyonReportUtil
{
  private static final Log LOG = LogFactory.getLog(SeeyonReportUtil.class);
  public static final String DEFAULTCONNNAME = "FR_REPORT";
  public static final String DEFAULTDBPWD = "defaultDbPwd2014";
  private static final String CONTEXTNAME = "seeyonreport";
  private static final String SERVLETNAME = "SeeyonReportServlet";
  private static final String SERVLETSERVICENAME = "SeeyonReportServiceServlet";
  public static String sysEnvDbUsername = "";
  public static Properties dbPro;
  
  static
  {
    try
    {
      dbPro = getSysDbConnProperties();
      sysEnvDbUsername = dbPro.getProperty(ReportDataSetEnum.SysEnvDatabase.USERNAME.getValue());
    }
    catch (BusinessException e)
    {
      LOG.error(e);
    }
  }
  
  public static ReportHttpClientUtil getHttpClientUtil(String methodType, String serviceType)
  {
    return getHttpClientUtil(methodType, serviceType, "seeyonreport", "SeeyonReportServlet");
  }
  
  public static ReportHttpClientUtil getServiceHttpClientUtil(String methodType, String serviceType)
  {
    return getHttpClientUtil(methodType, serviceType, "seeyonreport", "SeeyonReportServiceServlet");
  }
  
  private static ReportHttpClientUtil getHttpClientUtil(String methodType, String serviceType, String context, String servletName)
  {
    HttpServletRequest request = (HttpServletRequest)
      AppContext.getThreadContext("THREAD_CONTEXT_REQUEST_KEY");
    
    String url = "";
    
    ClusterConfigBean cluster = ClusterConfigBean.getInstance();
    if (cluster.isClusterEnabled())
    {
      if ("https".equals(request.getScheme())) {
        url = request.getScheme() + "://" + cluster.getClusterMainHost() + ":" + request.getServerPort();
      } else {
        url = request.getScheme() + "://" + cluster.getClusterMainHost() + ":" + cluster.getClusterMainHostPort();
      }
    }
    else
    {
      String hostIp = SystemProperties.getInstance().getProperty("seeyonreport.host.ip").trim();
      String hostPort = SystemProperties.getInstance().getProperty("seeyonreport.host.port").trim();
      if ((Strings.isBlank(hostIp)) || (Strings.isBlank(hostPort))) {
        url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
      } else {
        url = request.getScheme() + "://" + hostIp + ":" + hostPort;
      }
    }
    url = url + "/" + context + "/" + servletName;
    LOG.info("构建HttpClient请求URL:" + url);
    ReportHttpClientUtil client = new ReportHttpClientUtil();
    client.open(url, methodType);
    client.addParameter("serviceType", serviceType);
    
    client.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    return client;
  }
  
  public static String cjkEncode(String text)
  {
    if (text == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); i++)
    {
      char code = text.charAt(i);
      if ((code >= '') || (code == '[') || (code == ']')) {
        sb.append("[" + Integer.toHexString(code) + "]");
      } else {
        sb.append(text.charAt(i));
      }
    }
    return sb.toString();
  }
  
  public static List<ReportTemplateTreeVo> converJSON2TreeVO(List<Map<String, Object>> json, List<ReportTemplateTreeVo> listVo)
  {
    if (json.isEmpty()) {
      return listVo;
    }
    recursionTreeVoList(json, Long.valueOf(0L), listVo);
    return listVo;
  }
  
  private static List<ReportTemplateTreeVo> recursionTreeVoList(List<Map<String, Object>> srcList, Long pid, List<ReportTemplateTreeVo> deptList)
  {
    if (srcList.isEmpty()) {
      return deptList;
    }
    for (int i = 0; i < srcList.size(); i++)
    {
      Map<String, Object> text = (Map)srcList.get(i);
      String textVal = (String)text.get("text");
      ReportTemplateTreeVo vo = new ReportTemplateTreeVo();
      vo.setId(Long.valueOf(pid + (i + 1)));
      vo.setName(textVal);
      vo.setpId(pid);
      if (textVal.indexOf("cpt") == -1)
      {
        vo.setFile(true);
        deptList.add(vo);
        Object items = text.get("items");
        if (items != null)
        {
          List<Map<String, Object>> list = (List)items;
          recursionTreeVoList(list, vo.getId(), deptList);
        }
      }
      else
      {
        String dirVal = (String)text.get("dir");
        vo.setDir(dirVal);
        vo.setFile(false);
        deptList.add(vo);
      }
    }
    return deptList;
  }
  
  public static ReportTemplateVo convertVoCondtions(ReportTemplateVo vo)
  {
    if (vo == null) {
      return vo;
    }
    String xml = vo.getConditionInfo();
    if (Strings.isBlank(xml)) {
      return vo;
    }
    List list = (List)XMLCoder.decoder(xml);
    String json = JSONHelper.list2json(list);
    vo.setConditionInfoJson(json);
    
    StringBuilder show = new StringBuilder();
    for (Iterator item = list.iterator(); item.hasNext();)
    {
      List itemL = (List)item.next();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < itemL.size(); i++)
      {
        Map<String, String> map = (Map)itemL.get(i);
        
        String dsName = (String)map.get("dsName");
        if (i == 0)
        {
          sb.append(dsName);
          sb.append(":");
          sb.append("\r\n");
        }
        String leftBrackets = (String)map.get("leftBrackets");
        sb.append(leftBrackets);
        sb.append(" ");
        
        String dsColumnDisplay = (String)map.get("dsColumnDisplay");
        sb.append(dsColumnDisplay);
        sb.append(" ");
        
        String operText = (String)map.get("operText");
        sb.append(operText);
        sb.append(" ");
        
        String dsColumnValDisplay = (String)map.get("dsColumnValDisplay");
        sb.append("'");
        sb.append(dsColumnValDisplay);
        sb.append("'");
        sb.append(" ");
        
        String rightBrackets = (String)map.get("rightBrackets");
        sb.append(rightBrackets);
        sb.append(" ");
        
        String logicalOperators = (String)map.get("logicalOperators");
        sb.append(logicalOperators);
        sb.append(" ");
      }
      sb.append("\r\n");
      show.append(sb.toString());
    }
    vo.setSystemCondition(show.toString());
    return vo;
  }
  
  public static Map<String, String> convertTemplateCondtions2SQL(String conditionInfoXML)
  {
    Map<String, String> result = new HashMap();
    if (Strings.isBlank(conditionInfoXML)) {
      return result;
    }
    List<List<Map<String, String>>> list = (List)XMLCoder.decoder(conditionInfoXML);
    if (list == null) {
      return result;
    }
    for (Iterator<List<Map<String, String>>> item = list.iterator(); item.hasNext();)
    {
      String formTableName = "";
      
      List<Map<String, String>> itemL = (List)item.next();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < itemL.size(); i++)
      {
        Map<String, String> map = (Map)itemL.get(i);
        
        String leftBrackets = (String)map.get("leftBrackets");
        
        formTableName = (String)map.get("formTableName");
        
        String dsColumn = (String)map.get("dsColumnDisplay");
        
        String operVal = (String)map.get("operVal");
        
        String paramTypeVal = (String)map.get("paramTypeVal");
        
        String dsColumnVal = (String)map.get("dsColumnVal");
        
        String efv = "";
        ReportDataSetEnum.DataColumnType typeEnum = ReportDataSetEnum.DataColumnType.getEnumByValue(paramTypeVal);
        switch (typeEnum)
        {
        case TYPE_NUM: 
          String inputType = (String)map.get("inputType");
          efv = ReportFormFieldComUtil.getType_Kj_Sql(dsColumn, operVal, dsColumnVal, inputType);
          break;
        case TYPE_STR: 
          efv = ReportDateComUtil.getType_Date_Sql(dsColumn, operVal, dsColumnVal);
          break;
        case TYPE_ORG: 
          efv = ReportOrgComUtil.getType_Org_Sql(dsColumn, operVal, dsColumnVal);
          break;
        }
        String rightBrackets = (String)map.get("rightBrackets");
        
        String logicalOperators = (String)map.get("logicalOperators");
        
        sb.append(leftBrackets);
        sb.append(" ");
        sb.append(efv);
        sb.append(rightBrackets);
        sb.append(" ");
        sb.append(logicalOperators);
        sb.append(" ");
      }
      result.put(formTableName, sb.toString());
    }
    return result;
  }
  
  public static String convertXmlToSql(String xmlSql, String formTableName)
  {
    StringBuilder sb = new StringBuilder("select ");
    Map<String, String> dataSqlMap = (Map)XMLCoder.decoder(xmlSql);
    if (!dataSqlMap.isEmpty())
    {
      Iterator<Map.Entry<String, String>> it = dataSqlMap.entrySet().iterator();
      int index = 0;
      while (it.hasNext())
      {
        Map.Entry<String, String> entry = (Map.Entry)it.next();
        sb.append((String)entry.getKey());
        sb.append(" ");
        String display = (String)entry.getValue();
        if (display.contains("-")) {
          display = display.replace("-", "_");
        }
        sb.append(display);
        if (index < dataSqlMap.size() - 1) {
          sb.append(",");
        }
        index++;
      }
    }
    sb.append(" from ");
    sb.append(formTableName);
    return sb.toString();
  }
  
  private static String getSysDbDriverName()
  {
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    String driverName = "";
    while (drivers.hasMoreElements())
    {
      Driver di = (Driver)drivers.nextElement();
      if (!"JdbcOdbcDriver".equals(di.getClass().getSimpleName())) {
        driverName = di.getClass().getName();
      }
      if (ReportDBUtils.isOracleRuntime())
      {
        if ("oracle.jdbc.OracleDriver".equals(driverName))
        {
          driverName = "oracle.jdbc.driver.OracleDriver";
          break;
        }
      }
      else if (ReportDBUtils.isDMSQLRuntime())
      {
        driverName = "dm.jdbc.driver.DmDriver";
        break;
      }
      if (Strings.isNotBlank(driverName)) {
        break;
      }
    }
    return driverName;
  }
  
  /* Error */
  private static Properties getSysDbConnProperties()
    throws BusinessException
  {
	  Connection conn = null;
      final Properties pro = new Properties();
      try {
          conn = JDBCAgent.getDataSource().getConnection();
          final DatabaseMetaData dbmd = conn.getMetaData();
          final String dbUserName = dbmd.getUserName();
          pro.put(ReportDataSetEnum.SysEnvDatabase.USERNAME.getValue(), dbUserName);
          final String dbUrl = dbmd.getURL();
          if (ReportDBUtils.isSQLServerRuntime()) {
              final String[] urls = dbUrl.split(";");
              String url = "";
              String databaseName = "";
              for (int i = 0; i < urls.length; ++i) {
                  if (urls[i].indexOf("=") == -1 && urls[i].indexOf("://") != -1) {
                      url = urls[i];
                  }
                  if (urls[i].indexOf("=") != -1 && urls[i].indexOf("databaseName=") != -1) {
                      databaseName = urls[i];
                  }
              }
              pro.put(ReportDataSetEnum.SysEnvDatabase.URL.getValue(), String.valueOf(url) + ";" + databaseName);
          }
          else {
              pro.put(ReportDataSetEnum.SysEnvDatabase.URL.getValue(), dbUrl);
          }
          pro.put(ReportDataSetEnum.SysEnvDatabase.DRIVER.getValue(), getSysDbDriverName());
      }
      catch (SQLException e) {
          SeeyonReportUtil.LOG.error((Object)e);
          throw new BusinessException((Throwable)e);
      }
      finally {
          try {
              if (conn != null) {
                  conn.close();
              }
          }
          catch (SQLException e2) {
              SeeyonReportUtil.LOG.error((Object)e2);
              throw new BusinessException((Throwable)e2);
          }
      }
      try {
          if (conn != null) {
              conn.close();
          }
      }
      catch (SQLException e2) {
          SeeyonReportUtil.LOG.error((Object)e2);
          throw new BusinessException((Throwable)e2);
      }
      return pro;
  }
  
  public static Object toJavaBean(Object javabean, Map<String, Object> data)
  {
    Method[] methods = javabean.getClass().getDeclaredMethods();
    Method[] arrayOfMethod1;
    int j = (arrayOfMethod1 = methods).length;
    for (int i = 0; i < j; i++)
    {
      Method method = arrayOfMethod1[i];
      try
      {
        if (method.getName().startsWith("set"))
        {
          String field = method.getName();
          field = field.substring(field.indexOf("set") + 3);
          field = field.toLowerCase().charAt(0) + field.substring(1);
          method.invoke(javabean, new Object[] {
            data.get(field) });
        }
      }
      catch (Exception e)
      {
        LOG.error(e);
      }
    }
    return javabean;
  }
}
