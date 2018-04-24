package www.seeyon.com.mocnoyees;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import www.seeyon.com.utils.Base64Util;
import www.seeyon.com.utils.FileUtil;
import www.seeyon.com.utils.StringUtil;

public class VERTMocnoyees extends Properties {
	private static final long serialVersionUID = 3422780413887065275L;
	public static final byte[] m = { 77, 84, 103, 49, 77, 84, 69, 48, 79, 68, 99, 51, 77, 122, 65, 48, 78, 68, 85, 120,
			79, 68, 73, 53, 78, 84, 77, 48, 79, 68, 99, 48, 78, 68, 85, 52, 77, 106, 85, 120, 79, 68, 89, 49, 79, 68,
			89, 50, 79, 84, 107, 50, 78, 122, 103, 121, 78, 68, 69, 50, 78, 122, 65, 50, 77, 106, 103, 121, 77, 106, 89,
			50, 77, 68, 99, 51, 77, 68, 99, 122, 77, 68, 69, 50, 78, 84, 65, 119, 78, 106, 103, 52, 78, 84, 65, 53, 77,
			122, 73, 53, 78, 106, 107, 53, 78, 84, 81, 50, 78, 106, 81, 120, 79, 68, 89, 120, 77, 122, 107, 119, 77, 68,
			107, 119, 79, 84, 89, 48, 78, 106, 81, 121, 79, 68, 81, 119, 77, 122, 85, 53, 78, 84, 107, 120, 78, 84, 77,
			53, 79, 84, 107, 120, 78, 84, 65, 52, 78, 84, 99, 120, 79, 68, 81, 51, 77, 84, 69, 122, 78, 68, 69, 53, 79,
			84, 81, 50, 77, 122, 107, 50, 77, 84, 99, 120, 78, 122, 85, 51, 77, 106, 89, 51, 77, 106, 77, 53, 79, 68,
			73, 120, 77, 122, 107, 53, 78, 68, 69, 49, 77, 122, 85, 48, 77, 122, 99, 119, 77, 68, 99, 53, 77, 106, 107,
			53, 78, 68, 73, 119, 79, 84, 69, 52, 77, 68, 99, 52, 78, 122, 65, 52, 78, 122, 81, 122, 77, 84, 99, 53, 78,
			84, 89, 120, 78, 122, 99, 50, 78, 68, 73, 53, 78, 68, 77, 120, 78, 68, 107, 122, 77, 106, 69, 50, 78, 84,
			77, 122, 77, 106, 81, 53, 78, 84, 103, 50, 79, 84, 77, 121, 77, 106, 103, 50, 79, 84, 65, 53, 79, 68, 73,
			52, 78, 122, 85, 49, 79, 84, 77, 48, 77, 122, 69, 120, 77, 122, 89, 120, 78, 84, 69, 51, 77, 84, 81, 48, 78,
			84, 99, 119, 78, 84, 69, 121, 78, 84, 89, 50, 77, 122, 77, 49, 79, 68, 77, 53, 78, 68, 99, 122, 78, 84, 107,
			52, 79, 68, 99, 120, 78, 106, 77, 49, 77, 106, 107, 120, 77, 122, 99, 53, 78, 84, 65, 120, 78, 106, 103, 50,
			77, 122, 77, 53, 77, 68, 107, 50, 78, 84, 69, 119, 77, 68, 85, 122, 79, 84, 69, 50, 77, 68, 85, 122, 77, 84,
			65, 48, 78, 106, 85, 53, 78, 84, 69, 120, 77, 106, 107, 49, 77, 122, 69, 52, 77, 68, 65, 53, 79, 68, 73, 48,
			79, 84, 103, 122, 78, 68, 65, 52, 79, 68, 99, 49, 78, 68, 73, 48, 78, 122, 99, 48, 77, 84, 73, 121, 78, 68,
			99, 51, 79, 84, 103, 48, 77, 68, 107, 122, 77, 84, 77, 48, 78, 106, 85, 120, 77, 122, 65, 122, 77, 84, 107,
			50, 78, 106, 89, 50, 78, 122, 103, 120, 78, 68, 103, 121, 78, 84, 65, 49, 78, 68, 107, 52, 79, 84, 69, 119,
			77, 122, 85, 121, 77, 84, 107, 52, 77, 84, 69, 49, 78, 84, 99, 122, 78, 68, 103, 122, 77, 84, 99, 49, 79,
			68, 81, 52, 78, 84, 73, 120, 78, 84, 69, 52, 78, 122, 99, 50, 78, 106, 89, 52, 78, 122, 103, 49, 79, 84, 99,
			53, 78, 68, 77, 52, 77, 122, 85, 120, 78, 122, 103, 52, 78, 68, 107, 52, 77, 68, 103, 121, 79, 68, 85, 48,
			79, 68, 99, 119, 77, 84, 81, 122, 78, 122, 85, 122, 79, 68, 77, 51, 77, 68, 65, 121, 78, 68, 99, 120, 78,
			106, 99, 52, 77, 122, 103, 119, 77, 106, 77, 51, 77, 106, 65, 120, 77, 68, 85, 50, 79, 84, 99, 49, 78, 106,
			85, 122, 78, 106, 99, 120, 78, 122, 89, 120, 78, 84, 99, 50, 78, 122, 89, 51, 78, 84, 103, 48, 79, 84, 77,
			48, 78, 84, 99, 120, 78, 106, 89, 53, 78, 68, 103, 120, 77, 68, 65, 48, 78, 122, 69, 48, 77, 122, 69, 50,
			79, 68, 77, 50, 77, 122, 89, 48, 78, 68, 65, 121, 77, 84, 89, 119, 77, 68, 107, 122, 78, 68, 89, 120, 79,
			68, 107, 52, 78, 106, 89, 122, 77, 68, 99, 121, 79, 68, 77, 52, 78, 122, 73, 48, 78, 68, 69, 50, 78, 122,
			103, 119, 79, 84, 107, 121, 77, 106, 85, 120, 77, 122, 69, 51, 79, 68, 65, 122, 77, 84, 85, 48, 78, 84, 73,
			51, 78, 84, 73, 119, 79, 84, 99, 121, 78, 106, 77, 50, 78, 84, 73, 120, 78, 84, 69, 49, 77, 68, 81, 122, 78,
			84, 103, 50, 78, 84, 85, 120, 77, 84, 69, 50, 77, 84, 89, 50, 78, 122, 69, 50, 78, 122, 103, 48, 78, 84, 77,
			51, 78, 84, 107, 51, 77, 84, 69, 53, 78, 68, 73, 52, 77, 84, 77, 61 };

