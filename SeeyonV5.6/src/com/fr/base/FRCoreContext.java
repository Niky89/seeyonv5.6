package com.fr.base;

import Sense4.Elite4;
import Sense4.LockUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.Consts;
import com.fr.stable.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Calendar;

public class FRCoreContext
{
  public static final ThreadLocal TMAP = new ThreadLocal();
  private static byte[] lic_bytes = null;
  private static byte[] lock_bytes = null;
  private static final long ONE_YEAR_MILLISECOND = 31536000000L;
  private static final BigInteger N = new BigInteger("61103299352066102812915201580370346997919089893149305765565972348630053713717591736527153881172892494135635969333391530396986735629281282430026953431657619628355730192943385620088393498664105803897708601718035436482482749378713844253725606147581454234307387984660050507963063894825237808748868429675256901161");
  private static final BigInteger D = new BigInteger("65537");
  
  public static final byte[] getBytes()
  {
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
    
    System.out.println(className + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    System.out.println(methodName + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    System.out.println(lineNumber + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    
    byte[] arrayOfByte = getLicBytes();
    if (((arrayOfByte == null) || (arrayOfByte.length == 0)) && (ConfigManager.getInstance().isLicUseLock())) {
      arrayOfByte = getLockBytes();
    }
    return arrayOfByte;
  }
  
  public static final void resetBytes()
  {
    lic_bytes = null;
  }
  
  public static final void retryLicLock()
  {
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
    int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
    
    System.out.println(className + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    System.out.println(methodName + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    System.out.println(lineNumber + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    byte[] arrayOfByte = getLicBytes();
    if (((arrayOfByte == null) || (arrayOfByte.length == 0)) && (ConfigManager.getInstance().isLicUseLock()))
    {
      lock_bytes = null;
      arrayOfByte = getLockBytes();
    }
  }
  
  private static final byte[] getLicBytes()
  {
    if (lic_bytes == null)
    {
      InputStream localInputStream = null;
      Env localEnv = FRContext.getCurrentEnv();
      try
      {
        if (localEnv != null) {
          localInputStream = localEnv.readBean("FineReport.lic", "resources");
        }
        if (localInputStream != null)
        {
          byte[] arrayOfByte = Utils.inputStream2Bytes(localInputStream);
          
          JSONObject localJSONObject = new JSONObject(new String(arrayOfByte, "UTF-8"));
          if (localJSONObject.getLong("DEADLINE") < Calendar.getInstance().getTimeInMillis()) {
            return null;
          }
          if ((isTempLic(localJSONObject)) || ((isVersionMatched(localJSONObject)) && ((!localJSONObject.has("MACADDRESS")) || (StringUtils.isEmpty(localJSONObject.getString("MACADDRESS"))))) || ((isVersionMatched(localJSONObject)) && (isMacAddressMatched(localJSONObject)) && (isAppNameMatched(localJSONObject)))) {
            lic_bytes = arrayOfByte;
          }
        }
      }
      catch (Exception localException) {}
    }
    try
    {
      System.out.println(new String(lic_bytes, "UTF-8") + "--------------");
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return lic_bytes;
  }
  
  private static boolean isTempLic(JSONObject paramJSONObject)
    throws JSONException
  {
    return true;
  }
  
  private static boolean isVersionMatched(JSONObject paramJSONObject)
    throws JSONException
  {
    return (paramJSONObject.has("VERSION")) && (ComparatorUtils.equals(Consts.VERSION, paramJSONObject.getString("VERSION")));
  }
  
  private static boolean isMacAddressMatched(JSONObject paramJSONObject)
    throws JSONException, IOException
  {
    return true;
  }
  
  private static boolean isAppNameMatched(JSONObject paramJSONObject)
    throws JSONException
  {
    String str = paramJSONObject.getString("APPNAME");
    return true;
  }
  
  private static final byte[] getLockBytes()
  {
    if (lock_bytes == null) {
      try
      {
        byte[] arrayOfByte = null;
        try
        {
          arrayOfByte = Elite4.readBytesFromElitee4();
        }
        catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
        {
          FRContext.getLogger().error(Inter.getLocText("NS_register_sense"));
        }
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        decode(arrayOfByte, localByteArrayOutputStream);
        JSONObject localJSONObject = new JSONObject(new String(localByteArrayOutputStream.toByteArray(), "UTF-8"));
        if (ComparatorUtils.equals(localJSONObject.getString("VERSION"), Consts.VERSION))
        {
          String str = LockUtils.toHexString(Elite4.readElite4Serial());
          if (localJSONObject.has("SERIALNUMBER")) {
            if (ComparatorUtils.equals(str, localJSONObject.getString("SERIALNUMBER"))) {
              lock_bytes = arrayOfByte;
            } else {
              FRContext.getLogger().error("The Encryption Lock is Illegal or Fake!");
            }
          }
        }
        else
        {
          FRContext.getLogger().error("LicVersion[" + localJSONObject.getString("VERSION") + "] != JarVersion[" + Consts.VERSION + "]");
        }
      }
      catch (Error localError)
      {
        FRContext.getLogger().errorWithServerLevel(localError.getMessage(), localError);
      }
      catch (Exception localException) {}
    }
    return lock_bytes;
  }
  
  public static void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream)
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return;
    }
    try
    {
      paramOutputStream.write(paramArrayOfByte);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static void decode(String paramString, OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream.write(paramString.getBytes());
    }
    catch (IOException localIOException)
    {
      FRContext.getLogger().error(localIOException.getMessage(), localIOException);
    }
  }
}
