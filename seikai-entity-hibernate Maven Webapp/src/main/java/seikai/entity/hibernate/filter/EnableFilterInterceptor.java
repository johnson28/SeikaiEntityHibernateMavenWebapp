package seikai.entity.hibernate.filter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.ReflectionUtils;

import seikai.entity.hibernate.HibernateGenericDao;
import seikai.entity.hibernate.HibernateGenericPagingDao;

public class EnableFilterInterceptor implements MethodInterceptor {

	private EnableFilterInfoAware enableFilterInfoAware;

	@Required
	public void setEnableFilterInfoAware(EnableFilterInfoAware enableFilterInfoAware) {
		this.enableFilterInfoAware = enableFilterInfoAware;
	}

	@Override
	@SneakyThrows
	public Object invoke(MethodInvocation invocation) {
		Object target = invocation.getThis();
		if (target instanceof HibernateGenericDao || target instanceof HibernateGenericPagingDao) {
			SessionFactory sessionFactory = getSessionFactory(target);
			Session session = sessionFactory.getCurrentSession();
			@SuppressWarnings("unchecked")
			Set<String> filterNames = sessionFactory.getDefinedFilterNames();
			Map<String, Map<String, Object>> filterInfo = enableFilterInfoAware.getEnableFilterInfo();
			for (String filterName : filterNames) {
				if (filterInfo.containsKey(filterName)) {
					Filter filter = session.getEnabledFilter(filterName);
					if (filter == null) {
						filter = session.enableFilter(filterName);
						Map<String, Object> parameters = filterInfo.get(filterName);
						for (String parameterName : parameters.keySet()) {
							Object parameterValue = parameters.get(parameterName);
							filter.setParameter(parameterName, parameterValue);
						}
						filter.validate();
					}
				}
			}
		}

		Object result = invocation.proceed();
		return result;
	}

	private SessionFactory getSessionFactory(Object target) {
		Field hibernateTemplateField = ReflectionUtils.findField(target.getClass(), "hibernateTemplate",
				HibernateTemplate.class);
		ReflectionUtils.makeAccessible(hibernateTemplateField);
		HibernateTemplate hibernateTemplate = (HibernateTemplate) ReflectionUtils.getField(hibernateTemplateField,
				target);
		SessionFactory sessionFactory = hibernateTemplate.getSessionFactory();
		return sessionFactory;
	}

}
