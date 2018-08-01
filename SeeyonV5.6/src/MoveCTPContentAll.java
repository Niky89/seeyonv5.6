import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seeyon.ctp.util.UUIDLong;

public class MoveCTPContentAll {

	public static boolean isstop = false;
	//static String connect1 = "jdbc:sqlserver://192.168.1.175:1433; DatabaseName=v5x0617";
	static String connect1 = "jdbc:sqlserver://192.168.1.161:1433; DatabaseName=v5x0617";
	static String username="sa2";
	static String password="deheng@1993";
	static String frommainname ="formmain_3534";
	static String nl = "\n";
	static {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到jar包");
			e.printStackTrace();
		}
	}

	private static Connection conn = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;

	public static void main(String[] args) {
		MoveCTPContentAll mcca = new MoveCTPContentAll();
		ResultSet rs = mcca.doSelect("SELECT * FROM "+frommainname);
		try {
			while (rs.next()) {
				Long moduleId = rs.getLong("id");
				Long tempid = 0L;
				boolean ishas = mcca
						.isHas("SELECT * FROM dbo.CTP_CONTENT_ALL where module_id=" + String.valueOf(moduleId));
				String msg = "";
				boolean updateid = false;
				if (ishas) {// 查不到 这个地方有问题id在原表处已存在.需要更新一个新的id
					tempid = Long.valueOf(UUIDLong.longUUID());
					// 更新原来数据的id;
					updateid = mcca.doUpdate("update "+frommainname+" set id=" + String.valueOf(tempid)
							+ " where id=" + String.valueOf(moduleId));
					moduleId = tempid;
					msg = "更新id";
				}
				// 新增
				boolean tb = mcca.doUpdate("INSERT INTO dbo.CTP_CONTENT_ALL "
						+ " (id, body_type, content, create_date, MODIFY_DATE, MODULE_ID, content_name, MODULE_TEMPLATE_ID, MODIFY_ID, CONTENT_DATA_ID, CONTENT_TEMPLATE_ID, TITLE, SORT, MODULE_TYPE, CREATE_ID, CONTENT_TYPE) "
						+ " VALUES(" + Long.valueOf(UUIDLong.longUUID()) + ", '1100229988775566', '', getdate(), getdate(), " + moduleId
						+ ", '', -1289483259899524121, -3633308071696121177," + moduleId
						+ " , 7039606781818259373, '名片增改1', 0,37, -3633308071696121177, 20)");

				System.out.println(moduleId + "----" + msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean doUpdate(String sql) {
		boolean isok = false;
		try {
			conn = DriverManager.getConnection(connect1, username, password);
			ps = conn.prepareStatement(sql);
			isok = ps.execute();
			ps.close();
			conn.close();

		} catch (SQLException e) {
			System.out.println("sql 执行错误");
			e.printStackTrace();
		}
		return isok;
	}

	private static ResultSet doSelect(String sql) {
		try {
			conn = DriverManager.getConnection(connect1, username, password);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			return rs;
		} catch (SQLException e) {
			System.out.println("sql 执行错误");
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isHas(String sql) {
		ResultSet rs = doSelect(sql);
		try {
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}

	private static void close() {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
