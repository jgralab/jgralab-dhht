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

import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class RolenameCodeGenerator<MetaClass extends GraphElementClass<MetaClass, ?>> {

	private MetaClass metaClass;

	private String schemaRootPackageName;
	
	private String ownElementType;
	
	private String dualElementType;
	
	private boolean createVertex;
	
	
	/**
	 * Methods to be created: 
	 * 
	 * For each incident incidence class IC (interfaces only for owns)
	 * 
	 * void add_#rolename#(IC.getOtherType(this) elem);
	 * void remove_#rolename#(IC.getOtherType(this) elem);
	 * void remove_all_#rolename#();
	 * List<IC.getOtherType(this)> get_#rolename#();
	 * 
	 *   for all incidence classes OIC at the opposite end of IC.getOtherType();:
	 *   //add a new edge and returns it
	 *   IC.getOtherType(this) add_#otherrolename#_at_#rolename#(OIC.getOtherType(IC.getOtherType(this)) elem);
	 *   //remove the element from the first edge
	 *   IC.getOtherType(this) remove_#otherrolename#_at_#rolename#(OIC.getOtherType(IC.getOtherType(this)) elem);
	 *   List<OIC.getOtherType(IC.getOtherType(this))> get_#otherrolename#_at_#rolename#();
	 */

	RolenameCodeGenerator(MetaClass metaClass, boolean createForVertex) {
		this.metaClass = metaClass;
		this.createVertex = createForVertex;
		if (createForVertex) {
			this.ownElementType = "Vertex";
			this.dualElementType = "Edge";
		} else {
			this.ownElementType = "Edge";
			this.dualElementType = "Vertex";
		}
		schemaRootPackageName = metaClass.getSchema().getPackagePrefix()
				+ ".";
	}
	
	
	/** sets all necessary variables in the given code block 
	 * @return */
	private CodeBlock setVariables(CodeBlock code, IncidenceClass incClass) {
		code.setVariable("rolename", incClass.getRolename());
		code.setVariable("incClassName", schemaRootPackageName	+ incClass.getQualifiedName());
		code.setVariable("dir", "Direction." + incClass.getDirection().toString());
		code.setVariable("ownElementType", ownElementType);
		code.setVariable("dualElementType", dualElementType);
		code.setVariable("definingIncidentElementClassName", schemaRootPackageName + incClass.getOtherGraphElementClass(metaClass).getQualifiedName());
		return code;
	}
	

	private CodeBlock createRemoveIncidenceSnippet(IncidenceClass incClass, boolean createClass) {
		CodeSnippet code = (CodeSnippet) setVariables(new CodeSnippet(), incClass);
		if (!createClass) {
			code.add("/**",
					 " * removes the given <code>element</code> as <code>#rolename#</code> from this element, i.e. ",
					 " * deletes the <code>#incClassName#</code> incidence connecting this and <code>element</code>.",
					 " */",
					 "public boolean remove_#rolename#(#definingIncidentElementClassName# element) throws java.rmi.RemoteException;");
		} else {
			code.add("@Override",
					 "public boolean remove_#rolename#(#definingIncidentElementClassName# element) throws java.rmi.RemoteException {",
					   "\tboolean elementRemoved = false;",
					   "\t#incClassName# inc = (#incClassName#) getFirstIncidence(#incClassName#.class, #dir#);",
					   "\twhile (inc != null) {",
					     "\t\t#incClassName# next = (#incClassName#) inc.getNextIncidenceAt#ownElementType#(#incClassName#.class, #dir#);",
					     "\t\tif (inc.get#dualElementType#().equals(element)) {",
						   "\t\t\tinc.delete();",
						   "\t\t\telementRemoved = true;", 
					     "\t\t}",
					     "\t\tinc = next;", 
					  "\t}",
					  "\treturn elementRemoved;", 
					"}");
		}
		return code;
	}
	
	
	private CodeBlock createAddIncidenceSnippet(IncidenceClass incClass, IncidenceClass allowedIncClass, boolean createClass) {
		CodeSnippet code = (CodeSnippet) setVariables(new CodeSnippet(), incClass);
		code.setVariable("allowedIC", allowedIncClass.getQualifiedName());
		if (!createClass) {
			code.add("/**",
					 " * adds an incidence of the given vertex as <code>#rolename#</code> from this vertex, i.e. ",
					 " * deletes the <code>#edgeClassName#</code> edge connections of this vertex with ",
					 " * the given one.", " */",
					 "public void add_#rolename#(#definingIncidentElementClassName# element) throws java.rmi.RemoteException;");
		} else {
			code.add("@Override",
					 "public void add_#rolename#(#definingIncidentElementClassName# element) throws java.rmi.RemoteException {",
					   "\t#incClassName# inc = (#incClassName#) connect(#incClassName#.class, this, #element#);",
					"}");
		}
		return code;
	}
	

//	private CodeBlock createRemoveAdjacenceSnippet(IncidenceClass incidentIncidenceClass, IncidenceClass adjacentIncidenceClass, boolean createClass) {
//		CodeSnippet code = (CodeSnippet) setVariables(new CodeSnippet(), incidentIncidenceClass);
//		code.setVariable("adjacentIncClassName", adjacentIncidenceClass.getRolename());
//		code.setVariable("adjacentElementClassName", adjacentIncidenceClass.getRolename());
//		if (!createClass) {
//			code.add("/**",
//					 " * removes the given vertex as <code>#rolename#</code> from this vertex, i.e. ",
//					 " * deletes the <code>#edgeClassName#</code> edge connections of this vertex with ",
//					 " * the given one.", " */",
//					 "public boolean remove_#adjacentIncClassName#_at_#rolename#(#adjacentElementClassName# element);");
//		} else {
//			code.add("@Override",
//					 "public boolean remove_#adjacentIncClassName#_at_#rolename#(#adjacentElementClassName# element) {",
//					 "\treturn false;",
//					 "}");
//		}
//		return code;
//	}

//	private CodeBlock createRemoveAllAdjacencesSnippet(IncidenceClass incidentIncidenceClass, IncidenceClass adjacentIncidenceClass, boolean createClass) {
//		CodeSnippet code = (CodeSnippet) setVariables(new CodeSnippet(), incidentIncidenceClass);
//		if (!createClass) {
//			code.add(
//							"/**",
//							" * removes all #rolename# adjacences to all vertices by ",
//							" * deleting the <code>#edgeClassName#</code> edges of this vertex to ",
//							" * all other ones, but doesn't delete those vertices.",
//							" *",
//							" * @return the adjacent vertices prior to removal of incidences",
//							" */",
//							"public java.util.List<? extends #vertexClassName#> remove_#rolename#();");
//		} else {
//			code
//					.add(
//							"@Override",
//							"public java.util.List<? extends #vertexClassName#> remove_#rolename#() {",
//							"\tjava.util.List<#vertexClassName#> adjacences = new java.util.ArrayList<#vertexClassName#>();",
//							"\t#edgeClassName# edge = (#edgeClassName#) getFirstIncidence(#edgeClassName#.class, #dir#);",
//							"\twhile (edge != null) {",
//							"\t\t#edgeClassName# next = (#edgeClassName#) edge.getNextIncidence(#edgeClassName#.class, #dir#);",
//							"\t\tadjacences.add((#vertexClassName#) edge.getThat());",
//							"\t\tedge.delete();", "\t\tedge = next;", "\t}",
//							"\treturn adjacences;", "}");
//		}
//		return code;
//	}
	

//	private CodeBlock createGetAdjacencesSnippet(IncidenceClass incClass,
//			VertexClass allowedVertexClass, EdgeDirection dir,
//			boolean createClass) {
//		CodeSnippet code = new CodeSnippet();
//		code.setVariable("rolename", incClass.getRolename());
//		code.setVariable("edgeClassName", schemaRootPackageName
//				+ incClass.getEdgeClass().getQualifiedName());
//		code.setVariable("dir", "EdgeDirection." + dir.toString());
//		code.setVariable("vertexClassName", schemaRootPackageName
//				+ allowedVertexClass.getQualifiedName());
//
//		if (incClass.getMax() == 1) {
//			// if the rolename has an upper multiplicity of 1, create a method
//			// to access just the one element
//			if (!createClass) {
//				code
//						.add(
//								"/**",
//								" * @return the vertex to this one with the rolename '#rolename#' ",
//								" *         (connected with a <code>#edgeClassName#</code> edge), or null if no such vertex exists",
//								" */",
//								"public #vertexClassName# get_#rolename#();");
//			} else {
//				code
//						.add(
//								"@Override",
//								"public #vertexClassName# get_#rolename#() {",
//								"\t#edgeClassName# edge = (#edgeClassName#) getFirstIncidence(#edgeClassName#.class, #dir#);",
//								"\tif (edge != null) {",
//								"\t\treturn (#vertexClassName#) edge.getThat();",
//								"\t}", "\treturn null;", "}");
//			}
//		} else {
//			// if the rolename has an upper multiplicity greater than 1, create
//			// a method to access the list of elements
//			if (!createClass) {
//				code
//						.add(
//								"/**",
//								" * @return an Iterable of all vertices adjacent to this one with the rolename '#rolename#'",
//								" *         (connected with a <code>#edgeClassName#</code> edge).",
//								" */",
//								"public Iterable<? extends #vertexClassName#> get_#rolename#();");
//			} else {
//				code
//						.add(
//								"@Override",
//								"public Iterable<? extends #vertexClassName#> get_#rolename#() {",
//								"\treturn new de.uni_koblenz.jgralab.impl.NeighbourIterable<#edgeClassName#, #vertexClassName#>(this, #edgeClassName#.class, #dir#);",
//								"}");
//			}
//		}
//		return code;
//	}

//	private CodeBlock createAddRolenameSnippet(String rolename,
//			EdgeClass edgeClass, VertexClass definingVertexClass,
//			VertexClass allowedVertexClass, EdgeDirection dir,
//			boolean createClass) {
//		CodeSnippet code = new CodeSnippet();
//		code.setVariable("rolename", rolename);
//		code.setVariable("edgeClassName", schemaRootPackageName
//				+ edgeClass.getQualifiedName());
//		code.setVariable("graphClassName", schemaRootPackageName
//				+ edgeClass.getGraphClass().getQualifiedName());
//		code.setVariable("definingVertexClassName", schemaRootPackageName
//				+ definingVertexClass.getQualifiedName());
//		code.setVariable("allowedVertexClassName", schemaRootPackageName
//				+ allowedVertexClass.getQualifiedName());
//		code.setVariable("thisVertexClassName", schemaRootPackageName
//				+ vertexClass.getQualifiedName());
//		if (dir == EdgeDirection.OUT) {
//			code.setVariable("alpha", "this");
//			code.setVariable("alphaVertexClassName", schemaRootPackageName
//					+ vertexClass.getQualifiedName());
//			code.setVariable("omega", "vertex");
//			code.setVariable("omegaVertexClassName", schemaRootPackageName
//					+ allowedVertexClass.getQualifiedName());
//		} else {
//			code.setVariable("alpha", "vertex");
//			code.setVariable("alphaVertexClassName", schemaRootPackageName
//					+ allowedVertexClass.getQualifiedName());
//			code.setVariable("omega", "this");
//			code.setVariable("omegaVertexClassName", schemaRootPackageName
//					+ vertexClass.getQualifiedName());
//		}
//		if (!createClass) {
//			code
//					.add(
//							"/**",
//							" * adds the given vertex as <code>#rolename#</code> to this vertex, i.e. creates an",
//							" * <code>#edgeClassName#</code> edge from this vertex to the given ",
//							" * one and returns the created edge.",
//							" * @return  a newly created edge of type <code>#edgeClassName#</code>",
//							" *          between this vertex and the given one.",
//							" */",
//							"public #edgeClassName# add_#rolename#(#definingVertexClassName# vertex);");
//		} else {
//			code
//					.add("@Override",
//							"public #edgeClassName# add_#rolename#(#definingVertexClassName# vertex) {");
//			if (definingVertexClass != allowedVertexClass) {
//				code
//						.add(
//								"\tif (!(vertex instanceof #allowedVertexClassName#)) {",
//								"\t\tthrow new de.uni_koblenz.jgralab.GraphException(\"The rolename #rolename# was redefined at the vertex class #thisVertexClassName#. Only vertices of #allowedVertexClassName# are allowed.\"); ",
//								"\t}");
//			}
//			code
//					.add(
//							"\treturn ((#graphClassName#)getGraph()).createEdge(#edgeClassName#.class, (#alphaVertexClassName#) #alpha#, (#omegaVertexClassName#) #omega#);",
//							"}");
//		}
//		return code;
//	}

	public CodeBlock createRolenameMethods(boolean createClass) {
		CodeList list = new CodeList();
		//code for incidence incidences
		if (createClass) {
			//create methods for all incidence classes
			Map<IncidenceClass, IncidenceClass> redefinedIncidenceClasses = new HashMap<IncidenceClass, IncidenceClass>();
			for (IncidenceClass ic : metaClass.getAllIncidenceClasses() ) {
				redefinedIncidenceClasses.put(ic, ic);
			} 
			for (IncidenceClass ic : metaClass.getAllIncidenceClasses() ) {
				if (createVertex) {
					for (IncidenceClass rdic : ic.getHiddenEndsAtVertex()) {
						if (!redefinedIncidenceClasses.get(rdic).isSubClassOf(ic))
							redefinedIncidenceClasses.put(rdic, ic);
					}
				} else {
					for (IncidenceClass rdic : ic.getHiddenEndsAtEdge()) {
						if (!redefinedIncidenceClasses.get(rdic).isSubClassOf(ic))
							redefinedIncidenceClasses.put(rdic, ic);
					}
				}
			}
			for (IncidenceClass definedClass : redefinedIncidenceClasses.keySet()) {
				list.add(createMethodsForOneIncidenceClass(definedClass, redefinedIncidenceClasses.get(definedClass), createClass));
			}
		} else {
			//create only methods for own incidence classes since the others are inherited
			for (IncidenceClass ic : metaClass.getIncidenceClasses() ) {
				list.add(createMethodsForOneIncidenceClass(ic, ic, createClass));
			}
		}

		return list;
	}

	private CodeList createMethodsForOneIncidenceClass(
			IncidenceClass allowedIncidenceClass,
			IncidenceClass definingIncidenceClass, boolean createClass) {
		CodeList list = new CodeList();
		if (!definingIncidenceClass.getRolename().isEmpty()) {
		//	GraphElementClass definedIncidentGEC = definingIncidenceClass.getOtherGraphElementClass(metaClass);
		//	GraphElementClass allowedIncidentGEC = allowedIncidenceClass.getOtherGraphElementClass(metaClass);
			list.addNoIndent(createRemoveIncidenceSnippet(definingIncidenceClass, createClass));
			list.addNoIndent(createAddIncidenceSnippet(definingIncidenceClass, allowedIncidenceClass, createClass));
		//	list.addNoIndent(createAddIncidentRolenameSnippet(definingIncidenceClass, allowedIncidenceClass, createClass);
		//	list.addNoIndent(createAddAdjacentRolenameSnippet(definingIncidenceClass, allowedIncidenceClass, definingAdjacentIC, allowedAdjacentIC, createClass);	
		//	list.addNoIndent(createRemoveAllAdjacencesSnippet(rolename, ec, allowedVC, dir, createClass));
		//	list.addNoIndent(createRemoveAdjacenceSnippet(definingIncidenceClass, definingAdjacentIC, createClass));
		//	list.addNoIndent(createGetAdjacencesSnippet(definingIncidenceClass,	allowedVC, dir, createClass));
			
		    //tread adjacent incidence classes
			
			
			
		}
		return list;
	}

}
