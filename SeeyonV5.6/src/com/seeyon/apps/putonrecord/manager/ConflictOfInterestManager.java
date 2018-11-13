package com.seeyon.apps.putonrecord.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.collections.map.IdentityMap;

import com.alibaba.fastjson.JSONObject;
import com.deheng.utils.Util;
import com.seeyon.apps.putonrecord.dao.ConflictOfInterestDao;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.annotation.NeedlessCheckLogin;

/**
 * 
 * @author sxl 利益冲突逻辑处理类
 */
public class ConflictOfInterestManager {

	private ConflictOfInterestDao coiDao;

	/**
	 * 获取从前台传递过来的数据 委托人数量,委托人名称和id,委托人类型. 对方当事人数量,对方当事人姓名id,对方当事人类型.进行处理
	 * 
	 * 委托人和当前提交的立案申请中的对方当事人进行比对,中文权重100%,拼音,不包含音调 权重95%遍历比较. 对方当事人和已有客户进行比较.
	 * 
	 * @param wtcount
	 *            委托人数量
	 * @param wtobjs
	 *            委托人传递数据
	 * @param dfcount
	 *            对方当事人数量
	 * @param dfObjs
	 *            对方当事人传递数据
	 * @return 处理后的信息
	 * 
	 */
	@AjaxAccess
	@NeedlessCheckLogin
	public String checkConflictOfInterest(int wtcount, List<Map<String, Object>> wtobjs, int dfcount,
			List<Map<String, Object>> dfobjs, int type) {
		// 传上来的格式为id,name,type
		// 暂时只接受name //消息map
		long start = System.currentTimeMillis();
		Map<String, Map<String, Map<String, String>>> messageMap = new HashMap<String, Map<String, Map<String, String>>>();

		Map<String, Map<String, String>> wttemp = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> dftemp = new HashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> qttemp = new HashMap<String, Map<String, String>>();
		if (type == 1) {// 1查对方当事人2查客户
			for (Map<String, Object> m : wtobjs) {
				Object wtname = m.get("name");
				// 将name 分词生成sqlwhere
				if (wtname != null) {
					if (!wtname.toString().trim().equals("")) {
						String tempname = wtname.toString().trim().replace("、", "");
						List<Term> parse = ToAnalysis.parse(tempname);
//						StringBuffer prisqlwhere = new StringBuffer();
//						prisqlwhere.append(" '%");
//						for (Term t : parse) {
//							if (!t.getName().trim().equals("")) {
//								prisqlwhere.append(Util.checkSqlString(t.getName()) + "%");
//							}
//						}
//						prisqlwhere.append("' ");
						// 首先判断委托人信息，
						Map<String, Map<String, String>> wtrlist = coiDao.getAllOther(parse);
						wttemp.putAll(wtrlist);
					}
				}
			}
			messageMap.put("wt", wttemp);
		}
		if (type == 2) {
			for (Map<String, Object> m : dfobjs) {
				Object dfname = m.get("name");
				// 将name 分词生成sqlwhere
				if (dfname != null) {
					if (!dfname.toString().trim().equals("")) {
						String tempname = dfname.toString().trim().replace("、", "");
						List<Term> parse = ToAnalysis.parse(tempname);
//						StringBuffer allOthersqlwhere = new StringBuffer();
//						allOthersqlwhere.append(" '%");
//						for (Term t : parse) {
//							if (!t.getName().trim().equals("")) {
//								allOthersqlwhere.append(Util.checkSqlString(t.getName()) + "%");
//							}
//						}
//						allOthersqlwhere.append("' ");
						// 首先判断委托人信息，
						Map<String, Map<String, String>> dflist = coiDao.getALlPri(parse);
						dftemp.putAll(dflist);
					}
				}
			}
			messageMap.put("df", dftemp);
		}

		if (type == 3) {//预立案
			for (Map<String, Object> m : dfobjs) {
				Object dfname = m.get("name");
				// 将name 分词生成sqlwhere
				if (dfname != null) {
					if (!dfname.toString().trim().equals("")) {
						String tempname = dfname.toString().trim().replace("、", "");
						List<Term> parse = ToAnalysis.parse(tempname);
//						StringBuffer allOthersqlwhere = new StringBuffer();
//						allOthersqlwhere.append(" '%");
//						for (Term t : parse) {
//							if (!t.getName().trim().equals("")) {
//								allOthersqlwhere.append(Util.checkSqlString(t.getName()) + "%");
//							}
//						}
//						allOthersqlwhere.append("' ");
						// 首先判断委托人信息，
						Map<String, Map<String, String>> dflist = coiDao.getALlPri2(parse);

						dftemp.putAll(dflist);
					}
				}
			}
			messageMap.put("dfy", dftemp);
		}
		
		//其他利益相关当事人
		if(type == 4 ){
			for (Map<String, Object> m : wtobjs) {
				Object wtname = m.get("name");
				if (wtname != null) {
					if (!wtname.toString().trim().equals("")) {
						String tempname = wtname.toString().trim().replace("、", "");
						List<Term> parse = ToAnalysis.parse(tempname);

						Map<String, Map<String, String>> wtrlist = coiDao.getOtherInterests(parse);
						qttemp.putAll(wtrlist);
					}
				}
			}
			messageMap.put("qtly", qttemp);
		}

		// 创造 json
		return JSONObject.toJSONString(messageMap);
	}

	public List<Map<String, String>> getAllPrincipal(String param) {
		return coiDao.getAllPrincipal(param);
	}

	public ConflictOfInterestDao getCoiDao() {
		return coiDao;
	}

	public void setCoiDao(ConflictOfInterestDao coiDao) {
		this.coiDao = coiDao;
	}

}
