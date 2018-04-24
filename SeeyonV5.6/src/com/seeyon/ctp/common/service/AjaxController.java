package com.seeyon.ctp.common.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.aspect.AspectAnnotationAware;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.web.util.WebUtil;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.ZipUtil;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;
import com.seeyon.ctp.util.json.JsonErrorObject;
import com.seeyon.ctp.util.json.mapper.JSONMapper;
import com.seeyon.ctp.util.json.mapper.MapperException;
import com.seeyon.ctp.util.json.model.JSONArray;
import com.seeyon.ctp.util.json.model.JSONValue;
import com.seeyon.ctp.util.json.parser.JSONParser;

public class AjaxController extends MultiActionController {
	private static final Log logAjax = LogFactory.getLog("ajax");
	private static final Logger LOGGER = Logger.getLogger(AjaxController.class);
	private static final Set<String> EXCLUDE_METHODS = new HashSet();
	private static final String LEGAL_REGEX = "^[a-z0-9A-Z]+$";
	private AjaxAccessInterceptor ajaxAccessInterceptor;
	private final Map<String, String> managerJsCache = new ConcurrentHashMap();
	public static final String responseEncoding = "UTF-8";
	private OrgManager orgManager;

	static {
		EXCLUDE_METHODS.add("addAdvice");
		EXCLUDE_METHODS.add("addAdvisor");
		EXCLUDE_METHODS.add("equals");
		EXCLUDE_METHODS.add("getAdvisors");
		EXCLUDE_METHODS.add("getProxiedInterfaces");
		EXCLUDE_METHODS.add("getTargetSource");
		EXCLUDE_METHODS.add("getTargetClass");
		EXCLUDE_METHODS.add("hashCode");
		EXCLUDE_METHODS.add("indexOf");
		EXCLUDE_METHODS.add("isExposeProxy");
		EXCLUDE_METHODS.add("isFrozen");
		EXCLUDE_METHODS.add("isInterfaceProxied");
		EXCLUDE_METHODS.add("isProxyTargetClass");
		EXCLUDE_METHODS.add("isPreFiltered");
		EXCLUDE_METHODS.add("removeAdvice");
		EXCLUDE_METHODS.add("removeAdvisor");
		EXCLUDE_METHODS.add("replaceAdvisor");

		EXCLUDE_METHODS.add("setPreFiltered");
		EXCLUDE_METHODS.add("setExposeProxy");
		EXCLUDE_METHODS.add("setTargetSource");
		EXCLUDE_METHODS.add("toProxyConfigString");
		EXCLUDE_METHODS.add("toString");
	}

	@NeedlessCheckLogin
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String ua = request.getHeader("user-agent").toLowerCase();
	    if (ua.indexOf("micromessenger") <= 0)
	    {
	      User u = AppContext.getCurrentUser();
	      if (u == null)
	      {
	        OutputStream out = response.getOutputStream();
	        byte[] bout = "".getBytes("UTF-8");
	        response.setIntHeader("Content-Length", bout.length);
	        out.write(bout);
	        out.flush();
	        out.close();
	        return null;
	      }
	    }
		
		response.setContentType("text/plain; charset=UTF-8");
		String etag = "e" + SystemEnvironment.getServerStartTime() + request.getParameter("managerName").hashCode();
		if (WebUtil.checkEtag(request, response, etag)) {
			return null;
		}
		response.setStatus(200);

		WebUtil.writeETag(request, response, etag);

