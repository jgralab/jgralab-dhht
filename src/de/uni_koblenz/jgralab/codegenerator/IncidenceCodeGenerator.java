package de.uni_koblenz.jgralab.codegenerator;


import java.util.TreeSet;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

public class IncidenceCodeGenerator extends TypedElementCodeGenerator<IncidenceClass> {

	
	public IncidenceCodeGenerator(IncidenceClass metaClass, String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(metaClass,schemaPackageName, metaClass.getPackageName(), config);
		rootBlock.setVariable("connectedVertexClass", absoluteName(metaClass.getVertexClass()));
		rootBlock.setVariable("connectedEdgeClass", absoluteName(metaClass.getEdgeClass()));
		rootBlock.setVariable("baseClassName", "IncidenceImpl");
		rootBlock.setVariable("proxyClassName", "IncidenceProxy");
		rootBlock.setVariable("graphElementClass", "Incidence");
		rootBlock.setVariable("ownElementClass", "Incidence");
	//	System.out.println("Create incidence class code " + metaClass.getFileName());
		interfaces.add("Incidence");
	}

	
	protected boolean hasProxySupport() {
		return true;
	}
	
	@Override
	protected CodeBlock createHeader() {
		CodeList code = new CodeList();
		CodeSnippet snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add(" * Connects #inClass# (#inMin#, #inMax#) with #outClass# (#outMin#,#outMax#)");
		snippet.add(" */");
		if (aec.getDirection() == Direction.VERTEX_TO_EDGE) {
			snippet.setVariable("inClass", aec.getVertexClass().getQualifiedName());
			snippet.setVariable("inMin", Integer.toString(aec.getMinVerticesAtEdge()));
			snippet.setVariable("inMax", Integer.toString(aec.getMaxVerticesAtEdge()));
			snippet.setVariable("outClass", aec.getEdgeClass().getQualifiedName());
			snippet.setVariable("outMin", Integer.toString(aec.getMinEdgesAtVertex()));
			snippet.setVariable("outMax", Integer.toString(aec.getMaxEdgesAtVertex()));
		} else {
			snippet.setVariable("inClass", aec.getEdgeClass().getQualifiedName());
			snippet.setVariable("inMin", Integer.toString(aec.getMinEdgesAtVertex()));
			snippet.setVariable("inMax", Integer.toString(aec.getMaxEdgesAtVertex()));
			snippet.setVariable("outClass", aec.getVertexClass().getQualifiedName());
			snippet.setVariable("outMin", Integer.toString(aec.getMinVerticesAtEdge()));
			snippet.setVariable("outMax", Integer.toString(aec.getMaxVerticesAtEdge()));
		}

		code.addNoIndent(snippet);
		code.addNoIndent(super.createHeader());
		return code;
	}
	
	
	@Override
	protected CodeBlock createConstructor() {
		addImports("#usedJgImplPackage#.VertexImpl");
		addImports("#usedJgImplPackage#.EdgeImpl");
		addImports("#jgPackage#.Edge");
		addImports("#jgPackage#.Vertex");

		switch (currentCycle) {
		case MEMORYBASED:
			return createInMemoryConstructor();
		case DISKBASED:	
			return createDiskBasedConstructor();
		case DISKV2BASED:	
			return createDiskv2BasedConstructor();
		case DISKPROXIES:	
			addImports("#jgDiskImplPackage#.IncidenceProxy");
			addImports("#jgDiskImplPackage#.GraphDatabaseBaseImpl");
			addImports("#jgImplPackage#.RemoteGraphDatabaseAccess");
			return createProxyConstructor();
		case DISKV2PROXIES:
			addImports("#jgDiskv2ImplPackage#.IncidenceProxy");
			addImports("#jgDiskv2ImplPackage#.GraphDatabaseBaseImpl");
			addImports("#jgImplPackage#.RemoteGraphDatabaseAccess");
			return createProxyConstructor();
		case DISTRIBUTEDPROXIES:	
			addImports("#jgDistributedImplPackage#.IncidenceProxy");
			addImports("#jgDistributedImplPackage#.GraphDatabaseBaseImpl");
			addImports("#jgImplPackage#.RemoteGraphDatabaseAccess");
			return createProxyConstructor();
		case DISTRIBUTED:
			return createDistributedConstructor();
		default:
			throw new RuntimeException("Unhandled case");
		}
	}
	
