package com.seeyon.v3x.hr.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.HrSalaryPassword;

public class HrSalaryPasswordDao
  extends BaseHibernateDao<HrSalaryPassword>
{
  public HrSalaryPassword getSalaryRecordUniq(long userId)
  {
    return (HrSalaryPassword)findUniqueBy("userId", Long.valueOf(userId));
  }
}
