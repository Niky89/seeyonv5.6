package com.seeyon.v3x.online;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;

public class Constant
{
  public static final String MESSAGE_CONFIG_CATEGORY = "message_setting";
  public static final String MESSAGE_CONFIG_ITEM = "_custom_enable";
  public static final String resource_main = "com.seeyon.v3x.main.resources.i18n.MainResources";
  public static final String resource_common = "com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources";
  
  public static enum USER_TYPE
  {
    system,  unit,  group,  user,  audit;
  }
  
  public static enum OPEN_TYPE
  {
    open,  href;
  }
  
  public static String getValueFromMainRes(String key, String... params)
  {
    return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", key, params);
  }
  
  public static String getApplicationCategory(int category)
  {
    return ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "application." + category + ".label", new Object[0]);
  }
  
  public static String getValueFromCommonRes(String key)
  {
    return ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", key, new Object[0]);
  }
}
