package www.seeyon.com.mocnoyees;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

import www.seeyon.com.utils.Base64Util;
import www.seeyon.com.utils.FileUtil;
import www.seeyon.com.utils.ReflectUtil;

public class RSMocnoyees
{
  static final String _$11 = Charset.defaultCharset().displayName();
  static final String _$10 = "UTF-8";
  static final String _$9 = "ISO-8859-1";
  private static final String _$8 = "RSA";
  private static final int _$7 = 2048;
  private static final int _$6 = 245;
  private static final int _$5 = 256;
  private static final int _$4 = 100;
  private static final boolean _$3 = System.getProperty("java.vendor").contains("IBM");
  private static final String _$2 = "SeeyonRsaSign";
  private static final String _$1 = "SeeyonJCE";
  
  public static void createRSAKeyPairs(String paramString)
  {
    KeyPair localKeyPair = generateKeyPair();
    saveKeysByParam(localKeyPair, paramString);
  }
  
  public static KeyPair generateKeyPair()
  {
    return generateKeyPair(2048);
  }
  
  public static KeyPair generateKeyPair(int paramInt)
  {
    KeyPair localKeyPair = null;
    try
    {
      KeyPairGenerator localKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
      localKeyPairGenerator.initialize(paramInt, new SecureRandom());
      localKeyPair = localKeyPairGenerator.genKeyPair();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      System.out.println("没有找到加密算法类型!");
      localNoSuchAlgorithmException.printStackTrace();
    }
    return localKeyPair;
  }
  
  public static HashMap<String, String> getRSAParam()
  {
    return getRSAParam(generateKeyPair());
  }
  
