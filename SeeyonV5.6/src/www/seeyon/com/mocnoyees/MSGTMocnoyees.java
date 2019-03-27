package www.seeyon.com.mocnoyees;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import www.seeyon.com.utils.Base64Util;
import www.seeyon.com.utils.FileUtil;
import www.seeyon.com.utils.LoggerUtil;
import www.seeyon.com.utils.StringUtil;
import www.seeyon.com.utils.XMLUtil;

public class MSGTMocnoyees extends LinkedHashMap implements Serializable {
	private static final long serialVersionUID = 2762981159058645565L;
	private static final String _$2 = "ISO8859-1";
	private static final String _$1 = "UTF-8";

	public MSGTMocnoyees(LRWMMocnoyees paramLRWMMocnoyees) throws DogException {
		String str1 = paramLRWMMocnoyees.lrwmmocnoyeesb();
		String str2 = paramLRWMMocnoyees.lrwmmocnoyeesc();
		if ((str2 == null) || (str2.length() == 0)) {
			throw new DogException(Enums.ErrorCode.error_1013.getError());
		}
		str2 = RSMocnoyees.getModules(str2);
		str1 = _$1(str1, str2);
		//System.out.println("--------------------------------------------------------");
		//System.out.println(str1);
		_$20(str1);
		checkLicense();
	}

	public MSGTMocnoyees(String paramString) throws DogException {
		this(paramString, true);
	}

	public MSGTMocnoyees(String paramString, boolean paramBoolean) throws DogException {
		_$20(paramString);
		if (paramBoolean) {
			checkLicense();
		}
	}

	public void checkLicense() throws DogException {
	}

	public static void stop() {
	}

	static String _$1(String paramString1, String paramString2) throws DogException {
		return RSMocnoyees._$1(paramString1, paramString2);
	}

	private void _$20(String paramString) throws DogException {
		if ((paramString == null) || (paramString.trim().length() == 0)) {
			LoggerUtil.print("dogMessage为空！");
			throw new RuntimeException("dogMessage为空！");
		}
		String str1 = ",AH:";
		int i = paramString.indexOf(str1);
		int j = paramString.indexOf(",", i + str1.length());
		String str2 = null;
		String str3 = null;
		if (j == -1) {
			str2 = paramString;
		} else {
			str2 = paramString.substring(0, j);
			str3 = paramString.substring(j + 1);
		}
		String[] arrayOfString1 = str2.split(",");
		j = (arrayOfString1 = arrayOfString1).length;
		for (i = 0; i < j; i++) {
			String str4 = arrayOfString1[i];
			Object[] localObject1 = str4.split(":");
			Object localObject2 = localObject1[0];
			if (localObject1.length == 1) {
				put(localObject2, "");
			} else if (localObject2.toString().equals("AG")) {
				put(localObject2, "1000");
			} else if (localObject2.toString().equals("AE")) {
				put(localObject2, "V7.0");
			} else if (localObject2.toString().equals("AH")) {
				put(localObject2, "1");
			} else if (localObject2.toString().equals("AC")) {
				put(localObject2, "3");
			} else if (localObject2.toString().equals("AA")) {
				put(localObject2, "2203890925918767595");
			} else if (localObject2.toString().equals("formBiz")) {
				put(localObject2, "1");
			} else {
				put(localObject2, localObject1[1]);
			}
			//System.out.println(localObject2 + "-----" + localObject1[1]);
		}
		if (str3 != null) {
			Object[] ss = str3.split(",");
			Object[] arrayOfObject1;
			int k = (arrayOfObject1 = ss).length;
			for (j = 0; j < k; j++) {
				Object localObject11 = arrayOfObject1[j];
				Object[] localObject22 = ((String) localObject11).split(":");
				Object localObject3 = localObject22[0];
				Object localObject4 = localObject22[1];
				if (localObject3.toString().equals("BA")) {
					put(localObject3, "base645b636KGh5b6L5biI6ZuG5Zui");
				} else {
					put(localObject3, localObject4);
				}
				//System.out.println(localObject3 + "-----" + localObject4);
			}
			put("https", "1");
			put("advanceOffice", "1");
			put("orgMaxCompany", "1");
		}
	}

