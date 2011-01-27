package de.uni_koblenz.jgralab.codegenerator;


import java.util.TreeSet;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.GraphElementClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;

public class GraphElementCodeGenerator<MetaClass extends GraphElementClass<MetaClass, ?>> extends AttributedElementCodeGenerator<MetaClass> {


	protected RolenameCodeGenerator<MetaClass> rolenameGenerator;
	
	public GraphElementCodeGenerator(MetaClass metaClass, String schemaPackageName,
			String implementationName, CodeGeneratorConfiguration config, boolean createVertexClass) {
		super(metaClass, schemaPackageName, implementationName, config);
		if (createVertexClass) {
			rootBlock.setVariable("ownElementClass", "Vertex");	
			rootBlock.setVariable("dualElementClass", "Edge");	
		} else {
			rootBlock.setVariable("ownElementClass", "Edge");
			rootBlock.setVariable("dualElementClass", "Vertex");	
		}
		rolenameGenerator = new RolenameCodeGenerator<MetaClass>(metaClass, false);
	}

	
	@Override
	protected CodeBlock createHeader() {
		CodeList code = new CodeList();
		CodeSnippet snippet = new CodeSnippet();
		snippet.add("/**");
		snippet.add(" * Incomming IncidenceClasses:");
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
		CodeList code = new CodeList();
		addImports("#jgPackage#.#ownElementClass#");
		code.addNoIndent(new CodeSnippet(
						true,
						"public #simpleClassName#Impl(int id, #jgPackage#.Graph g) {",
						"\tsuper(id, g);"));
		if (hasDefaultAttributeValues()) {
			code.addNoIndent(new CodeSnippet("\tinitializeAttributesWithDefaultValues();"));
		}
		code.add(createSpecialConstructorCode());
		code.addNoIndent(new CodeSnippet("}"));
		return code;
	}
	
	
	private CodeSnippet createIncidenceClassCommentInHeader(IncidenceClass ic) {
		CodeSnippet snippet = new CodeSnippet();
		snippet.add(" *   Role: '#rolename#', ConnectedClass: '#gecName#");
		snippet.setVariable("rolename", ic.getRolename());
		snippet.setVariable("gecName", ic.getOtherGraphElementClass(aec).getQualifiedName());
		return snippet;
	}
	
	
	
	/**
	 * creates the body of the class file, that are methods and attributes
	 */
	@Override
	protected CodeList createBody() {
		CodeList code = (CodeList) super.createBody();
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			if (currentCycle.isStdImpl()) {
				addImports("#jgImplStdPackage#.#baseClassName#");
			} else if (currentCycle.isSaveMemImpl()) {
				addImports("#jgImplSaveMemPackage#.#baseClassName#");
			} else if (currentCycle.isTransImpl()) {
				addImports("#jgImplTransPackage#.#baseClassName#");
			} else if (currentCycle.isDbImpl()) {
				addImports("#jgImplDbPackage#.#baseClassName#");
			}

			rootBlock.setVariable("baseClassName", "#ownElementClass#Impl");
			//add valid incidences
		}

		if (config.hasTypeSpecificMethodsSupport() && !currentCycle.isClassOnly()) {
			code.add(createNextMethods());
			code.add(createFirstIncidenceMethods());
		//	code.add(rolenameGenerator.createRolenameMethods(currentCycle
		//			.isStdOrSaveMemOrDbImplOrTransImpl()));
		//	code.add(createIncidenceIteratorMethods());
		}
	//	if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
	//		code.add(createGetEdgeForRolenameMethod());
	//	}

