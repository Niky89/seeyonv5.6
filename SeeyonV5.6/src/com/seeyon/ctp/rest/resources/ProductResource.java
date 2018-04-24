package com.seeyon.ctp.rest.resources;

import com.seeyon.apps.ldap.util.LDAPTool;
import com.seeyon.ctp.cluster.ClusterConfigBean;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.ProductVersionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.v3x.bulletin.controller.BulDataController;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Path("product")
@Produces({"application/json", "application/xml"})
public class ProductResource
  extends BaseResource
{
  private static Log log = LogFactory.getLog(ProductResource.class);
  private SystemConfig systemConfig;
  
  private SystemConfig getSystemConfig()
  {
    if (this.systemConfig == null) {
      this.systemConfig = ((SystemConfig)AppContext.getBean("systemConfig"));
    }
    return this.systemConfig;
  }
  
  @GET
  @Path("/version")
  public Response getProductInfo()
  {
    String version = ProductVersionEnum.getCurrentVersion().getCanonicalVersion();
    return ok(version);
  }
  
  @GET
  @Path("orgType")
  public Response getProductType()
  {
    if (((Boolean)SysFlag.sys_isGroupVer.getFlag()).booleanValue()) {
      return ok("group");
    }
    return ok("notGroup");
  }
  
  @GET
  public Response getAllProductInfo()
  {
    Map data = new HashMap();
    data.put("version", ProductVersionEnum.getCurrentVersion().getCanonicalVersion());
    data.put("isGroup", SysFlag.sys_isGroupVer.getFlag());
    data.put("isClusterEnabled", Boolean.valueOf(ClusterConfigBean.getInstance().isClusterEnabled()));
    data.put("plugins", SystemEnvironment.getPluginIds());
    data.put("serverType", SystemEnvironment.getServerType());
    data.put("databaseType", SystemEnvironment.getDatabaseType());
    data.put("build", SystemEnvironment.getProductBuildVersion());
    data.put("buildDate", SystemEnvironment.getProductBuildDate());
    data.put("checkVerifyCode", "enable".equals(getSystemConfig().get("verify_code")) ? "1" : "0");
    data.put("canLocalAuth", LDAPTool.canLocalAuth() ? "1" : "0");
    data.put("DogNo", SystemEnvironment.getDogNo());
    if (SystemEnvironment.hasPlugin("ca"))
    {
      data.put("ca.factory", SystemProperties.getInstance().getProperty("ca.factory"));
      data.put("ca.filterstr", SystemProperties.getInstance().getProperty("ca.filterstr"));
    }
    return Response.ok(data).build();
  }
  
  @GET
  @Path("patch")
  public Response getProductPatchForWechat()
  {
    return ok("20150530");
  }
}
