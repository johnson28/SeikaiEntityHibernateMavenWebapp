package seikai.entity.hibernate.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.orm.hibernate3.HibernateTemplate;

import seikai.entity.base.BaseDto;
import seikai.entity.data.impl.AbstractGenericPagingDaoImpl;
import seikai.entity.hibernate.HibernateGenericPagingDao;

public abstract class AbstractHibernateGenericPagingDaoImpl<Entity extends BaseDto, Condition> extends
		AbstractGenericPagingDaoImpl<Entity, Condition> implements HibernateGenericPagingDao<Entity, Condition> {

	private HibernateTemplate hibernateTemplate;

	@Autowired
	@Required
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	protected HibernateOperations getHibernateTemplate() {
		return hibernateTemplate;
	}

	protected Session getCurrentSession() {
		return hibernateTemplate.getSessionFactory().getCurrentSession();
	}

	@Override
	public List<Entity> findPagingResults(final Condition condition, final int firstResult, final int maxResults) {
		return findPagingTransformResults(condition, firstResult, maxResults, getEntityClass());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <ResultClass> List<ResultClass> findPagingTransformResults(Condition condition, int firstResult,
			int maxResults, Class<ResultClass> resultClass) {
		Criteria criteria = getCurrentSession().createCriteria(getEntityClass(),
				getEntityClass().getSimpleName().toLowerCase());
		initCriteria(condition, criteria);
		exclusiveForPagingResults(condition, criteria);
		criteria.setFirstResult(firstResult);
		criteria.setMaxResults(maxResults);
		if (getEntityClass() != resultClass) {
			criteria.setResultTransformer(new AliasToBeanResultTransformer(resultClass));
		}
		return criteria.list();
	}

	@Override
	public long getRowCount(final Condition condition) {
		Criteria criteria = getCurrentSession().createCriteria(getEntityClass());
		initCriteria(condition, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	protected abstract void initCriteria(Condition condition, Criteria criteria);

	protected void exclusiveForPagingResults(Condition condition, Criteria criteria) {
	}

}
