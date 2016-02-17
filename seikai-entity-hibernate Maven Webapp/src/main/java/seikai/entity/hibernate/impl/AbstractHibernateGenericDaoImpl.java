package seikai.entity.hibernate.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.orm.hibernate3.HibernateTemplate;

import seikai.entity.base.BaseDto;
import seikai.entity.data.impl.AbstractGenericDaoImpl;
import seikai.entity.hibernate.HibernateGenericDao;

public abstract class AbstractHibernateGenericDaoImpl<Entity extends BaseDto, Id extends Serializable> extends
		AbstractGenericDaoImpl<Entity, Id> implements HibernateGenericDao<Entity, Id> {

	private HibernateTemplate hibernateTemplate;

	@Autowired
	@Required
	@Override
	public final void setSessionFactory(final SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	protected final HibernateOperations getHibernateTemplate() {
		return hibernateTemplate;
	}

	protected Session getCurrentSession() {
		return hibernateTemplate.getSessionFactory().getCurrentSession();
	}

	protected final AuditReader getAuditReader() {
		return AuditReaderFactory.get(getCurrentSession());
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Id save(final Entity entity) {
		return (Id) hibernateTemplate.save(entity);
	}

	@Override
	public final void saveOrUpdate(final Entity entity) {
		hibernateTemplate.saveOrUpdate(entity);
	}

	@Override
	public final Entity get(final Id id) {
		return hibernateTemplate.get(getEntityClass(), id);
	}

	@Override
	public final Entity load(final Id id) {
		return hibernateTemplate.load(getEntityClass(), id);
	}

	@Override
	public final void refresh(final Entity entity) {
		hibernateTemplate.refresh(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Entity> findAll() {
		return getCurrentSession().createCriteria(getEntityClass()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Entity> findByExample(final Entity exampleEntity) {
		return hibernateTemplate.findByExample(exampleEntity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final List<Entity> findByExample(final Entity exampleEntity, final int firstResult, final int maxResults) {
		return hibernateTemplate.findByExample(exampleEntity, firstResult, maxResults);
	}

	@Override
	public final List<Entity> findByRestrictions(final Map<String, Serializable> params) {
		try {
			Entity exampleEntity = getEntityClass().newInstance();
			BeanUtils.populate(exampleEntity, params);
			return findByExample(exampleEntity);
		} catch (Exception e) {
			throw new DataRetrievalFailureException(e.getMessage(), e);
		}
	}

	@Override
	public final List<Entity> findByRestrictions(final Map<String, Serializable> params, final int firstResult,
			final int maxResults) {
		try {
			Entity exampleEntity = getEntityClass().newInstance();
			BeanUtils.populate(exampleEntity, params);
			return findByExample(exampleEntity, firstResult, maxResults);
		} catch (Exception e) {
			throw new DataRetrievalFailureException(e.getMessage(), e);
		}
	}

	@Override
	public final void update(final Entity entity) {
		hibernateTemplate.update(entity);
	}

	@Override
	public final void delete(final Entity entity) {
		hibernateTemplate.delete(entity);
	}

	@Override
	public final void delete(Id id) {
		Entity entity = get(id);
		if (entity != null) {
			delete(entity);
		}
	}

	@Override
	public final int countAll() {
		return ((Long) getCurrentSession().createCriteria(getEntityClass()).setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
	}

	@Override
	public final int countByExample(final Entity exampleEntity) {
		return ((Long) getCurrentSession().createCriteria(getEntityClass()).add(Example.create(exampleEntity))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public final int countByRestrictions(final Map<String, Serializable> params) {
		try {
			Entity exampleEntity = getEntityClass().newInstance();
			BeanUtils.populate(exampleEntity, params);
			return countByExample(exampleEntity);
		} catch (Exception e) {
			throw new DataRetrievalFailureException(e.getMessage(), e);
		}
	}

}
