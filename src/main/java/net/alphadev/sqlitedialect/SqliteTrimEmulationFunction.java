package net.alphadev.sqlitedialect;

import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class SqliteTrimEmulationFunction
		extends AbstractAnsiTrimEmulationFunction {

	@Override
	protected SQLFunction resolveBothSpaceTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"trim(?1)");
	}

	@Override
	protected SQLFunction resolveBothSpaceTrimFromFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"trim(?2)");
	}

	@Override
	protected SQLFunction resolveLeadingSpaceTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"ltrim(?1)");
	}

	@Override
	protected SQLFunction resolveTrailingSpaceTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"rtrim(?1)");
	}

	@Override
	protected SQLFunction resolveBothTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"trim(?1, ?2)");
	}

	@Override
	protected SQLFunction resolveLeadingTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"ltrim(?1, ?2)");
	}

	@Override
	protected SQLFunction resolveTrailingTrimFunction() {
		return new SQLFunctionTemplate(StandardBasicTypes.STRING,
				"rtrim(?1, ?2)");
	}
}
