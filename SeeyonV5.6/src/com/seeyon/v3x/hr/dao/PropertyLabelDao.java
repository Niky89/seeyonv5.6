package com.seeyon.v3x.hr.dao;

import com.seeyon.ctp.util.CommonTools;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.Language;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PropertyLabelDao
  extends BaseHibernateDao<PropertyLabel>
{
  private LanguageDao languageDao;
  private List<Language> languageList;
  
  public List<Language> getLanguageList()
  {
    this.languageDao.findAllLanguage();
    return this.languageList;
  }
  
  public void setLanguageList(List<Language> languageList)
  {
    this.languageList = languageList;
  }
  
  public LanguageDao getLanguageDao()
  {
    return this.languageDao;
  }
  
  public void setLanguageDao(LanguageDao languageDao)
  {
    this.languageDao = languageDao;
  }
  
  public void save(PropertyLabel propertyLabel)
  {
    getHibernateTemplate().save(propertyLabel);
  }
  
  public void update(PropertyLabel propertyLabel)
  {
    super.update(propertyLabel);
  }
  
  public List<PropertyLabel> findPropertyLabelByPropertyId(Long property_id)
  {
    return find("From PropertyLabel where property_id=?", new Object[] { property_id });
  }
  
  public List<PropertyLabel> findPropertyLabelByPropertyIds(List<Long> property_ids)
  {
    return find("From PropertyLabel where property_id in (:property_ids)", -1, -1, CommonTools.newHashMap("property_ids", property_ids), new Object[0]);
  }
  
  public PropertyLabel findPropertyLabelByName(String propertyLabelValue)
  {
    return (PropertyLabel)findUniqueBy("propertyLabelValue", propertyLabelValue);
  }
  
  public List<PropertyLabel> findPropertyLabelByPropertyIdAndLanguage(Long property_id, int language)
  {
    return find("From PropertyLabel where property_id=? and language=?", new Object[] { property_id, Integer.valueOf(language) });
  }
  
  public void delPropertyLabel(final Long property_id)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete PropertyLabel");
        sHql.append(" where property_id = :property_id");
        Query query = session.createQuery(sHql.toString());
        query.setLong("property_id", property_id.longValue());
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
  
  public List<PropertyLabel> findAllPropertyLabel()
  {
    return getAll();
  }
}
