package de.uni_koblenz.jgralab.codegenerator;

import java.util.SortedSet;

import de.uni_koblenz.jgralab.schema.TypedElementClass;

public abstract class TypedElementCodeGenerator<ConcreteMetaClass extends TypedElementClass<ConcreteMetaClass, ?>>
		extends CodeGenerator {

	/**
	 * all the interfaces of the class which are being implemented
	 */
	protected SortedSet<String> interfaces;
	/**
	 * the AttributedElementClass to generate code for
	 */
	protected ConcreteMetaClass aec;

	public TypedElementCodeGenerator(ConcreteMetaClass metaClass, String schemaRootPackageName,
			String packageName, CodeGeneratorConfiguration config) {
		super(schemaRootPackageName, packageName, config);
		this.aec = metaClass;
	}

	@Override
	protected CodeList createBody() {
		CodeList code = new CodeList();
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add(createConstructor());
			code.add(createGetM1ClassMethod());
		}
		return code;
	}

	@Override
	protected CodeBlock createHeader() {
		CodeSnippet code = new CodeSnippet(true);
	
		code.setVariable("classOrInterface", currentCycle
				.isStdOrSaveMemOrDbImplOrTransImpl() ? " class" : " interface");
		code.setVariable("abstract", currentCycle
				.isStdOrSaveMemOrDbImplOrTransImpl()
				&& aec.isAbstract() ? " abstract" : "");
		code.setVariable("impl", currentCycle
				.isStdOrSaveMemOrDbImplOrTransImpl()
				&& !aec.isAbstract() ? "Impl" : "");
		code.add("public#abstract##classOrInterface# #simpleClassName##impl##extends##implements# {");
		code.setVariable(
						"extends",
						currentCycle.isStdOrSaveMemOrDbImplOrTransImpl() ? " extends #baseClassName#"
								: "");
	
		StringBuffer buf = new StringBuffer();
		if (interfaces.size() > 0) {
			String delim = currentCycle.isStdOrSaveMemOrDbImplOrTransImpl() ? " implements "
					: " extends ";
			for (String interfaceName : interfaces) {
				if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()
						|| !interfaceName.equals(aec.getQualifiedName())) {
					if (interfaceName.equals("Vertex")
							|| interfaceName.equals("Edge")
							|| interfaceName.equals("Graph")
							|| interfaceName.equals("Incidence")) {
						buf.append(delim);
						buf.append("#jgPackage#." + interfaceName);
						delim = ", ";
					} else {
						buf.append(delim);
						buf.append(schemaRootPackageName + "." + interfaceName);
						delim = ", ";
					}
				}
			}
		}
		code.setVariable("implements", buf.toString());
		return code;
	}

	protected CodeBlock createStaticImplementationClassField() {
		return new CodeSnippet(
				true,
				"/**",
				" * refers to the default implementation class of this interface",
				" */",
				"public static final java.lang.Class<#qualifiedImplClassName#> IMPLEMENTATION_CLASS = #qualifiedImplClassName#.class;");
	}

	protected abstract CodeBlock createConstructor();

	protected CodeBlock createSpecialConstructorCode() {
		return null;
	}
	
	
	
	protected CodeBlock createGetM1ClassMethod() {
		return new CodeSnippet(
				true,
				"public final java.lang.Class<? extends #jgPackage#.AttributedElement> getM1Class() {",
				"\treturn #javaClassName#.class;", "}");
	}



}