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

import java.util.Set;
import java.util.SortedSet;

import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class AttributedElementCodeGenerator<ConcreteMetaClass extends AttributedElementClass<ConcreteMetaClass, ?>>
		extends TypedElementCodeGenerator<ConcreteMetaClass> {

	/**
	 * specifies if the generated code is a special JGraLab class of layer M2
	 * this effects the way the constructor and some methods are built valid
	 * values: "Graph", "Vertex", "Edge", "Incidence"
	 */
	protected AttributedElementCodeGenerator(
			ConcreteMetaClass attributedElementClass,
			String schemaRootPackageName, CodeGeneratorConfiguration config) {
		super(attributedElementClass, schemaRootPackageName,
				attributedElementClass.getPackageName(), config);
	}

	@Override
	protected CodeList createBody() {
		CodeList code = super.createBody();
		if (currentCycle.isImplementationVariant()) {
			// code.add(createConstructor());
			code.add(createFields(aec.getAttributeList()));
			code.add(createGenericGetter(aec.getAttributeList()));
			code.add(createGenericSetter(aec.getAttributeList()));
			code.add(createGettersAndSetters(aec.getAttributeList()));
		}
		if (currentCycle.isAbstract()) {
			code.add(createGettersAndSetters(aec.getOwnAttributeList()));
		}
		if (currentCycle.isProxies()) {
			code.add(createGettersAndSetters(aec.getAttributeList()));
		}
		return code;
	}

	/**
	 * @return true if at least one own or inherited attribute has a default
	 *         value.
	 */
	protected boolean hasDefaultAttributeValues() {
		for (Attribute attr : aec.getAttributeList()) {
			if (attr.getDefaultValueAsString() != null) {
				return true;
			}
		}
		return false;
	}


	protected CodeBlock createGenericGetter(Set<Attribute> attrSet) {
		CodeList code = new CodeList();
		addImports("#jgPackage#.NoSuchAttributeException");
		code.addNoIndent(new CodeSnippet(true,
				"public Object getAttribute(String attributeName) {"));
		for (Attribute attr : attrSet) {
			CodeSnippet s = new CodeSnippet();
			s.setVariable("name", attr.getName());
			s.setVariable("isOrGet",
					attr.getDomain().getJavaClassName(schemaRootPackageName)
							.equals("Boolean") ? "is" : "get");
			s.setVariable("cName", attr.getName());
			s.add("if (attributeName.equals(\"#name#\")) return #isOrGet#_#cName#();");
			code.add(s);
		}
		code.add(new CodeSnippet(
				"throw new NoSuchAttributeException(\"#qualifiedClassName# doesn't contain an attribute \" + attributeName);"));
		code.addNoIndent(new CodeSnippet("}"));

		return code;
	}

	protected CodeBlock createGenericSetter(Set<Attribute> attrSet) {
		CodeList code = new CodeList();
		addImports("#jgPackage#.NoSuchAttributeException");
		CodeSnippet snip = new CodeSnippet(true);
		boolean suppressWarningsNeeded = false;
		for (Attribute attr : attrSet) {
			if (attr.getDomain().isComposite()
					&& !(attr.getDomain() instanceof RecordDomain)) {
				suppressWarningsNeeded = true;
				break;
			}
		}
		if (suppressWarningsNeeded) {
			snip.add("@SuppressWarnings(\"unchecked\")");
		}
		snip.add("public void setAttribute(String attributeName, Object data) {");
		code.addNoIndent(snip);
		if (currentCycle.isImplementationVariant()) {
		for (Attribute attr : attrSet) {
			CodeSnippet s = new CodeSnippet();
			s.setVariable("name", attr.getName());

			if (attr.getDomain().isComposite()) {
				String implTypeName = attr.getDomain().getJavaAttributeImplementationTypeName(schemaRootPackageName);
				s.setVariable("attributeClassName", implTypeName);
			} else {
				s.setVariable("attributeClassName", attr.getDomain()
						.getJavaClassName(schemaRootPackageName));
			}
			
			boolean isEnumDomain = false;
			if (attr.getDomain() instanceof EnumDomain) {
				isEnumDomain = true;
			}

			if (isEnumDomain) {
				s.add("if (attributeName.equals(\"#name#\")) {");
				s.add("\tif (data instanceof String) {");
				s.add("\t\tset_#name#(#attributeClassName#.valueOfPermitNull((String) data));");
				s.add("\t} else {");
				s.add("\t\tset_#name#((#attributeClassName#) data);");
				s.add("\t}");
				s.add("\treturn;");
				s.add("}");
			} else {
				s.add("if (attributeName.equals(\"#name#\")) {");
				s.add("\tset_#name#((#attributeClassName#) data);");
				s.add("\treturn;");
				s.add("}");
			}
			code.add(s);
		}
		code.add(new CodeSnippet(
				"throw new NoSuchAttributeException(\"#qualifiedClassName# doesn't contain an attribute \" + attributeName);"));
		}		
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}


	protected CodeBlock createGettersAndSetters(Set<Attribute> attrSet) {
		CodeList code = new CodeList();
		for (Attribute attr : attrSet) {
			code.addNoIndent(createGetter(attr));
			code.addNoIndent(createSetter(attr));
		}
		return code;
	}
	
	
	protected abstract CodeBlock createGetter(Attribute attr);
	
	
	protected abstract CodeBlock createSetter(Attribute attr);
	
	
	protected abstract CodeBlock createFields(Set<Attribute> attrSet);
	
	
	protected CodeBlock createField(Attribute attr) {
		CodeSnippet code = new CodeSnippet(true, "protected #type# _#name#;");
		code.setVariable("name", attr.getName());
		code.setVariable("type", attr.getDomain().getJavaAttributeImplementationTypeName(schemaRootPackageName));
		return code;
	}
	
	
	protected CodeBlock createReadAttributesFromStringMethod(Set<Attribute> attrSet, String attributeContainer) {
		CodeList code = new CodeList();
		addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException",
				"#jgPackage#.NoSuchAttributeException");
		code.addNoIndent(new CodeSnippet(
				true,
				"public void readAttributeValueFromString(String attributeName, String value) throws GraphIOException {"));

		if (attrSet != null) {
			code.add(new CodeSnippet("try {"));
			for (Attribute attribute : attrSet) {
				CodeList a = new CodeList();
				a.setVariable("variableName", attribute.getName());
				a.setVariable("setterName", "set_" + attribute.getName());
				a.setVariable("attributeContainer", attributeContainer);
				a.setVariable("attrType", attribute.getDomain().getJavaAttributeImplementationTypeName(schemaRootPackageName));
				a.setVariable("nullValue", attribute.getDomain().getInitialValue());
				a.add(new CodeSnippet("#attrType# temp_#variableName# = #nullValue#;"));
				a.add(new CodeSnippet(
						"if (attributeName.equals(\"#variableName#\")) {",
						"\tGraphIO io = GraphIO.createStringReader(value, getSchema());"));
				a.add(attribute.getDomain().getReadMethod(
							schemaRootPackageName, "temp_" + attribute.getName(),
							"io", ""));
				a.addNoIndent(new CodeSnippet(
							"\t#setterName#(temp_#variableName#);", "\treturn;",
							"}"));
				code.add(a);
			}
			code.add(new CodeSnippet(
					"} catch (Exception ex) {" +
					"\tthrow new RuntimeException(ex);" +
					"}"));
		}
		code.add(new CodeSnippet(
				"throw new NoSuchAttributeException(\"#qualifiedClassName# doesn't contain an attribute \" + attributeName);"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	/**
	 * 
	 * @param attrSet
	 * @return
	 */
	protected CodeBlock createWriteAttributeToStringMethod(Set<Attribute> attrSet, String attributeContainer) {
		CodeList code = new CodeList();
		addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException",
				"#jgPackage#.NoSuchAttributeException");
		code.addNoIndent(new CodeSnippet(
				true,
				"public String writeAttributeValueToString(String attributeName) throws IOException, GraphIOException {"));
		if (attrSet != null) {
			for (Attribute attribute : attrSet) {
				CodeList a = new CodeList();
				a.setVariable("variableName", attribute.getName());
				a.setVariable("setterName", "set_" + attribute.getName());
				a.addNoIndent(new CodeSnippet(
						"if (attributeName.equals(\"#variableName#\")) {",
						"\tGraphIO io = GraphIO.createStringWriter(getSchema());"));
				a.add(attribute.getDomain().getWriteMethod(
							schemaRootPackageName, "_" + attribute.getName(),
							"io", attributeContainer));
				a.addNoIndent(new CodeSnippet(
						"\treturn io.getStringWriterResult();", "}"));
				code.add(a);
			}
		}
		code.add(new CodeSnippet(
				"throw new NoSuchAttributeException(\"#qualifiedClassName# doesn't contain an attribute \" + attributeName);"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	protected CodeBlock createReadAttributesMethod(SortedSet<Attribute> attrSet, String attributeContainer) {
		CodeList code = new CodeList();

		addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException");

		code.addNoIndent(new CodeSnippet(true,
				"public void readAttributeValues(GraphIO io) throws GraphIOException {"));
		if (attrSet != null) {
			code.add(new CodeSnippet("try {"));
			for (Attribute attribute : attrSet) {
				CodeSnippet snippet = new CodeSnippet();
				snippet.setVariable("setterName", "set_" + attribute.getName());
				snippet.setVariable("variableName", attribute.getName());
				snippet.setVariable("attributeContainer", attributeContainer);
				if (currentCycle.isImplementationVariant()) {
					code.add(attribute.getDomain().getReadMethod(
							schemaRootPackageName, "_" + attribute.getName(),
							"io", attributeContainer));
				}
				snippet.add("#setterName#(#attributeContainer#_#variableName#);");
				code.add(snippet);
			}
			code.add(new CodeSnippet(true,
					"} catch (Exception ex) {" +
					"\tthrow new RuntimeException(ex);" +
					"}"));
		}
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}

	protected CodeBlock createWriteAttributesMethod(Set<Attribute> attrSet, String attributeContainer) {
		CodeList code = new CodeList();

		addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException",
				"java.io.IOException");

		code.addNoIndent(new CodeSnippet(
				true,
				"public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {"));
		if ((attrSet != null) && !attrSet.isEmpty()) {
			code.add(new CodeSnippet("io.space();"));
			for (Attribute attribute : attrSet) {
				if (currentCycle.isImplementationVariant()) {
					code.add(attribute.getDomain().getWriteMethod(
							schemaRootPackageName, "_" + attribute.getName(),
							"io", attributeContainer));
				}
			}
		}
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}


}
