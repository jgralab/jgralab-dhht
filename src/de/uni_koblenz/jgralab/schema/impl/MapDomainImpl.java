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
/**
 *
 */
package de.uni_koblenz.jgralab.schema.impl;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeList;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * @author Tassilo Horn <horn@uni-koblenz.de>
 * 
 */
public final class MapDomainImpl extends CompositeDomainImpl implements
		MapDomain {
	/**
	 * The domain of this MapDomain's keys.
	 */
	private final Domain keyDomain;

	/**
	 * The domain of this MapDomain's values.
	 */
	private final Domain valueDomain;

	MapDomainImpl(Schema schema, Domain aKeyDomain, Domain aValueDomain) {
		super(MAPDOMAIN_NAME + "<"
				+ aKeyDomain.getTGTypeName(schema.getDefaultPackage()) + ", "
				+ aValueDomain.getTGTypeName(schema.getDefaultPackage()) + ">",
				schema.getDefaultPackage());

		if (parentPackage.getSchema().getDomain(aKeyDomain.getQualifiedName()) == null) {
			throw new SchemaException("Key domain '"
					+ aKeyDomain.getQualifiedName()
					+ "' not existent in schema "
					+ parentPackage.getSchema().getQualifiedName());
		}
		if (parentPackage.getSchema()
				.getDomain(aValueDomain.getQualifiedName()) == null) {
			throw new SchemaException("Value domain '"
					+ aValueDomain.getQualifiedName()
					+ "' not existent in schema "
					+ parentPackage.getSchema().getQualifiedName());
		}
		keyDomain = aKeyDomain;
		valueDomain = aValueDomain;
	}

	@Override
	public Set<Domain> getAllComponentDomains() {
		HashSet<Domain> allComponentDomains = new HashSet<Domain>(2);
		allComponentDomains.add(keyDomain);
		allComponentDomains.add(valueDomain);
		return allComponentDomains;
	}

	@Override
	public String getJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return "java.util." + MAPDOMAIN_NAME + "<"
				+ keyDomain.getJavaClassName(schemaRootPackagePrefix) + ", "
				+ valueDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
	}

	@Override
	public String getJavaClassName(String schemaRootPackagePrefix) {
		return getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
	}

	@Override
	public Domain getKeyDomain() {
		return keyDomain;
	}

	@Override
	public CodeBlock getReadMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName, String attributeContainer) {
		CodeList code = new CodeList();
		code.setVariable("init", "");
		internalGetReadMethod(code, schemaRootPackagePrefix, variableName,
				graphIoVariableName, attributeContainer);

		return code;
	}

	@Override
	public String getTGTypeName(Package pkg) {
		return MAPDOMAIN_NAME + "<" + keyDomain.getTGTypeName(pkg) + ", "
				+ valueDomain.getTGTypeName(pkg) + ">";
	}

	@Override
	public Domain getValueDomain() {
		return valueDomain;
	}

	@Override
	public CodeBlock getWriteMethod(String schemaRootPackagePrefix,
			String variableName, String graphIoVariableName, String attributeContainer) {
		CodeList code = new CodeList();
		code.setVariable("name", variableName);
		internalGetWriteMethod(code, schemaRootPackagePrefix, variableName,
				graphIoVariableName, attributeContainer);

		return code;

	}

	@Override
	public String toString() {
		return "domain " + MAPDOMAIN_NAME + "<" + keyDomain.toString() + ", "
				+ valueDomain.toString() + ">";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MapDomain) {
			MapDomain other = (MapDomain) o;
			if (!getSchema().getQualifiedName().equals(
					other.getSchema().getQualifiedName())) {
				return false;
			}
			if ((keyDomain == null) || (valueDomain == null)) {
				return false;
			}
			return keyDomain.equals(other.getKeyDomain())
					&& valueDomain.equals(other.getValueDomain());
		}
		return false;
	}

	private void internalGetReadMethod(CodeList code,
			String schemaRootPackagePrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		code.setVariable("name", variableName);
		code.setVariable("attributeContainer", attributeContainer);

		code.setVariable("keydom", getKeyDomain().getJavaClassName(
				schemaRootPackagePrefix));
		code.setVariable("keytype",
				getKeyDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));

		code.setVariable("valuedom", getValueDomain().getJavaClassName(
				schemaRootPackagePrefix));
		code.setVariable("valuetype",
				getValueDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));

		code.setVariable("io", graphIoVariableName);

		code.addNoIndent(new CodeSnippet("#init#"));
		code.addNoIndent(new CodeSnippet("if (#io#.isNextToken(\"{\")) {"));
		code.add(new CodeSnippet("#attributeContainer#name# = #theGraph#.createMap();"));
		code.add(new CodeSnippet("#io#.match(\"{\");",
				"while (!#io#.isNextToken(\"}\")) {"));

		if (getKeyDomain().isComposite()) {
			code.add(new CodeSnippet("\t#keytype# #name#Key = null;"));
		} else {
			code.add(new CodeSnippet("\t#keytype# #name#Key;"));
		}
		if (getValueDomain().isComposite()) {
			code.add(new CodeSnippet("\t\t#valuetype# #name#Value = null;"));
		} else {
			code.add(new CodeSnippet("\t\t#valuetype# #name#Value;"));
		}

		code.add(getKeyDomain().getReadMethod(schemaRootPackagePrefix,
				variableName + "Key", graphIoVariableName, ""), 1);
		code.add(new CodeSnippet("\t#io#.match(\"-\");"));
		code.add(getValueDomain().getReadMethod(schemaRootPackagePrefix,
				variableName + "Value", graphIoVariableName, ""), 1);
		code.add(new CodeSnippet("\t#attributeContainer##name#.put(#name#Key, #name#Value);", "}",
				"#io#.match(\"}\");"));
		code.addNoIndent(new CodeSnippet(
				"} else if (#io#.isNextToken(GraphIO.NULL_LITERAL)) {"));
		code.add(new CodeSnippet("#io#.match();", "#name# = null;"));
		code.addNoIndent(new CodeSnippet("}"));
	}

	private void internalGetWriteMethod(CodeList code,
			String schemaRootPackagePrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		code.setVariable("nameKey", "key");
		code.setVariable("nameValue", "value");

		code.setVariable("keytype",
				getKeyDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));

		code.setVariable("valuetype",
				getValueDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));

		code.setVariable("io", graphIoVariableName);
		code.setVariable("attributeContainer", attributeContainer);
		
		
		code.addNoIndent(new CodeSnippet("if (#name# != null) {"));
		code.add(new CodeSnippet("#io#.writeSpace();", "#io#.write(\"{\");",
				"#io#.noSpace();"));
		code.add(new CodeSnippet(
						"for (#keytype# #nameKey#: #attributeContainer##name#.keySet()) {"));

		code.add(new CodeSnippet(
				"#valuetype# #nameValue# = #attributeContainer##name#.get(#nameKey#);"), 1);
		code.add(getKeyDomain().getWriteMethod(schemaRootPackagePrefix,
				code.getVariable("nameKey"), graphIoVariableName, ""), 1);

		code.add(new CodeSnippet("\t#io#.write(\" -\");"));

		code.add(getValueDomain().getWriteMethod(schemaRootPackagePrefix,
				code.getVariable("nameValue"), graphIoVariableName, ""), 1);

		code.add(new CodeSnippet("}", "#io#.write(\"}\");", "#io#.space();"));
		code.addNoIndent(new CodeSnippet("} else {"));
		code.add(new CodeSnippet(graphIoVariableName
				+ ".writeIdentifier(GraphIO.NULL_LITERAL);"));
		code.addNoIndent(new CodeSnippet("}"));
	}


	@Override
	public String getInitialValue() {
		return "null";
	}
}
