package com.deheng.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.alibaba.fastjson.JSONObject;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.authenticate.domain.User;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Util {

	// 获取汉语拼音 无声调
	public static String getPinyinWithMark2(String inputString) {
		// 汉语拼音格式输出类
		HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
		// 输出设置，大小写，音标方式等
		hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
		hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 有音调
		hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE); // '¨¹'
																				// is
																				// "u:"
		char[] input = inputString.trim().toCharArray();
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < input.length; i++) {
			// 是中文转换拼音(我的需求，是保留中文)
			if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) { // 中文字符
				try {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], hanYuPinOutputFormat);
					output.append(temp[0]);
					output.append(" ");
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else { // 其他字符
				output.append(Character.toString(input[i]));
			}
		}
		return output.toString().trim();
	}

	public String getProperty(String key) {
		String proString = this.getPorp().getProperty(key);
		if (proString == null) {
			proString = "";
		}
		return proString;
	}

	public Properties getPorp() {
		return getPorp("/deheng.properties");
	}

	public Properties getPorp(String fileName) {
		Properties p = new Properties();
		
		InputStream is = Util.class.getResourceAsStream(fileName);
		try {
			p.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	public Map<String, String> getAllPorperty() {
		Properties p = this.getPorp();
		Set<Object> ks = p.keySet();
		Map<String, String> map = new HashMap<String, String>();
		for (Object o : ks) {
			String str = (String) o;
			map.put(str, p.getProperty(str));
		}
		return map;
	}

	public static String checkSqlString(String str) {
		if (str == null) {
			return str;
		}
		str = str.replace("'", "''");
		str = str.replace("%", "\\%");
		str = str.replace("_", "\\_");
		return str;
	}

	public static double getDoubleValue(String paramString) {
		return getDoubleValue(paramString, -1.0D);
	}

	public static double getDoubleValue(String paramString, double paramDouble) {
		try {
			return Double.parseDouble(paramString);
		} catch (Exception localException) {
		}
		return paramDouble;
	}

	public static float getFloatValue(String paramString) {
		return getFloatValue(paramString, -1.0F);
	}

	public static float getFloatValue(String paramString, float paramFloat) {
		try {
			return Float.parseFloat(paramString);
		} catch (Exception localException) {
		}
		return paramFloat;
	}

	public static int getIntValue(String paramString) {
		return getIntValue(paramString, -1);
	}

	public static int getIntValue(String paramString, int paramInt) {
		try {
			return Integer.parseInt(paramString);
		} catch (Exception localException) {
		}
		return paramInt;
	}

	public static String getPath() {
		URL url = Util.class.getProtectionDomain().getCodeSource().getLocation();
		String path = url.toString();
		int index = path.indexOf("WEB-INF");

		if (index == -1) {
			index = path.indexOf("classes");
		}

		if (index == -1) {
			index = path.indexOf("bin");
		}

		path = path.substring(0, index);

		if (path.startsWith("zip")) {// 当class文件在war中时，此时返回zip:D:/...这样的路径
			path = path.substring(4);
		} else if (path.startsWith("file")) {// 当class文件在class文件中时，此时返回file:/D:/...这样的路径
			path = path.substring(6);
		} else if (path.startsWith("jar")) {// 当class文件在jar文件里面时，此时返回jar:file:/D:/...这样的路径
			path = path.substring(10);
		}
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return path;
	}

	/**
	 * 输入图片到
	 */
	public String writePng(byte[] img) {
		String filename = new Date().getTime() + ".png";
		String fd = Util.getPath() + "/icon/";
		String fn = fd + filename;
		File dff = new File(fd);
		if (!dff.exists()) {
			dff.mkdir();
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(fn);
			os.write(img);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}

	public String makeuidstring(int[] uids) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < uids.length; i++) {
			sb.append(uids[i]);
			if (i < uids.length - 1) {
				sb.append(",");
			}
		}
		if (sb.length() <= 0) {
			sb.append("-95595");
		}
		return sb.toString();
	}

	// 检测当前用户是否登录
	public static boolean isLogined() {
		User u = AppContext.getCurrentUser();
		if (u != null) {
			return true;
		} else {
			return false;
		}
	}

	// make page sql
	public static String makePageSql(int page, int pageSize, String tableName, String keyName, String wherestr) {
		int tempsize = pageSize * (page - 1);
		String sql = "SELECT TOP " + pageSize
				+ " * FROM ( SELECT ROW_NUMBER() OVER (ORDER BY id desc) AS RowNumber,* FROM " + tableName
				+ " ) as A WHERE RowNumber > " + tempsize;
		if (wherestr != null && !"".equals(wherestr)) {
			sql += "  and " + wherestr;
		}
		return sql;
	}

	// 格式化时间
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		return sdf.format(date);
	}

	public static int ckInt(Integer i) {
		return i == null ? 0 : i;
	}

	/**
	 * 获取企业微信access_token
	 * https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRECT
	 * 把企业微信token 放入系统缓存 qytoken 企业微信token缓存 qyouttime 超时时间
	 */
	public String getQyAccessToken() {
		String token = null;
		Object o=AppContext.getCache("qyouttime");//第一次取得此缓存为null
		Date date = null;
		Date now = new Date();
		if(o==null){
			date=now;
		}else{
			date= new Date((Long) o);
		}
		if (date.before(now) || date==now) {// 缓存时间在现在时间之前获取新的token
			
			HttpClient client = new HttpClient();

			client.getHostConfiguration().setHost("qyapi.weixin.qq.com", 443, "https");
			GetMethod get = new GetMethod(
					"/cgi-bin/gettoken?corpid="+this.getProperty("corpid")+"&corpsecret="+this.getProperty("corpsecret"));
			try {
				client.executeMethod(get);
				String response = new String(get.getResponseBody());
				JSONObject jo = JSONObject.parseObject(response);
				token = jo.getString("access_token");
				//将token 放入缓存并且更新时间
				AppContext.putCache("qytoken", token);
				AppContext.putCache("qyouttime", now.getTime()+(7000*1000));//7000秒*1000=毫秒
				get.releaseConnection();
			} catch (HttpException e) {
				e.printStackTrace();
				System.out.println("请求企业微信accesstoken 异常");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{//获取缓存token
			token=(String)AppContext.getCache("qytoken");
		}
		return token;
	}
	
	/**
	 * 不同的程序有不同的secret
	 * @param secret
	 * @return
	 */
	public String getQyAccessToken(String secret) {
		String token = null;
		Object o=AppContext.getCache("qyouttime"+secret);//第一次取得此缓存为null
		Date date = null;
		Date now = new Date();
		if(o==null){
			date=now;
		}else{
			date= new Date((Long) o);
		}
		if (date.before(now) || date==now) {// 缓存时间在现在时间之前获取新的token
			
			HttpClient client = new HttpClient();

			client.getHostConfiguration().setHost("qyapi.weixin.qq.com", 443, "https");
			GetMethod get = new GetMethod(
					"/cgi-bin/gettoken?corpid="+this.getProperty("corpid")+"&corpsecret="+secret);
			try {
				client.executeMethod(get);
				String response = new String(get.getResponseBody());
				JSONObject jo = JSONObject.parseObject(response);
				token = jo.getString("access_token");
				//将token 放入缓存并且更新时间
				AppContext.putCache("qytoken"+secret, token);
				AppContext.putCache("qyouttime"+secret, now.getTime()+(7000*1000));//7000秒*1000=毫秒
				get.releaseConnection();
			} catch (HttpException e) {
				e.printStackTrace();
				System.out.println("请求企业微信accesstoken 异常");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{//获取缓存token
			token=(String)AppContext.getCache("qytoken"+secret);
		}
		return token;
	}

	/**
	 * 检测企业微信扫码code
	 * 
	 * @param args
	 */
	public String checkQyCode(String code, String token) {
		HttpClient client = new HttpClient();
		String userid = null;
		client.getHostConfiguration().setHost("qyapi.weixin.qq.com", 443, "https");
		GetMethod get = new GetMethod("/cgi-bin/user/getuserinfo?access_token=" + token + "&code=" + code);
		try {
			client.executeMethod(get);
			String response = new String(get.getResponseBody());
			JSONObject jo = JSONObject.parseObject(response);
			int errcode = jo.getIntValue("errcode");
			userid = jo.getString("UserId");
			get.releaseConnection();
		} catch (HttpException e) {
			e.printStackTrace();
			System.out.println("请求企业微信验证code 异常");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userid;
	}

	public static void main(String[] args) {
		Util u = new Util();
		String token = u.getQyAccessToken("QDbQ7ImV5-BEEIsDQ4RBJJH9qHJktxjSzarPnpV75jU");
		System.out.println(token);
		System.out.println(u.checkQyCode("BUSm6QI-KCw4g4zttrU8yZe5hqmYmYXQjVit4ZhJJA4", token));

	}
}
