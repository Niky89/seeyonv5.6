package com.seeyon.ctp.login.interceptor;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.IP;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.LoginResult;
import com.seeyon.ctp.common.ipcontrol.IpcontrolUserManager;
import com.seeyon.ctp.login.AbstractLoginInterceptor;
import com.seeyon.ctp.util.Strings;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IpcontrolLoginInterceptor extends AbstractLoginInterceptor {
	private SystemConfig systemConfig = null;

	public LoginResult preHandle(HttpServletRequest request, HttpServletResponse response) {
		if (this.systemConfig == null) {
			this.systemConfig = ((SystemConfig) AppContext.getBean("systemConfig"));
		}
		if ("enable".equals(this.systemConfig.get("ip_control_enable"))) {
			String username = request.getParameter("login_username");
			String ipAddress = Strings.getRemoteAddr(request);
			if (username == null) {
				return LoginResult.OK;
			}
			Set<String> noLimitIp = IpcontrolUserManager.getInstance().getNoLimitIp();
			Map<String, List<IP>> limitIp = IpcontrolUserManager.getInstance().getLimitIp();
			if (noLimitIp.contains(username)) {
				return LoginResult.OK;
			}
			if (limitIp.containsKey(username)) {
				for (IP ip :  limitIp.get(username)) {
					if (ip.matching(ipAddress)) {
						return LoginResult.OK;
					}
				}
				return LoginResult.ERROR_IPCONTROL;
			}
			return LoginResult.OK;
		}
		return LoginResult.OK;
	}
}
