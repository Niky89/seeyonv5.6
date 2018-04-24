package com.seeyon.apps.putonrecord.manager;

//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.ansj.domain.Term;
//import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.deheng.utils.ExcelUtils;
import com.seeyon.apps.putonrecord.dao.AnJianDao;
import com.seeyon.apps.putonrecord.po.CaseInf;
import com.seeyon.ctp.form.service.FormCacheManager;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;

/**
 * 
 * @author zxj 案件信息查询处理类
 */
public class AnJianQueryManager {

	private static final Log LOGGER = LogFactory.getLog(AnJianQueryManager.class);

	private AnJianDao anjianDao;

	private FormCacheManager formCacheManager;

	private CaseInf caseInf = new CaseInf(); // 增加的类传入anJianDao.getAllList

	private int startRow;

	// 当前页面
	private int page = 1;
	// 显示多少行
	private int rows = 20;
	// 总记录条数
	private int total;

	private Map<String, Object> params;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void dcExcel(HttpServletRequest request, HttpServletResponse response) {
		ExcelUtils excelUtils = new ExcelUtils();

		params = new HashMap<String, Object>();
		String title = "案件信息查询";

		String[] headers = { "案件编号", "主办律师", "案件名称", "填报人", "所属部门", "办公室", "案件类别", "立案时间", "案件标的额", "是否属于特殊案件", "审级",
				"案源人", "案件简况", "类别", "委托人", "对方当事人", "展业费使用分配人员姓名", "展业费使用分配人员部门", "展业费使用分配比例" };
		String[] field = { "ajbh", "zblawyer", "ajmc", "tbname", "department", "tbbgs", "ajlb", "latime", "ajbde",
				"sfsytsaj", "sj", "ayperson", "ajjk", "hylb", "wtname", "dfname", "zyfname", "zyfdepartment", "zyfbl" };

		// 导出的文件目录
		// String filePath
		// =request.getSession().getServletContext().getRealPath("");

		// 查询条件
		String typeValue = "";
		if (!"".equals(request.getParameter("kehuname"))) {
			typeValue += "客户名称：" + request.getParameter("kehuname") + " ";
		}
		if (!"".equals(request.getParameter("latime1"))) {
			typeValue += "立案时间开始时间：" + request.getParameter("latime1") + " ";
		}
		if (!"".equals(request.getParameter("latime2"))) {
			typeValue += "立案时间结束时间：" + request.getParameter("latime2") + " ";
		}
		if (!"".equals(request.getParameter("zblawyer"))) {
			typeValue += "主办律师：" + request.getParameter("zblawyer") + " ";
		}
		if (!"".equals(request.getParameter("ajbh"))) {
			typeValue += "案件编号：" + request.getParameter("ajbh") + " ";
		}
		if (!"".equals(request.getParameter("khstyle"))) {
			typeValue += "客户类型：" + request.getParameter("khstyle") + " ";
		}
		if (!"".equals(request.getParameter("ajlb"))) {
			typeValue += "案件类型：" + request.getParameter("ajlb") + " ";
		}
		if (!"".equals(request.getParameter("hylb"))) {
			typeValue += "行业类别：" + request.getParameter("hylb") + " ";
		}
		if (!"".equals(request.getParameter("ahmainyw"))) {
			typeValue += "主营业务：" + request.getParameter("ahmainyw") + " ";
		}
		if (!"".equals(request.getParameter("khjb"))) {
			typeValue += "客户级别：" + request.getParameter("khjb") + " ";
		}
		if (!"".equals(request.getParameter("ayperson"))) {
			typeValue += "案源人：" + request.getParameter("ayperson") + " ";
		}
		if (!"".equals(request.getParameter("tbbgs"))) {
			typeValue += "填报办公室：" + request.getParameter("tbbgs") + " ";
		}
		if (!"".equals(request.getParameter("dycity"))) {
			typeValue += "地域：" + request.getParameter("dycity") + " ";
		}
		if (!"".equals(request.getParameter("department"))) {
			typeValue += "部门：" + request.getParameter("department") + " ";
		}

		List<CaseInf> list = new ArrayList<CaseInf>();
		String kehuname = request.getParameter("kehuname");
		String latime1 = request.getParameter("latime1");
		String latime2 = request.getParameter("latime2");
		String zblawyer = request.getParameter("zblawyer");
		String ajbh = request.getParameter("ajbh");
		String khstyle = request.getParameter("khstyle");
		String ajlb = request.getParameter("ajlb");

		String hylb = request.getParameter("hylb");

		String ahmainyw = request.getParameter("ahmainyw");
		String ayperson = request.getParameter("ayperson");
		String tbbgs = request.getParameter("tbbgs");
		String dycity = request.getParameter("dycity");
		String department = request.getParameter("department");
		if (kehuname == null) {
			params.put("kehuname", "");
		} else {
			params.put("kehuname", kehuname);
		}
		if (latime1 == null) {
			params.put("latime1", "");
		} else {
			params.put("latime1", latime1);
		}
		if (latime2 == null) {
			params.put("latime2", "");
		} else {
			params.put("latime2", latime2);
		}
		if (zblawyer == null) {
			params.put("zblawyer", "");
		} else {
			params.put("zblawyer", zblawyer);
		}
		if (ajbh == null) {
			params.put("ajbh", "");
		} else {
			params.put("ajbh", ajbh);
		}
		if (khstyle == null) {
			params.put("khstyle", "");
		} else {
			params.put("khstyle", khstyle);
		}
		if (ajlb == null) {
			params.put("ajlb", "");
		} else {
			params.put("ajlb", ajlb);
		}
		if (hylb == null) {
			params.put("hylb", "");
		} else {
			params.put("hylb", hylb);
		}
		if (ahmainyw == null) {
			params.put("ahmainyw", "");
		} else {
			params.put("ahmainyw", ahmainyw);
		}
		if (ayperson == null) {
			params.put("ayperson", "");
		} else {
			params.put("ayperson", ayperson);
		}
		if (tbbgs == null) {
			params.put("tbbgs", "");
		} else {
			params.put("tbbgs", tbbgs);
		}
		if (dycity == null) {
			params.put("dycity", "");
		} else {
			params.put("dycity", dycity);
		}
		if (department == null) {
			params.put("department", "");
		} else {
			params.put("department", department);
		}

		// 增加的部分针对anjianDao.getAllList设置的实体类

		if (kehuname == null) {
			caseInf.setKehuname("");
		} else {
			caseInf.setKehuname(kehuname);
		}
		if (latime1 == null) {
			caseInf.setLatime1("");
		} else {
			caseInf.setLatime1(latime1);
		}
		if (latime2 == null) {
			caseInf.setLatime2("");
		} else {
			caseInf.setLatime2(latime2);
		}
		if (zblawyer == null) {
			caseInf.setZblawyer("");
		} else {
			caseInf.setZblawyer(zblawyer);
		}
		if (ajbh == null) {
			caseInf.setAjbh("");
		} else {
			caseInf.setAjbh(ajbh);
		}
		if (khstyle == null) {
			caseInf.setKhstyle("");
		} else {
			caseInf.setKhstyle(khstyle);
		}
		if (ajlb == null) {
			caseInf.setAjlb("");
		} else {
			caseInf.setAjlb(ajlb);
		}
		if (hylb == null) {
			caseInf.setHylb("");
		} else {
			caseInf.setHylb(hylb);
		}
		if (ahmainyw == null) {
			caseInf.setAhmainyw("");
		} else {
			caseInf.setAhmainyw(ahmainyw);
		}
		if (ayperson == null) {
			caseInf.setAyperson("");
		} else {
			caseInf.setAyperson(ayperson);
		}
		if (tbbgs == null) {
			caseInf.setTbbgs("");
		} else {
			caseInf.setTbbgs(tbbgs);
		}
		if (dycity == null) {
			caseInf.setDycity("");
		} else {
			caseInf.setDycity(dycity);
		}
		if (department == null) {
			caseInf.setDepartment("");
		} else {
			caseInf.setDepartment(department);
		}

		// System.out.println(" params excel ========"+params);
		list = anjianDao.getAllList(caseInf);// 这个list存的是CaseInf实体类

		List<Map<String, Object>> newlist = null;// 存储Map类型的新的List
		// 从老list中取出实体类转存为Map，并重新放回List
		for (int i = 0; i < list.size(); i++) {
			CaseInf ci = list.get(i);
			Map<String, Object> newmap = null;// 新的map来替代实体类存放

			if (ci.getAhmainyw() != null) {
				newmap.put("ahmainyw", ci.getAhmainyw());
			}
			if (ci.getKhstyle() != null) {
				newmap.put("khstyle", ci.getKhstyle());
			}
			// if(ci.getState()!=null){newmap.put("state",ci.getState());}
			if (ci.getShstate() != null) {
				newmap.put("shstate", ci.getShstate());
			}
			if (ci.getFqname() != null) {
				newmap.put("fqname", ci.getFqname());
			}
			if (ci.getFqtime() != null) {
				newmap.put("fqtime", ci.getFqtime());
			}
			if (ci.getShname() != null) {
				newmap.put("shname", ci.getShname());
			}
			if (ci.getShtime() != null) {
				newmap.put("shtime", ci.getShtime());
			}
			if (ci.getLcstate() != null) {
				newmap.put("lcstate", ci.getLcstate());
			}
			if (ci.getHdstate() != null) {
				newmap.put("hdstate", ci.getHdstate());
			}
			if (ci.getHdname() != null) {
				newmap.put("hdname", ci.getHdname());
			}
			if (ci.getHdtime() != null) {
				newmap.put("hdtime", ci.getHdtime());
			}
			if (ci.getAjbh() != null) {
				newmap.put("ajbh", ci.getAjbh());
			}
			if (ci.getAjmc() != null) {
				newmap.put("ajmc", ci.getAjmc());
			}
			if (ci.getZblawyer() != null) {
				newmap.put("zblawyer", ci.getZblawyer());
			}
			if (ci.getTbname() != null) {
				newmap.put("tbname", ci.getTbname());
			}
			if (ci.getDepartment() != null) {
				newmap.put("department", ci.getDepartment());
			}
			if (ci.getTbbgs() != null) {
				newmap.put("tbbgs", ci.getTbbgs());
			}
			if (ci.getAjlb() != null) {
				newmap.put("ajlb", ci.getAjlb());
			}
			if (ci.getLatime() != null) {
				newmap.put("latime", ci.getLatime());
			}
			if (ci.getAjbde() != null) {
				newmap.put("ajbde", ci.getAjbde());
			}
			if (ci.getSfsytsaj() != null) {
				newmap.put("sfsytsaj", ci.getSfsytsaj());
			}
			if (ci.getSj() != null) {
				newmap.put("sj", ci.getSj());
			}
			if (ci.getAyperson() != null) {
				newmap.put("ayperson", ci.getAyperson());
			}
			if (ci.getAjjk() != null) {
				newmap.put("ajjk", ci.getAjjk());
			}
			if (ci.getHylb() != null) {
				newmap.put("hylb", ci.getHylb());
			}
			if (ci.getJffs() != null) {
				newmap.put("jffs", ci.getJffs());
			}
			if (ci.getJmsf() != null) {
				newmap.put("jmsf", ci.getJmsf());
			}
			if (ci.getDfname() != null) {
				newmap.put("dfname", ci.getDfname());
			}
			if (ci.getWtname() != null) {
				newmap.put("wtname", ci.getWtname());
			}
			if (ci.getZyfname() != null) {
				newmap.put("zyfname", ci.getZyfname());
			}
			if (ci.getZyfdepartment() != null) {
				newmap.put("zyfdepartment", ci.getZyfdepartment());
			}
			if (ci.getZyfbl() != null) {
				newmap.put("zyfbl", ci.getZyfbl());
			}
			if (ci.getJfjebzhong() != null) {
				newmap.put("jfjebzhong", ci.getJfjebzhong());
			}
			if (ci.getJfjesdlf() != null) {
				newmap.put("jfjesdlf", ci.getJfjesdlf());
			}
			if (ci.getQt() != null) {
				newmap.put("qt", ci.getQt());
			}
			if (ci.getJfjestarttime() != null) {
				newmap.put("jfjestarttime", ci.getJfjestarttime());
			}
			if (ci.getJfjeendtime() != null) {
				newmap.put("jfjeendtime", ci.getJfjeendtime());
			}
			if (ci.getJfjebz() != null) {
				newmap.put("jfjebz", ci.getJfjebz());
			}
			if (ci.getCyclname() != null) {
				newmap.put("cyclname", ci.getCyclname());
			}
			if (ci.getCycldepartment() != null) {
				newmap.put("cycldepartment", ci.getCycldepartment());
			}
			if (ci.getCyclbgs() != null) {
				newmap.put("cyclbgs", ci.getCyclbgs());
			}
			if (ci.getLsf() != null) {
				newmap.put("lsf", ci.getLsf());
			}
			if (ci.getLsflx() != null) {
				newmap.put("lsflx", ci.getLsflx());
			}
			if (ci.getFh() != null) {
				newmap.put("fh", ci.getFh());
			}
			if (ci.getXzname() != null) {
				newmap.put("xzname", ci.getXzname());
			}
			if (ci.getXzdepartment() != null) {
				newmap.put("xzdepartment", ci.getXzdepartment());
			}
			if (ci.getXzbgs() != null) {
				newmap.put("xzbgs", ci.getXzbgs());
			}
			if (ci.getWslsname() != null) {
				newmap.put("wslsname", ci.getWslsname());
			}
			if (ci.getLxfs() != null) {
				newmap.put("lxfs", ci.getLxfs());
			}
			if (ci.getSheng() != null) {
				newmap.put("sheng", ci.getSheng());
			}
			if (ci.getLsname() != null) {
				newmap.put("lsname", ci.getLsname());
			}

			newlist.add(newmap);
		}

		// 服务器缓存路径
		String fileRootPath = request.getSession().getServletContext().getRealPath("/");
		// excel文件名
		String excelName = request.getParameter("ajbh");
		String fileName = excelName + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
		String filePath = fileRootPath + "/" + fileName;

		if (list != null && list.size() > 0) {
			excelUtils.excelUtils(title, typeValue, headers, field, newlist, filePath);
			ExcelUtils.downLoadFile(response, filePath);
			// System.out.println("typeValue============="+typeValue);
			// System.out.println("filePath============="+filePath);
		}
	}