		String outStr = generateServiceJavascript(request, response);
		if (outStr != null) {
			OutputStream out = response.getOutputStream();
			byte[] bout = outStr.getBytes("UTF-8");
			response.setIntHeader("Content-Length", bout.length);
			out.write(bout);
			out.flush();
			out.close();
		}
		return null;
	}

	@NeedlessCheckLogin
	public ModelAndView ajaxAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		response.setContentType("application/json; charset=UTF-8");
		String responseCompress = request.getParameter("responseCompress");

		response.setStatus(200);
		request.getRequestURI();
		String outStr = "";

		outStr = invokeService(request, response);
		if (Strings.isNotBlank(request.getParameter("ClientRequestPath"))) {
			outStr = ZipUtil.compressResponse(outStr, responseCompress, "UTF-8", LOGGER);
		}
		Writer out = response.getWriter();
		
		out.write(outStr);
		return null;
	}

	private String generateServiceJavascript(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String serviceNameStr = request.getParameter("managerName");
		if ((serviceNameStr == null) || (serviceNameStr.trim().length() == 0)) {
			return "";
		}
		if (serviceNameStr.toLowerCase().endsWith("dao")) {
			this.logger.error("不允许AJAX访问Dao：" + serviceNameStr);
			notfound(response);
			return "您所访问的AJAX非法";
		}
		String script = (String) this.managerJsCache.get(serviceNameStr);
		if (script != null) {
			return script;
		}
		String gateway = request.getServletPath();
		String contextPath = request.getContextPath();
		script = generateSomeAjaxStub(contextPath, gateway, serviceNameStr);
		this.managerJsCache.put(serviceNameStr, script);
		return script;
	}

	public static String generateSomeAjaxStub(String contextPath, String gateway, String serviceNameStr) {
		StringBuilder strbuf = new StringBuilder();
		strbuf.append(getAjaxStubHeader(contextPath, gateway));
		String[] serviceNames = serviceNameStr.split(",");
		String currentServiceName = null;
		if ((serviceNames != null) && (serviceNames.length > 0)) {
			for (int j = 0; j < serviceNames.length; j++) {
				currentServiceName = serviceNames[j];
				strbuf.append(generateAjaxStub(currentServiceName));
				strbuf.append("\n");
			}
		}
		return strbuf.toString();
	}

	public static String getAjaxStubHeader(String contextPath, String gateway) {
		return "var ajaxUrl = \"" + contextPath + gateway + "?method=ajaxAction&managerName=\";";
	}

	public static String generateAjaxStub(String currentServiceName) {
		StringBuilder strbuf = new StringBuilder();
		if (!currentServiceName.matches("^[a-z0-9A-Z]+$")) {
			return "";
		}
		strbuf.append("var ").append(currentServiceName).append("=RJS.extend({\n");
		strbuf.append("jsonGateway:ajaxUrl+\"").append(currentServiceName).append("\"");
		try {
			Object service = getService(currentServiceName);
			Method[] methods = service.getClass().getMethods();
			Set methodNameSet = new TreeSet();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if ((method.getDeclaringClass().equals(service.getClass()))
						&& (!EXCLUDE_METHODS.contains(method.getName()))) {
					methodNameSet.add(method.getName());
				}
			}
			String[] methodNames = new String[methodNameSet.size()];
			methodNameSet.toArray(methodNames);
			strbuf.append(",");
			if (methodNames.length == 0) {
				return "";
			}
			for (int i = 0; i < methodNames.length; i++) {
				String methodName = methodNames[i];
				strbuf.append("").append(methodName).append(":function(){");

				strbuf.append("return this.c(arguments,\"" + methodName + "\");");
				strbuf.append("}");
				if (i != methodNames.length - 1) {
					strbuf.append(",");
				}
			}
		} catch (Exception localException) {
			LOGGER.debug("Service not found: " + currentServiceName + ", 相关插件未启用或其它未知原因！");
		}
		strbuf.append("});");
		return strbuf.toString();
	}

	private static Object getService(String serviceName) throws Exception {
		Object service = AppContext.getBean(serviceName);
		if (((service instanceof DataSource)) || ((service instanceof Session))
				|| ((service instanceof SessionFactory))) {
			service = null;
		}
		if (service == null) {
			Exception e = new Exception("can not find the service: " + serviceName);
			throw e;
		}
		return service;
	}

	private String invokeService(HttpServletRequest request, HttpServletResponse response) {
		AppContext.initSystemEnvironmentContext(request, response);
		String serviceName = request.getParameter("managerName");
		String methodName = request.getParameter("managerMethod");
		String strArgs = request.getParameter("arguments");
		String compressType = request.getParameter("requestCompress");
		strArgs = ZipUtil.uncompressRequest(strArgs, compressType, "UTF-8", LOGGER);
		if ((serviceName == null) || (serviceName.trim().length() == 0) || (!serviceName.matches("^[a-z0-9A-Z]+$"))) {
			return "";
		}
		if ((methodName == null) || (methodName.trim().length() == 0) || (!methodName.matches("^[a-z0-9A-Z]+$"))) {
			return "";
		}
		if (serviceName.toLowerCase().endsWith("dao")) {
			this.logger.error("不允许AJAX访问Dao：" + serviceName);
			notfound(response);
			return "您所访问的AJAX非法";
		}
		if ((this.ajaxAccessInterceptor != null) && (!this.ajaxAccessInterceptor.accept(serviceName, methodName))) {
			logAjax.error(serviceName + "," + methodName + "," + AppContext.currentUserLoginName() + ",非法的Ajax请求");
			notfound(response);
			return "非法的Ajax请求";
		}
		String ctpJSONPCallback = request.getParameter("ctpJSONPCallback");
		String retValue = null;

		JSONValue jsonValue = null;
		try {
			String ua = request.getHeader("user-agent").toLowerCase();
		    if (ua.indexOf("micromessenger") <= 0)
		    {
		    	User.validateResource(serviceName + "." + methodName, false);
		    }
			
			Object service = getService(serviceName);
			Object result = invokeMethod(service, methodName, strArgs, serviceName);
			if ((result instanceof FlipInfo)) {
				FlipInfo fpi = (FlipInfo) result;
				result = new HashMap();
				Map rmap = (Map) result;
				rmap.put("total", Integer.valueOf(fpi.getTotal()));
				rmap.put("data", fpi.getData());
				rmap.put("page", Integer.valueOf(fpi.getPage()));
				rmap.put("pages", fpi.getPages());
				rmap.put("total", Integer.valueOf(fpi.getTotal()));
				rmap.put("size", Integer.valueOf(fpi.getSize()));
			}
			if (ctpJSONPCallback == null) {
				jsonValue = JSONMapper.toJSON(result);
				retValue = jsonValue.render(false);
			} else {
				retValue = (String) result;
			}
		} catch (Throwable e) {
			Throwable e1 = e;
			if ((e instanceof InvocationTargetException)) {
				e1 = ((InvocationTargetException) e).getTargetException();
			}
			boolean isAlert = false;
			String message = null;

			Throwable tracet = null;
			String eid;
			if ((e1 instanceof BusinessException)) {
				BusinessException ae = (BusinessException) e1;
				Throwable rawCause = ae.getRawCause();
				if (rawCause == null) {
					ae = ae.getRawBusinessException();
					message = ae.getMessage();
					isAlert = true;
				} else {
					tracet = rawCause;
				}
				eid = ae.getCode();
			} else {
				tracet = e1;
				String curm = String.valueOf(System.currentTimeMillis());
				eid = curm.substring(curm.length() - 10);
			}
			if (tracet != null) {
				message = tracet.getMessage();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				tracet.printStackTrace(pw);
				sw.toString();
			}
			JsonErrorObject jsonErrorObject = new JsonErrorObject();
			jsonErrorObject.setMessage(message);

			jsonErrorObject.setCode(eid);
			try {
				jsonValue = JSONMapper.toJSON(jsonErrorObject);
				if (ctpJSONPCallback == null) {
					retValue = jsonValue.render(false);
				} else {
					retValue = String.format("%1$s(%2$s)", new Object[] { ctpJSONPCallback, jsonValue.render(false) });
				}
			} catch (Exception localException) {
			}
			if (ctpJSONPCallback == null) {
				response.setStatus(500);
			} else {
				response.setStatus(200);
			}
			if (!isAlert) {
				LOGGER.error("出现AJAX异常，ID：" + eid, e1);
			}
		}
		return retValue;
	}

	private void notfound(HttpServletResponse response) {
		try {
			response.sendError(404);
		} catch (IOException e) {
			this.logger.error(e.getLocalizedMessage(), e);
		}
	}

	private Object invokeMethod(Object service, String methodName, String strArgs, String serviceName)
			throws Exception {
		Object result = null;
		if ((strArgs == null) || ("".equals(strArgs)) || ("undefined".equals(strArgs))) {
			strArgs = "[]";
		}
		JSONValue jsonValueArgs = new JSONParser(new StringReader(strArgs)).nextValue();
		Object[] jsonArgs = { jsonValueArgs };
		if ((jsonValueArgs instanceof JSONArray)) {
			jsonArgs = ((JSONArray) jsonValueArgs).getValue().toArray();
		}
		int argsNum = jsonArgs.length;

		Method[] methods = service.getClass().getMethods();
		List candidateMethods = new ArrayList();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if ((method.getName().equals(methodName)) && (method.getParameterTypes().length == argsNum)) {
				candidateMethods.add(method);
			}
		}
		Method targetMethod = null;
		Object[] args = (Object[]) null;
		for (Iterator iter = candidateMethods.iterator(); iter.hasNext();) {
			Method method = (Method) iter.next();
			Class[] argTypes = method.getParameterTypes();
			args = new Object[argTypes.length];
			try {
				for (int i = 0; i < argTypes.length; i++) {
					args[i] = JSONMapper.toJava((JSONValue) jsonArgs[i], argTypes[i]);
				}
			} catch (MapperException e) {
				LOGGER.warn("", e);
				continue;
			}
			targetMethod = method;
			break;
		}
		if (targetMethod != null) {
			try {
				User currentUser = AppContext.getCurrentUser();
				if (logAjax.isDebugEnabled()) {
					logAjax.debug(serviceName + "," + methodName + "," + hasMemberId(args) + ","
							+ currentUser.isAdministrator() + "," + currentUser.isAuditAdmin() + ","
							+ currentUser.isSystemAdmin() + "," + currentUser.isGroupAdmin() + ","
							+ AppContext.currentUserLoginName() + "," + strArgs);
				}
			} catch (Exception localException) {
			}
			result = targetMethod.invoke(service, args);
			AspectAnnotationAware.execute(service, methodName);
		} else {
			String errStr = "can not find the method [" + methodName + "], which has " + argsNum + " arguments.";
			NoSuchMethodException ex = new NoSuchMethodException(errStr);
			throw ex;
		}
		return result;
	}

	private Set<String> memberIds = null;

	private OrgManager getOrgManager() {
		if (this.orgManager == null) {
			this.orgManager = ((OrgManager) AppContext.getBean("orgManager"));
		}
		return this.orgManager;
	}

	private Set<String> getAllMembers() throws BusinessException {
		if (this.memberIds == null) {
			this.memberIds = Collections.newSetFromMap(new ConcurrentHashMap());
			Iterator localIterator2;
			for (Iterator localIterator1 = getOrgManager().getAllAccounts().iterator(); localIterator1
					.hasNext(); localIterator2.hasNext()) {
				V3xOrgAccount account = (V3xOrgAccount) localIterator1.next();
				localIterator2 = getOrgManager().getAllMembers(account.getId()).iterator();
				V3xOrgMember member = (V3xOrgMember) localIterator2.next();
				this.memberIds.add(String.valueOf(member.getId()));
			}
		}
		return this.memberIds;
	}

	private boolean hasMemberId(Object[] args) {
		try {
			Object[] arrayOfObject;
			int j = (arrayOfObject = args).length;
			for (int i = 0; i < j; i++) {
				Object o = arrayOfObject[i];
				if (o != null) {
					if (getAllMembers().contains(o)) {
						return true;
					}
				}
			}
		} catch (Throwable e) {
			this.logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	public AjaxAccessInterceptor getAjaxAccessInterceptor() {
		return this.ajaxAccessInterceptor;
	}

	public void setAjaxAccessInterceptor(AjaxAccessInterceptor ajaxAccessInterceptor) {
		this.ajaxAccessInterceptor = ajaxAccessInterceptor;
	}
}
