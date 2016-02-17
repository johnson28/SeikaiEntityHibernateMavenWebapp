package seikai.entity.hibernate.impl;

import static org.hibernate.criterion.Restrictions.eq;

import java.io.Serializable;
import java.util.Date;
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
import org.springframework.orm.hibernate4.HibernateOperations;
import org.springframework.orm.hibernate4.HibernateTemplate;

import seikai.entity.base.RecordDto;
import seikai.entity.data.impl.AbstractGenericRecordDaoImpl;
import seikai.entity.hibernate.HibernateGenericRecordDao;

public abstract class AbstractHibernateGenericRecordDaoImpl<Entity extends RecordDto, EntityHistory extends RecordDto, Id extends Serializable>
		extends AbstractGenericRecordDaoImpl<Entity, EntityHistory, Id> implements
		HibernateGenericRecordDao<Entity, EntityHistory, Id> {

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

	@Override
	@SuppressWarnings({ "unchecked" })
	public final Id save(final Entity entity) throws Exception {
		Id id = (Id) hibernateTemplate.save(entity);
		copyEntity(entity);
		return id;
	}

	@Override
	public final void saveOrUpdate(final Entity entity) throws Exception {
		hibernateTemplate.saveOrUpdate(entity);
		copyEntity(entity);
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

	@Override
	@SuppressWarnings("unchecked")
	public final List<Entity> findAll() {
		return getCurrentSession().createCriteria(getEntityClass()).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final List<Entity> findAllEnable() {
		return getCurrentSession().createCriteria(getEntityClass()).add(eq("dataEnabled", true)).list();
	}

	@Override
	public final List<Entity> findByExample(final Entity exampleEntity) {
		return hibernateTemplate.findByExample(exampleEntity);
	}

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
	public final void update(final Entity entity) throws Exception {
		hibernateTemplate.update(entity);
		copyEntity(entity);
	}

	@Override
	public final void delete(Entity entity) throws Exception {
		entity.setDataEnabled(false);
		update(entity);
	}

	@Override
	public final void delete(Id id) throws Exception {
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

	private void copyEntity(final Entity entity) throws Exception {
		EntityHistory entityHistory = getEntityHistoryClass().newInstance();
		BeanUtils.copyProperties(entityHistory, entity);
		entityHistory.setId(null);
		entityHistory.setCreateDate(new Date());
		entityHistory.setRecordId(entity.getId());
		saveHistory(entityHistory);
	}

	private final void saveHistory(final EntityHistory entityHistory) {
		hibernateTemplate.save(entityHistory);
	}

}
