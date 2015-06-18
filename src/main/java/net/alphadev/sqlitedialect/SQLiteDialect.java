/*
 * The author disclaims copyright to this source code.  In place of
 * a legal notice, here is a blessing:
 *
 *    May you do good and not evil.
 *    May you find forgiveness for yourself and forgive others.
 *    May you share freely, never taking more than you give.
 *
 */
package net.alphadev.sqlitedialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class SQLiteDialect extends Dialect {

	public SQLiteDialect() {
		// https://www.sqlite.org/datatype3.html
		registerColumnType(Types.INTEGER, "INTEGER");
		registerColumnType(Types.TINYINT, "TINYINT");
		registerColumnType(Types.SMALLINT, "SMALLINT");
		registerColumnType(Types.BIGINT, "BIGINT");

		registerColumnType(Types.CHAR, "CHAR($l)");
		registerColumnType(Types.VARCHAR, "VARCHAR($l)");
		registerColumnType(Types.LONGVARCHAR, "longvarchar");
		registerColumnType(Types.CLOB, "clob");

		registerColumnType(Types.BINARY, "blob");
		registerColumnType(Types.LONGVARBINARY, "blob");
		registerColumnType(Types.VARBINARY, "blob");
		registerColumnType(Types.BLOB, "blob");

		registerColumnType(Types.REAL, "REAL");
		registerColumnType(Types.FLOAT, "FLOAT");
		registerColumnType(Types.DOUBLE, "DOUBLE");

		registerColumnType(Types.NUMERIC, "NUMERIC($p, $s)");
		registerColumnType(Types.BOOLEAN, "boolean");
		registerColumnType(Types.BIT, "BOOLEAN");
		registerColumnType(Types.DECIMAL, "DECIMAL");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TIMESTAMP, "datetime");

		registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
		registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
		registerFunction("quote", new StandardSQLFunction("quote", StandardBasicTypes.STRING));
		registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.INTEGER));
		registerFunction("round", new StandardSQLFunction("round"));
		registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
		registerFunction("trim", new SqliteTrimEmulationFunction());
	}

	@Override
	public boolean supportsIdentityColumns() {
		return true;
	}

	@Override
	public boolean hasDataTypeInIdentityColumn() {
		return false; // As specified in NHibernate dialect
	}

	@Override
	public String getIdentityColumnString() {
		return "INTEGER";
	}

	@Override
	public String getIdentitySelectString() {
		return "SELECT LAST_INSERT_ROWID()";
	}

	@Override
	public LimitHandler getLimitHandler() {
		return new AbstractLimitHandler() {
			@Override
			public String processSql(String sql, RowSelection selection) {
				return sql + (super.supportsLimitOffset() ? " LIMIT ? OFFSET ?" : " LIMIT ?");
			}

			@Override
			public boolean supportsLimit() {
				return true;
			}

			@Override
			public boolean bindLimitParametersInReverseOrder() {
				return true;
			}
		};
	}

	@Override
	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	@Override
	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	@Override
	public String getCurrentTimestampSelectString() {
		return "SELECT CURRENT_TIMESTAMP";
	}

	@Override
	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
		return new SqliteExceptionConversionDelegate();
	}

	@Override
	public boolean supportsUnionAll() {
		return true;
	}

	@Override
	public boolean hasAlterTable() {
		return false; // As specified in NHibernate dialect
	}

	@Override
	public boolean dropConstraints() {
		return false;
	}

	@Override
	public String getForUpdateString() {
		return "";
	}

	@Override
	public boolean supportsOuterJoinForUpdate() {
		return false;
	}

	@Override
	public String getDropForeignKeyString() {
		throw new UnsupportedOperationException(
				"No drop foreign key syntax supported by SQLiteDialect");
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName,
												   String[] foreignKey, String referencedTable,
												   String[] primaryKey,
												   boolean referencesPrimaryKey) {
		throw new UnsupportedOperationException(
				"No add foreign key syntax supported by SQLiteDialect");
	}

	@Override
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		throw new UnsupportedOperationException(
				"No add primary key syntax supported by SQLiteDialect");
	}

	@Override
	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}


	public boolean supportsCascadeDelete() {
		return true;
	}

	@Override
	public boolean supportsTupleDistinctCounts() {
		return false;
	}

	@Override
	public String getSelectGUIDString() {
		return "SELECT HEX(RANDOMBLOB(16))";
	}
}