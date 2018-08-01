package com.seeyon.apps.wechat.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

public class WorkWeichatLoginFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 首先,从request 中获取相应的参数
		// String appid = request.getParameter("appid");
		// if (appid != null) {
		// System.out.println(request.getParameter("method"));
		// System.out.println(request.getParameter("code"));
		// System.out.println(request.getParameter("state"));//作为key
		// 可以从配置文件中获取appid
		// 然后从配置文件中获取需要的数据
		HttpServletRequest req = (HttpServletRequest) request;
		String path = ((HttpServletRequest) request).getRequestURI();
		Map<String, String[]> parameterMap = new HashMap<String,String[]>(req.getParameterMap());
		String oticket=req.getParameter("ticket");
		String state=req.getParameter("state");
		String code=req.getParameter("code");
		JSONObject jobj=new JSONObject();
		jobj.put("ticket",oticket);
		jobj.put("state", state);
		jobj.put("code", code);
		String nticket=jobj.toJSONString();

		//此处把ticket 拼接上其他参数传递到sso登陆模块上去
		if(oticket!=null && nticket.length()>0){
			parameterMap.put("ticket",new String[]{nticket});
			request=new ParameterRequestWrapper(req, parameterMap);
		}
		
		
		//parameterMap.put("ticket",new String[]{oticket+"filter 修改"});
		//request=new ParameterRequestWrapper(req, parameterMap);
		// Util u = new Util();
		// String state = request.getParameter("state");
		// if (!Strings.isEmpty(state)) {// 参数不存在跳过
		// String wxconf = u.getProperty(state);
		// if (!Strings.isEmpty(wxconf)) {//判断是否有这个参数
		// JSONObject jo= JSONObject.parseObject(wxconf);
		// String appid=jo.getString("appid");
		// String code = request.getParameter("code");
		// String secret = jo.getString("Secret");
		//
		// String token = null;
		// if (secret == null) {
		// token = u.getQyAccessToken();
		// } else {
		// token = u.getQyAccessToken(secret);
		// }
		// String userid = u.checkQyCode(code, token);
		// // 验证通过合法id之后判断当前页面是否已经绑定企业微信
		// if (userid != null && !"".equals(userid)) {
		// OrgManager orgManager = (OrgManager)
		// AppContext.getBean("orgManager");
		// // 去数据库里查一下是否存在这个企业微信Userid
		// JDBCAgent dba = new JDBCAgent();
		// String sql = "SELECT id FROM ORG_MEMBER WHERE EXT_ATTR_32='" + userid
		// + "'";
		// HttpSession session = AppContext.getRawSession();
		//
		// try {
		// int i = dba.execute(sql);
		// ResultSet rs = dba.getQueryResult();
		// if (rs.next()) {// 能找到
		// Long id = rs.getLong("id");
		// rs.close();
		// dba.close();
		// V3xOrgMember v3xOrgmember = orgManager.getMemberById(id);//
		// 根据当前用户的id获取当前用户的orgmember
		// if (session != null) {
		// session.removeAttribute("qyweixinbind");
		// }
		// String [] test=new String[] { v3xOrgmember.getLoginName(), "~`@%^*#?"
		// };
		// } else {// 找不到
		// // 需要跳转到绑定页面
		// if (session != null) {
		// session.setAttribute("qywxuserid", userid);
		// session.setAttribute("qyweixinbind", "true");// 找不到
		// } // 加一个标签
		// }
		//
		// } catch (BusinessException e) {
		// e.printStackTrace();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		//
		// }
		//
		// System.out.println(wxconf);
		// }
		// }
		// 然后调用util 的 token功能然后登陆
		// 再然后执行验证功能.并返回数据
		chain.doFilter(request, response);
		// 如果以上都执行成功的话就执行不成功直接
		// } else {
		// chain.doFilter(request, response);
		// }
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