	private CodeBlock createInMemoryConstructor() {
		CodeList code = new CodeList();
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(long id, Vertex vertex, Edge edge) {",
						"\tsuper(id, (VertexImpl)vertex, (EdgeImpl)edge);",
						"}"));
		return code;
	}

	private CodeBlock createProxyConstructor() {
		CodeList code = new CodeList();
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Proxy(long globalId, GraphDatabaseBaseImpl localGraphDatabase,	RemoteGraphDatabaseAccess storingGraphDatabase) {",
						"\tsuper(globalId, localGraphDatabase, storingGraphDatabase);",
						"}"));
		return code;
	}
	
	
	private CodeBlock createDistributedConstructor() {
		CodeList code = new CodeList();
		addImports("#jgDistributedImplPackage#.GraphDatabaseBaseImpl");
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(long globalId, GraphDatabaseBaseImpl localGraphDatabase, long vertexId, long edgeId) {",
						"\tsuper(globalId,localGraphDatabase, vertexId, edgeId);"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}
	
	
	private CodeBlock createDiskBasedConstructor() {
		CodeList code = new CodeList();
		addImports("#jgDiskImplPackage#.GraphDatabaseBaseImpl");	
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(long globalId, GraphDatabaseBaseImpl localGraphDatabase, long vertexId, long edgeId) {",
						"\tsuper(globalId,localGraphDatabase, vertexId, edgeId);"));
		code.addNoIndent(new CodeSnippet("}"));
		code.addNoIndent(new CodeSnippet("/** Constructor only to be used by Background-Storage backend */"));
		code.addNoIndent(new CodeSnippet(
			true,
			"public #simpleClassName#Impl(long globalId, GraphDatabaseBaseImpl localGraphDatabase, #jgDiskImplPackage#.IncidenceContainer container) {",
			"\tsuper(globalId, localGraphDatabase, container);",
			"}"));
		return code;
	}
	
	private CodeBlock createDiskv2BasedConstructor() {
		CodeList code = new CodeList();
		addImports("#jgDiskv2ImplPackage#.GraphDatabaseBaseImpl");
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(long globalId, GraphDatabaseBaseImpl localGraphDatabase, long vertexId, long edgeId) {",
						"\tsuper(globalId,localGraphDatabase, vertexId, edgeId);"));
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}
	
	
	
	/**
	 * creates the body of the class file, that are methods and attributes
	 */
	@Override
	protected CodeList createBody() {
		CodeList code = (CodeList) super.createBody();

		if (config.hasTypeSpecificMethodsSupport() && !currentCycle.isClassOnly()) {
			code.add(createNextMethods());
			code.addNoIndent(createTypesafeGetEdgeAndVertexMethod());
		}
		code.add(createGetDirectionMethod());
		return code;
	}

	
	
	
	/**
	 * Creates <code>getNext#ownElementClass#ClassName()</code> methods
	 * @return the CodeBlock that contains the methods
	 */
	private CodeBlock createNextMethods() {
		CodeList code = new CodeList();

		TreeSet<IncidenceClass> superClasses = new TreeSet<IncidenceClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		if (config.hasTypeSpecificMethodsSupport()) {
			for (IncidenceClass sc : superClasses) {
				if (sc.isInternal()) {
					continue;
				}
				code.addNoIndent(createNextMethod(sc, true, false));
				code.addNoIndent(createNextMethod(sc, false, false));
				if (config.hasMethodsForSubclassesSupport()) {
					if (!sc.isAbstract()) {
						code.addNoIndent(createNextMethod(sc, true, true));
						code.addNoIndent(createNextMethod(sc, false, true));
					}
				}
			}
		}
		return code;
	}
	
	
	/**
	 * Creates <code>getNext#ownElementClass#ClassName()</code> method for given
	 * #ownElementClass#
	 * 
	 * @param createClass
	 *            if set to true, the method bodies will also be created
	 * @param atVertex 
	 *            toggles if "getNextXYIncidenceAtVertex" or "getNextXYIncidenceAtEdge"
	 *            methods should be created
	 * @param withTypeFlag
	 *            toggles if the "no subclasses"-parameter will be created
	 * @return the CodeBlock that contains the method
	 */
	private CodeBlock createNextMethod(IncidenceClass mc, boolean atVertex, boolean withTypeFlag) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("mcQualifiedName", mc.getQualifiedName());
		code.setVariable("mcFileName", absoluteName(mc));
		code.setVariable("mcCamelName", camelCase(mc.getRolename()));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"	: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses" : ""));
		code.setVariable("connectedElement", atVertex ? "Vertex" : "Edge" );

		if (currentCycle.isAbstract()) {
			code.add("/**",
					 " * @return the next #mcQualifiedName# incidence in the Lambda-sequence of the #connectedElement#");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #mcName# are accepted");
			}
			code.add(" */",
					 "public #mcFileName# getNext#mcCamelName#At#connectedElement#(#formalParams#);");
		}
		if (currentCycle.isImplementationVariant()  || currentCycle.isProxies() ) {
			code.add("@Override",
					 "public #mcFileName# getNext#mcCamelName#At#connectedElement#(#formalParams#) {",
					 "\treturn (#mcFileName#)getNextIncidenceAt#connectedElement#(#mcFileName#.class#actualParams#);",
					 "}");
		}
		return code;
	}


	private CodeBlock createGetDirectionMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("direction", aec.getDirection().toString());
		addImports("#jgPackage#.Direction");
		if (currentCycle.isImplementationVariant()  || currentCycle.isProxies() ) {
			code.add("@Override",
					 "public Direction getDirection() {",
					 "\treturn Direction.#direction#;",
					 "}");
		}
		return code;
	}
	
	private CodeBlock createTypesafeGetEdgeAndVertexMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("edgeClass", aec.getSchema().getPackagePrefix() + "." + aec.getEdgeClass().getQualifiedName());
		code.setVariable("vertexClass", aec.getSchema().getPackagePrefix() + "." + aec.getVertexClass().getQualifiedName());
		if (currentCycle.isAbstract()) {
			code.add(
					"/* Provided for type safety */",
					"@Override",
					"public #edgeClass# getEdge();",
					"",
					"",
					"/* Provided for type safety */",
					"@Override",
					"public #vertexClass# getVertex();");
		} else {
			code.add(
					"/* Provided for type safety */",
					"@Override",
					"public #edgeClass# getEdge() {",
					"\treturn (#edgeClass#) super.getEdge();",
					"}",
					"",
					"",
					"/* Provided for type safety */",
					"@Override",
					"public #vertexClass# getVertex() {",
					"\treturn (#vertexClass#) super.getVertex();",
					"}"
					);	
		}

		return code;
	}
	
	
}
