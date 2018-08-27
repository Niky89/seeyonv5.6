package com.deheng.Quartz;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.util.JDBCAgent;

public class updatePreFilingTask implements Runnable{

	/**
	 * 预立案客户更新
	 */
	@Override
	public void run() {
		JDBCAgent dba = new JDBCAgent();
		String sql = "update formmain_0226 set field0013='-7521400829327422566',field0032=isnull(field0032,'')+'-系统自动更新预立案过期客户为目标客户-' +convert(varchar(20),getdate(),120)   where field0013=-8598074631733755684 and start_date< dateadd(day,-30,getdate())";
		
		try {
			int i = dba.execute(sql);
		} catch (BusinessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dba.close(); 
	}

}
