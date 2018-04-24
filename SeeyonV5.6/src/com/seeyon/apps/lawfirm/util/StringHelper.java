package com.seeyon.apps.lawfirm.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StringHelper
{
  public static String concat(String s1, String s2, String sp)
  {
    if (s1 == null) {
      s1 = "";
    }
    if (s2 == null) {
      s2 = "";
    }
    if ((isNullOrEmpty(s1).booleanValue()) && (isNullOrEmpty(s2).booleanValue())) {
      return "";
    }
    if (isNullOrEmpty(s1).booleanValue()) {
      return s2;
    }
    if (isNullOrEmpty(s2).booleanValue()) {
      return s1;
    }
    return s1 + sp + s2;
  }
  
  public static Boolean isNullOrEmpty(String str)
  {
    if ((str != null) && (str.length() != 0)) {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
  
  public static Boolean containBySp(String str, String findStr, String sp)
  {
    if ((sp + str + sp).indexOf(sp + findStr + sp) != -1) {
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(false);
  }
  
  public static String joinList(List list, String sp)
  {
    String ret = "";
    for (Object obj : list) {
      if ((obj instanceof Map))
      {
        Map map = (Map)obj;
        Iterator iter = map.entrySet().iterator();
        if (iter.hasNext())
        {
          Map.Entry ent = (Map.Entry)iter.next();
          if (ent != null)
          {
            Object obj2 = ent.getValue();
            if (obj2 != null) {
              ret = concat(ret, obj2.toString(), sp);
            }
          }
        }
      }
      else if (obj != null)
      {
        ret = concat(ret, obj.toString(), sp);
      }
    }
    return ret;
  }
  
  public static String joinList(List list, String sL, String sR, String sp)
  {
    String ret = "";
    for (Object obj : list) {
      if ((obj instanceof Map))
      {
        Map map = (Map)obj;
        Iterator iter = map.entrySet().iterator();
        if (iter.hasNext())
        {
          Map.Entry ent = (Map.Entry)iter.next();
          if (ent != null)
          {
            Object obj2 = ent.getValue();
            if (obj2 != null) {
              ret = concat(ret, sL + obj2.toString() + sR, sp);
            }
          }
        }
      }
      else
      {
        ret = concat(ret, sL + obj.toString() + sR, sp);
      }
    }
    return ret;
  }
}
