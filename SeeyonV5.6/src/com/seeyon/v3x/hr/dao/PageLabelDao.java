package com.seeyon.v3x.hr.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.ctp.util.CommonTools;
import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.hr.domain.PageLabel;

public class PageLabelDao extends BaseHibernateDao<PageLabel> {
	private LanguageDao languageDao;
	private List languageList;

	public List getLanguageList() {
		this.languageDao.findAllLanguage();
		return this.languageList;
	}

	public void setLanguageList(List languageList) {
		this.languageList = languageList;
	}

	public LanguageDao getLanguageDao() {
		return this.languageDao;
	}

	public void setLanguageDao(LanguageDao languageDao) {
		this.languageDao = languageDao;
	}

	public void save(PageLabel pageLabel) {
		getHibernateTemplate().save(pageLabel);
	}

	public void update(PageLabel pageLabel) {
		getHibernateTemplate().update(pageLabel);
	}

	public List findPageLabelByPageId(final Long page_id) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From PageLabel where page_id = :page_id";
				Query query = session.createQuery(hql).setLong("page_id", page_id.longValue());
				return query.list();
			}
		});
	}

	public List findPageLabelByPageIdAndLange(final Long page_id, final int language) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String hql = "From pageLabel where page_id = :page_id and language = :language";
				Query query = session.createQuery(hql).setLong("page_id", page_id.longValue()).setInteger("language",
						language);
				return query.list();
			}
		});
	}

	public void delPageLabel(final Long page_id) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sHql = new StringBuffer();
				sHql.append("delete PageLabel");
				sHql.append(" where page_id = :page_id");
				Query query = session.createQuery(sHql.toString());
				query.setLong("page_id", page_id.longValue());
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

	public List<PageLabel> findPageLabelByPageIds(List<Long> pageIds) {
		return find(" from " + PageLabel.class.getName() + " as p where p.page_id in (:pageIds)", -1, -1,
				CommonTools.newHashMap("pageIds", pageIds), new Object[0]);
	}
}
