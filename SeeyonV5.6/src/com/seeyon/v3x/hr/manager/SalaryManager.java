package com.seeyon.v3x.hr.manager;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import java.util.List;

public abstract interface SalaryManager
{
  public abstract List findAllStaffSalary();
  
  public abstract List<Salary> findAllAccountStaffSalary(Long paramLong, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);
  
  public abstract void exportSalary(List<Salary> paramList1, List<Salary> paramList2, List<Repository> paramList)
    throws BusinessException;
  
  public abstract void addSalary(Salary paramSalary);
  
  public abstract void removeSalaryByIds(List<Long> paramList);
  
  public abstract Salary findSalaryById(Long paramLong);
  
  public abstract void updateSalary(Salary paramSalary);
  
  public abstract List findSalaryByStaffId(Long paramLong);
  
  public abstract List findSalaryByStaffId(Long paramLong, boolean paramBoolean);
  
  public abstract Salary getSalaryByStaffNameAndDate(String paramString, int paramInt1, int paramInt2)
    throws Exception;
  
  public abstract List getSalaryByTime(Long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws Exception;
  
  public abstract List getSalaryByTime(Long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    throws Exception;
  
  public abstract List getSalaryByName(String paramString);
  
  public abstract List getSalaryByName(String paramString, boolean paramBoolean);
  
  public abstract List getAllStaffSalarysByDate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws Exception;
  
  public abstract List getAllStaffSalarysByDate(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    throws Exception;
  
  public abstract List getSalaryByBasic(float paramFloat1, float paramFloat2);
  
  public abstract List getSalaryByBasic(float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  public abstract List getSalaryByActually(float paramFloat1, float paramFloat2);
  
  public abstract List getSalaryByActually(float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  public abstract boolean hasSalaryPasswordRecord(Long paramLong);
  
  public abstract void setSalaryPasswordRecord(Long paramLong, String paramString)
    throws Exception;
  
  public abstract boolean checkPassWord(Long paramLong, String paramString)
    throws Exception;
  
  public abstract boolean updatePassWord(Long paramLong, String paramString)
    throws Exception;
  
  public abstract boolean updatePassWord(String paramString1, String paramString2)
    throws Exception;
  
  public abstract void addAllSalary(List<Salary> paramList)
    throws Exception;
  
  public abstract int getfindSlary(Long paramLong, String paramString);
  
  public abstract String findSlaryBystaffidAndDate(Long paramLong, String paramString);
  
  public abstract void sendSalaryMessage(String paramString1, String paramString2);
}
