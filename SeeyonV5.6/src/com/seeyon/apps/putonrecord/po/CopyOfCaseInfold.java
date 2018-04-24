package com.seeyon.apps.putonrecord.po;

import java.util.Date;

public class CopyOfCaseInfold {

	private Date latime1;	 //立案时间
	private Date latime2;
	private Integer ajbh;	 //案件编号
	//private boolean sfsytsaj;
	private String sfsytsaj; //是否属于特殊案件
	private String ajlb;	 //案件类型
	private String hylb;	 //行业类别（案件分类）
	private String ayperson; //案源人
	private String ajstate;  //案件状态
	
	
	private String kehuname;   //客户姓名
	private String khstyle;	   //客户类型
	private String khjb;       //客户级别
	private String ahmainyw;   //客户主营业务查询
	
	
	private String zblawyer;    //主办律师
	private String department;  //主办部门
	private String tbbgs;		//填报办公室
	private String dycity;		//地域
	
	private String zyfname;		//展业费使用分配人员姓名
	private String zyfdepartment;//展业费使用分配人员部门
	private String zyfbl;		//展业费使用分配比例
	
	
	
	
	
	public CopyOfCaseInfold() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CopyOfCaseInfold(Date latime1, Date latime2, Integer ajbh, String sfsytsaj,
			String ajlb, String hylb, String ayperson, String ajstate,
			String kehuname, String khstyle, String khjb, String ahmainyw,
			String zblawyer, String department, String tbbgs, String dycity,
			String zyfname, String zyfdepartment, String zyfbl) {
		super();
		this.latime1 = latime1;
		this.latime2 = latime2;
		this.ajbh = ajbh;
		this.sfsytsaj = sfsytsaj;
		this.ajlb = ajlb;
		this.hylb = hylb;
		this.ayperson = ayperson;
		this.ajstate = ajstate;
		this.kehuname = kehuname;
		this.khstyle = khstyle;
		this.khjb = khjb;
		this.ahmainyw = ahmainyw;
		this.zblawyer = zblawyer;
		this.department = department;
		this.tbbgs = tbbgs;
		this.dycity = dycity;
		this.zyfname = zyfname;
		this.zyfdepartment = zyfdepartment;
		this.zyfbl = zyfbl;
	}
	public String getZyfname() {
		return zyfname;
	}
	public void setZyfname(String zyfname) {
		this.zyfname = zyfname;
	}
	public String getZyfdepartment() {
		return zyfdepartment;
	}
	public void setZyfdepartment(String zyfdepartment) {
		this.zyfdepartment = zyfdepartment;
	}
	public String getZyfbl() {
		return zyfbl;
	}
	public void setZyfbl(String zyfbl) {
		this.zyfbl = zyfbl;
	}
	public Date getLatime1() {
		return latime1;
	}
	public void setLatime1(Date latime1) {
		this.latime1 = latime1;
	}
	public Date getLatime2() {
		return latime2;
	}
	public void setLatime2(Date latime2) {
		this.latime2 = latime2;
	}
	public Integer getAjbh() {
		return ajbh;
	}
	public void setAjbh(Integer ajbh) {
		this.ajbh = ajbh;
	}
	public String getSfsytsaj() {
		return sfsytsaj;
	}
	public void setSfsytsaj(String sfsytsaj) {
		this.sfsytsaj = sfsytsaj;
	}
	public String getAjlb() {
		return ajlb;
	}
	public void setAjlb(String ajlb) {
		this.ajlb = ajlb;
	}
	public String getHylb() {
		return hylb;
	}
	public void setHylb(String hylb) {
		this.hylb = hylb;
	}
	public String getAyperson() {
		return ayperson;
	}
	public void setAyperson(String ayperson) {
		this.ayperson = ayperson;
	}
	public String getAjstate() {
		return ajstate;
	}
	public void setAjstate(String ajstate) {
		this.ajstate = ajstate;
	}
	public String getKehuname() {
		return kehuname;
	}
	public void setKehuname(String kehuname) {
		this.kehuname = kehuname;
	}
	public String getKhstyle() {
		return khstyle;
	}
	public void setKhstyle(String khstyle) {
		this.khstyle = khstyle;
	}
	public String getKhjb() {
		return khjb;
	}
	public void setKhjb(String khjb) {
		this.khjb = khjb;
	}
	public String getAhmainyw() {
		return ahmainyw;
	}
	public void setAhmainyw(String ahmainyw) {
		this.ahmainyw = ahmainyw;
	}
	public String getZblawyer() {
		return zblawyer;
	}
	public void setZblawyer(String zblawyer) {
		this.zblawyer = zblawyer;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getTbbgs() {
		return tbbgs;
	}
	public void setTbbgs(String tbbgs) {
		this.tbbgs = tbbgs;
	}
	public String getDycity() {
		return dycity;
	}
	public void setDycity(String dycity) {
		this.dycity = dycity;
	}
	
	
	
	
}
