package seikai.entity.hibernate.interceptor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.util.ReflectionUtils;

@EqualsAndHashCode(of = "embeddableClass", callSuper = false)
@ToString(of = "embeddableClass")
public abstract class EmbeddablePropertyInterceptor<Embeddable> extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	private Class<Embeddable> embeddableClass;

	@SuppressWarnings("unchecked")
	public EmbeddablePropertyInterceptor() {
		java.lang.reflect.Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		embeddableClass = (Class<Embeddable>) pt.getActualTypeArguments()[0];
	}

	@SneakyThrows
	private Embeddable newInstance() {
		return embeddableClass.newInstance();
	}

	@SuppressWarnings("unchecked")
	private Embeddable findEmbeddableProperty(Object entity, Object[] state, String[] propertyNames) {
		for (int i = 0; i < propertyNames.length; i++) {
			String propertyName = propertyNames[i];
			Field auditInfoField = ReflectionUtils.findField(entity.getClass(), propertyName, embeddableClass);
			if (auditInfoField != null) {
				if (state[i] == null) {
					state[i] = newInstance();
				}
				return (Embeddable) state[i];
			}
		}
		return null;
	}

	@Override
	public final boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		Embeddable embeddable = findEmbeddableProperty(entity, state, propertyNames);
		if (embeddable != null) {
			takeCreateEvent(embeddable);
			return true;
		} else {
			return false;
		}
	}

	protected abstract void takeCreateEvent(Embeddable embeddable);

	@Override
	public final boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		Embeddable embeddable = findEmbeddableProperty(entity, currentState, propertyNames);
		if (embeddable != null) {
			takeModifiedEvent(embeddable);
			return true;
		} else {
			return false;
		}
	}

	protected abstract void takeModifiedEvent(Embeddable embeddable);

}
