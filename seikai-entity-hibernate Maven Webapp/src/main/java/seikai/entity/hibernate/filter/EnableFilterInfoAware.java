package seikai.entity.hibernate.filter;

import java.util.Map;

public interface EnableFilterInfoAware {

	Map<String, Map<String, Object>> getEnableFilterInfo();

}
