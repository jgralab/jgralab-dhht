package de.uni_koblenz.jgralab.codegenerator;

import de.uni_koblenz.jgralab.schema.BinaryEdgeClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.IncidenceType;

public class BinaryEdgeCodeGenerator extends EdgeCodeGenerator {

	public BinaryEdgeCodeGenerator(BinaryEdgeClass edgeClass,
			String schemaPackageName,
			CodeGeneratorConfiguration config) {
		super(edgeClass, schemaPackageName, config);
		bec = edgeClass;
	}
	
	BinaryEdgeClass bec;
	
	protected void createMethodsForBinaryEdge(CodeList code) {
		if (currentCycle.isStdOrSaveMemOrDbImplOrTransImpl()) {
			code.add(createGetSemanticsMethod());
			code.add(createGetAlphaSemanticsMethod());
			code.add(createGetOmegaSemanticsMethod());
		}
	}	
	
	private CodeBlock createGetSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		EdgeClass ec = (EdgeClass) aec;
		String val = "NONE";

		if ((bec.getToIncidenceClass().getIncidenceType() == IncidenceType.COMPOSITION)
				|| (bec.getFromIncidenceClass().getIncidenceType() == IncidenceType.COMPOSITION)) {
			val = "COMPOSITE";
		} else if ((bec.getToIncidenceClass().getIncidenceType() == IncidenceType.AGGREGATION)
				|| (bec.getFromIncidenceClass().getIncidenceType() == IncidenceType.AGGREGATION)) {
			val = "SHARED";
		}
		code.setVariable("semantics", val);
		code.add("public de.uni_koblenz.jgralab.schema.IncidenceType getSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.IncidenceType.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetAlphaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		EdgeClass ec = (EdgeClass) aec;
		code.setVariable("semantics", bec.getFromIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.AggregationKind getAlphaSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.AggregationKind.#semantics#;",
				 "}");
		return code;
	}

	private CodeBlock createGetOmegaSemanticsMethod() {
		CodeSnippet code = new CodeSnippet(true);
		code.setVariable("semantics", bec.getToIncidenceClass().getIncidenceType().toString());
		code.add("@Override",
				 "public de.uni_koblenz.jgralab.schema.AggregationKind getOmegaSemantics() {",
				 "\treturn de.uni_koblenz.jgralab.schema.AggregationKind.#semantics#;",
				 "}");
		return code;
	}
	

}
