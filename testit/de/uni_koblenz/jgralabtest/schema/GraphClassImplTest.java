/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
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
package de.uni_koblenz.jgralabtest.schema;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public final class GraphClassImplTest extends AttributedElementClassImplTest {

	@Before
	@Override
	public void setUp() {
		super.setUp();
		attributedElement = graphClass;
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: An Exception must be thrown if you try to create a second
	 * GraphClass.
	 */
	@Test(expected = SchemaException.class)
	public void testCompareTo() {
		schema.createGraphClass("Z");
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing this element to another, where both element´s
	 * qualified names are equal
	 */
	@Test
	public void testCompareTo2() {
		Schema schema2 = new SchemaImpl("TestSchema2",
				"de.uni_koblenz.jgralabtest.schematest");
		GraphClass other = schema2.createGraphClass(graphClass.getSimpleName());

		testCompareTo3(other);
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing an element to itself
	 */
	@Test
	public void testCompareTo3() {
		testCompareTo3(graphClass);
	}

	/**
	 * getAllSubClasses()
	 * 
	 * TEST CASE: Getting all subclasses of an element that has no subclasses
	 */
	@Test
	public void testGetAllSubClasses4() {
		// A (virgin) GraphClass has no subclasses.
		testGetAllSubClasses(new Vector<AttributedElementClass>());
	}

	/**
	 * getAllSuperClasses()
	 * 
	 * TEST CASE: Getting all superclasses of an element that has no
	 * superclasses
	 */
	@Test
	public void testGetAllSuperClasses4() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		// Each element has a Default-Class as superclass upon creation.
		// GraphClass does not allow other superclasses.
		expectedSuperClasses.add(schema.getDefaultGraphClass());

		testGetAllSuperClasses(expectedSuperClasses);
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct subclasses of an element that has no direct
	 * subclasses.
	 */
	@Test
	public void testGetDirectSubClasses4() {
		// A (virgin) GraphClass has no subclasses.
		testGetDirectSubClasses(new Vector<AttributedElementClass>());
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct superclasses of an element that has no
	 * direct superclasses.
	 */
	@Test
	public void testGetDirectSuperClasses4() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		// Each element has a Default-Class as superclass upon creation.
		// GraphClass does not allow other superclasses.
		expectedSuperClasses.add(schema.getDefaultGraphClass());

		testGetDirectSuperClasses(expectedSuperClasses);
	}

	@Test
	public void testCreateAggregationClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testCreateCompositionClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testCreateEdgeClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testCreateVertexClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetAggregationClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetAggregationClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetCompositionClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetCompositionClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetEdgeClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetEdgeClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetGraphElementClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetGraphElementClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnAggregationClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnCompositionClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnEdgeClassCount() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnEdgeClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnGraphElementClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnVertexClassCount() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetOwnVertexClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetSchema() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetVariableName() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetVertexClass() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testGetVertexClasses() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testKnows() {
		// TODO Auto-generated method stub
	}

	@Test
	public void testKnowsOwn() {
		// TODO Auto-generated method stub
	}

	@Override
	@Test
	public void testToString() {
		// TODO Auto-generated method stub
	}
}
