package com.seeyon.v3x.hr.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class EncryptUtil
{
  public static double encrypt(Long userId, double num)
  {
    if (userId != null)
    {
      long abs = Math.abs(userId.longValue());
      String seed = String.valueOf(abs).substring(0, 2);
      return Integer.parseInt(seed) * num;
    }
    return num;
  }
  
  public static String doubleFormat(Double dvalue)
  {
    if (dvalue == null) {
      return "";
    }
    String retValue = null;
    DecimalFormat df = new DecimalFormat();
    df.setMinimumFractionDigits(0);
    df.setMaximumFractionDigits(2);
    retValue = df.format(dvalue);
    retValue = retValue.replaceAll(",", "");
    return retValue;
  }
  
  public static BigDecimal decrypt(Long userId, double num, int size)
  {
    if (userId != null)
    {
      long abs = Math.abs(userId.longValue());
      String seed = String.valueOf(abs).substring(0, 2);
      String str = format(num / Integer.parseInt(seed), size);
      BigDecimal bigdecimal = new BigDecimal(str);
      return bigdecimal.setScale(2);
    }
    return new BigDecimal(num).setScale(2);
  }
  
  private static String format(double number, int size)
  {
    StringBuffer formatString = new StringBuffer("0");
    if (size > 0) {
      formatString.append(".");
    }
    for (int i = 0; i < size; i++) {
      formatString.append("#");
    }
    DecimalFormat df = new DecimalFormat(formatString.toString());
    return df.format(number);
  }
}
