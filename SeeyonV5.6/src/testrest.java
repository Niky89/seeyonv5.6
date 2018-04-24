import com.seeyon.client.CTPRestClient;
import com.seeyon.client.CTPServiceClientManager;

public class testrest {

	// REST用户登录名
	private String userName = "dhrest";
	// REST用户密码
	private String password = "123456";
	// 定义REST动态客户机
	private CTPRestClient client = null;

	public testrest() {
		// 取得指定服务主机的客户端管理器。
		// 参数为服务主机地址，包含{协议}{Ip}:{端口}，如http://127.0.0.1:8088
		CTPServiceClientManager clientManager = CTPServiceClientManager.getInstance("http://221.0.200.194:6602");
		// 取得REST动态客户机。
		client = clientManager.getRestClient();
		// 登录校验,成功返回true,失败返回false,此过程并会把验证通过获取的token保存在缓存中
		// 再请求访问其他资源时会自动把token放入请求header中。
		client.authenticate(userName, password);
		System.out.println(client.get("orgDepartments/8664586609541663905", String.class));
	}

	public static void main(String[] args) {
		new testrest();
	}
}
