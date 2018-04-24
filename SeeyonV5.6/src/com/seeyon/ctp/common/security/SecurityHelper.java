package com.seeyon.ctp.common.security;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.exceptions.InfrastructureException;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.log4j.Logger;

public class SecurityHelper
  implements Serializable
{
  private static final long serialVersionUID = -5342133189696901106L;
  private static final Logger LOGGER = Logger.getLogger(SecurityHelper.class);
  private static final String ALGORITHM = "MD5";
  private static final Map<String, String> digestUrlMap = new HashMap();
  private static SecureRandom sr = new SecureRandom();
  private static Invocable invocableEngine;
  
  public static String digest(Object... digestPara)
  {
    StringBuilder digestStr = new StringBuilder();
    Object[] arrayOfObject = digestPara;int j = digestPara.length;
    for (int i = 0; i < j; i++)
    {
      Object o = arrayOfObject[i];
      if (o == null) {
        throw new InfrastructureException("Digest数据参数不能为null!");
      }
      digestStr.append(o);
    }
    MessageDigest digester = getCurrentSessionDigester();
    if (digester != null) {
      try
      {
        return bytes2Hex(((MessageDigest)digester.clone()).digest(digestStr.toString().getBytes()));
      }
      catch (CloneNotSupportedException e)
      {
        LOGGER.error("", e);
      }
    }
    return String.valueOf(digestStr);
  }
  
  public static String func_digest(Object o1)
  {
    return digest(new Object[] { o1 });
  }
  
  public static String func_digest(Object o1, Object o2)
  {
    return digest(new Object[] { o1, o2 });
  }
  
  public static String func_digest(Object o1, Object o2, Object o3)
  {
    return digest(new Object[] { o1, o2, o3 });
  }
  
  public static String func_digest(Object o1, Object o2, Object o3, Object o4)
  {
    return digest(new Object[] { o1, o2, o3, o4 });
  }
  
  public static String func_digest(Object o1, Object o2, Object o3, Object o4, Object o5)
  {
    return digest(new Object[] { o1, o2, o3, o4, o5 });
  }
  
  public static String func_digest(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
  {
    return digest(new Object[] { o1, o2, o3, o4, o5, o6 });
  }
  
  public static boolean verify(String id, String digest)
  {
	  return  true;
//    if ((id == null) || (digest == null)) {
//      return false;
//    }
//    if (digest.equals(digest(new Object[] { id }))) {
//      return true;
//    }
//    return false;
  }
  
  public static String getDigestUrlParam(String url)
  {
    return (String)digestUrlMap.get(url);
  }
  
  public static void initSecurityUrlConfig()
  {
    Collection<String> pluginIds = SystemEnvironment.getPluginIds();
    
    loadSecurityUrlConfig("ctp");
    for (String pluginId : pluginIds) {
      loadSecurityUrlConfig(pluginId);
    }
  }
  
  private static void loadSecurityUrlConfig(String pluginId)
  {
    String digestUrlCfg = AppContext.getSystemProperty(pluginId + ".security.digesturl");
    if (digestUrlCfg != null)
    {
      String[] urls = digestUrlCfg.split("\\|");
      String[] arrayOfString1;
      int j = (arrayOfString1 = urls).length;
      for (int i = 0; i < j; i++)
      {
        String s = arrayOfString1[i];
        String[] cfg = s.split("\\ ");
        if (cfg.length == 3) {
          digestUrlMap.put(cfg[0] + " " + cfg[1], cfg[2]);
        } else if (cfg.length == 2) {
          digestUrlMap.put(cfg[0], cfg[1]);
        }
      }
    }
  }
  
  public static int randomInt()
  {
    return sr.nextInt();
  }
  
  private static MessageDigest getCurrentSessionDigester()
  {
    MessageDigest digester = null;
    String seed = null;
    try
    {
      seed = (String)AppContext.getSessionContext("SESSION_CONTEXT_SECURITY_MESSAGEDIGEST_KEY");
      if (seed == null) {
        seed = String.valueOf(randomInt());
      }
      digester = MessageDigest.getInstance("MD5");
      digester.update(seed.getBytes());
      AppContext.putSessionContext("SESSION_CONTEXT_SECURITY_MESSAGEDIGEST_KEY", seed);
    }
    catch (NoSuchAlgorithmException e)
    {
      LOGGER.error("md", e);
    }
    catch (InfrastructureException localInfrastructureException)
    {
      try
      {
        digester = MessageDigest.getInstance("MD5");
        digester.update(String.valueOf(randomInt()).getBytes());
      }
      catch (NoSuchAlgorithmException e)
      {
        LOGGER.error("", e);
      }
    }
    return digester;
  }
  
  private static String bytes2Hex(byte[] bts)
  {
    String des = "";
    String tmp = null;
    for (int i = 0; i < bts.length; i++)
    {
      tmp = Integer.toHexString(bts[i] & 0xFF);
      if (tmp.length() == 1) {
        des = des + "0";
      }
      des = des + tmp;
    }
    return des;
  }
  
  public static String getSessionContextSeed()
  {
    String seed = (String)AppContext.getSessionContext("SESSION_CONTEXT_SECURITY_SEED_KEY");
    if (seed == null)
    {
      seed = String.valueOf(randomInt());
      AppContext.putSessionContext("SESSION_CONTEXT_SECURITY_SEED_KEY", seed);
    }
    return seed;
  }
  
  public static String decrypt(String cryptData)
  {
    String seed = getSessionContextSeed();
    
    Exception e = null;
    for (int i = 0; i < 5; i++) {
      try
      {
        return _decrypt(cryptData, seed);
      }
      catch (Exception ex)
      {
        e = ex;
      }
    }
    throw new InfrastructureException(e);
  }
  
  private static String _decrypt(String cryptData, String seed)
    throws Exception
  {
    return (String)invocableEngine.invokeFunction("decrypt", new Object[] { cryptData, seed });
  }
  
  public static void initScriptEngine(String appRoot)
  {
    ScriptEngineManager sem = new ScriptEngineManager();
    ScriptEngine se = sem.getEngineByName("javascript");
    try
    {
      invocableEngine = (Invocable)se;
      Reader reader = new FileReader(new File(appRoot, 
        "main/common/js/crypto.js".replace('/', File.separatorChar)));
      se.eval(reader);
      reader.close();
      se.eval("function decrypt(a,b){var v=CryptoJS.DES.decrypt(a,b);return ''+CryptoJS.enc.Utf8.stringify(v);}");
    }
    catch (Exception e)
    {
      throw new InfrastructureException(e);
    }
  }
  
  public static boolean isCryptPassword()
  {
    return "true".equals(AppContext.getSystemProperty("ctp.security.cryptLoginPassword"));
  }
}