	@AjaxAccess
	public FlipInfo queryAnJian(FlipInfo flipInfo, Map<String, Object> params) throws Exception {

		String latime1 = (String) params.get("latime1");

		// flipInfo.setData(oldList);
		try {
			// PageUtil page=new PageUtil();
			// page.setPage(flipInfo.getPage()!=0?flipInfo.getPage():1);
			// page.setRows(20);
			List<CaseInf> list = new ArrayList<CaseInf>();
			List<Map<String, Object>> newlist = null;
			// 将Map参数放入实体类中传入Dao层
			if (params.get("kehuname") == null) {
				caseInf.setKehuname("");
			} else {
				caseInf.setKehuname((String) params.get("kehuname"));
			}
			if (params.get("latime1") == null) {
				caseInf.setLatime1("");
			} else {
				caseInf.setLatime1((String) params.get("latime1"));
			}
			if (params.get("latime2") == null) {
				caseInf.setLatime2("");
			} else {
				caseInf.setLatime2((String) params.get("latime2"));
			}
			if (params.get("zblawyer") == null) {
				caseInf.setZblawyer("");
			} else {
				caseInf.setZblawyer((String) params.get("zblawyer"));
			}
			if (params.get("ajbh") == null) {
				caseInf.setAjbh("");
			} else {
				caseInf.setAjbh((String) params.get("ajbh"));
			}
			if (params.get("khstyle") == null) {
				caseInf.setKhstyle("");
			} else {
				caseInf.setKhstyle((String) params.get("khstyle"));
			}
			if (params.get("ajlb") == null) {
				caseInf.setAjlb("");
			} else {
				caseInf.setAjlb((String) params.get("ajlb"));
			}
			if (params.get("hylb") == null) {
				caseInf.setHylb("");
			} else {
				caseInf.setHylb((String) params.get("hylb"));
			}
			if (params.get("ahmainyw") == null) {
				caseInf.setAhmainyw("");
			} else {
				caseInf.setAhmainyw((String) params.get("ahmainyw"));
			}
			if (params.get("ayperson") == null) {
				caseInf.setAyperson("");
			} else {
				caseInf.setAyperson((String) params.get("ayperson"));
			}
			if (params.get("tbbgs") == null) {
				caseInf.setTbbgs("");
			} else {
				caseInf.setTbbgs((String) params.get("tbbgs"));
			}
			if (params.get("dycity") == null) {
				caseInf.setDycity("");
			} else {
				caseInf.setDycity((String) params.get("dycity"));
			}
			if (params.get("department") == null) {
				caseInf.setDepartment("");
			} else {
				caseInf.setDepartment((String) params.get("department"));
			}

			list = anjianDao.getAllList(caseInf);

			List<Map<String, Object>> newlist2 = new ArrayList<Map<String, Object>>();// 存储Map类型的新的List
			// 从老list中取出实体类转存为Map，并重新放回List
			for (int i = 0; i < list.size(); i++) {
				CaseInf ci = list.get(i);
				Map<String, Object> newmap = new HashMap(58);// 新的map来替代实体类存放

				// if(ci.getAhmainyw()!=null){newmap.put("ahmainyw",ci.getAhmainyw());}
				// if(ci.getKhstyle()!=null){newmap.put("khstyle",ci.getKhstyle());}
				// //if(ci.getState()!=null){newmap.put("state",ci.getState());}
				// if(ci.getShstate()!=null){newmap.put("shstate",ci.getShstate());}
				// if(ci.getFqname()!=null){newmap.put("fqname",ci.getFqname());}
				// if(ci.getFqtime()!=null){newmap.put("fqtime",ci.getFqtime());}
				// if(ci.getShname()!=null){newmap.put("shname",ci.getShname());}
				// if(ci.getShtime()!=null){newmap.put("shtime",ci.getShtime());}
				// if(ci.getLcstate()!=null){newmap.put("lcstate",ci.getLcstate());}
				// if(ci.getHdstate()!=null){newmap.put("hdstate",ci.getHdstate());}
				// if(ci.getHdname()!=null){newmap.put("hdname",ci.getHdname());}
				// if(ci.getHdtime()!=null){newmap.put("hdtime",ci.getHdtime());}
				// if(ci.getAjbh()!=null){newmap.put("ajbh",ci.getAjbh());}
				// if(ci.getAjmc()!=null){newmap.put("ajmc",ci.getAjmc());}
				// if(ci.getZblawyer()!=null){newmap.put("zblawyer",ci.getZblawyer());}
				// if(ci.getTbname()!=null){newmap.put("tbname",ci.getTbname());}
				// if(ci.getDepartment()!=null){newmap.put("department",ci.getDepartment());}
				// if(ci.getTbbgs()!=null){newmap.put("tbbgs",ci.getTbbgs());}
				// if(ci.getAjlb()!=null){newmap.put("ajlb",ci.getAjlb());}
				// if(ci.getLatime()!=null){newmap.put("latime",ci.getLatime());}
				// if(ci.getAjbde()!=null){newmap.put("ajbde",ci.getAjbde());}
				// if(ci.getSfsytsaj()!=null){newmap.put("sfsytsaj",ci.getSfsytsaj());}
				// if(ci.getSj()!=null){newmap.put("sj",ci.getSj());}
				// if(ci.getAyperson()!=null){newmap.put("ayperson",ci.getAyperson());}
				// if(ci.getAjjk()!=null){newmap.put("ajjk",ci.getAjjk());}
				// if(ci.getHylb()!=null){newmap.put("hylb",ci.getHylb());}
				// if(ci.getJffs()!=null){newmap.put("jffs",ci.getJffs());}
				// if(ci.getJmsf()!=null){newmap.put("jmsf",ci.getJmsf());}
				// if(ci.getDfname()!=null){newmap.put("dfname",ci.getDfname());}
				// if(ci.getWtname()!=null){newmap.put("wtname",ci.getWtname());}
				// if(ci.getZyfname()!=null){newmap.put("zyfname",ci.getZyfname());}
				// if(ci.getZyfdepartment()!=null){newmap.put("zyfdepartment",ci.getZyfdepartment());}
				// if(ci.getZyfbl()!=null){newmap.put("zyfbl",ci.getZyfbl());}
				// if(ci.getJfjebzhong()!=null){newmap.put("jfjebzhong",ci.getJfjebzhong());}
				// if(ci.getJfjesdlf()!=null){newmap.put("jfjesdlf",ci.getJfjesdlf());}
				// if(ci.getQt()!=null){newmap.put("qt",ci.getQt());}
				// if(ci.getJfjestarttime()!=null){newmap.put("jfjestarttime",ci.getJfjestarttime());}
				// if(ci.getJfjeendtime()!=null){newmap.put("jfjeendtime",ci.getJfjeendtime());}
				// if(ci.getJfjebz()!=null){newmap.put("jfjebz",ci.getJfjebz());}
				// if(ci.getCyclname()!=null){newmap.put("cyclname",ci.getCyclname());}
				// if(ci.getCycldepartment()!=null){newmap.put("cycldepartment",ci.getCycldepartment());}
				// if(ci.getCyclbgs()!=null){newmap.put("cyclbgs",ci.getCyclbgs());}
				// if(ci.getLsf()!=null){newmap.put("lsf",ci.getLsf());}
				// if(ci.getLsflx()!=null){newmap.put("lsflx",ci.getLsflx());}
				// if(ci.getFh()!=null){newmap.put("fh",ci.getFh());}
				// if(ci.getXzname()!=null){newmap.put("xzname",ci.getXzname());}
				// if(ci.getXzdepartment()!=null){newmap.put("xzdepartment",ci.getXzdepartment());}
				// if(ci.getXzbgs()!=null){newmap.put("xzbgs",ci.getXzbgs());}
				// if(ci.getWslsname()!=null){newmap.put("wslsname",ci.getWslsname());}
				// if(ci.getLxfs()!=null){newmap.put("lxfs",ci.getLxfs());}
				// if(ci.getSheng()!=null){newmap.put("sheng",ci.getSheng());}
				// if(ci.getLsname()!=null){newmap.put("lsname",ci.getLsname());}

				newmap.put("ahmainyw", ci.getAhmainyw());
				newmap.put("khstyle", ci.getKhstyle());
				newmap.put("state", ci.getState());
				newmap.put("shstate", ci.getShstate());
				newmap.put("fqname", ci.getFqname());
				newmap.put("fqtime", ci.getFqtime());
				newmap.put("shname", ci.getShname());
				newmap.put("shtime", ci.getShtime());
				newmap.put("lcstate", ci.getLcstate());
				newmap.put("hdstate", ci.getHdstate());
				newmap.put("hdname", ci.getHdname());
				newmap.put("hdtime", ci.getHdtime());
				newmap.put("ajbh", ci.getAjbh());
				newmap.put("ajmc", ci.getAjmc());
				newmap.put("zblawyer", ci.getZblawyer());
				newmap.put("tbname", ci.getTbname());
				newmap.put("department", ci.getDepartment());
				newmap.put("tbbgs", ci.getTbbgs());
				newmap.put("ajlb", ci.getAjlb());
				newmap.put("latime", ci.getLatime());
				newmap.put("ajbde", ci.getAjbde());
				newmap.put("sfsytsaj", ci.getSfsytsaj());
				newmap.put("sj", ci.getSj());
				newmap.put("ayperson", ci.getAyperson());
				newmap.put("ajjk", ci.getAjjk());
				newmap.put("hylb", ci.getHylb());
				newmap.put("jffs", ci.getJffs());
				newmap.put("jmsf", ci.getJmsf());
				newmap.put("dfname", ci.getDfname());
				newmap.put("wtname", ci.getWtname());
				newmap.put("zyfname", ci.getZyfname());
				newmap.put("zyfdepartment", ci.getZyfdepartment());
				newmap.put("zyfbl", ci.getZyfbl());
				newmap.put("jfjebzhong", ci.getJfjebzhong());
				newmap.put("jfjesdlf", ci.getJfjesdlf());
				newmap.put("qt", ci.getQt());
				newmap.put("jfjestarttime", ci.getJfjestarttime());
				newmap.put("jfjeendtime", ci.getJfjeendtime());
				newmap.put("jfjebz", ci.getJfjebz());
				newmap.put("cyclname", ci.getCyclname());
				newmap.put("cycldepartment", ci.getCycldepartment());
				newmap.put("cyclbgs", ci.getCyclbgs());
				newmap.put("lsf", ci.getLsf());
				newmap.put("lsflx", ci.getLsflx());
				newmap.put("fh", ci.getFh());
				newmap.put("xzname", ci.getXzname());
				newmap.put("xzdepartment", ci.getXzdepartment());
				newmap.put("xzbgs", ci.getXzbgs());
				newmap.put("wslsname", ci.getWslsname());
				newmap.put("lxfs", ci.getLxfs());
				newmap.put("sheng", ci.getSheng());
				newmap.put("lsname", ci.getLsname());

				newlist2.add(newmap);
			}

			if (newlist2.size() > 0) {
				// 总数
				total = newlist2.size();
				// 总共页数 total/rows
				page = flipInfo.getPage() != 0 ? flipInfo.getPage() : page;
				rows = flipInfo.getSize() != 0 ? flipInfo.getSize() : rows;
				newlist = newlist2.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
			}
			// List newlist =this.getPageList(list,page);
			// flipInfo = dba.findByPaging(sql2.toString(), flipInfo);

			flipInfo.setPage(page);
			flipInfo.setSize(rows);
			flipInfo.setTotal(total);
			flipInfo.setData(newlist);
			flipInfo.setSortField(latime1);
			// System.out.println("rows=================" + rows);
			// System.out.println("size=================" + rows);
			// System.out.println("newlist=================" + newlist.size());
		} catch (Exception e) {
			LOGGER.info("查询工作日志数据库表异常");
		}
		// List<Map> formDataMasterList = flipInfo.getData();

		return flipInfo;
	}

	/**
	 * 对list集合进行分页处理
	 * 
	 * @param i
	 * 
	 * @return
	 */
	private List getPageList(List list) {

		List newList = new ArrayList();

		int total = list.size();

		// newList=list.subList(page.getRows()*(page.getPage()-1),
		// ((page.getRows()*page.getPage())>total?total:((page.getRows()*page.getPage()))));
		// newList=list.subList(rows*(page-1),
		// ((rows*page)>total?total:(rows*page)));
		return newList;
	}

	public AnJianDao getAnjianDao() {
		return anjianDao;
	}

	public void setAnjianDao(AnJianDao anjianDao) {
		this.anjianDao = anjianDao;
	}

	public FormCacheManager getFormCacheManager() {
		return formCacheManager;
	}

	public void setFormCacheManager(FormCacheManager formCacheManager) {
		this.formCacheManager = formCacheManager;
	}

}
