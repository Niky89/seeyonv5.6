package www.seeyon.com.biz;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import www.seeyon.com.biz.enums.BizOperationEnum;
import www.seeyon.com.biz.enums.FormSourceTypeEnum;
import www.seeyon.com.utils.Base64Util;
import www.seeyon.com.utils.DesUtil;
import www.seeyon.com.utils.ReflectUtil;

public class BizChicken
{
  public static final int no_limit = -1;
  private static final int _$7 = 10;
  private static final int _$6 = 0;
  private static int _$5 = 999999;
  private static int _$4 = 10;
  private static int _$3 = 0;
  private static final String _$2 = "::";
  private static final String _$1 = "[oy8;h;flegku$324@jlfj2o93893/fdfrh024ufoklsdro";
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
     String []ss= _$1("279119AE5ABE5F1CF44762558DF01B7B7C7B128B813FFD3EF3F30080F8E8FC7E5CC0524C0AE786EE");
     
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public static List<Long> initUsedCount(List<Map<String, Object>> paramList)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    boolean bool = ((Boolean)ReflectUtil.invokeMethod("com.seeyon.ctp.common.SystemEnvironment", "isDevOrTG", null, null, null)).booleanValue();
    _$1(bool);
    return _$1(bool, paramList);
  }
  
  private static void _$1(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      _$4 = -1;
    }
    else
    {
      String str = null;
      boolean bool = ((Boolean)ReflectUtil.invokeMethod("com.seeyon.ctp.common.AppContext", "hasPlugin", null, new Class[] { String.class }, new String[] { "formBiz" })).booleanValue();
      if (!bool)
      {
        _$4 = 0;
      }
      else
      {
        str = (String)ReflectUtil.invokeMethod("com.seeyon.ctp.common.AppContext", "getPlugin", null, new Class[] { String.class }, new String[] { "formBiz.formBiz1" });
        try
        {
          if (null == str)
          {
            _$4 = 10;
            System.out.println(_$6("6Kej5p6Q5oC75Lqn6IO95byC5bi477yM5L2/55So6buY6K6k5Lqn6IO9MTA="));
          }
          else
          {
            _$4 = Integer.parseInt(str.trim());
          }
        }
        catch (Exception localException)
        {
          _$4 = 10;
          System.out.println(_$6("5pWw5a2X5qC85byP5YyW5aSx6LSl77yM5ZWG5Yqh5b2V5YWl55qE5L+h5oGv5pyJ6ZSZ6K+v"));
        }
      }
    }
  }
  
  private static List<Long> _$1(boolean paramBoolean, List<Map<String, Object>> paramList)
    throws BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException
  {
    ArrayList localArrayList1 = null;
    if (!paramBoolean)
    {
      localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      ArrayList localArrayList3 = new ArrayList();
      _$1(paramList, localArrayList3, localArrayList2, localArrayList1);
      List localList;
      if (localArrayList3.size() > getAllowNum())
      {
        localList = localArrayList3.subList(getAllowNum(), localArrayList3.size());
        localArrayList1.addAll(localList);
        localArrayList1.addAll(localArrayList2);
        _$3 = getAllowNum();
      }
      else
      {
        int i = getAllowNum() - localArrayList3.size();
        if (i > 0)
        {
          if (localArrayList2.size() > i)
          {
            localList = localArrayList2.subList(i, localArrayList2.size());
            localArrayList1.addAll(localList);
            _$3 = getAllowNum();
          }
          else
          {
            _$3 = localArrayList3.size() + localArrayList2.size();
          }
        }
        else
        {
          _$3 = getAllowNum();
          localArrayList1.addAll(localArrayList2);
        }
      }
    }
    return localArrayList1;
  }
  
  private static void _$1(List<Map<String, Object>> paramList, List<Long> paramList1, List<Long> paramList2, List<Long> paramList3)
    throws BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException
  {
    String str1 = BizEgg.getCustomerName();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Map localMap = (Map)localIterator.next();
      Long localLong = (Long)localMap.get("id");
      String str2 = (String)localMap.get("sourceType");
      Integer localInteger = (Integer)localMap.get("useFlag");
      String[] arrayOfString = _$1(str2);
      if (!_$1(arrayOfString, localLong))
      {
        paramList3.add(localLong);
      }
      else if (_$5(arrayOfString[2]))
      {
        if (localInteger.intValue() == 1) {
          if (_$1(arrayOfString, localLong, false))
          {
            if (str1.equals(arrayOfString[2])) {
              paramList1.add(localLong);
            } else {
              paramList2.add(localLong);
            }
          }
          else if (!str1.equals(arrayOfString[2])) {
            paramList3.add(localLong);
          }
        }
        if (localInteger.intValue() == 0)
        {
          if (_$2(arrayOfString, localLong)) {
            _$5 += 1;
          }
          if ((!_$1(arrayOfString, localLong, false)) && (!str1.equals(arrayOfString[2]))) {
            paramList3.add(localLong);
          }
        }
      }
    }
  }
  
  private static String _$6(String paramString)
  {
    return Base64Util.decode(paramString);
  }
  
  private static boolean _$2(String paramString, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    String[] arrayOfString = _$1(paramString);
    return _$1(arrayOfString, paramLong, true);
  }
  
  private static boolean _$5(String paramString)
  {
    return (!_$2(paramString)) && (!_$3(paramString));
  }
  
  private static boolean _$4(String paramString)
  {
    return "致远内部".equals(paramString);
  }
  
  private static boolean _$3(String paramString)
  {
    return "公有蛋".equals(paramString);
  }
  
  private static boolean _$2(String paramString)
  {
    return (FormSourceTypeEnum.create_form_upgrade.getKey() + "").equals(paramString);
  }
  
  private static boolean _$1(String[] paramArrayOfString, Long paramLong, boolean paramBoolean)
  {
    if (!_$1(paramArrayOfString, paramLong)) {
      return true;
    }
    FormSourceTypeEnum localFormSourceTypeEnum = getFormSourceType(paramArrayOfString, paramLong);
    boolean bool = localFormSourceTypeEnum.isEffectNum();
    if ((!bool) && (paramBoolean) && (_$5(paramArrayOfString[2])))
    {
      String str = BizEgg.getCustomerName();
      bool = !str.equals(paramArrayOfString[2]);
    }
    return bool;
  }
  
  private static boolean _$2(String[] paramArrayOfString, Long paramLong)
  {
    FormSourceTypeEnum localFormSourceTypeEnum = getFormSourceType(paramArrayOfString, paramLong);
    return localFormSourceTypeEnum.isEffectNum4Update();
  }
  
  public static FormSourceTypeEnum getFormSourceType(String paramString, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    String[] arrayOfString = _$1(paramString);
    return getFormSourceType(arrayOfString, paramLong);
  }
  
  public static FormSourceTypeEnum getFormSourceType(String[] paramArrayOfString, Long paramLong)
  {
    if (!_$1(paramArrayOfString, paramLong)) {
      return FormSourceTypeEnum.create_form_custom;
    }
    String str = paramArrayOfString[0];
    return FormSourceTypeEnum.getTypeByKey(Integer.parseInt(str));
  }
  
  private static boolean _$1(String paramString, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    String[] arrayOfString = _$1(paramString);
    return _$1(arrayOfString, paramLong);
  }
  
  private static boolean _$1(String[] paramArrayOfString, Long paramLong)
  {
    if ((paramArrayOfString == null) || (paramArrayOfString.length != 3))
    {
      System.out.println(_$6("6Kej5a+G5aSx6LSl77yM5pyJ56+h5pS55auM55aR77yM5YGc55So"));
      return false;
    }
    if (!paramLong.toString().equals(paramArrayOfString[1]))
    {
      System.out.println(_$6("SUTkuI3ljLnphY3vvIzmnInmi7fotJ3lq4znlpHvvIznrpfkvZzlt7LnlKg="));
      return false;
    }
    return true;
  }
  
  public static String getEncodeString(FormSourceTypeEnum paramFormSourceTypeEnum, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
  {
    return getEncodeString(paramFormSourceTypeEnum, paramLong, paramFormSourceTypeEnum.getCustomerName());
  }
  
  public static String getEncodeString(BizEgg paramBizEgg, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
  {
    FormSourceTypeEnum localFormSourceTypeEnum = paramBizEgg.getSourceTypeEnum();
    return getEncodeString(localFormSourceTypeEnum, paramLong, localFormSourceTypeEnum.getCustomerName(paramBizEgg));
  }
  
  public static String getEncodeString(FormSourceTypeEnum paramFormSourceTypeEnum, Long paramLong, String paramString)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
  {
    return getEncodeString(paramFormSourceTypeEnum.getKey() + "", paramLong.toString(), paramString);
  }
  
  public static String getEncodeString(String paramString1, String paramString2, String paramString3)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException
  {
    String str = paramString1 + "::" + paramString2 + "::" + paramString3;
    str = DesUtil.encode(str, "[oy8;h;flegku$324@jlfj2o93893/fdfrh024ufoklsdro");
    return str;
  }
  
  public static boolean isSameCustomName(String paramString1, Long paramLong, String paramString2)
  {
    if ((!_$5(paramString2)) || (_$4(paramString2))) {
      return false;
    }
    String[] arrayOfString;
    try
    {
      arrayOfString = _$1(paramString1);
    }
    catch (Exception localException)
    {
      System.out.println(paramLong + "解密失败！");
      return false;
    }
    return (_$1(arrayOfString, paramLong)) && (paramString2.equals(arrayOfString[2]));
  }
  
  public static String updateSourceInfo4CustomName(String paramString1, String paramString2)
    throws Exception
  {
    String[] arrayOfString;
    try
    {
      arrayOfString = _$1(paramString1);
    }
    catch (Exception localException)
    {
      System.out.println(paramString1 + "解密失败！");
      return paramString1;
    }
    return getEncodeString(arrayOfString[0], arrayOfString[1], paramString2);
  }
  
  private static String[] _$1(String paramString)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    if ((paramString == null) || ("".equals(paramString.trim()))) {
      return null;
    }
    String str = DesUtil.decode(paramString, "[oy8;h;flegku$324@jlfj2o93893/fdfrh024ufoklsdro");
    String[] arrayOfString = null;
    if ((str != null) && (str.contains("::"))) {
      arrayOfString = str.split("::");
    }
    return arrayOfString;
  }
  
  public static int getUsedNum()
  {
    return _$3;
  }
  
  public static int getTotalNum()
  {
    return _$4 + _$5;
  }
  
  public static int getAllowNum()
  {
    return getTotalNum() - getUsedNum();
  }
  
  public static boolean isAllowAdd()
  {
    return isAllowAdd(1);
  }
  
  public static boolean isAllowAdd(int paramInt)
  {
    boolean bool = false;
    int i = getAllowNum();
    if (paramInt <= i) {
      bool = true;
    }
    return bool;
  }
  
  public static synchronized void modifyUsedCount(BizOperationEnum paramBizOperationEnum)
  {
    _$3 += paramBizOperationEnum.getAddCount();
    _$5 += paramBizOperationEnum.getAddUpgradeCount();
    if (_$3 < 0) {
      _$3 = 0;
    }
  }
  
  public static FormSourceTypeEnum getCreateSourceType(BizOperationEnum paramBizOperationEnum)
  {
    boolean bool = ((Boolean)ReflectUtil.invokeMethod("com.seeyon.ctp.common.SystemEnvironment", "isDevOrTG", null, null, null)).booleanValue();
    FormSourceTypeEnum localFormSourceTypeEnum;
    if (bool) {
      localFormSourceTypeEnum = paramBizOperationEnum.getType4TG();
    } else {
      localFormSourceTypeEnum = paramBizOperationEnum.getType4Normal();
    }
    return localFormSourceTypeEnum;
  }
  
  public static boolean isEffectNum(String paramString, Long paramLong)
    throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException
  {
    return _$2(paramString, paramLong);
  }
}
