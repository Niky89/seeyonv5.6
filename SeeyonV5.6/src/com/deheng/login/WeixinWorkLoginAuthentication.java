package com.deheng.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.deheng.utils.Util;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.login.LoginAuthentication;
import com.seeyon.ctp.login.LoginAuthenticationException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.JDBCAgent;

public class WeixinWorkLoginAuthentication implements LoginAuthentication {

	@Override
	public String[] authenticate(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse) throws LoginAuthenticationException {
		// 验证不通过 返回null
		// 验证通过 返回数组,组成部分0为username 1 为password
		// &code=YGBFNklXbwY_-1b0wQp55h9xv41Ze-totFcwmuZZrWg&state=abc&appid=wx119567e228b99787
		String appid = paramHttpServletRequest.getParameter("appid");
		if (appid == null) {
			appid = (String) paramHttpServletRequest.getAttribute("appid");
		}
		String state = paramHttpServletRequest.getParameter("state");
		String code = paramHttpServletRequest.getParameter("code");
		String secret = (String) paramHttpServletRequest.getAttribute("secret");
		if (appid == null || state == null) {
			return null;
		}
		if (appid.equals("wx119567e228b99787") && state.equals("abc")) {// 初步验证
			// 合法链接之后请求微信企业号验证code 是否合法.
			Util u = new Util();
			String token = null;
			if (secret == null) {
				token = u.getQyAccessToken();
			} else {
				token = u.getQyAccessToken(secret);
			}
			String userid  = u.checkQyCode(code, token);
			// 验证通过合法id之后判断当前页面是否已经绑定企业微信
			if (userid != null && !"".equals(userid)) {
				OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
				// 去数据库里查一下是否存在这个企业微信Userid
				JDBCAgent dba = new JDBCAgent();
				String sql = "SELECT id FROM ORG_MEMBER WHERE EXT_ATTR_32='" + userid + "'";
				HttpSession session = AppContext.getRawSession();

				try {
					int i = dba.execute(sql);
					ResultSet rs = dba.getQueryResult();
					if (rs.next()) {// 能找到
						Long id = rs.getLong("id");
						rs.close();
						dba.close(); 
						V3xOrgMember v3xOrgmember = orgManager.getMemberById(id);// 根据当前用户的id获取当前用户的orgmember
						if (session != null) {
							session.removeAttribute("qyweixinbind");
						}
						return new String[] { v3xOrgmember.getLoginName(), "~`@%^*#?" };
					} else {// 找不到
						rs.close();
						dba.close(); 
						// 需要跳转到绑定页面
						if (session != null) {
							session.setAttribute("qywxuserid", userid);
							session.setAttribute("qyweixinbind", "true");// 找不到
						} // 加一个标签
					}
					
				} catch (BusinessException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
		}
		return null;
	}

}