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

import java.util.Collection;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.BinaryEdgeClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class GraphCodeGenerator extends AttributedElementCodeGenerator<GraphClass> {

	public GraphCodeGenerator(GraphClass graphClass, String schemaPackageName,
			String schemaName, CodeGeneratorConfiguration config) {
		super(graphClass, schemaPackageName, config);
		rootBlock.setVariable("graphElementClass", "Graph");
		rootBlock.setVariable("schemaName", schemaName);
		rootBlock.setVariable("theGraph", "this");
	}

	@Override
	protected CodeBlock createHeader() {
		return super.createHeader();
	}

	@Override
	protected CodeList createBody() {
		CodeList code = (CodeList) super.createBody();
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
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
				addImports("de.uni_koblenz.jgralab.GraphException",
						"#jgImplDbPackage#.#baseClassName#",
						"#jgImplDbPackage#.GraphDatabase",
						"#jgImplDbPackage#.GraphDatabaseException");
			}

			rootBlock.setVariable("baseClassName", "CompleteGraphImpl");
			addImports("de.uni_koblenz.jgralab.impl.CompleteGraphImpl");
		//	addImports("java.util.List");
		//	addImports("de.uni_koblenz.jgralab.Vertex");
		//	addImports("de.uni_koblenz.jgralab.greql2.jvalue.JValue");
		//	addImports("de.uni_koblenz.jgralab.greql2.jvalue.JValueSet");
		//	addImports("de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl");
		//	addImports("de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator");

			// for Vertex.reachableVertices()
//			code.add(new CodeSnippet(
//						"\n\tprotected GreqlEvaluator greqlEvaluator = null;\n",
//						"@SuppressWarnings(\"unchecked\") ",
//						"@Override ",
//						"public synchronized <T extends Vertex> List<T> reachableVertices(Vertex startVertex, String pathDescription, Class<T> vertexType) { ",
//						"\tif (greqlEvaluator == null) { ",
//						"\t\tgreqlEvaluator = new GreqlEvaluator((String) null, this, null); ",
//						"\t} ",
//						"\tgreqlEvaluator.setVariable(\"v\", new JValueImpl(startVertex)); ",
//						"\tgreqlEvaluator.setQuery(\"using v: v \" + pathDescription); ",
//						"\tgreqlEvaluator.startEvaluation(); ",
//						"\tJValueSet rs = greqlEvaluator.getEvaluationResult().toJValueSet(); ",
//						"\tjava.util.List<T> lst = new java.util.LinkedList<T>(); ",
//						"\tfor (JValue jv : rs) { ",
//						"\t\tVertex v = jv.toVertex();",
//						"\t\tif (vertexType.isInstance(v)) {",
//						"\t\t\tlst.add((T) v);", "\t\t}", "\t}",
//						"\treturn lst; ", "}"));
		}
		code.add(createGraphElementClassMethods());
		code.add(createIteratorMethods());
		code.add(createCreateRecordsMethods());
		return code;
	}

	/**
	 * Create "create"-methods for each RecordDomain defined in the schema.
	 * 
	 * @param createClass
	 * @return
	 */
	private CodeBlock createCreateRecordsMethods() {
		CodeList code = new CodeList();

		if (currentCycle.isAbstract()) {
			if (aec.getSchema().getRecordDomains().size() > 0) {
				addImports("java.util.Map");
				addImports("#jgPackage#.GraphIO");
				addImports("#jgPackage#.GraphIOException");
			}
			for (RecordDomain rd : aec.getSchema().getRecordDomains()) {
				CodeSnippet cs = new CodeSnippet(true);
				cs.add("public #rcname# create#rname#(GraphIO io) throws GraphIOException;");
				cs.add("");

				cs.add("public #rcname# create#rname#(Map<String, Object> fields);");
				cs.add("");

				cs.add("public #rcname# create#rname#(#parawtypes#);");

				cs.setVariable("parawtypes", buildParametersOutput(rd
						.getComponents(), true));
				cs.setVariable("rcname", rd
						.getJavaClassName(schemaRootPackageName));
				cs.setVariable("rname", rd.getUniqueName());
				cs.add("");
				code.addNoIndent(cs);
			}
		}

		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			if (aec.getSchema().getRecordDomains().size() > 0) {
				addImports("java.util.Map");
			}
			for (RecordDomain rd : aec.getSchema().getRecordDomains()) {
				CodeSnippet cs = new CodeSnippet(true);
				cs.add("public #rcname# create#rname#(GraphIO io) throws GraphIOException {");

				if (currentCycle.isTransImpl()) {
					cs.add("\tif(!isLoading() && getCurrentTransaction().isReadOnly())");
					cs.add("\t\tthrow new #jgPackage#.GraphException(\"Read-only transactions are not allowed to create instances of #rtype#.\");");
					cs.add("\t#rcname# record = graphFactory.createRecordWithTransactionSupport(#rcname#.class, this);");
				} else if (currentCycle.isStdImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecord(#rcname#.class, this);");
				} else if (currentCycle.isSaveMemImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithSavememSupport(#rcname#.class, this);");
				} else if (currentCycle.isDbImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithDatabaseSupport(#rcname#.class, this);");
				}
				cs.add("\trecord.readComponentValues(io);");
				cs.add("\treturn record;");
				cs.add("}");
				cs.add("");

				cs.add("public #rcname# create#rname#(Map<String, Object> fields) {");
				if (currentCycle.isTransImpl()) {
					cs.add("\tif(!isLoading() && getCurrentTransaction().isReadOnly())");
					cs.add("\t\tthrow new #jgPackage#.GraphException(\"Read-only transactions are not allowed to create instances of #rtype#.\");");
					cs.add("\t#rcname# record = graphFactory.createRecordWithTransactionSupport(#rcname#.class, this);");
				} else if (currentCycle.isStdImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecord(#rcname#.class, this);");
				} else if (currentCycle.isSaveMemImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithSavememSupport(#rcname#.class, this);");
				} else if (currentCycle.isDbImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithDatabaseSupport(#rcname#.class, this);");
				}

				cs.add("\trecord.setComponentValues(fields);");
				cs.add("\treturn record;");
				cs.add("}");
				cs.add("");

				cs.setVariable("parawtypes", buildParametersOutput(rd
						.getComponents(), true));
				cs.setVariable("parawotypes", buildParametersOutput(rd
						.getComponents(), false));

				cs.add("");
				cs.add("public #rcname# create#rname#(#parawtypes#) {");
				if (currentCycle.isTransImpl()) {
					cs.add("\tif(!isLoading() && getCurrentTransaction().isReadOnly())");
					cs.add("\t\tthrow new #jgPackage#.GraphException(\"Read-only transactions are not allowed to create instances of #rtype#.\");");
					cs.add("\t#rcname# record = graphFactory.createRecordWithTransactionSupport(#rcname#.class, this);");
				} else if (currentCycle.isStdImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecord(#rcname#.class, this);");
				} else if (currentCycle.isSaveMemImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithSavememSupport(#rcname#.class, this);");
				} else if (currentCycle.isDbImpl()) {
					cs.add("\t#rcname# record = graphFactory.createRecordWithDatabaseSupport(#rcname#.class, this);");
				}
				for (RecordComponent entry : rd.getComponents()) {
					cs.add("\trecord.set_" + entry.getName() + "(_"
							+ entry.getName() + ");");
				}
				cs.add("\treturn record;");
				cs.add("}");
				cs.add("");

				cs.setVariable("rcname", rd.getJavaClassName(schemaRootPackageName));
				cs.setVariable("rname", rd.getUniqueName());
				cs.setVariable("rtype",	rd.getJavaAttributeImplementationTypeName(schemaRootPackageName));
				cs.setVariable("rtranstype",rd.getTransactionJavaAttributeImplementationTypeName(schemaRootPackageName));
				cs.setVariable("rstdtype",rd.getStandardJavaAttributeImplementationTypeName(schemaRootPackageName));
				cs.setVariable("rsavememtype",rd.getSavememJavaAttributeImplementationTypeName(schemaRootPackageName));
				code.addNoIndent(cs);
			}
		}
		return code;
	}

	/**
	 * 
	 * @param components
	 * @param withTypes
	 * @return
	 */
	private String buildParametersOutput(
			Collection<RecordComponent> components, boolean withTypes) {
		StringBuilder parameters = new StringBuilder();
		int count = 0;
		int size = components.size();
		for (RecordComponent entry : components) {
			parameters.append(
					(withTypes ? entry.getDomain()
							.getJavaAttributeImplementationTypeName(
									schemaRootPackageName) : "")).append(" _")
					.append(entry.getName());
			count++;
			if (size != count) {
				parameters.append(", ");
			}
		}
		return parameters.toString();
	}

	@Override
	protected CodeBlock createConstructor() {
		addImports("#schemaPackageName#.#schemaName#");
		CodeSnippet code = new CodeSnippet(true);
		if (currentCycle.isTransImpl()) {
			code.setVariable("createSuffix", "WithTransactionSupport");
		}
		if (currentCycle.isStdImpl()) {
			code.setVariable("createSuffix", "");
		}
		if (currentCycle.isDbImpl()) {
			code.setVariable("createSuffix", "WithDatabaseSupport");
		}
		if (currentCycle.isSaveMemImpl()) {
			code.setVariable("createSuffix", "WithSavememSupport");
		}
		// TODO if(currentCycle.isDbImpl()) only write ctors and create with
		// GraphDatabase as param.
		if (!currentCycle.isDbImpl()) {
			code.add(
							"/* Constructors and create methods with values for initial vertex and edge count */",
							"public #simpleClassName#Impl(int vMax, int eMax) {",
							"\tthis(null, vMax, eMax);",
							"}",
							"",
							"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {",
							"\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);",
							"\tinitializeAttributesWithDefaultValues();",
							"}",
							"",
							"public static #javaClassName# create(int vMax, int eMax) {",
							"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null, vMax, eMax);",
							"}",
							"",
							"public static #javaClassName# create(String id, int vMax, int eMax) {",
							"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, vMax, eMax);",
							"}",
							"",
							"/* Constructors and create methods without values for initial vertex and edge count */",
							"public #simpleClassName#Impl() {",
							"\tthis(null);",
							"}",
							"",
							"public #simpleClassName#Impl(java.lang.String id) {",
							"\tsuper(id, #schemaName#.instance().#schemaVariableName#);",
							"\tinitializeAttributesWithDefaultValues();",
							"}",
							"",
							"public static #javaClassName# create() {",
							"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(null);",
							"}",
							"",
							"public static #javaClassName# create(String id) {",
							"\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id);",
							"}");
		} else {
			code.add("/* Constructors and create methods for database support */",
					 "",
					 /*
					 * "public #simpleClassName#Impl(java.lang.String id) {",
							 * "\tsuper(id, #schemaName#.instance().#schemaVariableName#);"
							 * , // TODO Should not be allowed. "}", "",
							 * "public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax) {"
							 * ,
							 * "\tsuper(id, #schemaName#.instance().#schemaVariableName#, vMax, eMax);"
							 * , // TODO Should not be allowed.
							 * "\tinitializeAttributesWithDefaultValues();",
							 * "}", "",
							 */
							"public #simpleClassName#Impl(java.lang.String id, GraphDatabase graphDatabase) {",
							"\tsuper(id, #schemaName#.instance().#schemaVariableName#, graphDatabase);",
							"\tinitializeAttributesWithDefaultValues();",
							"}",
							"",
							"public #simpleClassName#Impl(java.lang.String id, int vMax, int eMax, GraphDatabase graphDatabase) {",
							"\tsuper(id, vMax, eMax, #schemaName#.instance().#schemaVariableName#, graphDatabase);",
							"\tinitializeAttributesWithDefaultValues();",
							"}",
							"",

							"public static #javaClassName# create(String id, GraphDatabase graphDatabase) {",
							"\ttry{",
							"\t\treturn (#javaClassName#) #schemaName#.instance().create#uniqueClassName##createSuffix#(id, graphDatabase);",
							"\t}",
							"\tcatch(GraphDatabaseException exception){",
							"\t\tthrow new GraphException(\"Could not create graph.\", exception);",
							"\t}", "}");
		}
		return code;
	}

	private CodeBlock createGraphElementClassMethods() {
		CodeList code = new CodeList();

		GraphClass gc = (GraphClass) aec;
		TreeSet<GraphElementClass<?,?>> sortedClasses = new TreeSet<GraphElementClass<?,?>>();
		sortedClasses.addAll(gc.getGraphElementClasses());
		for (GraphElementClass<?,?> gec : sortedClasses) {
			if (!gec.isInternal()) {
				CodeList gecCode = new CodeList();
				code.addNoIndent(gecCode);

				gecCode.addNoIndent(new CodeSnippet(
								true,
								"// ------------------------ Code for #ecQualifiedName# ------------------------"));

				gecCode.setVariable("ecSimpleName", gec.getSimpleName());
				gecCode.setVariable("ecUniqueName", gec.getUniqueName());
				gecCode.setVariable("ecQualifiedName", gec.getQualifiedName());
				gecCode.setVariable("ecSchemaVariableName", gec
						.getVariableName());
				gecCode.setVariable("ecJavaClassName", schemaRootPackageName
						+ "." + gec.getQualifiedName());
				gecCode.setVariable("ecType",
						(gec instanceof VertexClass ? "Vertex" : "Edge"));
				gecCode.setVariable("ecTypeInComment",
						(gec instanceof VertexClass ? "vertex" : "edge"));
				gecCode.setVariable("ecCamelName", camelCase(gec
						.getUniqueName()));
				gecCode.setVariable("ecImplName",
						(gec.isAbstract() ? "**ERROR**" : camelCase(gec
								.getQualifiedName())
								+ "Impl"));

				gecCode.addNoIndent(createGetFirstMethods(gec));
				gecCode.addNoIndent(createFactoryMethods(gec));
			}
		}

		return code;
	}

	private CodeBlock createGetFirstMethods(GraphElementClass<?,?> gec) {
		CodeList code = new CodeList();
		if (config.hasTypeSpecificMethodsSupport()) {
			code.addNoIndent(createGetFirstMethod(gec, false));
			if (config.hasMethodsForSubclassesSupport()) {
				if (!gec.isAbstract()) {
					code.addNoIndent(createGetFirstMethod(gec, true));
				}
			}
		}
		return code;
	}

	private CodeBlock createGetFirstMethod(GraphElementClass<?,?> gec,
			boolean withTypeFlag) {
		CodeSnippet code = new CodeSnippet(true);
		if (currentCycle.isAbstract()) {
			code.add("/**",
					 " * @return the first #ecSimpleName# #ecTypeInComment# in this graph");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #ecSimpleName# are accepted");
			}
			code.add(" */",
							"public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add("public #ecJavaClassName# getFirst#ecCamelName#(#formalParams#) {",
					 "\treturn (#ecJavaClassName#)getFirst#ecType#(#schemaName#.instance().#ecSchemaVariableName##actualParams#);",
					 "}");
		}
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"	: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses"	: ""));

		return code;
	}

	private CodeBlock createFactoryMethods(GraphElementClass<?,?> gec) {
		if (gec.isAbstract()) {
			return null;
		}
		CodeList code = new CodeList();
		code.addNoIndent(createFactoryMethod(gec, false));
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.addNoIndent(createFactoryMethod(gec, true));
		}
		return code;
	}

	private CodeBlock createFactoryMethod(GraphElementClass<?,?> gec, boolean withId) {
		CodeSnippet code = new CodeSnippet(true);

		if (currentCycle.isStdImpl()) {
			code.setVariable("cycleSupportSuffix", "");
		} else if (currentCycle.isTransImpl()) {
			code.setVariable("cycleSupportSuffix", "WithTransactionSupport");
		} else if (currentCycle.isDbImpl()) {
			code.setVariable("cycleSupportSuffix", "WithDatabaseSupport");
		} else if (currentCycle.isSaveMemImpl()) {
			code.setVariable("cycleSupportSuffix", "WithSavememSupport");
		}

		if (currentCycle.isAbstract()) {
			code.add("/**",
					 " * Creates a new #ecUniqueName# #ecTypeInComment# in this graph.",
					 " *");
			if (withId) {
				code.add(" * @param id the <code>id</code> of the #ecTypeInComment#");
			}
			if (gec instanceof EdgeClass) {
				code.add(" * @param alpha the start vertex of the edge",
					  	 " * @param omega the target vertex of the edge");
			}
			code.add("*/",
					 "public #ecJavaClassName# create#ecCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add("public #ecJavaClassName# create#ecCamelName#(#formalParams#) {",
					 "\t#ecJavaClassName# new#ecType# = (#ecJavaClassName#) graphFactory.create#ecType##cycleSupportSuffix#(#ecJavaClassName#.class, #newActualParams#, this#additionalParams#);",
					 "\treturn new#ecType#;", "}");
			code.setVariable("additionalParams", "");
		}

		//TODO: For binary Edge constructor
		
		if (gec instanceof BinaryEdgeClass) {
			String fromClass = null;
			String toClass = null;
			for (IncidenceClass ic : gec.getAllIncidenceClasses()) {
				if (!ic.isAbstract()) {
					if (ic.getDirection() == Direction.EDGE_TO_VERTEX) {
						toClass = ic.getVertexClass().getQualifiedName();
					} else {
						fromClass = ic.getVertexClass().getQualifiedName();
					}
				}
			}
			if (fromClass.equals("Vertex")) {
				code.setVariable("fromClass", rootBlock.getVariable("jgPackage") + "." + "Vertex");
			} else {
				code.setVariable("fromClass", schemaRootPackageName + "." + fromClass);
			}
			if (toClass.equals("Vertex")) {
				code.setVariable("toClass", rootBlock.getVariable("jgPackage")
						+ "." + "Vertex");
			} else {
				code.setVariable("toClass", schemaRootPackageName + "."
						+ toClass);
			}
			code.setVariable("formalParams", (withId ? "int id, " : "")
					+ "#fromClass# alpha, #toClass# omega");
			code.setVariable("addActualParams", ", alpha, omega");
			code.setVariable("additionalParams", ", alpha, omega");
		} else {
			code.setVariable("formalParams", (withId ? "int id" : ""));
			code.setVariable("addActualParams", "");
		}
		code.setVariable("newActualParams", (withId ? "id" : "0"));
		return code;
		// TODO if isDbImpl() only write two create methods!
	}

	private CodeBlock createIteratorMethods() {
		GraphClass gc = (GraphClass) aec;

		CodeList code = new CodeList();
		if (!config.hasTypeSpecificMethodsSupport()) {
			return code;
		}
		CodeBlock block = createIteratorMethods(gc.getVertexClasses());
		block.setVariable("elemClassName", "Vertex");
		block.setVariable("elemClassLowName", "vertex"); 
		block.setVariable("elemClassPluralName", "Vertices");
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			addImports("#jgImplPackage#.VertexIterable");
		}
		code.add(block);
		block = createIteratorMethods(gc.getEdgeClasses());
		block.setVariable("elemClassName", "Edge");
		block.setVariable("elemClassLowName", "edge"); 
		block.setVariable("elemClassPluralName", "Edges");
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			addImports("#jgImplPackage#.EdgeIterable");
		}
		code.add(block);
		return code;
	}
	
	protected CodeBlock createIteratorMethods(Iterable<? extends GraphElementClass<?,?>> set) {
		CodeList code = new CodeList();
		for (GraphElementClass<?,?> gec : set) {
			if (gec.isInternal()) {
				continue;
			}
			code.addNoIndent(createIteratorMethods(gec));
		}
		return code;
	}
	
	
	
	protected CodeBlock createIteratorMethods(GraphElementClass<?,?> gec) {
		CodeList code = new CodeList();
		CodeSnippet s = new CodeSnippet(true);
		code.addNoIndent(s);
		s.setVariable("elemQualifiedName", gec.getQualifiedName());
		s.setVariable("elemJavaClassName", schemaRootPackageName + "." + gec.getQualifiedName());
		s.setVariable("elemCamelName", camelCase(gec.getUniqueName()));
		
		if (currentCycle.isAbstract()) {
			s.add("/**");
			s.add(" * @return an Iterable for all #elemClassPluralName# of this graph that are of type #elemQualifiedName# or subtypes.");
			s.add(" */");
			s.add("public Iterable<#elemJavaClassName#> get#elemCamelName##elemClassPluralName#();");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			s.add("public Iterable<#elemJavaClassName#> get#elemCamelName##elemClassPluralName#() {");
			s.add("\treturn new #elemClassName#Iterable<#elemJavaClassName#>(this, #elemJavaClassName#.class);");
			s.add("}");
		}
		s.add("");
	
		if (config.hasMethodsForSubclassesSupport()) {
			if (currentCycle.isAbstract()) {
				s.add("/**");
				s.add(" * @return an Iterable for all #elemClassPluralName# of this graph that are of type #elemQualifiedName#.");
				s.add(" *");
				s.add(" * @param noSubClasses toggles wether subclasses of #elemQualifiedName# should be excluded");
				s.add(" */");
				s.add("public Iterable<#elemJavaClassName#> get#elemCamelName#elemClassPluralName(boolean noSubClasses);");
			}
			if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
				s.add("public Iterable<#elemJavaClassName#> get#elemCamelName#elemClassPluralName(boolean noSubClasses) {");
				s.add("\treturn new #elemClasslName#Iterable<#elemJavaClassName#>(this, #elemJavaClassName#.class, noSubClasses);");
				s.add("}\n");
			}
		}
		return code;
	}
	

	@Override
	protected void addCheckValidityCode(CodeSnippet code) {
		// just do nothing here
	}
}