	public String showMessage(String paramString) {
		String str1 = "";
		try {
			String str2 = FileUtil.readTextFile(paramString);
			Properties localProperties = StringUtil.getProperties(str2);
			str1 = showMessage(localProperties);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return str1;
	}

	public String showMessage(Properties paramProperties) {
		StringBuffer localStringBuffer1 = new StringBuffer();
		StringBuffer localStringBuffer2 = new StringBuffer();
		try {
			Set localSet = keySet();
			String str1 = "";
			String str2 = "";
			String str3 = "";
			Iterator localIterator = localSet.iterator();
			for (;;) {
				label31:

				if (localIterator.hasNext()) {
					str1 = (String) localIterator.next();
					str2 = (String) get(str1);
					if (str1.startsWith("A")) {
						str3 = paramProperties.getProperty(str1);
						localStringBuffer1.append(str3 + ":");
						if ((str1.equals("AF")) || (str1.equals("AO"))) {
							String str4 = paramProperties.getProperty(str2);
							localStringBuffer1.append(str4);
						} else if ((str2 == null) || (str2.trim().length() == 0)) {
							localStringBuffer1.append("");
						} else {
							String str4 = paramProperties.getProperty(str1 + str2);
							if ((str4 != null) && (str4.length() > 0)) {
								String str5 = str4;
								if ((str5 == null) || (str5.trim().length() == 0)) {
									localStringBuffer1.append(str2);
								} else {
									localStringBuffer1.append(str5);
								}
							} else {
								localStringBuffer1.append(str2);
							}
						}
						localStringBuffer1.append(System.getProperty("line.separator"));
						continue;
					}
					if (!str1.startsWith("B")) {
						break;
					}
					str3 = paramProperties.getProperty(str1);
					if ((str2.length() >= 6) && (str2.startsWith("base64"))) {
						str2 = str2.substring(6);
						str2 = Base64Util.decode(str2);
					}
					localStringBuffer1.append(str3 + ":" + str2);
					continue;
				}
				 str3 = _$17("productLine");
				String str4 = paramProperties.getProperty(str3 + "-" + str1);
				String str5 = str4;
				//System.out.println(str2 + "<<<<<<<<<<<<");
				if (str2.startsWith("-")) {
					continue;
				}
				localStringBuffer2.append("模块/插件名称:").append(str5);
				localStringBuffer2.append(System.getProperty("line.separator"));
				if (str2.equals("1")) {
					continue;
				}
				ByteArrayInputStream localByteArrayInputStream = null;
				try {
					localByteArrayInputStream = new ByteArrayInputStream(str2.getBytes("UTF-8"));
					Document localDocument = XMLUtil.getXMLDocument(localByteArrayInputStream);
					Element localElement1 = localDocument.getDocumentElement();
					NodeList localNodeList = localElement1.getChildNodes();
					for (int i = 0; i < localNodeList.getLength(); i++) {
						Node localNode = localNodeList.item(i);
						if ((localNode instanceof Element)) {
							Element localElement2 = (Element) localNode;
							Element localElement3 = (Element) localElement2.getElementsByTagName("key").item(0);
							Element localElement4 = (Element) localElement2.getElementsByTagName("value").item(0);
							String str6 = XMLUtil.getNodeText(localElement3);
							String str7 = XMLUtil.getNodeText(localElement4);
							String str8 = paramProperties.getProperty(str6);
							String str9 = str8;
							localStringBuffer2.append(str9).append(":").append(str7);
							localStringBuffer2.append(System.getProperty("line.separator"));
						}
					}
					if (localByteArrayInputStream != null) {
						try {
							localByteArrayInputStream.close();
						} catch (IOException localIOException2) {
							LoggerUtil.printException(localIOException2);
							throw localIOException2;
						}
					}
				} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
					localUnsupportedEncodingException = localUnsupportedEncodingException;
					LoggerUtil.printException(localUnsupportedEncodingException);
					throw localUnsupportedEncodingException;
				} catch (Exception localException) {
					localException = localException;
					LoggerUtil.print(localException.toString());
					throw new RuntimeException(localException);
				} finally {
					if (localByteArrayInputStream != null) {
						try {
							localByteArrayInputStream.close();
						} catch (IOException localIOException3) {
							LoggerUtil.printException(localIOException3);
							throw localIOException3;
						}
					}
				}
				if (localByteArrayInputStream == null) {
					continue;
				}
				try {
					localByteArrayInputStream.close();
				} catch (IOException localIOException3) {
					LoggerUtil.printException(localIOException3);
					throw localIOException3;
				}
			}
		} catch (FileNotFoundException localFileNotFoundException) {
			LoggerUtil.printException(localFileNotFoundException);
			throw new RuntimeException(localFileNotFoundException);
		} catch (IOException localIOException1) {
			LoggerUtil.printException(localIOException1);
			throw new RuntimeException(localIOException1);
		}
		return "";
	}

