package com.seeyon.ctp.portal.sso;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.TrustAddressManager;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager;
import com.seeyon.ctp.common.authenticate.sso.SSOTicketManager.TicketInfo;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.organization.principal.PrincipalManager;
import com.seeyon.ctp.signin.manager.SignInManager;
import com.seeyon.ctp.signin.po.SignIn;
import com.seeyon.ctp.util.Strings;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

public class SSOLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 8250719400904761530L;
	private static final Log log = LogFactory.getLog(SSOLoginServlet.class);
	private transient Object object = new Object();
	private transient PrincipalManager principalManager = null;
	private SignInManager ssoManager = null;

	public void setSignInManager(SignInManager signInManager) {
		this.ssoManager = signInManager;
	}

	public SignInManager getSignInManager() {
		return this.ssoManager;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (this.principalManager == null) {
			this.principalManager = ((PrincipalManager) AppContext.getBean("principalManager"));
		}
		if (this.ssoManager == null) {
			this.ssoManager = ((SignInManager) AppContext.getBean("signInManager"));
		}
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		PrintWriter out = resp.getWriter();
		SSOLoginContext ssoLoginContext = null;

		String from = req.getParameter("from");
		String signin = req.getParameter("signin");
		if ((!isBlank(signin)) && (isBlank(from))) {
			ssoLoginContext = signinTo(signin);
		} else if ((isBlank(signin)) && (!isBlank(from))) {
			ssoLoginContext = SSOLoginContextManager.getInstance().getSSOLoginContext(from);
		} else if ((isBlank(signin)) && (isBlank(from))) {
			out.println("Parameter 'from' is not available.");
			resp.addHeader("SSOError", "Parameter 'from' is not available.");
			return;
		}
		if (ssoLoginContext == null) {
			out.println(Strings.toHTML(from) + " is not available.");
			resp.addHeader("SSOError", "from is not available.");
			return;
		}
		if ((ssoLoginContext.isEnableTrustAddress()) && (!TrustAddressManager.getInstance().isTrustPass(req))) {
			out.println(Strings.toHTML(from) + " client ip is untrusted.");
			resp.addHeader("SSOError", "from client ip is untrusted.");
			return;
		}
		String ticketName = ssoLoginContext.getTicketName();

		String ticket = req.getParameter(ticketName);
		if (isBlank(ticket)) {
			out.println("Parameter '" + Strings.escapeJavascript(ticketName) + "' is not available.");
			resp.addHeader("SSOError", "Parameter '" + Strings.escapeJavascript(ticketName) + "' is not available.");
			return;
		}
		SSOLoginHandshakeInterface handshake = ssoLoginContext.getHandshake();
		if (handshake == null) {
			out.println("SSOLoginHandshakeInterface is not implements.");
			resp.addHeader("SSOError", "Parameter 'SSOLoginHandshakeInterface is not implements.");
			return;
		}
		String username = null;
		try {
			username = handshake.handshake(ticket);
		} catch (Throwable e) {
			log.error(ssoLoginContext, e);
			out.println("Login fail");
			resp.addHeader("SSOError", Strings.escapeJavascript(e.getMessage()));
			return;
		}
		if (!isBlank(username)) {
			username = URLDecoder.decode(username, "UTF-8");
			if (!ssoLoginContext.isForward()) {
				V3xOrgMember member = null;
				SSOTicketManager.TicketInfo ticketInfo1 = null;
				SSOTicketManager.TicketInfo ticketInfo2 = null;
				try {
					member = SSOTicketManager.getInstance().getOrgManager().getMemberByLoginName(username);
				} catch (BusinessException e1) {
					e1.printStackTrace();
				}
				if ((member != null) && (member.isValid())) {
					ticketInfo1 = SSOTicketManager.getInstance().getTicketInfo(ticket);
					ticketInfo2 = SSOTicketManager.getInstance().getTicketInfoByUsername(username);
				}
				if ((ticketInfo1 != null) && (ticketInfo2 != null)) {
					if (ticketInfo1.equals(ticketInfo2)) {
						out.println("TicketInfo '" + Strings.escapeJavascript(ticket) + ", "
								+ Strings.escapeJavascript(username) + "' already SSO.");
						resp.addHeader("SSOOK", "TicketInfo '" + Strings.escapeJavascript(ticket) + ", "
								+ Strings.escapeJavascript(username) + "' already SSO.");
					} else {
						out.println(
								"Ticket or Username already exists, but with the current information does not match.");
						resp.addHeader("SSOError",
								"Ticket or Username already exists, but with the current information does not match.");
					}
					return;
				}
				try {
					if (member.isValid()) {
						SSOTicketManager.getInstance().newTicketInfo(ticket, username, from);
						out.println("SSOOK");
						resp.addHeader("SSOOK", "");
					} else {
						out.println("Username '" + Strings.escapeJavascript(username) + "' is not available.");
						resp.addHeader("SSOError",
								"Username '" + Strings.escapeJavascript(username) + "' is not available.");
					}
				} catch (Exception e) {
					log.error("", e);
					out.println(Strings.escapeJavascript(e.getMessage()));
					resp.addHeader("SSOError", "Username is not available." + Strings.escapeJavascript(e.getMessage()));
				}
			} else {
				try {
					String tourl = req.getParameter("tourl");
					SSOTicketManager.getInstance().newTicketInfo(ticket, username, from);
					StringBuilder sb = null;
					synchronized (this.object) {
						sb = get2URL(req, ssoLoginContext, ticket, tourl);
					}
					out.println("报错了");
					System.out.println(sb.toString());
					resp.sendRedirect(sb.toString());
				} catch (Throwable e) {
					out.println(e.getMessage());
				}
			}
		} else {
			out.println("Username is not available.");
			resp.addHeader("SSOError", "Username is not available.");
		}
	}

	private SSOLoginContext signinTo(String param) {
		SignIn ssoInfo = this.ssoManager.findSsoByName(param);

		SSOLoginHandshakeInterface handshake = null;
		try {
			handshake = (SSOLoginHandshakeInterface) Class
					.forName(ssoInfo.getCheckUrl() != null ? ssoInfo.getCheckUrl() : null).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		SSOLoginContext signin = new SSOLoginContext();
		signin.setHandshake(handshake);
		signin.setForward(ssoInfo.getSort().intValue() != 0);
		signin.setTicketName(ssoInfo.getLoginParam() != null ? ssoInfo.getLoginParam() : "ticket");

		return signin;
	}

	private StringBuilder get2URL(HttpServletRequest req, SSOLoginContext ssoLoginContext, String ticket,
			String tourl) {
		StringBuilder sb = new StringBuilder();
		if (!isBlank(tourl)) {
			sb.append(tourl + "?ticket=" + URLEncoder.encode(ticket));
			String formUrl = req.getParameter("formUrl");
			if (!isBlank(formUrl)) {
				int end = formUrl.indexOf("login/sso");
				if (end != -1) {
					formUrl = formUrl.substring(0, end);
				}
				sb.append("&formUrl=" + URLEncoder.encode(formUrl));
			}
			sb = urlMosaic(req, ssoLoginContext, tourl, sb);

			return sb;
		}
		sb.append("/seeyon/main.do?method=login&ticket=" + URLEncoder.encode(ticket) + "&ssoFrom="
				+ req.getParameter("from"));

		sb = urlMosaic(req, ssoLoginContext, tourl, sb);
		return sb;
	}

	private StringBuilder urlMosaic(HttpServletRequest req, SSOLoginContext ssoLoginContext, String tourl,
			StringBuilder sb) {
		String UserLanguage = req.getParameter("login_locale");
		if (UserLanguage != null) {
			sb.append("&login_locale=" + UserLanguage);
		}
		String UserAgentFrom = req.getParameter("UserAgentFrom");
		if (UserAgentFrom != null) {
			sb.append("&UserAgentFrom=" + UserAgentFrom);
		}
		String des = tourl != null ? tourl : ssoLoginContext.getDestination();
		if (StringUtils.hasText(des)) {
			sb.append("&");
			sb.append("login.destination");
			sb.append("=");
			sb.append(URLEncoder.encode(des));

			String top = ssoLoginContext.getTopFrameName();
			if (StringUtils.hasText(top)) {
				sb.append("&");
				sb.append("com.seeyon.sso.topframename");
				sb.append("=");
				sb.append(top);
			}
		}
		return sb;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private boolean isBlank(String s) {
		return (s == null) || (s.length() == 0);
	}
}
