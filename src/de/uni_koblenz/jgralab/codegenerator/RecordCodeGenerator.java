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

import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;

/**
 * TODO add comment
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class RecordCodeGenerator extends CodeGenerator {

	/**
	 * The RecordDomain to create code for
	 */
	protected RecordDomain recordDomain;

	/**
	 * Creates a new RecordCodeGenerator which creates code for the given
	 * recordDomain object
	 */
	public RecordCodeGenerator(RecordDomain recordDomain,
			String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(schemaPackageName, recordDomain.getPackageName(), config);
		rootBlock.setVariable("simpleClassName", recordDomain.getSimpleName());
		rootBlock.setVariable("simpleImplClassName", recordDomain
				.getSimpleName()
				+ "Impl");
		rootBlock.setVariable("theGraph", "graph");
		this.recordDomain = recordDomain;
	}

	@Override
	protected CodeBlock createBody() {
		CodeList code = new CodeList();
		code.add(createRecordComponents());
		code.add(createGetterMethods());
		code.add(createSetterMethods());
		code.add(createGenericSetter());
		code.add(createGenericGetter());
		code.add(createVariableParametersSetter());
		code.add(createMapSetter());
		// code.add(createVariableParametersConstructor());
		code.add(createDefaultConstructor());
		code.add(createFieldConstructor());
		// code.add(createMapConstructor());
		code.add(createToStringMethod());
		code.add(createReadComponentsMethod());
		code.add(createWriteComponentsMethod());
		code.add(createEqualsMethod());
		return code;
	}

	/**
	 * Default constructor needed for transaction support.
	 * 
	 * @return
	 */
	private CodeBlock createDefaultConstructor() {
		CodeList code = new CodeList();
		if (!currentCycle.isAbstract()) {
			CodeSnippet header = null;
			header = new CodeSnippet(true,
					"public #simpleImplClassName#(Graph g) {");
			code.addNoIndent(header);
			if (hasCompositeRecordComponent()) {
				code.add(new CodeSnippet("#theGraph# = g;"));
			}
			code.addNoIndent(new CodeSnippet("}"));
		}
		return code;
	}

	private CodeBlock createFieldConstructor() {
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			StringBuilder sb = new StringBuilder();
			CodeSnippet header = null;
			header = new CodeSnippet(true,
					"protected #simpleImplClassName#(Graph g, #fields#) {");

			code.addNoIndent(header);
			if (hasCompositeRecordComponent()) {
				code.add(new CodeSnippet("#theGraph# = g;"));
			}

			String delim = "";
			for (RecordComponent rdc : recordDomain.getComponents()) {
				sb.append(delim);
				delim = ", ";
				sb.append(rdc.getDomain()
						.getJavaAttributeImplementationTypeName(
								schemaRootPackageName));
				sb.append(" _");
				sb.append(rdc.getName());

				CodeBlock assign = new CodeSnippet("this._#name# = _#name#;");

				assign.setVariable("name", rdc.getName());
				code.add(assign);
			}
			header.setVariable("fields", sb.toString());
			code.addNoIndent(new CodeSnippet("}"));
		}
		return code;
	}

	/**
	 * 
	 * 
	 * @param createClass
	 * @return
	 */
	private CodeBlock createVariableParametersSetter() {
		CodeList code = new CodeList();

		if (currentCycle.isMemOrDiskImpl()) {
			CodeSnippet codeSnippet = new CodeSnippet(true);

			if (hasCompositeRecordComponent()) {
				codeSnippet.add("@SuppressWarnings(\"unchecked\")");
			}

			codeSnippet.add("@Override");
			codeSnippet
					.add("public void setComponentValues(Object... components) {");

			code.addNoIndent(codeSnippet);

			int count = 0;
			for (RecordComponent rdc : recordDomain.getComponents()) {

				CodeSnippet assign = new CodeSnippet(
							"\tthis._#name# = (#type#) components[#index#];");


				assign.setVariable("name", rdc.getName());
				assign.setVariable("type", rdc.getDomain().getJavaClassName(
						schemaRootPackageName));
				assign.setVariable("index", Integer.valueOf(count).toString());
				code.addNoIndent(assign);
				count++;
			}
			code.addNoIndent(new CodeSnippet("}"));
		}
		return code;
	}


	/**
	 * 
	 * @param createClass
	 * @return
	 */
	private CodeBlock createEqualsMethod() {
		CodeList code = new CodeList();
		if (currentCycle.isAbstract()) {
			return code;
		}
		code.addNoIndent(new CodeSnippet(true,
				"public boolean equals(Object o) {"));
		code.add(new CodeSnippet("if(o == null)", "\treturn false;"));
		if (currentCycle.isMemOrDiskImpl()) {
			code.add(new CodeSnippet(
					"if(!(o instanceof #simpleImplClassName#))",
					"\treturn false;"));
			code.add(new CodeSnippet(
							"#simpleImplClassName# record = (#simpleImplClassName#) o;"));
		}

		CodeSnippet codeSnippet = null;
		for (RecordComponent entry : recordDomain.getComponents()) {
			switch (currentCycle) {
			case MEMORYBASED:
				codeSnippet = new CodeSnippet(true);
				if (entry.getDomain().isComposite()) {
					codeSnippet.add("\tif(!(_#name#.equals(record._#name#)))");
					codeSnippet.add("\t\treturn false;");
				} else {
					codeSnippet.add("\tif(_#name# != record._#name#)");
					codeSnippet.add("\t\treturn false;");
				}
				code.addNoIndent(codeSnippet);
				codeSnippet.setVariable("name", entry.getName());
				break;
			}
		}
		code.add(new CodeSnippet("\n\t\treturn true;"));
		code.addNoIndent(new CodeSnippet("}\n"));
		return code;
	}



	@Override
	protected CodeBlock createHeader() {
		CodeSnippet code = null;
		if (currentCycle.isMemOrDiskImpl()) {
			addImports("de.uni_koblenz.jgralab.NoSuchAttributeException");
		}
		switch (currentCycle) {
		case ABSTRACT:
			code = new CodeSnippet(
					true,
					"public abstract class #simpleClassName# implements de.uni_koblenz.jgralab.Record {");
			break;
		case DISKBASED:	
		case MEMORYBASED:
			addImports("#jgPackage#.Graph");
			addImports("#schemaPackage#.#simpleClassName#");
			code = new CodeSnippet(true,
					"public class #simpleImplClassName# extends #simpleClassName# {");
			// only needed in std/savemem when composite domains are used.
			if (hasCompositeRecordComponent()) {
				code.add("\tprivate Graph #theGraph#;");
			}
			break;
		}
		return code;
	}

	/**
	 * Getter-methods for fields needed for transaction support.
	 * 
	 * @return
	 */
	protected CodeBlock createGetterMethods() {
		CodeList code = new CodeList();
		for (RecordComponent rdc : recordDomain.getComponents()) {
			CodeSnippet getterCode = new CodeSnippet(true);
			getterCode.setVariable("name", rdc.getName());
			getterCode.setVariable("isOrGet", rdc.getDomain().getJavaClassName(
					schemaRootPackageName).equals("Boolean") ? "is" : "get");
			getterCode.setVariable("type", rdc.getDomain()
					.getJavaAttributeImplementationTypeName(
							schemaRootPackageName));

			switch (currentCycle) {
			case ABSTRACT:
				getterCode.add("public abstract #type# #isOrGet#_#name#();");
				break;
			case MEMORYBASED:
				getterCode.setVariable("ctype", rdc.getDomain()
						.getJavaAttributeImplementationTypeName(
								schemaRootPackageName));
				getterCode.add("public #type# #isOrGet#_#name#() {");
				getterCode.add("\treturn _#name#;");
				getterCode.add("}");
				break;
			}
			code.addNoIndent(getterCode);
		}
		return code;
	}

	/**
	 * Setter-methods for fields needed for transaction support.
	 * 
	 * @return
	 */
	protected CodeBlock createSetterMethods() {
		CodeList code = new CodeList();
		for (RecordComponent rdc : recordDomain.getComponents()) {
			CodeSnippet setterCode = new CodeSnippet(true);
			setterCode.setVariable("name", rdc.getName());
			setterCode.setVariable("setter", "set_" + rdc.getName()
					+ "(#type# _#name#)");
			setterCode.setVariable("type", rdc.getDomain()
					.getJavaAttributeImplementationTypeName(
							schemaRootPackageName));

			switch (currentCycle) {
			case ABSTRACT:
				setterCode.add("public abstract void #setter#;");
				break;
			case MEMORYBASED:
				setterCode.setVariable("ctype", rdc.getDomain()
						.getJavaAttributeImplementationTypeName(
								schemaRootPackageName));
				setterCode.add("public void #setter# {");
				setterCode.add("\tthis._#name# = (#ctype#) _#name#;");
				setterCode.add("}");
				break;
			}
			code.addNoIndent(setterCode);
		}
		return code;
	}

	private CodeBlock createMapSetter() {
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			// suppress "unchecked" warnings if this record domain contains a
			// Collection domain (Set<E>, List<E>, Map<K, V>)
			for (RecordComponent comp : recordDomain.getComponents()) {
				Domain d = comp.getDomain();
				if (d.isComposite() && !(d instanceof RecordDomain)) {
					code.addNoIndent(new CodeSnippet(true,
							"@SuppressWarnings(\"unchecked\")"));
					break;
				}
			}

			code.addNoIndent(new CodeSnippet(false, "@Override"));
			code.addNoIndent(new CodeSnippet(false,
							"public void setComponentValues(java.util.Map<String, Object> fields) {"));

			for (RecordComponent rdc : recordDomain.getComponents()) {
				CodeBlock assign = new CodeSnippet(
							"this._#name# = (#cname#)fields.get(\"#name#\");");

				assign.setVariable("name", rdc.getName());
				assign.setVariable("cname", rdc.getDomain().getJavaClassName(
						schemaRootPackageName));
				code.add(assign);
			}
			code.addNoIndent(new CodeSnippet("}"));
		}
		return code;
	}

	private CodeBlock createGenericSetter() {
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			// suppress "unchecked" warnings if this record domain contains a
			// Collection domain (Set<E>, List<E>, Map<K, V>)
			for (RecordComponent comp : recordDomain.getComponents()) {
				Domain d = comp.getDomain();
				if (d.isComposite() && !(d instanceof RecordDomain)) {
					code.addNoIndent(new CodeSnippet(true,
							"@SuppressWarnings(\"unchecked\")"));
					break;
				}
			}

			code.addNoIndent(new CodeSnippet(false, "@Override"));
			code.addNoIndent(new CodeSnippet(false,
					"public void setComponent(String name, Object value) {"));

			for (RecordComponent rdc : recordDomain.getComponents()) {
				CodeBlock assign = new CodeSnippet("if (name.equals(\"#name#\")) {",
							"\tthis._#name# = (#cname#)value;", "\treturn;",
							"}");

				assign.setVariable("name", rdc.getName());
				assign.setVariable("cname", rdc.getDomain().getJavaClassName(
						schemaRootPackageName));
				code.add(assign);
			}
			code
					.add(new CodeSnippet(
							"throw new NoSuchAttributeException(\"#rcname# doesn't contain an attribute \" + name);"));
			code.addNoIndent(new CodeSnippet("}"));
			code.setVariable("rcname", recordDomain.getQualifiedName());
		}
		return code;
	}

	private CodeBlock createGenericGetter() {
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			code.addNoIndent(new CodeSnippet(false, "@Override"));
			code.addNoIndent(new CodeSnippet(false,
					"public Object getComponent(String name) {"));

			for (RecordComponent rdc : recordDomain.getComponents()) {
				CodeBlock assign  = new CodeSnippet("if (name.equals(\"#name#\")) {",
							"\treturn this._#name#;", "}");

				assign.setVariable("name", rdc.getName());
				assign.setVariable("cname", rdc.getDomain().getJavaClassName(
						schemaRootPackageName));
				assign.setVariable("isOrGet", rdc.getDomain() == rdc
						.getDomain().getSchema().getBooleanDomain() ? "is"
						: "get");
				code.add(assign);
			}
			code
					.add(new CodeSnippet(
							"throw new NoSuchAttributeException(\"#rcname# doesn't contain an attribute \" + name);"));
			code.addNoIndent(new CodeSnippet("}"));
			code.setVariable("rcname", recordDomain.getQualifiedName());
		}
		return code;
	}

	private CodeBlock createReadComponentsMethod() {
		CodeList code = new CodeList();
		// abstract class (or better use interface?)
		if (currentCycle.isMemOrDiskImpl()) {
			addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException");
			code.addNoIndent(new CodeSnippet("@Override"));
			code
					.addNoIndent(new CodeSnippet(true,
							"public void readComponentValues(GraphIO io) throws GraphIOException {"));

			code.add(new CodeSnippet("io.match(\"(\");"));
			for (RecordComponent c : recordDomain.getComponents()) {
				code.add(c.getDomain().getReadMethod(schemaRootPackageName,
							"_" + c.getName(), "io", ""));
			}
			code.add(new CodeSnippet("io.match(\")\");"));
			code.addNoIndent(new CodeSnippet("}"));
		}
		return code;
	}

	private CodeBlock createWriteComponentsMethod() {
		CodeList code = new CodeList();
		if (currentCycle.isAbstract()) {
			addImports("#jgPackage#.GraphIO", "#jgPackage#.GraphIOException",
					"java.io.IOException");
			code.addNoIndent(new CodeSnippet(false, "@Override"));
			code.addNoIndent(new CodeSnippet(
							true,
							"public void writeComponentValues(GraphIO io) throws IOException, GraphIOException {",
							"\tio.writeSpace();", "\tio.write(\"(\");",
							"\tio.noSpace();"));
			for (RecordComponent c : recordDomain.getComponents()) {
				String isOrGet = c.getDomain() instanceof BooleanDomain ? "is"
						: "get";
				code.add(c.getDomain().getWriteMethod(schemaRootPackageName,
						isOrGet + "_" + c.getName() + "()", "io",""));
			}
			code.addNoIndent(new CodeSnippet("\tio.write(\")\");", "}"));
		}
		return code;
	}

	private CodeBlock createRecordComponents() {
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			for (RecordComponent rdc : recordDomain.getComponents()) {
				Domain dom = rdc.getDomain();
				CodeSnippet s = new CodeSnippet(true,
						"private #type# _#field#;");
				s.setVariable("field", rdc.getName());
				s.setVariable("type", dom.getJavaAttributeImplementationTypeName(schemaRootPackageName));
				code.addNoIndent(s);
			}
		}
		return code;
	}

	/**
	 * Creates the toString()-method for this record domain
	 */
	private CodeBlock createToStringMethod() {
		CodeList code = new CodeList();

		if (currentCycle.isAbstract()) {
			code.addNoIndent(new CodeSnippet(true,
					"public String toString() {",
					"\tStringBuilder sb = new StringBuilder();"));
			String delim = "[";
			for (RecordComponent c : recordDomain.getComponents()) {
				CodeSnippet s = new CodeSnippet("sb.append(\"#delim#\");",
						"sb.append(\"#key#\");", "sb.append(\"=\");",
						"sb.append(#isOrGet#_#key#()#toString#);");
				Domain domain = c.getDomain();
				s
						.setVariable("isOrGet", domain.getJavaClassName(
								schemaRootPackageName).equals("Boolean") ? "is"
								: "get");
				s.setVariable("delim", delim);
				s.setVariable("key", c.getName());
				s.setVariable("toString", domain.isComposite() ? ".toString()"
						: "");
				code.add(s);
				delim = ", ";
			}
			code.addNoIndent(new CodeSnippet("\tsb.append(\"]\");",
					"\treturn sb.toString();", "}"));
		}
		return code;
	}



	/**
	 * 
	 * @return
	 */
	private boolean hasCompositeRecordComponent() {
		for (RecordComponent comp : recordDomain.getComponents()) {
			if (comp.getDomain().isComposite()) {
				return true;
			}
		}
		return false;
	}

}
