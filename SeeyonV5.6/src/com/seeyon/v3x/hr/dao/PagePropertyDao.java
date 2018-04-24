package com.seeyon.v3x.hr.dao;

import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PagePropertyDao extends BaseHibernateDao<PageProperty> {
	public void save(PageProperty pageProperty) {
		getHibernateTemplate().save(pageProperty);
	}

	public void update(PageProperty pageProperty) {
		getHibernateTemplate().update(pageProperty);
	}

	public PageProperty findPropertyById(Long property_id) {
		return (PageProperty) get(property_id);
	}

	public List<PageProperty> findAllProperty() {
		return getAll();
	}

	public void delProperty(final List<Long> ids) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PageProperty");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("ids", ids);
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

	public List findPropertyByPageId(final Long page_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From PageProperty where page_id = :page_id";
				Query query = session.createQuery(hql).setLong("page_id", page_id.longValue());
				return query.list();
			}
		});
	}

	public List<PageProperty> findPropertyByAccount(Long accountId) {
		String hql = " from " + PageProperty.class.getName() + " where accountId=?";
		return find(hql, -1, -1, null, new Object[] { accountId });
	}

	public List<PageProperty> findPropertyByRemove(int remove) {
		String hql = " from " + PageProperty.class.getName() + " where accountId=? and remove=?";
		return find(hql, -1, -1, null, new Object[] { CurrentUser.get().getLoginAccount(), Integer.valueOf(remove) });
	}

	public List<PageProperty> findPropertyByRemove(int remove, boolean sysFlag) {
		String hql = " from " + PageProperty.class.getName() + " where sysFlag=? and accountId=? and remove=?";
		return find(hql, -1, -1, null, new Object[] { Boolean.valueOf(sysFlag), CurrentUser.get().getLoginAccount(),
				Integer.valueOf(remove) });
	}

	public List findPropertyByRemove(final int remove, int pageNo, int pageSize) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From PageProperty where remove = :remove";
				Query query = session.createQuery(hql).setInteger("remove", remove);
				return query.list();
			}
		});
	}

	public List findPropertyByCategoryId(final Long category_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From PageProperty where category_id = :category_id";
				Query query = session.createQuery(hql).setLong("category_id", category_id.longValue());
				return query.list();
			}
		});
	}

	public List findPropertyByCategoryId(final Long category_id, final int remove) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From PageProperty where category_id = :category_id and remove = :remove";
				Query query = session.createQuery(hql).setLong("category_id", category_id.longValue())
						.setInteger("remove", remove);
				return query.list();
			}
		});
	}

	public List<PageProperty> findUnUsePropertyByCategoryId(Long category_id, int remove) {
		Map<String, Object> params = new HashMap();
		String hql = " from " + PageProperty.class.getName()
				+ " p where p.category_id = :category_id and p.remove = :remove "
				+ "and p.id not in (select ps.pageProperty.id from " + PageProperties.class.getName() + " ps)";
		params.put("category_id", category_id);
		params.put("remove", Integer.valueOf(remove));
		return super.find(hql, -1, -1, params, new Object[0]);
	}
}
