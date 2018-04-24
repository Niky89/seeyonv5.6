package com.deheng.utils;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.sql.DataSource;

/**
 * 
 * @author sxl
 *
 */
public class DBUtil {
	private int curpos;
	private Vector<Object> array = new Vector<Object>();
	private Connection conn;
	private PreparedStatement ps;
	private CallableStatement cs;
	private ResultSet rs;
	private DBConnect dbconnect;
	private String[] columnName;
	private int[] columnType;

	
	
	public int getColumnCount(){
		if(columnName==null){
			return 0;
		}
		return columnName.length;
	}

	private void init() {
		this.curpos = -1;
		this.array.clear();
	}

	// 默认使用配置文件
	public DBUtil() {
		this.dbconnect = new DBConnectByProp();
	}

	public DBUtil(String type) {
		this.dbconnect = new DBConnectByProp();
	}

	public boolean getConnection(DataSource source) {
		try {
			conn = source.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null && !conn.isClosed()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String makeTransctSQL(String tname, List<Object> param, List<Object> output) {
		List<Object> temp = new ArrayList<Object>();
		if (param != null) {
			temp.addAll(param);
		}
		if (output != null) {
			temp.addAll(output);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{ call ");
		sb.append(tname);
		sb.append("( ");
		for (int i = 0; i < temp.size(); i++) {
			sb.append("?");
			if (i < temp.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(" )}");
		System.out.println(sb.toString());
		return sb.toString();
	}

	private CallableStatement makeTransactCallable(Connection conn, String tname, List<Object> param, List<Object> output) throws SQLException {
		String sql = this.makeTransctSQL(tname, param, output);
		CallableStatement cs = conn.prepareCall(sql);
		int count = param.size();
		int k = 1;
		for (int i = 0; i < count; i++) {
			if (param.get(i) instanceof BigDecimal) {
				cs.setBigDecimal(k, (BigDecimal) param.get(i));
			} else {
				cs.setObject(k, param.get(i));
			}
			k++;
		}
		if (output != null) {
			for (int i = 0; i < output.size(); i++) {
				if (output.get(i) instanceof Integer) {// 只判断String
														// ,和数字
					cs.registerOutParameter(k, Types.INTEGER);

				}
				if (output.get(i) instanceof String) {// 只判断String
														// ,和数字
					cs.registerOutParameter(k, Types.VARCHAR);

				}
				if (output.get(i) instanceof Double) {// 只判断String
														// ,和数字
					cs.registerOutParameter(k, Types.DOUBLE);

				}
				k++;
			}
		}
		return cs;
	}

	// 调用存储过程的方法
	public boolean executeTransact(String id, String Tname, List<Object> param) {
		DataSource ds = dbconnect.getDataSource(id);
		if (!new Util().getProperty("debug").equals("")) {
			System.out.println(Tname);
		}
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return false;
		}
		try {
			cs = this.makeTransactCallable(conn, Tname, param, null);
			rs = cs.executeQuery();
			if (rs != null) {
				parseResultSet(rs);
				rs.close();
			}
			cs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 调用存储过程的方法
	public boolean executeTransactNoQuery(String id, String Tname, List<Object> param) {
		DataSource ds = dbconnect.getDataSource(id);
		if (!new Util().getProperty("debug").equals("")) {
			System.out.println(Tname);
		}
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return false;
		}
		try {
			cs = this.makeTransactCallable(conn, Tname, param, null);
			cs.execute();
			cs.close();
			// conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 输入参数最后一个为返回值?

	public int executeTransactWithInt(String id, String Tname, List<Object> param) {
		DataSource ds = dbconnect.getDataSource(id);
		int result = -95595;
		if (!new Util().getProperty("debug").equals("")) {
			System.out.println(Tname);
		}
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return -95595;
		}
		List<Object> output = new ArrayList<Object>();
		output.add(new Integer(1));
		try {
			cs = this.makeTransactCallable(conn, Tname, param, output);
			cs.execute();
			result = cs.getInt(param.size() + 1);
			cs.close();
			// conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -95595;
		}
		return result;
	}

	// 输入参数最后一个为返回值?

	public String executeTransactWithString(String id, String Tname, List<Object> param) {
		DataSource ds = dbconnect.getDataSource(id);
		String result = null;
		if (!new Util().getProperty("debug").equals("")) {
			System.out.println(Tname);
		}
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return null;
		}
		List<Object> output = new ArrayList<Object>();
		output.add("");
		try {
			cs = this.makeTransactCallable(conn, Tname, param, output);
			cs.execute();
			result = cs.getString(param.size() + 1);
			cs.close();
			// conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	// 调用sql
	public boolean executeSql(String id, String sql) {
		DataSource ds = dbconnect.getDataSource(id);
		System.out.println(sql);
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return false;
		}
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs != null) {
				parseResultSet(rs);
				rs.close();
			}
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean executeUpdateSql(String id, String sql) {
		DataSource ds = dbconnect.getDataSource(id);
		if (!new Util().getProperty("debug").equals("")) {
			System.out.println(sql);
		}
		if (!getConnection(ds)) {
			System.out.println("-----unknow datasource--------");
			return false;
		}
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("出错了");
			return false;
		}
	}

	private void parseResultSet(ResultSet rs) throws SQLException {
		init();
		ResultSetMetaData localResultSetMetaData = rs.getMetaData();
		int i = localResultSetMetaData.getColumnCount();
		this.columnName = new String[i];
		this.columnType = new int[i];
		for (int j = 0; j < i; j++) {
			this.columnName[j] = localResultSetMetaData.getColumnName(j + 1);
			this.columnType[j] = localResultSetMetaData.getColumnType(j + 1);
		}
		while (rs.next()) {
			Object[] arrayOfObject = new Object[i];
			for (int m = 1; m <= i; m++) {
				Object localObject = rs.getObject(m);
				if (localObject == null) {
					arrayOfObject[(m - 1)] = "";
				} else if (this.columnType[(m - 1)] == 2005) {
					Clob localClob = null;
					try {
						localClob = (Clob) localObject;
						if (localClob != null) {
							String str = "";
							StringBuffer localStringBuffer = new StringBuffer("");
							BufferedReader localBufferedReader = new BufferedReader(localClob.getCharacterStream());
							while ((str = localBufferedReader.readLine()) != null)
								localStringBuffer = localStringBuffer.append(str);
							localBufferedReader.close();
							arrayOfObject[(m - 1)] = localStringBuffer.toString();
						}
					} catch (Exception localException) {
						arrayOfObject[(m - 1)] = "";
					}
				} else {
					arrayOfObject[(m - 1)] = localObject;
				}
			}
			this.array.add(arrayOfObject);
		}
	}

	public boolean next() {
		if (this.array.isEmpty()) {
			return false;
		}
		if (this.curpos < this.array.size() - 1) {
			this.curpos += 1;
			return true;
		}

		return false;
	}

	public String getString(int paramInt) {
		paramInt--;
		String str = "";
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos < this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);
			if ((paramInt >= 0) && (paramInt < arrayOfObject.length))
				try {
					str = arrayOfObject[paramInt].toString();

					str = str.trim();
				} catch (Exception localException) {
					str = "";
				}
		}
		return str;
	}

	private int getColumnIndex(String paramString) {
		for (int i = 0; i < this.columnName.length; i++)
			if (this.columnName[i].equalsIgnoreCase(paramString))
				return i + 1;
		return -1;
	}
	
	public String getColumnName(int i){
		return this.columnName[i];
	}

	public boolean getBoolean(String paramString) {
		return getBoolean(getColumnIndex(paramString));
	}

	public int getCounts() {
		return this.array.size();
	}

	public Date getDate(int paramInt) {
		paramInt--;
		Date localDate = null;
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos <= this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);		
			try {
				Object o=arrayOfObject[paramInt];
				if(o!=null&&!(o instanceof String)){
				localDate = (Date)o;
				}
				//localDate = new Date(((Timestamp)arrayOfObject[paramInt]).getTime());
				
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				
//				localDate=sdf.parse((String)arrayOfObject[paramInt]);
				
			} catch (Exception localException) {
				System.out.println(localException.getMessage());
				return null;
			}
		}
		return localDate;
	}

	public Date getDate(String paramString) {
		return getDate(getColumnIndex(paramString));
	}

	public boolean getBoolean(int paramInt) {
		paramInt--;
		boolean bool = false;
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos <= this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);
			try {
				bool = ((Boolean) arrayOfObject[paramInt]).booleanValue();
			} catch (Exception localException) {
				throw new ClassCastException();
			}
		}
		return bool;
	}

