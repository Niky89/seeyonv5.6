package com.seeyon.ctp.seeyonreport.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.seeyonreport.ssl.ReportSSLProtocolSocketFactory;
import com.seeyon.ctp.util.HttpClientUtil;
import com.seeyon.ctp.util.Strings;

public class ReportHttpClientUtil
{
  private static final Log log = LogFactory.getLog(HttpClientUtil.class);
  private HttpClient client;
  private HttpMethodBase httpMethod;
  
  public ReportHttpClientUtil()
  {
    this.client = new HttpClient();
    setProxy(this.client);
  }
  
  public ReportHttpClientUtil(int timeoutInMilliseconds)
  {
    this();
    HttpConnectionManagerParams ps = this.client.getHttpConnectionManager().getParams();
    ps.setSoTimeout(timeoutInMilliseconds);
    ps.setConnectionTimeout(timeoutInMilliseconds);
  }
  
  public void open(String url, String method)
  {
    if (url.startsWith("https:"))
    {
      Protocol myhttps = new Protocol("https", new ReportSSLProtocolSocketFactory(), 443);
      Protocol.registerProtocol("https", myhttps);
    }
    if ("get".equalsIgnoreCase(method))
    {
      this.httpMethod = new GetMethod(url);
    }
    else if ("post".equalsIgnoreCase(method))
    {
      this.httpMethod = new PostMethod(url);
    }
    else
    {
      log.error("Unsupport method : " + method);
      throw new IllegalArgumentException("Unsupport method : " + method);
    }
  }
  
  public void setRequestHeader(String name, String value)
  {
    this.httpMethod.setRequestHeader(name, value);
  }
  
  public void addParameter(String name, String value)
    throws IllegalArgumentException
  {
    if ((name == null) || (value == null)) {
      throw new IllegalArgumentException(
        "Arguments to addParameter(String, String) cannot be null");
    }
    if ((this.httpMethod instanceof GetMethod))
    {
      String q = this.httpMethod.getQueryString();
      if (q == null) {
        this.httpMethod.setQueryString(name + "=" + value);
      } else {
        this.httpMethod.setQueryString(q + "&" + name + "=" + value);
      }
    }
    else if ((this.httpMethod instanceof PostMethod))
    {
      ((PostMethod)this.httpMethod).addParameter(name, String.valueOf(value));
    }
  }
  
  public int send()
    throws IOException
  {
    this.httpMethod.setRequestHeader("Connection", "close");
    return this.client.executeMethod(this.httpMethod);
  }
  
  public Map<String, String> getResponseHeader()
  {
    Map<String, String> r = new HashMap();
    Header[] h = this.httpMethod.getResponseHeaders();
    Header[] arrayOfHeader1;
    int j = (arrayOfHeader1 = h).length;
    for (int i = 0; i < j; i++)
    {
      Header header = arrayOfHeader1[i];
      r.put(header.getName(), header.getValue());
    }
    return r;
  }
  
  public Map<String, String> getCookies()
  {
    Map<String, String> r = new HashMap();
    Cookie[] cs = this.client.getState().getCookies();
    Cookie[] arrayOfCookie1;
    int j = (arrayOfCookie1 = cs).length;
    for (int i = 0; i < j; i++)
    {
      Cookie c = arrayOfCookie1[i];
      r.put(c.getName(), c.getValue());
    }
    return r;
  }
  
  public InputStream getResponseBodyAsStream()
    throws IOException
  {
    return this.httpMethod.getResponseBodyAsStream();
  }
  
  public String getResponseBodyAsString(String contentCharset)
    throws IOException
  {
    InputStream instream = this.httpMethod.getResponseBodyAsStream();
    ByteArrayOutputStream outstream = new ByteArrayOutputStream(4096);
    byte[] buffer = new byte['က'];
    int len;
    while ((len = instream.read(buffer)) > 0)
    {
      outstream.write(buffer, 0, len);
    }
    outstream.close();
    
    byte[] rawdata = outstream.toByteArray();
    if (contentCharset != null) {
      return new String(rawdata, contentCharset);
    }
    return new String(rawdata);
  }
  
  public void close()
  {
    if (this.httpMethod != null) {
      try
      {
        this.httpMethod.releaseConnection();
      }
      catch (Exception e)
      {
        log.error(e);
      }
    }
  }
  
  private static void setProxy(HttpClient client)
  {
    String proxyHost = SystemEnvironment.getHttpProxyHost();
    System.out.println(proxyHost+"xxxxxx333xxxxxxxxxx"+Strings.isNotBlank(proxyHost));
    if (Strings.isNotBlank(proxyHost))
    {
      int proxyPort = SystemEnvironment.getHttpProxyPort();
      if (proxyPort > 0) {
    	  System.out.println(proxyPort+"xxxxxxxxxxxxxxxx"+(proxyPort > 0));
        client.getHostConfiguration().setProxy(proxyHost, proxyPort);
      }
    }
  }
  
  public static String getContent(String url)
  {
    if (Strings.isNotBlank(url))
    {
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod(url);
      
      setProxy(client);
      
      get.setRequestHeader("Connection", "close");
      try
      {
        client.executeMethod(get);
        return get.getResponseBodyAsString();
      }
      catch (Exception e)
      {
        log.error(e);
      }
      finally
      {
        get.releaseConnection();
      }
    }
    return null;
  }
}
