/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tutorial.annotations;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Illustrates the use of Hibernate native APIs.  The code here is unchanged from the {@code basic} example, the
 * only difference being the use of annotations to supply the metadata instead of Hibernate mapping files.
 *
 * @author Steve Ebersole
 */
public class AnnotationsIllustrationTest {
	private SessionFactory sessionFactory;

	@Before
	public void setUp() {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
			.configure() // configures settings from hibernate.cfg.xml
			.build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
			// so destroy it manually.
			StandardServiceRegistryBuilder.destroy(registry);
			throw e;
		}
	}

	@After
	public void tearDown() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@Test
	public void testBasicUsage() {
		// create a couple of events...
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			session.save(new Event("Our very first event!", Timestamp.valueOf(LocalDateTime.now())));
			session.save(new Event("A follow up event", Timestamp.valueOf(LocalDateTime.now())));
			session.getTransaction().commit();
		}
		// now lets pull events from the database and list them
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			List<Event> result = session.createQuery("from Event", Event.class).list();
			for (Event event : result) {
				System.out.println("Event (" + event.getDate() + ") : " + event.getTitle());
			}
			session.getTransaction().commit();
		}
	}

	@Test
	public void uniqueConstraint() {
		// create a couple of events...
		try (Session session = sessionFactory.openSession()) {
			session.beginTransaction();
			final String title = "Dup";
			session.save(new Event(title, Timestamp.valueOf(LocalDateTime.now())));
			session.save(new Event(title, Timestamp.valueOf(LocalDateTime.now())));
			fail("UNIQUE constraint violation expected");
		} catch (ConstraintViolationException cve) {
			assertEquals("EVENTS.title", cve.getConstraintName());
		}
	}
}
