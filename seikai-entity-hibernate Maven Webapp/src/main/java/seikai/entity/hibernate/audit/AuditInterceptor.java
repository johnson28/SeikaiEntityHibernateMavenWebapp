package seikai.entity.hibernate.audit;

import java.util.Date;

import org.springframework.beans.factory.annotation.Required;

import seikai.entity.hibernate.interceptor.EmbeddablePropertyInterceptor;

public class AuditInterceptor extends EmbeddablePropertyInterceptor<AuditInfo> {

	private static final long serialVersionUID = 1L;

	private AuditUserIdAware auditUserIdAware;

	/**
	 * @param auditUserIdAware
	 */
	@Required
	public void setAuditUserIdAware(AuditUserIdAware auditUserIdAware) {
		this.auditUserIdAware = auditUserIdAware;
	}

	@Override
	protected void takeCreateEvent(AuditInfo auditInfo) {
		auditInfo.setCreateUserId(auditUserIdAware.getAuditUserId());
		auditInfo.setCreateUserName(auditUserIdAware.getAuditUserName());
		auditInfo.setCreateUserEnglishName(auditUserIdAware.getAuditUserEnglishName());
		auditInfo.setCreateDate(new Date());
	}

	@Override
	protected void takeModifiedEvent(AuditInfo auditInfo) {
		auditInfo.setLastModifiedUserId(auditUserIdAware.getAuditUserId());
		auditInfo.setLastModifiedUserName(auditUserIdAware.getAuditUserName());
		auditInfo.setLastModifiedUserEnglishName(auditUserIdAware.getAuditUserEnglishName());
		auditInfo.setLastModifiedDate(new Date());
	}

}
