package seikai.entity.hibernate.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Embeddable
public class AuditInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int LENGTH = 50;

	@Column(name = "CREATE_USER_ID", length = LENGTH)
	private String createUserId;

	@Column(name = "CREATE_USER_NAME", length = LENGTH)
	private String createUserName;

	@Column(name = "CREATE_USER_ENGLISH_NAME", length = LENGTH)
	private String createUserEnglishName;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "LAST_MODIFIED_USER_ID", length = LENGTH)
	private String lastModifiedUserId;

	@Column(name = "LAST_MODIFIED_USER_NAME", length = LENGTH)
	private String lastModifiedUserName;

	@Column(name = "LAST_MODIFIED_USER_ENGLISH_NAME", length = LENGTH)
	private String lastModifiedUserEnglishName;

	@Column(name = "LAST_MODIFIED_DATE")
	private Date lastModifiedDate;

}
