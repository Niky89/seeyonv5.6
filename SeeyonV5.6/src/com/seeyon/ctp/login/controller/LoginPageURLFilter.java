package com.seeyon.ctp.login.controller;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.po.PortalTemplateSetting;
import com.seeyon.ctp.portal.portaltemplate.manager.PortalTemplateSettingManager;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoginPageURLFilter implements Filter {
	private static final long serialVersionUID = -7995851457610927846L;
	private static final Log log = LogFactory.getLog(LoginPageURLFilter.class);
	private OrgManager orgManager;
	private PortalTemplateSettingManager portalTemplateSettingManager;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestURL = httpRequest.getRequestURI();
		AppContext.initSystemEnvironmentContext(httpRequest, httpResponse, false);
		if (this.orgManager == null) {
			this.orgManager = ((OrgManager) AppContext.getBean("orgManager"));
		}
		if (this.portalTemplateSettingManager == null) {
			this.portalTemplateSettingManager = ((PortalTemplateSettingManager) AppContext
					.getBean("portalTemplateSettingManager"));
		}
		String contextPath = httpRequest.getContextPath();
		int part1Index = requestURL.indexOf(contextPath);
		String domainName = requestURL.substring(part1Index + contextPath.length() + 1, requestURL.length());
		int domainNameLength = domainName.length();
		if (("".equals(domainName)) || ("main.do".equals(domainName.toLowerCase()))
				|| ((contextPath + "/index.jsp").equals(requestURL))) {
			String referer = httpRequest.getHeader("Referer");
			if ((referer == null) || (referer.trim().length() == 0)) {
				httpRequest.setAttribute("loginPageURL", "/main.do");
			} else {
				httpRequest.setAttribute("loginPageURL", null);
			}
		}
		int lastIndex = domainNameLength - 1;
		int charPosition1 = domainName.lastIndexOf("/");
		int charPosition2 = domainName.lastIndexOf(".");
		String domainNameLowerCase = domainName.toLowerCase();
		if ((domainNameLength == 0) || (domainNameLowerCase.contains("servlet"))
				|| (domainNameLowerCase.contains("reportserver")) || (charPosition2 != -1)
				|| ((charPosition1 != -1) && (charPosition1 != lastIndex))) {
			chain.doFilter(request, response);
			return;
		}
		if ((domainNameLength > 0) && (charPosition1 == lastIndex)) {
			domainName = new StringBuilder(domainName).deleteCharAt(lastIndex).toString();
		}
		Long accountId = null;
		String loginPageURL = null;
		try {
			accountId = this.orgManager.getAccountIdByCustomLoginUrl(domainName);
			if (accountId == null) {
				chain.doFilter(request, response);
				return;
			}
			V3xOrgAccount account = this.orgManager.getAccountById(accountId);
			if (account.isCustomLogin()) {
				loginPageURL = "/" + domainName;
			} else {
				loginPageURL = "/main.do";
				accountId = OrgConstants.GROUPID;
			}
			httpRequest.setAttribute("loginPageURL", loginPageURL);
			httpRequest.setAttribute("loginAccountId", accountId);
		} catch (BusinessException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		PortalTemplateSetting setting = this.portalTemplateSettingManager.getLoginSettingBy(accountId.longValue(),
				accountId.longValue());
		httpRequest.setAttribute("PortalLoginTemplateSetting", setting);
		RequestDispatcher dispatcher = httpRequest.getRequestDispatcher("/main.do");
		dispatcher.forward(httpRequest, httpResponse);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
