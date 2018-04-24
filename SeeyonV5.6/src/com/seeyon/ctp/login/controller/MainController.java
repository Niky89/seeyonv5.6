package com.seeyon.ctp.login.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.apps.ldap.config.LDAPConfig;
import com.seeyon.apps.uc.util.S2SConfig;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ServerState;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.Constants;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.controller.BaseController;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.common.security.SecurityHelper;
import com.seeyon.ctp.common.shareMap.V3xShareMap;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.common.web.util.WebUtil;
import com.seeyon.ctp.form.upgrade.UpgradeUtil;
import com.seeyon.ctp.login.HomePageParamsInterface;
import com.seeyon.ctp.login.LoginActiveX;
import com.seeyon.ctp.login.LoginControl;
import com.seeyon.ctp.login.online.OnlineManager;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.portal.expansion.ExpandJspForHomePage;
import com.seeyon.ctp.portal.hotspot.manager.PortalHotSpotManager;
import com.seeyon.ctp.portal.manager.PortalManager;
import com.seeyon.ctp.portal.po.PortalHotspot;
import com.seeyon.ctp.portal.po.PortalLoginTemplate;
import com.seeyon.ctp.portal.po.PortalSkinChoice;
import com.seeyon.ctp.portal.po.PortalTemplate;
import com.seeyon.ctp.portal.po.PortalTemplateSetting;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalSkinChoiceManager;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalTemplateSettingManager;
import com.seeyon.ctp.util.Cookies;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.json.JSONUtil;
import com.seeyon.ctp.util.json.mapper.JSONMapper;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;

public class MainController extends BaseController {
	private static final Log log = LogFactory.getLog(MainController.class);
	private static final int expiry = 86400;
	private static final int expiry10year = 315360000;
	private SystemConfig systemConfig;
	private LoginControl loginControl;
	private final String DefaultFramePage = "/frame.jsp";
	private final String DefaultLoginPage = "/login.jsp";
	private static String appDefaultPath = "/indexOpenWindow.jsp";
	private final String DefaultSkinPath = "harmony";
	private String cframePage;
	private String cloginPage;
	private PortalTemplateSettingManager portalTemplateSettingManager;
	private PortalHotSpotManager portalHotSpotManager;
	private PortalSkinChoiceManager portalSkinChoiceManager;
	private PortalManager portalManager;
	private OnlineManager onlineManager;
	private MobileMessageManager mobileMessageManager;

	public void setPortalManager(PortalManager portalManager) {
		this.portalManager = portalManager;
	}

	public void setPortalHotSpotManager(PortalHotSpotManager portalHotSpotManager) {
		this.portalHotSpotManager = portalHotSpotManager;
	}

	public void setOnlineManager(OnlineManager onlineManager) {
		this.onlineManager = onlineManager;
	}

