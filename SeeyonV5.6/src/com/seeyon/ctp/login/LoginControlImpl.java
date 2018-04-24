package com.seeyon.ctp.login;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.Constants;
import com.seeyon.ctp.common.constants.LoginResult;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.common.po.customize.CtpCustomize;
import com.seeyon.ctp.login.logonlog.manager.LogonLogManager;
import com.seeyon.ctp.login.online.OnlineManager;
import com.seeyon.ctp.login.po.LogonLog;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.portal.customize.manager.CustomizeManager;
import com.seeyon.ctp.portal.hotspot.manager.PortalHotSpotManager;
import com.seeyon.ctp.portal.manager.PortalCacheManager;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalSkinChoiceManager;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalTemplateManager;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalTemplateSettingManager;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.privilege.dao.PrivilegeCache;
import com.seeyon.ctp.privilege.manager.PrivilegeManage;
import com.seeyon.ctp.privilege.manager.ResourceManager;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;

public final class LoginControlImpl implements LoginControl {
	private static final Logger LOGGER = Logger.getLogger(LoginControlImpl.class);
	private Map<String, LoginAuthentication> loginAuthentications;
	private Map<String, LoginInterceptor> loginIntercepters;
	private Map<String, LoginActiveX> loginActiveXes;
	private PrincipalManager principalManager;
	private OrgManager orgManager;
	private ResourceManager resourceManager;
	private PrivilegeManage privilegeManager;
	private PrivilegeCache privilegeCache;
	private OnlineManager onLineManager;
	private SpaceManager spaceManager;
	private PortalHotSpotManager portalHotSpotManager;
	private PortalTemplateManager portalTemplateManager;
	private PortalTemplateSettingManager portalTemplateSettingManager;
	private PortalSkinChoiceManager portalSkinChoiceManager;
	private PortalCacheManager portalCacheManager;
	private MobileMessageManager mobileMessageManager;
	private CustomizeManager customizeManager;
	private LogonLogManager logonLogManager = null;

	public Logger getLogger() {
		return LOGGER;
	}

	public Map<String, LoginAuthentication> getLoginAuthentications() {
		return this.loginAuthentications;
	}

	public Map<String, LoginInterceptor> getLoginIntercepters() {
		return this.loginIntercepters;
	}

	public PrincipalManager getPrincipalManager() {
		return this.principalManager;
	}

	public OrgManager getOrgManager() {
		return this.orgManager;
	}

	public ResourceManager getResourceManager() {
		return this.resourceManager;
	}

	public PrivilegeManage getPrivilegeManager() {
		return this.privilegeManager;
	}

	public PrivilegeCache getPrivilegeCache() {
		return this.privilegeCache;
	}

	public OnlineManager getOnlineManager() {
		return this.onLineManager;
	}

	public SpaceManager getSpaceManager() {
		return this.spaceManager;
	}

	public PortalHotSpotManager getPortalHotSpotManager() {
		return this.portalHotSpotManager;
	}

	public void setLogonLogManager(LogonLogManager logonLogManager) {
		this.logonLogManager = logonLogManager;
	}

