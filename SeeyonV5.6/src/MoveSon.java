import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.xml.resolver.apps.resolver;

import com.fr.third.org.apache.poi.hssf.record.formula.functions.Result;
import com.seeyon.ctp.util.UUIDLong;

public class MoveSon {

	public static boolean isstop = false;
	// static String connect1 = "jdbc:sqlserver://192.168.1.175:1433;
	// DatabaseName=v5x0617";
	static String connect1 = "jdbc:sqlserver://192.168.1.161:1433; DatabaseName=v5x0617";
	static String username = "sa2";
	static String password = "deheng@1993";
	static String frommainname = "formmain_3534";
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

	public static void main(String[] args) throws SQLException {
		conn = DriverManager.getConnection(connect1, username, password);
		MoveSon mcca = new MoveSon();
		ResultSet rsmain = mcca.doSelect("select a.id,b.id as 'sid' from formmain_0420 a "+
"left join  formmain_3534 b on a.field0002=b.field0002");
		try {
			while (rsmain.next()) {
				Long mainid = rsmain.getLong("id");
				Long subid = rsmain.getLong("sid");
				if (subid>0) {
					mcca.doUpdate("update dbo.formson_3535 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3536 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3537 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3538 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3539 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3540 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3541 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3542 set formmain_id="+subid+" where formmain_id="+mainid);
					mcca.doUpdate("update dbo.formson_3543 set formmain_id="+subid+" where formmain_id="+mainid);
					System.out.println(subid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean doUpdate(String sql) {
		boolean isok = false;
		try {
			
			ps = conn.prepareStatement(sql);
			isok = ps.execute();
			ps.close();
			//conn.close();

		} catch (SQLException e) {
			System.out.println("sql 执行错误");
			e.printStackTrace();
		}
		return isok;
	}

	private static ResultSet doSelect(String sql) {
		try {
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
