/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.datastore.infinispanremote.query.parsing.impl;

import java.util.List;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.ast.spi.EntityNamesResolver;
import org.hibernate.ogm.query.parsing.impl.ParserPropertyHelper;
import org.hibernate.ogm.type.spi.GridType;
import org.hibernate.ogm.type.spi.TypeTranslator;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * Property helper dealing with Infinispan Remote.
 *
 * @author Fabio Massimo Ercoli
 */
public class InfinispanRemotePropertyHelper extends ParserPropertyHelper {

	private final SessionFactoryImplementor sessionFactory;

	public InfinispanRemotePropertyHelper(SessionFactoryImplementor sessionFactory, EntityNamesResolver entityNames) {
		super( sessionFactory, entityNames );
		this.sessionFactory = sessionFactory;
	}

	public String getColumnName(String entityType, List<String> propertyPath) {
		return getColumn( getPersister( entityType ), propertyPath );
	}

	@Override
	protected Type getPropertyType(String entityType, List<String> propertyPath) {
		Type propertyType = super.getPropertyType( entityType, propertyPath );
		if ( isElementCollection( propertyType ) ) {
			// For collection of elements we return the type of the collection
			return ( (CollectionType) propertyType ).getElementType( sessionFactory );
		}
		return propertyType;
	}

	@Override
	public Object convertToBackendType(String entityType, List<String> propertyPath, Object value) {
		if ( value instanceof InfinispanRemoteQueryParameter ) {
			return value;
		}

		Type propertyType = getPropertyType( entityType, propertyPath );
		if ( isElementCollection( propertyType ) ) {
			// For collection of elements we return the type of the collection
			propertyType = ( (CollectionType) propertyType ).getElementType( sessionFactory );
		}
		GridType ogmType = sessionFactory.getServiceRegistry().getService( TypeTranslator.class ).getType( propertyType );
		return ogmType.convertToBackendType( value, sessionFactory );
	}
}
