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

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeList;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.SetDomain;

public final class SetDomainImpl extends CollectionDomainImpl implements
		SetDomain {

	SetDomainImpl(Schema schema, Domain aBaseDomain) {
		super(SETDOMAIN_NAME + "<"
				+ aBaseDomain.getTGTypeName(schema.getDefaultPackage()) + ">",
				schema.getDefaultPackage(), aBaseDomain);
	}

	@Override
	public Set<Domain> getAllComponentDomains() {
		HashSet<Domain> componentDomainSet = new HashSet<Domain>(1);
		componentDomainSet.add(baseDomain);
		return componentDomainSet;
	}

	@Override
	public String getJavaAttributeImplementationTypeName(
			String schemaRootPackagePrefix) {
		return "java.util." + SETDOMAIN_NAME + "<"
				+ baseDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
		// return "de.uni_koblenz.jgralab.impl.std.JGraLabSetImpl<"
		// + baseDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
	}

	@Override
	public String getJavaClassName(String schemaRootPackagePrefix) {
		// return "de.uni_koblenz.jgralab.impl.std.JGraLabSetImpl<"
		// + baseDomain.getJavaClassName(schemaRootPackagePrefix) + ">";
		return getJavaAttributeImplementationTypeName(schemaRootPackagePrefix);
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
		return SETDOMAIN_NAME + "<" + baseDomain.getTGTypeName(pkg) + ">";
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
		return "domain " + SETDOMAIN_NAME + "<" + baseDomain.toString() + ">";
	}

	private void internalGetReadMethod(CodeList code,
			String schemaRootPackagePrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		code.setVariable("name", variableName);
		code.setVariable("empty", SetDomain.EMPTY_SET);
		code.setVariable("basedom",
				getBaseDomain().getJavaClassName(schemaRootPackagePrefix));
		code.setVariable(
				"basetype",
				getBaseDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));
		code.setVariable("io", graphIoVariableName);

		code.addNoIndent(new CodeSnippet("#init#"));
		code.addNoIndent(new CodeSnippet("if (#io#.isNextToken(\"{\")) {"));
		code.add(new CodeSnippet(SETDOMAIN_TYPE
				+ "<#basedom#> $#name# = #empty#;"));
		code.add(new CodeSnippet("#io#.match(\"{\");",
				"while (!#io#.isNextToken(\"}\")) {"));
		if (getBaseDomain().isComposite()) {
			code.add(new CodeSnippet("\t#basetype# $#name#Element = null;"));
		} else {
			code.add(new CodeSnippet("\t#basetype# $#name#Element;"));
		}
		code.add(
				getBaseDomain().getReadMethod(schemaRootPackagePrefix,
						"$" + variableName + "Element", graphIoVariableName, ""), 1);
		code.add(new CodeSnippet("\t$#name# = $#name#.plus($#name#Element);",
				"}", "#io#.match(\"}\");", attributeContainer + "#name# = $#name#;"));
		code.addNoIndent(new CodeSnippet(
				"} else if (#io#.isNextToken(GraphIO.NULL_LITERAL)) {"));
		code.add(new CodeSnippet("#io#.match();", attributeContainer +  "#name# = null;"));
		code.addNoIndent(new CodeSnippet("} else {", "\t" + attributeContainer +  "#name# = null;", "}"));
	}


	private void internalGetWriteMethod(CodeList code,
			String schemaRootPackagePrefix, String variableName,
			String graphIoVariableName, String attributeContainer) {
		code.setVariable("basedom", getBaseDomain().getJavaClassName(
				schemaRootPackagePrefix));
		code.setVariable("basetype",
				getBaseDomain().getJavaAttributeImplementationTypeName(
						schemaRootPackagePrefix));
		code.setVariable("io", graphIoVariableName);
		code.setVariable("attributeContainer",attributeContainer);
		
		String element = variableName + "Element";
		element = element.replace('(', '_');
		element = element.replace(')', '_');
		code.setVariable("element", element);

		code.addNoIndent(new CodeSnippet("if (#attributeContainer##name# != null) {"));
		code.add(new CodeSnippet("#io#.writeSpace();", "#io#.write(\"{\");",
				"#io#.noSpace();", "for (#basetype# #element# : #attributeContainer##name#) {"));
		code.add(getBaseDomain().getWriteMethod(schemaRootPackagePrefix,
				code.getVariable("element"), graphIoVariableName,""), 1);
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
