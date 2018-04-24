package com.seeyon.ctp.login;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager;
import com.seeyon.ctp.common.constants.Constants;
import com.seeyon.ctp.common.constants.LoginResult;
import com.seeyon.ctp.common.constants.SystemProperties;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.BrowserEnum;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.i18n.LocaleContext;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.thirdparty.ThirdpartyTicketManager;
import com.seeyon.ctp.login.bo.MenuBO;
import com.seeyon.ctp.login.online.OnlineRecorder;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.permission.bo.LicensePerInfo;
import com.seeyon.ctp.portal.space.bo.MenuTreeNode;
import com.seeyon.ctp.portal.sso.SSOLoginContext;
import com.seeyon.ctp.portal.sso.SSOLoginContextManager;
import com.seeyon.ctp.portal.sso.SSOLoginHandshakeInterface;
import com.seeyon.ctp.privilege.po.PrivResource;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;

public final class LoginHelper {
	private static final Log loginLog = LogFactory.getLog("login");
	private static final String ncportalURL = SystemProperties.getInstance().getProperty("nc.portal.url");
	private static final String ncURL = SystemProperties.getInstance().getProperty("nc.server.url.prefix");
	private static Object isExceedMaxLoginNumberLock = new Object();
	private static LicensePerInfo o = null;
	private static int serverType;
	private static int m1Type;

	private static void init() {
		if (o != null) {
			return;
		}
		o = LicensePerInfo.getInstance("");// MclclzUtil.invoke(c1,
											// "getInstance", new Class[] {
											// String.class }, null, new
											// Object[] { "" });

		serverType = o.getserverType().intValue();// ((Integer)MclclzUtil.invoke(c1,
													// "getserverType", null, o,
													// null)).intValue();
		m1Type = o.getm1Type();// ((Integer)MclclzUtil.invoke(c1, "getm1Type",
								// null, o, null)).intValue();
	}

	public static LoginResult transDoLogin(HttpServletRequest request, HttpSession session,
			HttpServletResponse response, LoginControlImpl loginControl) throws BusinessException {
		long startTime = System.currentTimeMillis();

		init();

		String userAgentFrom = request.getParameter("UserAgentFrom");
		if ((userAgentFrom == null) || ("".equals(userAgentFrom))) {
			userAgentFrom = Constants.login_useragent_from.pc.name();
		}
		BrowserEnum browser = BrowserEnum.valueOf(request);
		if (browser == null) {
			browser = BrowserEnum.IE;
		}
		String username = null;
		String password = null;

		boolean success = false;

		Locale locale = LocaleContext.make4Frontpage(request);
		AppContext.putSessionContext(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);

		Iterator<String> ite = loginControl.getLoginIntercepters().keySet().iterator();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			LoginInterceptor loginInterceptor = (LoginInterceptor) loginControl.getLoginIntercepters().get(key);
			LoginResult loginResult = loginInterceptor.preHandle(request, response);
			loginResultCheck(loginResult);
		}
		ite = loginControl.getLoginAuthentications().keySet().iterator();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			LoginAuthentication loginAuthentication = (LoginAuthentication) loginControl.getLoginAuthentications()
					.get(key);

