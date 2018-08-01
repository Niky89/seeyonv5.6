package com.deheng.login;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONObject;
import com.deheng.utils.Util;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.portal.sso.SSOLoginHandshakeAbstract;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.Strings;

public class WeixinWorkLoginSso extends SSOLoginHandshakeAbstract {

	@Override
	/**
	 * param 是ticket 票据
	 */
	public String handshake(String paramString) {
		// 握手协议
		// 返回用户登录名
		/*
		JSONObject jobj = JSONObject.parseObject(paramString);
		String oticket = jobj.get("ticket").toString();
		String state = jobj.get("state").toString();
		String code = jobj.get("code").toString();

		// 用state 获取配置文件
		Util u = new Util();
		if (!Strings.isEmpty(state)) {// 参数不存在跳过
			String wxconf = u.getProperty(state);
			if (!Strings.isEmpty(wxconf)) {// 判断是否有这个参数
				JSONObject jo = JSONObject.parseObject(wxconf);
				String cappid = jo.getString("appid");
				String secret = jo.getString("Secret");
				String token = null;
				if (secret == null) {
					token = u.getQyAccessToken();
				} else {
					token = u.getQyAccessToken(secret);
				}
				String userid = u.checkQyCode(code, token);
				if (userid != null && !"".equals(userid)) {
					
					OrgManager orgManager = (OrgManager) AppContext.getBean("orgManager");
					// 去数据库里查一下是否存在这个企业微信Userid
					JDBCAgent dba = new JDBCAgent();
					String sql = "SELECT id FROM ORG_MEMBER WHERE EXT_ATTR_32='" + userid + "'";
					try {
						int i = dba.execute(sql);
						ResultSet rs = dba.getQueryResult();
						if (rs.next()) {// 能找到
							Long id = rs.getLong("id");
							rs.close();
							dba.close();
							V3xOrgMember v3xOrgmember = orgManager.getMemberById(id);// 根据当前用户的id获取当前用户的orgmember
							return v3xOrgmember.getLoginName();
						} 

					} catch (BusinessException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			} 
		} 
		// 如果获取的到 进行验证.否则返回空
		// 验证通过返回登录名
		// 验证失败返回空*/
		return "songxiaolong";
	}

	@Override
	public void logoutNotify(String paramString) {
		// 登出时调用此功能
	}

}
