import java.util.Calendar;
import java.util.Date;

import com.seeyon.ctp.common.AppContext;

public class test {
	public static void main(String[] args) {
		String fullPath = "集团文档\\律师业务\\办案指引\\办案参考";
		String[] stet = fullPath.split("\\\\");
		for (String s : stet) {
			System.out.println(s);
		}

		System.out.println("[DHqdb2015805180]郭凤刚、张喜芳-诉讼仲裁".equals("[DHqdb2015805180]郭风刚、张喜芳-诉讼仲裁"));

		System.out.println("浦东新区司法局刘龙宝副局长、魏孟宏处长等领导一行莅临德和衡上海律师所指导调研律".length());

		byte[] b = { 119, 119, 119, 46, 115, 101, 101, 121, 111, 110, 46, 99, 111, 109, 46, 109, 111, 99, 110, 111, 121,
				101, 101, 115, 46, 70, 68, 77, 77, 111, 99, 110, 111, 121, 101, 101, 115 };
		System.out.println(new String(b));

		System.out.println("00000002000100010080".substring(0, 12));

		System.out.println("7826652803590538525".equals("7826652803590538525"));

		com.seeyon.ctp.common.authenticate.domain.User u = AppContext.getCurrentUser();
		com.seeyon.cmp.controller.SeeyonCMPController a = new com.seeyon.cmp.controller.SeeyonCMPController();
		System.out.println((int) '聙');

		long systime = Calendar.getInstance().getTimeInMillis();
		long dead =9999999999999l + systime;
		Date d=new Date(dead);
		System.out.println(d);
		System.out.println(dead);
		
		
	}
}
