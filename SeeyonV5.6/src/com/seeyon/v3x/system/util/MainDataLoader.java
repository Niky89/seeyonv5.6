package com.seeyon.v3x.system.util;

import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.cache.CacheAccessable;
import com.seeyon.ctp.common.cache.CacheFactory;
import com.seeyon.ctp.common.cache.CacheMap;
import com.seeyon.ctp.common.config.manager.ConfigManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.flag.SysFlag;
import com.seeyon.ctp.common.po.config.ConfigItem;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.manager.OrgManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MainDataLoader
{
  private static Log log = LogFactory.getLog(MainDataLoader.class);
  private static final CacheAccessable cacheFactory = CacheFactory.getInstance(MainDataLoader.class);
  private String loginImagePath = null;
  private OrgManager orgManager;
  private ConfigManager configManager;
  private CacheMap<Long, AccountSymbol> accountSymbolMap = cacheFactory.createMap("AccountSymbolMap");
  private static MainDataLoader instance = new MainDataLoader();
  
  public static MainDataLoader getInstance()
  {
    return instance;
  }
  
  private Long groupAccountId = null;
  
  private MainDataLoader()
  {
    this.orgManager = ((OrgManager)AppContext.getBean("orgManager"));
    this.configManager = ((ConfigManager)AppContext.getBean("configManager"));
    
    ConfigItem configItem_login = this.configManager.getConfigItem("System_Login_Image", "loginBgFileName");
    if (configItem_login != null)
    {
      this.loginImagePath = configItem_login.getConfigValue();
      refreshLocalImage(configItem_login);
    }
    List<V3xOrgAccount> accountList = new ArrayList();
    try
    {
      accountList = this.orgManager.getAllAccounts();
    }
    catch (BusinessException e)
    {
      log.error("单位标识中取得单位列表异常:", e);
    }
    for (V3xOrgAccount account : accountList)
    {
      Long accountId = account.getId();
      AccountSymbol accountSymbol = null;
      
      List<ConfigItem> configItemList = this.configManager.listAllConfigByCategory("Account_Symbol_Config", accountId);
      if ((configItemList != null) && (!configItemList.isEmpty())) {
        accountSymbol = new AccountSymbol();
      }
      for (ConfigItem configItem : configItemList)
      {
        String configItemStr = configItem.getConfigItem();
        String configValue = configItem.getConfigValue();
        if ("logoFileName".equals(configItemStr))
        {
          accountSymbol.setLogoImagePath(configValue);
          refreshLocalImage(configItem);
        }
        else if ("isHiddenLogo".equals(configItemStr))
        {
          accountSymbol.setHiddenLogo(Boolean.parseBoolean(configValue));
        }
        else if ("bannerFileName".equals(configItemStr))
        {
          accountSymbol.setBannerImagePath(configValue);
          refreshLocalImage(configItem);
        }
        else if ("isTileBanner".equals(configItemStr))
        {
          accountSymbol.setTileBanner(Boolean.parseBoolean(configValue));
        }
        else if ("isHiddenAccountName".equals(configItemStr))
        {
          accountSymbol.setHiddenAccountName(Boolean.parseBoolean(configValue));
        }
        else if ("isHiddenGroupName".equals(configItemStr))
        {
          accountSymbol.setHiddenGroupName(Boolean.valueOf(Boolean.parseBoolean(configValue)));
        }
      }
      this.accountSymbolMap.put(accountId, accountSymbol);
      if (account.isGroup()) {
        this.groupAccountId = accountId;
      }
    }
  }
  
  public String getLoginImagePath()
  {
    String defaultLoginImagePath = "/common/skin/default" + com.seeyon.v3x.skin.Constants.getSkinSuffix() + "/images/login.gif";
    if (this.loginImagePath == null) {
      return defaultLoginImagePath;
    }
    if (this.loginImagePath.contains("/common/skin/default")) {
      return defaultLoginImagePath;
    }
    return this.loginImagePath;
  }
  
  public void setLoginImagePath(String loginImagePath)
  {
    this.loginImagePath = loginImagePath;
  }
  
  public AccountSymbol getAccountSymbol(long accountId)
  {
    AccountSymbol accountSymbol = (AccountSymbol)this.accountSymbolMap.get(Long.valueOf(accountId));
    if ((accountSymbol == null) || (accountSymbol.getLogoImagePath() == null) || (accountSymbol.getBannerImagePath() == null) || (accountSymbol.isHiddenGroupName() == null))
    {
      if (((Boolean)SysFlag.frontPage_isNeedGetSymbolFromGroup.getFlag()).booleanValue())
      {
        boolean isNeedGetSymbolFromGroup = false;
        try
        {
          isNeedGetSymbolFromGroup = (accountId == 1L) || ((accountId != this.groupAccountId.longValue()) && (this.orgManager.isAccountInGroupTree(Long.valueOf(accountId))));
        }
        catch (BusinessException e)
        {
          log.error("单位标识中判断单位是否在集团树下出现异常:", e);
        }
        if (isNeedGetSymbolFromGroup)
        {
          AccountSymbol groupAccountSymbol = (AccountSymbol)this.accountSymbolMap.get(this.groupAccountId);
          if (accountSymbol == null)
          {
            accountSymbol = groupAccountSymbol;
          }
          else
          {
            if ((accountSymbol.getLogoImagePath() == null) && (groupAccountSymbol != null)) {
              accountSymbol.setLogoImagePath(groupAccountSymbol.getLogoImagePath());
            }
            if ((accountSymbol.getBannerImagePath() == null) && (groupAccountSymbol != null)) {
              accountSymbol.setBannerImagePath(groupAccountSymbol.getBannerImagePath());
            }
            if ((accountSymbol.isHiddenGroupName() == null) && (groupAccountSymbol != null) && (groupAccountSymbol.isHiddenGroupName() != null)) {
              accountSymbol.setHiddenGroupName(groupAccountSymbol.isHiddenGroupName());
            }
          }
        }
      }
      else if (accountId == 1L)
      {
        try
        {
          List<V3xOrgAccount> accountsList = this.orgManager.getAllAccounts();
          for (V3xOrgAccount account : accountsList) {
            if (!account.isGroup())
            {
              accountSymbol = (AccountSymbol)this.accountSymbolMap.get(account.getId());
              break;
            }
          }
        }
        catch (BusinessException e)
        {
          log.error("企业版系统管理员取得单位标识异常:", e);
        }
      }
      if (accountSymbol == null) {
        accountSymbol = new AccountSymbol();
      }
      if (accountSymbol.getLogoImagePath() == null) {
        accountSymbol.setLogoImagePath(com.seeyon.ctp.system.Constants.DEFAULT_LOGO_NAME);
      }
      if (accountSymbol.getBannerImagePath() == null) {
        accountSymbol.setBannerImagePath(com.seeyon.ctp.system.Constants.DEFAULT_BANNER_NAME);
      }
      if (accountSymbol.isHiddenGroupName() == null) {
        accountSymbol.setHiddenGroupName(Boolean.valueOf(false));
      }
    }
    return accountSymbol;
  }
  
  public AccountSymbol getAccountSymbolFromMap(long accountId)
  {
    return (AccountSymbol)this.accountSymbolMap.get(Long.valueOf(accountId));
  }
  
  public void updateAccountSymbol(long accountId, AccountSymbol accountSymbol)
  {
    if (accountSymbol == null) {
      return;
    }
    this.accountSymbolMap.put(Long.valueOf(accountId), accountSymbol);
  }
  
  public void notifyUpdateAccountSymbol(long accountId)
  {
    this.accountSymbolMap.notifyUpdate(Long.valueOf(accountId));
  }
  
  public void deleteAccountSymbol(long accountId)
  {
    this.accountSymbolMap.remove(Long.valueOf(accountId));
  }
  
  public String getLogoImagePath(long accountId)
  {
    AccountSymbol accountSymbol = getAccountSymbol(accountId);
    if (accountSymbol != null) {
      return accountSymbol.getLogoImagePath();
    }
    return com.seeyon.ctp.system.Constants.DEFAULT_LOGO_NAME;
  }
  
  /* Error */
  public void refreshLocalImage(ConfigItem item)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +4 -> 5
    //   4: return
    //   5: new 233	java/lang/StringBuilder
    //   8: dup
    //   9: invokestatic 330	com/seeyon/ctp/common/SystemEnvironment:getApplicationFolder	()Ljava/lang/String;
    //   12: invokestatic 335	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   15: invokespecial 237	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   18: aload_1
    //   19: invokevirtual 89	com/seeyon/ctp/common/po/config/ConfigItem:getConfigValue	()Ljava/lang/String;
    //   22: invokevirtual 244	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: invokevirtual 250	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   28: astore_2
    //   29: aload_1
    //   30: invokevirtual 338	com/seeyon/ctp/common/po/config/ConfigItem:getExtConfigValue	()Ljava/lang/String;
    //   33: astore_3
    //   34: aload_3
    //   35: invokestatic 341	com/seeyon/ctp/util/Strings:isEmpty	(Ljava/lang/String;)Z
    //   38: ifeq +4 -> 42
    //   41: return
    //   42: new 345	com/seeyon/ctp/util/Base64
    //   45: dup
    //   46: invokespecial 347	com/seeyon/ctp/util/Base64:<init>	()V
    //   49: aload_3
    //   50: invokevirtual 348	java/lang/String:getBytes	()[B
    //   53: invokevirtual 352	com/seeyon/ctp/util/Base64:decode	([B)[B
    //   56: astore 4
    //   58: aconst_null
    //   59: astore 5
    //   61: new 356	java/io/FileOutputStream
    //   64: dup
    //   65: aload_2
    //   66: invokespecial 358	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   69: astore 5
    //   71: aload 5
    //   73: aload 4
    //   75: invokevirtual 359	java/io/FileOutputStream:write	([B)V
    //   78: goto +71 -> 149
    //   81: astore 6
    //   83: getstatic 32	com/seeyon/v3x/system/util/MainDataLoader:log	Lorg/apache/commons/logging/Log;
    //   86: new 233	java/lang/StringBuilder
    //   89: dup
    //   90: ldc_w 363
    //   93: invokespecial 237	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   96: aload_1
    //   97: invokevirtual 89	com/seeyon/ctp/common/po/config/ConfigItem:getConfigValue	()Ljava/lang/String;
    //   100: invokevirtual 244	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: invokevirtual 250	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: aload 6
    //   108: invokeinterface 108 3 0
    //   113: aload 5
    //   115: ifnull +48 -> 163
    //   118: aload 5
    //   120: invokevirtual 365	java/io/FileOutputStream:close	()V
    //   123: goto +40 -> 163
    //   126: pop
    //   127: goto +36 -> 163
    //   130: astore 7
    //   132: aload 5
    //   134: ifnull +12 -> 146
    //   137: aload 5
    //   139: invokevirtual 365	java/io/FileOutputStream:close	()V
    //   142: goto +4 -> 146
    //   145: pop
    //   146: aload 7
    //   148: athrow
    //   149: aload 5
    //   151: ifnull +12 -> 163
    //   154: aload 5
    //   156: invokevirtual 365	java/io/FileOutputStream:close	()V
    //   159: goto +4 -> 163
    //   162: pop
    //   163: return
    // Line number table:
    //   Java source line #264	-> byte code offset #0
    //   Java source line #265	-> byte code offset #5
    //   Java source line #266	-> byte code offset #18
    //   Java source line #265	-> byte code offset #25
    //   Java source line #267	-> byte code offset #29
    //   Java source line #269	-> byte code offset #34
    //   Java source line #270	-> byte code offset #41
    //   Java source line #271	-> byte code offset #42
    //   Java source line #272	-> byte code offset #58
    //   Java source line #274	-> byte code offset #61
    //   Java source line #275	-> byte code offset #71
    //   Java source line #276	-> byte code offset #81
    //   Java source line #277	-> byte code offset #83
    //   Java source line #279	-> byte code offset #113
    //   Java source line #281	-> byte code offset #118
    //   Java source line #282	-> byte code offset #126
    //   Java source line #278	-> byte code offset #130
    //   Java source line #279	-> byte code offset #132
    //   Java source line #281	-> byte code offset #137
    //   Java source line #282	-> byte code offset #145
    //   Java source line #286	-> byte code offset #146
    //   Java source line #279	-> byte code offset #149
    //   Java source line #281	-> byte code offset #154
    //   Java source line #282	-> byte code offset #162
    //   Java source line #287	-> byte code offset #163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	this	MainDataLoader
    //   0	164	1	item	ConfigItem
    //   28	38	2	path	String
    //   33	17	3	base64	String
    //   56	18	4	data	byte[]
    //   59	96	5	out	java.io.FileOutputStream
    //   81	26	6	e	java.io.IOException
    //   130	17	7	localObject	Object
    //   126	1	8	localIOException1	java.io.IOException
    //   145	1	9	localIOException2	java.io.IOException
    //   162	1	10	localIOException3	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   61	78	81	java/io/IOException
    //   118	123	126	java/io/IOException
    //   61	113	130	finally
    //   137	142	145	java/io/IOException
    //   154	159	162	java/io/IOException
  }
}
