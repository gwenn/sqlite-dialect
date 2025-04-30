package org.sqlite.hibernate.dialect;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.jboss.logging.Logger;

public class SQLiteMetadataBuilderInitializer implements MetadataBuilderInitializer {

	private static final Logger logger = Logger.getLogger(SQLiteMetadataBuilderInitializer.class);

	@Override
	public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
		DialectResolver dialectResolver = serviceRegistry.getService(DialectResolver.class);

		if (!(dialectResolver instanceof DialectResolverSet)) {
			logger.warnf("DialectResolver '%s' is not an instance of DialectResolverSet, not registering SQLiteDialect",
					dialectResolver);
			return;
		}

		((DialectResolverSet) dialectResolver).addResolver(resolver);
	}

	private static final SQLiteDialect dialect = new SQLiteDialect();

	private static final DialectResolver resolver = info -> {
		if (info.getDatabaseName().startsWith("SQLite"))
			return dialect;
		return null;
	};
}
