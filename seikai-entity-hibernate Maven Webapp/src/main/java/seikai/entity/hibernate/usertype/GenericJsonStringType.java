package seikai.entity.hibernate.usertype;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import seikai.gson.ISODateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class GenericJsonStringType<T> implements UserType {

	private static final int[] SQL_TYPES = { Types.LONGVARCHAR };

	private final Type userType;

	private final Gson gson;

	public GenericJsonStringType() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) type;
		userType = pt.getActualTypeArguments()[0];
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new ISODateTimeAdapter()).create();
	}

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class returnedClass() {
		if (userType instanceof Class) {
			return (Class) userType;
		} else {
			return (Class) ((ParameterizedType) userType).getRawType();
		}
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return ObjectUtils.equals(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	// @Override
	// public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
	// throws HibernateException, SQLException {
	// String jsonString = rs.getString(names[0]);
	// return gson.fromJson(jsonString, userType);
	// }
	//
	// @Override
	// public void nullSafeSet(PreparedStatement st, Object value, int index)
	// throws HibernateException, SQLException {
	// st.setString(index, gson.toJson(value, userType));
	// }

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return gson.fromJson(gson.toJson(value, userType), userType);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return gson.toJson(value, userType);
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return gson.fromJson((String) cached, userType);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
