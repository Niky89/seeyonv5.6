package com.seeyon.ctp.common.init;

import com.seeyon.ctp.common.exceptions.InfrastructureException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MclclzUtil
{
  private static final Xcyskm mcl = new Xcyskm(MclclzUtil.class.getClassLoader());
  public static boolean sfsdflkjfl = false;
  private static Map<String, Method> methodCache = new HashMap();
  private static Map<String, Class<?>> classCache = new HashMap();
  
  public static Class<?> ioiekc(String nm)
  {
    Class<?> c = null;
    try
    {
      if (sfsdflkjfl) {
        c = Class.forName(nm);
      } else {
        c = mcl.loadClass(nm);
      }
    }
    catch (ClassNotFoundException e)
    {
      throw new InfrastructureException(e);
    }
    return c;
  }
  
  public static Object invoke(Class<?> c1, String method)
  {
    return invoke(c1, method, null, null, null);
  }
  
  public static Object invoke(Class<?> c1, String method, Class<?>[] types, Object obj, Object[] args)
  {
    try
    {
      Method mainMethod = c1.getMethod(method, types);
      
      return mainMethod.invoke(obj, args);
    }
    catch (Exception e)
    {
      e.getCause().printStackTrace();
      throw new InfrastructureException(e);
    }
  }
  
  public static void setAttr(Class<?> c1, String fieldName, Object value)
  {
    try
    {
      Object o = c1.newInstance();
      Field f = o.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(o, value);
    }
    catch (Exception e)
    {
      e.getCause().printStackTrace();
      throw new InfrastructureException(e);
    }
  }
}
