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

import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * This class is used by the method Schema.commit() to generate the Java-classes
 * that implement the VertexClasses of a graph schema.
 * 
 * @author ist@uni-koblenz.de
 */
public class VertexCodeGenerator extends GraphElementCodeGenerator<VertexClass> {

	public VertexCodeGenerator(VertexClass vertexClass,
			String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(vertexClass, schemaPackageName, config, true);
		rootBlock.setVariable("baseClassName", "VertexImpl");
		rootBlock.setVariable("proxyClassName", "VertexProxy");
		rootBlock.setVariable("graphElementClass", "Vertex");
		rootBlock.setVariable("edgeOrVertex", "Vertex");
	}



	@Override
	protected CodeBlock createConstructor() {
		CodeList code = (CodeList) super.createConstructor();
		code.setVariable("implOrProxy", currentCycle.isImplementationVariant() ? "Impl" : "Proxy");
		if (currentCycle.isProxies())
			addImports("#jgDiskImplPackage#.VertexProxy");
		if (currentCycle.isDiskbasedImpl()) {
			code.addNoIndent(new CodeSnippet("/** Constructor only to be used by Background-Storage backend */"));
			code.addNoIndent(new CodeSnippet(
					true,
					"public #simpleClassName##implOrProxy#(long id, #jgDiskImplPackage#.GraphDatabaseBaseImpl g, #jgDiskImplPackage#.VertexContainer container) throws java.io.IOException {",
					"\tsuper(id, g, container);" +
					"}"));
		}
		return code;
	}
	

