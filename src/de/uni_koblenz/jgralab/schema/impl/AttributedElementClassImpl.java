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

package de.uni_koblenz.jgralab.schema.impl;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.DuplicateAttributeException;
import de.uni_koblenz.jgralab.schema.exception.InheritanceException;

public abstract class AttributedElementClassImpl<ConcreteMetaClass extends AttributedElementClass<ConcreteMetaClass, ConcreteInterface>, ConcreteInterface extends AttributedElement<ConcreteMetaClass, ConcreteInterface>> extends TypedElementClassImpl<ConcreteMetaClass, ConcreteInterface>
		implements AttributedElementClass<ConcreteMetaClass, ConcreteInterface> {

	/**
	 * a list of attributes which belongs to the m2 element
	 * (edgeclass/vertexclass/graphclass). Only the own attributes of this class
	 * are stored here, no inherited attributes
	 */
	private final TreeSet<Attribute> attributeList = new TreeSet<Attribute>();

	/**
	 * builds a new attributed element class
	 * 
	 * @param qn
	 *            the unique identifier of the element in the schema
	 */
	protected AttributedElementClassImpl(String simpleName, Package pkg,
			Schema schema) {
		super(simpleName, pkg, schema);
	}

	@Override
	public void addAttribute(Attribute anAttribute) {
		if (containsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(anAttribute.getName(),
					getQualifiedName());
		}
		// Check if a subclass already contains an attribute with that name. In
		// that case, it may not be added, too.
		if (subclassContainsAttribute(anAttribute.getName())) {
			throw new DuplicateAttributeException(
					"Duplicate Attribute '"
							+ anAttribute.getName()
							+ "' in AttributedElementClass '"
							+ getQualifiedName()
							+ "'. "
							+ "A derived AttributedElementClass already contains this Attribute.");
		}
		attributeList.add(anAttribute);
	}

	@Override
	public void addAttribute(String name, Domain domain,
			String defaultValueAsString) {
		addAttribute(new AttributeImpl(name, domain, this, defaultValueAsString));
	}
	
	/**
	 * adds a superClass to this class
	 * 
	 * @param superClass
	 *            the class to add as superclass
	 */
	@SuppressWarnings("unchecked")
	protected void addSuperClass(ConcreteMetaClass superClass) {
		if ((superClass == this) || (superClass == null)) {
			return;
		}
		directSuperClasses.remove(getSchema().getDefaultGraphClass());
		directSuperClasses.remove(getSchema().getDefaultEdgeClass());
		directSuperClasses.remove(getSchema().getDefaultVertexClass());
	
		for (Attribute a : superClass.getAttributeList()) {
			if (getOwnAttribute(a.getName()) != null) {
				throw new InheritanceException("Cannot add "
						+ superClass.getQualifiedName() + " as superclass of "
						+ getQualifiedName() + ", cause: Attribute "
						+ a.getName() + " is declared in both classes");
			}
		}
		if (superClass.isSubClassOf((ConcreteMetaClass) this)) {
			for (ConcreteMetaClass attr : superClass.getAllSuperClasses()) {
				System.out.println(attr.getQualifiedName());
			}
			throw new InheritanceException(
					"Cycle in class hierarchie for classes: "
							+ getQualifiedName() + " and "
							+ superClass.getQualifiedName());
		}
		directSuperClasses.add(superClass);
		((AttributedElementClassImpl)superClass).directSubClasses.add((AttributedElementClass)this);
	}


	/**
	 * @return a textual representation of all attributes the element holds
	 */
	protected String attributesToString() {
		StringBuilder output = new StringBuilder("\nSelf Attributes:\n");
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = it.next();
			output.append(a.toString() + "\n");
		}
		output.append("\nSelf + Inherited Attributes:\n");
		it = getAttributeList().iterator();
		while (it.hasNext()) {
			a = it.next();
			output.append(a.toString() + "\n");
		}
		return output.toString();
	}

	@Override
	public boolean containsAttribute(String name) {
		return (getAttribute(name) != null);
	}

	@Override
	public Attribute getAttribute(String name) {
		Attribute ownAttr = getOwnAttribute(name);
		if (ownAttr != null) {
			return ownAttr;
		}
		for (ConcreteMetaClass superClass : directSuperClasses) {
			Attribute inheritedAttr = superClass.getAttribute(name);
			if (inheritedAttr != null) {
				return inheritedAttr;
			}
		}
		return null;
	}

	@Override
	public int getAttributeCount() {
		int attrCount = getOwnAttributeCount();
		for (ConcreteMetaClass superClass : directSuperClasses) {
			attrCount += superClass.getAttributeCount();
		}
		return attrCount;
	}

	@Override
	public SortedSet<Attribute> getAttributeList() {
		TreeSet<Attribute> attrList = new TreeSet<Attribute>();
		attrList.addAll(attributeList);
		for (ConcreteMetaClass superClass : directSuperClasses) {
			attrList.addAll(superClass.getAttributeList());
		}
		return attrList;
	}

	@Override
	public Attribute getOwnAttribute(String name) {
		Iterator<Attribute> it = attributeList.iterator();
		Attribute a;
		while (it.hasNext()) {
			a = it.next();
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	@Override
	public int getOwnAttributeCount() {
		return attributeList.size();
	}

	@Override
	public SortedSet<Attribute> getOwnAttributeList() {
		return attributeList;
	}

	@Override
	public boolean hasAttributes() {
		return !getAttributeList().isEmpty();
	}

	@Override
	public boolean hasOwnAttributes() {
		return !attributeList.isEmpty();
	}

	protected boolean subclassContainsAttribute(String name) {
		for (ConcreteMetaClass subClass : getAllSubClasses()) {
			Attribute subclassAttr = subClass.getAttribute(name);
			if (subclassAttr != null) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() == o.getClass()) {
			ConcreteMetaClass other = (ConcreteMetaClass) o;
			if (!qualifiedName.equals(other.getQualifiedName())) {
				return false;
			}
			if (!getSchema().getQualifiedName().equals(
					other.getSchema().getQualifiedName())) {
				return false;
			}
			if (getAttributeCount() != other.getAttributeCount()) {
				return false;
			}
			for (Attribute attr : getAttributeList()) {
				if (!attr.equals(other.getAttribute(attr.getName()))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	


}