	public void setLoginControl(LoginControl loginControl) {
		this.loginControl = loginControl;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public void setPortalTemplateSettingManager(PortalTemplateSettingManager portalTemplateSettingManager) {
		this.portalTemplateSettingManager = portalTemplateSettingManager;
	}

	public void setPortalSkinChoiceManager(PortalSkinChoiceManager portalSkinChoiceManager) {
		this.portalSkinChoiceManager = portalSkinChoiceManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (this.cloginPage == null) {
			String loginPage = this.systemConfig.get("login_page");
			if (loginPage != null) {
				this.cloginPage = loginPage;
			} else {
				this.cloginPage = "/login.jsp";
			}
		}
		String selectedPath = null;
		PortalTemplateSetting setting = (PortalTemplateSetting) request.getAttribute("PortalLoginTemplateSetting");
		Long loginAccountId = (Long) request.getAttribute("loginAccountId");
		List<PortalHotspot> hotSpots = null;
		if (setting == null) {
			setting = this.portalTemplateSettingManager.getLoginSettingBy(OrgConstants.GROUPID.longValue(),
					OrgConstants.GROUPID.longValue());
			hotSpots = this.portalHotSpotManager.getHotSpotsBy(setting.getTemplateId().longValue(), null,
					OrgConstants.GROUPID.longValue(), OrgConstants.GROUPID.longValue());
		} else {
			hotSpots = this.portalHotSpotManager.getHotSpotsBy(setting.getTemplateId().longValue(), null,
					loginAccountId.longValue(), loginAccountId.longValue());
		}
		PortalLoginTemplate plt = (PortalLoginTemplate) DBAgent.get(PortalLoginTemplate.class, setting.getTemplateId());
		if (plt != null) {
			String path = plt.getPath();
			if ((path != null) && (path.trim().length() > 0)) {
				selectedPath = "/main/login/" + path;
			}
		}
		ModelAndView modelAndView = null;
		if (selectedPath != null) {
			modelAndView = new ModelAndView("raw:" + selectedPath);
		} else {
			modelAndView = new ModelAndView("raw:" + this.cloginPage);
		}
		Locale currentLocale = LocaleContext.make4Frontpage(request);
		List<Locale> locales = LocaleContext.getAllLocales();
		List localeCode = new ArrayList();
		Map localeCodeCfg = new HashMap();
		localeCode.add(localeCodeCfg);
		localeCodeCfg.put("eleid", "login_locale");
		localeCodeCfg.put("defaultValue", currentLocale.toString());
		Map options = new LinkedHashMap();
		localeCodeCfg.put("options", options);
		for (Locale loc : locales) {
			String locStr = loc.toString();
			options.put(locStr, ResourceUtil.getString("localeselector.locale." + locStr));
		}
		String loginTitleName = Functions.getPageTitle();
		modelAndView.addObject("templatesJsonStr", JSONUtil.toJSONString(plt));
		modelAndView.addObject("hotSpotsJsonStr", JSONUtil.toJSONString(hotSpots));
		if ((CollectionUtils.isNotEmpty(hotSpots)) && (plt.getPreset().intValue() == 1)) {
			for (PortalHotspot hotspot : hotSpots) {
				if ("note".equals(hotspot.getHotspotkey())) {
					String noteName = hotspot.getHotspotvalue();
					if ((noteName == null) || (noteName.trim().length() == 0) || ("null".equals(noteName))) {
						loginTitleName = Functions.getVersion();
						break;
					}
					loginTitleName = ResourceUtil.getString(noteName) + " " + Functions.getVersion();

					break;
				}
			}
		}
		Object activeXMap = this.loginControl.getLoginActiveXes();
		StringBuilder activeXLoader = new StringBuilder();
		if (activeXMap != null) {
			Iterator<String> ite = ((Map) activeXMap).keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				LoginActiveX loginActiveX = (LoginActiveX) ((Map) activeXMap).get(key);
				String activeX = loginActiveX.getActiveX(request, response);
				activeXLoader.append(activeX);
			}
		}
		modelAndView.addObject("currentLocale", currentLocale);
		modelAndView.addObject("locales", JSONMapper.toJSON(localeCode).render(false));

		modelAndView.addObject("loginTitleName", loginTitleName);
		modelAndView.addObject("productCategory", ProductEditionEnum.getCurrentProductEditionEnum().getName());

		setProductInfo(modelAndView);
		modelAndView.addObject("ServerState", Boolean.valueOf(ServerState.getInstance().isShutdown()));
		modelAndView.addObject("ServerStateComment", Strings.toHTML(ServerState.getInstance().getComment()));
		if (this.onlineManager == null) {
			modelAndView.addObject("OnlineNumber", "...");
		} else {
			modelAndView.addObject("OnlineNumber", Integer.valueOf(this.onlineManager.getOnlineNumber()));
		}
		modelAndView.addObject("verifyCode", Boolean.valueOf("enable".equals(this.systemConfig.get("verify_code"))));
		modelAndView.addObject("activeXLoader", activeXLoader.toString());
		if ((LDAPConfig.getInstance().getIsEnableLdap())
				&& (request.getServerName().equalsIgnoreCase(LDAPConfig.getInstance().getA8ServerDomainName())))

		{
			String adssoToken = request.getHeader("authorization");
			if (adssoToken == null) {
				modelAndView.addObject("adSSOEnable", Boolean.valueOf(true));
			} else {
				modelAndView.addObject("authorization", adssoToken);
			}
		}
		loadCAPlugIn(request, modelAndView);

		String exceptPlugin = "";
		String ucServerIpOrPort = "NULL/NULL/5222";
		if (!SystemEnvironment.hasPlugin("videoconference")) {
			exceptPlugin = exceptPlugin + "@videoconf";
		}
		if (!SystemEnvironment.hasPlugin("https")) {
			exceptPlugin = exceptPlugin + "@seeyonRootCA";
		}
		if (!SystemEnvironment.hasPlugin("identification")) {
			exceptPlugin = exceptPlugin + "@identificationDog";
		}
		if (!SystemEnvironment.hasPlugin("officeOcx")) {
			exceptPlugin = exceptPlugin + "@officeOcx";
		}
		if (!SystemEnvironment.hasPlugin("barCode")) {
			exceptPlugin = exceptPlugin + "@erweima";
		}
		if (!SystemEnvironment.hasPlugin("u8")) {
			exceptPlugin = exceptPlugin + "@U8Reg";
		}
		if (SystemEnvironment.hasPlugin("uc")) {
			exceptPlugin = exceptPlugin + "@wizard";
			String ucServerInIp = S2SConfig.queryItem("uc_server_inip");
			if (Strings.isBlank(ucServerInIp)) {
				ucServerInIp = "NULL";
			}
			String ucServerOutIp = S2SConfig.queryItem("uc_server_outip");
			if (Strings.isBlank(ucServerOutIp)) {
				ucServerOutIp = "NULL";
			}
			String ucC2sPort = S2SConfig.queryItem("uc_c2s_port");
			if (Strings.isBlank(ucC2sPort)) {
				ucC2sPort = "5222";
			}
			ucServerIpOrPort = ucServerInIp + "/" + ucServerOutIp + "/" + ucC2sPort;
		} else {
			exceptPlugin = exceptPlugin + "@zhixin";
		}
		modelAndView.addObject("ucServerIpOrPort", ucServerIpOrPort);
		modelAndView.addObject("exceptPlugin", exceptPlugin);

		boolean isCanUseSMS = false;
		if ((SystemEnvironment.hasPlugin("sms")) && (this.mobileMessageManager.isCanUseSMS())) {
			isCanUseSMS = true;
		}
		modelAndView.addObject("isCanUseSMS", Boolean.valueOf(isCanUseSMS));
		if (SecurityHelper.isCryptPassword()) {
			modelAndView.addObject("_SecuritySeed", SecurityHelper.getSessionContextSeed());
		}
		if (UpgradeUtil.upgradeIngTag) {
			HttpSession session = request.getSession(false);
			session.setAttribute("login.result", ResourceUtil.getString("login.label.ErrorCode.50"));
		} else if (!UpgradeUtil.isUpgradedV5()) {
			HttpSession session = request.getSession(false);
			session.setAttribute("login.result", ResourceUtil.getString("login.label.ErrorCode.51"));
		}
		String loginPageURL = (String) request.getAttribute("loginPageURL");
		Cookie cookie = null;
		if (loginPageURL != null) {
			cookie = new Cookie("loginPageURL", loginPageURL);
			cookie.setMaxAge(86400);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		return modelAndView;
	}

	private void loadCAPlugIn(HttpServletRequest request, ModelAndView modelAndView)
			throws UnsupportedEncodingException {
		String caFactory = SystemProperties.getInstance().getProperty("ca.factory");
		String factoryJsp = "/WEB-INF/jsp/ca/ca4" + caFactory + ".jsp";
		String sslVerifyCertValue = "no";
		String keyNum = "noKey";
		boolean hasPluginCA = SystemEnvironment.hasPlugin("ca");
		if ("koal".equals(caFactory)) {
			Cookie[] cookies = request.getCookies();
			if (cookies == null) {
				cookies = new Cookie[0];
			}
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if ("SSL_VERIFY_CERT".equals(cookie.getName())) {
					sslVerifyCertValue = new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"),
							"utf-8");
				}
				if ("KOAL_CERT_CN".equals(cookie.getName())) {
					keyNum = new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "utf-8");
				}
			}
		}
		if ("Jit".equals(caFactory)) {
			loadJitCAPlugin(request, modelAndView);
		}
		modelAndView.addObject("caFactory", caFactory);
		modelAndView.addObject("sslVerifyCertValue", sslVerifyCertValue);
		modelAndView.addObject("keyNum", keyNum);
		modelAndView.addObject("hasPluginCA", Boolean.valueOf(hasPluginCA));
		modelAndView.addObject("pageUrl", factoryJsp);
		File jspFile = new File(SystemEnvironment.getApplicationFolder() + factoryJsp);
		if ((hasPluginCA) && (!"koal".equals(caFactory)) && (jspFile.exists())) {
			modelAndView.addObject("includeJsp", Boolean.valueOf(true));
		} else {
			modelAndView.addObject("includeJsp", Boolean.valueOf(false));
		}
	}

	private void loadJitCAPlugin(HttpServletRequest request, ModelAndView modelAndView) {
		HttpSession session = request.getSession();
		String randNum = generateRandomNum();

		session.setAttribute("ToSign", randNum);

		modelAndView.addObject("original", randNum);
	}

	private String generateRandomNum() {
		String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
		int size = 6;
		char[] charArray = num.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(charArray[((int) (Math.random() * 10000.0D) % charArray.length)]);
		}
		return sb.toString();
	}

	public ModelAndView changeLocale(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Locale locale = LocaleContext.parseLocale((String) ParamUtil.getJsonParams().get("login_locale"));
		LocaleContext.setLocale(request, locale);
		if (locale.equals(LocaleContext.getAllLocales().get(0))) {
			Cookies.remove(response, "login_locale");
		} else {
			Cookies.add(response, "login_locale", locale.toString(), 31536000);
		}
		User user = AppContext.getCurrentUser();
		if (user != null) {
			user.setLocale(locale);
		}
		return index(request, response);
	}

	public ModelAndView changeTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long portalTemplateId = ParamUtil.getLong(request.getParameterMap(), "portalTemplateId");
		String showSkinchoose = request.getParameter("showSkinchoose");

		String isPortalTemplateSwitching = request.getParameter("isPortalTemplateSwitching");
		User user = AppContext.getCurrentUser();
		boolean isGroupVer = ((Boolean) SysFlag.sys_isGroupVer.getFlag()).booleanValue();
		long entityId = user.getId().longValue();
		long accountId = user.getLoginAccount().longValue();
		if ((user.isGroupAdmin()) || ((!isGroupVer) && ((user.isAdministrator()) || (user.isSystemAdmin())))) {
			entityId = OrgConstants.GROUPID.longValue();
			accountId = OrgConstants.GROUPID.longValue();
		} else if (user.isAdministrator()) {
			entityId = user.getLoginAccount().longValue();
			accountId = user.getLoginAccount().longValue();
		}
		PortalSkinChoice skinChoice = this.portalSkinChoiceManager.getPortalSkinChoiceBy(portalTemplateId.longValue(),
				entityId, accountId);
		List<PortalHotspot> hotSpots = this.portalHotSpotManager.getHotSpotsBy(portalTemplateId.longValue(),
				skinChoice.getSkinStyle(), entityId, accountId);
		PortalTemplate pt = (PortalTemplate) DBAgent.get(PortalTemplate.class, portalTemplateId);
		pt.setPortalHotspots(hotSpots);
		List<PortalTemplate> templates = new ArrayList();
		templates.add(pt);
		user.setTemplates(templates);
		this.loginControl.transChangeTemplate(portalTemplateId);
		StringBuilder url = new StringBuilder("main.do?method=main");
		if (showSkinchoose != null) {
			url.append("&showSkinchoose=true");
		}
		if (isPortalTemplateSwitching != null) {
			url.append("&isPortalTemplateSwitching=true");
		}
		response.sendRedirect(url.toString());
		return null;
	}

	public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		try {
			if (UpgradeUtil.upgradeIngTag) {
				BusinessException be = getBusinessException(new BusinessException("login.label.ErrorCode.50"));
				goout(request, session, response, be);
				return null;
			}
			if (!UpgradeUtil.isUpgradedV5()) {
				super.rendJavaScriptUnclose(response,
						"alert(\"" + ResourceUtil.getString("login.label.ErrorCode.51") + "\");");
				ModelAndView view = new ModelAndView("ctp/form/upgrade/formUpgradeIframe");
				view.addObject("viewUpgrade", Boolean.valueOf(true));
				return view;
			}
			String verifyCode = null;

			String oriToSign = null;

			String seed = null;
			if (session != null) {
				verifyCode = (String) session.getAttribute("login.VerifyCode");
				oriToSign = (String) session.getAttribute("ToSign");
				seed = (String) session.getAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY");
				try {
					session.invalidate();
				} catch (Throwable localThrowable1) {
				}
			}
			session = request.getSession(true);
			if (Strings.isNotBlank(verifyCode)) {
				session.setAttribute("login.VerifyCode", verifyCode);
			}
			if (Strings.isNotBlank(oriToSign)) {
				session.setAttribute("ToSign", oriToSign);
			}
			if (Strings.isNotBlank(seed)) {
				session.setAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY", seed);
			}
			AppContext.putThreadContext("THREAD_CONTEXT_SESSION_KEY", session);

			// 执行登陆验证
			this.loginControl.transDoLogin(request, session, response);
			
			String power = request.getParameter("power");
			if (power != null) {
				session.setAttribute("power", power);
			}
			User user = AppContext.getCurrentUser();
			String username = user.getLoginName();
			String password = user.getPassword();
			String userAgentFrom = user.getUserAgentFrom();
			Locale locale = user.getLocale();
			String fontSize = request.getParameter("fontSize");
			
			
			if (Strings.isNotBlank(fontSize)) {
				session.setAttribute("fontSize", fontSize);
			}
			session.setAttribute("ssoFrom", Strings.escapeNULL(request.getParameter("ssoFrom"), "PC"));

			String destination = getDestination(request, session);

			writeCookie(request, response, session, username, password, userAgentFrom, locale);
			//从session中获取企业微信账号 如果登陆成功将其保存到用户信息中
			String qyuserid=request.getParameter("qywxuserid");
			
			if(qyuserid==null){
				response.sendRedirect(destination);
			}else{
				response.sendRedirect("/seeyon/hrStaff.do?method=updateQYWX&qywxuserid="+qyuserid);
			} 
			
		} catch (Throwable e) {
			BusinessException be = getBusinessException(e);
			// 当登陆失败时将报异常.所以在这里抓跳转.
			// 判断是否存在企业微信登陆session标志,如果有跳转到绑定页面
			String isqywxbind = (String) session.getAttribute("qyweixinbind");
			if (isqywxbind != null) {
				// 这用完了之后把微信登录的session标记移除
				session.removeAttribute("qyweixinbind");
				//跳转到主页并在主页提示登陆.
				response.sendRedirect("/seeyon/main.do");
			} else {
				goout(request, session, response, be);
			}
			return null;
		}
		return null;
	}

	private String getDestination(HttpServletRequest request, HttpSession session) {
		String destination = request.getParameter("login.destination");
		if (destination != null) {
			session.setAttribute("login.destination", destination);
		} else {
			session.removeAttribute("login.destination");
		}
		if ((destination == null) || (destination.equals(request.getContextPath()))) {
			String contextPath = request.getContextPath();
			if ("/".equals(contextPath)) {
				contextPath = "";
			}
			destination = contextPath + appDefaultPath;
		}
		return destination;
	}

	private BusinessException getBusinessException(Throwable e) {
		if (e == null) {
			return null;
		}
		if ((e instanceof BusinessException)) {
			return (BusinessException) e;
		}
		return getBusinessException(e.getCause());
	}

	public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			return index(request, response);
		}
		User user = AppContext.getCurrentUser();
		if (user != null) {
			if (this.cframePage == null) {
				String framePage = this.systemConfig.get("frame_page");
				if (framePage != null) {
					this.cframePage = framePage;
				} else {
					this.cframePage = "/frame.jsp";
				}
			}
			String uframe = user.getMainFrame();
			ModelAndView modelAndView;
			if (uframe != null) {
				modelAndView = new ModelAndView("raw:" + uframe);
			} else {
				modelAndView = new ModelAndView("raw:" + this.cframePage);
			}
			Object[] pwdExpirationInfo = (Object[]) V3xShareMap.get("PwdExpirationInfo-" + user.getLoginName());
			Object loginAuthentication = V3xShareMap.get("LoginAuthentication-" + user.getLoginName());
			modelAndView.addObject("pwdExpirationInfo", pwdExpirationInfo);
			modelAndView.addObject("loginAuthentication", loginAuthentication);
			int onlineNum = this.onlineManager.getOnlineNumber();
			modelAndView.addObject("onlineNumber", Integer.valueOf(onlineNum));

			boolean isCanSendSMS = false;
			if ((SystemEnvironment.hasPlugin("sms")) && (this.mobileMessageManager.isCanSend(user.getId().longValue(),
					user.getLoginAccount().longValue()))) {
				isCanSendSMS = true;
			}
			String power = (String) session.getAttribute("power");
			modelAndView.addObject("isCanSendSMS", Boolean.valueOf(isCanSendSMS));
			modelAndView.addObject("pwd_NeedUpdate", Integer.valueOf(PwdStrengthValidationUtil.getPwdNeedUpdate()));
			modelAndView.addObject("PwdStrengthValidationValue",
					Integer.valueOf(PwdStrengthValidationUtil.getPwdStrengthValidationValue()));
			modelAndView.addObject("power", power);

			boolean isEnableMsgSound = false;
			String enableMsgSoundConfig = this.systemConfig.get("SMS_hint");
			if ((enableMsgSoundConfig != null) && ("enable".equals(enableMsgSoundConfig))) {
				isEnableMsgSound = "true".equals(user.getCustomize("messageSoundEnabled"));
			}
			modelAndView.addObject("isEnableMsgSound", Boolean.valueOf(isEnableMsgSound));

			modelAndView.addObject("msgClosedEnable",
					Boolean.valueOf(!"false".equals(user.getCustomize("messageViewRemoved"))));

			String currentSpaceForNC = request.getParameter("currentSpaceForNC");
			modelAndView.addObject("currentSpaceForNC", currentSpaceForNC);

			String pageTitle = this.portalManager.getPageTitle();
			modelAndView.addObject("pageTitle", pageTitle);

			String groupSecondName = this.portalManager.getGroupSecondName();
			modelAndView.addObject("groupSecondName", Strings.escapeJavascript(groupSecondName));

			String accountSecondName = this.portalManager.getAccountSecondName();
			modelAndView.addObject("accountSecondName", Strings.escapeJavascript(accountSecondName));

			String skinPathKey = getSkinPathKey(user);
			modelAndView.addObject("skinPathKey", skinPathKey);

			user.setSkin(skinPathKey);

			String personModifyPwd = SystemProperties.getInstance().getProperty("person.disable.modify.password");
			modelAndView.addObject("personModifyPwd", Boolean.valueOf("1".equals(personModifyPwd)));

			SystemConfig systemConfig = (SystemConfig) AppContext.getBean("systemConfig");
			String ci = systemConfig.get("card_enable");
			boolean cardEnabled = (ci != null) && ("enable".equals(ci));
			modelAndView.addObject("cardEnabled", Boolean.valueOf(cardEnabled));

			String mainMenuId = request.getParameter("mainMenuId");
			if (Strings.isNotBlank(mainMenuId)) {
				modelAndView.addObject("mainMenuId", mainMenuId);
			}
			String clickMenuId = request.getParameter("clickMenuId");
			if (Strings.isNotBlank(clickMenuId)) {
				modelAndView.addObject("clickMenuId", clickMenuId);
			}
			String mainSpaceId = request.getParameter("mainSpaceId");
			if (Strings.isNotBlank(mainSpaceId)) {
				modelAndView.addObject("mainSpaceId", mainSpaceId);
			}
			String shortCutId = request.getParameter("shortCutId");
			if (Strings.isNotBlank(shortCutId)) {
				modelAndView.addObject("shortCutId", shortCutId);
			}
			String isRefresh = request.getParameter("isRefresh");
			if (Strings.isNotBlank(isRefresh)) {
				modelAndView.addObject("isRefresh", isRefresh);
			}
			String showSkinchoose = request.getParameter("showSkinchoose");
			if (Strings.isNotBlank(showSkinchoose)) {
				modelAndView.addObject("showSkinchoose", showSkinchoose);
			}
			String isPortalTemplateSwitching = request.getParameter("isPortalTemplateSwitching");
			if (Strings.isNotBlank(isPortalTemplateSwitching)) {
				modelAndView.addObject("isPortalTemplateSwitching", isPortalTemplateSwitching);
			}
			String portal_default_page = request.getParameter("portal_default_page");
			if (Strings.isNotBlank(portal_default_page)) {
				modelAndView.addObject("portal_default_page", portal_default_page);
			}
			String openFrom = (String) session.getAttribute("ssoFrom");
			if (Strings.isNotBlank(openFrom)) {
				modelAndView.addObject("openFrom", openFrom);
			} else {
				modelAndView.addObject("openFrom", "");
			}
			String fontSize = (String) session.getAttribute("fontSize");
			if ((Strings.isNotBlank(fontSize)) && (!"12".equals(fontSize))) {
				user.setFontSize(fontSize);
			} else {
				user.setFontSize("");
			}
			Map<String, HomePageParamsInterface> paramsCreaters = AppContext
					.getBeansOfType(HomePageParamsInterface.class);
			Map<String, Object> params;
			if ((paramsCreaters != null) && (paramsCreaters.size() > 0)) {
				for (Map.Entry<String, HomePageParamsInterface> entry : paramsCreaters.entrySet()) {
					HomePageParamsInterface paramsCreater = (HomePageParamsInterface) entry.getValue();
					params = paramsCreater.getParamsForHomePage();
					if ((params != null) && (params.size() > 0)) {
						modelAndView.addAllObjects(params);
					}
				}
			}
			Map<String, ExpandJspForHomePage> expansionJspCreaters = AppContext
					.getBeansOfType(ExpandJspForHomePage.class);
			if ((expansionJspCreaters != null) && (expansionJspCreaters.size() > 0)) {
				Object expansionJspForHomepage = new ArrayList();
				for (Map.Entry<String, ExpandJspForHomePage> entry : expansionJspCreaters.entrySet()) {
					ExpandJspForHomePage expansionJspCreater = (ExpandJspForHomePage) entry.getValue();
					List<String> expansionJsp = expansionJspCreater.expandJspForHomePage(null);
					if ((expansionJsp != null) && (expansionJsp.size() > 0)) {
						((List) expansionJspForHomepage).addAll(expansionJsp);
					}
				}
				if (CollectionUtils.isNotEmpty((Collection) expansionJspForHomepage)) {
					modelAndView.addObject("ExpansionJsp", expansionJspForHomepage);
				}
			}
			Cookie cookie = new Cookie("avatarImageUrl", String.valueOf(AppContext.currentUserId()));
			cookie.setMaxAge(315360000);
			cookie.setPath("/");
			response.addCookie(cookie);
			return modelAndView;
		}
		BusinessException e = new BusinessException("loginUserState.unknown");
		e.setCode("-1");
		goout(request, session, response, e);
		return null;
	}

	private String getSkinPathKey(User user) {
		if (user != null) {
			List<PortalTemplate> templates = user.getTemplates();
			if (CollectionUtils.isNotEmpty(templates)) {
				PortalTemplate temp = (PortalTemplate) templates.get(0);
				List<PortalHotspot> hotspots = temp.getPortalHotspots();
				if (CollectionUtils.isNotEmpty(hotspots)) {
					return ((PortalHotspot) hotspots.get(0)).getExt10();
				}
				return "harmony";
			}
		}
		return "harmony";
	}

	private String getErrorDestination(HttpServletRequest request, HttpSession session) {
		String error_destination = request.getParameter("com.seeyon.login.error_destination");
		if (session != null) {
			if (error_destination != null) {
				session.setAttribute("com.seeyon.login.error_destination", error_destination);
			} else {
				session.removeAttribute("com.seeyon.login.error_destination");
			}
		}
		if (error_destination == null) {
			error_destination = request.getContextPath();
			if ((error_destination == null) || ("".equals(error_destination))) {
				error_destination = "/main.do";
			} else {
				error_destination = error_destination + "/main.do";
			}
		}
		return error_destination;
	}

	private void redirectToIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(response.encodeURL(getErrorDestination(request, request.getSession(false))));
	}

	private void goout(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			BusinessException be) {
		String error_destination = getErrorDestination(request, session);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			Cookie[] arrayOfCookie1;
			int j = (arrayOfCookie1 = cookies).length;
			for (int i = 0; i < j; i++) {
				Cookie cookie = arrayOfCookie1[i];
				if (("loginPageURL".equals(cookie.getName())) && (cookie.getValue().length() > 0)) {
					error_destination = request.getContextPath() + cookie.getValue();
					cookie.setMaxAge(0);
					cookie.setValue(null);
					response.addCookie(cookie);
				}
			}
		}
		if (be != null) {
			Enumeration<String> attsEnu = session.getAttributeNames();
			while (attsEnu.hasMoreElements()) {
				session.removeAttribute((String) attsEnu.nextElement());
			}
			session.setAttribute("login.result", be.getMessage());
			response.addHeader("LoginError", be.getCode());
		}
		try {
			response.sendRedirect(response.encodeURL(error_destination));
		} catch (Exception localException) {
		}
	}

	private static void writeCookie(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			String username, String password, String userAgentFrom, Locale locale) {
		if (Constants.login_useragent_from.mobile.name().equals(userAgentFrom)) {
			boolean rememberName = request.getParameterValues("login.rememberName") != null;

			boolean rememberPassword = false;
			if (rememberName) {
				rememberPassword = true;
			}
			if (Boolean.TRUE.equals(Boolean.valueOf(rememberName))) {
				Cookies.add(response, "login_username", username, 31536000, true);
				Cookies.add(response, "login.rememberName", "true", 31536000);
				session.removeAttribute("login.rememberName");
			} else {
				Cookies.remove(response, "login_username");
				Cookies.remove(response, "login.rememberName");
			}
			if (Boolean.TRUE.equals(Boolean.valueOf(rememberPassword))) {
				Cookies.add(response, "login_password", password, 31536000, true);
				Cookies.add(response, "login.rememberPassword", "true", 31536000);
				session.removeAttribute("login.rememberPassword");
			} else {
				Cookies.remove(response, "login_password");
				Cookies.remove(response, "login.rememberPassword");
			}
			Cookies.add(response, "u_login_from", userAgentFrom, 31536000, false);
			Cookies.add(response, "u_login_name", username, 86400, true);
			Cookies.add(response, "u_login_password", password, 86400, true);
		}
		if (locale != null) {
			if (locale.equals(LocaleContext.getAllLocales().get(0))) {
				Cookies.remove(response, "login_locale");
			} else {
				Cookies.add(response, "login_locale", locale.toString(), 31536000);
			}
		}
	}

	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setDateHeader("Expires", -1L);
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragrma", "no-cache");

		HttpSession session = request.getSession(false);

		String verifyCode = null;

		String oriToSign = null;

		String seed = null;
		if (session != null) {
			verifyCode = (String) session.getAttribute("login.VerifyCode");
			oriToSign = (String) session.getAttribute("ToSign");
			seed = (String) session.getAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY");
		}
		String destination = this.loginControl.transDoLogout(request, session, response);
		session = request.getSession(true);
		if (Strings.isNotBlank(verifyCode)) {
			session.setAttribute("login.VerifyCode", verifyCode);
		}
		if (Strings.isNotBlank(oriToSign)) {
			session.setAttribute("ToSign", oriToSign);
		}
		if (Strings.isNotBlank(seed)) {
			session.setAttribute("SESSION_CONTEXT_SECURITY_SEED_KEY", seed);
		}
		AppContext.putThreadContext("THREAD_CONTEXT_SESSION_KEY", session);
		if ("close".equals(destination)) {
			response.setContentType("text/html; charset=UTF-8");

			PrintWriter out = response.getWriter();
			out.println("<script>top.window.close();</script>");
			out.close();
		} else {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				Cookie[] arrayOfCookie1;
				int j = (arrayOfCookie1 = cookies).length;
				for (int i = 0; i < j; i++) {
					Cookie cookie = arrayOfCookie1[i];
					if (("loginPageURL".equals(cookie.getName())) && (cookie.getValue().length() > 0)) {
						destination = cookie.getValue();
						cookie.setMaxAge(0);
						cookie.setValue(null);
						response.addCookie(cookie);
					}
				}
			}
			response.sendRedirect(SystemEnvironment.getContextPath() + destination);
		}
		return null;
	}

	public ModelAndView changeLoginAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String loginAccount = request.getParameter("login.accountId");
		if (Strings.isNotBlank(loginAccount)) {
			this.loginControl.transChangeLoginAccount(Long.parseLong(loginAccount));
		}
		String isRefresh = request.getParameter("isRefresh");
		String showSkinchoose = request.getParameter("showSkinchoose");
		String isPortalTemplateSwitching = request.getParameter("isPortalTemplateSwitching");
		String portal_default_page = request.getParameter("portal_default_page");
		String param = "";
		if (Strings.isNotBlank(isRefresh)) {
			param = param + "&isRefresh=" + isRefresh;
		}
		if (Strings.isNotBlank(showSkinchoose)) {
			param = param + "&showSkinchoose=true";
		}
		if (Strings.isNotBlank(isPortalTemplateSwitching)) {
			param = param + "&isPortalTemplateSwitching=true";
		}
		if (Strings.isNotBlank(portal_default_page)) {
			param = param + "&portal_default_page=default";
		}
		response.sendRedirect("main.do?method=main" + param);

		return null;
	}

	public ModelAndView showAbout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("common/about");

		modelAndView.addObject("productVersion", Functions.getVersion());
		modelAndView.addObject("buildId",
				"B" + Datetimes.format(SystemEnvironment.getProductBuildDate(), "yyMMdd") + "."
						+ SystemEnvironment.getProductBuildVersion() + ".CTP"
						+ SystemEnvironment.getCtpProductBuildVersion());
		String edition = ProductEditionEnum.getCurrentProductEditionEnum().getName();
		modelAndView.addObject("productCategory", edition);
		setProductInfo(modelAndView);

		return modelAndView;
	}

	public ModelAndView headerjs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = AppContext.getCurrentUser();
		long time = user != null ? user.getLoginTimestamp().getTime() : 0L;
		String etag = "e" + SystemEnvironment.getProductBuildVersion() + time;
		if (WebUtil.checkEtag(request, response, etag)) {
			return null;
		}
		response.setStatus(200);
		ModelAndView modelAndView = new ModelAndView("common/header_js");

		WebUtil.writeETag(request, response, etag);
		return modelAndView;
	}

	public ModelAndView hangup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User currentUser = AppContext.getCurrentUser();
		if (currentUser != null) {
			this.onlineManager.updateOnlineState(currentUser);
		}
		return null;
	}

	private void setProductInfo(ModelAndView modelAndView) {
		Object o = MclclzUtil.invoke(c1, "getInstance", new Class[] { String.class }, null, new Object[] { "" });

		Integer serverType = (Integer) MclclzUtil.invoke(c1, "getserverType", null, o, null);

		Integer m1ServerType = (Integer) MclclzUtil.invoke(c1, "getm1Type", null, o, null);

		modelAndView.addObject("serverType", serverType);
		modelAndView.addObject("m1ServerType", m1ServerType);
		modelAndView.addObject("maxOnline", MclclzUtil.invoke(c1, "getTotalservernum", null, o, null));
		modelAndView.addObject("maxOnline", MclclzUtil.invoke(c1, "getTotalservernum", null, o, null));
		modelAndView.addObject("m1MaxOnline", MclclzUtil.invoke(c1, "getTotalm1num", null, o, null));
	}

	private static final Class<?> c1 = MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");
}
