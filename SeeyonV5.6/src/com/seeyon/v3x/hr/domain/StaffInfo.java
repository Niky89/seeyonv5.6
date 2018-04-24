package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.hr.util.EncryptUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class StaffInfo extends BaseModel implements Serializable {
	private static final long serialVersionUID = 7383995091816052579L;
	private Long org_member_id;
	private String sex;
	private String name;
	private String usedname;
	private String age;
	private Date birthday;
	private String nation;
	private String birthplace;
	private String ID_card;
	private int edu_level;
	private int marriage;
	private int political_position;
	private Date work_starting_date;
	private int working_time;
	private String specialty;
	private String hobby;
	private Double record_wage;
	private String remark;
	private String email;
	private String tel_number;
	private String qq;
	private String msn;
	private Long image_id;
	private Date image_datetime;
	private String image_name;
	private String self_image_name;
	private String degreeLevel;
	private String record_wage_str;
	// 客开新增字段呢sxl
	private String domicile_place;// 户籍地
	private Date frist_work_date;// 首次参加工作时间
	private String health;// 健康状况
	private String infectious_diseases;// 曾患何种传染病
	private String isok;// 是否合格标记
	private String work_language;// 工作语言
	private String lawNo;// 律师证号
	private String pralawNo;// 实习律师证号
	private String judicial_qualificationNo;// 司法资格证号

	public String getWork_language() {
		return work_language;
	}

	public void setWork_language(String work_language) {
		this.work_language = work_language;
	}

	public String getLawNo() {
		return lawNo;
	}

	public void setLawNo(String lawNo) {
		this.lawNo = lawNo;
	}

	public String getPralawNo() {
		return pralawNo;
	}

	public void setPralawNo(String pralawNo) {
		this.pralawNo = pralawNo;
	}

	public String getJudicial_qualificationNo() {
		return judicial_qualificationNo;
	}

	public void setJudicial_qualificationNo(String judicial_qualificationNo) {
		this.judicial_qualificationNo = judicial_qualificationNo;
	}

	public String getIsok() {
		return isok;
	}

	public void setIsok(String isok) {
		this.isok = isok;
	}

	public String getDomicile_place() {
		return domicile_place;
	}

	public void setDomicile_place(String domicile_place) {
		this.domicile_place = domicile_place;
	}

	public Date getFrist_work_date() {
		return frist_work_date;
	}

	public void setFrist_work_date(Date frist_work_date) {
		this.frist_work_date = frist_work_date;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

	public String getInfectious_diseases() {
		return infectious_diseases;
	}

	public void setInfectious_diseases(String infectious_diseases) {
		this.infectious_diseases = infectious_diseases;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setRecord_wage_str(String record_wage_str) {
		this.record_wage_str = record_wage_str;
	}

	public String getRecord_wage_str() {
		this.record_wage_str = EncryptUtil.doubleFormat(getRecord_wage());
		return this.record_wage_str;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getBirthplace() {
		return this.birthplace;
	}

	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}

	public int getEdu_level() {
		return this.edu_level;
	}

	public void setEdu_level(int edu_level) {
		this.edu_level = edu_level;
	}

	public String getHobby() {
		return this.hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getID_card() {
		return this.ID_card;
	}

	public void setID_card(String id_card) {
		this.ID_card = id_card;
	}

	public int getMarriage() {
		return this.marriage;
	}

	public void setMarriage(int marriage) {
		this.marriage = marriage;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNation() {
		return this.nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public int getPolitical_position() {
		return this.political_position;
	}

	public void setPolitical_position(int political_position) {
		this.political_position = political_position;
	}

	public Double getRecord_wage() {
		return this.record_wage;
	}

	public void setRecord_wage(Double record_wage) {
		this.record_wage = record_wage;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSpecialty() {
		return this.specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getUsedname() {
		return this.usedname;
	}

	public void setUsedname(String usedname) {
		this.usedname = usedname;
	}

	public Date getWork_starting_date() {
		return this.work_starting_date;
	}

	public void setWork_starting_date(Date work_starting_date) {
		this.work_starting_date = work_starting_date;
	}

	public String getAge() {
		return this.age;
	}

	public void setAgeByBirthday(Date birthday) {
		int age = 1;
		if (birthday != null) {
			Calendar now = Calendar.getInstance();
			int year1 = now.get(1);
			Calendar bd = Calendar.getInstance();
			bd.setTime(birthday);
			int year2 = bd.get(1);
			age = year1 - year2;
		}
		if (age == 0) {
			age = 1;
		}
		this.age = String.valueOf(age);
	}

	public int getWorking_time() {
		return this.working_time;
	}

	public void setWorking_time(int working_time) {
		this.working_time = working_time;
	}

	public Long getOrg_member_id() {
		return this.org_member_id;
	}

	public void setOrg_member_id(Long org_member_id) {
		this.org_member_id = org_member_id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMsn() {
		return this.msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getQq() {
		return this.qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getTel_number() {
		return this.tel_number;
	}

	public void setTel_number(String tel_number) {
		this.tel_number = tel_number;
	}

	public Date getImage_datetime() {
		return this.image_datetime;
	}

	public void setImage_datetime(Date image_datetime) {
		this.image_datetime = image_datetime;
	}

	public Long getImage_id() {
		return this.image_id;
	}

	public void setImage_id(Long image_id) {
		this.image_id = image_id;
	}

	public String getImage_name() {
		return this.image_name;
	}

	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}

	public String getSelf_image_name() {
		return this.self_image_name;
	}

	public void setSelf_image_name(String self_image_name) {
		this.self_image_name = self_image_name;
	}

	public String getDegreeLevel() {
		return this.degreeLevel;
	}

	public void setDegreeLevel(String degreeLevel) {
		this.degreeLevel = degreeLevel;
	}
}
