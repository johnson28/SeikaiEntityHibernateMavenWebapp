package seikai.entity.hibernate.interceptor;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.extern.apachecommons.CommonsLog;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

@CommonsLog
public class EmbeddablePropertyInterceptorChainProxy extends EmptyInterceptor {

	private static final long serialVersionUID = 5566719359520070978L;

	private Set<EmbeddablePropertyInterceptor<?>> interceptorChain = new LinkedHashSet<EmbeddablePropertyInterceptor<?>>();

	public void setInterceptorChain(Set<EmbeddablePropertyInterceptor<?>> interceptorChain) {
		this.interceptorChain.addAll(interceptorChain);
	}

	public void addInterceptorChain(EmbeddablePropertyInterceptor<?> interceptor) {
		interceptorChain.add(interceptor);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		boolean isStateChanged = false;
		for (EmbeddablePropertyInterceptor<?> interceptor : interceptorChain) {
			log.debug("invoke onFlushDirty of " + interceptor.toString());

			if (interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types)) {
				isStateChanged = true;
			}
		}
		return isStateChanged;
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		boolean isStateChanged = false;
		for (EmbeddablePropertyInterceptor<?> interceptor : interceptorChain) {
			log.debug("invoke onSave of " + interceptor.toString());

			if (interceptor.onSave(entity, id, state, propertyNames, types)) {
				isStateChanged = true;
			}
		}
		return isStateChanged;
	}

}
