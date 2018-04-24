package com.seeyon.v3x.system;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import java.util.Locale;

public class Constants
{
  private static final String resource_baseName = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
  
  public static String getString4CurrentUser(String key, Object... parameters)
  {
    return ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", key, parameters);
  }
  
  public static String getString(String key, Locale locale, Object... parameters)
  {
    return ResourceBundleUtil.getString("com.seeyon.v3x.system.resources.i18n.SysMgrResources", locale, key, 
      parameters);
  }
  
  public static String PATH_SEPARATOR = "/";
  public static final String USER_IMAGE_PATH = PATH_SEPARATOR + "USER-DATA" + PATH_SEPARATOR + "IMAGES" + PATH_SEPARATOR;
  public static final String CONFIG_CATRGORY_ACCOUNT_SYMBOL = "Account_Symbol_Config";
  public static final String CONFIG_CATRGORY_LOGIN_IMAGE = "System_Login_Image";
  public static final String CONFIG_ITEM_LOGO = "logoFileName";
  public static final String CONFIG_ITEM_LOGO_IsHidden = "isHiddenLogo";
  public static final String CONFIG_ITEM_BANNER = "bannerFileName";
  public static final String CONFIG_ITEM_BANNER_IsTile = "isTileBanner";
  public static final String CONFIG_ITEM_LOGIN_IMAGE = "loginBgFileName";
  public static final String CONFIG_ITEM_ACCOUNTNAME_IsHidden = "isHiddenAccountName";
  public static final String CONFIG_ITEM_GROUPNAME_IsHidden = "isHiddenGroupName";
  public static final String CONFIG_CATRGORY_COMMON_TOOLS = "common_tools";
  public static final String CONFIG_ITEM_WEATHER = "weather";
  public static final String OEM_SUFFIX = Functions.oemSuffixInJS();
  public static final String DEFAULT_LOGINBG_NAME = PATH_SEPARATOR + "apps_res" + PATH_SEPARATOR + "v3xmain" + PATH_SEPARATOR + "images" + PATH_SEPARATOR + "login.gif";
  public static final String DEFAULT_BANNER_NAME = PATH_SEPARATOR + "common" + PATH_SEPARATOR + "images" + PATH_SEPARATOR + "space.gif";
  public static final int METADATAITEM_SWITCH_ENABLE = 1;
  public static final int METADATAITEM_SWITCH_DISABLE = 0;
  public static final int METADATAITEM_ISSYSTEM_YES = 1;
  public static final int METADATAITEM_ISSYSTEM_NO = 0;
  public static final String SYSTEM_LOGIN_NAME = "system";
  public static final String AUDIT_ADMIN_LOGIN_NAME = "audit-admin";
  
  public static String getDefaultLogoName()
  {
    return PATH_SEPARATOR + "apps_res" + PATH_SEPARATOR + "v3xmain" + PATH_SEPARATOR + "images" + PATH_SEPARATOR + "logo" + com.seeyon.v3x.skin.Constants.getUserSkinSuffix() + ".gif";
  }
}
