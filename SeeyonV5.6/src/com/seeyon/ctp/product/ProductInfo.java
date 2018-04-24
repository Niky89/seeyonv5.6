package com.seeyon.ctp.product;

import java.io.File;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernet.utils.OutNa;

import com.kg.commons.utils.Base64Util;
import com.seeyon.ctp.cluster.ClusterConfigBean;
import com.seeyon.ctp.cluster.ClusterConfigValidator;
import com.seeyon.ctp.cluster.notification.NotificationManager;
import com.seeyon.ctp.cluster.notification.NotificationType;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.constants.ProductEditionEnum;
import com.seeyon.ctp.common.constants.ProductVersionEnum;
import com.seeyon.ctp.common.init.MclclzUtil;
import com.seeyon.ctp.product.dao.ProductInfoDaoImpl;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.TextEncoder;
import com.seeyon.ctp.util.json.JSONUtil;
import com.seeyon.m1.product.utils.SeeyonJSONUtils;
import com.seeyon.m1.product.utils.SeeyonSecurityUtils;
import com.seeyon.m1.product.utils.SeeyonVSMUtils;

import code2.www.seeyon.com.system.auth.SeeyonDog;
import www.seeyon.com.mocnoyees.CHKUMocnoyees;
import www.seeyon.com.mocnoyees.DogException;
import www.seeyon.com.mocnoyees.Enums;
import www.seeyon.com.mocnoyees.LRWMMocnoyees;
import www.seeyon.com.mocnoyees.MSGMocnoyees;
import www.seeyon.com.mocnoyees.VERMocnoyees;
import www.seeyon.com.utils.StringUtil;

public final class ProductInfo
{
  private static final Log logger = LogFactory.getLog(ProductInfo.class);
  private static Set<String> pluginInfos = new HashSet();
  private static int maxOnlineSize = 0;
  private static int maxRegisterSize = 0;
  private static int maxCompanySize = 0;
  private static Map<String, Map<String, Object>> dogBindingMap = new HashMap();
  private static final String fileSeparator = File.separator;
  private static MSGMocnoyees mocnoyeesA = null;
  private static VERMocnoyees mocnoyeesVer = null;
  private static boolean isVerDev = false;
  private static String productLine = null;
  private static String appFolder = SystemEnvironment.getApplicationFolder();
  
  static
  {
    if (MclclzUtil.sfsdflkjfl) {
      appFolder = "D:/runtime/v5/ApacheJetspeed/webapps/seeyon";
    }
  }
  
  private static final String versionFileFullPath = appFolder + fileSeparator + "common" + fileSeparator + "js" + fileSeparator + "ui" + fileSeparator + "portaletMenu.js";
  private static final String developEditionFileFullPath = appFolder + fileSeparator + "common" + fileSeparator + "js" + fileSeparator + "ui" + fileSeparator + "partaletindev.js";
  private static final String productFolder = new File(appFolder).getParentFile().getParentFile().getParentFile().getAbsolutePath();
  private static final String BaseLicenseFolder = productFolder + fileSeparator + "base" + fileSeparator + "license";
  private static String lisenceFileFullPath = BaseLicenseFolder + fileSeparator;
  private static final String signatureFileFullPath = appFolder + fileSeparator + "common" + fileSeparator + "js" + fileSeparator + "ui" + fileSeparator + "publicinfomenu.js";
  private static OutNa oldDog = null;
  private static boolean isOldTG = false;
  private Connection con = null;
  
