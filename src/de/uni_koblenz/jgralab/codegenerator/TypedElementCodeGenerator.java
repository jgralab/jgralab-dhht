package de.uni_koblenz.jgralab.codegenerator;

import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.schema.IncidenceClass;
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
		rootBlock.setVariable("ecName", aec.getSimpleName());
		rootBlock.setVariable("qualifiedClassName", aec.getQualifiedName());
		rootBlock.setVariable("schemaName", aec.getSchema().getName());
		rootBlock.setVariable("schemaVariableName", aec.getVariableName());
		rootBlock.setVariable("javaClassName", schemaRootPackageName + "."
				+ aec.getQualifiedName());
		rootBlock.setVariable("qualifiedImplClassName", schemaRootPackageName
				+ ".impl." + "std"
				+ aec.getQualifiedName() + "Impl");
		rootBlock.setVariable("simpleClassName", aec.getSimpleName());
		rootBlock.setVariable("simpleImplClassName", aec.getSimpleName() + "Impl");
		rootBlock.setVariable("uniqueClassName", aec.getUniqueName());
		rootBlock.setVariable("schemaPackageName", schemaRootPackageName);
		rootBlock.setVariable("theGraph", "graph");
		

		rootBlock.setVariable("isAbstractClass", aec.isAbstract() ? "true"
				: "false");
		interfaces = new TreeSet<String>();
		interfaces.add(aec.getQualifiedName());
		for (TypedElementClass<?,?> superClass : aec.getDirectSuperClasses()) {
			if (!(IncidenceClass.class.isInstance(aec) && superClass.isInternal()))
				interfaces.add(superClass.getQualifiedName());
		}
	}
	
	/**
	 * Returns the absolute name of the given AttributdelementClass. The name is
	 * composed of the package-prefix of the schema the class belongs to and the
	 * qualified name of the class
	 * 
	 * @param ,Classmet
	 * @return
	 */
	protected String absoluteName(TypedElementClass<?,?> metaClass) {
		return schemaRootPackageName + "." +  metaClass.getQualifiedName();
	}

	@Override
	protected CodeList createBody() {
		if (currentCycle.isMemOrDiskImpl()) {
			addImports("#usedJgImplPackage#.#baseClassName#");
		}
		CodeList code = new CodeList();
		if (currentCycle.isMemOrDiskImpl()) {
			code.add(createGetTypeMethod());
			code.add(createConstructor());
			code.add(createGetM1ClassMethod());
		} else if (currentCycle.isProxies()) {
			code.add(createConstructor());
		}
		return code;
	}

	@Override
	protected CodeBlock createHeader() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("classOrInterface", currentCycle.isMemOrDiskImpl() || currentCycle.isProxies() ? " class" : " interface");
		code.setVariable("interfaceOrImplClass", currentCycle.isMemOrDiskImpl() ? rootBlock.getVariable("simpleImplClassName") 
																: (rootBlock.getVariable("simpleClassName") + (currentCycle.isProxies() ? "Proxy" : "")));
		code.setVariable("abstract", currentCycle.isMemOrDiskImpl()  && aec.isAbstract() ? " abstract" : "");
		code.add("public#abstract##classOrInterface# #interfaceOrImplClass##extends##implements# {");
		code.setVariable("extends",	" ");
		if (currentCycle.isMemOrDiskImpl()) {
			code.setVariable("extends",	" extends #baseClassName#");
		} else if (currentCycle.isProxies()) {
			code.setVariable("extends",	" extends #proxyClassName#");
		}
		StringBuffer buf = new StringBuffer();
		if (interfaces.size() > 0) {
			String delim = currentCycle.isMemOrDiskImpl() || currentCycle.isProxies() ? " implements " : " extends ";
			for (String interfaceName : interfaces) {
				if (currentCycle.isMemOrDiskImpl()
						|| !interfaceName.equals(aec.getQualifiedName())) {
					if (interfaceName.equals("Vertex")
							|| interfaceName.equals("Edge")
							|| interfaceName.equals("BinaryEdge")
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
	
	protected CodeBlock createGetTypeMethod() {
		return new CodeSnippet(
				true,
				"public final #jgSchemaPackage#.#graphElementClass#Class getType() {",
				"\treturn #schemaPackageName#.#schemaName#.instance().#schemaVariableName#;",
				"}");
	}

	
	protected CodeBlock createGetM1ClassMethod() {
		return new CodeSnippet(
				true,
				"public final java.lang.Class<? extends #jgPackage#.#graphElementClass#> getM1Class() {",
				"\treturn #javaClassName#.class;", "}");
	}

	
	protected CodeBlock createGetM1ImplementationClassMethod() {
		return new CodeSnippet(
				true,
				"public final java.lang.Class<? extends #jgPackage#.#graphElementClass#> getM1ImplementationClass() {",
				"\treturn #javaImplClassName#.class;", "}");
	}


}