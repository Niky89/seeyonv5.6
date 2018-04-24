package com.seeyon.apps.putonrecord.po;

public class CaseInf {

	private Object latime1=null;	 //立案时间
	private Object latime2=null;
	private Object ajbh=null;	 //案件编号
	//private boolean sfsytsaj;
	private Object sfsytsaj=null; //是否属于特殊案件
	private Object ajlb=null;	 //案件类型
	private Object hylb=null;	 //行业类别（案件分类）
	private Object ayperson=null; //案源人
	private Object ajstate=null;  //案件状态
	
	
	private Object kehuname=null;   //客户姓名
	private Object khstyle=null;	   //客户类型
	private Object khjb=null;       //客户级别
	private Object ahmainyw=null;   //客户主营业务查询
	
	
	private Object zblawyer=null;    //主办律师
	private Object department=null;  //主办部门
	private Object tbbgs=null;		//填报办公室
	private Object dycity=null;		//地域
	
	private Object zyfname=null;		//展业费使用分配人员姓名
	private Object zyfdepartment=null;//展业费使用分配人员部门
	private Object zyfbl=null;		//展业费使用分配比例
	
	//////新字段

	private Object state=null;
	private Object shstate=null;
	private Object fqname=null;
	private Object fqtime=null;
	private Object shname=null;
	private Object shtime=null;
	private Object lcstate=null;
	private Object hdstate=null;
	private Object hdname=null;
	private Object hdtime=null;

	private Object ajmc=null;

	private Object tbname=null;



	private Object latime=null;
	private Object ajbde=null;

	private Object sj=null;

	private Object ajjk=null;

	private Object jffs=null;
	private Object jmsf=null;
	private Object dfname=null;
	private Object wtname=null;



	private Object jfjebzhong=null;
	private Object jfjesdlf=null;
	private Object qt=null;
	private Object jfjestarttime=null;
	private Object jfjeendtime=null;
	private Object jfjebz=null;
	private Object cyclname=null;
	private Object cycldepartment=null;
	private Object cyclbgs=null;
	private Object lsf=null;
	private Object lsflx=null;
	private Object fh=null;
	private Object xzname=null;
	private Object xzdepartment=null;
	private Object xzbgs=null;
	private Object wslsname=null;
	private Object lxfs=null;
	private Object sheng=null;
	private Object lsname=null;
	
	
	
