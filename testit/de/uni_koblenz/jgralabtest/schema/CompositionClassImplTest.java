package de.uni_koblenz.jgralabtest.schema;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.CompositionClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.schema.impl.SchemaImpl;

public final class CompositionClassImplTest extends AggregationClassImplTest {

	private CompositionClass compositionClass;
	private VertexClass compositionClassFromVertexClass,
			compositionClassToVertexClass;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		compositionClassFromVertexClass = graphClass
				.createVertexClass("CompositionClassFromVertexClass");
		compositionClassToVertexClass = graphClass
				.createVertexClass("CompositionClassToVertexClass");

		attributedElement = compositionClass = graphClass
				.createCompositionClass("CompositionClass1",
						compositionClassFromVertexClass, 0, 1,
						"CompositionClassFromRoleName", true,
						compositionClassToVertexClass, 1,
						(int) (Math.random() * 100) + 1,
						"CompositionClassToRoleName");
	}

	/**
	 * addAttribute(Attribute)
	 * 
	 * TEST CASE: Adding an attribute, already contained in a superclass of this
	 * element
	 */
	@Test
	@Override
	public void testAddAttribute4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testAddAttribute4(superClass);
	}

	/**
	 * addAttribute(Attribute)
	 * 
	 * TEST CASE: Adding an attribute, already contained in a subclass of this
	 * element
	 */
	@Test
	@Override
	public void testAddAttribute5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		testAddAttribute5(subClass);
	}

	/**
	 * addConstraint(Constraint)
	 * 
	 * TEST CASE: Adding a constraint, already contained in a superclass of this
	 * element
	 */
	@Test
	@Override
	public void testAddConstraint4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testAddConstraint4(superClass);
	}

	/**
	 * addConstraint(Constraint)
	 * 
	 * TEST CASE: Adding a constraint, already contained in a subclass of this
	 * element
	 */
	@Test
	@Override
	public void testAddConstraint5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		testAddConstraint5(subClass);
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing this element to another, where this element´s
	 * qualified name is lexicographically less than the other´s
	 */
	@Test
	@Override
	public void testCompareTo() {
		CompositionClass other = graphClass.createCompositionClass("Z",
				compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testCompareTo(other);
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing this element to another, where this element´s
	 * qualified name is lexicographically greater than the other´s
	 */
	@Test
	@Override
	public void testCompareTo2() {
		CompositionClass other = graphClass.createCompositionClass("A",
				compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testCompareTo2(other);
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing this element to another, where both element´s
	 * qualified names are equal
	 */
	@Test
	@Override
	public void testCompareTo3() {
		Schema schema2 = new SchemaImpl("TestSchema2",
				"de.uni_koblenz.jgralabtest.schematest");
		GraphClass graphClass2 = schema2.createGraphClass(graphClass
				.getSimpleName());
		VertexClass compositionClassFromVertexClass2 = graphClass2
				.createVertexClass("CompositionClassFromVertexClass");
		VertexClass compositionClassToVertexClass2 = graphClass2
				.createVertexClass("CompositionClassToVertexClass");
		CompositionClass other = graphClass2.createCompositionClass(
				compositionClass.getQualifiedName(),
				compositionClassFromVertexClass2, true,
				compositionClassToVertexClass2);

		testCompareTo3(other);
	}

	/**
	 * compareTo(AttributedElementClass)
	 * 
	 * TEST CASE: Comparing an element to itself
	 */
	@Test
	@Override
	public void testCompareTo4() {
		testCompareTo3(compositionClass);
	}

	/**
	 * containsAttribute(String)
	 * 
	 * TEST CASE: looking for an attribute, present in a superclass of this
	 * element
	 */
	@Test
	@Override
	public void testContainsAttribute3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testContainsAttribute3(superClass);
	}

	/**
	 * getAllSubClasses()
	 * 
	 * TEST CASE: Getting all subclasses of an element with one direct subclass
	 */
	@Test
	@Override
	public void testGetAllSubClasses() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);
		// expected names of subclasses of this element
		expectedSubClasses.add(subClass);

		testGetAllSubClasses(expectedSubClasses);
	}

	/**
	 * getAllSubClasses()
	 * 
	 * TEST CASE: Getting all subclasses of an element with multiple direct
	 * subclasses
	 */
	@Test
	@Override
	public void testGetAllSubClasses2() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(compositionClass);

		expectedSubClasses.add(subClass);
		expectedSubClasses.add(subClass2);

		testGetAllSubClasses(expectedSubClasses);
	}

	/**
	 * getAllSubClasses()
	 * 
	 * TEST CASE: Getting all subclasses of an element with multiple direct and
	 * indirect subclasses
	 */
	@Test
	@Override
	public void testGetAllSubClasses3() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(subClass);

		expectedSubClasses.add(subClass);
		expectedSubClasses.add(subClass2);

		testGetAllSubClasses(expectedSubClasses);
	}

	/**
	 * getAllSubClasses()
	 * 
	 * TEST CASE: Getting all subclasses of an element that has no subclasses
	 */
	@Test
	@Override
	public void testGetAllSubClasses4() {
		// no subclasses expected
		testGetAllSubClasses(new Vector<AttributedElementClass>());
	}

	/**
	 * getAllSuperClasses()
	 * 
	 * TEST CASE: Getting all superclasses of an element with one direct
	 * superclass
	 */
	@Test
	@Override
	public void testGetAllSuperClasses() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		expectedSuperClasses.add(schema.getDefaultEdgeClass());
		expectedSuperClasses.add(schema.getDefaultAggregationClass());
		expectedSuperClasses.add(schema.getDefaultCompositionClass());
		expectedSuperClasses.add(superClass);

		testGetAllSuperClasses(expectedSuperClasses);
	}

	/**
	 * getAllSuperClasses()
	 * 
	 * TEST CASE: Getting all superclasses of an element with multiple direct
	 * superclasses
	 */
	@Test
	@Override
	public void testGetAllSuperClasses2() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);
		compositionClass.addSuperClass(superClass2);

		expectedSuperClasses.add(schema.getDefaultEdgeClass());
		expectedSuperClasses.add(schema.getDefaultAggregationClass());
		expectedSuperClasses.add(schema.getDefaultCompositionClass());
		expectedSuperClasses.add(superClass);
		expectedSuperClasses.add(superClass2);

		testGetAllSuperClasses(expectedSuperClasses);
	}

	/**
	 * getAllSuperClasses()
	 * 
	 * TEST CASE: Getting all superclasses of an element with multiple direct
	 * and indirect superclasses
	 */
	@Test
	@Override
	public void testGetAllSuperClasses3() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);
		superClass.addSuperClass(superClass2);

		expectedSuperClasses.add(schema.getDefaultEdgeClass());
		expectedSuperClasses.add(schema.getDefaultAggregationClass());
		expectedSuperClasses.add(schema.getDefaultCompositionClass());
		expectedSuperClasses.add(superClass);
		expectedSuperClasses.add(superClass2);

		testGetAllSuperClasses(expectedSuperClasses);
	}

	/**
	 * getAllSuperClasses()
	 * 
	 * TEST CASE: Getting all superclasses of an element that has no
	 * superclasses
	 */
	@Test
	@Override
	public void testGetAllSuperClasses4() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		expectedSuperClasses.add(schema.getDefaultEdgeClass());
		expectedSuperClasses.add(schema.getDefaultAggregationClass());
		expectedSuperClasses.add(schema.getDefaultCompositionClass());

		testGetAllSuperClasses(expectedSuperClasses);
	}

	/**
	 * getAttribute()
	 * 
	 * TEST CASE: Getting an inherited attribute
	 */
	@Test
	@Override
	public void testGetAttribute2() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetAttribute2(superClass);
	}

	/**
	 * getAttribute()
	 * 
	 * TEST CASE: Trying to get an attribute present in a subclass of this
	 * element
	 */
	@Test
	@Override
	public void testGetAttribute5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		testGetAttribute5(subClass);
	}

	/**
	 * getAttributeCount()
	 * 
	 * TEST CASE: Getting the number of attributes of an element which has
	 * exactly only one inherited attribute and no direct attributes
	 */
	@Test
	@Override
	public void testGetAttributeCount2() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetAttributeCount2(superClass);
	}

	/**
	 * getAttributeCount()
	 * 
	 * TEST CASE: Getting the number of attributes of an element which has
	 * multiple direct and indirect attributes
	 */
	@Test
	@Override
	public void testGetAttributeCount3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetAttributeCount3(superClass);
	}

	/**
	 * getAttributeCount()
	 * 
	 * TEST CASE: Getting the number of attributes of an element which has no
	 * direct nor inherited attributes but whose subclass has attributes
	 */
	@Test
	@Override
	public void testGetAttributeCount5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		testGetAttributeCount5(subClass);
	}

	/**
	 * getAttributeList()
	 * 
	 * TEST CASE: Getting an element´s list of attributes, which has exactly one
	 * inherited attribute and no direct attributes
	 */
	@Test
	@Override
	public void testGetAttributeList2() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetAttributeList2(superClass);
	}

	/**
	 * getAttributeList()
	 * 
	 * TEST CASE: Getting an element´s list of attributes, which has mutliple
	 * direct and inherited attributes
	 */
	@Test
	@Override
	public void testGetAttributeList3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetAttributeList3(superClass);
	}

	/**
	 * getAttributeList()
	 * 
	 * TEST CASE: Getting an element´s list of attributes, which has no direct
	 * nor inherited attributes but whose subclass has attributes
	 */
	@Test
	@Override
	public void testGetAttributeList5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		testGetAttributeList5(subClass);
	}

	/**
	 * getConstraints()
	 * 
	 * TEST CASE: Getting an element´s list of constraints, that has a
	 * superclass with constraints
	 */
	@Override
	@Test
	public void testGetConstraints4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		testGetConstraints4(superClass);
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct subclasses of an element that has one
	 * direct subclass.
	 */
	@Test
	@Override
	public void testGetDirectSubClasses() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);

		expectedSubClasses.add(subClass);

		testGetDirectSubClasses(expectedSubClasses);
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct subclasses of an element that has multiple
	 * direct subclasses.
	 */
	@Test
	@Override
	public void testGetDirectSubClasses2() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(compositionClass);

		expectedSubClasses.add(subClass);
		expectedSubClasses.add(subClass2);

		testGetDirectSubClasses(expectedSubClasses);
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct subclasses of an element that has multiple
	 * direct and indirect subclasses.
	 */
	@Test
	@Override
	public void testGetDirectSubClasses3() {
		Vector<AttributedElementClass> expectedSubClasses = new Vector<AttributedElementClass>();

		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass); // Direct subclass
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass); // Indirect subclass

		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(subClass);

		expectedSubClasses.add(subClass);

		testGetDirectSubClasses(expectedSubClasses);
	}

	/**
	 * getDirectSubClasses()
	 * 
	 * TEST CASE: Getting all direct subclasses of an element that has no direct
	 * subclasses.
	 */
	@Test
	@Override
	public void testGetDirectSubClasses4() {
		testGetDirectSubClasses(new Vector<AttributedElementClass>());
	}

	/**
	 * getDirectSuperClasses()
	 * 
	 * TEST CASE: Getting all direct superclasses of an element that has one
	 * direct superclass.
	 */
	@Test
	@Override
	public void testGetDirectSuperClasses() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);

		expectedSuperClasses.add(superClass);
		expectedSuperClasses.add(schema.getDefaultCompositionClass());

		testGetDirectSuperClasses(expectedSuperClasses);
	}

	/**
	 * getDirectSuperClasses()
	 * 
	 * TEST CASE: Getting all direct superclasses of an element that has
	 * multiple direct superclasses.
	 */
	@Test
	@Override
	public void testGetDirectSuperClasses2() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);

		compositionClass.addSuperClass(superClass);
		compositionClass.addSuperClass(superClass2);

		expectedSuperClasses.add(superClass);
		expectedSuperClasses.add(superClass2);

		testGetDirectSuperClasses(expectedSuperClasses);
	}

	/**
	 * getDirectSuperClasses()
	 * 
	 * TEST CASE: Getting all direct superclasses of an element that has
	 * multiple direct and indirect superclasses.
	 */
	@Test
	@Override
	public void testGetDirectSuperClasses3() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass); // Direct superclass
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass); // Indirect superclass

		compositionClass.addSuperClass(superClass);
		superClass.addSuperClass(superClass2);

		expectedSuperClasses.add(superClass);
		expectedSuperClasses.add(schema.getDefaultCompositionClass());

		testGetDirectSuperClasses(expectedSuperClasses);
	}

	/**
	 * getDirectSuperClasses()
	 * 
	 * TEST CASE: Getting all direct superclasses of an element that has no
	 * direct superclasses.
	 */
	@Test
	@Override
	public void testGetDirectSuperClasses4() {
		Vector<AttributedElementClass> expectedSuperClasses = new Vector<AttributedElementClass>();

		expectedSuperClasses.add(schema.getDefaultCompositionClass());

		testGetDirectSuperClasses(expectedSuperClasses);
	}

	/**
	 * getOwnAttribute()
	 * 
	 * TEST CASE: Trying to get an attribute present in a superclass of this
	 * element
	 */
	@Test
	@Override
	public void testGetOwnAttribute4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testGetOwnAttribute4(superClass);
	}

	/**
	 * getOwnAttribute()
	 * 
	 * TEST CASE: Trying to get an attribute present in a subclass of this
	 * element
	 */
	@Test
	@Override
	public void testGetOwnAttribute5() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testGetOwnAttribute4(subClass);
	}

	/**
	 * getOwnAttributeCount()
	 * 
	 * TEST CASE: Getting the number of attributes of an element that only has
	 * inherited attributes and no direct attributes
	 */
	@Test
	@Override
	public void testGetOwnAttributeCount4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testGetOwnAttributeCount4(superClass);
	}

	/**
	 * getOwnAttributeList()
	 * 
	 * TEST CASE: Getting an element´s list of attributes, that only has
	 * inherited attributes and no direct attributes
	 */
	@Test
	@Override
	public void testGetOwnAttributeList4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testGetOwnAttributeList4(superClass);
	}

	/**
	 * hasAttributes()
	 * 
	 * TEST CASE: The element has one inherited attribute
	 */
	@Test
	@Override
	public void testHasAttributes3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testHasAttributes3(superClass);
	}

	/**
	 * hasAttributes()
	 * 
	 * TEST CASE: The element has multiple inherited attributes
	 */
	@Test
	@Override
	public void testHasAttributes4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testHasAttributes4(superClass);
	}

	/**
	 * hasAttributes()
	 * 
	 * TEST CASE: The element has multiple direct and indirect attributes
	 */
	@Test
	@Override
	public void testHasAttributes5() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testHasAttributes5(superClass);
	}

	/**
	 * hasOwnAttributes()
	 * 
	 * TEST CASE: The element has direct and inherited attributes
	 */
	@Test
	@Override
	public void testHasOwnAttributes4() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testHasOwnAttributes4(superClass);
	}

	/**
	 * hasOwnAttributes()
	 * 
	 * TEST CASE: The element has no direct but indirect attributes
	 */
	@Test
	@Override
	public void testHasOwnAttributes5() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testHasOwnAttributes5(superClass);
	}

	/**
	 * isDirectSubClassOf()
	 * 
	 * TEST CASE: The other element is a direct superclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSubClassOf() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testIsDirectSubClassOf(superClass);
	}

	/**
	 * isDirectSubClassOf()
	 * 
	 * TEST CASE: The other element is an inherited superclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSubClassOf2() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);
		superClass.addSuperClass(superClass2);

		testIsDirectSubClassOf2(superClass2);
	}

	/**
	 * isDirectSubClassOf()
	 * 
	 * TEST CASE: The other element is a subclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSubClassOf3() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testIsDirectSubClassOf2(subClass);
	}

	/**
	 * isDirectSubClassOf()
	 * 
	 * TEST CASE: The other element has no relation with this element
	 */
	@Test
	@Override
	public void testIsDirectSubClassOf4() {
		CompositionClass compositionClass2 = graphClass.createCompositionClass(
				"CompositionClass2", compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testIsDirectSubClassOf2(compositionClass2);
	}

	/**
	 * isDirectSubClassOf()
	 * 
	 * TEST CASE: The other element and this element are the same
	 */
	@Test
	@Override
	public void testIsDirectSubClassOf5() {
		testIsDirectSubClassOf2(compositionClass);
	}

	/**
	 * isDirectSuperClassOf()
	 * 
	 * TEST CASE: The other element is a direct subclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSuperClassOf() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testIsDirectSuperClassOf(subClass);
	}

	/**
	 * isDirectSuperClassOf()
	 * 
	 * TEST CASE: The other element is an inherited subclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSuperClassOf2() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(subClass);

		testIsDirectSuperClassOf2(subClass2);
	}

	/**
	 * isDirectSuperClassOf()
	 * 
	 * TEST CASE: The other element is a superclass of this element
	 */
	@Test
	@Override
	public void testIsDirectSuperClassOf3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testIsDirectSuperClassOf2(superClass);
	}

	/**
	 * isDirectSuperClassOf()
	 * 
	 * TEST CASE: The other element has no relation with this element
	 */
	@Test
	@Override
	public void testIsDirectSuperClassOf4() {
		CompositionClass compositionClass2 = graphClass.createCompositionClass(
				"CompositionClass2", compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testIsDirectSuperClassOf2(compositionClass2);
	}

	/**
	 * isDirectSuperClassOf()
	 * 
	 * TEST CASE: The other element and this element are the same
	 */
	@Test
	@Override
	public void testIsDirectSuperClassOf5() {
		testIsDirectSuperClassOf2(compositionClass);
	}

	/**
	 * isSubClassOf()
	 * 
	 * TEST CASE: The other element is a direct superclass of this element
	 */
	@Test
	@Override
	public void testIsSubClassOf() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testIsSubClassOf(superClass);
	}

	/**
	 * isSubClassOf()
	 * 
	 * TEST CASE: The other element is an inherited superclass of this element
	 */
	@Test
	@Override
	public void testIsSubClassOf2() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass superClass2 = graphClass.createCompositionClass(
				"CompositionClassSuperClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);
		superClass.addSuperClass(superClass2);

		testIsSubClassOf(superClass2);
	}

	/**
	 * isSubClassOf()
	 * 
	 * TEST CASE: The other element is a subclass of this element
	 */
	@Test
	@Override
	public void testIsSubClassOf3() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testIsSubClassOf2(subClass);
	}

	/**
	 * isSubClassOf()
	 * 
	 * TEST CASE: The other element has no relation with this element
	 */
	@Test
	@Override
	public void testIsSubClassOf4() {
		CompositionClass compositionClass2 = graphClass.createCompositionClass(
				"CompositionClass2", compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testIsSubClassOf2(compositionClass2);
	}

	/**
	 * isSubClassOf()
	 * 
	 * TEST CASE: The other element and this element are the same
	 */
	@Test
	@Override
	public void testIsSubClassOf5() {
		testIsSubClassOf2(compositionClass);
	}

	/**
	 * isSuperClassOf()
	 * 
	 * TEST CASE: The other element is a direct subclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOf() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testIsSuperClassOf(subClass);
	}

	/**
	 * isSuperClassOf()
	 * 
	 * TEST CASE: The other element is an inherited subclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOf2() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(subClass);

		testIsSuperClassOf(subClass2);
	}

	/**
	 * isSuperClassOf()
	 * 
	 * TEST CASE: The other element is a superclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOf3() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testIsSuperClassOf2(superClass);
	}

	/**
	 * isSuperClassOf()
	 * 
	 * TEST CASE: The other element has no relation with this element
	 */
	@Test
	@Override
	public void testIsSuperClassOf4() {
		CompositionClass compositionClass2 = graphClass.createCompositionClass(
				"CompositionClass2", compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testIsSuperClassOf2(compositionClass2);
	}

	/**
	 * isSuperClassOf()
	 * 
	 * TEST CASE: The other element and this element are the same
	 */
	@Test
	@Override
	public void testIsSuperClassOf5() {
		testIsSuperClassOf2(compositionClass);
	}

	/**
	 * isSuperClassOfOrEquals()
	 * 
	 * TEST CASE: The other element is a direct subclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOfOrEquals() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);

		testIsSuperClassOfOrEquals(subClass);
	}

	/**
	 * isSuperClassOfOrEquals()
	 * 
	 * TEST CASE: The other element is an inherited subclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOfOrEquals2() {
		CompositionClass subClass = graphClass.createCompositionClass(
				"CompositionClassSubClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		CompositionClass subClass2 = graphClass.createCompositionClass(
				"CompositionClassSubClass2", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		subClass.addSuperClass(compositionClass);
		subClass2.addSuperClass(subClass);

		testIsSuperClassOfOrEquals(subClass2);
	}

	/**
	 * isSuperClassOfOrEquals()
	 * 
	 * TEST CASE: The other element and this element are the same
	 */
	@Test
	@Override
	public void testIsSuperClassOfOrEquals3() {
		testIsSuperClassOfOrEquals(compositionClass);
	}

	/**
	 * isSuperClassOfOrEquals()
	 * 
	 * TEST CASE: The other element has no relation with this element
	 */
	@Test
	@Override
	public void testIsSuperClassOfOrEquals4() {
		CompositionClass compositionClass2 = graphClass.createCompositionClass(
				"CompositionClass2", compositionClassFromVertexClass, true,
				compositionClassToVertexClass);

		testIsSuperClassOfOrEquals2(compositionClass2);
	}

	/**
	 * isSuperClassOfOrEquals()
	 * 
	 * TEST CASE: The other element is a superclass of this element
	 */
	@Test
	@Override
	public void testIsSuperClassOfOrEquals5() {
		CompositionClass superClass = graphClass.createCompositionClass(
				"CompositionClassSuperClass", compositionClassFromVertexClass,
				true, compositionClassToVertexClass);
		compositionClass.addSuperClass(superClass);

		testIsSuperClassOfOrEquals2(superClass);
	}
}