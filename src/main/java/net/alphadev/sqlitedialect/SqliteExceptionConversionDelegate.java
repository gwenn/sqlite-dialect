package net.alphadev.sqlitedialect;

import org.hibernate.JDBCException;
import org.hibernate.exception.*;
import org.hibernate.exception.spi.*;
import org.hibernate.internal.util.JdbcExceptionHelper;

import java.sql.SQLException;

import static net.alphadev.sqlitedialect.ErrorCodes.*;
import static net.alphadev.sqlitedialect.ErrorCodes.SQLITE_NOTADB;

public class SqliteExceptionConversionDelegate
		implements SQLExceptionConversionDelegate {

	@Override
	public JDBCException convert(SQLException sqlException, String message, String sql) {
		switch (JdbcExceptionHelper.extractErrorCode(sqlException)) {
			case SQLITE_CONSTRAINT:
				final String constraintName = EXTRACTER.extractConstraintName(sqlException);
				return new ConstraintViolationException(message, sqlException, sql, constraintName);
			case SQLITE_TOOBIG:
			case SQLITE_MISMATCH:
				return new DataException(message, sqlException, sql);
			case SQLITE_BUSY:
			case SQLITE_LOCKED:
				return new LockAcquisitionException(message, sqlException, sql);
			case SQLITE_IOERR:

			case SQLITE_PROTOCOL:
			case SQLITE_NOTADB:
				return new JDBCConnectionException(message, sqlException,
						sql);
			default:
				return new GenericJDBCException(message, sqlException, sql);
		}
	}

	private static final ViolatedConstraintNameExtracter EXTRACTER =
			new TemplatedViolatedConstraintNameExtracter() {
				@Override
				public String extractConstraintName(SQLException sqle) {
					return extractUsingTemplate("constraint ", " failed", sqle.getMessage());
				}
			};
}
