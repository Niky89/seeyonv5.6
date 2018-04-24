import java.security.NoSuchAlgorithmException;

import com.seeyon.client.CTPRestClient;
import com.seeyon.client.CTPServiceClientManager;

public class test2 {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		CTPServiceClientManager clientManager = CTPServiceClientManager.getInstance("http://192.168.1.170");

		// 取得REST动态客户机实例
		CTPRestClient client = clientManager.getRestClient();
		client.authenticate("test", "123456");
		String json00 = client.get("affairs/pending/code/7826652803590538525", String.class);
		System.out.println(json00+"<<<<<<<");
//		String json = client.get("token/wechat/123456", String.class);
//		JSONObject jo = JSONObject.parseObject(json);
//		System.out.println(jo.getString("id"));
//		System.out.println(client.post("porduct", "", String.class));
//		String json1 = client.get("porduct?token=" + jo.getString("id"), String.class);
//		System.out.println(json1);
		
	}
}
