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

package de.uni_koblenz.jgralab.codegenerator;

import java.util.TreeSet;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class EdgeCodeGenerator extends AttributedElementCodeGenerator<EdgeClass> {

	public EdgeCodeGenerator(EdgeClass edgeClass, String schemaPackageName,
			String implementationName, CodeGeneratorConfiguration config) {
		super(edgeClass, schemaPackageName, implementationName, config);
		rootBlock.setVariable("graphElementClass", "Edge");
	}

	@Override
	protected CodeBlock createHeader() {
		CodeList code = new CodeList();
		CodeSnippet snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add(" * Incomming IncidenceClasses:");
		code.addNoIndent(snippet);
		for (IncidenceClass ic : aec.getIncidenceClasses()) {
			if (ic.getDirection()==Direction.VERTEX_TO_EDGE)
			code.addNoIndent(createIncidenceClassCommentInHeader(ic));
		}	
		snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add(" * Outgoing IncidenceClasses:");
		code.addNoIndent(snippet);
		for (IncidenceClass ic : aec.getIncidenceClasses()) {
			if (ic.getDirection()==Direction.EDGE_TO_VERTEX)
			code.addNoIndent(createIncidenceClassCommentInHeader(ic));
		}	
		
		snippet = new CodeSnippet();
		snippet.add(" */");
		code.addNoIndent(snippet);
		code.addNoIndent(super.createHeader());
		return code;
	}
	

	private CodeSnippet createIncidenceClassCommentInHeader(IncidenceClass ic) {
		CodeSnippet snippet = new CodeSnippet();
		snippet.add(" *   Role: '#rolename#', VertexClass: '#vcName#");
		snippet.setVariable("rolename", ic.getRolename());
		snippet.setVariable("vcName", ic.getVertexClass().getQualifiedName());
		return snippet;
	}
	
	
	

	@Override
	protected CodeBlock createConstructor() {
		CodeList code = new CodeList();
		addImports("#jgPackage#.Vertex");
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(int id, #jgPackage#.Graph g, Vertex alpha, Vertex omega) {",
						"\tsuper(id, g, alpha, omega);"));
		if (hasDefaultAttributeValues()) {
			code.addNoIndent(new CodeSnippet(
					"\tinitializeAttributesWithDefaultValues();"));
		}
		code.add(createSpecialConstructorCode());
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	@Override
	protected CodeBlock createBody() {
		CodeList code = (CodeList) super.createBody();
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			rootBlock.setVariable("baseClassName", "EdgeImpl");
			if (currentCycle.isStdImpl()) {
				addImports("#jgImplStdPackage#.#baseClassName#");
			}
			if (currentCycle.isSaveMemImpl()) {
				addImports("#jgImplSaveMemPackage#.#baseClassName#");
			}
			if (currentCycle.isTransImpl()) {
				addImports("#jgImplTransPackage#.#baseClassName#");
			}
			if (currentCycle.isDbImpl()) {
				addImports("#jgImplDbPackage#.#baseClassName#");
			}
		}
		if (config.hasTypeSpecificMethodsSupport()
				&& !currentCycle.isClassOnly()) {
			code.add(createNextEdgeInGraphMethods());
			code.add(createNextEdgeAtVertexMethods());
		}
        createMethodsForBinaryEdge(code);
		// code.add(createValidRolesMethod());
		return code;
	}
	
	protected void createMethodsForBinaryEdge(CodeList code) {
		
	}


	private CodeBlock createNextEdgeInGraphMethods() {
		CodeList code = new CodeList();
		TreeSet<AttributedElementClass> superClasses = new TreeSet<AttributedElementClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		if (config.hasTypeSpecificMethodsSupport()) {
			for (AttributedElementClass ec : superClasses) {
				if (ec.isInternal()) {
					continue;
				}
				EdgeClass ecl = (EdgeClass) ec;
				code.addNoIndent(createNextEdgeInGraphMethod(ecl, false));
				if (config.hasMethodsForSubclassesSupport()) {
					if (!ecl.isAbstract()) {
						code
								.addNoIndent(createNextEdgeInGraphMethod(ecl,
										true));
					}
				}
			}
		}
		return code;
	}

	private CodeBlock createNextEdgeInGraphMethod(EdgeClass ec,
			boolean withTypeFlag) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("ecQualifiedName", schemaRootPackageName + "."
				+ ec.getQualifiedName());
		code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"
				: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses"
						: ""));

		if (currentCycle.isAbstract()) {
			code.add("/**",
							" * @return the next #ecQualifiedName# edge in the global edge sequence");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecQualifiedName# are accepted");
			}
			code.add(" */",
							"public #ecQualifiedName# getNext#ecCamelName#InGraph(#formalParams#);");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add("public #ecQualifiedName# getNext#ecCamelName#InGraph(#formalParams#) {",
					 "\treturn (#ecQualifiedName#)getNextEdge(#ecQualifiedName#.class#actualParams#);",
					 "}");
		}
		return code;
	}

	private CodeBlock createNextEdgeAtVertexMethods() {
		CodeList code = new CodeList();

		TreeSet<AttributedElementClass> superClasses = new TreeSet<AttributedElementClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		if (config.hasTypeSpecificMethodsSupport()) {
			for (AttributedElementClass ec : superClasses) {
				if (ec.isInternal()) {
					continue;
				}
				addImports("#jgPackage#.EdgeDirection");
				EdgeClass ecl = (EdgeClass) ec;
				code.addNoIndent(createNextEdgeAtVertexMethod(ecl, false,
								false));
				code.addNoIndent(createNextEdgeAtVertexMethod(ecl, true,
								false));
				if (config.hasMethodsForSubclassesSupport()) {
					if (!ecl.isAbstract()) {
						code.addNoIndent(createNextEdgeAtVertexMethod(ecl,
								false, true));
						code.addNoIndent(createNextEdgeAtVertexMethod(ecl,
								true, true));
					}
				}
			}
		}
		return code;
	}

	private CodeBlock createNextEdgeAtVertexMethod(EdgeClass ec,
			boolean withOrientation, boolean withTypeFlag) {

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("ecQualifiedName", schemaRootPackageName + "."
				+ ec.getQualifiedName());
		code.setVariable("ecCamelName", camelCase(ec.getUniqueName()));
		code.setVariable("formalParams",
				(withOrientation ? "EdgeDirection orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "boolean noSubClasses" : ""));
		code.setVariable("actualParams",
				(withOrientation || withTypeFlag ? ", " : "")
						+ (withOrientation ? "orientation" : "")
						+ (withOrientation && withTypeFlag ? ", " : "")
						+ (withTypeFlag ? "noSubClasses" : ""));
		if (currentCycle.isAbstract()) {
			code.add("/**",
							" * @return the next edge of class #ecQualifiedName# at the \"this\" vertex");

			if (withOrientation) {
				code.add(" * @param orientation the orientation of the edge");
			}
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecQualifiedName# are accepted");
			}
			code.add(" */",
							"public #ecQualifiedName# getNext#ecCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add("public #ecQualifiedName# getNext#ecCamelName#(#formalParams#) {",
					 "\treturn (#ecQualifiedName#)getNextIncidence(#ecQualifiedName#.class#actualParams#);",
					 "}");
		}
		return code;
	}

	
}
