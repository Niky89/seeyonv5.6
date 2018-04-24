package com.seeyon.v3x.hr.util;

import java.util.List;
import org.hibernate.Query;

public class HibernatePage
{
  private List results;
  private int pageSize = 20;
  private int pageNo = 0;
  
  public int getPageSize()
  {
    return this.pageSize;
  }
  
  public void setPageSize(int pageSize)
  {
    if (pageSize > 0) {
      this.pageSize = pageSize;
    }
  }
  
  public int getPageNo()
  {
    return this.pageNo;
  }
  
  public void setPageNo(int pageNo)
  {
    if (pageNo > 0) {
      this.pageNo = pageNo;
    }
  }
  
  public List getResults()
  {
    return this.results;
  }
  
  public HibernatePage(Query query, int pageNo, int pageSize)
  {
    setPageNo(pageNo);
    setPageSize(pageSize);
    this.results = query.setFirstResult(getPageNo() * getPageSize()).setMaxResults(
      getPageSize()).list();
  }
}
