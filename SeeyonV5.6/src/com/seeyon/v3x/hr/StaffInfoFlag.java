package com.seeyon.v3x.hr;

import java.util.ArrayList;
import java.util.List;

public class StaffInfoFlag
{
  public static final String SINGLE = "1";
  public static final String MARRIED = "2";
  public static final int JUNIOR_HIGH_SCHOOL = 1;
  public static final int SENIOR_HIGH_SCHOOL = 2;
  public static final int JUNIOR_COLLEGE = 3;
  public static final int UNDERGRADUATE_COLLEGE = 4;
  public static final int MASTER_DEGREE = 5;
  public static final int DOCTORAL_DEGREE = 6;
  public static final int OTHER = 7;
  public static final int VOCATIONAL_HIGH_SCHOOL = 8;
  public static final int TECHNICAL_SECONDARY_SCHOOL = 9;
  public static final int TECHNICAL_SCHOOL = 10;
  private static List<Integer> allEducation = null;
  public static final int COMMIE = 1;
  public static final int OTHER_PARTY = 2;
  public static final int REWARD = 1;
  public static final int PUNISHMENT = 2;
  private static final byte[] EduTypeLock = new byte[0];
  
  public static List<Integer> getAllEducation()
  {
    synchronized (EduTypeLock)
    {
      if (allEducation == null)
      {
        allEducation = new ArrayList();
        allEducation.add(Integer.valueOf(1));
        allEducation.add(Integer.valueOf(2));
        allEducation.add(Integer.valueOf(8));
        allEducation.add(Integer.valueOf(9));
        allEducation.add(Integer.valueOf(10));
        allEducation.add(Integer.valueOf(3));
        allEducation.add(Integer.valueOf(4));
        allEducation.add(Integer.valueOf(5));
        allEducation.add(Integer.valueOf(6));
        allEducation.add(Integer.valueOf(7));
      }
    }
    return allEducation;
  }
}
