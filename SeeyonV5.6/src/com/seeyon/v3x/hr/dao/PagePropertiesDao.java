package com.seeyon.v3x.hr.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.PageProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PagePropertiesDao
  extends BaseHibernateDao<PageProperties>
{
  public void save(PageProperties properties)
  {
    getHibernateTemplate().save(properties);
  }
  
  public void update(PageProperties properties)
  {
    super.update(properties);
  }
  
  public List<PageProperties> findPagePropertiesByPageId(Long page_id)
  {
    return find("From PageProperties p where p.page.id=? order by p.property_ordering", new Object[] { page_id });
  }
  
  public List<PageProperties> findPagePropertiesByPropertyId(Long property_id)
  {
    return find("From PageProperties p where p.pageProperty.id=?", new Object[] { property_id });
  }
  
  public List<PageProperties> findPagePropertiesByPages(List<Long> pageIds)
  {
    String hql = " from " + PageProperties.class.getName() + " p where p.page.id in (:pageIds)";
    Map<String, Object> params = new HashMap();
    params.put("pageIds", pageIds);
    return find(hql, -1, -1, params, new Object[0]);
  }
  
  public void delPageProperties(final Long page_id)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete PageProperties");
        sHql.append(" where page_id = :page_id");
        Query query = session.createQuery(sHql.toString());
        query.setLong("page_id", page_id.longValue());
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
  
  public void delPagePropertiesByPropertyId(final Long property_id)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete PageProperties");
        sHql.append(" where property_id = :property_id");
        Query query = session.createQuery(sHql.toString());
        query.setLong("property_id", property_id.longValue());
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
}
