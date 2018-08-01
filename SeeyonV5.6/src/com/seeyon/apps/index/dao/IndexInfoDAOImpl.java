package com.seeyon.apps.index.dao;

import com.seeyon.apps.index.po.IndexIdInfo;
import com.seeyon.ctp.common.dao.BaseHibernateDao;
import com.seeyon.ctp.util.Datetimes;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class IndexInfoDAOImpl
  extends BaseHibernateDao<IndexIdInfo>
  implements IndexInfoDAO
{
  private static final Log log = LogFactory.getLog(IndexInfoDAOImpl.class);
  
  public List<IndexIdInfo> records()
  {
    Date date = Datetimes.addDate(new Date(), -90);
    super.getHibernateTemplate().bulkUpdate("delete from IndexIdInfo where createDate<?", date);
    log.info("清除90天以前的未入库数据");
    
    List<IndexIdInfo> indexIdInfos = getHibernateTemplate().find("from IndexIdInfo");
    log.info("加载未入库的数据：" + indexIdInfos.size() + "条");
    
    return indexIdInfos;
  }
  
  public void delete(final Long entityId)
  {
    HibernateTemplate ht = getHibernateTemplate();
    ht.execute(new HibernateCallback()
    {
      public Object doInHibernate(Session session)
        throws HibernateException
      {
        Criteria criteria = session.createCriteria(IndexIdInfo.class);
        criteria.add(Restrictions.eq("entityId", entityId));
        criteria.addOrder(Order.asc("createDate"));
        criteria.setFirstResult(0).setMaxResults(1);
        IndexIdInfo result = (IndexIdInfo)criteria.uniqueResult();
        session.delete(result);
        return null;
      }
    });
  }
  
  public void save(Long entityId, Integer type)
  {
    IndexIdInfo index = getV3xIndexByEntityId(entityId.longValue());
    if (index == null)
    {
      IndexIdInfo updateIndex = new IndexIdInfo();
      updateIndex.setEntityId(entityId);
      updateIndex.setIdIfNew();
      updateIndex.setType(type);
      updateIndex.setCreateDate(new Date());
      save(updateIndex);
    }
  }
  
  public void save(Long entityId, Integer type, Integer operation)
  {
    IndexIdInfo index = getV3xIndexByEntityId(entityId.longValue());
    if (index == null)
    {
      IndexIdInfo updateIndex = new IndexIdInfo();
      updateIndex.setEntityId(entityId);
      updateIndex.setIdIfNew();
      updateIndex.setType(type);
      updateIndex.setOperation(operation);
      updateIndex.setCreateDate(new Date());
      save(updateIndex);
    }
  }
  
  public IndexIdInfo getV3xIndexByEntityId(long entityId)
  {
    DetachedCriteria criteria = DetachedCriteria.forClass(this.entityClass);
    criteria.add(Restrictions.eq("entityId", Long.valueOf(entityId)));
    List l = null;
    try
    {
      l = super.executeCriteria(criteria, -1, -1);
      if ((l == null) || (l.isEmpty())) {
        return null;
      }
      return (IndexIdInfo)l.get(0);
    }
    catch (Exception e)
    {
      log.error("", e);
    }
    return null;
  }
  
  public void deleteIndex(List<Long> entityList)
  {
    if (CollectionUtils.isNotEmpty(entityList))
    {
      String sql = "delete from " + IndexIdInfo.class.getName() + " where 1=1";
      Map<String, Object> nameParameters = new HashMap();
      for(Long l:entityList){
    	  sql+=" and entityId="+String.valueOf(l);
      }
      super.bulkUpdate(sql, nameParameters, new Object[0]);
    }
  }
  
  public void deleteIndexForDelete(List<Long> entityList)
  {
    if ((entityList == null) || (entityList.isEmpty())) {
      return;
    }
    String sql = "delete from " + IndexIdInfo.class.getName() + " where operation= " + IndexIdInfo.INDEX_TYPE_DELETE;
    Map<String, Object> nameParameters = new HashMap();
    for(Long l:entityList){
  	  sql+=" and entityId="+String.valueOf(l);
    }
    super.bulkUpdate(sql, nameParameters, new Object[0]);
  }
}
