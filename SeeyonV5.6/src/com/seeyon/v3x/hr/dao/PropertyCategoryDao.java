package com.seeyon.v3x.hr.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.PropertyCategory;

public class PropertyCategoryDao
  extends BaseHibernateDao<PropertyCategory>
{
  public void save(PropertyCategory category)
  {
    getHibernateTemplate().save(category);
  }
  
  public void update(PropertyCategory category)
  {
    getHibernateTemplate().update(category);
  }
  
  public List findAllCategory()
  {
	  return  (List)getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        Long accountId = CurrentUser.get().getLoginAccount();
        String hql = "From PropertyCategory where accountId = :accountId";
        Query query = session.createQuery(hql).setLong("accountId", accountId.longValue());
        return query.list();
      }
    });
  }
  
  public List<PropertyCategory> findCategoryByAccount(Long accountId)
  {
    String hql = " from " + PropertyCategory.class.getName() + " where accountId=?";
    return find(hql, -1, -1, null, new Object[] { accountId });
  }
  
  public void delCategory(final List<Long> ids)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete PropertyCategory");
        sHql.append(" where id in (:ids)");
        Query query = session.createQuery(sHql.toString());
        query.setParameterList("ids", ids);
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
  
  public PropertyCategory findCategoryById(Long id)
  {
    return (PropertyCategory)get(id);
  }
  
  public void delCategoryById(final Long category_id)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete PropertyCategory");
        sHql.append(" where id = :category_id");
        Query query = session.createQuery(sHql.toString());
        query.setLong("category_id", category_id.longValue());
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
  
  public List<PropertyCategory> findCategorysByRemove(int remove)
  {
    String hql = " from " + PropertyCategory.class.getName() + " where accountId=? and remove=?";
    return find(hql, -1, -1, null, new Object[] { CurrentUser.get().getLoginAccount(), Integer.valueOf(remove) });
  }
  
  public List<PropertyCategory> findCategorysByRemove(int remove, boolean sysFlag)
  {
    String hql = " from " + PropertyCategory.class.getName() + " where sysFlag=? and accountId=? and remove=?";
    return find(hql, -1, -1, null, new Object[] { Boolean.valueOf(sysFlag), CurrentUser.get().getLoginAccount(), Integer.valueOf(remove) });
  }
}