		return code;
	}

	

	private CodeBlock createFirstIncidenceMethods() {
		CodeList code = new CodeList();
		//Iterate all IncidenceClasses known at this vertex class
		for (IncidenceClass ic : aec.getAllIncidenceClasses()) {
			boolean incidenceDefinedForExactlyThisGraphElementClass = ic.getConnectedGraphElementClassOfOwnType(aec) == aec;
			boolean incidenceDefinedForVertexAsDirectSuperclass = (ic.getConnectedGraphElementClassOfOwnType(aec) == aec.getDefaultClass()) 
			        && (aec.getDefaultClass().isDirectSuperClassOf(aec));
			//if incidence class is connected to exactly this vertex or to vertex and this class is a direct subclass of vertex or if the 
			//implementation should be created
			boolean createMethod = (!currentCycle.isAbstract()) || incidenceDefinedForExactlyThisGraphElementClass || incidenceDefinedForVertexAsDirectSuperclass;

			if (createMethod) {
				addImports("#jgPackage#.EdgeDirection");
				if (config.hasTypeSpecificMethodsSupport()) {
					code.addNoIndent(createFirstIncidenceMethod(ic, true));
					if (config.hasMethodsForSubclassesSupport() && !ic.isAbstract()) {
						code.addNoIndent(createFirstIncidenceMethod(ic, false));
					}	
				}
			}
		}
		return code;
	}
	
		
	
	
	private CodeBlock createFirstIncidenceMethod(IncidenceClass ic, boolean typeFlag) {
		CodeSnippet s = new CodeSnippet();
		s.setVariable("incidenceClassName", ic.getRolename());
		s.setVariable("qualifiedIncidenceClassName", ic.getQualifiedName());
		s.setVariable("typeflagFormalParam", typeFlag ? "boolean noSubtypes" : "");
		s.setVariable("typeflagActualParam", typeFlag ? ", noSubtypes" : "");
		if (currentCycle.isAbstract()) {
			//create interface
			s.add("/*",
				  " * @return the first #incidenceClassName# incidence at this #ownElementClass# ");
			if (typeFlag) {
				s.add(" * @param noSubclass if set to true, only incidence of class #incidenceClassName# but not of subclasses will be returned");
			}
			s.add("*/",
				  "public #qualifiedIncidenceClassName# getFirst_#incidenceClassName#(#typeflagFormalParam#);"); 	
		} else {
			s.add("@Override",
			     "public #qualifiedIncidenceClassName# getFirst_#incidenceClassName#(#typeflagFormalParam#) {");
			s.add("\treturn getFirstIncidence(#incidenceClassName#.class#typeflagActualParam#);");
			s.add("}");
			
		}
		return s;
	}
	
	
	/**
	 * Creates <code>getNext#ownElementClass#ClassName()</code> methods
	 * @return the CodeBlock that contains the methods
	 */
	private CodeBlock createNextMethods() {
		CodeList code = new CodeList();

		TreeSet<MetaClass> superClasses = new TreeSet<MetaClass>();
		superClasses.addAll(aec.getAllSuperClasses());
		superClasses.add(aec);

		if (config.hasTypeSpecificMethodsSupport()) {
			for (MetaClass sc : superClasses) {
				if (sc.isInternal()) {
					continue;
				}
				code.addNoIndent(createNextMethod(sc, false));
				if (config.hasMethodsForSubclassesSupport()) {
					if (!sc.isAbstract()) {
						code.addNoIndent(createNextMethod(sc, true));
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
	 * @param withTypeFlag
	 *            toggles if the "no subclasses"-parameter will be created
	 * @return the CodeBlock that contains the method
	 */
	private CodeBlock createNextMethod(MetaClass mc, boolean withTypeFlag) {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("mcQualifiedName", absoluteName(mc));
		code.setVariable("mcCamelName", camelCase(mc.getUniqueName()));
		code.setVariable("formalParams", (withTypeFlag ? "boolean noSubClasses"	: ""));
		code.setVariable("actualParams", (withTypeFlag ? ", noSubClasses" : ""));

		if (currentCycle.isAbstract()) {
			code.add("/**",
					 " * @return the next #mcQualifiedName# #ownElementClass# in the global #ownElementClass# sequence");
			if (withTypeFlag) {
				code.add(" * @param noSubClasses if set to <code>true</code>, no subclasses of #mcName# are accepted");
			}
			code.add(" */",
					 "public #mcQualifiedName# getNext#mcCamelName#(#formalParams#);");
		}
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add("@Override",
					 "public #mcQualifiedName# getNext#mcCamelName#(#formalParams#) {",
					 "\treturn (#mcQualifiedName#)getNext#ownElementClass#(#mcQualifiedName#.class#actualParams#);",
					 "}");
		}
		return code;
	}


	
}
