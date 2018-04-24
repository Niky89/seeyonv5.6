package com.seeyon.v3x.hr.webmodel;

import com.seeyon.ctp.organization.webmodel.WebV3xOrgMember;
import com.seeyon.ctp.util.DateUtil;
import java.io.Serializable;
import java.util.Date;

public class WebStaffInfo
  extends WebV3xOrgMember
  implements Serializable
{
  private static final long serialVersionUID = 1578579978101264139L;
  private Long id;
  private int nameList_number;
  private String code;
  private String sex;
  private String name;
  private String usedname;
  private String nation;
  private String birthplace;
  private Date birthday;
  private int age;
  private String ID_card;
  private int edu_level;
  private int marriage;
  private int political_position;
  private boolean people_type;
  private String org_name;
  private Long org_id;
  private Byte type;
  private Byte state;
  private String department_name;
  private String level_name;
  private Long orgLevelId;
  private String post_name;
  private Long orgPostId;
  private String second_posts;
  private String second_posts_ids;
  private Date work_starting_date;
  private int working_time;
  private String specialty;
  private String hobby;
  private String record_wage;
  private String remark;
  private String image;
  private String telephone;
  private String email;
  private String blog;
  private String telNumber;
  private String website;
  private String postalcode;
  private String address;
  private String dutyLevelName;
  private String birthdayStr;
  
  public String getDutyLevelName()
  {
    return this.dutyLevelName;
  }
  
  public void setDutyLevelName(String dutyLevelName)
  {
    this.dutyLevelName = dutyLevelName;
  }
  
  public String getAddress()
  {
    return this.address;
  }
  
  public void setAddress(String address)
  {
    this.address = address;
  }
  
  public String getBlog()
  {
    return this.blog;
  }
  
  public void setBlog(String blog)
  {
    this.blog = blog;
  }
  
  public String getEmail()
  {
    return this.email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getPostalcode()
  {
    return this.postalcode;
  }
  
  public void setPostalcode(String postalcode)
  {
    this.postalcode = postalcode;
  }
  
  public String getTelephone()
  {
    return this.telephone;
  }
  
  public void setTelephone(String telephone)
  {
    this.telephone = telephone;
  }
  
  public String getWebsite()
  {
    return this.website;
  }
  
  public void setWebsite(String website)
  {
    this.website = website;
  }
  
  public int getAge()
  {
    return this.age;
  }
  
  public void setAge(int age)
  {
    this.age = age;
  }
  
  public Date getBirthday()
  {
    return this.birthday;
  }
  
  public void setBirthday(Date birthday)
  {
    this.birthday = birthday;
  }
  
  public String getBirthplace()
  {
    return this.birthplace;
  }
  
  public void setBirthplace(String birthplace)
  {
    this.birthplace = birthplace;
  }
  
  public int getEdu_level()
  {
    return this.edu_level;
  }
  
  public void setEdu_level(int edu_level)
  {
    this.edu_level = edu_level;
  }
  
  public String getHobby()
  {
    return this.hobby;
  }
  
  public void setHobby(String hobby)
  {
    this.hobby = hobby;
  }
  
  public String getID_card()
  {
    return this.ID_card;
  }
  
  public void setID_card(String id_card)
  {
    this.ID_card = id_card;
  }
  
  public String getImage()
  {
    return this.image;
  }
  
  public void setImage(String image)
  {
    this.image = image;
  }
  
  public String getLevel_name()
  {
    return this.level_name;
  }
  
  public void setLevel_name(String level_name)
  {
    this.level_name = level_name;
  }
  
  public int getMarriage()
  {
    return this.marriage;
  }
  
  public void setMarriage(int marriage)
  {
    this.marriage = marriage;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String code)
  {
    this.code = code;
  }
  
  public Byte getType()
  {
    return this.type;
  }
  
  public void setType(Byte type)
  {
    this.type = type;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getNation()
  {
    return this.nation;
  }
  
  public void setNation(String nation)
  {
    this.nation = nation;
  }
  
  public String getOrg_name()
  {
    return this.org_name;
  }
  
  public void setOrg_name(String org_name)
  {
    this.org_name = org_name;
  }
  
  public int getPolitical_position()
  {
    return this.political_position;
  }
  
  public void setPolitical_position(int political_position)
  {
    this.political_position = political_position;
  }
  
  public String getPost_name()
  {
    return this.post_name;
  }
  
  public void setPost_name(String post_name)
  {
    this.post_name = post_name;
  }
  
  public String getRecord_wage()
  {
    return this.record_wage;
  }
  
  public void setRecord_wage(String record_wage)
  {
    this.record_wage = record_wage;
  }
  
  public String getRemark()
  {
    return this.remark;
  }
  
  public void setRemark(String remark)
  {
    this.remark = remark;
  }
  
  public String getSecond_posts()
  {
    return this.second_posts;
  }
  
  public void setSecond_posts(String second_posts)
  {
    this.second_posts = second_posts;
  }
  
  public String getSex()
  {
    return this.sex;
  }
  
  public void setSex(String sex)
  {
    this.sex = sex;
  }
  
  public String getSpecialty()
  {
    return this.specialty;
  }
  
  public void setSpecialty(String specialty)
  {
    this.specialty = specialty;
  }
  
  public String getUsedname()
  {
    return this.usedname;
  }
  
  public void setUsedname(String usedname)
  {
    this.usedname = usedname;
  }
  
  public Date getWork_starting_date()
  {
    return this.work_starting_date;
  }
  
  public void setWork_starting_date(Date work_starting_date)
  {
    this.work_starting_date = work_starting_date;
  }
  
  public int getWorking_time()
  {
    return this.working_time;
  }
  
  public void setWorking_time(int working_time)
  {
    this.working_time = working_time;
  }
  
  public Byte getState()
  {
    return this.state;
  }
  
  public void setState(Byte state)
  {
    this.state = state;
  }
  
  public Long getOrgLevelId()
  {
    return this.orgLevelId;
  }
  
  public void setOrgLevelId(Long orgLevelId)
  {
    this.orgLevelId = orgLevelId;
  }
  
  public Long getOrg_id()
  {
    return this.org_id;
  }
  
  public void setOrg_id(Long org_id)
  {
    this.org_id = org_id;
  }
  
  public Long getOrgPostId()
  {
    return this.orgPostId;
  }
  
  public void setOrgPostId(Long orgPostId)
  {
    this.orgPostId = orgPostId;
  }
  
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public boolean getPeople_type()
  {
    return this.people_type;
  }
  
  public void setPeople_type(boolean people_type)
  {
    this.people_type = people_type;
  }
  
  public String getDepartment_name()
  {
    return this.department_name;
  }
  
  public void setDepartment_name(String department_name)
  {
    this.department_name = department_name;
  }
  
  public int getNameList_number()
  {
    return this.nameList_number;
  }
  
  public void setNameList_number(int nameList_number)
  {
    this.nameList_number = nameList_number;
  }
  
  public String getTelNumber()
  {
    return this.telNumber;
  }
  
  public void setTelNumber(String telNumber)
  {
    this.telNumber = telNumber;
  }
  
  public String getSecond_posts_ids()
  {
    return this.second_posts_ids;
  }
  
  public void setSecond_posts_ids(String second_posts_ids)
  {
    this.second_posts_ids = second_posts_ids;
  }
  
  public void setBirthdayStr(String birthdayStr)
  {
    this.birthdayStr = birthdayStr;
  }
  
  public String getBirthdayStr()
  {
    if (this.birthday != null) {
      this.birthdayStr = DateUtil.format(this.birthday);
    } else {
      this.birthdayStr = "";
    }
    return this.birthdayStr;
  }
}
