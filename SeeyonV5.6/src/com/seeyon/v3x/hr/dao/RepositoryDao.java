package com.seeyon.v3x.hr.dao;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.CollectionUtils;

public class RepositoryDao extends BaseHibernateDao<Repository> {
	private static final transient Log LOG = LogFactory.getLog(RepositoryDao.class);

	public void save(Repository repository) {
		getHibernateTemplate().save(repository);
	}

	public Repository findById(Long id) {
		return (Repository) get(id);
	}

	public void update(Repository repository) {
		getHibernateTemplate().update(repository);
	}

	public void delRepository(final Long page_id) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete Repository");
				sHql.append(" where page_id = :page_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("page_id", page_id.longValue());
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

	public void delRepository(final Long property_id, final Long member_id) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete Repository");
				sHql.append(" where member_id=:member_id and property_id = :property_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("member_id", member_id.longValue()).setLong("property_id", property_id.longValue());
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

	public void delRepositoryByOperationId(Long id) {
		String hql = "delete from " + Repository.class.getName() + " where operation_id=:id";
		Map<String, Object> params = new HashMap();
		params.put("id", id);
		try {
			super.bulkUpdate(hql, params, new Object[0]);
		} catch (Exception e) {
			this.logger.error("删除工资：", e);
		}
	}

	public void delRepositoryByOperationId(List<Long> ids) {
		String hql = "delete from " + Repository.class.getName() + " where operation_id in(:ids)";
		Map<String, Object> params = new HashMap();
		params.put("ids", ids);
		try {
			super.bulkUpdate(hql, params, new Object[0]);
		} catch (Exception e) {
			this.logger.error("批量删除工资：", e);
		}
	}

	public void delRepositoryByIds(final List<Long> ids) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete Repository");
				sHql.append(" where id in (:ids)");
				Query query = session.createQuery(sHql.toString());
				query.setParameterList("ids", ids);
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

	public List findRepositoryByMemberIdAndPropertyId(final Long memberId, final Long property_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "from Repository where memberId = :memberId and property_id = :property_id order by createTime";
				Query query = session.createQuery(hql).setLong("memberId", memberId.longValue()).setLong("property_id",
						property_id.longValue());
				return query.list();
			}
		});
	}

	public List findRepositoryByMemberIdAndPropertyIdAndpageId(final Long memberId, final Long property_id,
			final Long page_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "from Repository where memberId = :memberId and property_id = :property_id and page_id = :page_id order by createTime";
				Query query = session.createQuery(hql).setLong("memberId", memberId.longValue())
						.setLong("property_id", property_id.longValue()).setLong("page_id", page_id.longValue());
				return query.list();
			}
		});
	}

	public List findRepositoryByOperation_id(final Long operation_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "from Repository where operation_id = :operation_id order by ordering";
				Query query = session.createQuery(hql).setLong("operation_id", operation_id.longValue());
				return query.list();
			}
		});
	}

	public List findRepositoryByPage_id(final Long page_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "from Repository where page_id = :page_id order by ordering";
				Query query = session.createQuery(hql).setLong("page_id", page_id.longValue());
				return query.list();
			}
		});
	}

	public List<Repository> getRepositoryPropertyId(List<Long> property_ids) {
		if ((property_ids == null) || (property_ids.isEmpty())) {
			return new ArrayList(0);
		}
		String hql = "from Repository where property_id in (:property_ids) order by ordering";
		Map<String, Object> map = new HashMap();
		map.put("property_ids", property_ids);
		return super.find(hql, -1, -1, map, new Object[0]);
	}

	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> property_ids, Long accountId) {
		if ((property_ids == null) || (property_ids.isEmpty())) {
			return new ArrayList(0);
		}
		String hql = "select repository from Repository repository ,Salary salary where repository.property_id in (:property_ids)  and repository.operation_id = salary.id and salary.accountId = :accountId order by ordering";

		Map<String, Object> map = new HashMap();

		map.put("property_ids", property_ids);

		map.put("accountId", accountId);

		return super.find(hql, -1, -1, map, new Object[0]);
	}

	public List<Repository> getSalaryAdminRepositoryPropertyId(List<Long> salaryIds, List<Long> property_ids,
			Long accountId) {
		if ((CollectionUtils.isEmpty(property_ids)) || (CollectionUtils.isEmpty(salaryIds))) {
			return new ArrayList(0);
		}
		String hql = "select repository from Repository repository ,Salary salary where repository.operation_id = salary.id and  repository.property_id in (:property_ids) and  salary.id in (:salaryIds) and  salary.accountId = :accountId order by repository.ordering";

		Map<String, Object> map = new HashMap();
		map.put("salaryIds", salaryIds);
		map.put("property_ids", property_ids);
		map.put("accountId", accountId);

		return find(hql, -1, -1, map, new Object[0]);
	}
}
