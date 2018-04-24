package com.seeyon.apps.putonrecord.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.HashSet;
import java.util.List;

import com.deheng.utils.DBUtil;
//import com.deheng.utils.Util;
import com.seeyon.apps.putonrecord.po.CaseInf;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.JDBCAgent;
/**
 * 
 * @author zxj
 */
public class AnJianDao {
	/**
	 * 获得案件信息查询list
	 * 
	 */
	public List<CaseInf> getAllList(CaseInf caseInf) {

		List<CaseInf> oldList = new ArrayList<CaseInf>();
		List<CaseInf> list = new ArrayList<CaseInf>();

		// 改变后的实体类

		CaseInf rsTree_old;
		CaseInf rsTree1;
		CaseInf rsTree2;
		CaseInf rsTree3;
		CaseInf rsTree4;

		String kehuname = (String) caseInf.getKehuname();
		String latime1 = (String) caseInf.getLatime1();
		String latime2 = (String) caseInf.getLatime2();
		String zblawyer = (String) caseInf.getZblawyer();
		String ajbh = (String) caseInf.getAjbh();
		// String khstyle = caseInf.getKhstyle(); //NOT USED
		String ajlb = (String) caseInf.getAjlb();
		// String ajstate = (String) params.get("ajstate");
		String hylb = (String) caseInf.getHylb();
		// String khjb = (String) params.get("khjb");
		String ahmainyw = (String) caseInf.getAhmainyw();
		String ayperson = (String) caseInf.getAyperson();
		String tbbgs = (String) caseInf.getTbbgs();
		String dycity = (String) caseInf.getDycity();
		String department = (String) caseInf.getDepartment();
		JDBCAgent dba1 = new JDBCAgent();
		JDBCAgent dba2 = new JDBCAgent();
		JDBCAgent dba3 = new JDBCAgent();
		JDBCAgent dba4 = new JDBCAgent();

		// 查询老内网数据
		DBUtil dbu = new DBUtil();
		// 原版
		String dhsql = "select top 20  ((case a.S_bgs when '青岛'  then 'QD' when '北京' then 'BJ' when '济南' then 'JN' when '上海' then 'SH' when '青岛高新区' then 'GX' when '青岛西海岸' then 'XHA' when '德和衡上海' then 'DHHSH' when '德和衡北京' then 'DHHBJ' when '德和衡南京' then 'DHHNJ' when '德和衡邯郸' then 'DHHHD' when '德和衡青岛' then 'DHHQD' else '' end  ) + (case a.s_lxflag when '法律顾问'  then 'A' when '诉讼与仲裁业务（非刑事）' then 'B' when '诉讼与仲裁业务（刑事）' then 'B'  when '非诉业务' then 'C'  else '' end  )+cast(a.n_bh as varchar(100)))   as ajbh,"
				+ " ((stuff((select ',' + c.S_name from ( select distinct N_zbid ,S_name from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) + '-' +  a.s_lxflag  ) as ajmc ,replace((stuff((select ',' + c.S_xm  from ( select distinct N_zbid , S_xm from t_las_bar where N_rylx ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) ,'、','') as zblawyer,ja.S_tbr  as tbname ,a.S_bgs as tbbgs,a.s_lxflag as ajlb,"
				+ " CONVERT(varchar(100),a.D_ladate, 23) as latime,a.N_ajbde  as ajbde, (case  a.N_ts  when '0' then '否' when '1' then '是' else '' end ) as sfsytsaj ,a.s_sj as sj,  d.S_mc  as ayperson, "
				+ "a.S_ajqk as  ajjk ,a.S_fl as hylb ,(stuff((select ',' + c.S_name  from ( select distinct N_zbid ,S_name,N_hylb , S_ountry,S_province,S_city ,s_zyyw ,s_khlx from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as wtname,"
				+ "(stuff((select ',' + c.S_name  from ( select distinct N_zbid ,S_name,N_hylb , S_ountry,S_province,S_city ,s_zyyw ,s_khlx from t_las_khxx where N_flag ='2') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as dfname,"
				+ " (stuff((select ',' + c.S_xm  from ( select distinct N_zbid , S_xm  from t_las_zyf  ) c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as zyfname,"
				+ "(stuff((select ',' + c.N_bl  from ( select distinct N_zbid , cast(N_bl as varchar(100)) as N_bl  from t_las_zyf  ) c  where a.id=c.N_zbid   for xml path('')), 1, 1, ''))  as zyfbl,"
				+ " (stuff((select ',' + c.S_bm  from ( select distinct N_zbid , S_bm  from t_las_zyf ) c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as zyfdepartment ,"
				+ " (select department  from lawyer  c  where c.name = ja.S_tbr  )  as department ,"
				+ " (case a.N_sflx when '' then '计件' when '1' then '计件' when '2' then '计时' when '3' then '按标的' when '4' then '风险' when '5' then '分段' when '7' then '减收费' when '8' then '免费' when '9' then '合并收费' when '11' then '收费待定' else '其他' end) as  jffs,"
				+ " a.S_jfsfyy  as jmsf ,(stuff((select ',' + c.s_zyyw  from ( select distinct N_zbid , s_zyyw  from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as ahmainyw ,"
				+ " (stuff((select ',' + c.s_khlx  from ( select distinct N_zbid , s_khlx from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) as khstyle ,"
				+ "(stuff((select ',' + c.S_ountry  from ( select distinct N_zbid , S_ountry  from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')),"
				+ "(stuff((select ',' + c.S_province  from ( select distinct N_zbid , S_province from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')),"
				+ "(stuff((select ',' + c.S_city  from ( select distinct N_zbid , S_city   from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')), '' as state  "
				+ " from t_la a  "
				+ " left join t_ja ja on ja.N_laid = a.id  left join t_la_zd_ajly d on a.N_ajlyid=d.id  where a.n_bh !=0 ";
		// 根据客户名称查询
		if (!kehuname.equals("") && null != kehuname) {
			dhsql = dhsql
					+ " and (stuff((select ',' + c.S_name  from ( select distinct N_zbid ,S_name from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) like '%"
					+ kehuname + "%'";
		}
		// 根据立案时间查询
		if (!latime1.equals("") && null != latime1) {
			dhsql = dhsql + " and a.D_ladate >='" + latime1 + "'";
		}
		if (!latime2.equals("") && null != latime2) {
			dhsql = dhsql + " and a.D_ladate <='" + latime2 + "'";
		}
		// 根据主办律师查询
		if (!zblawyer.equals("") && null != zblawyer) {
			dhsql = dhsql
					+ "and  replace((stuff((select ',' + c.S_xm  from ( select distinct N_zbid , S_xm from t_las_bar where N_rylx ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) ,'、','') like '%"
					+ zblawyer + "%'";
		}
		// 根据案件编号查询
		if (!ajbh.equals("") && null != ajbh) {
			dhsql = dhsql
					+ " and  ((case a.S_bgs when '青岛'  then 'QD' when '北京' then 'BJ' when '济南' then 'JN' when '上海' then 'SH' when '青岛高新区' then 'GX' when '青岛西海岸' then 'XHA' when '德和衡上海' then 'DHHSH' when '德和衡北京' then 'DHHBJ' when '德和衡南京' then 'DHHNJ' when '德和衡邯郸' then 'DHHHD' when '德和衡青岛' then 'DHHQD' else '' end  ) + (case a.s_lxflag when '法律顾问'  then 'A' when '诉讼与仲裁业务（非刑事）' then 'B' when '诉讼与仲裁业务（刑事）' then 'B'  when '非诉业务' then 'C'  else '' end  )+cast(a.n_bh as varchar(100))) like '%"
					+ ajbh + "%'";
		}
		// 根据客户类型查询
		// if(!khstyle.equals("") && null != khstyle){
		// dhsql = dhsql +" and a.N_flag ='"+ khstyle + "'";
		// }
		// 根据案件类型查询
		if (!ajlb.equals("") && null != ajlb) {
			dhsql = dhsql + " and  a.s_lxflag like '%" + ajlb + "%'";
		}
		// 根据行业类别查询(案件分类（刑事案件没有）)
		if (!hylb.equals("") && null != hylb) {
			dhsql = dhsql + " and  a.S_fl like '%" + hylb + "%'";
		}
		// 根据案件状态查询
		// if(!ajstate.equals("") && null != ajstate){
		// dhsql = dhsql +" and state ='"+ ajstate + "'";
		// }

		// 根据客户级别查询
		// if(!khjb.equals("") && null != khjb){
		// dhsql = dhsql +" and khjb ='"+ khjb + "'";
		// }
		// 客户主营业务查询
		if (!ahmainyw.equals("") && null != ahmainyw) {
			dhsql = dhsql
					+ " and  (stuff((select ',' + c.s_zyyw  from ( select distinct N_zbid , s_zyyw  from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) like'%"
					+ ahmainyw + "%'";
		}
		// 案源人查询N_ajlyid(案源来源id)
		if (!ayperson.equals("") && null != ayperson) {
			dhsql = dhsql + " and  d.S_mc like'%" + ayperson + "%'";
		}
		// 根据填报办公室查询
		if (!tbbgs.equals("") && null != tbbgs) {
			dhsql = dhsql + " and  a.S_bgs like'%" + tbbgs + "%'";
		}
		// 根据地域查询
		if (!dycity.equals("") && null != dycity) {
			dhsql = dhsql
					+ " and  ((stuff((select ',' + c.S_province  from ( select distinct N_zbid , S_province from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) like'%"
					+ dycity
					+ "%' or (stuff((select ',' + c.S_city  from ( select distinct N_zbid , S_city   from t_las_khxx where N_flag ='1') c  where a.id=c.N_zbid   for xml path('')), 1, 1, '')) like '%"
					+ dycity + "%')  ";
		}
		// 根据部门查询
		// if(!department.equals("") && null != department){
		// dhsql = dhsql +" and department like'%"+ department + "%'";
		// }
		boolean dhflag = dbu.executeSql("", dhsql);

		// ResultSet rs_old = null; //not USED

		//
		if (dhflag) {
			int numberOfColumns_old = dbu.getColumnCount();
			while (dbu.next()) {
				rsTree_old = new CaseInf();// 注意要new
				for (int r = 1; r < numberOfColumns_old + 1; r++) {
					if (dbu.getColumnName(r - 1).equals("ahmainyw")) {
						rsTree_old.setAhmainyw(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("khstyle")) {
						rsTree_old.setKhstyle(dbu.getString(r));
					}
					// if(dbu.getColumnName(r-1).equals("state")){rsTree_old.setState(dbu.getObject(r));}
					if (dbu.getColumnName(r - 1).equals("shstate")) {
						rsTree_old.setShstate(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("fqname")) {
						rsTree_old.setFqname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("fqtime")) {
						rsTree_old.setFqtime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("shname")) {
						rsTree_old.setShname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("shtime")) {
						rsTree_old.setShtime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("lcstate")) {
						rsTree_old.setLcstate(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("hdstate")) {
						rsTree_old.setHdstate(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("hdname")) {
						rsTree_old.setHdname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("hdtime")) {
						rsTree_old.setHdtime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ajbh")) {
						rsTree_old.setAjbh(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ajmc")) {
						rsTree_old.setAjmc(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("zblawyer")) {
						rsTree_old.setZblawyer(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("tbname")) {
						rsTree_old.setTbname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("department")) {
						rsTree_old.setDepartment(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("tbbgs")) {
						rsTree_old.setTbbgs(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ajlb")) {
						rsTree_old.setAjlb(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("latime")) {
						rsTree_old.setLatime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ajbde")) {
						rsTree_old.setAjbde(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("sfsytsaj")) {
						rsTree_old.setSfsytsaj(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("sj")) {
						rsTree_old.setSj(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ayperson")) {
						rsTree_old.setAyperson(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("ajjk")) {
						rsTree_old.setAjjk(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("hylb")) {
						rsTree_old.setHylb(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jffs")) {
						rsTree_old.setJffs(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jmsf")) {
						rsTree_old.setJmsf(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("dfname")) {
						rsTree_old.setDfname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("wtname")) {
						rsTree_old.setWtname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("zyfname")) {
						rsTree_old.setZyfname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("zyfdepartment")) {
						rsTree_old.setZyfdepartment(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("zyfbl")) {
						rsTree_old.setZyfbl(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jfjebzhong")) {
						rsTree_old.setJfjebzhong(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jfjesdlf")) {
						rsTree_old.setJfjesdlf(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("qt")) {
						rsTree_old.setQt(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jfjestarttime")) {
						rsTree_old.setJfjestarttime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jfjeendtime")) {
						rsTree_old.setJfjeendtime(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("jfjebz")) {
						rsTree_old.setJfjebz(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("cyclname")) {
						rsTree_old.setCyclname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("cycldepartment")) {
						rsTree_old.setCycldepartment(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("cyclbgs")) {
						rsTree_old.setCyclbgs(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("lsf")) {
						rsTree_old.setLsf(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("lsflx")) {
						rsTree_old.setLsflx(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("fh")) {
						rsTree_old.setFh(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("xzname")) {
						rsTree_old.setXzname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("xzdepartment")) {
						rsTree_old.setXzdepartment(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("xzbgs")) {
						rsTree_old.setXzbgs(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("wslsname")) {
						rsTree_old.setWslsname(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("lxfs")) {
						rsTree_old.setLxfs(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("sheng")) {
						rsTree_old.setSheng(dbu.getString(r));
					}
					if (dbu.getColumnName(r - 1).equals("lsname")) {
						rsTree_old.setLsname(dbu.getString(r));
					}

					// rsTree_old.put(dbu.getColumnName(r-1),dbu.getObject(r));
					// System.out.print(dbu.getColumnName(r)+"=="+dbu.getObject(r)+";");
				}
				oldList.add(rsTree_old);
			}
			list.addAll(oldList);
		}

		// 致远内网查询
		// 诉讼与仲裁

		List<CaseInf> newlist1 = new ArrayList<CaseInf>();

		ResultSet rs1 = null;
		StringBuffer sql1 = new StringBuffer(
				// "select top 20 shstate , fqname , fqtime , shname , shtime ,
				// lcstate hdstate , hdname, hdtime , ");
				// sql1.append("ajbh , ajmc , zblawyer , tbname , department ,
				// tbbgs , ajlb, latime , ajbde , sfsytsaj , sj ");
				// sql1.append(", ayperson, ajjk , hylb , jffs , jmsf , dfname,
				// wtname, zyfname, zyfdepartment , zyfbl , jfjebzhong,");
				// sql1.append("jfjesdlf , qt , jfjestarttime , jfjeendtime ,
				// jfjebz, cyclname, cycldepartment , cyclbgs, lsf , lsflx , fh
				// , ");
				// sql1.append("xzname xzdepartment , xzbgs, wslsname , lxfs ,
				// sheng , lsname from (
				"select top 20  a.state as shstate ,  a.start_member_id as fqname ,a.start_date as fqtime ,a.approve_member_id as shname ,");
		sql1.append(
				"a.approve_date as shtime ,a.finishedflag as lcstate ,a.ratifyflag as hdstate ,a.ratify_member_id as hdname,a.ratify_date as hdtime ,");
		sql1.append(
				" a.field0001 as ajbh ,a.field0002 as ajmc ,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, ''))  as zblawyer ,");
		sql1.append(
				" (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0004)>0 for xml path('')), 1, 1, '')) as tbname ,(stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, ''))   as department ,bgs.name as tbbgs ,");
		sql1.append(
				" '诉讼与仲裁' as ajlb, a.field0008 ,a.field0058,a.field0059,CONVERT(varchar(100),a.field0009, 23)  as latime ,a.field0010 as ajbde ,( case a.field0011 when '-3232358927559997682'  then '是' when '-8192780380962075123' then '否' else '' end ) as sfsytsaj ,sjb.showvalue as sj ");
		sql1.append(
				" ,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0013)>0 for xml path('')), 1, 1, ''))  as ayperson,a.field0014  as ajjk ,a.field0015 as hylb ,a.field0016 as jffs ,a.field0017 as jmsf ,a.field0018,a.field0019,a.field0020,a.field0021,a.field0022,");
		sql1.append(
				" a.field0023,a.field0024,a.field0025,a.field0026,a.field0027,a.field0028,a.field0029,a.field0030,a.field0031, ");
		sql1.append(
				"a.field0032,a.field0033,a.field0034,a.field0035,a.field0036,a.field0037,a.field0038,a.field0039,a.field0060,");
		sql1.append(" a.field0063,a.field0064,");
		sql1.append(
				"  (stuff((select ',' + cast(c.field0041 as varchar(100)) from formson_0265 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as dfname,(stuff((select ',' + cast(c.field0040 as varchar(100)) from formson_0264 c  where a.id=c.formmain_id   for xml path('')), 1, 1, ''))  as wtname,(stuff((select ',' + c.NAME from org_member c left join ( select distinct  formmain_id,field0043,field0042,field0044 from   formson_0266 ) d on a.id=d.formmain_id  where charindex(cast(c.id as varchar(100)),d.field0042)>0 for xml path('')), 1, 1, ''))  as zyfname, (stuff((select distinct ',' + l.department_name   from RPT_MATTER_REPORT l left join ( select distinct  formmain_id,field0043,field0042,field0044 from   formson_0266 ) d on a.id=d.formmain_id where charindex(cast(l.department_id as varchar(100)),d.field0043)>0 for xml path('')), 1, 1, ''))  as zyfdepartment ,(stuff((select ',' + cast(c.field0044 as varchar(100)) from formson_0266 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as zyfbl , '' as state   ");
		// sql1.append("e.field0045 as jfjebzhong, e.field0046 as jfjesdlf
		// ,e.field0047 as qt , e.field0048 as jfjestarttime ,e.field0049 as
		// jfjeendtime , e.field0050 as jfjebz,");
		// 参与承揽与经办人员及律师费分配重复表
		// sql1.append(" f.field0051 as cyclname, f.field0052 as cycldepartment
		// , f.field0053 as cyclbgs, f.field0054 as lsf , f.field0061 as lsflx
		// ,f.field0062 as fh ,");
		// sql1.append(" g.field0055 as xzname ,g.field0056 as xzdepartment
		// ,g.field0057 as xzbgs,");
		// sql1.append("h.field0065 as wslsname , h.field0066 as lxfs
		// ,h.field0067 as sheng ,h.field0068 as shi,h.field0069 as lsname , ''
		// as state ");
		sql1.append("  from formmain_0263 a  ");
		sql1.append(" left join CTP_ENUM_ITEM sjb on a.field0012=sjb.id  ");
		sql1.append(
				" left join ( select distinct  field0042  from  formmain_0309 )  jab  on a.field0001 = jab.field0042  ");
		// sql1.append(" left join ( select distinct formmain_id,field0041 from
		// formson_0265 ) b on a.id=b.formmain_id ");
		// sql1.append(" left join ( select distinct formmain_id,field0040 from
		// formson_0264) c on a.id=c.formmain_id ");

		// sql1.append(" left join ( select distinct
		// formmain_id,field0045,field0046
		// ,field0047,field0048,field0049,field0050 from formson_0267 ) e on
		// a.id=e.formmain_id ");
		// 参与承揽与经办人员及律师费分配重复表
		// sql1.append(" left join ( select distinct formmain_id,field0051
		// ,field0052,field0053, field0054 ,field0061,field0062 from
		// formson_0268) f on a.id=f.formmain_id ");
		// sql1.append(" left join ( select distinct
		// formmain_id,field0055,field0056 ,field0057 from formson_0269 ) g on
		// a.id=g.formmain_id ");
		// sql1.append(" left join ( select distinct formmain_id ,field0065,
		// field0066 , field0067 ,field0068 , field0069 from formson_1369) h on
		// a.id=h.formmain_id ");
		// sql1.append(" left join (select distinct department_id,
		// department_name from RPT_MATTER_REPORT ) dep on dep.department_id =
		// a.field0005 ");
		sql1.append(" left join v3x_org_department bgs on bgs.id =  a.field0006 ");
		sql1.append("   where   a.field0001  !='0' ");

		// 根据客户名称查询
		if (!kehuname.equals("") && null != kehuname) {
			sql1.append(
					"and  (stuff((select ',' + cast(c.field0040 as varchar(100)) from formson_0265 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) like '%"
							+ kehuname + "%'");
		}
		// 根据立案时间查询
		if (!latime1.equals("") && null != latime1) {
			sql1.append("and  a.field0009 >='" + latime1 + "'");
		}
		if (!latime2.equals("") && null != latime2) {
			sql1.append("and  a.field0009 <='" + latime2 + "'");
		}

		// 根据案件编号查询
		if (!ajbh.equals("") && null != ajbh) {
			sql1.append("and  a.field0001 like'%" + ajbh + "%'");
		}

		// 根据行业类别查询
		if (!hylb.equals("") && null != hylb) {
			sql1.append("and  a.field0015 like'%" + hylb + "%'");
		}
		// 根据填报办公室查询
		if (!tbbgs.equals("") && null != tbbgs) {
			sql1.append("and  a.field0006 like'%" + tbbgs + "%'");
		}
		// 根据部门查询
		if (!department.equals("") && null != department) {
			sql1.append(
					"and  (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, '')) like'%"
							+ department + "%'");
		}
		// 根据客户类型查询即当前状态：预立案客户、目标客户、曾经客户、现立案客户(新网暂时查不到)
		// if(!khstyle.equals("") && null != khstyle){
		// sql.append("and latime ='"+ latime + "'");
		// }
		// 根据客户级别查询(新网暂时查不到)
		// if(!khjb.equals("") && null != khjb){
		// sql.append("and state ='"+ khjb + "'");
		// }
		// 客户主营业务查询(新网暂时查不到新网暂时查不到 select a.id, a.ref_enumid,a.showvalue,
		// dqztb.field0001 from CTP_ENUM_ITEM a left join formmain_0226 dqztb on
		// cast(a.id as varchar(100)) = dqztb.field0013)
		// if(!ahmainyw.equals("") && null != ahmainyw){
		// sql.append("and state ='"+ ahmainyw + "'");
		// }
		// 根据地域查询(新网暂时查不到)
		// if(!dycity.equals("") && null != dycity){
		// sql.append("and state ='"+ dycity + "'");
		// }
		// 根据案件状态查询
		// if (!ajstate.equals("") && null != ajstate) {
		// sql1.append("and a.state like '%" + ajstate + "%'");
		// }
		// 根据主办律师查询
		if (!zblawyer.equals("") && null != zblawyer) {
			sql1.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, ''))  like'%"
							+ zblawyer + "%'");
		}
		// 安源人查询
		if (!ayperson.equals("") && null != ayperson) {
			sql1.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0013)>0 for xml path('')), 1, 1, ''))  like'%"
							+ ayperson + "%'");
		}

		// 非诉业务
		List<CaseInf> newlist2 = new ArrayList<CaseInf>();

		ResultSet rs2 = null;
		StringBuffer sql2 = new StringBuffer(
				"select top 20    a.state as shstate , a.start_member_id as fqname, a.start_date as fqtime, a.approve_member_id as shname, ");
		sql2.append(
				" a.approve_date as shtime, a.finishedflag as lcstate,a.ratifyflag as hdstate, a.ratify_member_id as hdname, ");
		sql2.append(
				" a.ratify_date as hdtime,a.field0001 as ajbh, a.field0002 as ajmc,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, ''))   as zblawyer,");
		sql2.append(
				" (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0004)>0 for xml path('')), 1, 1, ''))  as tbname, ");
		sql2.append(
				" (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, ''))  as department,    bgs.name as tbbgs ,'非诉业务'  as ajlb,a.field0008, a.field0055, a.field0056,");
		sql2.append(
				" CONVERT(varchar(100),a.field0009, 23) as latime,'' as ajbde,'' as sfsytsaj,'' as sj , (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, ''))  as ayperson, a.field0011 as ajjk, a.field0012 as hylb , a.field0013 as jffs, ");
		sql2.append(
				" a.field0014 as jmsf,(stuff((select ',' + cast(c.field0038 as varchar(100)) from formson_0281 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as dfname,(stuff((select ',' + cast(c.field0037 as varchar(100)) from formson_0280 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as wtname,(stuff((select ',' + c.NAME from org_member c left join ( select distinct  formmain_id,field0039 , field0041,field0040  from formson_0282  )  d on a.id=d.formmain_id  where charindex(cast(c.id as varchar(100)),d.field0039)>0 for xml path('')), 1, 1, '')) as zyfname,(stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l left join ( select distinct  formmain_id,field0039 , field0041,field0040  from formson_0282  )  d on a.id=d.formmain_id  where charindex(cast(l.department_id as varchar(100)),d.field0040)>0 for xml path('')), 1, 1, ''))  as zyfdepartment , ");
		sql2.append(
				" (stuff((select ',' + cast(c.field0041 as varchar(100)) from formson_0282 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as zyfbl ,'' as state    ");
		// sql2.append(" e.field0042 as jfjebzhong, e.field0043 as jfjesdlf
		// ,e.field0044 as qt , e.field0045 as jfjestarttime ,e.field0046 as
		// jfjeendtime , e.field0047 as jfjebz , ");
		// 非诉-参与承揽与经办人员及律师费分配重复表
		// sql2.append(" f.field0048 as cyclname, f.field0049 as cycldepartment
		// , f.field0050 as cyclbgs, f.field0051 as lsf , f.field0058 as lsflx
		// ,f.field0059 as fh , ");
		// sql2.append(" g.field0052 as xzname ,g.field0053 as xzdepartment
		// ,g.field0054 as xzbgs , ");
		// sql2.append(" h.field0062 as wslsname , h.field0063 as lxfs
		// ,h.field0064 as sheng ,h.field0065 as shi,h.field0066 as lsname , ''
		// as state ");
		sql2.append(" from formmain_0279 a  ");

		sql2.append(
				" left join ( select distinct  field0041  from formmain_0320 ) jab on a.field0001=jab.field0041   ");
		// sql2.append(" left join ( select distinct formmain_id, field0037 from
		// formson_0280 ) b on a.id=b.formmain_id ");
		// sql2.append(" left join ( select distinct formmain_id, field0038 from
		// formson_0281 ) c on a.id=c.formmain_id ");

		// sql2.append(" left join ( select distinct formmain_id, field0042 ,
		// field0043 ,field0044 , field0045 , field0046,field0047 from
		// formson_0283 ) e on a.id=e.formmain_id ");
		// 非诉-参与承揽与经办人员及律师费分配重复表
		// sql2.append(" left join ( select distinct formmain_id, field0050
		// ,field0051 ,field0058 ,field0059 ,field0048,field0049 from
		// formson_0284 ) f on a.id=f.formmain_id ");
		// sql2.append(" left join ( select distinct formmain_id,
		// field0052,field0053, field0054 from formson_0285 ) g on
		// a.id=g.formmain_id ");
		// sql2.append(" left join ( select distinct formmain_id, field0062
		// ,field0063 ,field0064 ,field0065 ,field0066 from formson_1361 ) h on
		// a.id=h.formmain_id ");
		// sql2.append(" left join (select distinct department_id,
		// department_name from RPT_MATTER_REPORT ) dep on dep.department_id =
		// a.field0005");
		sql2.append(" left join v3x_org_department bgs on bgs.id =  a.field0006 ");
		sql2.append("   where   a.field0001  !='0'   ");

		// 根据客户名称查询
		if (!kehuname.equals("") && null != kehuname) {
			sql2.append(
					"and  (stuff((select ',' + cast(c.field0037 as varchar(100)) from formson_0280 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) like'%"
							+ kehuname + "%'");
		}
		// 根据立案时间查询
		if (!latime1.equals("") && null != latime1) {
			sql2.append("and  a.field0009 >='" + latime1 + "'");
		}
		if (!latime2.equals("") && null != latime2) {
			sql2.append("and  a.field0009 <='" + latime2 + "'");
		}

		// 根据案件编号查询
		if (!ajbh.equals("") && null != ajbh) {
			sql2.append("and  a.field0001 like'%" + ajbh + "%'");
		}

		// 根据行业类别查询
		if (!hylb.equals("") && null != hylb) {
			sql2.append("and  a.field0012 like'%" + hylb + "%'");
		}
		// 根据填报办公室查询
		if (!tbbgs.equals("") && null != tbbgs) {
			sql2.append("and  a.field0006 like'%" + tbbgs + "%'");
		}
		// 根据部门查询
		if (!department.equals("") && null != department) {
			sql2.append(
					"and (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, '')) like'%"
							+ department + "%'");
		}
		// 根据客户类型查询(新网暂时查不到)
		// if(!khstyle.equals("") && null != khstyle){
		// sql2.append("and latime ='"+ latime + "'");
		// }

		// 根据客户级别查询(新网暂时查不到)
		// if(!khjb.equals("") && null != khjb){
		// sql2.append("and state ='"+ khjb + "'");
		// }
		// 客户主营业务查询(新网暂时查不到)
		// if(!ahmainyw.equals("") && null != ahmainyw){
		// sql2.append("and state ='"+ ahmainyw + "'");
		// }
		// 根据地域查询(新网暂时查不到)
		// if(!dycity.equals("") && null != dycity){
		// sql2.append("and state ='"+ dycity + "'");
		// }

		// 根据案件状态查询
		// if (!ajstate.equals("") && null != ajstate) {
		// sql2.append("and a.state like '%" + ajstate + "%'");
		// }
		// 根据主办律师查询
		if (!zblawyer.equals("") && null != zblawyer) {
			sql2.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, '')) like'%"
							+ zblawyer + "%'");
		}
		// 安源人查询
		if (!ayperson.equals("") && null != ayperson) {
			sql2.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, '')) like'%"
							+ ayperson + "%'");
		}

		// 刑事
		List<CaseInf> newlist3 = new ArrayList<CaseInf>();

		ResultSet rs3 = null;
		StringBuffer sql3 = new StringBuffer(
				"select top 20  a.state as shstate , a.start_member_id   as fqname, a.start_date as fqtime, ");
		sql3.append("a.approve_member_id as shname,    a.approve_date as shtime,   a.finishedflag as lcstate,  ");
		sql3.append("a.ratifyflag as hdstate,  a.ratify_member_id as hdname, a.ratify_date as hdtime,  ");
		sql3.append(
				"a.field0001 as ajbh,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0002)>0 for xml path('')), 1, 1, '')) as tbname,  (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, ''))  as department  ,bgs.name as tbbgs ,  ");
		sql3.append(
				"'刑事' as   ajlb, a.field0006,a.field0063, a.field0064, CONVERT(varchar(100),a.field0007, 23)  as latime,  '' as ajbde,'' as sfsytsaj , ");
		sql3.append(
				"a.field0008  as ajmc ,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0009)>0 for xml path('')), 1, 1, ''))  as zblawyer ,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, ''))  as ayperson,   ");
		sql3.append("a.field0011 as dsrfzxyr, a.field0012 as dsrbgr,a.field0013 as dsrbhr,a.field0014 as sfwcnaj,   ");
		sql3.append("a.field0015 as sfgjfry, a.field0016 as sftsaj,  a.field0017 as ay, sjb.showvalue as sj,    ");
		sql3.append("a.field0019 as ajjk,a.field0020 as hylb,a.field0021 as jffs,a.field0022 as jmsf, ");
		sql3.append(
				"(stuff((select ',' + cast(c.field0046 as varchar(100)) from formson_0296 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as dfname, (stuff((select ',' + cast(c.field0045 as varchar(100)) from formson_0295 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as wtname,   ");
		sql3.append(
				"(stuff((select ',' + c.NAME from org_member c left join ( select distinct  formmain_id, field0047,field0048,field0049  from  formson_0297 )   d on a.id=d.formmain_id  where charindex(cast(c.id as varchar(100)),d.field0047)>0 for xml path('')), 1, 1, '')) as zyfname, (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l left join ( select distinct  formmain_id, field0047,field0048,field0049  from  formson_0297 )   d on a.id=d.formmain_id  where charindex(cast(l.department_id as varchar(100)),d.field0048)>0 for xml path('')), 1, 1, ''))  as zyfdepartment ,(stuff((select ',' + cast(c.field0049 as varchar(100)) from formson_0297 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as zyfbl,  '' as state   ");
		// sql3.append("e.field0050 as jfjebzhong, e.field0051 as jfjesdlf
		// ,e.field0052 as qt , e.field0053 as jfjestarttime , ");
		// sql3.append("e.field0054 as jfjeendtime , e.field0055 as jfjebz, ");
		// sql3.append("f.field0056 as cyclname, f.field0057 as cycldepartment ,
		// f.field0058 as cyclbgs, f.field0059 as lsf , ");
		// sql3.append("f.field0066 as lsflx ,f.field0067 as fh ,f.field0068 as
		// lsflx1 , ");
		// sql3.append("g.field0060 as xzname ,g.field0061 as xzdepartment
		// ,g.field0062 as xzbgs , ");
		// sql3.append("h.field0072 as wslsname , h.field0073 as lxfs
		// ,h.field0074 as sheng ,h.field0075 as shi,h.field0076 as lsname , ''
		// as state ");
		sql3.append("from formmain_0294 a  ");
		sql3.append("left join CTP_ENUM_ITEM sjb on  a.field0018=sjb.id  ");
		sql3.append("left join ( select distinct field0045 from  formmain_0324 )  k on a.field0001=k.field0045  ");
		// sql3.append("left join ( select distinct formmain_id, field0046 from
		// formson_0296 ) b on a.id=b.formmain_id ");
		// sql3.append("left join ( select distinct formmain_id, field0045 from
		// formson_0295 ) c on a.id=c.formmain_id ");

		// sql3.append("left join ( select distinct formmain_id, field0050
		// ,field0051,field0052,field0053,field0054 ,field0055 from formson_0298
		// ) e on a.id=e.formmain_id ");
		// 刑事-参与承揽与经办人员及律师费分配重复表
		// sql3.append("left join ( select distinct formmain_id, field0056
		// ,field0057 ,field0058,field0059 ,field0066,field0067,field0068 from
		// formson_0299 ) f on a .id=f.formmain_id ");
		// sql3.append("left join ( select distinct formmain_id,
		// field0060,field0061,field0062 from formson_0300 ) g on
		// a.id=g.formmain_id ");
		// sql3.append("left join ( select distinct formmain_id,
		// field0072,field0073,field0074 ,field0075,field0076 from formson_1377
		// ) h on a.id=h.formmain_id ");
		// sql3.append(" left join (select distinct department_id,
		// department_name from RPT_MATTER_REPORT ) dep on dep.department_id =
		// a.field0003");
		sql3.append(" left join v3x_org_department bgs on bgs.id =  a.field0004 ");
		sql3.append("   where  a.field0001  !='0'  ");
		// 根据客户名称查询
		if (!kehuname.equals("") && null != kehuname) {
			sql3.append(
					"and  (stuff((select ',' + cast(c.field0045 as varchar(100)) from formson_0295 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) like '%"
							+ kehuname + "%' ");
		}
		// 根据立案时间查询
		if (!latime1.equals("") && null != latime1) {
			sql3.append("and  a.field0007 >='" + latime1 + "'");
		}
		if (!latime2.equals("") && null != latime2) {
			sql3.append("and  a.field0007 <='" + latime2 + "'");
		}
		// 根据案件编号查询
		if (!ajbh.equals("") && null != ajbh) {
			sql3.append("and  a.field0001 like'%" + ajbh + "%'");
		}

		// 根据行业类别查询
		if (!hylb.equals("") && null != hylb) {
			sql3.append("and  a.field0020 like'%" + hylb + "%'");
		}
		// 根据填报办公室查询
		if (!tbbgs.equals("") && null != tbbgs) {
			sql3.append("and  a.field0004 like'%" + tbbgs + "%'");
		}
		// 根据部门查询
		if (!department.equals("") && null != department) {
			sql3.append(
					"and   (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, '')) like'%"
							+ department + "%'");
		}
		// 根据客户类型查询(新网暂时查不到)
		// if(!khstyle.equals("") && null != khstyle){
		// sql3.append("and latime ='"+ latime + "'");
		// }
		// 根据地域查询(新网暂时查不到)
		// if(!dycity.equals("") && null != dycity){
		// sql3.append("and state ='"+ dycity + "'");
		// }
		// 根据客户级别查询(新网暂时查不到)
		// if(!khjb.equals("") && null != khjb){
		// sql3.append("and state ='"+ khjb + "'");
		// }
		// 客户主营业务查询(新网暂时查不到)
		// if(!ahmainyw.equals("") && null != ahmainyw){
		// sql3.append("and state ='"+ ahmainyw + "'");
		// }
		// 根据案件状态查询
		// if (!ajstate.equals("") && null != ajstate) {
		// sql3.append("and a.state like '%" + ajstate + "%'");
		// }
		// 安源人查询
		if (!ayperson.equals("") && null != ayperson) {
			sql3.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, '')) like'%"
							+ ayperson + "%'");
		}
		// 根据主办律师查询
		if (!zblawyer.equals("") && null != zblawyer) {
			sql3.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0009)>0 for xml path('')), 1, 1, '')) like'%"
							+ zblawyer + "%'");
		}

		// 法律顾问
		List<CaseInf> newlist4 = new ArrayList<CaseInf>();

		ResultSet rs4 = null;
		StringBuffer sql4 = new StringBuffer(
				"select top 20   a.state as shstate ,  a.start_member_id as fqname,  a.start_date  as fqtime,  ");
		sql4.append("a.approve_member_id as shname, a.approve_date as shtime,a.finishedflag as lcstate,   ");
		sql4.append("a.ratifyflag  as hdstate, a.ratify_member_id  as hdname,a.ratify_date  as hdtime,  ");
		sql4.append(
				"a.field0002  as ajbh,a.field0001  as ajmc,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, ''))   as zblawyer,  ");
		sql4.append(
				"(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0004)>0 for xml path('')), 1, 1, '')) as tbname,(stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, ''))  as department, bgs.name  as tbbgs ,   ");
		sql4.append("'法律顾问' as ajlb,     a.field0008,      a.field0056,      a.field0057,   ");
		sql4.append(
				"CONVERT(varchar(100),a.field0009, 23)  as latime,'' as ajbde,'' as sfsytsaj,'' as sj ,(stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, '')) as ayperson,a.field0011 as gwstarttime , a.field0012 as gwendtime ,  ");
		sql4.append(
				"a.field0013 as ajjk,  a.field0014 as hylb,a.field0015 as jffs,  a.field0016 as jmsf, ''  as dfname,");
		sql4.append(
				" (stuff((select ',' + cast(c.field0039 as varchar(100)) from formson_0274 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as wtname,(stuff((select ',' + c.NAME from org_member c  left join ( select distinct  formmain_id, field0040 ,field0042,field0041  from formson_0275 ) d on a.id=d.formmain_id  where charindex(cast(c.id as varchar(100)),d.field0040)>0 for xml path('')), 1, 1, '')) as zyfname, (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l left join ( select distinct  formmain_id, field0040 ,field0042,field0041  from formson_0275 ) d on a.id=d.formmain_id  where charindex(cast(l.department_id as varchar(100)),d.field0041)>0 for xml path('')), 1, 1, ''))  as zyfdepartment ,(stuff((select ',' + cast(c.field0042 as varchar(100)) from formson_0275 c  where a.id=c.formmain_id   for xml path('')), 1, 1, '')) as zyfbl ,  '' as state   ");
		// sql4.append("e.field0043 as jfjebzhong, e.field0044 as jfjesdlf
		// ,e.field0045 as qt , e.field0046 as jfjestarttime , ");
		// sql4.append("e.field0047 as jfjeendtime , e.field0048 as jfjebz , ");
		// sql4.append("f.field0049 as cyclname, f.field0050 as cycldepartment ,
		// f.field0051 as cyclbgs, f.field0052 as lsf , ");
		// sql4.append("f.field0059 as lsflx ,f.field0060 as fh , ");
		// sql4.append("g.field0053 as xzname , g.field0054 as xzdepartment
		// ,g.field0055 as xzbgs ,h.field0063 as wslsname , ");
		// sql4.append("h.field0064 as lxfs ,h.field0065 as sheng ,h.field0066
		// as shi,h.field0067 as lsname , ");
		sql4.append(" from formmain_0273 a  ");

		sql4.append("left join ( select distinct field0041 from  formmain_0317  ) k on a.field0002=k.field0041   ");
		// sql4.append("left join ( select distinct formmain_id,field0039 from
		// formson_0274 ) c on a.id=c.formmain_id ");
		// 法律-计费金额重复表
		// sql4.append("left join ( select distinct formmain_id,
		// field0043,field0044 ,field0045,field0046,field0047 ,field0048 from
		// formson_0276 ) e on a.id=e.formmain_id ");
		// 法律-参与承揽与经办人员及律师费分配重复表
		// sql4.append("left join ( select distinct formmain_id, field0049
		// ,field0050, field0051 ,field0052 ,field0059 ,field0060 from
		// formson_0277 ) f on a.id=f.formmain_id ");
		// 协助人员重复表
		// sql4.append("left join ( select distinct formmain_id, field0053
		// ,field0054,field0055 from formson_0278 ) g on a.id=g.formmain_id ");
		// 外所合作律师重复表
		// sql4.append("left join ( select distinct formmain_id, field0063
		// ,field0064 ,field0065 ,field0066 ,field0067 from formson_1354 ) h on
		// a.id=h.formmain_id ");
		// sql4.append(" left join (select distinct department_id,
		// department_name from RPT_MATTER_REPORT ) dep on dep.department_id =
		// a.field0005");
		sql4.append(" left join v3x_org_department bgs on bgs.id =  a.field0006 ");
		sql4.append("  where  a.field0002  !='0' ");

		// 根据客户名称查询
		if (!kehuname.equals("") && null != kehuname) {
			sql4.append(
					"and  (stuff((select ',' + cast(c.field0039 as varchar(100)) from formson_0274 c  where a.id=c.formmain_id   for xml path('')), 1, 1, ''))   like '%"
							+ kehuname + "%' ");
		}
		// 根据立案时间查询
		if (!latime1.equals("") && null != latime1) {
			sql4.append("and  a.field0009 >='" + latime1 + "'");
		}
		if (!latime2.equals("") && null != latime2) {
			sql4.append("and  a.field0009 <='" + latime2 + "'");
		}

		// 根据案件编号查询
		if (!ajbh.equals("") && null != ajbh) {
			sql4.append("and  a.field0002 like '%" + ajbh + "%'");
		}

		// 根据行业类别查询
		if (!hylb.equals("") && null != hylb) {
			sql4.append("and  a.field0014 like'%" + hylb + "%'");
		}
		// 根据填报办公室查询
		if (!tbbgs.equals("") && null != tbbgs) {
			sql4.append("and  a.field0006 like'%" + tbbgs + "%'");
		}
		// 根据部门查询
		if (!department.equals("") && null != department) {
			sql4.append(
					"and  (stuff((select distinct ',' + l.department_name from RPT_MATTER_REPORT l where charindex(cast(l.department_id as varchar(100)),a.field0005)>0 for xml path('')), 1, 1, '')) like'%"
							+ department + "%'");
		}
		// 根据客户级别查询(新网暂时查不到)
		// if(!khjb.equals("") && null != khjb){
		// sql4.append("and state ='"+ khjb + "'");
		// }
		// 客户主营业务查询(新网暂时查不到)
		// if(!ahmainyw.equals("") && null != ahmainyw){
		// sql4.append("and state ='"+ ahmainyw + "'");
		// }
		// 根据客户类型查询(新网暂时查不到)
		// if(!khstyle.equals("") && null != khstyle){
		// sql4.append("and latime ='"+ latime + "'");
		// }
		// 根据地域查询(新网暂时查不到)
		// if(!dycity.equals("") && null != dycity){
		// sql4.append("and state ='"+ dycity + "'");
		// }

		// 根据案件状态查询
		// if (!ajstate.equals("") && null != ajstate) {
		// sql4.append("and a.state like '%" + ajstate + "%'");
		// }
		// 根据主办律师查询
		if (!zblawyer.equals("") && null != zblawyer) {
			sql4.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0003)>0 for xml path('')), 1, 1, '')) like'%"
							+ zblawyer + "%'");
		}
		// 安源人查询
		if (!ayperson.equals("") && null != ayperson) {
			sql4.append(
					"and  (stuff((select ',' + c.NAME from org_member c where charindex(cast(c.id as varchar(100)),a.field0010)>0 for xml path('')), 1, 1, '')) like'%"
							+ ayperson + "%'");
		}

		// rs1 = stmt1.executeQuery(sql1.toString());

		try {
			int tempi1 = dba1.execute(sql1.toString());
			int tempi2 = dba2.execute(sql2.toString());
			int tempi3 = dba3.execute(sql3.toString());
			int tempi4 = dba4.execute(sql4.toString());
			if (tempi1 > 0) {

				rs1 = dba1.getQueryResult();
				ResultSetMetaData rsmd1 = rs1.getMetaData();
				int numberOfColumns1 = rsmd1.getColumnCount();
				while (rs1.next()) {

					rsTree1 = new CaseInf();// 注意要new
					for (int r = 1; r < numberOfColumns1 + 1; r++) {

						// rsTree1.put(rsmd1.getColumnName(r),rs1.getObject(r));
						// if(rsmd1.getColumnName(r-1).equals("zblawyer")){System.out.println(rs1.get(r));}

						if (rsmd1.getColumnName(r).equals("ahmainyw")) {
							rsTree1.setAhmainyw(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("khstyle")) {
							rsTree1.setKhstyle(rs1.getObject(r));
						}
						// if(dbu.getColumnName(r).equals("state")){rsTree1.setState(dbu.getObject(r));System.out.println("state"+dbu.getObject(r));}
						if (rsmd1.getColumnName(r).equals("shstate")) {
							rsTree1.setShstate(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("fqname")) {
							rsTree1.setFqname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("fqtime")) {
							rsTree1.setFqtime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("shname")) {
							rsTree1.setShname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("shtime")) {
							rsTree1.setShtime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("lcstate")) {
							rsTree1.setLcstate(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("hdstate")) {
							rsTree1.setHdstate(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("hdname")) {
							rsTree1.setHdname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("hdtime")) {
							rsTree1.setHdtime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ajbh")) {
							rsTree1.setAjbh(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ajmc")) {
							rsTree1.setAjmc(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("zblawyer")) {
							rsTree1.setZblawyer(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("tbname")) {
							rsTree1.setTbname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("department")) {
							rsTree1.setDepartment(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("tbbgs")) {
							rsTree1.setTbbgs(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ajlb")) {
							rsTree1.setAjlb(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("latime")) {
							rsTree1.setLatime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ajbde")) {
							rsTree1.setAjbde(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("sfsytsaj")) {
							rsTree1.setSfsytsaj(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("sj")) {
							rsTree1.setSj(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ayperson")) {
							rsTree1.setAyperson(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("ajjk")) {
							rsTree1.setAjjk(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("hylb")) {
							rsTree1.setHylb(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jffs")) {
							rsTree1.setJffs(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jmsf")) {
							rsTree1.setJmsf(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("dfname")) {
							rsTree1.setDfname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("wtname")) {
							rsTree1.setWtname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("zyfname")) {
							rsTree1.setZyfname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("zyfdepartment")) {
							rsTree1.setZyfdepartment(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("zyfbl")) {
							rsTree1.setZyfbl(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jfjebzhong")) {
							rsTree1.setJfjebzhong(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jfjesdlf")) {
							rsTree1.setJfjesdlf(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("qt")) {
							rsTree1.setQt(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jfjestarttime")) {
							rsTree1.setJfjestarttime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jfjeendtime")) {
							rsTree1.setJfjeendtime(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("jfjebz")) {
							rsTree1.setJfjebz(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("cyclname")) {
							rsTree1.setCyclname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("cycldepartment")) {
							rsTree1.setCycldepartment(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("cyclbgs")) {
							rsTree1.setCyclbgs(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("lsf")) {
							rsTree1.setLsf(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("lsflx")) {
							rsTree1.setLsflx(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("fh")) {
							rsTree1.setFh(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("xzname")) {
							rsTree1.setXzname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("xzdepartment")) {
							rsTree1.setXzdepartment(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("xzbgs")) {
							rsTree1.setXzbgs(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("wslsname")) {
							rsTree1.setWslsname(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("lxfs")) {
							rsTree1.setLxfs(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("sheng")) {
							rsTree1.setSheng(rs1.getObject(r));
						}
						if (rsmd1.getColumnName(r).equals("lsname")) {
							rsTree1.setLsname(rs1.getObject(r));
						}

						if (dbu.getColumnName(r).equals("state")) {
							break;
						}
					}

					newlist1.add(rsTree1);

				}

				String newajlb = (String) ajlb;
				if (newajlb.equals("") || null == newajlb || newajlb.contains("诉讼") || newajlb.contains("仲裁")) {

					list.addAll(newlist1);

				}
			}

			if (tempi2 > 0) {
				rs2 = dba2.getQueryResult();
				ResultSetMetaData rsmd2 = rs2.getMetaData();
				int numberOfColumns2 = rsmd2.getColumnCount();
				while (rs2.next()) {
					rsTree2 = new CaseInf();// 注意要new
					for (int r = 1; r < numberOfColumns2 + 1; r++) {
						// System.out.println(rsmd2.getColumnName(r));
						// rsTree2.put(rsmd2.getColumnName(r),
						// rs2.getObject(r));

						if (rsmd2.getColumnName(r).equals("ahmainyw")) {
							rsTree2.setAhmainyw(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("khstyle")) {
							rsTree2.setKhstyle(rs2.getObject(r));
						}
						// if(dbu.getColumnName(r).equals("state")){rsTree1.setState(dbu.getObject(r));System.out.println("state"+dbu.getObject(r));}
						if (rsmd2.getColumnName(r).equals("shstate")) {
							rsTree2.setShstate(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("fqname")) {
							rsTree2.setFqname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("fqtime")) {
							rsTree2.setFqtime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("shname")) {
							rsTree2.setShname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("shtime")) {
							rsTree2.setShtime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("lcstate")) {
							rsTree2.setLcstate(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("hdstate")) {
							rsTree2.setHdstate(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("hdname")) {
							rsTree2.setHdname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("hdtime")) {
							rsTree2.setHdtime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ajbh")) {
							rsTree2.setAjbh(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ajmc")) {
							rsTree2.setAjmc(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("zblawyer")) {
							rsTree2.setZblawyer(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("tbname")) {
							rsTree2.setTbname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("department")) {
							rsTree2.setDepartment(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("tbbgs")) {
							rsTree2.setTbbgs(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ajlb")) {
							rsTree2.setAjlb(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("latime")) {
							rsTree2.setLatime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ajbde")) {
							rsTree2.setAjbde(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("sfsytsaj")) {
							rsTree2.setSfsytsaj(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("sj")) {
							rsTree2.setSj(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ayperson")) {
							rsTree2.setAyperson(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("ajjk")) {
							rsTree2.setAjjk(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("hylb")) {
							rsTree2.setHylb(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jffs")) {
							rsTree2.setJffs(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jmsf")) {
							rsTree2.setJmsf(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("dfname")) {
							rsTree2.setDfname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("wtname")) {
							rsTree2.setWtname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("zyfname")) {
							rsTree2.setZyfname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("zyfdepartment")) {
							rsTree2.setZyfdepartment(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("zyfbl")) {
							rsTree2.setZyfbl(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jfjebzhong")) {
							rsTree2.setJfjebzhong(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jfjesdlf")) {
							rsTree2.setJfjesdlf(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("qt")) {
							rsTree2.setQt(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jfjestarttime")) {
							rsTree2.setJfjestarttime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jfjeendtime")) {
							rsTree2.setJfjeendtime(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("jfjebz")) {
							rsTree2.setJfjebz(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("cyclname")) {
							rsTree2.setCyclname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("cycldepartment")) {
							rsTree2.setCycldepartment(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("cyclbgs")) {
							rsTree2.setCyclbgs(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("lsf")) {
							rsTree2.setLsf(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("lsflx")) {
							rsTree2.setLsflx(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("fh")) {
							rsTree2.setFh(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("xzname")) {
							rsTree2.setXzname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("xzdepartment")) {
							rsTree2.setXzdepartment(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("xzbgs")) {
							rsTree2.setXzbgs(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("wslsname")) {
							rsTree2.setWslsname(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("lxfs")) {
							rsTree2.setLxfs(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("sheng")) {
							rsTree2.setSheng(rs2.getObject(r));
						}
						if (rsmd2.getColumnName(r).equals("lsname")) {
							rsTree2.setLsname(rs2.getObject(r));
						}

						if (dbu.getColumnName(r).equals("state")) {
							break;
						}
					}

					newlist2.add(rsTree2);

				}
				String newajlb = (String) ajlb;
				if (newajlb.equals("") || null == newajlb || newajlb.contains("非诉")) {

					list.addAll(newlist2);
				}

			}

			if (tempi3 > 0) {
				rs3 = dba3.getQueryResult();
				ResultSetMetaData rsmd3 = rs3.getMetaData();
				int numberOfColumns3 = rsmd3.getColumnCount();
				while (rs3.next()) {
					rsTree3 = new CaseInf();// 注意要new
					for (int r = 1; r < numberOfColumns3 + 1; r++) {
						// System.out.println(rsmd2.getColumnName(r));
						// rsTree2.put(rsmd2.getColumnName(r),
						// rs2.getObject(r));

						if (rsmd3.getColumnName(r).equals("ahmainyw")) {
							rsTree3.setAhmainyw(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("khstyle")) {
							rsTree3.setKhstyle(rs3.getObject(r));
						}
						// if(dbu.getColumnName(r).equals("state")){rsTree1.setState(dbu.getObject(r));System.out.println("state"+dbu.getObject(r));}
						if (rsmd3.getColumnName(r).equals("shstate")) {
							rsTree3.setShstate(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("fqname")) {
							rsTree3.setFqname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("fqtime")) {
							rsTree3.setFqtime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("shname")) {
							rsTree3.setShname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("shtime")) {
							rsTree3.setShtime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("lcstate")) {
							rsTree3.setLcstate(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("hdstate")) {
							rsTree3.setHdstate(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("hdname")) {
							rsTree3.setHdname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("hdtime")) {
							rsTree3.setHdtime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ajbh")) {
							rsTree3.setAjbh(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ajmc")) {
							rsTree3.setAjmc(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("zblawyer")) {
							rsTree3.setZblawyer(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("tbname")) {
							rsTree3.setTbname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("department")) {
							rsTree3.setDepartment(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("tbbgs")) {
							rsTree3.setTbbgs(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ajlb")) {
							rsTree3.setAjlb(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("latime")) {
							rsTree3.setLatime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ajbde")) {
							rsTree3.setAjbde(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("sfsytsaj")) {
							rsTree3.setSfsytsaj(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("sj")) {
							rsTree3.setSj(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ayperson")) {
							rsTree3.setAyperson(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("ajjk")) {
							rsTree3.setAjjk(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("hylb")) {
							rsTree3.setHylb(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jffs")) {
							rsTree3.setJffs(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jmsf")) {
							rsTree3.setJmsf(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("dfname")) {
							rsTree3.setDfname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("wtname")) {
							rsTree3.setWtname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("zyfname")) {
							rsTree3.setZyfname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("zyfdepartment")) {
							rsTree3.setZyfdepartment(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("zyfbl")) {
							rsTree3.setZyfbl(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jfjebzhong")) {
							rsTree3.setJfjebzhong(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jfjesdlf")) {
							rsTree3.setJfjesdlf(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("qt")) {
							rsTree3.setQt(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jfjestarttime")) {
							rsTree3.setJfjestarttime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jfjeendtime")) {
							rsTree3.setJfjeendtime(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("jfjebz")) {
							rsTree3.setJfjebz(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("cyclname")) {
							rsTree3.setCyclname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("cycldepartment")) {
							rsTree3.setCycldepartment(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("cyclbgs")) {
							rsTree3.setCyclbgs(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("lsf")) {
							rsTree3.setLsf(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("lsflx")) {
							rsTree3.setLsflx(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("fh")) {
							rsTree3.setFh(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("xzname")) {
							rsTree3.setXzname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("xzdepartment")) {
							rsTree3.setXzdepartment(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("xzbgs")) {
							rsTree3.setXzbgs(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("wslsname")) {
							rsTree3.setWslsname(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("lxfs")) {
							rsTree3.setLxfs(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("sheng")) {
							rsTree3.setSheng(rs3.getObject(r));
						}
						if (rsmd3.getColumnName(r).equals("lsname")) {
							rsTree3.setLsname(rs3.getObject(r));
						}

						if (dbu.getColumnName(r).equals("state")) {
							break;
						}
					}

					newlist3.add(rsTree3);

				}
				String newajlb = (String) ajlb;
				if (newajlb.equals("") || null == newajlb || newajlb.contains("非诉")) {

					list.addAll(newlist3);
				}

			}
			if (tempi4 > 0) {
				rs4 = dba4.getQueryResult();
				ResultSetMetaData rsmd4 = rs4.getMetaData();
				int numberOfColumns4 = rsmd4.getColumnCount();
				while (rs4.next()) {
					rsTree4 = new CaseInf();// 注意要new
					for (int r = 1; r < numberOfColumns4 + 1; r++) {
						// rsTree4.put(rsmd4.getColumnName(r),rs4.getObject(r));

						if (rsmd4.getColumnName(r).equals("ahmainyw")) {
							rsTree4.setAhmainyw(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("khstyle")) {
							rsTree4.setKhstyle(rs4.getObject(r));
						}
						// if(dbu.getColumnName(r).equals("state")){rsTree1.setState(dbu.getObject(r));System.out.println("state"+dbu.getObject(r));}
						if (rsmd4.getColumnName(r).equals("shstate")) {
							rsTree4.setShstate(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("fqname")) {
							rsTree4.setFqname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("fqtime")) {
							rsTree4.setFqtime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("shname")) {
							rsTree4.setShname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("shtime")) {
							rsTree4.setShtime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("lcstate")) {
							rsTree4.setLcstate(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("hdstate")) {
							rsTree4.setHdstate(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("hdname")) {
							rsTree4.setHdname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("hdtime")) {
							rsTree4.setHdtime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ajbh")) {
							rsTree4.setAjbh(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ajmc")) {
							rsTree4.setAjmc(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("zblawyer")) {
							rsTree4.setZblawyer(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("tbname")) {
							rsTree4.setTbname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("department")) {
							rsTree4.setDepartment(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("tbbgs")) {
							rsTree4.setTbbgs(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ajlb")) {
							rsTree4.setAjlb(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("latime")) {
							rsTree4.setLatime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ajbde")) {
							rsTree4.setAjbde(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("sfsytsaj")) {
							rsTree4.setSfsytsaj(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("sj")) {
							rsTree4.setSj(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ayperson")) {
							rsTree4.setAyperson(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("ajjk")) {
							rsTree4.setAjjk(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("hylb")) {
							rsTree4.setHylb(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jffs")) {
							rsTree4.setJffs(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jmsf")) {
							rsTree4.setJmsf(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("dfname")) {
							rsTree4.setDfname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("wtname")) {
							rsTree4.setWtname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("zyfname")) {
							rsTree4.setZyfname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("zyfdepartment")) {
							rsTree4.setZyfdepartment(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("zyfbl")) {
							rsTree4.setZyfbl(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jfjebzhong")) {
							rsTree4.setJfjebzhong(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jfjesdlf")) {
							rsTree4.setJfjesdlf(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("qt")) {
							rsTree4.setQt(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jfjestarttime")) {
							rsTree4.setJfjestarttime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jfjeendtime")) {
							rsTree4.setJfjeendtime(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("jfjebz")) {
							rsTree4.setJfjebz(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("cyclname")) {
							rsTree4.setCyclname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("cycldepartment")) {
							rsTree4.setCycldepartment(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("cyclbgs")) {
							rsTree4.setCyclbgs(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("lsf")) {
							rsTree4.setLsf(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("lsflx")) {
							rsTree4.setLsflx(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("fh")) {
							rsTree4.setFh(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("xzname")) {
							rsTree4.setXzname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("xzdepartment")) {
							rsTree4.setXzdepartment(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("xzbgs")) {
							rsTree4.setXzbgs(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("wslsname")) {
							rsTree4.setWslsname(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("lxfs")) {
							rsTree4.setLxfs(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("sheng")) {
							rsTree4.setSheng(rs4.getObject(r));
						}
						if (rsmd4.getColumnName(r).equals("lsname")) {
							rsTree4.setLsname(rs4.getObject(r));
						}

						if (dbu.getColumnName(r).equals("state")) {
							break;
						}
					}
					newlist4.add(rsTree4);
				}

				String newajlb = (String) ajlb;
				if (newajlb.equals("") || null == newajlb || newajlb.contains("顾问")) {

					list.addAll(newlist4);
				}

			}
			dba1.close();
			dba2.close();
			dba3.close();
			dba4.close();
		} catch (BusinessException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return list;
	}
}
