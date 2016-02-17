package seikai.entity.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import seikai.entity.base.RecordDto;
import seikai.entity.data.GenericRecordDao;

public interface HibernateGenericRecordDao<Entity extends RecordDto, EntityHistory extends RecordDto, Id extends Serializable>
		extends GenericRecordDao<Entity, EntityHistory, Id> {

	void setSessionFactory(SessionFactory sessionFactory);

}
