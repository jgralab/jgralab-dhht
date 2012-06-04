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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
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
	protected CodeBlock createHeader() {
		CodeList code = new CodeList();
		CodeSnippet snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add(" * Incoming IncidenceClasses:");
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



	@Override
	protected CodeBlock createConstructor() {
		CodeList code = (CodeList) super.createConstructor();
		code.setVariable("implOrProxy", currentCycle.isImplementationVariant() ? "Impl" : "Proxy");
		switch (currentCycle) {
		case DISKPROXIES:
			addImports("#jgDiskImplPackage#.VertexProxy");
			break;
		case DISKV2PROXIES:
			addImports("#jgDiskv2ImplPackage#.VertexProxy");
			break;
		case DISTRIBUTEDPROXIES:
			addImports("#jgDistributedImplPackage#.VertexProxy");
			break;
		case DISKBASED:
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

	
	protected CodeBlock createCompatibilityMethods() {
		addImports("#jgPackage#.Direction");
		VertexClass vc = (VertexClass) aec;
		CodeList code = new CodeList();
		Set<EdgeClass> edgeClassSet = new HashSet<EdgeClass>();
		addImports("#jgPackage#.Incidence");
		if (currentCycle.isAbstract()) {
			//interface, create only method declarations for own edge classes
			for (IncidenceClass ic : vc.getIncidenceClasses()) {
				edgeClassSet.add(ic.getEdgeClass());
			}
		} else {
			for (IncidenceClass ic : vc.getAllIncidenceClasses()) {
				if (vc.getSimpleName().equals("BinaryEdgeClass"))
					System.out.println("Adding incidence class:" + ic.getSimpleName());
				edgeClassSet.add(ic.getEdgeClass());
			}
		}
		for (EdgeClass ec : edgeClassSet) {
			if (ec.isInternal())
				continue;
			CodeSnippet s = new CodeSnippet();
			code.addNoIndent(s);
			s.setVariable("edgeClassSimpleName", ec.getSimpleName());
			s.setVariable("edgeClassQualifiedName", ec.getSchema().getPackagePrefix().concat("." + ec.getQualifiedName()));
			s.setVariable("edgeClassUniqueName", ec.getUniqueName());
			s.setVariable("incidenceClassQualifiedName", "Incidence");
			boolean debug = vc.getSimpleName().equals("BinaryEdgeClass") && ec.getSimpleName().equals("SpecializesEdgeClass");
			Set<IncidenceClass> ics = new HashSet<IncidenceClass>();
			if (debug)
				System.out.println("Handling incidence classes");
			for (IncidenceClass ic : ec.getAllIncidenceClasses()) {
				if (ic.isInternal())
					continue;
				if (ic.getVertexClass() == vc || (ic.getVertexClass().isSuperClassOf(vc)) || (ic.getVertexClass().isSubClassOf(vc)) ) {
					if (debug)
						System.out.println("Thinking of adding incidence class: " + ic.getSimpleName());
					boolean superclassContained = false;
					for (IncidenceClass superClass : ic.getAllSuperClasses()) {
						if (ics.contains(superClass) && !superClass.isAbstract()) {
							superclassContained = true;
						}
					}
					if (!superclassContained) {
						//remove all subclasses of the current incidence class
						if (debug)
							System.out.println("Adding incidence class: " + ic.getSimpleName());
						if (!ic.isAbstract())
							ics.removeAll(ic.getAllSubClasses());
						ics.add(ic);
					} else {
						if (debug)
							System.out.println("Not adding incidence class: " + ic.getSimpleName());
					}
				}
			}
			//remove all abstract classes with only one contained superclass
			if (debug)
			for (IncidenceClass ic : ics) {
				System.out.println("Contained incidence class: " + ic.getSimpleName());
			}
			Set<IncidenceClass> subclassesToBeRemoved = new HashSet<IncidenceClass>();
			Iterator<IncidenceClass> it = ics.iterator();
			while (it.hasNext()) {
				IncidenceClass possibleAbstractSuperClass = it.next();
				if (debug)
					System.out.println("Testing possible abstract incidence class: " + possibleAbstractSuperClass.getSimpleName());
				if (debug)
					System.out.println("  Abstract:  " + possibleAbstractSuperClass.isAbstract());
				if (!possibleAbstractSuperClass.isAbstract())
					continue;
				int numberOfContainedSubclasses = 0;
				for (IncidenceClass directSubclass : possibleAbstractSuperClass.getDirectSubClasses()) {
					if (ics.contains(directSubclass)) {
						if (debug)
							System.out.println("  Contained Subclass: " + directSubclass.getSimpleName());
						numberOfContainedSubclasses++;
					}	
				}
				if (debug)
					System.out.println("  Contained Subclasses: " + numberOfContainedSubclasses);
				if (numberOfContainedSubclasses <= 1) {
					it.remove();
				} else {
					for (IncidenceClass directSubclass : possibleAbstractSuperClass.getDirectSubClasses()) {
						if (ics.contains(directSubclass))
							subclassesToBeRemoved.add(directSubclass);
					}
				}
			}
			
			
			if (ics.size() == 1) {
				//set one and only possible incidence class name 
				it = ics.iterator();
				it.hasNext();
				IncidenceClass ic = it.next();
				s.setVariable("incidenceClassQualifiedName", ic.getSchema().getPackagePrefix() + "." + ic.getQualifiedName());
			}

			//Methods to access first incidence
			if (currentCycle.isAbstract()) {
				s.add("/**");
				s.add(" * Returns the first incidence leading to an edge of type #edgeClassSimpleName# or subtypes.");
				s.add(" */");
				s.add("public #incidenceClassQualifiedName# getFirstIncidenceTo#edgeClassUniqueName#(Direction direction);");
				s.add("");
				s.add("/**");
				s.add(" * Returns the first incidence leading to an edge of type #edgeClassSimpleName# or subtypes.");
				s.add(" */");
				s.add("public #incidenceClassQualifiedName# getFirstIncidenceTo#edgeClassUniqueName#();");
			} else { 
				s.add("@Override");
				s.add("public #incidenceClassQualifiedName# getFirstIncidenceTo#edgeClassUniqueName#(Direction direction) {");
				s.add("\treturn (#incidenceClassQualifiedName#) getFirstIncidenceToEdge(#edgeClassQualifiedName#.class, direction);");
				s.add("}");
				s.add("");
				s.add("@Override");
				s.add("public #incidenceClassQualifiedName# getFirstIncidenceTo#edgeClassUniqueName#() {");
				s.add("\treturn (#incidenceClassQualifiedName#) getFirstIncidenceToEdge(#edgeClassQualifiedName#.class, Direction.BOTH);");
				s.add("}");
			}
			
			//methods to access incidence sequence 
			if (currentCycle.isAbstract()) {
				s.add("/**");
				s.add(" * Returns an iterable for all incident edges of type #edgeClassSimpleName# or subtypes.");
				s.add(" */");
				s.add("public Iterable<#edgeClassQualifiedName#> getIncidentEdgesOfType_#edgeClassUniqueName#();");
				s.add("");
				s.add("");
				s.add("/**");
				s.add(" * Returns an iterable for all incident edges of type #edgeClassSimpleName# or subtypes.");
				s.add(" */");
				s.add("public Iterable<#edgeClassQualifiedName#> getIncidentEdgesOfType_#edgeClassUniqueName#(Direction direction);");
			} else { 
				s.add("@Override");
				s.add("public Iterable<#edgeClassQualifiedName#> getIncidentEdgesOfType_#edgeClassUniqueName#() {");
				s.add("\treturn getIncidentEdges(#edgeClassQualifiedName#.class);");
				s.add("}");
				s.add("");
				s.add("");
				s.add("@Override");
				s.add("public Iterable<#edgeClassQualifiedName#> getIncidentEdgesOfType_#edgeClassUniqueName#(Direction direction) {");
				s.add("\treturn getIncidentEdges(#edgeClassQualifiedName#.class, direction);");
				s.add("}");
			}
			s.add("");
		}
		return code;
	}

	protected CodeBlock createIncidenceIteratorMethod(IncidenceClass ic) {
		CodeSnippet s = new CodeSnippet();
		addImports("#jgImplPackage#.IncidenceIterableAtVertex");
		s.setVariable("incidenceClassName", ic.getRolename());
		s.setVariable("incidenceUniqueClassName", ic.getUniqueName());
		s.setVariable("qualifiedIncidenceClassName", schemaRootPackageName + "." +  ic.getQualifiedName());
		if (currentCycle.isAbstract()) {
			s.add("/**");
			s.add(" * Returns an Iterable for all incidences that are of type #incidenceClassName# or subtypes.");
			s.add(" */");
			s.add("public Iterable<#qualifiedIncidenceClassName#> get#incidenceUniqueClassName#Incidences();");
		} else {
			s.add("@Override");
			s.add("public Iterable<#qualifiedIncidenceClassName#> get#incidenceUniqueClassName#Incidences() {");
			s.add("\treturn new IncidenceIterableAtVertex(this, #qualifiedIncidenceClassName#.class);");
			s.add("}");
			
		}
		return s;
	}	

}