  public static HashMap<String, String> getRSAParam(KeyPair paramKeyPair)
  {
    HashMap localHashMap = new HashMap();
    RSAPublicKey localRSAPublicKey = (RSAPublicKey)paramKeyPair.getPublic();
    RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)paramKeyPair.getPrivate();
    BigInteger localBigInteger1 = localRSAPublicKey.getModulus();
    BigInteger localBigInteger2 = localRSAPublicKey.getPublicExponent();
    BigInteger localBigInteger3 = localRSAPrivateKey.getModulus();
    BigInteger localBigInteger4 = localRSAPrivateKey.getPrivateExponent();
    localHashMap.put("publicExponent", localBigInteger2.toString());
    localHashMap.put("privateExponent", localBigInteger4.toString());
    localHashMap.put("modules", localBigInteger1.toString());
    return localHashMap;
  }
  
  public static void saveKeysByParam(KeyPair paramKeyPair, String paramString)
  {
    RSAPublicKey localRSAPublicKey = (RSAPublicKey)paramKeyPair.getPublic();
    RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)paramKeyPair.getPrivate();
    BigInteger localBigInteger1 = localRSAPublicKey.getModulus();
    BigInteger localBigInteger2 = localRSAPublicKey.getPublicExponent();
    BigInteger localBigInteger3 = localRSAPrivateKey.getModulus();
    BigInteger localBigInteger4 = localRSAPrivateKey.getPrivateExponent();
    byte[] arrayOfByte1 = ("Modulus=" + localBigInteger1 + "\n" + "publicKey=" + localBigInteger2).getBytes();
    byte[] arrayOfByte2 = ("Modulus=" + localBigInteger3 + "\n" + "privateKey=" + localBigInteger4).getBytes();
    FileUtil.writeFile(arrayOfByte1, paramString + "RSAPublic.key");
    FileUtil.writeFile(arrayOfByte2, paramString + "RSAPrivate.key");
  }
  
  public static void saveKeysByBytes(KeyPair paramKeyPair, String paramString)
  {
    RSAPublicKey localRSAPublicKey = (RSAPublicKey)paramKeyPair.getPublic();
    RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)paramKeyPair.getPrivate();
    byte[] arrayOfByte1 = localRSAPublicKey.getEncoded();
    byte[] arrayOfByte2 = localRSAPrivateKey.getEncoded();
    FileUtil.writeFile(arrayOfByte1, paramString + "RSAPublic.key");
    FileUtil.writeFile(arrayOfByte2, paramString + "RSAPrivate.key");
  }
  
  public static Key loadKeyByParam(String paramString1, String paramString2)
    throws IOException
  {
    if (!new File(paramString2).exists()) {
      return null;
    }
    Key localKey = null;
    FileInputStream localFileInputStream = new FileInputStream(paramString2);
    Properties localProperties = new Properties();
    localProperties.load(localFileInputStream);
    String str1;
    String str2;
    if (paramString1.equals("publicKey"))
    {
      str1 = localProperties.get("Modulus").toString();
      str2 = localProperties.get("publicKey").toString();
      localKey = getPublicKey(str2, str1);
    }
    else if (paramString1.equals("privateKey"))
    {
      str1 = localProperties.get("Modulus").toString();
      str2 = localProperties.get("privateKey").toString();
      localKey = getPrivateKey(str2, str1);
    }
    return localKey;
  }
  
  public static Key loadKeyByBytes(String paramString1, String paramString2)
  {
    if (!new File(paramString2).exists()) {
      return null;
    }
    Object localObject1 = null;
    try
    {
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      byte[] arrayOfByte = null;
      Object localObject2;
      Object localObject3;
      if (paramString1.equals("publicKey"))
      {
        arrayOfByte = FileUtil.loadFile(paramString2);
        localObject2 = new X509EncodedKeySpec(arrayOfByte);
        localObject3 = (RSAPublicKey)localKeyFactory.generatePublic((KeySpec)localObject2);
        localObject1 = localObject3;
      }
      else if (paramString1.equals("privateKey"))
      {
        arrayOfByte = FileUtil.loadFile(paramString2);
        System.out.println(new String(arrayOfByte));
        localObject2 = new PKCS8EncodedKeySpec(arrayOfByte);
        localObject3 = (RSAPrivateKey)localKeyFactory.generatePrivate((KeySpec)localObject2);
        localObject1 = localObject3;
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      System.out.println("没有找到加密算法类型!");
      localNoSuchAlgorithmException.printStackTrace();
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      System.out.println("创建KEY失败！");
      localInvalidKeySpecException.printStackTrace();
    }
    return (Key)localObject1;
  }
  
  public static Key getPublicKey(String paramString1, String paramString2)
  {
    RSAPublicKey localRSAPublicKey = null;
    KeyFactory localKeyFactory = null;
    try
    {
      BigInteger localBigInteger1 = new BigInteger(paramString2);
      BigInteger localBigInteger2 = new BigInteger(paramString1);
      if (_$3)
      {
        Class.forName("seeyon.security.rsa.SeeyonRsaSign");
        localKeyFactory = KeyFactory.getInstance("RSA", "SeeyonRsaSign");
      }
      else
      {
        localKeyFactory = KeyFactory.getInstance("RSA");
      }
      localRSAPublicKey = (RSAPublicKey)localKeyFactory.generatePublic(new RSAPublicKeySpec(localBigInteger1, localBigInteger2));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localRSAPublicKey;
  }
  
  public static Key getPrivateKey(String paramString1, String paramString2)
  {
    RSAPrivateKey localRSAPrivateKey = null;
    try
    {
      BigInteger localBigInteger1 = new BigInteger(paramString2);
      BigInteger localBigInteger2 = new BigInteger(paramString1);
      KeyFactory localKeyFactory = KeyFactory.getInstance("RSA");
      localRSAPrivateKey = (RSAPrivateKey)localKeyFactory.generatePrivate(new RSAPrivateKeySpec(localBigInteger1, localBigInteger2));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localRSAPrivateKey;
  }
  
  public static byte[] encode(Key paramKey, byte[] paramArrayOfByte)
    throws DogException
  {
    return encode(paramKey, paramArrayOfByte, 245);
  }
  
  public static byte[] encode(Key paramKey, byte[] paramArrayOfByte, int paramInt)
    throws DogException
  {
    byte[] arrayOfByte = null;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramArrayOfByte.length);
    try
    {
      javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("RSA");
      localCipher.init(1, paramKey);
      int i = 0;
      int j = paramArrayOfByte.length;
      int k = 1;
      while (k != 0) {
        if (i + paramInt <= j)
        {
          localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, paramInt));
          i += paramInt;
        }
        else
        {
          k = 0;
        }
      }
      if (i < j) {
        localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, j - i));
      }
      arrayOfByte = localByteArrayOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      throw new DogException(Enums.ErrorCode.error_1013.getError());
    }
    return arrayOfByte;
  }
  
  public static byte[] decode(Key paramKey, byte[] paramArrayOfByte)
    throws DogException
  {
    if (_$3) {
      return decode_ibm(paramKey, paramArrayOfByte);
    }
    return decode_sun(paramKey, paramArrayOfByte);
  }
  
  public static byte[] decode(Key paramKey, byte[] paramArrayOfByte, int paramInt)
    throws DogException
  {
    if (_$3) {
      return decode_ibm(paramKey, paramArrayOfByte, paramInt);
    }
    return decode_sun(paramKey, paramArrayOfByte, paramInt);
  }
  
  public static byte[] decode_sun(Key paramKey, byte[] paramArrayOfByte)
    throws DogException
  {
    return decode_sun(paramKey, paramArrayOfByte, 256);
  }
  
  public static byte[] decode_sun(Key paramKey, byte[] paramArrayOfByte, int paramInt)
    throws DogException
  {
    byte[] arrayOfByte = null;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramArrayOfByte.length);
    try
    {
      javax.crypto.Cipher localCipher = javax.crypto.Cipher.getInstance("RSA");
      localCipher.init(2, paramKey);
      int i = 0;
      int j = paramArrayOfByte.length;
      int k = 1;
      while (k != 0) {
        if (i + paramInt <= j)
        {
          localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, paramInt));
          i += paramInt;
        }
        else
        {
          k = 0;
        }
      }
      if (i < j) {
        localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, j - i));
      }
      arrayOfByte = localByteArrayOutputStream.toByteArray();
    }
    catch (Exception localException)
    {
      throw new DogException(Enums.ErrorCode.error_1013.getError());
    }
    return arrayOfByte;
  }
  
  public static byte[] decode_ibm(Key paramKey, byte[] paramArrayOfByte)
    throws DogException
  {
    return decode_ibm(paramKey, paramArrayOfByte, 256);
  }
  
  public static byte[] decode_ibm(Key paramKey, byte[] paramArrayOfByte, int paramInt)
    throws DogException
  {
//    byte[] arrayOfByte = null;
//    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(paramArrayOfByte.length);
//    try
//    {
//      Class.forName("com.seeyon.crypto.provider.SeeyonJCE");
//      seeyon.crypto.Cipher localCipher = seeyon.crypto.Cipher.getInstance("RSA", "SeeyonJCE");
//      localCipher.init(2, paramKey);
//      int i = 0;
//      int j = paramArrayOfByte.length;
//      int k = 1;
//      while (k != 0) {
//        if (i + paramInt <= j)
//        {
//          localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, paramInt));
//          i += paramInt;
//        }
//        else
//        {
//          k = 0;
//        }
//      }
//      if (i < j) {
//        localByteArrayOutputStream.write(localCipher.doFinal(paramArrayOfByte, i, j - i));
//      }
//      arrayOfByte = localByteArrayOutputStream.toByteArray();
//    }
//    catch (Exception localException)
//    {
//      throw new DogException(Enums.ErrorCode.error_1013.getError());
//    }
//    return arrayOfByte;
	  return null;
  }
  
  public static void printCharset(String paramString)
  {
    System.out.println(paramString.getBytes().toString());
    SortedMap localSortedMap = Charset.availableCharsets();
    Iterator localIterator = localSortedMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      try
      {
        String str = paramString.getBytes((String)localEntry.getKey()).toString();
        if (paramString.equals(str)) {
          System.out.println((String)localEntry.getKey() + ":" + localEntry.getValue());
        }
      }
      catch (Exception localException) {}
    }
  }
  
  static String _$1(String paramString1, String paramString2)
    throws DogException
  {
    RSAPublicKey localRSAPublicKey = (RSAPublicKey)getPublicKey("65537", paramString2);
    String str = null;
    byte[] arrayOfByte = Base64Util.base64Decode(paramString1);
    arrayOfByte = decode(localRSAPublicKey, arrayOfByte);
    str = new String(arrayOfByte);
    return str;
  }
  
  public static String[] mocnoyeesel(String paramString, boolean paramBoolean)
    throws IOException, DogException
  {
    return mocnoyeesel(paramString, paramBoolean, 1);
  }
  
  public static String[] mocnoyeesel(String paramString, boolean paramBoolean, int paramInt)
    throws IOException, DogException
  {
    RSAPrivateKey localRSAPrivateKey = null;
    String[] arrayOfString = new String[3];
    String str1 = "";
    if (paramBoolean)
    {
     Object localObject = getRSAParam();
      String str2 = (String)((HashMap)localObject).get("publicExponent");
      String str3 = (String)((HashMap)localObject).get("privateExponent");
      str1 = (String)((HashMap)localObject).get("modules");
      localRSAPrivateKey = (RSAPrivateKey)getPrivateKey(str3, str1);
      str1 = str1 + getRandomStr(100);
    }
    else
    {
    Object  []localObject = (String[])ReflectUtil.invokeStaticMethod(DMMocnoyees._$1(DMMocnoyees._$6), DMMocnoyees._$1(DMMocnoyees._$3) + paramInt);
      localRSAPrivateKey = (RSAPrivateKey)getPrivateKey(localObject[1].toString(), localObject[0].toString());
    }
    Object localObject = encode(localRSAPrivateKey, paramString.getBytes());
    paramString = Base64Util.encode((byte[])localObject);
    arrayOfString[0] = paramString;
    arrayOfString[1] = str1;
    return arrayOfString;
  }
  
  public static String getRandomStr(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    for (int i = 0; i < paramInt; i++)
    {
      double d = Math.random();
      String str2 = String.valueOf(d);
      str2 = str2.substring(2, 3);
      localStringBuffer.append(str2);
    }
    String str1 = localStringBuffer.toString();
    return str1;
  }
  
  public static String getModules(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 100)) {
      return null;
    }
    String str = paramString.substring(0, paramString.length() - 100);
    return str;
  }
}
