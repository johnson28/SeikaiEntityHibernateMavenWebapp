package seikai.entity.hibernate;

import org.hibernate.SessionFactory;

import seikai.entity.base.BaseDto;
import seikai.entity.data.GenericPagingDao;

public interface HibernateGenericPagingDao<Entity extends BaseDto, Condition> extends
		GenericPagingDao<Entity, Condition> {

	void setSessionFactory(SessionFactory sessionFactory);

}
