package com.deheng.utils;


import javax.sql.DataSource;

public interface DBConnect {
	public DataSource getDataSource(String id);
}