	public VERTMocnoyees(String paramString) throws Exception {
		String str = null;
		try {
			str = FileUtil.readTextFile(paramString);
		} catch (Exception ex) {
			str="BRD9aBup6WlMdOi6Nec/idNlgBL8Uvxpik8szUlsWiNQwmyL8MTx3MfgLUh1muMHpNEoC1bPDAjE5MC1GroZDLYgr4oZ6hBNjUw/NtDIf46KWe64aS6sRWNTZpXFtEeDDaSyV2tfF/t7mjA/R6FPx4qgizpzFmney1b/qMew1l64a8T64O0hUrKSptNYWowBwWJ6c+AXq4F7mnMC6rpvxIX2/stINkx6/hiprZSwvWQ4gbKm1Cx74Eim2jpw1t4OBoJD9sLpzNZ6XPsfR7T0tXAYbdlRTTpo45In2Xc0PS1A4SkLfDRoWfvNvz9rh5gEje++SWIHuP9CQeTDp1m58A==";
		}
		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println(str);
		str = RSMocnoyees._$1(str, getString(m));
		System.out.println("-----------------------------------------------------------------------------------------");
		System.out.println(str);
		Properties localProperties = StringUtil.getProperties(str);
		Set localSet = localProperties.keySet();
		Iterator localIterator = localSet.iterator();
		while (localIterator.hasNext()) {
			Object localObject = localIterator.next();
			put(localObject, localProperties.get(localObject));
		}
	}

	public static boolean isDev(String paramString) {
		if (!new File(paramString).exists()) {
			return false;
		}
		String str = FileUtil.readTextFile(paramString);
		if ((str == null) || (str.equals("")) || (str.length() == 0)) {
			return false;
		}
		try {
			str = RSMocnoyees._$1(str, getString(m));
		} catch (Exception localException) {
			return false;
		}
		if ((str == null) || (str.equals("")) || (str.length() == 0)) {
			return false;
		}
		return str.equals("development");
	}

	public static String getString(byte[] paramArrayOfByte) {
		String str = new String(paramArrayOfByte);
		str = Base64Util.decode(str);
		return str;
	}
}
