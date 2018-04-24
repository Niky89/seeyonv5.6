package com.seeyon.v3x.hr.webmodel;

public class WebStatisticStaff
{
  private String depName;
  private int count;
  private String post;
  private String level;
  private String education;
  private String gender;
  private String ageLevel;
  private String politicalPosition;
  
  public String getAgeLevel()
  {
    return this.ageLevel;
  }
  
  public void setAgeLevel(String ageLevel)
  {
    this.ageLevel = ageLevel;
  }
  
  public String getGender()
  {
    return this.gender;
  }
  
  public void setGender(String gender)
  {
    this.gender = gender;
  }
  
  public String getEducation()
  {
    return this.education;
  }
  
  public void setEducation(String education)
  {
    this.education = education;
  }
  
  public String getLevel()
  {
    return this.level;
  }
  
  public void setLevel(String level)
  {
    this.level = level;
  }
  
  public int getCount()
  {
    return this.count;
  }
  
  public void setCount(int count)
  {
    this.count = count;
  }
  
  public String getPost()
  {
    return this.post;
  }
  
  public void setPost(String post)
  {
    this.post = post;
  }
  
  public String getDepName()
  {
    return this.depName;
  }
  
  public void setDepName(String depName)
  {
    this.depName = depName;
  }
  
  public String getPoliticalPosition()
  {
    return this.politicalPosition;
  }
  
  public void setPoliticalPosition(String politicalPosition)
  {
    this.politicalPosition = politicalPosition;
  }
}
