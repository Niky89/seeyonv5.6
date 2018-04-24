package com.seeyon.ctp.portal.space.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.constants.LayoutConstants;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.portal.exception.SpaceException;
import com.seeyon.ctp.portal.po.PortalPagePortlet;
import com.seeyon.ctp.portal.space.decorations.PortalDecoration;
import com.seeyon.ctp.portal.space.decorations.PortalDecorationManager;
import com.seeyon.ctp.portal.space.manager.SpaceManager;
import com.seeyon.ctp.portal.util.PortalConstants;
import com.seeyon.ctp.util.Strings;

public class PortalServlet extends HttpServlet {
	private static final long serialVersionUID = -7078002568107431228L;
	private static final Log log;
	private static final Log logc;
	private transient SpaceManager spaceManager;
	public static final String CONTENT_TYPE = "text/html; charset=UTF-8";

	static {
		log = LogFactory.getLog((Class) PortalServlet.class);
		logc = LogFactory.getLog("capability");
	}

	public void init() throws ServletException {
		super.init();
	}

	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		final long startTime = System.currentTimeMillis();
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		AppContext.initSystemEnvironmentContext(request, response, false);
		final User user = AppContext.getCurrentUser();
		if (user == null) {
			try {
				final HttpSession session = request.getSession(false);
				if (session == null) {
					PortalServlet.log.error(
							(Object) ("\u7a7a\u95f4\u52a0\u8f7d\uff0csession\u83b7\u53d6\u5931\u8d25\uff1a" + session));
				} else {
					final Enumeration attrName = session.getAttributeNames();
					PortalServlet.log
							.error((Object) ("\u7a7a\u95f4\u52a0\u8f7d\uff0c\u4eba\u5458\u4fe1\u606f\u83b7\u53d6\u5931\u8d25\uff1asessionId:"
									+ session.getId()));
					while (attrName.hasMoreElements()) {
						final Object ele = attrName.nextElement(); 
						PortalServlet.log
								.error((Object) ("\u7a7a\u95f4\u52a0\u8f7d\uff0c\u4eba\u5458\u4fe1\u606f\u83b7\u53d6\u5931\u8d25\uff1a"
										+ ele.toString() + "||" + session.getAttribute(ele.toString())));
					}
				}
			} catch (Exception e) {
				PortalServlet.log
						.error((Object) ("\u7a7a\u95f4\u52a0\u8f7d\uff0c\u4eba\u5458\u4fe1\u606f\u83b7\u53d6\u5931\u8d25\uff1a"
								+ e.getMessage()));
			}
			final PrintWriter out = response.getWriter();
			out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + request.getContextPath()
					+ "/common/js/V3X.js\"></script>");
			out.println("<script>");
			out.println("alert(\"" + ResourceUtil.getString("loginUserState.unknown") + "\");");
			out.println("getCtpTop().location.href =\"" + request.getContextPath() + "/main.do?method=logout\";\n");
			out.println("</script>");
			out.close();
			AppContext.clearThreadContext();
			return;
		}
		final String path = request.getRequestURI();
		String showState = request.getParameter("showState");
		final String sprint = request.getParameter("sprint");
		request.setAttribute("sprint", (Object) sprint);
		if (showState == null) {
			if (user.isAdministrator() || user.isGroupAdmin()) {
				showState = PortalConstants.EditModel.view.name();
			} else {
				showState = PortalConstants.EditModel.show.name();
			}
		}
		if (this.spaceManager == null) {
			this.spaceManager = (SpaceManager) AppContext.getBean("spaceManager");
		}
		request.setAttribute("showState", (Object) showState);
		final String editKeyId = request.getParameter("editKeyId");
		final Long userId = user.getId();
		final Long longinAccountId = user.getLoginAccount();
		final String isChangedIndex = request.getParameter("isChangedIndex");
		if (Strings.isNotBlank(isChangedIndex)) {
			request.setAttribute("isChangedIndex", (Object) isChangedIndex);
		}
		final String toDefault = request.getParameter("toDefault");
		if (Strings.isNotBlank(toDefault)) {
			request.setAttribute("toDefault", (Object) toDefault);
		}
		try {
			System.out.println("path:"+path+"##editkeyid"+editKeyId+"##");
			final boolean result = PortalConstants.initPortalData(request, response, path, this.spaceManager, editKeyId,
					userId, longinAccountId);
			if (!result) {
				return;
			}
			Map<String, Map<String, PortalPagePortlet>> fragments = (Map<String, Map<String, PortalPagePortlet>>) request
					.getAttribute("fragments");
			final PortalDecoration decoration = (PortalDecoration) request.getAttribute("decoration");
			final RequestDispatcher dispatcher = request.getRequestDispatcher(decoration.getViewPath());
			List<String> tlist = PortalDecorationManager.getAllLayoutType();
			request.setAttribute("allLayout", (Object) PortalDecorationManager.getAllLayoutType());
			request.setAttribute("layoutTypes", (Object) LayoutConstants.lagoutToDecorations);
			request.setAttribute("CurrentUser", (Object) user);
			dispatcher.forward((ServletRequest) request, (ServletResponse) response);
		} catch (SpaceException e2) {
			PortalServlet.log.error((Object) ("\u9996\u9875\uff0c\u53d6\u5f97\u7a7a\u95f4\u5e03\u5c40\uff1a" + path),
					(Throwable) e2);
			AppContext.clearThreadContext();
			try {
				if (PortalServlet.logc.isDebugEnabled()) {
					if (user != null) {
						PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ","
								+ user.getLoginName() + ",PortalServlet," + (System.currentTimeMillis() - startTime)));
					} else {
						PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ",PortalServlet,"
								+ (System.currentTimeMillis() - startTime)));
					}
				}
			} catch (Exception ex) {
			}
			return;
		} finally {
			AppContext.clearThreadContext();
			try {
				if (PortalServlet.logc.isDebugEnabled()) {
					if (user != null) {
						PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ","
								+ user.getLoginName() + ",PortalServlet," + (System.currentTimeMillis() - startTime)));
					} else {
						PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ",PortalServlet,"
								+ (System.currentTimeMillis() - startTime)));
					}
				}
			} catch (Exception ex2) {
			}
		}
		AppContext.clearThreadContext();
		try {
			if (PortalServlet.logc.isDebugEnabled()) {
				if (user != null) {
					PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ","
							+ user.getLoginName() + ",PortalServlet," + (System.currentTimeMillis() - startTime)));
				} else {
					PortalServlet.logc.debug((Object) (String.valueOf(request.getRemoteAddr()) + ",PortalServlet,"
							+ (System.currentTimeMillis() - startTime)));
				}
			}
		} catch (Exception ex3) {
		}
	}

	protected void doPost(final HttpServletRequest arg0, final HttpServletResponse arg1)
			throws ServletException, IOException {
		this.doGet(arg0, arg1);
	}
}
