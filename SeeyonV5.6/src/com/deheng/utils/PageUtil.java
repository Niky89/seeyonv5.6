package com.deheng.utils;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
	
	//当前页面
	private int page = 1;
	//显示多少行
	private int rows = 20;
	//总记录条数
	private int total;
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	


}
