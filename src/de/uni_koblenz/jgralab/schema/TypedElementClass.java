package de.uni_koblenz.jgralab.schema;

import java.util.Set;

import de.uni_koblenz.jgralab.TypedElement;
import de.uni_koblenz.jgralab.schema.exception.M1ClassAccessException;

public interface TypedElementClass
	<ConcreteMetaClass extends TypedElementClass<ConcreteMetaClass, ConcreteInterface>, 
	 ConcreteInterface extends TypedElement<ConcreteMetaClass, ConcreteInterface>> extends NamedElementClass {

	/**
	 * Adds a {@link Constraint} to this typed element. Constraints are
	 * greql2 predicates, that can be used to validate the graph.
	 * 
	 * <p>
	 * <b>Note:</b> Constraints are not inheritable.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.addConstraint(constr);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>attrElement'.getConstraints().size >= 0</code></li>
	 * <li>
	 * <code>attrElement'.getConstraints().size() == attrElement.getConstraints().size() + 1</code>
	 * , if for each constraint <code>c</code> of <code>attrElement</code> the
	 * following condition holds: <code>!constr.equals(c)</code></li>
	 * <li><code>attrElement'.getConstraints()</code> does not contain any
	 * inherited constraints from possible superclasses of
	 * <code>attrElement</code></li>
	 * </ul>
	 * </p>
	 * </p>
	 * 
	 * @param constraint
	 *            a {@link Constraint} to add to this element
	 */
	public void addConstraint(Constraint constraint);
	
	
	/**
	 * Returns all direct and indirect subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>subClasses = attrElement.getAllSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect subclasses of this element
	 */
	public Set<ConcreteMetaClass> getAllSubClasses();

	/**
	 * Lists all direct and indirect superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>typedElementClass</code> has a dedicated default superclass at
	 * the top of its inheritance hierarchy. Please consult the specifications
	 * of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getAllSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null </code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct and indirect superclasses (including the default superclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct and indirect superclasses of this element
	 */
	public Set<ConcreteMetaClass> getAllSuperClasses();
	
	/**
	 * Returns this element's Set of {@link Constraint}s.
	 * 
	 * <p>
	 * Constraints are greql2 predicates, that can be used to validate the
	 * graph. Constraints are bound to a specific typed element and are not
	 * inheritable.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>constrs = attrElement.getConstraints();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>constrs != null</code></li>
	 * <li><code>constrs.size() >= 0</code></li>
	 * <li><code>constrs</code> contains all of this element's constraints</li>
	 * <li><code>constrs</code> does not contain any inherited constraint</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all {@link Constraint}s of this typed element
	 */
	public Set<Constraint> getConstraints();

	/**
	 * Lists all direct subclasses of this element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>subClasses = attrElement.getDirectSubClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>subClasses != null</code></li>
	 * <li><code>subClasses.size() >= 0</code></li>
	 * <li><code>subClasses</code> holds all of <code>attrElement´s</code>
	 * direct subclasses</li>
	 * <li><code>subClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited subclasses</li>
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct subclasses of this element
	 */
	public Set<ConcreteMetaClass> getDirectSubClasses();

	/**
	 * Returns all direct superclasses of this element.
	 * 
	 * <p>
	 * <b>Note:</b> Each instance of a subclass of
	 * <code>typedElementClass</code> has one default direct superclass.
	 * Please consult the specifications of the used subclass for details.
	 * </p>
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code>superClasses = attrElement.getDirectSuperClasses();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b>
	 * <ul>
	 * <li><code>superClasses != null</code></li>
	 * <li><code>superClasses.size() >= 0</code></li>
	 * <li><code>superClasses</code> holds all of <code>attrElement´s</code>
	 * direct superclasses (including the default superclass)</li>
	 * <li><code>superClasses</code> does not hold any of
	 * <code>attrElement´s</code> inherited superclasses
	 * </ul>
	 * </p>
	 * 
	 * @return a Set of all direct superclasses of this element
	 */
	public Set<ConcreteMetaClass> getDirectSuperClasses();

	/**
	 * Returns the M1 interface class for this typed element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>m1Class = attrElement.getM1Class();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the M1 interface class for this element
	 * 
	 * @throws M1ClassAccessException
	 *             if reflection exceptions occur.
	 */
	public Class<ConcreteInterface> getM1Class();

	/**
	 * Returns the M1 implementation class for this typed element.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>m1ImplClass = attrElement.getM1Class();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> not yet defined
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> not yet defined
	 * </p>
	 * 
	 * @return the M1 implementation class for this element
	 * 
	 * @throws M1ClassAccessException
	 *             if:
	 *             <ul>
	 *             <li>this element is abstract</li>
	 *             <li>there are reflection exceptions</li>
	 *             </ul>
	 */
	public Class<? extends ConcreteInterface> getM1ImplementationClass();
	
	/**
	 * Retrieves the name used for elements of this TypedElementClass in
	 * files created by the code generator.
	 * 
	 * @return the variable name.
	 */
	public String getVariableName();
	
	/**
	 * States if this typed element is abstract. Abstract elements can´t
	 * have instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>isAbstract = attrElement.isAbstract();</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isAbstract</code> is:
	 * <ul>
	 * <li><code>true</code> if <code>attrElement</code> is abstract and
	 * therefore may not have any instances</li>
	 * <li>otherwise <code>false</code>
	 * </ul>
	 * 
	 * @return <code>true</code>, if the element is abstract , otherwise
	 *         <code>false</code>
	 */
	public boolean isAbstract();

	/**
	 * Checks if the current element is a direct subclass of another typed
	 * element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSubClass = attrElement.isDirectSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> typed element is a
	 * direct superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> typed
	 * element are the same</li>
	 * <li>the <code>other</code> typed element is not a direct superclass
	 * of <code>attrElement</code></li>
	 * <li>the <code>other</code> typed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param antypedElementClass
	 *            the possible superclass of this typed element
	 * @return <code>true</code> if <code>antypedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSubClassOf(ConcreteMetaClass antypedElementClass);

	/**
	 * Checks if the current element is a direct superclass of another
	 * typed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isDirectSuperClass = attrElement.isDirectSuperClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isDirectSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> typed element is a
	 * direct subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> typed
	 * element are the same</li>
	 * <li>the <code>other</code> typed element is not a direct subclass of
	 * <code>attrElement</code></li>
	 * <li>the <code>other</code> typed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param antypedElementClass
	 *            the possible subclass of this typed element
	 * @return <code>true</code> if <code>antypedElementClass</code> is a
	 *         direct subclass of this element, otherwise <code>false</code>
	 */
	public boolean isDirectSuperClassOf(ConcreteMetaClass antypedElementClass);

	/**
	 * @return true, if this typedElementClass is only for internal use
	 */
	public boolean isInternal();

	/**
	 * Checks if the current element is a direct or indirect subclass of another
	 * typed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSubClass = attrElement.isSubClassOf(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSubClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> typed element is a
	 * direct or inherited superclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> typed
	 * element are the same</li>
	 * <li>the <code>other</code> typed element is not a direct or
	 * inherited superclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> typed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param antypedElementClass
	 *            the possible superclass of this typed element
	 * @return <code>true</code> if <code>antypedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
	public boolean isSubClassOf(ConcreteMetaClass antypedElementClass);

	/**
	 * Checks if the current element is a direct or inherited superclass of
	 * another typed element.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClass = attrElement.isSuperClass(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClass</code> is:
	 * <ul>
	 * <li><code>true</code> if the <code>other</code> typed element is a
	 * direct or inherited subclass of this element</li>
	 * <li><code>false</code> if one of the following occurs:
	 * <ul>
	 * <li><code>attrElement</code> and the given <code>other</code> typed
	 * element are the same</li>
	 * <li>the <code>other</code> typed element is not a direct or indirect
	 * subclass of <code>attrElement</code></li>
	 * <li>the <code>other</code> typed element has no relation with
	 * <code>attrElement</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @param antypedElementClass
	 *            the possible subclass of this typed element
	 * @return <code>true</code> if <code>antypedElementClass</code> is a
	 *         direct or indirect subclass of this element, otherwise
	 *         <code>false</code>
	 */
	public boolean isSuperClassOf(ConcreteMetaClass antypedElementClass);

	/**
	 * Tests if the current element equals another typed element or is
	 * another attributes element´s direct or indirect superclass.
	 * 
	 * <p>
	 * <b>Pattern:</b>
	 * <code> isSuperClassOrEquals = attrElement.isSuperClassOfOrEquals(other);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>isSuperClassOrEquals</code> is:
	 * <ul>
	 * <li><code>true</code> if one of the following occurs:
	 * <ul>
	 * <li>the <code>other</code> typed element is a direct or indirect
	 * subclass of this element</li>
	 * <li><code>attrElement == other</code></li>
	 * </ul>
	 * </li>
	 * <li><code>false</code> if the <code>other</code> typed element has
	 * no relation with <code>attrElement</code> (not the same, not a direct or
	 * indirect subclass)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param antypedElementClass
	 *            the possible subclass of this typed element
	 * @return <code>true</code> if <code>antypedElementClass</code> is a
	 *         direct or indirect subclass of this element or <code>this</code>
	 *         typed element itself, otherwise <code>false</code>
	 */
	public boolean isSuperClassOfOrEquals(ConcreteMetaClass antypedElementClass);

	/**
	 * Defines if this typed element is abstract. Abstract elements can´t
	 * have instances.
	 * 
	 * <p>
	 * <b>Pattern:</b> <code>attrElement.setAbstract(value);</code>
	 * </p>
	 * 
	 * <p>
	 * <b>Preconditions:</b> none
	 * </p>
	 * 
	 * <p>
	 * <b>Postconditions:</b> <code>attrElement'</code> is abstract and no new
	 * instances can be created
	 * </p>
	 * 
	 * @param isAbstract
	 *            the new value defining the state of this typed element
	 */
	public void setAbstract(boolean isAbstract);
	
	/**
	 * 
	 * @return the default class of type ConcreteMetaClass of the schema
	 *         e.g., for an EdgeClass the DefaultEdgeClass is returned
	 */
	public ConcreteMetaClass getDefaultClass();
	
	
	/**
	 * Adds the given class as superclass to this element
	 * @param superClass
	 */
	public void addSuperClass(ConcreteMetaClass superClass);

	
	/**
	 * Returns the unique ID of this typed element class in the schema
	 * @return
	 */
	public int getId();
	
	/**
	 * Sets the unique ID of this typed element class in the schema,
	 * to be used only internally by the schema class
	 */
	public void setId(int id);
	
}
