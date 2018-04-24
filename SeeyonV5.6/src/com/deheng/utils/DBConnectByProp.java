package com.deheng.utils;

import javax.sql.DataSource;

import com.seeyon.ctp.common.AppContext;


public class DBConnectByProp implements DBConnect{

	@Override
	public DataSource getDataSource(String id) {
		return (DataSource) AppContext.getBean("dh2dataSource");
	}

}
