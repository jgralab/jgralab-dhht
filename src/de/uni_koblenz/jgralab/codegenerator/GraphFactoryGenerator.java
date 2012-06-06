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
		code.setVariable("graphImplName", graphClass.getSimpleName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("setGraphImplementationClass_InMemoryStorage(#graphName#.class, #schemaMemImplPackage#.#graphImplName#Impl.class);");
			code.add("setGraphImplementationClass_DistributedStorage(#graphName#.class, #schemaDistributedImplPackage#.#graphImplName#Impl.class);");
			code.add("setGraphImplementationClass_DiskBasedStorage(#graphName#.class, #schemaDiskImplPackage#.#graphImplName#Impl.class);");
			code.add("setGraphImplementationClass_Diskv2BasedStorage(#graphName#.class, #schemaDiskv2ImplPackage#.#graphImplName#Impl.class);");
		}
		return code;
	}
	
	protected CodeBlock createFillTableForSubordinateGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "." + graphClass.getQualifiedName());
		code.setVariable("graphInMemImplName", schemaRootPackageName + ".impl.mem."+ graphClass.getQualifiedName());
		code.setVariable("graphDistributedImplName", schemaRootPackageName + ".impl.distributed."+ graphClass.getQualifiedName());
		code.setVariable("graphOnDiskImplName", schemaRootPackageName + ".impl.disk."+ graphClass.getQualifiedName());
		code.setVariable("graphOnDiskv2ImplName", schemaRootPackageName + ".impl.diskv2."+ graphClass.getQualifiedName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("/* TODO: Uncomment line 145 of GraphFactoryGenerator */");
			code.add("setSubordinateGraphImplementationClass_InMemoryStorage(#graphName#.class, #graphInMemImplName#SubordinateImpl.class); ");
			code.add("setSubordinateGraphImplementationClass_DistributedStorage(#graphName#.class, #graphDistributedImplName#SubordinateImpl.class); ");
			code.add("setSubordinateGraphImplementationClass_DiskBasedStorage(#graphName#.class, #graphOnDiskImplName#SubordinateImpl.class); ");
			code.add("setSubordinateGraphImplementationClass_Diskv2BasedStorage(#graphName#.class, #graphOnDiskv2ImplName#SubordinateImpl.class); ");
		}
		return code;
	}

	
	protected CodeBlock createFillTableForViewGraph(GraphClass graphClass) {
		if (graphClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("graphName", schemaRootPackageName + "." + graphClass.getQualifiedName());
		code.setVariable("graphInMemImplName", schemaRootPackageName + ".impl.mem."+ graphClass.getQualifiedName());
		code.setVariable("graphDistributedImplName", schemaRootPackageName + ".impl.distributed."+ graphClass.getQualifiedName());
		code.setVariable("graphOnDiskImplName", schemaRootPackageName + ".impl.disk."+ graphClass.getQualifiedName());
		code.setVariable("graphOnDiskv2ImplName", schemaRootPackageName + ".impl.diskv2."+ graphClass.getQualifiedName());
		if (!graphClass.isAbstract()) {
			code.add("/* code for graph #graphName# */");
			code.add("/* TODO: Uncomment line 162 of GraphFactoryGenerator */");
			code.add("setViewGraphImplementationClass_InMemoryStorage(#graphName#.class, #graphInMemImplName#ViewImpl.class);");
			code.add("setViewGraphImplementationClass_DistributedStorage(#graphName#.class, #graphDistributedImplName#ViewImpl.class);");
			code.add("setViewGraphImplementationClass_DiskBasedStorage(#graphName#.class, #graphOnDiskImplName#ViewImpl.class); ");
			code.add("setViewGraphImplementationClass_Diskv2BasedStorage(#graphName#.class, #graphOnDiskv2ImplName#ViewImpl.class); ");
		}
		return code;
	}


	protected CodeBlock createFillTableForVertex(VertexClass vertexClass) {
		if (vertexClass.isAbstract()) {
			return null;
		}

		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("vertexName", schemaRootPackageName + "."	+ vertexClass.getQualifiedName());
		code.setVariable("vertexMemImplName", schemaRootPackageName + ".impl.mem." 	+ vertexClass.getQualifiedName());
		code.setVariable("vertexDistributedImplName", schemaRootPackageName + ".impl.distributed." 	+ vertexClass.getQualifiedName());
		code.setVariable("vertexDiskImplName", schemaRootPackageName + ".impl.disk." 	+ vertexClass.getQualifiedName());
		code.setVariable("vertexDiskv2ImplName", schemaRootPackageName + ".impl.diskv2." 	+ vertexClass.getQualifiedName());
		if (!vertexClass.isAbstract()) {
			code.add("setVertexImplementationClass_InMemoryStorage(#vertexName#.class, #vertexMemImplName#Impl.class);");
			code.add("setVertexImplementationClass_DistributedStorage(#vertexName#.class, #vertexDistributedImplName#Impl.class);");
			code.add("setVertexProxyImplementationClass_DistributedStorage(#vertexName#.class, #vertexDistributedImplName#Proxy.class);");
			code.add("setVertexImplementationClass_DiskBasedStorage(#vertexName#.class, #vertexDiskImplName#Impl.class);");
			code.add("setVertexProxyImplementationClass_DiskBasedStorage(#vertexName#.class, #vertexDiskImplName#Proxy.class);");
			code.add("setVertexImplementationClass_Diskv2BasedStorage(#vertexName#.class, #vertexDiskv2ImplName#Impl.class);");
			code.add("setVertexProxyImplementationClass_Diskv2BasedStorage(#vertexName#.class, #vertexDiskv2ImplName#Proxy.class);");
		}
		return code;
	}


	protected CodeBlock createFillTableForEdge(EdgeClass edgeClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeName", schemaRootPackageName + "." + edgeClass.getQualifiedName());
		code.setVariable("edgeMemImplName", schemaRootPackageName + ".impl.mem." + edgeClass.getQualifiedName());
		code.setVariable("edgeDistributedImplName", schemaRootPackageName + ".impl.distributed." + edgeClass.getQualifiedName());
		code.setVariable("edgeDiskImplName", schemaRootPackageName + ".impl.disk." + edgeClass.getQualifiedName());
		code.setVariable("edgeDiskv2ImplName", schemaRootPackageName + ".impl.diskv2." + edgeClass.getQualifiedName());

		if (!edgeClass.isAbstract()) {
			code.add("setEdgeImplementationClass_InMemoryStorage(#edgeName#.class, #edgeMemImplName#Impl.class);");
			code.add("setEdgeImplementationClass_DistributedStorage(#edgeName#.class, #edgeDistributedImplName#Impl.class);");
			code.add("setEdgeProxyImplementationClass_DistributedStorage(#edgeName#.class, #edgeDistributedImplName#Proxy.class);");
			code.add("setEdgeImplementationClass_DiskBasedStorage(#edgeName#.class, #edgeDiskImplName#Impl.class);");
			code.add("setEdgeProxyImplementationClass_DiskBasedStorage(#edgeName#.class, #edgeDiskImplName#Proxy.class);");
			code.add("setEdgeImplementationClass_Diskv2BasedStorage(#edgeName#.class, #edgeDiskv2ImplName#Impl.class);");
			code.add("setEdgeProxyImplementationClass_Diskv2BasedStorage(#edgeName#.class, #edgeDiskv2ImplName#Proxy.class);");
		}
		return code;
	}
	
	protected CodeBlock createFillTableForIncidence(IncidenceClass incClass) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("incName", schemaRootPackageName + "." + incClass.getQualifiedName());
		code.setVariable("incMemImplName", schemaRootPackageName + ".impl.mem." + incClass.getQualifiedName());
		code.setVariable("incDistributedImplName", schemaRootPackageName + ".impl.distributed." + incClass.getQualifiedName());
		code.setVariable("incDiskImplName", schemaRootPackageName + ".impl.disk." + incClass.getQualifiedName());
		code.setVariable("incDiskv2ImplName", schemaRootPackageName + ".impl.diskv2." + incClass.getQualifiedName());

		if (!incClass.isAbstract()) {
			code.add("setIncidenceImplementationClass_InMemoryStorage(#incName#.class, #incMemImplName#Impl.class);");
			code.add("setIncidenceImplementationClass_DistributedStorage(#incName#.class, #incDistributedImplName#Impl.class);");
			code.add("setIncidenceProxyImplementationClass_DistributedStorage(#incName#.class, #incDistributedImplName#Proxy.class);");
			code.add("setIncidenceImplementationClass_DiskBasedStorage(#incName#.class, #incDiskImplName#Impl.class);");
			code.add("setIncidenceProxyImplementationClass_DiskBasedStorage(#incName#.class, #incDiskImplName#Proxy.class);");
			code.add("setIncidenceImplementationClass_Diskv2BasedStorage(#incName#.class, #incDiskv2ImplName#Impl.class);");
			code.add("setIncidenceProxyImplementationClass_Diskv2BasedStorage(#incName#.class, #incDiskv2ImplName#Proxy.class);");
		}
		return code;
	}
	
	
}
