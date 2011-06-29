package de.uni_koblenz.jgralab.codegenerator;

import de.uni_koblenz.jgralab.schema.BinaryEdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

public class BinaryEdgeCodeGenerator extends EdgeCodeGenerator {

	public BinaryEdgeCodeGenerator(BinaryEdgeClass edgeClass,
			String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(edgeClass, schemaPackageName, config);
		bec = edgeClass;
		rootBlock.setVariable("baseClassName", "BinaryEdgeImpl");
		rootBlock.setVariable("graphElementClass", "BinaryEdge");
	}
	
	BinaryEdgeClass bec;
	
	protected void createMethodsForBinaryEdge(CodeList code) {
		if (currentCycle.isMemOrDiskImpl() || currentCycle.isProxies()) {
			code.add(createGetSemanticsMethod());
			code.add(createGetAlphaSemanticsMethod());
			code.add(createGetOmegaSemanticsMethod());
			//code.add(createBinaryConstructor());
		}
	}


//	protected CodeBlock createBinaryConstructor() {
//		if (aec.isAbstract())
//			return null;
//		CodeList code = new CodeList();
//		addImports("#jgPackage#.#ownElementClass#");
//		IncidenceClass alphaInc = null;
//		IncidenceClass omegaInc = null;
//		for (IncidenceClass ic : aec.getAllIncidenceClasses()) {
//			if (!ic.isAbstract()) {
//				if (ic.getDirection() == Direction.EDGE_TO_VERTEX) {
//					omegaInc = ic;
//				} else {
//					alphaInc = ic;
//				}
//			}
//		}
//		code.setVariable("alphaVertex", absoluteName(alphaInc.getVertexClass()));
//		code.setVariable("omegaVertex", absoluteName(omegaInc.getVertexClass()));
//		code.setVariable("alphaInc", absoluteName(alphaInc));
//		code.setVariable("omegaInc", absoluteName(omegaInc));
//		code.addNoIndent(new CodeSnippet(
//						true,
//						"public #simpleClassName#Impl(int id, #jgPackage#.Graph g, #alphaVertex# alpha, #omegaVertex# omega) throws java.io.IOException {",
//						"\tthis(id, g);"));
//		code.addNoIndent(new CodeSnippet("alpha.connect(#alphaInc#.class, this);"));
//		code.addNoIndent(new CodeSnippet("omega.connect(#omegaInc#.class, this);"));
//		//code.addNoIndent(new CodeSnippet("/* implement setting of alpha and omega */"));
//		code.addNoIndent(new CodeSnippet("}"));
//		return code;
//	}
	
	private CodeBlock createGetSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		String val = "EDGE";

		if ((bec.getToIncidenceClass().getIncidenceType() == IncidenceType.COMPOSITION)
				|| (bec.getFromIncidenceClass().getIncidenceType() == IncidenceType.COMPOSITION)) {
			val = "COMPOSITION";
		} else if ((bec.getToIncidenceClass().getIncidenceType() == IncidenceType.AGGREGATION)
				|| (bec.getFromIncidenceClass().getIncidenceType() == IncidenceType.AGGREGATION)) {
			val = "AGGREGATION";
		}
		code.setVariable("semantics", val);
		code.add("public de.uni_koblenz.jgralab.schema.IncidenceType getSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetAlphaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("semantics", bec.getFromIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.IncidenceType getAlphaSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetOmegaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("semantics", bec.getToIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.IncidenceType getOmegaSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}
	

}