	public void setPortalHotSpotManager(PortalHotSpotManager portalHotSpotManager) {
		this.portalHotSpotManager = portalHotSpotManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setPrivilegeManager(PrivilegeManage privilegeManager) {
		this.privilegeManager = privilegeManager;
	}

	public void setPrivilegeCache(PrivilegeCache privilegeCache) {
		this.privilegeCache = privilegeCache;
	}

	public void setOnlineManager(OnlineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	public PortalTemplateManager getPortalTemplateManager() {
		return this.portalTemplateManager;
	}

	public void setPortalTemplateManager(PortalTemplateManager portalTemplateManager) {
		this.portalTemplateManager = portalTemplateManager;
	}

	public MobileMessageManager getMobileMessageManager() {
		return this.mobileMessageManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

	public PortalTemplateSettingManager getPortalTemplateSettingManager() {
		return this.portalTemplateSettingManager;
	}

	public void setPortalTemplateSettingManager(PortalTemplateSettingManager portalTemplateSettingManager) {
		this.portalTemplateSettingManager = portalTemplateSettingManager;
	}

	public PortalSkinChoiceManager getPortalSkinChoiceManager() {
		return this.portalSkinChoiceManager;
	}

	public void setPortalSkinChoiceManager(PortalSkinChoiceManager portalSkinChoiceManager) {
		this.portalSkinChoiceManager = portalSkinChoiceManager;
	}

	public PortalCacheManager getPortalCacheManager() {
		return this.portalCacheManager;
	}

	public void setPortalCacheManager(PortalCacheManager portalCacheManager) {
		this.portalCacheManager = portalCacheManager;
	}

	public void init() {
		LoginExtensionBeanLoader loader = LoginExtensionBeanLoader.getInstance();
		try {
			loader.init(AppContext.getCfgHome().getAbsolutePath());
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		this.loginAuthentications = SystemProperties.getBeansOfTypeAndSysConfig(LoginAuthentication.class,
				SystemProperties.CONFIG_LOGIN_AUTHENTICATION);
		this.loginAuthentications = loader.getLoginAuthentications(this.loginAuthentications,
				LoginAuthentication.class);
		this.loginIntercepters = SystemProperties.getBeansOfTypeAndSysConfig(LoginInterceptor.class,
				SystemProperties.CONFIG_LOGIN_INTERCEPTER);
		this.loginIntercepters = loader.getLoginAuthentications(this.loginIntercepters, LoginInterceptor.class);
		this.loginActiveXes = SystemProperties.getBeansOfTypeAndSysConfig(LoginActiveX.class,
				SystemProperties.CONFIG_LOGIN_ACTIVEX);
		this.loginActiveXes = loader.getLoginAuthentications(this.loginActiveXes, LoginActiveX.class);
		if (LOGGER.isInfoEnabled()) {
			Iterator<String> ite = this.loginAuthentications.keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				LOGGER.info("加载LoginAuthentication[" + key + "]");
			}
			ite = this.loginIntercepters.keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				LOGGER.info("加载LoginIntercepter[" + key + "]");
			}
			ite = this.loginActiveXes.keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				LOGGER.info("加载loginActiveX[" + key + "]");
			}
		}
	}

	public LoginResult transDoLogin(HttpServletRequest request, HttpSession session, HttpServletResponse response)
			throws BusinessException {
		/*
		 * return (LoginResult)MclclzUtil.invoke(c1, "transDoLogin", new Class[]
		 * { HttpServletRequest.class, HttpSession.class,
		 * HttpServletResponse.class, LoginControlImpl.class }, null, new
		 * Object[] { request, session, response, this });
		 */
		return LoginHelper.transDoLogin(request, session, response, this);
	}

	/**
	 * 新增微信企业号登陆功能.
	 * 
	 * @param loginResult
	 * @author sxl
	 */
	public LoginResult transDoLoginByWeixin(HttpServletRequest request, HttpSession session,
			HttpServletResponse response) throws BusinessException {

		return LoginHelper.transDoLogin(request, session, response, this);
	}

	public void loginResultCheck(LoginResult loginResult) throws BusinessException {
		if (!loginResult.isOK()) {
			BusinessException e = new BusinessException("loginUserState.loginError" + loginResult.getStatus());
			e.setCode(String.valueOf(loginResult.getStatus()));
			throw e;
		}
	}

	public void transChangeTemplate(Long templateId) {
		User user = AppContext.getCurrentUser();
		if (user != null) {
			user.changeTemplate(templateId);
		}
	}

	public Map<String, String> getCustomizeInfo(Long userId) {
		Map<String, String> customizeMap = new HashMap();

		List<CtpCustomize> customizeList = this.customizeManager.getCustomizeInfo(userId);
		for (CtpCustomize cc : customizeList) {
			customizeMap.put(cc.getCkey(), cc.getCvalue());
		}
		return customizeMap;
	}

	public String getSessionId(HttpServletRequest request) {
		Cookie[] cookie = request.getCookies();
		if (cookie == null) {
			return null;
		}
		for (int i = 0; i < cookie.length; i++) {
			if ("JSESSIONID".equalsIgnoreCase(cookie[i].getName().trim())) {
				return cookie[i].getValue().trim();
			}
		}
		return null;
	}

	public void getTopFrame(User user, HttpServletRequest request) {
		String topFrame = request.getParameter("com.seeyon.sso.topframename");
		if (topFrame != null) {
			user.setUserSSOFrom(Constants.user_sso_from.nc_portal.name());
		}
	}

	public void createLog(User currentUser) {
		if (currentUser == null) {
			return;
		}
		if (this.logonLogManager != null) {
			int loginType = currentUser.getUserAgentFromEnum().ordinal();
			LogonLog log = this.logonLogManager.save(currentUser.getAccountId().longValue(),
					currentUser.getDepartmentId().longValue(), currentUser.getId().longValue(), loginType,
					currentUser.getRemoteAddr(), currentUser.getLoginTimestamp());

			currentUser.setLoginLogId(log.getId());
		}
	}

	public void logoutLog(User currentUser) {
		if (currentUser == null) {
			return;
		}
		if ((this.logonLogManager != null) && (currentUser.getLoginLogId() != null)) {
			this.logonLogManager.updateOnlineTime(currentUser.getLoginLogId().longValue(), 0,
					currentUser.getLoginTimestamp());
		}
	}

	public void transChangeLoginAccount(long newLoginAccountId) throws BusinessException {
		AppContext.getCurrentUser().setEtagRandom(null);
		/*
		 * MclclzUtil.invoke(c1, "transChangeLoginAccount", new Class[] {
		 * Long.class, LoginControlImpl.class }, null, new Object[] {
		 * Long.valueOf(newLoginAccountId), this });
		 */
		LoginHelper.transChangeLoginAccount(newLoginAccountId, this);
	}

	public String transDoLogout(HttpServletRequest request, HttpSession session, HttpServletResponse response)
			throws BusinessException {
		/*
		 * return (String)MclclzUtil.invoke(c1, "transDoLogout", new Class[] {
		 * HttpServletRequest.class, HttpSession.class,
		 * HttpServletResponse.class, LoginControlImpl.class }, null, new
		 * Object[] { request, session, response, this });
		 */
		return LoginHelper.transDoLogout(request, session, response, this);
	}

	public Map<String, LoginActiveX> getLoginActiveXes() {
		return this.loginActiveXes;
	}

	public CustomizeManager getCustomizeManager() {
		return this.customizeManager;
	}

	public void setCustomizeManager(CustomizeManager customizeManager) {
		this.customizeManager = customizeManager;
	}
}
