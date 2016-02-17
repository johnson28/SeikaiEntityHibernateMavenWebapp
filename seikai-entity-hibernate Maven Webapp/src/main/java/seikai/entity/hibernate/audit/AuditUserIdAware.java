package seikai.entity.hibernate.audit;

import java.io.Serializable;

public interface AuditUserIdAware extends Serializable {

	String getAuditUserId();

	String getAuditUserName();

	String getAuditUserEnglishName();

}
