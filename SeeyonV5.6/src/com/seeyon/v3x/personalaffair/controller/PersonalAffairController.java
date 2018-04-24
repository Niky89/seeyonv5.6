package com.seeyon.v3x.personalaffair.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.agent.bo.AgentModel;
import com.seeyon.apps.agent.utils.AgentUtil;
import com.seeyon.apps.uc.manager.S2SManager;
import com.seeyon.apps.uc.util.S2SUtil;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.appLog.AppLogAction;
import com.seeyon.ctp.common.appLog.manager.AppLogManager;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.content.affair.AffairCondition;
import com.seeyon.ctp.common.content.affair.AffairManager;
import com.seeyon.ctp.common.content.affair.constants.StateEnum;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.manager.OrgManagerDirect;
import com.seeyon.ctp.portal.customize.manager.CustomizeManager;
import com.seeyon.ctp.system.Constants;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.v3x.common.manager.ConfigGrantManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.EdocEnum;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;

public class PersonalAffairController
  extends BaseController
{
  private SystemConfig systemConfig;
  private OrgManager orgManager;
  private OrgManagerDirect orgManagerDirect;
  private CustomizeManager customizeManager;
  private StaffInfoManager staffInfoManager;
  private AffairManager affairManager;
  private ConfigGrantManager configGrantManager;
  private MobileMessageManager mobileMessageManager;
  private S2SManager s2sManager;
  private AppLogManager appLogManager;
  private S2SUtil s2sUtil;
  
  public void setS2sUtil(S2SUtil s2sUtil)
  {
    this.s2sUtil = s2sUtil;
  }
  
  public void setSystemConfig(SystemConfig systemConfig)
  {
    this.systemConfig = systemConfig;
  }
  
  public void setOrgManager(OrgManager orgManager)
  {
    this.orgManager = orgManager;
  }
  
  public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect)
  {
    this.orgManagerDirect = orgManagerDirect;
  }
  
  public void setCustomizeManager(CustomizeManager customizeManager)
  {
    this.customizeManager = customizeManager;
  }
  
  public void setStaffInfoManager(StaffInfoManager staffInfoManager)
  {
    this.staffInfoManager = staffInfoManager;
  }
  
  public void setAffairManager(AffairManager affairManager)
  {
    this.affairManager = affairManager;
  }
  
  public void setConfigGrantManager(ConfigGrantManager configGrantManager)
  {
    this.configGrantManager = configGrantManager;
  }
  
  public void setMobileMessageManager(MobileMessageManager mobileMessageManager)
  {
    this.mobileMessageManager = mobileMessageManager;
  }
  
  public void setS2sManager(S2SManager s2sManager)
  {
    this.s2sManager = s2sManager;
  }
  
  public void setAppLogManager(AppLogManager appLogManager)
  {
    this.appLogManager = appLogManager;
  }
  
  public ModelAndView personalInfo(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("personalAffair/person/personalInfo");
    User user = AppContext.getCurrentUser();
    Long memberId = user.getId();
    V3xOrgMember member = this.orgManager.getMemberById(memberId);
    modelAndView.addObject("member", member);
    Locale orgLocale = this.orgManagerDirect.getMemberLocaleById(memberId);
    modelAndView.addObject("orgLocale", orgLocale);
    ContactInfo contactInfo = this.staffInfoManager.getContactInfoById(memberId);
    modelAndView.addObject("contactInfo", contactInfo);
    String fileName = Functions.getAvatarImageUrl(memberId);
    modelAndView.addObject("fileName", fileName);
    String selfImage = this.customizeManager.getCustomizeValue(memberId.longValue(), "avatar");
    if (selfImage != null) {
      modelAndView.addObject("selfImage", selfImage);
    } else {
      modelAndView.addObject("selfImage", "pic.gif");
    }
    String ctp2 = this.customizeManager.getCustomizeValue(memberId.longValue(), "messageSoundEnabled");
    String ctp3 = this.customizeManager.getCustomizeValue(memberId.longValue(), "messageViewRemoved");
    String ctp4 = this.customizeManager.getCustomizeValue(memberId.longValue(), "indexShow");
    String ctp5 = this.customizeManager.getCustomizeValue(memberId.longValue(), "handle_Expand");
    String ctp6 = this.customizeManager.getCustomizeValue(memberId.longValue(), "track_send");
    String ctp7 = this.customizeManager.getCustomizeValue(memberId.longValue(), "track_process");
    String ctp10 = this.customizeManager.getCustomizeValue(memberId.longValue(), "smsLoginEnabled");
    
    String enableMsgSound = ctp2 == null ? "" : ctp2;
    String msgClosedEnable = ctp3 == null ? "" : ctp3;
    String isShowIndexSummary = ctp4 == null ? "" : ctp4;
    String extendConfig = ctp5 == null ? "" : ctp5;
    String tracksend = ctp6 == null ? "" : ctp6;
    String trackprocess = ctp7 == null ? "" : ctp7;
    String smsLoginEnable = ctp10 == null ? "" : ctp10;
    
    modelAndView.addObject("enableMsgSound", enableMsgSound);
    modelAndView.addObject("msgClosedEnable", msgClosedEnable);
    modelAndView.addObject("isShowIndexSummary", isShowIndexSummary);
    modelAndView.addObject("extendConfig", extendConfig);
    modelAndView.addObject("tracksend", tracksend);
    modelAndView.addObject("trackprocess", trackprocess);
    modelAndView.addObject("smsLoginEnable", smsLoginEnable);
    
    boolean systemMsgSoundEnable = false;
    String enableMsgSoundConfig = this.systemConfig.get("SMS_hint");
    if (enableMsgSoundConfig != null) {
      systemMsgSoundEnable = "enable".equals(enableMsgSoundConfig);
    }
    modelAndView.addObject("systemMsgSoundEnable", Boolean.valueOf(systemMsgSoundEnable));
    
    modelAndView.addObject("isCanUseSMS", Boolean.valueOf(this.mobileMessageManager.isAccountOfCanUseSMS(user.getAccountId().longValue())));
    
    return modelAndView;
  }
  
  public ModelAndView updatePersonalInfo(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    if ((this.s2sManager != null) && (this.s2sUtil != null) && 
      (this.s2sUtil.getConn())) {
      this.s2sManager.setSeverIpOrPort(String.valueOf(request.getServerPort()));
    }
    User user = AppContext.getCurrentUser();
    Long memberId = user.getId();
    V3xOrgMember member = this.orgManager.getMemberById(memberId);
    StaffInfo staffinfo = this.staffInfoManager.getStaffInfoById(memberId);
    String imageName = request.getParameter("filename");
    if (staffinfo == null)
    {
      staffinfo = new StaffInfo();
      staffinfo.setIdIfNew();
      staffinfo.setOrg_member_id(memberId);
      staffinfo.setSelf_image_name(imageName);
      this.staffInfoManager.addStaffInfo(staffinfo);
    }
    else
    {
      staffinfo.setSelf_image_name(imageName);
      this.staffInfoManager.updateStaffInfo(staffinfo);
    }
    Locale locale = LocaleContext.parseLocale(request.getParameter("primaryLanguange"));
    this.orgManagerDirect.setMemberLocale(member, locale);
    
    Map<String, Object> memberMap = new HashMap();
    memberMap.put("officenumber", Strings.isBlank(request.getParameter("telephone")) ? "" : request.getParameter("telephone").toString());
    memberMap.put("telnumber", Strings.isBlank(request.getParameter("telNumber")) ? "" : request.getParameter("telNumber").toString());
    memberMap.put("address", Strings.isBlank(request.getParameter("address")) ? "" : request.getParameter("address").toString());
    memberMap.put("postalcode", Strings.isBlank(request.getParameter("postalcode")) ? "" : request.getParameter("postalcode").toString());
    memberMap.put("emailaddress", Strings.isBlank(request.getParameter("email")) ? "" : request.getParameter("email").toString());
    memberMap.put("postAddress", Strings.isBlank(request.getParameter("communication")) ? "" : request.getParameter("communication").toString());
    memberMap.put("weibo", Strings.isBlank(request.getParameter("wb")) ? "" : request.getParameter("wb").toString());
    memberMap.put("weixin", Strings.isBlank(request.getParameter("wx")) ? "" : request.getParameter("wx").toString());
    memberMap.put("website", Strings.isBlank(request.getParameter("website")) ? "" : request.getParameter("website").toString());
    memberMap.put("blog", Strings.isBlank(request.getParameter("blog")) ? "" : request.getParameter("blog").toString());
    member.setProperties(memberMap);
    member.setDescription(Strings.isBlank(request.getParameter("comment")) ? "" : request.getParameter("comment").toString());
    
    ContactInfo contact = this.staffInfoManager.getContactInfoById(memberId);
    if (contact == null)
    {
      ContactInfo contactInfo = new ContactInfo();
      bind(request, contactInfo);
      contactInfo.setMember_id(memberId);
      this.staffInfoManager.addContactInfo(contactInfo, member);
    }
    else
    {
      bind(request, contact);
      contact.setMember_id(memberId);
      this.staffInfoManager.updateContactInfo(contact, member);
    }
    this.orgManagerDirect.updateMember(member);
    
    List<Map<String, String>> listCustomize = new ArrayList();
    Map<String, String> map2 = new HashMap();
    Map<String, String> map3 = new HashMap();
    Map<String, String> map4 = new HashMap();
    Map<String, String> map5 = new HashMap();
    Map<String, String> map6 = new HashMap();
    Map<String, String> map7 = new HashMap();
    Map<String, String> map8 = new HashMap();
    Map<String, String> mapSmsLoginEnable = new HashMap();
    
    map2.put("Ckey", "messageSoundEnabled");
    map2.put("Cvalue", Strings.isNotBlank(request.getParameter("enableMsgSound")) ? request.getParameter("enableMsgSound") : "");
    listCustomize.add(map2);
    
    map3.put("Ckey", "messageViewRemoved");
    map3.put("Cvalue", Strings.isNotBlank(request.getParameter("msgClosedEnable")) ? "true" : "false");
    listCustomize.add(map3);
    
    map4.put("Ckey", "indexShow");
    map4.put("Cvalue", Strings.isNotBlank(request.getParameter("isShowIndexSummary")) ? "false" : "");
    listCustomize.add(map4);
    
    map5.put("Ckey", "handle_Expand");
    map5.put("Cvalue", Strings.isNotBlank(request.getParameter("extendConfig")) ? "" : "false");
    listCustomize.add(map5);
    
    map6.put("Ckey", "avatar");
    map6.put("Cvalue", Strings.isNotBlank(imageName) ? imageName : "");
    listCustomize.add(map6);
    
    map7.put("Ckey", "track_send");
    map7.put("Cvalue", Strings.isNotBlank(request.getParameter("tracksend")) ? "true" : "false");
    listCustomize.add(map7);
    
    map8.put("Ckey", "track_process");
    map8.put("Cvalue", Strings.isNotBlank(request.getParameter("trackprocess")) ? "true" : "false");
    listCustomize.add(map8);
    
    mapSmsLoginEnable.put("Ckey", "smsLoginEnabled");
    String smsLoginEnable = Strings.isNotBlank(request.getParameter("smsLoginEnable")) ? "true" : "false";
    String smsLoginEnableOld = this.customizeManager.getCustomizeValue(memberId.longValue(), "smsLoginEnabled");
    if ((smsLoginEnableOld == null) || (smsLoginEnableOld.trim().length() == 0)) {
      smsLoginEnableOld = "false";
    }
    if (!smsLoginEnableOld.equals(smsLoginEnable)) {
      if ("true".equals(smsLoginEnable)) {
        this.appLogManager.insertLog(user, AppLogAction.SmsLogin_Enable, new String[0]);
      } else {
        this.appLogManager.insertLog(user, AppLogAction.SmsLogin_Disable, new String[0]);
      }
    }
    mapSmsLoginEnable.put("Cvalue", smsLoginEnable);
    listCustomize.add(mapSmsLoginEnable);
    
    saveOrUpdateCustomize(listCustomize);
    
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<script type='text/javascript'>");
    out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok", new Object[0]) + "')");
    out.println("</script>");
    out.flush();
    return super.redirectModelAndView("/personalAffair.do?method=personalInfo");
  }
  
  public void saveOrUpdateCustomize(List<Map<String, String>> listCustomize)
  {
    User user = AppContext.getCurrentUser();
    if (Strings.isNotEmpty(listCustomize)) {
      for (int i = 0; i < listCustomize.size(); i++)
      {
        Map<String, String> mapCustomize = (Map)listCustomize.get(i);
        String Ckey = (String)mapCustomize.get("Ckey");
        String Cvalue = (String)mapCustomize.get("Cvalue");
        user.setCustomize(Ckey, Cvalue);
      }
    }
  }
  
  @Deprecated
  public ModelAndView morePending4App(HttpServletRequest request, HttpServletResponse response)
    throws Exception
  {
    ModelAndView modelAndView = new ModelAndView("personalAffair/person/morePending");
    
    String app = request.getParameter("app");
    
    Long memberId = AppContext.getCurrentUser().getId();
    Object[] agentObj = AgentUtil.getUserAgentToMap(memberId);
    boolean agentToFlag = ((Boolean)agentObj[0]).booleanValue();
    Map<Integer, List<AgentModel>> ma = (Map)agentObj[1];
    
    String ExtendTitle = null;
    String ExtendId = null;
    boolean isHiddenBach = false;
    
    AffairCondition condition = null;
    if ("agent".equals(app))
    {
      List<ApplicationCategoryEnum> apps = new ArrayList();
      apps.add(ApplicationCategoryEnum.collaboration);
      apps.add(ApplicationCategoryEnum.edoc);
      apps.add(ApplicationCategoryEnum.meeting);
      apps.add(ApplicationCategoryEnum.inquiry);
      apps.add(ApplicationCategoryEnum.bulletin);
      apps.add(ApplicationCategoryEnum.news);
      boolean isGov = ((Boolean)SysFlag.is_gov_only.getFlag()).booleanValue();
      
      String infoSearchValue = "";
      if (isGov)
      {
        apps.add(ApplicationCategoryEnum.info);
        infoSearchValue = ",A___32,A___29";
      }
      condition = new AffairCondition(memberId, StateEnum.col_pending, apps);
      condition.addSearch(AffairCondition.SearchCondition.policy4Portal, "A___1,A___4,A___6,A___8" + infoSearchValue, null);
      condition.setAgent(Boolean.valueOf(agentToFlag), ma);
      
      ExtendTitle = "menu.person.space.8";
      ExtendId = "8";
    }
    else if ("Coll".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.collaboration });
      
      condition.setAgent(Boolean.valueOf(agentToFlag), ma);
      ExtendTitle = "menu.person.space.1";
      ExtendId = "1";
    }
    else if ("Edoc".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.edoc });
      
      condition.setAgent(Boolean.valueOf(agentToFlag), ma);
      ExtendTitle = "menu.person.space.2";
      ExtendId = "2";
    }
    else if ("Meeting".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.meeting });
      
      condition.setAgent(Boolean.valueOf(agentToFlag), ma);
      ExtendTitle = "menu.person.space.4";
      ExtendId = "4";
      isHiddenBach = true;
    }
    else if ("PubInfo".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.bulletin, 
        ApplicationCategoryEnum.news, 
        ApplicationCategoryEnum.inquiry });
      
      condition.addSearch(AffairCondition.SearchCondition.policy4Portal, "A___8", null);
      condition.setAgent(Boolean.valueOf(agentToFlag), ma);
      ExtendTitle = "menu.person.space.6";
      ExtendId = "6";
      isHiddenBach = true;
    }
    else if ("Inquiry".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.inquiry });
      
      condition.addSearch(AffairCondition.SearchCondition.policy4Portal, "A___10___1", null);
      ExtendTitle = "menu.person.space.5";
      ExtendId = "5";
      isHiddenBach = true;
    }
    else if ("ZHBG".equals(app))
    {
      condition = new AffairCondition(memberId, StateEnum.col_pending, new ApplicationCategoryEnum[] {
        ApplicationCategoryEnum.office });
      
      ExtendTitle = "menu.person.space.7";
      ExtendId = "7";
      isHiddenBach = true;
    }
    else
    {
      return modelAndView;
    }
    String conditions = request.getParameter("condition");
    if (Strings.isNotBlank(conditions))
    {
      String textField1 = request.getParameter("textfield");
      String textField2 = request.getParameter("textfield1");
      AffairCondition.SearchCondition con = AffairCondition.SearchCondition.valueOf(conditions);
      if (con != null)
      {
        if ((Strings.isNotBlank(textField1)) && (Strings.isNotBlank(textField2)))
        {
          textField1 = Datetimes.formatDatetime(Datetimes.getTodayFirstTime(textField1));
          textField2 = Datetimes.formatDatetime(Datetimes.getTodayLastTime(textField2));
        }
        condition.addSearch(con, textField1, textField2);
      }
    }
    boolean isGovVersion = ((Boolean)SysFlag.is_gov_only.getFlag()).booleanValue();
    if (isGovVersion)
    {
      boolean isEdocCreateRegister = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), memberId, EdocEnum.edocType.recEdoc.ordinal());
      
      boolean hasEdocDistributeGrant = EdocRoleHelper.isEdocCreateRole(CurrentUser.get().getLoginAccount(), memberId, EdocEnum.edocType.distributeEdoc.ordinal());
      
      boolean hasMtAppAuditGrant = this.configGrantManager.hasConfigGrant(CurrentUser.get().getLoginAccount(), memberId, "v3x_meeting_create_acc", "v3x_meeting_create_acc_review");
      modelAndView.addObject("isEdocCreateRegister", Boolean.valueOf(isEdocCreateRegister));
      modelAndView.addObject("hasMtAppAuditGrant", Boolean.valueOf(hasMtAppAuditGrant));
      modelAndView.addObject("hasEdocDistributeGrant", Boolean.valueOf(hasEdocDistributeGrant));
    }
    List<CtpAffair> listAffair = null;
    if ("agent".equals(app)) {
      listAffair = condition.getAgentPendingAffair(this.affairManager, new FlipInfo());
    } else {
      listAffair = condition.getPendingAffair(this.affairManager, new FlipInfo());
    }
    modelAndView.addObject("isHiddenBach", Boolean.valueOf(isHiddenBach));
    modelAndView.addObject("panelSize", Integer.valueOf(1));
    modelAndView.addObject("SampleSearch", Boolean.valueOf(true));
    modelAndView.addObject("ExtendTitle", ExtendTitle);
    modelAndView.addObject("ExtendId", ExtendId);
    
    modelAndView.addObject("pendingList", listAffair);
    
    return modelAndView;
  }
}
