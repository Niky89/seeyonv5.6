package com.seeyon.v3x.hr.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.Language;
import java.util.List;

public class LanguageDao
  extends BaseHibernateDao<Language>
{
  public List<Language> findAllLanguage()
  {
    return getAll();
  }
}
