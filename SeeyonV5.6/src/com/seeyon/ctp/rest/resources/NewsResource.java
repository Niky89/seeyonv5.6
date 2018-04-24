package com.seeyon.ctp.rest.resources;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.usermessage.MessageContent;
import com.seeyon.ctp.common.usermessage.MessageReceiver;
import com.seeyon.ctp.common.usermessage.UserMessageManager;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.sso.SSOTicketBean;
import com.seeyon.oainterface.common.OAInterfaceException;
import com.seeyon.v3x.news.NewsException;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsReadManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.news.util.NewsUtils;

@Path("news")
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class NewsResource
  extends BaseResource
{
  private static Log log = LogFactory.getLog(NewsResource.class);
  private NewsTypeManager newsTypeManager;
  private NewsDataManager newsDataManager;
  private OrgManager orgManager;
  private NewsReadManager newsReadManager;
  private UserMessageManager userMessageManager;
  private NewsUtils newsUtils;
  
  public NewsUtils getNewsUtils()
  {
    if (this.newsUtils == null) {
      this.newsUtils = ((NewsUtils)AppContext.getBean("newsUtils"));
    }
    return this.newsUtils;
  }
  
  public void setNewsUtils(NewsUtils newsUtils)
  {
    this.newsUtils = newsUtils;
  }
  
  public UserMessageManager getUserMessageManager()
  {
    if (this.userMessageManager == null) {
      this.userMessageManager = ((UserMessageManager)AppContext.getBean("userMessageManager"));
    }
    return this.userMessageManager;
  }
  
  public void setUserMessageManager(UserMessageManager userMessageManager)
  {
    this.userMessageManager = userMessageManager;
  }
  
  public NewsDataManager getNewsDataManager()
  {
    if (this.newsDataManager == null) {
      this.newsDataManager = ((NewsDataManager)AppContext.getBean("newsDataManager"));
    }
    return this.newsDataManager;
  }
  
  public NewsReadManager getNewsReadManager()
  {
    if (this.newsReadManager == null) {
      this.newsReadManager = ((NewsReadManager)AppContext.getBean("newsReadManager"));
    }
    return this.newsReadManager;
  }
  
  public void setNewsDataManager(NewsDataManager newsDataManager)
  {
    this.newsDataManager = newsDataManager;
  }
  
  public OrgManager getOrgManager()
  {
    if (this.orgManager == null) {
      this.orgManager = ((OrgManager)AppContext.getBean("orgManager"));
    }
    return this.orgManager;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public NewsTypeManager getNewsTypeManager()
  {
    if (this.newsTypeManager == null) {
      this.newsTypeManager = ((NewsTypeManager)AppContext.getBean("newsTypeManager"));
    }
    return this.newsTypeManager;
  }
  
  public void setNewsTypeManager(NewsTypeManager newsTypeManager)
  {
    this.newsTypeManager = newsTypeManager;
  }
  
  @GET
  @Path("newsType/unit/{unitId}")
  public Response getNewsTypeByUnitId(@PathParam("unitId") long unitId)
  {
    return ok(getNewsTypeManager().findAll(unitId));
  }
  
  @GET
  @Path("newsType/group")
  public Response getGroupNewsTypes()
  {
    return ok(getNewsTypeManager().groupFindAll());
  }
  
  @GET
  @Path("newsType/{typeId}")
  public Response getNewsByTypeId(@PathParam("typeId") long typeId, @QueryParam("ticket") String ticket)
    throws OAInterfaceException, NewsException
  {
    SSOTicketManager.TicketInfo info = SSOTicketBean.getTicketInfo(ticket);
    
    System.out.println(typeId+"--------------------------------"+ticket+"---------------------");
    V3xOrgMember m = null;
    long memberId;
    if (info == null)
    {
      try
      {
        m = getOrgManager().getMemberByLoginName(ticket);
      }
      catch (BusinessException e)
      {
    	  System.out.println("======="+"获取用户错误！");
        log.error("获取用户错误！");
        throw new OAInterfaceException(-1, e.getMessage(), e);
      }
      if (m == null)
      {
    	  System.out.println("======="+"ticket 无效");
        log.error("ticket 无效");
        return ok(null);
      }
      memberId = m.getId().longValue();
    }
    else
    {
      memberId = info.getMemberId();
    }
    System.out.println(memberId+"<---________________________----");
    return ok(getNewsDataManager().findByReadUserContent(Long.valueOf(memberId), Long.valueOf(typeId)));
  }
  
  @GET
  @Path("unit/{unitId}")
  public Response getNewsByUnitId(@PathParam("unitId") long unitId, @QueryParam("ticket") String ticket, @DefaultValue("2") @QueryParam("imageOrFocus") Integer imageOrFocus)
    throws OAInterfaceException, DataAccessException, NewsException
  {
    SSOTicketManager.TicketInfo info = SSOTicketBean.getTicketInfo(ticket);
    
    V3xOrgMember m = null;
    long memberId;
    if (info == null)
    {
      try
      {
        m = getOrgManager().getMemberByLoginName(ticket);
      }
      catch (BusinessException e)
      {
        log.error("获取用户错误！");
        throw new OAInterfaceException(-1, e.getMessage(), e);
      }
      if (m == null)
      {
        log.error("ticket 无效");
        return ok(null);
      }
      memberId = m.getId().longValue();
    }
    else
    {
      memberId = info.getMemberId();
    }
    List<NewsType> newsTypes = getNewsTypeManager().findAll(unitId);
    if (imageOrFocus.intValue() == 2) {
      imageOrFocus = null;
    }
    return ok(getNewsDataManager().findByReadUserContent(memberId, newsTypes, unitId, imageOrFocus));
  }
  
  @GET
  @Path("user/{userId}")
  public Response getNewsByUserId(@PathParam("userId") long userId, @QueryParam("firstNum") int firstNum, @DefaultValue("-1") @QueryParam("pageSize") int pageSize)
  {
    return ok(getNewsDataManager().findNewsByUserId(Long.valueOf(userId), firstNum, pageSize));
  }
  
  @GET
  @Path("read/{userId}/{newsId}")
  public Response readNews(@PathParam("userId") long userId, @PathParam("newsId") long newsId)
  {
    NewsData newsData = getNewsDataManager().getById(Long.valueOf(newsId));
    getNewsDataManager().syncCache(newsData, newsData.getReadCount() == null ? 0 : newsData.getReadCount().intValue());
    getNewsReadManager().setReadState(newsData, Long.valueOf(userId));
    return ok("");
  }
  
  @GET
  @Path("count/{readState}/{userId}")
  public Response getCountByReadState(@PathParam("userId") long userId, @PathParam("readState") String readState)
  {
    int count = 0;
    try
    {
      count = getNewsDataManager().findByReadUserForWechat(userId, readState);
    }
    catch (BusinessException e)
    {
      log.error("获取新闻信息异常！", e);
    }
    return ok(Integer.valueOf(count));
  }
  
  @POST
  @Consumes({"application/json"})
  @Produces({"application/json"})
  public Response addNews(Map info)
    throws Exception
  {
    String senderuser = (String)info.get("senderuser");
    String createUser = (String)info.get("createUser");
    Long userId = Long.valueOf(0L);
    if ((createUser == null) && (senderuser != null))
    {
      V3xOrgMember sendMember = getOrgManager().getMemberByLoginName(decode(senderuser));
      userId = Long.valueOf((String)info.get("createUser") == null ? sendMember.getId().longValue() : Long.parseLong((String)info.get("createUser")));
      info.put("createUser", sendMember.getId());
      info.put("publishDepartmentId", sendMember.getOrgDepartmentId());
      info.put("publishUserId", sendMember.getId());
    }
    else
    {
      userId = Long.valueOf(Long.parseLong((String)info.get("createUser")));
    }
    boolean sendAccess = getNewsTypeManager().isManagerOfType(
      Long.parseLong((String)info.get("typeId")), 
      userId.longValue());
    if (sendAccess)
    {
      NewsData data = new NewsData();
      data.setIdIfNew();
      NewsType type = getNewsTypeManager().getById(Long.valueOf(Long.parseLong((String)info.get("typeId"))));
      data.setType(type);
      Timestamp ts = new Timestamp(System.currentTimeMillis());
      ts = Timestamp.valueOf(info.get("createDate").toString());
      data.setCreateDate(ts);
      data.setReadCount(
        Integer.valueOf(Integer.parseInt(info.get("readCount").toString())));
      copyProperties(info, data);
      data.setTopOrder(new Byte("0"));
      data = getNewsDataManager().save(data, true);
      
      String ext3 = (String)info.get("ext3");
      String state = (String)info.get("state");
      if ((ext3 != null) && (state != null) && ("0".equals(ext3)) && ("30".equals(state)))
      {
        boolean noAuditPublishEdit = false;
        String deptName = data.getPublishDepartmentName();
        Set<Long> resultIds = new HashSet();
        List<V3xOrgMember> listMemberId = new ArrayList();
        listMemberId = getNewsUtils().getScopeMembers(type.getSpaceType().intValue(), data.getAccountId().longValue(), 
          type.getOutterPermit().booleanValue());
        for (V3xOrgMember member : listMemberId) {
          resultIds.add(member.getId());
        }
        getUserMessageManager().sendSystemMessage(
          MessageContent.get(noAuditPublishEdit ? "news.publishEdit" : "news.auditing", new Object[] { data.getTitle(), 
          deptName }).setBody(data.getContent(), data.getDataFormat(), data.getCreateDate()), 
          ApplicationCategoryEnum.news, 
          data.getCreateUser().longValue(), 
          MessageReceiver.get(data.getId(), resultIds, "message.link.news.assessor.auditing", new Object[] {
          String.valueOf(data.getId()) }), new Object[] {data.getTypeId() });
      }
      return ok(data);
    }
    return ok(Integer.valueOf(-1));
  }
}
