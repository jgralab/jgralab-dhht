/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab;

import java.io.IOException;

import de.uni_koblenz.jgralab.schema.AttributedElementClass;

/**
 * Aggregates graphs, edges and vertices.
 * 
 * @author ist@uni-koblenz.de
 * 
 * @param ConcreteAttributeElementClass the non-abstract metaclass of this class, e.g. VertexClass or EdgeClass, used for generic implementations of methods such as addSuperclass
 * @param ConcreteInterface the non-abstract interface for the instances of this class, e.g. Vertex for a VertexClass
 * 
 */
public interface AttributedElement<ConcreteAttributedElementClass extends AttributedElementClass<ConcreteAttributedElementClass, ConcreteInterface>, ConcreteInterface extends AttributedElement<ConcreteAttributedElementClass, ConcreteInterface>> 
extends TypedElement<ConcreteAttributedElementClass, ConcreteInterface> {


	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException;

	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException;

	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException;

	public void readAttributeValues(GraphIO io) throws GraphIOException;

	/**
	 * Returns the value of the attribute <code>name</code> of this
	 * {@link AttributedElement}.
	 * 
	 * @param name
	 *            {@link String} the name of the requested attribute
	 * @return {@link Object} value of attribute <code>name</code>
	 * @throws NoSuchAttributeException
	 *             if the attribute <code>name</code> does not exist at this
	 *             {@link AttributedElement}
	 */
	public Object getAttribute(String name) throws NoSuchAttributeException;

	public void setAttribute(String name, Object data)
			throws NoSuchAttributeException;

	void initializeAttributesWithDefaultValues();
}
