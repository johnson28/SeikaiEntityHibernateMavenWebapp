package seikai.entity.hibernate.dialet;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2008Dialect;

public class SQLServer2008NationalDialect extends SQLServer2008Dialect {

	/**
	 * select.
	 */
	private static final String SELECT = "select";

	/**
	 * from.
	 */
	private static final String FROM = " from ";

	/**
	 * distinct.
	 */
	private static final String DISTINCT = "distinct";

	/**
	 * order by.
	 */
	private static final String ORDERBY = "order by";

	/**
	 * Max nvarchar length = 8000.
	 */
	private static final int MAX_LENGTH = 8000;

	/**
	 * override varchar by nvarchar.
	 */
	public SQLServer2008NationalDialect() {
		registerColumnType(Types.CLOB, "nvarchar(MAX)");
		registerColumnType(Types.LONGVARCHAR, "nvarchar(MAX)");
		registerColumnType(Types.VARCHAR, "nvarchar(MAX)");
		registerColumnType(Types.VARCHAR, MAX_LENGTH, "nvarchar($l)");
	}

	/**
	 * Fix table/column name case sensitive issue.
	 */
	@Override
	public String getLimitString(String querySqlString, boolean hasOffset) {
		/*
		 * Do NOT toLowerCase() StringBuilder sb = new
		 * StringBuilder(querySqlString.trim().toLowerCase());
		 */
		StringBuilder sb = new StringBuilder(querySqlString.trim().replaceFirst("(?i)" + SELECT, SELECT)
				.replaceFirst("(?i)" + FROM, FROM).replaceFirst("(?i)" + DISTINCT, DISTINCT)
				.replaceFirst("(?i)" + ORDERBY, ORDERBY));

		int orderByIndex = sb.indexOf(ORDERBY);
		CharSequence orderby = orderByIndex > 0 ? sb.subSequence(orderByIndex, sb.length())
				: "ORDER BY CURRENT_TIMESTAMP";

		// Delete the order by clause at the end of the query
		if (orderByIndex > 0) {
			sb.delete(orderByIndex, orderByIndex + orderby.length());
		}

		// HHH-5715 bug fix
		// replaceDistinctWithGroupBy(sb);
		//
		// insertRowNumberFunction(sb, orderby);

		// Wrap the query within a with statement:
		sb.insert(0, "WITH query AS (").append(") SELECT * FROM query ");
		sb.append("WHERE __hibernate_row_nr__ BETWEEN ? AND ?");

		return sb.toString();
	}

}
