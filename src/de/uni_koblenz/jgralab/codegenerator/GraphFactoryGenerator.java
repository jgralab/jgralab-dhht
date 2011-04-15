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

import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class generates the code of the GraphElement Factory.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class GraphFactoryGenerator extends CodeGenerator {

	private final Schema schema;

	public GraphFactoryGenerator(Schema schema, String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(schemaPackageName, "", config);
		this.schema = schema;
		rootBlock.setVariable("className", schema.getName() + "Factory");
		rootBlock.setVariable("simpleClassName", schema.getName() + "Factory");
		rootBlock.setVariable("isClassOnly", "true");
	}

	@Override
	protected CodeBlock createHeader() {
		addImports("#jgImplPackage#.GraphFactoryImpl");
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public class #className# extends GraphFactoryImpl {");
		return code;
	}

	@Override
	protected CodeBlock createBody() {
		CodeList code = new CodeList();
		if (currentCycle.isClassOnly()) {
			code.add(createConstructor());
			code.add(createFillTableMethod());
		}
		return code;
	}

	protected CodeBlock createConstructor() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("className", schema.getName() + "Factory");
		code.add("public #className#() {");
		code.add("\tsuper();");
		code.add("\tfillTable();");
		code.add("}");
		return code;
	}

	protected CodeBlock createFillTableMethod() {
		CodeList code = new CodeList();
		CodeSnippet s = new CodeSnippet(true);
		s.add("protected void fillTable() { ");
		code.addNoIndent(s);

		GraphClass graphClass = schema.getGraphClass();
		code.add(createFillTableForGraph(graphClass));
		code.add(createFillTableForSubordinateGraph(graphClass));
		code.add(createFillTableForViewGraph(graphClass));
		for (VertexClass vertexClass : graphClass.getVertexClasses()) {
			code.add(createFillTableForVertex(vertexClass));
		}
		for (EdgeClass edgeClass : graphClass.getEdgeClasses()) {
			code.add(createFillTableForEdge(edgeClass));
			for (IncidenceClass incClass : edgeClass.getIncidenceClasses()) {
				code.add(createFillTableForIncidence(incClass));
			}
		}
		for (RecordDomain recordDomain : schema.getRecordDomains()) {
			code.add(createFillTableForRecord(recordDomain));
		}

		s = new CodeSnippet(true);
		s.add("}");
		code.addNoIndent(s);
		return code;
	}

	protected CodeBlock createFillTableForGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "."
				+ graphClass.getQualifiedName());
		code.setVariable("graphImplName", schemaRootPackageName + ".impl."+ graphClass.getQualifiedName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("setGraphImplementationClass(#graphName#.class, #graphImplName#Impl.class);");
		}
		return code;
	}
	
	protected CodeBlock createFillTableForSubordinateGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "." + graphClass.getQualifiedName());
		code.setVariable("graphImplName", schemaRootPackageName + ".impl."+ graphClass.getQualifiedName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("setSubordinateGraphImplementationClass(#graphName#.class, #graphImplName#SubordinateImpl.class);");
		}
		return code;
	}

	
	protected CodeBlock createFillTableForViewGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "." + graphClass.getQualifiedName());
		code.setVariable("graphImplName", schemaRootPackageName + ".impl."+ graphClass.getQualifiedName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("setViewGraphImplementationClass(#graphName#.class, #graphImplName#ViewImpl.class);");
		}
		return code;
	}


	protected CodeBlock createFillTableForVertex(VertexClass vertexClass) {
		if (vertexClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vertexName", schemaRootPackageName + "."	+ vertexClass.getQualifiedName());
		code.setVariable("vertexImplName", schemaRootPackageName + ".impl." 	+ vertexClass.getQualifiedName());

		if (!vertexClass.isAbstract()) {
			code.add("setVertexImplementationClass(#vertexName#.class, #vertexImplName#Impl.class);");
		}
		return code;
	}

	protected CodeBlock createFillTableForRecord(RecordDomain recordDomain) {

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("recordName", schemaRootPackageName + "."
				+ recordDomain.getQualifiedName());
		code.setVariable("recordImplName", schemaRootPackageName + ".impl."
				+ recordDomain.getQualifiedName());
		code.add("setRecordImplementationClass(#recordName#.class, #recordImplName#Impl.class);");
		return code;
	}

	protected CodeBlock createFillTableForEdge(EdgeClass edgeClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeName", schemaRootPackageName + "." + edgeClass.getQualifiedName());
		code.setVariable("edgeImplName", schemaRootPackageName + ".impl." + edgeClass.getQualifiedName());
		

		if (!edgeClass.isAbstract()) {
			code.add("setEdgeImplementationClass(#edgeName#.class, #edgeImplName#Impl.class);");
		}
		return code;
	}
	
	protected CodeBlock createFillTableForIncidence(IncidenceClass incClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("incName", schemaRootPackageName + "." + incClass.getQualifiedName());
		code.setVariable("incImplName", schemaRootPackageName + ".impl." + incClass.getQualifiedName());
		

		if (!incClass.isAbstract()) {
			code.add("setIncidenceImplementationClass(#incName#.class, #incImplName#Impl.class);");
		}
		return code;
	}
	
	
}