	public CaseInf() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CaseInf(Object latime1, Object latime2, Object ajbh, Object sfsytsaj, Object ajlb, Object hylb,
			Object ayperson, Object ajstate, Object kehuname, Object khstyle, Object khjb, Object ahmainyw,
			Object zblawyer, Object department, Object tbbgs, Object dycity, Object zyfname, Object zyfdepartment,
			Object zyfbl, Object state, Object shstate, Object fqname, Object fqtime, Object shname, Object shtime,
			Object lcstate, Object hdstate, Object hdname, Object hdtime, Object ajmc, Object tbname, Object latime,
			Object ajbde, Object sj, Object ajjk, Object jffs, Object jmsf, Object dfname, Object wtname,
			Object jfjebzhong, Object jfjesdlf, Object qt, Object jfjestarttime, Object jfjeendtime, Object jfjebz,
			Object cyclname, Object cycldepartment, Object cyclbgs, Object lsf, Object lsflx, Object fh, Object xzname,
			Object xzdepartment, Object xzbgs, Object wslsname, Object lxfs, Object sheng, Object lsname) {
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
		this.state = state;
		this.shstate = shstate;
		this.fqname = fqname;
		this.fqtime = fqtime;
		this.shname = shname;
		this.shtime = shtime;
		this.lcstate = lcstate;
		this.hdstate = hdstate;
		this.hdname = hdname;
		this.hdtime = hdtime;
		this.ajmc = ajmc;
		this.tbname = tbname;
		this.latime = latime;
		this.ajbde = ajbde;
		this.sj = sj;
		this.ajjk = ajjk;
		this.jffs = jffs;
		this.jmsf = jmsf;
		this.dfname = dfname;
		this.wtname = wtname;
		this.jfjebzhong = jfjebzhong;
		this.jfjesdlf = jfjesdlf;
		this.qt = qt;
		this.jfjestarttime = jfjestarttime;
		this.jfjeendtime = jfjeendtime;
		this.jfjebz = jfjebz;
		this.cyclname = cyclname;
		this.cycldepartment = cycldepartment;
		this.cyclbgs = cyclbgs;
		this.lsf = lsf;
		this.lsflx = lsflx;
		this.fh = fh;
		this.xzname = xzname;
		this.xzdepartment = xzdepartment;
		this.xzbgs = xzbgs;
		this.wslsname = wslsname;
		this.lxfs = lxfs;
		this.sheng = sheng;
		this.lsname = lsname;
	}
	public Object getZyfname() {
		return zyfname;
	}
	public void setZyfname(Object zyfname) {
		this.zyfname = zyfname;
	}
	public Object getZyfdepartment() {
		return zyfdepartment;
	}
	public void setZyfdepartment(Object zyfdepartment) {
		this.zyfdepartment = zyfdepartment;
	}
	public Object getZyfbl() {
		return zyfbl;
	}
	public void setZyfbl(Object zyfbl) {
		this.zyfbl = zyfbl;
	}
	public Object getLatime1() {
		return latime1;
	}
	public void setLatime1(Object latime1) {
		this.latime1 = latime1;
	}
	public Object getLatime2() {
		return latime2;
	}
	public void setLatime2(Object latime2) {
		this.latime2 = latime2;
	}
	public Object getAjbh() {
		return ajbh;
	}
	public void setAjbh(Object ajbh) {
		this.ajbh = ajbh;
	}
	public Object getSfsytsaj() {
		return sfsytsaj;
	}
	public void setSfsytsaj(Object sfsytsaj) {
		this.sfsytsaj = sfsytsaj;
	}
	public Object getAjlb() {
		return ajlb;
	}
	public void setAjlb(Object ajlb) {
		this.ajlb = ajlb;
	}
	public Object getHylb() {
		return hylb;
	}
	public void setHylb(Object hylb) {
		this.hylb = hylb;
	}
	public Object getAyperson() {
		return ayperson;
	}
	public void setAyperson(Object ayperson) {
		this.ayperson = ayperson;
	}
	public Object getAjstate() {
		return ajstate;
	}
	public void setAjstate(Object ajstate) {
		this.ajstate = ajstate;
	}
	public Object getKehuname() {
		return kehuname;
	}
	public void setKehuname(Object kehuname) {
		this.kehuname = kehuname;
	}
	public Object getKhstyle() {
		return khstyle;
	}
	public void setKhstyle(Object khstyle) {
		this.khstyle = khstyle;
	}
	public Object getKhjb() {
		return khjb;
	}
	public void setKhjb(Object khjb) {
		this.khjb = khjb;
	}
	public Object getAhmainyw() {
		return ahmainyw;
	}
	public void setAhmainyw(Object ahmainyw) {
		this.ahmainyw = ahmainyw;
	}
	public Object getZblawyer() {
		return zblawyer;
	}
	public void setZblawyer(Object zblawyer) {
		this.zblawyer = zblawyer;
	}
	public Object getDepartment() {
		return department;
	}
	public void setDepartment(Object department) {
		this.department = department;
	}
	public Object getTbbgs() {
		return tbbgs;
	}
	public void setTbbgs(Object tbbgs) {
		this.tbbgs = tbbgs;
	}
	public Object getDycity() {
		return dycity;
	}
	public void setDycity(Object dycity) {
		this.dycity = dycity;
	}
	
	
	public Object getState() {
		return state;
	}
	public void setState(Object state) {
		this.state = state;
	}
	public Object getShstate() {
		return shstate;
	}
	public void setShstate(Object shstate) {
		this.shstate = shstate;
	}
	public Object getFqname() {
		return fqname;
	}
	public void setFqname(Object fqname) {
		this.fqname = fqname;
	}
	public Object getFqtime() {
		return fqtime;
	}
	public void setFqtime(Object fqtime) {
		this.fqtime = fqtime;
	}
	public Object getShname() {
		return shname;
	}
	public void setShname(Object shname) {
		this.shname = shname;
	}
	public Object getShtime() {
		return shtime;
	}
	public void setShtime(Object shtime) {
		this.shtime = shtime;
	}
	public Object getLcstate() {
		return lcstate;
	}
	public void setLcstate(Object lcstate) {
		this.lcstate = lcstate;
	}
	public Object getHdstate() {
		return hdstate;
	}
	public void setHdstate(Object hdstate) {
		this.hdstate = hdstate;
	}
	public Object getHdname() {
		return hdname;
	}
	public void setHdname(Object hdname) {
		this.hdname = hdname;
	}
	public Object getHdtime() {
		return hdtime;
	}
	public void setHdtime(Object hdtime) {
		this.hdtime = hdtime;
	}
	public Object getAjmc() {
		return ajmc;
	}
	public void setAjmc(Object ajmc) {
		this.ajmc = ajmc;
	}
	public Object getTbname() {
		return tbname;
	}
	public void setTbname(Object tbname) {
		this.tbname = tbname;
	}
	public Object getLatime() {
		return latime;
	}
	public void setLatime(Object latime) {
		this.latime = latime;
	}
	public Object getAjbde() {
		return ajbde;
	}
	public void setAjbde(Object ajbde) {
		this.ajbde = ajbde;
	}
	public Object getSj() {
		return sj;
	}
	public void setSj(Object sj) {
		this.sj = sj;
	}
	public Object getAjjk() {
		return ajjk;
	}
	public void setAjjk(Object ajjk) {
		this.ajjk = ajjk;
	}
	public Object getJffs() {
		return jffs;
	}
	public void setJffs(Object jffs) {
		this.jffs = jffs;
	}
	public Object getJmsf() {
		return jmsf;
	}
	public void setJmsf(Object jmsf) {
		this.jmsf = jmsf;
	}
	public Object getDfname() {
		return dfname;
	}
	public void setDfname(Object dfname) {
		this.dfname = dfname;
	}
	public Object getWtname() {
		return wtname;
	}
	public void setWtname(Object wtname) {
		this.wtname = wtname;
	}
	public Object getJfjebzhong() {
		return jfjebzhong;
	}
	public void setJfjebzhong(Object jfjebzhong) {
		this.jfjebzhong = jfjebzhong;
	}
	public Object getJfjesdlf() {
		return jfjesdlf;
	}
	public void setJfjesdlf(Object jfjesdlf) {
		this.jfjesdlf = jfjesdlf;
	}
	public Object getQt() {
		return qt;
	}
	public void setQt(Object qt) {
		this.qt = qt;
	}
	public Object getJfjestarttime() {
		return jfjestarttime;
	}
	public void setJfjestarttime(Object jfjestarttime) {
		this.jfjestarttime = jfjestarttime;
	}
	public Object getJfjeendtime() {
		return jfjeendtime;
	}
	public void setJfjeendtime(Object jfjeendtime) {
		this.jfjeendtime = jfjeendtime;
	}
	public Object getJfjebz() {
		return jfjebz;
	}
	public void setJfjebz(Object jfjebz) {
		this.jfjebz = jfjebz;
	}
	public Object getCyclname() {
		return cyclname;
	}
	public void setCyclname(Object cyclname) {
		this.cyclname = cyclname;
	}
	public Object getCycldepartment() {
		return cycldepartment;
	}
	public void setCycldepartment(Object cycldepartment) {
		this.cycldepartment = cycldepartment;
	}
	public Object getCyclbgs() {
		return cyclbgs;
	}
	public void setCyclbgs(Object cyclbgs) {
		this.cyclbgs = cyclbgs;
	}
	public Object getLsf() {
		return lsf;
	}
	public void setLsf(Object lsf) {
		this.lsf = lsf;
	}
	public Object getLsflx() {
		return lsflx;
	}
	public void setLsflx(Object lsflx) {
		this.lsflx = lsflx;
	}
	public Object getFh() {
		return fh;
	}
	public void setFh(Object fh) {
		this.fh = fh;
	}
	public Object getXzname() {
		return xzname;
	}
	public void setXzname(Object xzname) {
		this.xzname = xzname;
	}
	public Object getXzdepartment() {
		return xzdepartment;
	}
	public void setXzdepartment(Object xzdepartment) {
		this.xzdepartment = xzdepartment;
	}
	public Object getXzbgs() {
		return xzbgs;
	}
	public void setXzbgs(Object xzbgs) {
		this.xzbgs = xzbgs;
	}
	public Object getWslsname() {
		return wslsname;
	}
	public void setWslsname(Object wslsname) {
		this.wslsname = wslsname;
	}
	public Object getLxfs() {
		return lxfs;
	}
	public void setLxfs(Object lxfs) {
		this.lxfs = lxfs;
	}
	public Object getSheng() {
		return sheng;
	}
	public void setSheng(Object sheng) {
		this.sheng = sheng;
	}
	public Object getLsname() {
		return lsname;
	}
	public void setLsname(Object lsname) {
		this.lsname = lsname;
	}
	
	
	
}