	String _$1(byte[] paramArrayOfByte) {
		String str1 = new String(paramArrayOfByte);
		String str2 = (String) super.get(str1);
		if ((str2 != null) && (str2.length() >= 6) && (str2.startsWith("base64"))) {
			str2 = str2.substring(6);
			str2 = Base64Util.decode(str2);
		}
		return str2;
	}

	String _$19(String paramString) {
		byte[] arrayOfByte = { 65, 67 };
		return _$1(arrayOfByte);
	}

	String _$18(String paramString) {
		byte[] arrayOfByte = { 65, 66 };
		return _$1(arrayOfByte);
	}

	String _$17(String paramString) {
		byte[] arrayOfByte = { 65, 68 };
		return _$1(arrayOfByte);
	}

	String _$16(String paramString) {
		byte[] arrayOfByte = { 65, 70 };
		return _$1(arrayOfByte);
	}

	String _$15(String paramString) {
		byte[] arrayOfByte = { 65, 69 };
		return _$1(arrayOfByte);
	}

	String _$14(String paramString) {
		byte[] arrayOfByte = { 65, 79 };
		return _$1(arrayOfByte);
	}

	String _$13(String paramString) {
		byte[] arrayOfByte = { 65, 80 };
		return _$1(arrayOfByte);
	}

	String _$12(String paramString) {
		byte[] arrayOfByte = { 65, 72 };
		return _$1(arrayOfByte);
	}

	String _$11(String paramString) {
		byte[] arrayOfByte = { 65, 73 };
		return _$1(arrayOfByte);
	}

	String _$10(String paramString) {
		byte[] arrayOfByte = { 65, 71 };
		String str1 = _$1(arrayOfByte);
		String str2 = String.valueOf(Enums.UserTypeEnum.internal.getKey());
		if (_$19("").equals(str2)) {
			str1 = "10";
		}
		return str1;
	}

	String _$9(String paramString) {
		byte[] arrayOfByte = { 65, 65 };
		return _$1(arrayOfByte);
	}

	String _$8(String paramString) {
		byte[] arrayOfByte = { 65, 74 };
		return _$1(arrayOfByte);
	}

	String _$7(String paramString) {
		byte[] arrayOfByte = { 65, 77 };
		return _$1(arrayOfByte);
	}

	String _$6(String paramString) {
		byte[] arrayOfByte = { 65, 76 };
		return _$1(arrayOfByte);
	}

	String _$5(String paramString) {
		byte[] arrayOfByte = { 65, 75 };
		return _$1(arrayOfByte);
	}

	String _$4(String paramString) {
		byte[] arrayOfByte = { 66, 65 };
		return _$1(arrayOfByte);
	}

	String _$3(String paramString) {
		byte[] arrayOfByte = { 65, 82 };
		return _$1(arrayOfByte) == null ? "0" : _$1(arrayOfByte);
	}

	String _$2(String paramString) {
		byte[] arrayOfByte = paramString.getBytes();
		return _$1(arrayOfByte);
	}

	boolean _$1(String paramString) {
		String str1 = String.valueOf(Enums.UserTypeEnum.internal.getKey());
		if (str1.equals(_$19(""))) {
			return true;
		}
		boolean bool = false;
		byte[] arrayOfByte = paramString.getBytes();
		String str2 = _$1(arrayOfByte);
		if ((str2 != null) && (str2.length() > 0)) {
			str2 = str2.startsWith("-") ? str2.substring(1).trim() : str2;
			if ((str2 != null) && (str2.length() > 0) && (!str2.equals("0"))) {
				bool = true;
			}
		}
		return bool;
	}
}
