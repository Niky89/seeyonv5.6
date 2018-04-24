package www.seeyon.com.mocnoyees;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import www.seeyon.com.utils.Base64Util;
import www.seeyon.com.utils.FileUtil;
import www.seeyon.com.utils.LoggerUtil;
import www.seeyon.com.utils.StringUtil;

public class CHKUMocnoyees
{
  public static void createListToFile(String[] paramArrayOfString, String paramString1, String paramString2)
  {
    File[] arrayOfFile = new File[paramArrayOfString.length];
    for (int i = 0; i < arrayOfFile.length; i++) {
      arrayOfFile[i] = new File(paramArrayOfString[i]);
    }
    createListToFile(arrayOfFile, paramString1, paramString2);
  }
  
  public static void createListToFile(File[] paramArrayOfFile, String paramString1, String paramString2)
  {
    if (!new File(paramString2).exists()) {
      LoggerUtil.stop("未找到加密文件:" + paramString2);
    }
    StringBuffer localStringBuffer = new StringBuffer("");
    for (File localFile : paramArrayOfFile) {
      localStringBuffer.append(localFile.getName() + "=" + FileUtil.getHash(localFile) + "\n");
    }
    String ss = localStringBuffer.toString();
    try
    {
      RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)RSMocnoyees.loadKeyByParam("privateKey", paramString2);
      byte[] arrayOfByte = RSMocnoyees.encode(localRSAPrivateKey, ((String)ss).getBytes());
     ss = Base64Util.encode(arrayOfByte);
    }
    catch (Exception localException)
    {
      LoggerUtil.stop("加密认证文件错误!", localException);
    }
    FileUtil.writeFile(paramString1, (String)ss);
  }
  
  public static boolean checkFile(File[] paramArrayOfFile, String paramString1, String paramString2)
  {
    if (!new File(paramString1).exists()) {
      return false;
    }
    HashMap localHashMap = new HashMap();
    //Object localObject2;
    for (Object localObject2 : paramArrayOfFile) {
      localHashMap.put(((File)localObject2).getName(), FileUtil.getHash((File)localObject2));
    }
    try
    {
      Object xx = (RSAPublicKey)RSMocnoyees.loadKeyByParam("publicKey", paramString2);
      String str1 = FileUtil.readTextFile(paramString1);
      byte[] arrayOfByte = Base64Util.base64Decode(str1);
      arrayOfByte = RSMocnoyees.decode((Key)xx, arrayOfByte);
    Object  localObject2 = new ByteArrayInputStream(arrayOfByte);
      Properties localProperties = new Properties();
      localProperties.load((InputStream)localObject2);
      Set localSet = localProperties.entrySet();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str2 = (String)localEntry.getKey();
        String str3 = (String)localEntry.getValue();
        if (!str3.equals(localHashMap.get(str2))) {
          LoggerUtil.stop(Enums.ErrorCode.error_1002.getError() + ":" + str2);
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      LoggerUtil.stop(Enums.ErrorCode.error_1002.getError() + ":", localException);
    }
    return true;
  }
  
  public static boolean checkFile(String paramString1, String paramString2)
    throws DogException
  {
    if (!new File(paramString1).exists()) {
      throw new DogException(Enums.ErrorCode.error_3002.getError(), Enums.ErrorCode.error_3002.getError() + ":" + paramString1 + " not exists");
    }
    if ((paramString2 == null) || (paramString2.length() == 0) || (!new File(paramString2).exists())) {
      throw new DogException(Enums.ErrorCode.error_3002.getError(), Enums.ErrorCode.error_3002.getError() + ":" + paramString2 + " not exists");
    }
    if ((!paramString2.endsWith("/")) || (!paramString2.endsWith("\\"))) {
      paramString2 = paramString2 + File.separator;
    }
    try
    {
      String str4 = FileUtil.readTextFile(paramString1);
      str4 = MSGMocnoyees.msgmocnoyeesdl(str4);
      Properties localProperties = StringUtil.getProperties(str4);
      Set localSet = localProperties.entrySet();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str1 = (String)localEntry.getKey();
        if (!new File(paramString2 + str1).exists()) {
         // throw new DogException(Enums.ErrorCode.error_3002.getError(), Enums.ErrorCode.error_3002.getError() + ":" + str1 + " not exists");
        }
        String str2 = (String)localEntry.getValue();
        String str3 = FileUtil.getHash(paramString2 + str1);
        if (!str2.equals(str3)) {
         // throw new DogException(Enums.ErrorCode.error_3002.getError(), Enums.ErrorCode.error_3002.getError() + "签名文件不匹配");
        }
      }
    }
    catch (Exception localException)
    {
     // throw new DogException(Enums.ErrorCode.error_3002.getError(), Enums.ErrorCode.error_3002.getError() + ":" + localException.getMessage());
    }
    return true;
  }
}
