package com.seeyon.apps.putonrecord.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;

import com.deheng.utils.DBUtil;
import com.deheng.utils.Util;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.JDBCAgent;

/**
 * 
 * @author sxl 利益冲突数据库访问类 //测试调用第三方数据库. DBUtil dbu=new DBUtil();
 *         dbu.executeSql("11","select * from dbo.t_kq where id=643323");
 *
 *         String s="传递数量为"+wtcount+"---"+objs.size(); while (dbu.next()) {
 *         s+=dbu.getString("S_ksbz")+"<<>>"; }
 */
public class ConflictOfInterestDao {

	public List<Map<String, String>> getAllPrincipal(String param) {
		int count = 0;
		List<Map<String, String>> result = new ArrayList();

		DBUtil dbu = new DBUtil();
		String dhsql = "select top 20 id,S_name as name from dbo.t_las_khxx where n_flag=1 and S_name <>'' and S_name is not null and S_name like '%" + Util.checkSqlString(param) + "%'";
		boolean dhflag = dbu.executeSql("", dhsql);
		if (dhflag) {
			while (dbu.next()) {
				Map<String, String> m = new HashMap();

				m.put("id", dbu.getString("id"));
				m.put("name", dbu.getString("name"));
				m.put("ls", "DH");
				m.put("latype", "现立案客户");
				result.add(m);
				count++;
			}
		}
		if (count > 20) {
			return result;
		}
		JDBCAgent dba = new JDBCAgent();
		String sysql = "select top 20 ID,field0007 as name from formmain_0032 where field0007 like '%" + Util.checkSqlString(param) + "%'";
		try {
			dba.execute(sysql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				Map<String, String> m = new HashMap();
				m.put("id", rs.getString("ID"));
				m.put("name", rs.getString("name"));
				m.put("latype", "现立案客户");
				result.add(m);
				count++;
				if (count > 20) {
					break;
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Map<String, String>> getALlPri(List<Term> parse) {
		Map<String, Map<String, String>> result = new HashMap();

		DBUtil dbu = new DBUtil();
		String dhsql = "select a.s_bgs as bgs, d.n_zbid as zbid, d.S_name as name,d.n_flag as flag from t_la a left join dbo.t_las_khxx d on a.id=d.N_zbid where d.n_flag =1 ";
		for (Term t : parse) {
			dhsql = dhsql + " and S_name like '%" + Util.checkSqlString(t.getName()) + "%'";
		}

		dhsql = dhsql + " order by n_zbid desc";
		boolean dhflag = dbu.executeSql("", dhsql);
		if (dhflag) {
			while (dbu.next()) {
				if (dbu.getString("name") != null) {
					Map<String, String> itemmap = new HashMap();
					// 老内网数据通过主表id获取主办律师

					itemmap.put("zbls", getZBLSFromOldDB(dbu.getString("zbid")));
					if ((dbu.getString("bgs") != null) && (dbu.getString("bgs").contains("德和衡"))) {
						itemmap.put("ls", "德和衡");
					} else {
						itemmap.put("ls", "德衡");
					}
					if (dbu.getInt("flag") != 0) {
						itemmap.put("latype", "现立案客户");

						result.put(dbu.getString("name").trim(), itemmap);
					}
				}
			}
		}
		JDBCAgent dba = new JDBCAgent();

		String sysql = "select t.showvalue as showvalue,a.id,a.field0001 as ls,field0007 as name,a.field0006 as zbls from formmain_0226 a left join ctp_enum_item t on a.field0013=t.id where 1=1 ";
		for (Term t : parse) {
			sysql = sysql + " and a.field0007 like '%" + Util.checkSqlString(t.getName()) + "%'";
		}
		sysql = sysql + " order by id desc";
		try {
			dba.execute(sysql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				Map<String, String> itemmap = new HashMap();
				itemmap.put("id", rs.getString("id"));
				if (this.getOrgMemberName(rs.getString("zbls")) != null) {
					itemmap.put("zbls", this.getOrgMemberName(rs.getString("zbls")));
				} else {
					itemmap.put("zbls", "");
				}
				itemmap.put("latype", rs.getString("showvalue"));
				if ((rs.getString("ls") != null) && (rs.getString("ls").contains("DHH"))) {
					itemmap.put("ls", "德和衡");
				} else {
					itemmap.put("ls", "德衡");
				}
				if (!rs.getString("showvalue").trim().contains("预立案客户")) {
					itemmap.put("latype", rs.getString("showvalue").trim());
					result.put(rs.getString("name").trim(), itemmap);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Map<String, String>> getALlPri2(List<Term> parse) {
		Map<String, Map<String, String>> result = new HashMap();

		DBUtil dbu = new DBUtil();
		String dhsql = "select a.s_bgs as bgs, d.n_zbid as zbid, d.S_name as name,d.n_flag as flag,a.D_ladate as latime from t_la a left join dbo.t_las_khxx d on a.id=d.N_zbid where d.n_flag =0  ";
		for (Term t : parse) {
			dhsql = dhsql + " and S_name like '%" + Util.checkSqlString(t.getName()) + "%'";
		}

		dhsql = dhsql + " order by n_zbid desc";
		boolean dhflag = dbu.executeSql("", dhsql);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		if (dhflag) {
			while (dbu.next()) {
				if (dbu.getString("name") != null) {
					Map<String, String> itemmap = new HashMap();
					itemmap.put("zbls", getZBLSFromOldDB(dbu.getString("zbid")));
					if ((dbu.getString("bgs") != null) && (dbu.getString("bgs").contains("德和衡"))) {
						itemmap.put("ls", "德和衡");
					} else {
						itemmap.put("ls", "德衡");
					}
					if (dbu.getDate("latime") != null) {
						itemmap.put("latime", sdf.format(dbu.getDate("latime")));
					} else {
						itemmap.put("latime", "");
					}
					if (dbu.getInt("flag") == 0) {
						itemmap.put("latype", "预立案客户");
						result.put(dbu.getString("name").trim(), itemmap);
					}
				}
			}
		}
		JDBCAgent dba = new JDBCAgent();

		String sysql = "select t.showvalue,a.id,a.field0001 as ls,field0007 as name,a.field0006 as zbls,a.start_date as latime from formmain_0226 a left join ctp_enum_item t on a.field0013=t.id where  1=1  ";
		for (Term t : parse) {
			sysql = sysql + " and a.field0007 like '%" + Util.checkSqlString(t.getName()) + "%'";
		}
		sysql = sysql + " order by id desc";
		try {
			dba.execute(sysql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				Map<String, String> itemmap = new HashMap();
				itemmap.put("id", rs.getString("id"));
				if (this.getOrgMemberName(rs.getString("zbls")) != null) {
					itemmap.put("zbls", this.getOrgMemberName(rs.getString("zbls")));
				} else {
					itemmap.put("zbls", "");
				}
				if ((rs.getString("ls") != null) && (rs.getString("ls").contains("DHH"))) {
					itemmap.put("ls", "德和衡");
				} else {
					itemmap.put("ls", "德衡");
				}
				if (rs.getDate("latime") != null) {
					itemmap.put("latime", sdf.format(rs.getDate("latime")));
				} else {
					itemmap.put("latime", "");
				}
				if (rs.getString("showvalue").contains("预立案客户")) {
					itemmap.put("latype", "预立案客户");
					result.put(rs.getString("name").trim(), itemmap);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
//查询所有对方当事人
	public Map<String, Map<String, String>> getAllOther(List<Term> parse) {
		Map<String, Map<String, String>> result = new HashMap();

		DBUtil dbu = new DBUtil();
		String dhsql = "select a.s_bgs as bgs,d.n_zbid as zbid,d.S_name as name,d.n_flag as flag from t_la a inner join dbo.t_las_khxx d on a.id=d.N_zbid where n_flag=2 ";
		for (Term t : parse) {
			dhsql = dhsql + " and S_name like '%" + Util.checkSqlString(t.getName()) + "%'";
		}

		dhsql = dhsql + " order by n_zbid desc";
		boolean dhflag = dbu.executeSql("", dhsql);
		if (dhflag) {
			//当出现多个同名人的时候需要对其进行拆分.此处通过增加后缀的方式来处理
			//需要检测其中的这个名是否重复出现并进行标记
			while (dbu.next()) {
				if (dbu.getString("name") != null) {
					Map<String, String> itemmap = new HashMap();
					itemmap.put("id", dbu.getString("zbid"));
					String zbls=getZBLSFromOldDB(dbu.getString("zbid"));
					itemmap.put("zbls",zbls );
					if ((dbu.getString("bgs") != null) && (dbu.getString("bgs").contains("德和衡"))) {
						itemmap.put("ls", "德和衡");
					} else {
						itemmap.put("ls", "德衡");
					}
					if (dbu.getInt("flag") == 0) {
						itemmap.put("latype", "预立案客户");
					} else {
						itemmap.put("latype", "现立案客户");
					}
					result.put(dbu.getString("name").trim()+"#"+zbls, itemmap);
				}
			}
		}
		JDBCAgent dba = new JDBCAgent();

		String sysql1 = "select field0001 as ls,field0041 as name,field0003 as orgid from formmain_0263 a left join formson_0265 b on a.id=b.formmain_id";
		String sysql2 = "select field0001 as ls,field0038 as name,field0003 as orgid from formmain_0279 a left join formson_0281 b on a.id=b.formmain_id";
		String sysql3 = "select field0001 as ls,field0046 as name,field0003 as orgid from formmain_0294 a left join formson_0296 b on a.id=b.formmain_id";

		String uni = " union ";
		String sysql = "select ls,name,a.orgid as zbls from (" + sysql1 + uni + sysql2 + uni + sysql3 + ") a where 1=1 ";

		for (Term t : parse) {
			sysql = sysql + " and name like '%" + Util.checkSqlString(t.getName()) + "%'";
		}
		try {
			dba.execute(sysql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				if (rs.getString("name") != null) {
					Map<String, String> itemmap = new HashMap<String, String>();
					String zbls="";
					if (this.getOrgMemberName(rs.getString("zbls")) != null) {
						zbls=this.getOrgMemberName(rs.getString("zbls"));
						itemmap.put("zbls", zbls);
					} else {
						itemmap.put("zbls", "");
					}
					itemmap.put("latype", "");
					if ((rs.getString("ls") != null) && (rs.getString("ls").contains("DHH"))) {
						itemmap.put("ls", "德和衡");
					} else {
						itemmap.put("ls", "德衡");
					}
					result.put(rs.getString("name").trim()+"#"+zbls, itemmap);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//查询其他利益相关当事人 其他利益相关当事人应该为委托人的利益相关方
	public Map<String, Map<String, String>> getOtherInterests(List<Term> parse) {
		Map<String, Map<String, String>> result = new HashMap();
		JDBCAgent dba = new JDBCAgent();

		String sysql = "select a.field0002 as ls,b.field0112 as name, a.field0004 as zbls,b.field0113 as bz  from formmain_0420 a left join  dbo.formson_4276 b on a.id=b.formmain_id where 1=1 ";

		for (Term t : parse) {
			sysql = sysql + " and field0112 like '%" + Util.checkSqlString(t.getName()) + "%'";
		}
		try {
			dba.execute(sysql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				if (rs.getString("name") != null) {
					Map<String, String> itemmap = new HashMap<String, String>();
					String zbls="";
					if (this.getOrgMemberName(rs.getString("zbls")) != null) {
						zbls=this.getOrgMemberName(rs.getString("zbls"));
						itemmap.put("zbls", zbls);
					} else {
						itemmap.put("zbls", "");
					}
					itemmap.put("bz", rs.getString("bz"));
					if ((rs.getString("ls") != null) && (rs.getString("ls").contains("DHH"))) {
						itemmap.put("ls", "德和衡");
					} else {
						itemmap.put("ls", "德衡");
					}
					result.put(rs.getString("name").trim()+"#"+zbls, itemmap);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 根据字段获取数据库中org_member 拼接字符串
	public String getOrgMemberName(String zbls) {
		JDBCAgent dba = new JDBCAgent();
		if (zbls == null) {
			return null;
		}
		String[] zbss = zbls.split(",");
		String sql = "select name from org_member where id in (";
		for (int i = 0; i < zbss.length; i++) {
			sql += "'" + zbss[i] + "'";
			if (i < zbss.length - 1) {
				sql += ",";
			}
		}
		sql += ")";
		String result = "";

		try {
			dba.execute(sql);
			ResultSet rs = dba.getQueryResult();
			while (rs.next()) {
				result += rs.getString("name") + ",";
			}
			if (result.length() > 0) {
				result = result.substring(0, result.length() - 1);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dba.close();

		return result;
	}

	public String getZBLSFromOldDB(String zbid) {
		DBUtil dbu = new DBUtil();
		String dhsql = "select s_xm from t_las_bar where N_rylx=1 and N_zbid=" + zbid;

		boolean dhflag = dbu.executeSql("", dhsql);
		StringBuffer sb = new StringBuffer();
		if (dhflag) {
			while (dbu.next()) {
				String xm = dbu.getString("s_xm");
				sb.append(xm.replace("、", "") + ",");
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		} else {
			return "";
		}
	}

	public static void main(String[] args) {
		String result = "111,111,222,2222x,";
		result.substring(0, result.length() - 1);
		System.out.println(result);
	}
}
