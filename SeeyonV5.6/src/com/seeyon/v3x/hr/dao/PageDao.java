package com.seeyon.v3x.hr.dao;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.Page;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PageDao
  extends BaseHibernateDao<Page>
{
  public List<Page> findAllPage()
  {
    return getAll();
  }
  
  public void save(Page page)
  {
    getHibernateTemplate().save(page);
  }
  
  public void updatePage(Page page)
  {
    getHibernateTemplate().update(page);
  }
  
  public void delPage(final List<Long> ids)
  {
    getHibernateTemplate().execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        StringBuffer sHql = new StringBuffer();
        sHql.append("delete Page");
        sHql.append(" where id in (:ids)");
        Query query = session.createQuery(sHql.toString());
        query.setParameterList("ids", ids);
        return Integer.valueOf(query.executeUpdate());
      }
    });
  }
  
  public Page findPageById(Long page_id)
  {
    return (Page)get(page_id);
  }
  
  public List<Page> findPageByModelName(String modelName)
  {
    return findPageByModelName(modelName, false, false);
  }
  
  public List<Page> findPageByModelName(String modelName, boolean containRemove, boolean containDisplay)
  {
    Map<String, Object> params = new HashMap();
    StringBuilder hql = new StringBuilder();
    hql.append("from " + Page.class.getName() + " where accountId=:accountId and modelName=:modelName ");
    params.put("accountId", CurrentUser.get().getLoginAccount());
    params.put("modelName", modelName);
    if (!containRemove)
    {
      hql.append(" and remove=:remove ");
      params.put("remove", Integer.valueOf(0));
    }
    if (!containDisplay)
    {
      hql.append(" and pageDisplay=:pageDisplay ");
      params.put("pageDisplay", Integer.valueOf(0));
    }
    hql.append(" order by sort");
    return find(hql.toString(), -1, -1, params, new Object[0]);
  }
  
  public List<Page> findPageByRemove(int remove)
  {
    return find("From Page where accountId=? and remove=? order by sort", -1, -1, null, new Object[] { CurrentUser.get().getLoginAccount(), Integer.valueOf(remove) });
  }
  
  public List<Page> findPageByAccount(Long accountId)
  {
    String hql = " from " + Page.class.getName() + " where accountId=?";
    return find(hql, -1, -1, null, new Object[] { accountId });
  }
  
  public Page getById(Long id)
  {
    return (Page)get(id);
  }
}
