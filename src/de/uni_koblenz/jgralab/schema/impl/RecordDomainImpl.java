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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.CompositeDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.exception.DuplicateRecordComponentException;
import de.uni_koblenz.jgralab.schema.exception.InvalidNameException;
import de.uni_koblenz.jgralab.schema.exception.NoSuchRecordComponentException;
import de.uni_koblenz.jgralab.schema.exception.RecordCycleException;
import de.uni_koblenz.jgralab.schema.exception.SchemaClassAccessException;
import de.uni_koblenz.jgralab.schema.exception.WrongSchemaException;
import de.uni_koblenz.jgralab.schema.impl.compilation.SchemaClassManager;

public final class RecordDomainImpl extends CompositeDomainImpl implements
		RecordDomain {

	/**
	 * The class object representing the generated interface for this
	 * AttributedElementClass
	 */
	private Class<? extends Object> m1Class;

	/**
	 * holds a list of the components of the record
	 */
	private final Map<String, RecordComponent> components = new TreeMap<String, RecordComponent>();

	/**
	 * @param qn
	 *            the unique name of the record in the schema
	 * @param components
	 *            a list of the components of the record
	 */
	RecordDomainImpl(String sn, Package pkg,
			Collection<RecordComponent> components) {
		super(sn, pkg);
		if (components != null) {
			for (RecordComponent c : components) {
				addComponent(c.getName(), c.getDomain());
			}
		}
	}

	@Override
	public void addComponent(String name, Domain domain) {
		if (name.isEmpty()) {
			throw new InvalidNameException(
					"Cannot create a record component with an empty name.");
		}
		if (components.containsKey(name)) {
			throw new DuplicateRecordComponentException(name,
					getQualifiedName());
		}
		if (parentPackage.getSchema().getDomain(domain.getQualifiedName()) != domain) {
			throw new WrongSchemaException(domain.getQualifiedName()
					+ " must be a domain of the schema "
					+ parentPackage.getSchema().getQualifiedName());
		}
		if (!staysAcyclicAfterAdding(domain)) {
			throw new RecordCycleException(
					"The creation of a component, which has the type " + domain
							+ ", would create a cycle of RecordDomains.");
		}
		RecordComponent c = new RecordComponent(name, domain);
		components.put(name, c);
	}

	@Override
	public Set<Domain> getAllComponentDomains() {
		Set<Domain> domains = new HashSet<Domain>();
		for (RecordComponent c : components.values()) {
			domains.add(c.getDomain());
		}
		return domains;
	}

	@Override
	public Collection<RecordComponent> getComponents() {
		return components.values();
	}

	@Override
	public Domain getDomainOfComponent(String name) {
		if (!components.containsKey(name)) {
			throw new NoSuchRecordComponentException(getQualifiedName(), name);
		}
		return components.get(name).getDomain();
	}

	@Override
	public String getJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return schemaRootPackagePrefix + "." + getQualifiedName();
	}

	@Override
	public String getJavaClassName(String schemaRootPackagePrefix) {
		return getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
		// return getJavaAttributeTypeName(schemaRootPackagePrefix);
	}

	public Class<? extends Object> getM1Class() {
		if (m1Class == null) {
			String m1ClassName = getSchema().getPackagePrefix() + "."
					+ getQualifiedName();
			try {
				m1Class = Class.forName(m1ClassName, true, SchemaClassManager
						.instance(getSchema().getQualifiedName()));
			} catch (ClassNotFoundException e) {
				throw new SchemaClassAccessException(
						"Can't load M1 class for AttributedElementClass '"
								+ getQualifiedName() + "'", e);
			}
		}
		return m1Class;
	}

	@Override
	public CodeBlock getReadMethod(String schemaPrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		CodeSnippet code = new CodeSnippet();
		code.setVariable("name", variableName);
		code.setVariable("init", "");
		internalGetReadMethod(code, schemaPrefix, variableName,
				graphIoVariableName, attributeContainer);

		return code;
	}

	@Override
	public String getTGTypeName(Package pkg) {
		return getQualifiedName(pkg);
	}

	@Override
	public CodeBlock getWriteMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName, String attributeContainer) {
		CodeSnippet code = new CodeSnippet();
		code.setVariable("name", variableName);
		internalGetWriteMethod(code, schemaRootPackagePrefix, variableName,
				graphIoVariableName, attributeContainer);

		return code;
	}

	/**
	 * @param d
	 *            the component domain which should be checked
	 * @return <code>true</code> if the addition of <code>d</code> wouldn't
	 *         create an inclusion cycle, <code>false</code> otherwise
	 */
	private boolean staysAcyclicAfterAdding(Domain d) {
		if (d == this) {
			return false;
		}
		if (!(d instanceof CompositeDomain)) {
			return true;
		}
		CompositeDomain c = (CompositeDomain) d;
		for (CompositeDomain comp : c.getAllComponentCompositeDomains()) {
			if (!staysAcyclicAfterAdding(comp)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder("Record " + getQualifiedName());
		String delim = " (";
		for (RecordComponent component : components.values()) {
			output.append(delim);
			output.append(component.getName());
			output.append('=');
			output.append(component.getDomain());
			delim = ", ";
		}
		output.append(")");
		return output.toString();
	}


	private void internalGetReadMethod(CodeSnippet code, String schemaPrefix,
			String variableName, String graphIoVariableName, String attributeContainer) {
		code.add("#init#");
		code.add("if (" + graphIoVariableName + ".isNextToken(\"(\")) {");
		code.add("\t" + attributeContainer + "#name# = new " + getSchema().getPackagePrefix() + "."
				+ getQualifiedName() + "(io);");
		code.add("} else if (" + graphIoVariableName
				+ ".isNextToken(GraphIO.NULL_LITERAL)) {");
		code.add("\t" + graphIoVariableName + ".match();");
		code.add("\t" +  attributeContainer + variableName + " = null;");
		code.add("} else {");
		code.add("\tthrow new GraphIOException(\"This is no record!\");");
		code.add("}");
	}

	private void internalGetWriteMethod(CodeSnippet code,
			String schemaRootPackagePrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		code.setVariable("attributeContainer", attributeContainer);
		code.add("if (#attributeContainer##name# != null) {");
		code.add("\t" + "#attributeContainer##name#.writeComponentValues(" + graphIoVariableName
				+ ");");
		code.add("} else {");
		code.add("\t" + graphIoVariableName
				+ ".writeIdentifier(GraphIO.NULL_LITERAL);");
		code.add("}");
	}


	@Override
	public String getInitialValue() {
		return "null";
	}

	@Override
	public Boolean hasComponent(String name) {
		return components.containsKey(name);
	}


}