  public synchronized void init()
  {
    try
    {
      ClusterConfigBean clusterConfigBean = ClusterConfigBean.getInstance();
      ClusterConfigValidator clusterConfigValidator = ClusterConfigValidator.getInstance();
      boolean isLoadDog = true;
      if (clusterConfigBean.isClusterEnabled())
      {
        initCluster();
        if (!clusterConfigBean.isClusterMain())
        {
          logger.info("当前是从服务器，开始从主服务器同步产品加密信息(最长需要20秒)...");
          int counter = 0;
          while ((!ClusterSlaveOK) && (counter++ < 10)) {
            try
            {
              Thread.sleep(2000L);
            }
            catch (InterruptedException localInterruptedException) {}
          }
          if (!ClusterSlaveOK)
          {
            out("不能启动集群/双机的从服务器，请先启动主服务器。[" + clusterConfigBean.toString() + "]");
            return;
          }
          logger.info("从主服务器同步产品加密信息完成.");
          isLoadDog = false;
        }
        clusterConfigValidator.validate();
      }
      if (isLoadDog)
      {
        try
        {
          mocnoyeesVer = new VERMocnoyees(versionFileFullPath);
        }
        catch (Throwable e)
        {
          out("验证产品版本文件无效: " + e.getMessage());
          return;
        }
        if (isNCOEM())
        {
          initEditionNC();
          initEdition();
          if (!MclclzUtil.sfsdflkjfl)
          {
            File sFile = new File(signatureFileFullPath);
            String rootFolder = sFile.getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/";
            CHKUMocnoyees.checkFile(signatureFileFullPath, rootFolder);
          }
        }
        else
        {
          try
          {
            if (mocnoyeesVer.methoddev(developEditionFileFullPath)) {
              isVerDev = true;
            }
          }
          catch (Throwable e)
          {
            logger.info("不是开发版");
          }
          initEdition();
          try
          {
            oldDog = new OutNa();
            oldDog.proc1(1);
            isOldTG = oldDog.proc9();
          }
          catch (Throwable e)
          {
            String msg = e.getMessage();
            if ((msg.contains("3023")) || (msg.contains("1008")) || (msg.contains("2021")) || (msg.contains("2022")) || 
              (msg.contains("2023")))
            {
              out("D-" + msg);
              return;
            }
          }
          try
          {
            if ((oldDog != null) && (isOldTG))
            {
              initDog2Cache();
            }
            else if (!isVerDev)
            {
              Class handlerClass = Class.forName("www.seeyon.com.mocnoyees.VERMocnoyees");
              ClassLoader cl = handlerClass.getClassLoader();
              String filepath = ((URL)cl.getResources("www/seeyon/com/mocnoyees/VERMocnoyees.class").nextElement()).toString();
              if (!filepath.endsWith("/WEB-INF/lib/mocnoyeeswz.jar!/www/seeyon/com/mocnoyees/VERMocnoyees.class"))
              {
                out("初始化产品文件的效验码无效, CL:V");
                return;
              }
              if (isU8OEM())
              {
                if (mocnoyeesA == null)
                {
                  String ls = SeeyonDog.getInstance().getLicense(mocnoyeesVer, getCompanyName());
                  logger.info(ls);
                  mocnoyeesA = new MSGMocnoyees(ls);
                }
              }
              else
              {
                LRWMMocnoyees lrwmmocnoyees = null;
                File licenseFile = new File(lisenceFileFullPath);
                if ((licenseFile != null) && (licenseFile.exists())) {
                  lrwmmocnoyees = new LRWMMocnoyees(licenseFile);
                } else {
                  lrwmmocnoyees = new LRWMMocnoyees(productLine);
                }
                if (mocnoyeesA == null)
                {
                  mocnoyeesA = new MSGMocnoyees(lrwmmocnoyees);
                  if (mocnoyeesA.methoda(productLine).equals(String.valueOf(Enums.UserTypeEnum.internal.getKey()))) {
                    isOldTG = true;
                  }
                }
              }
              if ((mocnoyeesA.methodzz("ncbusiness")) || (mocnoyeesA.methodzz("ncehr")) || (mocnoyeesA.methodzz("ncsupplychain")) || (mocnoyeesA.methodzz("ncfinance")) || (mocnoyeesA.methodzz("ncfdc")) || (mocnoyeesA.methodzz("u8business"))) {
                mocnoyeesA.methodxu("dee", "1");
              }
              if (mocnoyeesA.methodzz("thirdpartReport")) {
                mocnoyeesA.methodxu("seeyonreport", "1");
              }
              try
              {
                if (!String.valueOf(Enums.UserTypeEnum.internal.getKey()).equals(mocnoyeesA.methoda(productLine))) {
                  checkProductInfo4DogAndProgram();
                }
              }
              catch (Throwable e)
              {
                out("验证版本号和版本匹配情况异常: " + e.getMessage());
                return;
              }
              dataBind();
              if (!MclclzUtil.sfsdflkjfl)
              {
                File sFile = new File(signatureFileFullPath);
                String rootFolder = sFile.getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath() + "/";
                CHKUMocnoyees.checkFile(signatureFileFullPath, rootFolder);
              }
            }
          }
          catch (DogException de)
          {
            de.printStackTrace();
            out("验证产品加密狗无效: " + de.getErrorCode() + "-" + de.getErrorMsg());
            return;
          }
          catch (Throwable e)
          {
            e.printStackTrace();
            out("验证产品加密狗无效: " + e.getMessage());
            return;
          }
          initOnlineSize(isVerDev);
          initRegisterSize(isVerDev);
          initCompanySize(isVerDev);
          initDog2Cache();
          checkCompnayCount();
        }
      }
      checkProductInfoDBAndProgram();
      if (this.con != null) {
        try
        {
          this.con.close();
        }
        catch (SQLException localSQLException) {}
      }
      return;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public String getVersionFilePath()
  {
    return versionFileFullPath;
  }
  
  private static final Class<?> c3 = MclclzUtil.ioiekc("com.seeyon.ctp.permission.bo.LicensePerInfo");
  
  /* Error */
  private void checkCompnayCount()
  {
    // Byte code:
    //   0: ldc_w 496
    //   3: astore_1
    //   4: lconst_0
    //   5: lstore_2
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 5
    //   12: aconst_null
    //   13: astore 6
    //   15: aload_0
    //   16: invokespecial 498	com/seeyon/ctp/product/ProductInfo:getConnection	()Ljava/sql/Connection;
    //   19: astore 6
    //   21: aload 6
    //   23: invokeinterface 502 1 0
    //   28: astore 4
    //   30: aload 4
    //   32: aload_1
    //   33: invokeinterface 506 2 0
    //   38: astore 5
    //   40: aload 5
    //   42: invokeinterface 512 1 0
    //   47: ifeq +46 -> 93
    //   50: aload 5
    //   52: iconst_1
    //   53: invokeinterface 517 2 0
    //   58: lstore_2
    //   59: goto +34 -> 93
    //   62: astore 7
    //   64: aload 7
    //   66: invokevirtual 465	java/lang/Exception:printStackTrace	()V
    //   69: aload 5
    //   71: aload 4
    //   73: aconst_null
    //   74: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   77: goto +24 -> 101
    //   80: astore 8
    //   82: aload 5
    //   84: aload 4
    //   86: aconst_null
    //   87: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   90: aload 8
    //   92: athrow
    //   93: aload 5
    //   95: aload 4
    //   97: aconst_null
    //   98: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   101: invokestatic 527	com/seeyon/ctp/product/ProductInfo:getMaxCompanySize	()I
    //   104: ifle +34 -> 138
    //   107: lload_2
    //   108: invokestatic 527	com/seeyon/ctp/product/ProductInfo:getMaxCompanySize	()I
    //   111: i2l
    //   112: lcmp
    //   113: ifle +25 -> 138
    //   116: new 103	java/lang/StringBuilder
    //   119: dup
    //   120: ldc_w 530
    //   123: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   126: invokestatic 527	com/seeyon/ctp/product/ProductInfo:getMaxCompanySize	()I
    //   129: invokevirtual 532	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   132: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   138: return
    // Line number table:
    //   Java source line #281	-> byte code offset #0
    //   Java source line #282	-> byte code offset #4
    //   Java source line #283	-> byte code offset #6
    //   Java source line #284	-> byte code offset #9
    //   Java source line #285	-> byte code offset #12
    //   Java source line #287	-> byte code offset #15
    //   Java source line #288	-> byte code offset #21
    //   Java source line #289	-> byte code offset #30
    //   Java source line #290	-> byte code offset #40
    //   Java source line #291	-> byte code offset #50
    //   Java source line #293	-> byte code offset #59
    //   Java source line #294	-> byte code offset #64
    //   Java source line #296	-> byte code offset #69
    //   Java source line #295	-> byte code offset #80
    //   Java source line #296	-> byte code offset #82
    //   Java source line #297	-> byte code offset #90
    //   Java source line #296	-> byte code offset #93
    //   Java source line #298	-> byte code offset #101
    //   Java source line #299	-> byte code offset #116
    //   Java source line #301	-> byte code offset #138
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	139	0	this	ProductInfo
    //   3	30	1	sql	String
    //   5	103	2	companyCount	long
    //   7	89	4	s	java.sql.Statement
    //   10	84	5	rs	java.sql.ResultSet
    //   13	9	6	con	Connection
    //   62	3	7	e	Exception
    //   80	11	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   15	59	62	java/lang/Exception
    //   15	69	80	finally
  }
  
  /* Error */
  private String getCompanyName()
  {
	  return null;
    // Byte code:
    //   0: ldc_w 542
    //   3: astore_1
    //   4: ldc_w 544
    //   7: astore_2
    //   8: aconst_null
    //   9: astore_3
    //   10: aconst_null
    //   11: astore 4
    //   13: aconst_null
    //   14: astore 5
    //   16: aload_0
    //   17: invokespecial 498	com/seeyon/ctp/product/ProductInfo:getConnection	()Ljava/sql/Connection;
    //   20: astore 5
    //   22: aload 5
    //   24: invokeinterface 502 1 0
    //   29: astore_3
    //   30: aload_3
    //   31: aload_1
    //   32: invokeinterface 506 2 0
    //   37: astore 4
    //   39: aload 4
    //   41: invokeinterface 512 1 0
    //   46: ifeq +44 -> 90
    //   49: aload 4
    //   51: iconst_1
    //   52: invokeinterface 546 2 0
    //   57: astore_2
    //   58: goto +32 -> 90
    //   61: astore 6
    //   63: aload 6
    //   65: invokevirtual 465	java/lang/Exception:printStackTrace	()V
    //   68: aload 4
    //   70: aload_3
    //   71: aconst_null
    //   72: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   75: goto +22 -> 97
    //   78: astore 7
    //   80: aload 4
    //   82: aload_3
    //   83: aconst_null
    //   84: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   87: aload 7
    //   89: athrow
    //   90: aload 4
    //   92: aload_3
    //   93: aconst_null
    //   94: invokestatic 521	www/seeyon/com/utils/SQLUtil:close1	(Ljava/sql/ResultSet;Ljava/sql/Statement;Ljava/sql/Connection;)V
    //   97: aload_2
    //   98: areturn
    // Line number table:
    //   Java source line #307	-> byte code offset #0
    //   Java source line #308	-> byte code offset #4
    //   Java source line #309	-> byte code offset #8
    //   Java source line #310	-> byte code offset #10
    //   Java source line #311	-> byte code offset #13
    //   Java source line #313	-> byte code offset #16
    //   Java source line #314	-> byte code offset #22
    //   Java source line #315	-> byte code offset #30
    //   Java source line #316	-> byte code offset #39
    //   Java source line #317	-> byte code offset #49
    //   Java source line #319	-> byte code offset #58
    //   Java source line #320	-> byte code offset #63
    //   Java source line #322	-> byte code offset #68
    //   Java source line #321	-> byte code offset #78
    //   Java source line #322	-> byte code offset #80
    //   Java source line #323	-> byte code offset #87
    //   Java source line #322	-> byte code offset #90
    //   Java source line #324	-> byte code offset #97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	99	0	this	ProductInfo
    //   3	29	1	sql	String
    //   7	91	2	name	String
    //   9	84	3	s	java.sql.Statement
    //   11	80	4	rs	java.sql.ResultSet
    //   14	9	5	con	Connection
    //   61	3	6	e	Exception
    //   78	10	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   16	58	61	java/lang/Exception
    //   16	68	78	finally
  }
  
  private void checkRegisterSize()
  {
    try
    {
      Object o = MclclzUtil.invoke(c3, "getInstance", new Class[] { String.class }, null, new Object[] { "" });
      Integer serverType = (Integer)MclclzUtil.invoke(c3, "getserverType", null, o, null);
      boolean licensePerServerValid = ((Boolean)MclclzUtil.invoke(c3, "licensePerServerValid", null, o, null)).booleanValue();
      if ((serverType.intValue() == 1) && (!licensePerServerValid)) {
        out("系统启动失败，当前系统实际注册用户数超出授权注册用户数限制！" + getMaxRegisterSize());
      }
    }
    catch (Exception e)
    {
      out("系统启动失败，注册数授权验证异常：" + e.getMessage());
    }
  }
  
  private String bytes2Hex(byte[] bts)
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
  
  private void initDog2Cache()
  {
    Map dogMap = new HashMap();
    if ((oldDog != null) && (isOldTG))
    {
      dogMap.put("userType", Integer.valueOf(Enums.UserTypeEnum.internal.getKey()));
      dogMap.put("customName", "致远内部(通狗)");
      dogMap.put("dogNo", "-2");
    }
    else if (isVerDev)
    {
      dogMap.put("userType", Integer.valueOf(Enums.UserTypeEnum.internal.getKey()));
      dogMap.put("customName", "致远内部(开发)");
      dogMap.put("dogNo", "-1");
    }
    else
    {
      dogMap.put("userType", mocnoyeesA.methoda(productLine));
      dogMap.put("customName", mocnoyeesA.methodp(productLine));
      dogMap.put("dogNo", mocnoyeesA.methodk(productLine));
    }
    if (mocnoyeesA == null)
    {
      dogMap.put("versionNo", mocnoyeesVer.methodc(productLine));
      dogMap.put("versionName", mocnoyeesVer.methode(productLine));
    }
    else
    {
      dogMap.put("versionNo", mocnoyeesA.methode(productLine));
      dogMap.put("versionName", mocnoyeesA.methodd(productLine));
      dogMap.put("useEndDate", mocnoyeesA.methodh(productLine));
    }
    dogMap.put("mocnoyeesA", mocnoyeesA);
    dogBindingMap.put("dog", dogMap);
  }
  
  /* Error */
  private void initEditionNC()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: ldc_w 649
    //   5: invokestatic 651	com/seeyon/ctp/util/PropertiesUtil:getFromClasspath	(Ljava/lang/String;)Ljava/util/Properties;
    //   8: astore_2
    //   9: new 68	java/util/HashMap
    //   12: dup
    //   13: invokespecial 70	java/util/HashMap:<init>	()V
    //   16: astore_3
    //   17: aload_2
    //   18: invokevirtual 657	java/util/Properties:entrySet	()Ljava/util/Set;
    //   21: invokeinterface 663 1 0
    //   26: astore 4
    //   28: goto +50 -> 78
    //   31: aload 4
    //   33: invokeinterface 669 1 0
    //   38: checkcast 673	java/util/Map$Entry
    //   41: astore 5
    //   43: aload 5
    //   45: invokeinterface 675 1 0
    //   50: invokestatic 105	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   53: astore 6
    //   55: aload 5
    //   57: invokeinterface 677 1 0
    //   62: invokestatic 105	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   65: astore 7
    //   67: aload_3
    //   68: aload 7
    //   70: aload 6
    //   72: invokeinterface 602 3 0
    //   77: pop
    //   78: aload 4
    //   80: invokeinterface 680 1 0
    //   85: ifne -54 -> 31
    //   88: new 683	com/seeyon/ctp/product/util/GenerateKey
    //   91: dup
    //   92: invokespecial 685	com/seeyon/ctp/product/util/GenerateKey:<init>	()V
    //   95: astore 4
    //   97: aload 4
    //   99: invokevirtual 686	com/seeyon/ctp/product/util/GenerateKey:getPublicExponent	()Ljava/math/BigInteger;
    //   102: astore 5
    //   104: aload 4
    //   106: invokevirtual 690	com/seeyon/ctp/product/util/GenerateKey:getModulus	()Ljava/math/BigInteger;
    //   109: astore 6
    //   111: aload 4
    //   113: invokevirtual 693	com/seeyon/ctp/product/util/GenerateKey:getPrivateExponent	()Ljava/math/BigInteger;
    //   116: astore 7
    //   118: new 103	java/lang/StringBuilder
    //   121: dup
    //   122: invokestatic 88	com/seeyon/ctp/common/SystemEnvironment:getApplicationFolder	()Ljava/lang/String;
    //   125: invokestatic 105	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   128: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   131: ldc_w 696
    //   134: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   140: astore 8
    //   142: aload 8
    //   144: invokestatic 698	com/seeyon/ctp/util/Strings:getCanonicalPath	(Ljava/lang/String;)Ljava/lang/String;
    //   147: astore 8
    //   149: new 103	java/lang/StringBuilder
    //   152: dup
    //   153: aload 8
    //   155: invokestatic 105	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   158: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   161: getstatic 73	java/io/File:separator	Ljava/lang/String;
    //   164: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: ldc_w 703
    //   170: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: getstatic 73	java/io/File:separator	Ljava/lang/String;
    //   176: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   179: ldc_w 705
    //   182: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: invokestatic 707	com/seeyon/ctp/util/PropertiesUtil:getFromAbsolutepath	(Ljava/lang/String;)Ljava/util/Properties;
    //   191: astore 9
    //   193: aload 9
    //   195: ldc_w 710
    //   198: invokevirtual 712	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   201: astore 10
    //   203: aload 10
    //   205: ifnull +14 -> 219
    //   208: ldc_w 544
    //   211: aload 10
    //   213: invokevirtual 389	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   216: ifeq +10 -> 226
    //   219: ldc_w 715
    //   222: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   225: return
    //   226: new 103	java/lang/StringBuilder
    //   229: dup
    //   230: aload 10
    //   232: invokestatic 105	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   235: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   238: ldc_w 717
    //   241: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   244: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   247: astore 11
    //   249: new 719	com/seeyon/ctp/util/HttpClientUtil
    //   252: dup
    //   253: invokespecial 721	com/seeyon/ctp/util/HttpClientUtil:<init>	()V
    //   256: astore 12
    //   258: aload_2
    //   259: invokevirtual 722	java/util/Properties:values	()Ljava/util/Collection;
    //   262: ldc_w 726
    //   265: invokestatic 728	com/seeyon/ctp/product/ProductInfo:join	(Ljava/util/Collection;Ljava/lang/String;)Ljava/lang/String;
    //   268: astore 13
    //   270: aload 12
    //   272: aload 11
    //   274: ldc_w 732
    //   277: invokevirtual 734	com/seeyon/ctp/util/HttpClientUtil:open	(Ljava/lang/String;Ljava/lang/String;)V
    //   280: aload 12
    //   282: ldc_w 738
    //   285: aload 5
    //   287: invokevirtual 740	java/math/BigInteger:toString	()Ljava/lang/String;
    //   290: invokevirtual 743	com/seeyon/ctp/util/HttpClientUtil:addParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   293: aload 12
    //   295: ldc_w 746
    //   298: aload 6
    //   300: invokevirtual 740	java/math/BigInteger:toString	()Ljava/lang/String;
    //   303: invokevirtual 743	com/seeyon/ctp/util/HttpClientUtil:addParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   306: aload 12
    //   308: ldc_w 748
    //   311: aload 13
    //   313: invokevirtual 743	com/seeyon/ctp/util/HttpClientUtil:addParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   316: aload 12
    //   318: ldc_w 750
    //   321: aload_2
    //   322: ldc_w 752
    //   325: invokevirtual 712	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   328: invokevirtual 743	com/seeyon/ctp/util/HttpClientUtil:addParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   331: aload 12
    //   333: ldc_w 754
    //   336: aload_2
    //   337: ldc_w 756
    //   340: invokevirtual 712	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   343: invokevirtual 743	com/seeyon/ctp/util/HttpClientUtil:addParameter	(Ljava/lang/String;Ljava/lang/String;)V
    //   346: aload 12
    //   348: invokevirtual 758	com/seeyon/ctp/util/HttpClientUtil:send	()I
    //   351: pop
    //   352: aload 12
    //   354: aconst_null
    //   355: invokevirtual 761	com/seeyon/ctp/util/HttpClientUtil:getResponseBodyAsString	(Ljava/lang/String;)Ljava/lang/String;
    //   358: astore_1
    //   359: goto +56 -> 415
    //   362: astore 13
    //   364: new 103	java/lang/StringBuilder
    //   367: dup
    //   368: ldc_w 764
    //   371: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   374: aload 10
    //   376: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: ldc_w 766
    //   382: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   385: aload 13
    //   387: invokevirtual 250	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   390: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   393: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   396: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   399: aload 12
    //   401: invokevirtual 768	com/seeyon/ctp/util/HttpClientUtil:close	()V
    //   404: return
    //   405: astore 14
    //   407: aload 12
    //   409: invokevirtual 768	com/seeyon/ctp/util/HttpClientUtil:close	()V
    //   412: aload 14
    //   414: athrow
    //   415: aload 12
    //   417: invokevirtual 768	com/seeyon/ctp/util/HttpClientUtil:close	()V
    //   420: aload 12
    //   422: invokevirtual 769	com/seeyon/ctp/util/HttpClientUtil:getResponseHeader	()Ljava/util/Map;
    //   425: ldc_w 773
    //   428: invokeinterface 775 2 0
    //   433: checkcast 106	java/lang/String
    //   436: astore 13
    //   438: ldc_w 779
    //   441: aload 13
    //   443: invokevirtual 781	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   446: ifne +36 -> 482
    //   449: new 103	java/lang/StringBuilder
    //   452: dup
    //   453: ldc_w 764
    //   456: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   459: aload 10
    //   461: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   464: ldc_w 784
    //   467: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   470: aload 13
    //   472: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   475: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   478: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   481: return
    //   482: aload_1
    //   483: ifnull +511 -> 994
    //   486: aload_1
    //   487: ldc_w 786
    //   490: invokevirtual 788	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   493: istore 14
    //   495: aload_1
    //   496: ldc_w 792
    //   499: invokevirtual 788	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   502: istore 15
    //   504: iload 14
    //   506: iconst_m1
    //   507: if_icmple +455 -> 962
    //   510: iload 15
    //   512: iconst_m1
    //   513: if_icmple +449 -> 962
    //   516: aload_1
    //   517: iload 14
    //   519: iconst_5
    //   520: iadd
    //   521: iload 15
    //   523: invokevirtual 794	java/lang/String:substring	(II)Ljava/lang/String;
    //   526: astore 16
    //   528: aload 16
    //   530: aload 7
    //   532: aload 6
    //   534: invokestatic 798	com/seeyon/ctp/product/ProductInfo:decrypt	(Ljava/lang/String;Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/lang/String;
    //   537: astore 17
    //   539: aload 17
    //   541: ldc_w 802
    //   544: invokevirtual 804	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   547: astore 18
    //   549: aload 18
    //   551: iconst_0
    //   552: aaload
    //   553: invokestatic 808	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   556: putstatic 62	com/seeyon/ctp/product/ProductInfo:maxOnlineSize	I
    //   559: getstatic 62	com/seeyon/ctp/product/ProductInfo:maxOnlineSize	I
    //   562: ifle +204 -> 766
    //   565: aload 18
    //   567: iconst_2
    //   568: aaload
    //   569: invokestatic 808	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   572: putstatic 66	com/seeyon/ctp/product/ProductInfo:maxCompanySize	I
    //   575: iconst_3
    //   576: istore 19
    //   578: goto +33 -> 611
    //   581: aload 18
    //   583: iload 19
    //   585: aaload
    //   586: astore 20
    //   588: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   591: aload_3
    //   592: aload 20
    //   594: invokeinterface 775 2 0
    //   599: checkcast 106	java/lang/String
    //   602: invokeinterface 811 2 0
    //   607: pop
    //   608: iinc 19 1
    //   611: iload 19
    //   613: aload 18
    //   615: arraylength
    //   616: if_icmplt -35 -> 581
    //   619: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   622: ldc_w 814
    //   625: invokeinterface 811 2 0
    //   630: pop
    //   631: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   634: ldc_w 816
    //   637: invokeinterface 811 2 0
    //   642: pop
    //   643: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   646: ldc_w 818
    //   649: invokeinterface 811 2 0
    //   654: pop
    //   655: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   658: ldc_w 820
    //   661: invokeinterface 811 2 0
    //   666: pop
    //   667: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   670: ldc_w 408
    //   673: invokeinterface 811 2 0
    //   678: pop
    //   679: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   682: ldc_w 822
    //   685: invokeinterface 811 2 0
    //   690: pop
    //   691: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   694: ldc_w 824
    //   697: invokeinterface 811 2 0
    //   702: pop
    //   703: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   706: ldc_w 826
    //   709: invokeinterface 811 2 0
    //   714: pop
    //   715: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   718: ldc_w 828
    //   721: invokeinterface 811 2 0
    //   726: pop
    //   727: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   730: ldc_w 830
    //   733: invokeinterface 811 2 0
    //   738: pop
    //   739: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   742: ldc_w 832
    //   745: invokeinterface 811 2 0
    //   750: pop
    //   751: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   754: ldc_w 834
    //   757: invokeinterface 811 2 0
    //   762: pop
    //   763: goto +290 -> 1053
    //   766: getstatic 62	com/seeyon/ctp/product/ProductInfo:maxOnlineSize	I
    //   769: iconst_m1
    //   770: if_icmpne +169 -> 939
    //   773: iconst_5
    //   774: putstatic 62	com/seeyon/ctp/product/ProductInfo:maxOnlineSize	I
    //   777: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   780: aload_3
    //   781: invokeinterface 836 1 0
    //   786: invokeinterface 837 2 0
    //   791: pop
    //   792: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   795: ldc_w 814
    //   798: invokeinterface 811 2 0
    //   803: pop
    //   804: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   807: ldc_w 816
    //   810: invokeinterface 811 2 0
    //   815: pop
    //   816: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   819: ldc_w 818
    //   822: invokeinterface 811 2 0
    //   827: pop
    //   828: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   831: ldc_w 820
    //   834: invokeinterface 811 2 0
    //   839: pop
    //   840: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   843: ldc_w 408
    //   846: invokeinterface 811 2 0
    //   851: pop
    //   852: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   855: ldc_w 822
    //   858: invokeinterface 811 2 0
    //   863: pop
    //   864: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   867: ldc_w 824
    //   870: invokeinterface 811 2 0
    //   875: pop
    //   876: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   879: ldc_w 826
    //   882: invokeinterface 811 2 0
    //   887: pop
    //   888: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   891: ldc_w 828
    //   894: invokeinterface 811 2 0
    //   899: pop
    //   900: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   903: ldc_w 830
    //   906: invokeinterface 811 2 0
    //   911: pop
    //   912: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   915: ldc_w 832
    //   918: invokeinterface 811 2 0
    //   923: pop
    //   924: getstatic 60	com/seeyon/ctp/product/ProductInfo:pluginInfos	Ljava/util/Set;
    //   927: ldc_w 834
    //   930: invokeinterface 811 2 0
    //   935: pop
    //   936: goto +117 -> 1053
    //   939: new 103	java/lang/StringBuilder
    //   942: dup
    //   943: ldc_w 841
    //   946: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   949: getstatic 62	com/seeyon/ctp/product/ProductInfo:maxOnlineSize	I
    //   952: invokevirtual 532	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   955: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   958: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   961: return
    //   962: new 103	java/lang/StringBuilder
    //   965: dup
    //   966: ldc_w 764
    //   969: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   972: aload 10
    //   974: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   977: ldc_w 784
    //   980: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   983: aload_1
    //   984: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   987: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   990: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   993: return
    //   994: new 103	java/lang/StringBuilder
    //   997: dup
    //   998: ldc_w 764
    //   1001: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1004: aload 10
    //   1006: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1009: ldc_w 843
    //   1012: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1015: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1018: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   1021: return
    //   1022: astore_2
    //   1023: new 103	java/lang/StringBuilder
    //   1026: dup
    //   1027: ldc_w 845
    //   1030: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   1033: aload_1
    //   1034: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1037: ldc -21
    //   1039: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1042: aload_2
    //   1043: invokevirtual 847	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1046: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1049: invokestatic 237	com/seeyon/ctp/product/ProductInfo:out	(Ljava/lang/String;)V
    //   1052: return
    //   1053: return
    // Line number table:
    //   Java source line #395	-> byte code offset #0
    //   Java source line #398	-> byte code offset #2
    //   Java source line #397	-> byte code offset #8
    //   Java source line #399	-> byte code offset #9
    //   Java source line #400	-> byte code offset #17
    //   Java source line #401	-> byte code offset #31
    //   Java source line #402	-> byte code offset #43
    //   Java source line #403	-> byte code offset #55
    //   Java source line #404	-> byte code offset #67
    //   Java source line #400	-> byte code offset #78
    //   Java source line #406	-> byte code offset #88
    //   Java source line #407	-> byte code offset #97
    //   Java source line #408	-> byte code offset #104
    //   Java source line #409	-> byte code offset #111
    //   Java source line #410	-> byte code offset #118
    //   Java source line #411	-> byte code offset #142
    //   Java source line #412	-> byte code offset #149
    //   Java source line #413	-> byte code offset #193
    //   Java source line #414	-> byte code offset #203
    //   Java source line #415	-> byte code offset #219
    //   Java source line #416	-> byte code offset #225
    //   Java source line #418	-> byte code offset #226
    //   Java source line #419	-> byte code offset #249
    //   Java source line #421	-> byte code offset #258
    //   Java source line #422	-> byte code offset #270
    //   Java source line #423	-> byte code offset #280
    //   Java source line #424	-> byte code offset #293
    //   Java source line #425	-> byte code offset #306
    //   Java source line #426	-> byte code offset #316
    //   Java source line #427	-> byte code offset #331
    //   Java source line #428	-> byte code offset #346
    //   Java source line #429	-> byte code offset #352
    //   Java source line #430	-> byte code offset #359
    //   Java source line #431	-> byte code offset #364
    //   Java source line #434	-> byte code offset #399
    //   Java source line #432	-> byte code offset #404
    //   Java source line #433	-> byte code offset #405
    //   Java source line #434	-> byte code offset #407
    //   Java source line #435	-> byte code offset #412
    //   Java source line #434	-> byte code offset #415
    //   Java source line #436	-> byte code offset #420
    //   Java source line #437	-> byte code offset #438
    //   Java source line #438	-> byte code offset #449
    //   Java source line #439	-> byte code offset #481
    //   Java source line #441	-> byte code offset #482
    //   Java source line #442	-> byte code offset #486
    //   Java source line #443	-> byte code offset #495
    //   Java source line #444	-> byte code offset #504
    //   Java source line #445	-> byte code offset #516
    //   Java source line #446	-> byte code offset #528
    //   Java source line #448	-> byte code offset #539
    //   Java source line #449	-> byte code offset #549
    //   Java source line #450	-> byte code offset #559
    //   Java source line #453	-> byte code offset #565
    //   Java source line #454	-> byte code offset #575
    //   Java source line #455	-> byte code offset #581
    //   Java source line #458	-> byte code offset #588
    //   Java source line #454	-> byte code offset #608
    //   Java source line #460	-> byte code offset #619
    //   Java source line #461	-> byte code offset #631
    //   Java source line #462	-> byte code offset #643
    //   Java source line #463	-> byte code offset #655
    //   Java source line #464	-> byte code offset #667
    //   Java source line #465	-> byte code offset #679
    //   Java source line #466	-> byte code offset #691
    //   Java source line #467	-> byte code offset #703
    //   Java source line #468	-> byte code offset #715
    //   Java source line #469	-> byte code offset #727
    //   Java source line #470	-> byte code offset #739
    //   Java source line #471	-> byte code offset #751
    //   Java source line #472	-> byte code offset #763
    //   Java source line #473	-> byte code offset #773
    //   Java source line #476	-> byte code offset #777
    //   Java source line #477	-> byte code offset #792
    //   Java source line #478	-> byte code offset #804
    //   Java source line #479	-> byte code offset #816
    //   Java source line #480	-> byte code offset #828
    //   Java source line #481	-> byte code offset #840
    //   Java source line #482	-> byte code offset #852
    //   Java source line #483	-> byte code offset #864
    //   Java source line #484	-> byte code offset #876
    //   Java source line #485	-> byte code offset #888
    //   Java source line #486	-> byte code offset #900
    //   Java source line #487	-> byte code offset #912
    //   Java source line #488	-> byte code offset #924
    //   Java source line #489	-> byte code offset #936
    //   Java source line #490	-> byte code offset #939
    //   Java source line #491	-> byte code offset #961
    //   Java source line #494	-> byte code offset #962
    //   Java source line #495	-> byte code offset #993
    //   Java source line #498	-> byte code offset #994
    //   Java source line #499	-> byte code offset #1021
    //   Java source line #501	-> byte code offset #1022
    //   Java source line #502	-> byte code offset #1023
    //   Java source line #503	-> byte code offset #1052
    //   Java source line #505	-> byte code offset #1053
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1054	0	this	ProductInfo
    //   1	1033	1	result	String
    //   8	329	2	plugin2NCProductCode	java.util.Properties
    //   1022	21	2	e	Throwable
    //   16	765	3	NCProductCodePlugin	Map<String, String>
    //   26	53	4	iter	java.util.Iterator<java.util.Map.Entry<Object, Object>>
    //   95	17	4	g	com.seeyon.ctp.product.util.GenerateKey
    //   41	15	5	e	java.util.Map.Entry<Object, Object>
    //   102	184	5	publicExponent	BigInteger
    //   53	18	6	plugin	String
    //   109	424	6	modulus	BigInteger
    //   65	4	7	code	String
    //   116	415	7	privateExponent	BigInteger
    //   140	14	8	basePath	String
    //   191	3	9	ncProperties	java.util.Properties
    //   201	804	10	prefix	String
    //   247	26	11	url	String
    //   256	165	12	h	com.seeyon.ctp.util.HttpClientUtil
    //   268	44	13	NCProductCodeStr	String
    //   362	24	13	e	Throwable
    //   436	35	13	re	String
    //   405	8	14	localObject	Object
    //   493	28	14	b	int
    //   502	20	15	e	int
    //   526	3	16	text	String
    //   537	3	17	lic	String
    //   547	67	18	lics	String[]
    //   576	36	19	i	int
    //   586	7	20	p	String
    // Exception table:
    //   from	to	target	type
    //   258	359	362	java/lang/Throwable
    //   258	399	405	finally
    //   2	225	1022	java/lang/Throwable
    //   226	404	1022	java/lang/Throwable
    //   405	481	1022	java/lang/Throwable
    //   482	961	1022	java/lang/Throwable
    //   962	993	1022	java/lang/Throwable
    //   994	1021	1022	java/lang/Throwable
  }
  
  public static Map initDogBinding(String ver, String m1Lic)
  {
    if ((m1Lic == null) || ("".equals(m1Lic))) {
      return null;
    }
    try
    {
      String licJSON = TextEncoder.decode(m1Lic);
      Map licMap = (Map)JSONUtil.parseJSONString(licJSON, Map.class);
      Map result = checkDog(ver, licMap);
      if ("pass".equals(result.get("success")))
      {
        Map<String, Object> curBindMap = new HashMap();
        
        dogBindingMap.put(ver, curBindMap);
        
        curBindMap.put("MaxOnlineSize", licMap.get(ver + "MaxOnlineSize"));
        curBindMap.put("MaxRegisterSize", licMap.get(ver + "MaxRegisterSize"));
        curBindMap.put("VersionName", licMap.get(ver + "VersionName"));
        curBindMap.put("OverDate", licMap.get(ver + "OverDate"));
        
        ClusterConfigBean bean = ClusterConfigBean.getInstance();
        
        Map<String, Object> response = new HashMap();
        response.put("Action", "Response");
        response.put("ClusterName", bean.getClusterName());
        response.put("ProductInfo.DogBindingMap", dogBindingMap);
        
        response.put("mocnoyeesA", mocnoyeesA);
        NotificationManager.getInstance().send(NotificationType.ProductInfo, response, true);
      }
      return result;
    }
    catch (Throwable t) {}
    return new HashMap();
  }
  
  private static Map<String, Object> checkDog(String ver, Map data)
  {
    Map<String, Object> result = new HashMap();
    String msg = "fail";
    String secretKey = null;
    if (data != null)
    {
      String bindDogNum = (String)data.get(ver + "BindDogNum");
      String dogType = (String)data.get(ver + "DogType");
      String dogNum = getDogNo();
      if (dogNum == null) {
        dogNum = "null";
      }
      if (!bindDogNum.equals(dogNum))
      {
        msg = "dogerror";
      }
      else
      {
        String overdueDateStr = (String)data.get(ver + "OverDate");
        if (overdueDateStr != null)
        {
          boolean b = false;
          if ("-1".equals(overdueDateStr)) {
            b = true;
          } else {
            try
            {
              Date overdueDate = DateUtil.parse(overdueDateStr, "yyyy-MM-dd");
              if (overdueDate != null)
              {
                Date today = DateUtil.currentDate();
                if (today.before(overdueDate)) {
                  b = true;
                }
              }
            }
            catch (ParseException localParseException) {}
          }
          if (b)
          {
            msg = "pass";
            secretKey = dogNum + bindDogNum + UUID.randomUUID();
          }
          else
          {
            msg = "overdate";
          }
        }
      }
    }
    result.put("success", msg);
    result.put("key", secretKey);
    
    return result;
  }
  
  private void dataBind()
    throws Exception
  {
    if (mocnoyeesA.methoda(productLine).equals(String.valueOf(Enums.UserTypeEnum.internal.getKey()))) {
      return;
    }
    mocnoyeesA.checkDatabase(getConnection());
  }
  
  private static void initEdition()
  {
    String mocnoyeesVerEdition = mocnoyeesVer.methode("Ver");
    ProductEditionEnum edition = ProductEditionEnum.valueOfKey(mocnoyeesVerEdition);
    if (edition == null)
    {
      out("不正确的版本信息：" + mocnoyeesVerEdition);
      return;
    }
    ProductEditionEnum.initCurrentProductEdition(edition);
    
    productLine = mocnoyeesVer.methoda("productLine");
    lisenceFileFullPath = lisenceFileFullPath + productLine.toLowerCase() + "." + "seeyonkey";
  }
  
  private void initOnlineSize(boolean isVerDev)
  {
    if (isVerDev) {
      maxOnlineSize = 5;
    } else if (isOldTG) {
      maxOnlineSize = 10;
    } else {
      maxOnlineSize = Integer.parseInt(mocnoyeesA.methodj(productLine));
    }
  }
  
  private void initRegisterSize(boolean isVerDev)
  {
    if (isVerDev) {
      maxRegisterSize = 0;
    } else if (isOldTG) {
      maxRegisterSize = 0;
    } else {
      maxRegisterSize = Integer.parseInt(mocnoyeesA.methodq(productLine));
    }
  }
  
  private void initCompanySize(boolean isVerDev)
  {
    if (isVerDev) {
      maxCompanySize = 0;
    } else if (isOldTG) {
      maxCompanySize = 0;
    } else if (mocnoyeesA.methodz("orgMaxCompany") == null) {
      maxCompanySize = 0;
    } else {
      try
      {
        maxCompanySize = Integer.parseInt(mocnoyeesA.methodz("orgMaxCompany.orgMaxCompany1"));
      }
      catch (Exception e)
      {
        maxCompanySize = 0;
      }
    }
  }
  
  private void checkProductInfo4DogAndProgram()
    throws Exception
  {
    String mocnoyeesVerVersionNo = mocnoyeesVer.methodc("Ver");
    String mocnoyeesVerEdition = mocnoyeesVer.methode("Ver");
    
    String mocnoyeesDogVersionNo = mocnoyeesA.methode(productLine);
    String mocnoyeesDogEdition = mocnoyeesA.methodd(productLine);
    if ((StringUtil.isEmpty(mocnoyeesVerVersionNo)) || (!mocnoyeesVerVersionNo.equals(mocnoyeesDogVersionNo)))
    {
      out("版本文件版本号[" + mocnoyeesVerVersionNo + "]和加密狗版本号不一致[" + mocnoyeesDogVersionNo + "].");
      return;
    }
    if ((StringUtil.isEmpty(mocnoyeesVerEdition)) || (!mocnoyeesVerEdition.equals(mocnoyeesDogEdition)))
    {
      out(
        "版本文件版本[" + mocnoyeesVerEdition + "]和加密狗版本不一致[" + mocnoyeesDogEdition + "].\n说明:A6V5-1:A6企业版, A6V5-2:A6-s版,A8V5-1:企业版, A8V5-2:集团版, G6V5-1:政务版, G6V5-2:政务多组织版, NCV5-1:NC协同OA");
      return;
    }
  }
  
  private void checkProductInfoDBAndProgram()
  {
    ProductEditionEnum currentProductEdition = getCurrentProductEdition();
    
    ProductInfoDaoImpl productInfoDao = new ProductInfoDaoImpl(getConnection());
    
    Map<String, String> configs = productInfoDao.getProductInfoConfigs();
    
    ProductVersionEnum currentVersion = ProductVersionEnum.getCurrentVersion();
    
    String version = (String)configs.get("version");
    if ((version == null) || (!currentVersion.getCanonicalVersion().equals(version)))
    {
      out(
        productLine + "数据表版本号[" + version + "]和" + productLine + "应用程序版本号不一致[" + currentVersion.getCanonicalVersion() + "].");
      return;
    }
    String productEdition = (String)configs.get("productEdition");
    if ((productEdition == null) || (!productEdition.equals(String.valueOf(currentProductEdition.getValue()))))
    {
      out(
        productLine + "数据表版本名称[" + productEdition + "]和" + "应用程序版本名称不一致[" + currentProductEdition.getValue() + "].\n说明:A6V5-1:A6企业,A6V5-2:A6-s版, A8V5-1:企业版, A8V5-2:集团版, G6V5-1:政务版, G6V5-2:政务多组织版, NCV5-1:NC协同OA");
      return;
    }
    logger.info("当前产品版本: " + currentProductEdition + "; " + getEditionA() + "; " + 
      currentVersion.getCanonicalVersion());
  }
  
  private static ProductEditionEnum getCurrentProductEdition()
  {
    return ProductEditionEnum.getCurrentProductEditionEnum();
  }
  
  public static boolean hasPlugin(String pluginId)
  {
    return checkPlugin(pluginId);
  }
  
  private static boolean checkPlugin(String pluginId)
  {
    if (pluginInfos.contains(pluginId)) {
      return true;
    }
    try
    {
      if ((isVerDev) || (isOldTG))
      {
        boolean valid = false;
        if ("mm1".equals(pluginId))
        {
          if ((getM1MaxOnline() > 0) || (getM1MaxRegisterSize() > 0)) {
            valid = true;
          }
        }
        else
        {
          Info info = (Info)PlugInList.getAllPluginList4ProductLine(productLine).get(pluginId);
          if (info == null) {
            valid = false;
          } else {
            valid = true;
          }
        }
        if (valid) {
          pluginInfos.add(pluginId);
        }
        return valid;
      }
      boolean isValid;
      if ("indexResume".equals(pluginId))
      {
        isValid = isValidPlugin("index");
      }
      else
      {
        if ("mm1".equals(pluginId))
        {
          if ((getM1MaxOnline() > 0) || (getM1MaxRegisterSize() > 0)) {
            isValid = true;
          } else {
            isValid = false;
          }
        }
        else
        {
          isValid = isValidPlugin(pluginId);
        }
      }
      if (isValid)
      {
        pluginInfos.add(pluginId);
        return true;
      }
    }
    catch (Throwable localThrowable) {}
    return false;
  }
  
  private static boolean isValidPlugin(String pluginId)
  {
    boolean isValid = false;
    try
    {
      isValid = mocnoyeesA.methodzz(pluginId);
    }
    catch (Exception localException) {}
    if (!isValid)
    {
      List<Info> pluginInfoList = new ArrayList(PlugInList.getAllPluginList4ProductLine(productLine).values());
      if (pluginInfoList != null)
      {
        boolean contains = false;
        for (Info info : pluginInfoList) {
          if (info.getKey().equals(pluginId))
          {
            contains = true;
            if (info.isDisplay()) {
              break;
            }
            if (info.getValue() == 1)
            {
              isValid = true;
              break;
            }
            if (info.getValue() != 0) {
              break;
            }
            isValid = false;
            
            break;
          }
        }
      }
    }
    return isValid;
  }
  
  public static boolean isExceedMaxLoginNumber(int number, String loginName)
  {
    return number > maxOnlineSize;
  }
  
  public static boolean isExceedMaxLoginNumberM1(int number)
  {
    return number > getM1MaxOnline();
  }
  
  private Connection getConnection()
  {
    if (this.con == null) {
      try
      {
        Context ic = new InitialContext();
        DataSource source = null;
        try
        {
          source = (DataSource)ic.lookup("java:comp/env/jdbc/ctpDataSource");
        }
        catch (Exception e)
        {
          source = (DataSource)ic.lookup("jdbc/ctpDataSource");
        }
        this.con = source.getConnection();
      }
      catch (NamingException e)
      {
        out("数据源查找失败：" + e.getMessage());
      }
      catch (SQLException e)
      {
        out("获取数据库连接失败：" + e.getMessage());
      }
    }
    return this.con;
  }
  
  private static void out(String message)
  {
    System.out.println("**************************************************************************");
    System.out.println("");
    System.out.println("Exception,Error : " + message);
    System.out.println("");
    System.out.println("**************************************************************************");
    try
    {
      Thread.sleep(5000L);
    }
    catch (Throwable localThrowable) {}
    System.exit(-1);
  }
  
  private static String decrypt(String text, BigInteger privateExponent, BigInteger modulus)
  {
    try
    {
      byte[] data = Base64Util.decodeByte(text, "utf-8");
      KeyFactory keyFac = KeyFactory.getInstance("RSA");
      RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);
      PrivateKey privateKey = keyFac.generatePrivate(priKeySpec);
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(2, privateKey);
      return new String(cipher.doFinal(data));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
  
  private static String join(Collection<Object> c, String separator)
  {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Object o : c)
    {
      if (i > 0) {
        sb.append(separator);
      }
      sb.append(o);
      i++;
    }
    return sb.toString();
  }
  
  private static String ClusterTempHostNo = String.valueOf(UUID.randomUUID().getMostSignificantBits());
  private static boolean ClusterSlaveOK = false;
  
  private void initCluster()
  {
    ClusterConfigBean bean = ClusterConfigBean.getInstance();
    ProductInfoProxy proxy = new ProductInfoProxy();
    proxy.setClusterSlaveOK(ClusterSlaveOK, false);
    proxy.setClusterTempHostNo(ClusterTempHostNo, false);
    proxy.setDogBindingMap(dogBindingMap, false);
    proxy.setMaxCompanySize(maxCompanySize, false);
    proxy.setMaxOnlineSize(maxOnlineSize, false);
    proxy.setMaxRegisterSize(maxRegisterSize, false);
    proxy.setPluginInfos(pluginInfos, false);
    proxy.setProductLine(productLine, false);
    
    NotificationManager.getInstance().register(new ProductNotificationListener(proxy));
    if (!bean.isClusterMain())
    {
      Map<String, Object> request = new HashMap();
      request.put("Action", "Request");
      request.put("ClusterName", bean.getClusterName());
      request.put("TempHostNo", ClusterTempHostNo);
      request.put("ClusterHostIndex", bean.getClusterHostIndex());
      NotificationManager.getInstance().send(NotificationType.ProductInfo, request, true);
    }
  }
  
  public static String m1ProductInfo(String str)
  {
    String success = "fail";
    String returnValue = "unknow";
    Map<String, String> checkValue = null;
    try
    {
      String dogNumStr = getDogNo();
      String dogNum;
      if (dogNumStr != null) {
        dogNum = TextEncoder.decode(dogNumStr);
      } else {
        dogNum = String.valueOf(dogNumStr);
      }
      String dataStr = SeeyonSecurityUtils.decrypt(str, dogNum);
      Map<String, String> data = (Map)SeeyonJSONUtils.readValue(dataStr, Map.class);
      
      String maxOnlineSize = (String)data.get("concurrentNum");
      String maxRegistSize = (String)data.get("registerNum");
      String versionStr = (String)data.get("m1VersionNum");
      if (versionStr == null) {
        versionStr = "5.0.0";
      }
      String versionName = "V" + versionStr;
      String overDate = (String)data.get("overdueDate");
      String dogType = (String)data.get("dogType");
      String bindDogNo = (String)data.get("bindDogNum");
      if ((!"1".equals(dogType)) && (dogNumStr != null)) {
        bindDogNo = TextEncoder.encode(bindDogNo);
      }
      Map<String, String> initM1 = new HashMap();
      initM1.put("M1MaxOnlineSize", maxOnlineSize);
      initM1.put("M1MaxRegisterSize", maxRegistSize);
      initM1.put("M1VersionName", versionName);
      initM1.put("M1OverDate", overDate);
      initM1.put("M1DogType", dogType);
      initM1.put("M1BindDogNum", bindDogNo);
      
      String temp = SeeyonJSONUtils.writeValueAsString(initM1);
      String initStr = TextEncoder.encode(temp);
      
      checkValue = initDogBinding("M1", initStr);
      
      SeeyonVSMUtils.setSecretKey(checkValue);
    }
    catch (Exception e)
    {
      checkValue = new HashMap();
      checkValue.put("success", success);
      checkValue.put("key", returnValue);
    }
    String result = SeeyonJSONUtils.writeValueAsString(checkValue);
    return result;
  }
  
  public static String getEditionA()
  {
    return ProductEditionEnum.getCurrentProductEditionEnum().getName() + (isVerDev ? ".development" : "product");
  }
  
  public static String getSpVersion()
  {
    String res = ProductVersionEnum.getCurrentVersion().getSpVersion();
    if (res != null) {
      return res;
    }
    return "";
  }
  
  public static String getM1Version()
  {
    Map bindMap = (Map)dogBindingMap.get("M1");
    if (bindMap != null) {
      return (String)bindMap.get("VersionName");
    }
    return "";
  }
  
  public static String getM1OverDate()
  {
    Map bindMap = (Map)dogBindingMap.get("M1");
    if (bindMap != null) {
      return (String)bindMap.get("OverDate");
    }
    return "";
  }
  
  public static boolean isU8OEM()
  {
    return (mocnoyeesVer != null) && (mocnoyeesVer.methodg("U8+") != null) && (mocnoyeesVer.methodg("U8+").equals("U8+"));
  }
  
  public static boolean isNCOEM()
  {
    return (mocnoyeesVer != null) && (mocnoyeesVer.methodg("NC") != null) && (mocnoyeesVer.methodg("NC").equals("NC"));
  }
  
  public static boolean isCworkInner()
  {
    if ((mocnoyeesVer != null) && (mocnoyeesVer.methoda("CWORK") != null) && (mocnoyeesVer.methoda("CWORK").equals("CWORK")) && 
      (hasPlugin("cworkInner"))) {
      return true;
    }
    return false;
  }
  
  public static boolean isCworkOuter()
  {
    if ((mocnoyeesVer != null) && (mocnoyeesVer.methoda("CWORK") != null) && (mocnoyeesVer.methoda("CWORK").equals("CWORK")) && 
      (hasPlugin("cworkInner"))) {
      return false;
    }
    return false;
  }
  
  public static int getMaxOnline()
  {
    return maxOnlineSize;
  }
  
  public static int getMaxRegisterSize()
  {
    return maxRegisterSize;
  }
  
  public static int getMaxCompanySize()
  {
    return maxCompanySize;
  }
  
  public static int getM1MaxOnline()
  {
    if (isU8OEM()) {
      return SeeyonDog.getInstance().getM1();
    }
    Map bindMap = (Map)dogBindingMap.get("M1");
    if (bindMap != null) {
      return Integer.parseInt((String)bindMap.get("MaxOnlineSize"));
    }
    return 0;
  }
  
  public static int getM1MaxRegisterSize()
  {
    Map bindMap = (Map)dogBindingMap.get("M1");
    if (bindMap != null) {
      return Integer.parseInt((String)bindMap.get("MaxRegisterSize"));
    }
    return 0;
  }
  
  public static String getPlugin(String pluginId)
  {
    return mocnoyeesA == null ? null : mocnoyeesA.methodz(pluginId);
  }
  
  public static String getDogNo()
  {
    String dogNo = (String)((Map)dogBindingMap.get("dog")).get("dogNo");
    dogNo = TextEncoder.encode(dogNo);
    return dogNo;
  }
  
  public static String getUserType()
  {
    return (String)((Map)dogBindingMap.get("dog")).get("userType");
  }
  
  public static String getVersion()
  {
    String res = ProductVersionEnum.getCurrentVersion().getMainVersion();
    if (res != null) {
      return res;
    }
    return "";
  }
  
  public static String getVersionNo()
  {
    return (String)((Map)dogBindingMap.get("dog")).get("versionNo");
  }
  
  public static String getVersionName()
  {
    return (String)((Map)dogBindingMap.get("dog")).get("versionName");
  }
  
  public static String getCustomName()
  {
    return (String)((Map)dogBindingMap.get("dog")).get("customName");
  }
  
  public static String getUseEndDate()
  {
    return (String)((Map)dogBindingMap.get("dog")).get("useEndDate");
  }
  
  public static String getProductLine()
  {
    return productLine;
  }
  
  public static boolean isDev()
  {
    return isVerDev;
  }
  
  public static boolean isTongDog()
  {
    return isOldTG;
  }
}