	protected CodeBlock createLoadAttributeContainer() {
		if (currentCycle.isProxies())
			return null;
		addImports("#jgDiskImplPackage#.GraphDatabaseBaseImpl");
		return new CodeSnippet(
				true,
				"protected InnerAttributeContainer loadAttributeContainer() {",
				"\treturn (InnerAttributeContainer) container.backgroundStorage.getVertexAttributeContainer(GraphDatabaseBaseImpl.convertToLocalId(elementId));",
				"}"
		);
	}




//	/**
//	 * Creates <code>getEdgeNameIncidences</code> methods.
//	 * 
//	 * @param createClass
//	 *            if set to true, also the method bodies will be created
//	 * @return the CodeBlock that contains the code for the
//	 *         getEdgeNameIncidences-methods
//	 */
//	private CodeBlock createIncidenceIteratorMethods() {
//		VertexClass vc = (VertexClass) aec;
//		CodeList code = new CodeList();
//		Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
//		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//	//		edgeClassSet.addAll(vc.getConnectedEdgeClasses());
//		}
//		if (currentCycle.isAbstract()) {
//		//	edgeClassSet.addAll(vc.getOwnConnectedEdgeClasses());
//			// if the current class is a direct subclass of vertex, all edges
//			// defined in the schema to start or end at a vertex
//			// need also to be considered in generation
//			if (vc.getAllSuperClasses().size() == 1) {
//			//	for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
//					VertexClass dvc = vc.getGraphClass().getSchema()
//							.getDefaultVertexClass();
//			//		if ((ec.getTo().getVertexClass() == dvc)
//			//				|| (ec.getFrom().getVertexClass() == dvc)) {
//						edgeClassSet.add(ec);
//					}
//				}
//			}
//		}
//
//		for (EdgeClass ec : edgeClassSet) {
//			if (ec.isInternal()) {
//				continue;
//			}
//
//			if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//				addImports("#jgImplPackage#.IncidenceIterable");
//			}
//
//			CodeSnippet s = new CodeSnippet(true);
//			code.addNoIndent(s);
//
//			String targetClassName = schemaRootPackageName + "."
//					+ ec.getQualifiedName();
//			s.setVariable("edgeClassSimpleName", ec.getSimpleName());
//			s.setVariable("edgeClassQualifiedName", targetClassName);
//			s.setVariable("edgeClassUniqueName", ec.getUniqueName());
//
//			// getFooIncidences()
//			if (currentCycle.isAbstract()) {
//				s.add("/**");
//				s
//						.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName# or subtypes.");
//				s.add(" */");
//				s
//						.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences();");
//			}
//			if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//				s.add("@Override");
//				s
//						.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences() {");
//				s
//						.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class);");
//				s.add("}");
//			}
//			s.add("");
//			// getFooIncidences(boolean nosubclasses)
//			if (config.hasMethodsForSubclassesSupport()) {
//				if (currentCycle.isAbstract()) {
//					s.add("/**");
//					s
//							.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
//					s
//							.add(" * @param noSubClasses toggles wether subclasses of #edgeClassName# should be excluded");
//					s.add(" */");
//					s
//							.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(boolean noSubClasses);");
//				}
//				if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//					s.add("@Override");
//					s
//							.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(boolean noSubClasses) {");
//					s
//							.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, noSubClasses);");
//					s.add("}\n");
//				}
//			}
//			// getFooIncidences(EdgeDirection direction, boolean nosubclasses)
//			if (config.hasMethodsForSubclassesSupport()) {
//				if (currentCycle.isAbstract()) {
//					s.add("/**");
//					s
//							.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
//					s
//							.add(" * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the Iterable");
//					s
//							.add(" * @param noSubClasses toggles wether subclasses of #edgeClassName# should be excluded");
//					s.add(" */");
//					s
//							.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction, boolean noSubClasses);");
//				}
//				if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//					s.add("@Override");
//					s
//							.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction, boolean noSubClasses) {");
//					s
//							.add("\treturn  new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, direction, noSubClasses);");
//					s.add("}");
//				}
//			}
//			s.add("");
//			// getFooIncidences(EdgeDirection direction)
//			if (currentCycle.isAbstract()) {
//				s.add("/**");
//				s
//						.add(" * Returns an Iterable for all incidence edges of this vertex that are of type #edgeClassSimpleName#.");
//				s
//						.add(" * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the Iterable");
//				s.add(" */");
//				s
//						.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction);");
//			}
//			if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
//				s.add("@Override");
//				s
//						.add("public Iterable<#edgeClassQualifiedName#> get#edgeClassUniqueName#Incidences(EdgeDirection direction) {");
//				s
//						.add("\treturn new IncidenceIterable<#edgeClassQualifiedName#>(this, #edgeClassQualifiedName#.class, direction);");
//				s.add("}");
//			}
//		}
//		return code;
//	}
//
//	/**
//	 * creates the sets of valid in and valid out edges
//	 */
//	private CodeBlock createValidEdgeSets(VertexClass vc) {
//		addImports("java.util.Set");
//		addImports("java.util.HashSet");
//		addImports("#jgPackage#.Edge");
//		CodeList code = new CodeList();
//		code.setVariable("vcQualifiedName", schemaRootPackageName + ".impl."
//				+ vc.getQualifiedName());
//		code.setVariable("vcCamelName", camelCase(vc.getUniqueName()));
//		CodeSnippet s = new CodeSnippet(true);
//		s.add("/* add all valid from edges */");
//		s
//				.add("private static Set<java.lang.Class<? extends Edge>> validFromEdges = new HashSet<java.lang.Class<? extends Edge>>();");
//		s.add("");
//		s.add("/* (non-Javadoc)");
//		s.add(" * @see jgralab.Vertex:isValidAlpha()");
//		s.add(" */");
//		s.add("@Override");
//		s.add("public boolean isValidAlpha(Edge edge) {");
//		s.add("\treturn validFromEdges.contains(edge.getM1Class());");
//		s.add("}");
//		s.add("");
//		s.add("{");
//		code.addNoIndent(s);
//		for (EdgeClass ec : vc.getValidFromEdgeClasses()) {
//			CodeSnippet line = new CodeSnippet(true);
//			line.setVariable("edgeClassQualifiedName", schemaRootPackageName
//					+ "." + ec.getQualifiedName());
//			line.add("\tvalidFromEdges.add(#edgeClassQualifiedName#.class);");
//			code.addNoIndent(line);
//		}
//		s = new CodeSnippet(true);
//		s.add("}");
//		s.add("");
//		s.add("/* add all valid to edges */");
//		s
//				.add("private static Set<java.lang.Class<? extends Edge>> validToEdges = new HashSet<java.lang.Class<? extends Edge>>();");
//		s.add("");
//		s.add("/* (non-Javadoc)");
//		s.add(" * @see jgralab.Vertex:isValidOemga()");
//		s.add(" */");
//		s.add("@Override");
//		s.add("public boolean isValidOmega(Edge edge) {");
//		s.add("\treturn validToEdges.contains(edge.getM1Class());");
//		s.add("}");
//		s.add("");
//		s.add("{");
//		code.addNoIndent(s);
//		for (EdgeClass ec : vc.getValidToEdgeClasses()) {
//			CodeSnippet line = new CodeSnippet(true);
//			line.setVariable("edgeClassQualifiedName", schemaRootPackageName
//					+ "." + ec.getQualifiedName());
//			line.add("\tvalidToEdges.add(#edgeClassQualifiedName#.class);");
//			code.addNoIndent(line);
//		}
//		s = new CodeSnippet(true);
//		s.add("}");
//		code.addNoIndent(s);
//		return code;
//	}
//
//	// TODO Check duplicate rolenames at vertex class.
//	private CodeBlock createGetEdgeForRolenameMethod() {
//		CodeList list = new CodeList();
//		addImports("de.uni_koblenz.jgralab.schema.impl.DirectedM1EdgeClass");
//		CodeSnippet code = new CodeSnippet(true);
//		code
//				.add("private static java.util.Map<String, DirectedM1EdgeClass> roleMap;");
//		list.addNoIndent(code);
//		code = new CodeSnippet(true);
//		code.add("static {");
//		code
//				.add("roleMap = new java.util.HashMap<String, DirectedM1EdgeClass>();");
//		list.addNoIndent(code);
//		// addImports("de.uni_koblenz.jgralab.EdgeDirection");
//		VertexClass vc = (VertexClass) aec;
//		for (EdgeClass ec : vc.getValidFromEdgeClasses()) {
//			if (!ec.getTo().getRolename().isEmpty()) {
//				code = new CodeSnippet(true);
//				code.setVariable("rolename", ec.getTo().getRolename());
//				code.setVariable("edgeclass", schemaRootPackageName + "."
//						+ ec.getQualifiedName());
//				code.setVariable("dir",
//						"de.uni_koblenz.jgralab.EdgeDirection.OUT");
//				code
//						.add("roleMap.put(\"#rolename#\", new DirectedM1EdgeClass(#edgeclass#.class, #dir#));");
//				list.addNoIndent(code);
//			}
//		}
//		for (EdgeClass ec : vc.getValidToEdgeClasses()) {
//			if (!ec.getFrom().getRolename().isEmpty()) {
//				code = new CodeSnippet(true);
//				code.setVariable("rolename", ec.getFrom().getRolename());
//				code.setVariable("edgeclass", schemaRootPackageName + "."
//						+ ec.getQualifiedName());
//				code.setVariable("dir",
//						"de.uni_koblenz.jgralab.EdgeDirection.IN");
//				code
//						.add("roleMap.put(\"#rolename#\", new DirectedM1EdgeClass(#edgeclass#.class, #dir#));");
//				list.addNoIndent(code);
//			}
//		}
//		code = new CodeSnippet(true);
//		code.add("}");
//		list.addNoIndent(code);
//		code = new CodeSnippet(true);
//		code
//				.add(
//						"public DirectedM1EdgeClass getEdgeForRolename(String rolename) {",
//						"\treturn roleMap.get(rolename);", "}");
//		list.addNoIndent(code);
//		return list;
//	}
}
