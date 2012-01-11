package de.uni_koblenz.jgralab.schema.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.M1ClassManager;
import de.uni_koblenz.jgralab.TypedElement;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.TypedElementClass;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;
import de.uni_koblenz.jgralab.schema.exception.M1ClassAccessException;

public abstract class TypedElementClassImpl
	<ConcreteMetaClass extends TypedElementClass<ConcreteMetaClass, ConcreteInterface>,
	ConcreteInterface extends TypedElement<ConcreteMetaClass, ConcreteInterface>>
	extends NamedElementClassImpl implements TypedElementClass<ConcreteMetaClass, ConcreteInterface> {

	/**
	 * A set of {@link Constraint}s which can be used to validate the graph.
	 */
	protected HashSet<Constraint> constraints = new HashSet<Constraint>(1);
	/**
	 * the immediate sub classes of this class
	 */
	protected HashSet<ConcreteMetaClass> directSubClasses = new HashSet<ConcreteMetaClass>();
	/**
	 * the immediate super classes of this class
	 */
	protected HashSet<ConcreteMetaClass> directSuperClasses = new HashSet<ConcreteMetaClass>();
	/**
	 * defines the m2 element as abstract, i.e. that it may not have any
	 * instances
	 */
	private boolean isAbstract = false;
	/**
	 * The class object representing the generated interface for this
	 * AttributedElementClass
	 */
	private Class<? extends ConcreteInterface> m1Class;
	/**
	 * The class object representing the implementation class for this
	 * AttributedElementClass. This may be either the generated class or a
	 * subclass of this
	 */
	private Class<? extends ConcreteInterface> m1ImplementationClass;
	
	/**
	 * The id of this class unique in the schema
	 */
	private int id;
	

	public TypedElementClassImpl(String simpleName, Package pkg, Schema schema) {
		super(simpleName, pkg, schema);
	}

	@Override
	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}


	@Override
	public Set<ConcreteMetaClass> getAllSubClasses() {
		Set<ConcreteMetaClass> returnSet = new HashSet<ConcreteMetaClass>();
		for (ConcreteMetaClass subclass : directSubClasses) {
			returnSet.add(subclass);
			returnSet.addAll((Collection<? extends ConcreteMetaClass>) subclass.getAllSubClasses());
		}
		return returnSet;
	}

	@Override
	public Set<ConcreteMetaClass> getAllSuperClasses() {
		HashSet<ConcreteMetaClass> allSuperClasses = new HashSet<ConcreteMetaClass>();
		allSuperClasses.addAll(directSuperClasses);
		for (ConcreteMetaClass superClass : directSuperClasses) {
			allSuperClasses.addAll((Collection<? extends ConcreteMetaClass>) superClass.getAllSuperClasses());
		}
		return allSuperClasses;
	}

	@Override
	public Set<Constraint> getConstraints() {
		return constraints;
	}

	@Override
	public Set<ConcreteMetaClass> getDirectSubClasses() {
		return directSubClasses;
	}

	@Override
	public Set<ConcreteMetaClass> getDirectSuperClasses() {
		return new HashSet<ConcreteMetaClass>(directSuperClasses);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ConcreteInterface> getM1Class() {
		if (m1Class == null) {
			String m1ClassName = getSchema().getPackagePrefix() + "."
					+ getQualifiedName();
			try {
				m1Class = (Class<? extends ConcreteInterface>) Class
						.forName(m1ClassName, true, M1ClassManager
								.instance(getSchema().getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new M1ClassAccessException(
						"Can't load M1 class for AttributedElementClass '"
								+ getQualifiedName() + "'", e);
			}
		}
		return m1Class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ConcreteInterface> getM1ImplementationClass() {
		if (isAbstract()) {
			throw new M1ClassAccessException(
					"Can't get M1 implementation class. AttributedElementClass '"
							+ getQualifiedName() + "' is abstract!");
		}
		if (m1ImplementationClass == null) {
			try {
				Field f = getM1Class().getField("IMPLEMENTATION_CLASS");
				m1ImplementationClass = (Class<? extends ConcreteInterface>) f
						.get(m1Class);
			} catch (SecurityException e) {
				throw new M1ClassAccessException(e);
			} catch (NoSuchFieldException e) {
				throw new M1ClassAccessException(e);
			} catch (IllegalArgumentException e) {
				throw new M1ClassAccessException(e);
			} catch (IllegalAccessException e) {
				throw new M1ClassAccessException(e);
			}
		}
		return m1ImplementationClass;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isDirectSubClassOf(ConcreteMetaClass anAttributedElementClass) {
		return directSuperClasses.contains(anAttributedElementClass);
	}

	@Override
	public boolean isDirectSuperClassOf(ConcreteMetaClass anAttributedElementClass) {
		return (((ConcreteMetaClass) anAttributedElementClass).getDirectSuperClasses()
				.contains(this));
	}

	@Override
	public boolean isInternal() {
		Schema s = getSchema();
		TypedElementClass<?,?> t = this;
		return (   (t == s.getDefaultEdgeClass())
				|| (t == s.getDefaultGraphClass()) 
				|| (t == s.getDefaultBinaryEdgeClass()) 
				|| (t == s.getDefaultVertexClass())
				|| (t == s.getDefaultIncidenceClass(Direction.VERTEX_TO_EDGE) ) 
				|| (t == s.getDefaultIncidenceClass(Direction.EDGE_TO_VERTEX) ) );
	}

	@Override
	public boolean isSubClassOf(ConcreteMetaClass anAttributedElementClass) {
		return getAllSuperClasses().contains(anAttributedElementClass);
	}

	@Override
	public boolean isSuperClassOf(ConcreteMetaClass anAttributedElementClass) {
		return anAttributedElementClass.getAllSuperClasses().contains(this);
	}

	@Override
	public boolean isSuperClassOfOrEquals(ConcreteMetaClass anAttributedElementClass) {
		return ((this == anAttributedElementClass) || (isSuperClassOf(anAttributedElementClass)));
	}

	@Override
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	
	protected abstract void checkSpecialization(ConcreteMetaClass superclass);
	
	/**
	 * adds a superClass to this class
	 * 
	 * @param superClass
	 *            the class to add as superclass
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addSuperClass(ConcreteMetaClass superClass) {
		if ((superClass == this) || (superClass == null)) {
			return;
		}
		directSuperClasses.remove(getDefaultClass());

		if (((TypedElementClass<ConcreteMetaClass,?>)superClass).isSubClassOf((ConcreteMetaClass) this)) {
			for (ConcreteMetaClass mc : ((TypedElementClass<ConcreteMetaClass,?>)superClass).getAllSuperClasses()) {
				System.out.println(mc.getQualifiedName());
			}
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: "
							+ getQualifiedName() + " and "
							+ superClass.getQualifiedName());
		}
		directSuperClasses.add(superClass);
		((TypedElementClassImpl)superClass).directSubClasses.add(this);
	}
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		assert getSchema().getTypeForId(id) == this;
		this.id = id;
	}

}