			request.setAttribute("__LoginAuthenticationClassSimpleName",
					loginAuthentication.getClass().getSimpleName());
			try {

				String[] _success = loginAuthentication.authenticate(request, response);
				if (_success != null) {
					success = true;
					username = _success[0];
					password = _success[1];
				}
			} catch (LoginAuthenticationException e) {
				afterFailure(request, response, loginControl);
				loginResultCheck(e.getLoginReslut());
			} catch (Exception e) {
				loginControl.getLogger().error("", e);
				loginResultCheck(LoginResult.ERROR_UNKNOWN_USER);
			}
		}
		if (success) {
			if (!loginControl.getPrincipalManager().isExist(username)) {
				loginResultCheck(LoginResult.ERROR_UNKNOWN_USER);
			}
			TimeZone timeZone = TimeZone.getDefault();
			try {
				String tempTimeZone = request.getParameter("login.timezone");
				if (Strings.isNotBlank(tempTimeZone)) {
					timeZone = TimeZone.getTimeZone(tempTimeZone);
				}
			} catch (Exception e) {
				loginControl.getLogger().warn("Get Info of TimeZone from Request error : " + e.getLocalizedMessage());
			}
			User user = new User();
			session.setAttribute("com.seeyon.current_user", user);
			AppContext.putThreadContext("SESSION_CONTEXT_USERINFO_KEY", user);
			user.setSecurityKey(UUIDLong.longUUID());
			user.setLoginName(username);
			user.setPassword(password);
			user.setUserAgentFrom(userAgentFrom);
			user.setBrowser(browser);
			user.setLocale(locale);
			user.setTimeZone(timeZone);
			if (request.getParameter("clientPath") == null) {
				user.setRemoteAddr(Strings.getRemoteAddr(request));
			} else {
				user.setRemoteAddr(request.getParameter("clientPath"));
			}
			user.setUserSSOFrom(request.getParameter("com.seeyon.sso.topframename"));
			String loginAccount = request.getParameter("login.accountId");
			if (Strings.isNotBlank(loginAccount)) {
				user.setLoginAccount(new Long(loginAccount));
			}
			String sessionId = loginControl.getSessionId(request);
			sessionId = sessionId == null ? session.getId() : sessionId;
			user.setSessionId(sessionId);

			LoginResult code0 = mergeUserInfo(user, loginControl);

			loginResultCheck(code0);

			ite = loginControl.getLoginIntercepters().keySet().iterator();
			while (ite.hasNext()) {
				String key = (String) ite.next();
				LoginInterceptor loginInterceptor = (LoginInterceptor) loginControl.getLoginIntercepters().get(key);
				LoginResult loginResult = loginInterceptor.afterComplete(request, response);
				loginResultCheck(loginResult);
			}
			String m1PermissionType = null;
			String serverPermissionType = null;
			if (user.isFromM1()) {
				m1PermissionType = LicensePerInfo.getM1PermissionType();// (String)MclclzUtil.invoke(c1,
																		// "getM1PermissionType",
																		// null,
																		// o,
																		// null);
			} else {
				serverPermissionType = LicensePerInfo.getServerPermissionType();// (String)MclclzUtil.invoke(c1,
																				// "getServerPermissionType",
																				// null,
																				// o,
																				// null);
			}
			Long loginAccountId = user.getLoginAccount();
			synchronized (isExceedMaxLoginNumberLock) {
				if (user.isFromM1()) {
					if (m1Type == 2) {
						boolean isExceedMaxLoginNumberM1 = OnlineRecorder.isExceedMaxLoginNumberM1();
						if (isExceedMaxLoginNumberM1) {
							loginResultCheck(new LoginResult(4006, new String[0]));
						}
						if (("2".equals(m1PermissionType))
								&& (OnlineRecorder.isExceedMaxLoginNumberM1InAccount(loginAccountId))) {
							loginResultCheck(new LoginResult(4007, new String[0]));
						}
					}
				} else if (serverType == 2) {
					if (!user.isAdmin()) {
						boolean isExceedMaxLoginNumber = OnlineRecorder.isExceedMaxLoginNumberServer();
						if (isExceedMaxLoginNumber) {
							loginResultCheck(LoginResult.ERROR_EXCEED_MAXNUMBER);
						}
						if (("2".equals(serverPermissionType))
								&& (OnlineRecorder.isExceedMaxLoginNumberServerInAccount(loginAccountId))) {
							loginResultCheck(LoginResult.ERROR_EXCEED_MAXNUMBER_IN_ACCOUNT);
						}
					}
				}
				addToOnlineUserList(loginControl);
			}
			user.setLoginState(User.login_state_enum.ok);

			loginControl.getTopFrame(user, request);

			loginControl.createLog(user);

			response.addHeader("LoginOK", "ok");

			String screen = "";
			if ((Strings.isNotBlank(request.getParameter("screenWidth")))
					&& (Strings.isNotBlank(request.getParameter("screenHeight")))) {
				screen = request.getParameter("screenWidth") + "*" + request.getParameter("screenHeight");
			}
			loginLog.info("Login," + user.getLoginName() + "," + user.getUserAgentFrom() + "," + user.getRemoteAddr()
					+ "," + user.getSessionId() + "," + (System.currentTimeMillis() - startTime) + ","
					+ BrowserEnum.valueOf1(request) + "," + screen);

			return LoginResult.OK;
		}
		LoginResult r = afterFailure(request, response, loginControl);
		loginResultCheck(r);
		return r;
	}

	private static LoginResult afterFailure(HttpServletRequest request, HttpServletResponse response,
			LoginControlImpl loginControl) {
		LoginResult r = LoginResult.ERROR_UNKNOWN_USER;
		Iterator<String> ite = loginControl.getLoginIntercepters().keySet().iterator();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			LoginInterceptor loginInterceptor = (LoginInterceptor) loginControl.getLoginIntercepters().get(key);
			LoginResult tmp = loginInterceptor.afterFailure(request, response);
			if ((tmp != null) && (!tmp.isOK())) {
				r = tmp;
			}
		}
		return r;
	}

	private static void loginResultCheck(LoginResult loginResult) throws BusinessException {
		if (!loginResult.isOK()) {
			BusinessException e = new BusinessException("login.label.ErrorCode." + loginResult.getStatus(),
					loginResult.getParameters());
			e.setCode(String.valueOf(loginResult.getStatus()));
			throw e;
		}
	}

	private static LoginResult mergeUserInfo(User currentUser, LoginControlImpl loginControl) {
		if (currentUser == null) {
			return LoginResult.ERROR_UNKNOWN_USER;
		}
		try {
			String loginName = currentUser.getLoginName();
			V3xOrgMember member = loginControl.getOrgManager().getMemberByLoginName(loginName);
			if ((member == null) || (!member.isValid())) {
				return LoginResult.ERROR_UNKNOWN_USER;
			}
			long userId = member.getId().longValue();

			V3xOrgAccount account = loginControl.getOrgManager().getAccountById(member.getOrgAccountId());
			V3xOrgAccount loginAccount;
			if (currentUser.getLoginAccount() != null) {
				loginAccount = loginControl.getOrgManager().getAccountById(currentUser.getLoginAccount());
			} else {
				loginAccount = account;
			}
			if ((account == null) || (loginAccount == null) || (!account.isValid()) || (!loginAccount.isValid())) {
				return LoginResult.ERROR_UNKNOWN_USER;
			}
			currentUser.setId(Long.valueOf(userId));
			currentUser.setAccountId(account.getId());
			currentUser.setLoginAccount(loginAccount.getId());
			currentUser.setLoginAccountName(loginAccount.getName());
			currentUser.setLoginAccountShortName(loginAccount.getShortName());

			String name = null;
			if (member.getIsAdmin().booleanValue()) {
				if (loginControl.getOrgManager().isAuditAdminById(Long.valueOf(userId)).booleanValue()) {
					currentUser.setAuditAdmin(true);
					name = ResourceUtil.getString("org.auditAdminName.value");
				} else if (loginControl.getOrgManager().isGroupAdminById(Long.valueOf(userId)).booleanValue()) {
					currentUser.setGroupAdmin(true);
					name = ResourceUtil.getString(
							"org.account_form.groupAdminName.value" + (String) SysFlag.EditionSuffix.getFlag());
				} else if (loginControl.getOrgManager().isAdministratorById(Long.valueOf(userId), loginAccount)
						.booleanValue()) {
					currentUser.setAdministrator(true);
					name = loginAccount.getName() + ResourceUtil.getString("org.account_form.adminName.value");
				} else if (loginControl.getOrgManager().isSystemAdminById(Long.valueOf(userId)).booleanValue()) {
					currentUser.setSystemAdmin(true);
					name = ResourceUtil.getString("org.account_form.systemAdminName.value");
				} else if (loginControl.getOrgManager().isSuperAdmin(loginName, loginAccount).booleanValue()) {
					currentUser.setSuperAdmin(true);
					name = ResourceUtil.getString("org.account_form.superAdminName.value");
				} else if (loginControl.getOrgManager().isPlatformAdminById(Long.valueOf(userId)).booleanValue()) {
					currentUser.setPlatformAdmin(true);
					name = ResourceUtil.getString("org.account_form.platformAdminName.value");
				}
			} else {
				name = member.getName();
			}
			currentUser.setName(name);
			currentUser.setDepartmentId(member.getOrgDepartmentId());
			currentUser.setLevelId(member.getOrgLevelId());
			currentUser.setPostId(member.getOrgPostId());
			currentUser.setInternal(member.getIsInternal().booleanValue());
			currentUser.setCustomizes(loginControl.getCustomizeInfo(Long.valueOf(userId)));

			List<V3xOrgAccount> concurrentAccount = loginControl.getOrgManager()
					.concurrentAccounts4ChangeAccount(Long.valueOf(userId));
			currentUser.setConcurrentAccount(concurrentAccount);

			currentUser.setMainFrame(currentUser.getCustomize("frame_page"));
			List<MenuBO> menus = loginControl.getSpaceManager().getCustomizeMenusOfMember(currentUser, null);
			List<PrivResource> resources = loginControl.getResourceManager().getByMember(Long.valueOf(userId),
					loginAccount.getId());
			currentUser.setResources(resources);
			currentUser.setMenus(menus);

			List<MenuTreeNode> shortcuts = loginControl.getPortalCacheManager().getShortcuts(currentUser);
			currentUser.setShortcuts(shortcuts);

			boolean canSendSMS = false;
			if ((SystemEnvironment.hasPlugin("sms"))
					&& (loginControl.getMobileMessageManager().isCanSend(userId, loginAccount.getId().longValue()))) {
				canSendSMS = true;
			}
			currentUser.setCanSendSMS(canSendSMS);

			List<String[]> spaces = loginControl.getPortalCacheManager().getSpaces(currentUser);
			currentUser.setSpaces(spaces);

			loginControl.getPortalCacheManager().getPortalTemplate(currentUser);
		} catch (Exception e) {
			loginControl.getLogger().error("", e);
			return LoginResult.ERROR_UNKNOWN_USER;
		}
		return LoginResult.OK;
	}

	private static void addToOnlineUserList(LoginControlImpl loginControl) {
		User user = AppContext.getCurrentUser();

		OnlineRecorder.moveToOffline(user.getLoginName(), Constants.LoginOfflineOperation.loginAnotherone);

		loginControl.getOnlineManager().updateOnlineState(user);
	}

	public static void transChangeLoginAccount(Long newLoginAccountId, LoginControlImpl loginControl)
			throws BusinessException {
		User currentUser = AppContext.getCurrentUser();

		Long oldAccountId = currentUser.getLoginAccount();
		if (newLoginAccountId == oldAccountId) {
			return;
		}
		currentUser.setLoginAccount(newLoginAccountId);

		mergeUserInfo(currentUser, loginControl);

		LicensePerInfo o = LicensePerInfo.getInstance("");// MclclzUtil.invoke(c1,
															// "getInstance",
															// new Class[] {
															// String.class },
															// null, new
															// Object[] { "" });
		synchronized (isExceedMaxLoginNumberLock) {
			int serverType = o.getserverType();// ((Integer)MclclzUtil.invoke(c1,
												// "getserverType", null, o,
												// null)).intValue();
			if (serverType == 2) {
				if (currentUser.isFromM1()) {
					String m1PermissionType = LicensePerInfo.getM1PermissionType();// (String)MclclzUtil.invoke(c1,
																					// "getM1PermissionType",
																					// null,
																					// o,
																					// null);
					if (("2".equals(m1PermissionType))
							&& (OnlineRecorder.isExceedMaxLoginNumberM1InAccount(newLoginAccountId))) {
						loginResultCheck(new LoginResult(4007, new String[0]));
					}
				} else {
					String serverPermissionType = LicensePerInfo.getServerPermissionType();// (String)MclclzUtil.invoke(c1,
																							// "getServerPermissionType",
																							// null,
																							// o,
																							// null);
					if ((!currentUser.isAdmin()) && ("2".equals(serverPermissionType))) {
						if (OnlineRecorder.isExceedMaxLoginNumberServerInAccount(newLoginAccountId)) {
							loginResultCheck(LoginResult.ERROR_EXCEED_MAXNUMBER_IN_ACCOUNT);
						}
					}
				}
			}
			loginControl.getOnlineManager().swithAccount(oldAccountId, currentUser);
		}
	}

	public static String transDoLogout(HttpServletRequest request, HttpSession session, HttpServletResponse response,
			LoginControlImpl loginControl) throws BusinessException {
		String destination = null;
		if ("m".equalsIgnoreCase(request.getParameter("f"))) {
			destination = "/m/";
		} else if ("toPortal".equalsIgnoreCase(request.getParameter("toPortal"))) {
			destination = ncportalURL + "/portal/logout.jsp";
			if (Strings.isBlank(ncportalURL)) {
				destination = ncURL + "/portal/logout.jsp";
			}
		} else if (session != null) {
			destination = (String) session.getAttribute("com.seeyon.login.error_destination");
		}
		String caFactory = null;
		try {
			User user = null;
			if (session != null) {
				user = (User) session.getAttribute("com.seeyon.current_user");
			}
			if ((user != null) && (request.getParameter("Offline") == null)) {
				synchronized (isExceedMaxLoginNumberLock) {
					OnlineRecorder.logoutUser(user);
				}
				SSOTicketManager.TicketInfo ticketInfo = SSOTicketManager.getInstance()
						.getTicketInfoByUsername(user.getLoginName());
				if (ticketInfo != null) {
					SSOLoginContext ssoLoginContext = SSOLoginContextManager.getInstance()
							.getSSOLoginContext(ticketInfo.getFrom());
					if (ssoLoginContext != null) {
						SSOLoginHandshakeInterface handshake = ssoLoginContext.getHandshake();
						if (handshake != null) {
							handshake.logoutNotify(ticketInfo.getTicket());
						}
					}
					SSOTicketManager.getInstance().removeTicketInfo(ticketInfo.getUsername());
				}
				ThirdpartyTicketManager.getInstance().removeTicketInfosByUsername(user.getLoginName());

				loginControl.logoutLog(user);

				loginLog.info("Logout:" + user.getLoginName() + "," + user.getUserAgentFrom() + ","
						+ user.getRemoteAddr() + "," + user.getSessionId());
				try {
					if (session != null) {
						session.invalidate();
					}
				} catch (Throwable e) {
					loginControl.getLogger().error("", e);
				}
			}
			caFactory = SystemProperties.getInstance().getProperty("ca.factory");
		} catch (Throwable e) {
			loginControl.getLogger().error("", e);
		}

		if (("true".equalsIgnoreCase(request.getParameter("close"))) || ("koal".equals(caFactory))) {
			return "close";
		}
		if (Strings.isBlank(destination)) {
			destination = "/main.do";
		}
		return destination;
	}

	// private static final Class<?> c1 =
	// MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");
}
