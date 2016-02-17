package seikai.entity.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import seikai.entity.base.BaseDto;
import seikai.entity.data.GenericDao;

public interface HibernateGenericDao<Entity extends BaseDto, Id extends Serializable> extends GenericDao<Entity, Id> {

	void setSessionFactory(SessionFactory sessionFactory);

}
