package com.seeyon.v3x.hr.util;

import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.view.AbstractView;

public class AjaxJsonView
  extends AbstractView
{
  private static final transient Log LOG = LogFactory.getLog(AjaxJsonView.class);
  
  protected void renderMergedOutputModel(Map map, HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    boolean isArray = false;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Starting JSON rendering of " + getBeanName());
    }
    Object model = null;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Received Object");
    }
    model = map.get("json");
    
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    if (model != null) {
      if ((model instanceof JSONArray))
      {
        isArray = true;
        jsonArray = (JSONArray)model;
      }
      else if ((model instanceof JSONObject))
      {
        jsonObject = (JSONObject)model;
      }
    }
    request.setCharacterEncoding("UTF-8");
    if (StringUtils.isEmpty(getContentType())) {
      setContentType("text/html; charset=UTF-8");
    }
    response.setContentType(getContentType());
    
    response.setHeader("Cache-Control", 
      "no-store, max-age=0, no-cache, must-revalidate");
    
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    
    response.setHeader("Pragma", "no-cache");
    if (isArray)
    {
      response.getWriter().write(jsonArray.toString());
      if (LOG.isDebugEnabled())
      {
        LOG.debug("JSON ARRAY !!!!!!");
        LOG.debug("json=" + jsonArray.toString());
        LOG.debug("content type : " + response.getContentType());
      }
    }
    else
    {
      response.getWriter().write("[" + jsonObject.toString() + "]");
      if (LOG.isDebugEnabled())
      {
        LOG.debug("JSON OBJECT !!!!!!");
        LOG.debug("json=" + jsonObject.toString());
        LOG.debug("content type : " + response.getContentType());
      }
    }
  }
}