	public String getString(String paramString) {
		return getString(getColumnIndex(paramString));
	}

	public double getDouble(int paramInt) {
		paramInt--;
		double d = 0.0D;
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos <= this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);
			try {
				d = Util.getDoubleValue(arrayOfObject[paramInt].toString(), -1.0D);
			} catch (ClassCastException localClassCastException) {
				throw new ClassCastException();
			}
		}
		return d;
	}

	public double getDouble(String paramString) {
		return getDouble(getColumnIndex(paramString));
	}

	public float getFloat(int paramInt) {
		paramInt--;
		float f = 0.0F;
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos <= this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);
			try {
				f = Util.getFloatValue(arrayOfObject[paramInt].toString(), -1.0F);
			} catch (ClassCastException localClassCastException) {
				throw new ClassCastException();
			}
		}
		return f;
	}

	public float getFloat(String paramString) {
		return getFloat(getColumnIndex(paramString));
	}

	public int getInt(int paramInt) {
		paramInt--;
		int i = -1;
		if ((!this.array.isEmpty()) && (this.curpos >= 0) && (this.curpos < this.array.size())) {
			Object[] arrayOfObject = (Object[]) this.array.get(this.curpos);
			if ((paramInt >= 0) && (paramInt < arrayOfObject.length)) {
				i = Util.getIntValue(arrayOfObject[paramInt].toString(), -1);
			}
		}
		return i;
	}

	public int getInt(String paramString) {
		return getInt(getColumnIndex(paramString));
	}
}