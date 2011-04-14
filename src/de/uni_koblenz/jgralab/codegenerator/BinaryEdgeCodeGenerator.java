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
		if (currentCycle.isStdImpl()) {
			code.add(createGetSemanticsMethod());
			code.add(createGetAlphaSemanticsMethod());
			code.add(createGetOmegaSemanticsMethod());
		}
	}	
	
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
		code.add("public de.uni_koblenz.jgralab.schema.IncidenceType getSemantics() throws RuntimeException {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetAlphaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("semantics", bec.getFromIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.IncidenceType getAlphaSemantics() throws RuntimeException {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetOmegaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("semantics", bec.getToIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.IncidenceType getOmegaSemantics() throws RuntimeException {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}
	

}
