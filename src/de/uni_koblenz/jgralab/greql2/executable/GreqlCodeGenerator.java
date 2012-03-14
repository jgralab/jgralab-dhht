package de.uni_koblenz.jgralab.greql2.executable;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.codegenerator.CodeList;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.graphmarker.ObjectGraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.AggregationIncidenceTransition_Db;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.NFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.SimpleIncidenceTransition_Db;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.State;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.Transition;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.schema.BoolLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.DoubleLiteral;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.ForwardElementSet;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IncDirection;
import de.uni_koblenz.jgralab.greql2.schema.IntLiteral;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf_isArgumentOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf_isBoundVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf_isConstraintOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf_isDeclaredVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfStoreClause_isIdOfStoreClause_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsPartOf_isPartOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsQueryExprOf_isQueryExprOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf_isSimpleDeclOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeRestrOfExpression_isTypeRestrOfExpression_omega;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.schema.ListConstruction;
import de.uni_koblenz.jgralab.greql2.schema.ListRangeConstruction;
import de.uni_koblenz.jgralab.greql2.schema.Literal;
import de.uni_koblenz.jgralab.greql2.schema.LongLiteral;
import de.uni_koblenz.jgralab.greql2.schema.MapComprehension;
import de.uni_koblenz.jgralab.greql2.schema.QuantifiedExpression;
import de.uni_koblenz.jgralab.greql2.schema.Quantifier;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;
import de.uni_koblenz.jgralab.greql2.schema.SetConstruction;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.StringLiteral;
import de.uni_koblenz.jgralab.greql2.schema.ThisEdge;
import de.uni_koblenz.jgralab.greql2.schema.ThisVertex;
import de.uni_koblenz.jgralab.greql2.schema.TypeId;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.greql2.schema.VertexSetExpression;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;
import de.uni_koblenz.jgralab.schema.IncidenceType;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.TypedElementClass;

public class GreqlCodeGenerator extends CodeGenerator {

	private GreqlSyntaxGraph graph;
	
	private String classname;
	
	private int functionNumber = 1;
	
	private CodeSnippet classFieldSnippet = new CodeSnippet();
	
	private CodeSnippet staticFieldSnippet = new CodeSnippet();
	
	private CodeSnippet staticInitializerSnippet = new CodeSnippet("static {");
	
	private List<CodeBlock> createdMethods = new LinkedList<CodeBlock>();
	
	private Scope scope;
	
	private Schema schema;
	
	private boolean thisLiteralsCreated = false;
	
	private ObjectGraphMarker<Vertex, VertexEvaluator> vertexEvalGraphMarker;
	
	public GreqlCodeGenerator(GreqlSyntaxGraph graph, ObjectGraphMarker<Vertex, VertexEvaluator> vertexEvalGraphMarker, Schema datagraphSchema) {
		super("de.uni_koblenz.jgralab.greql2.executable", "queries", CodeGeneratorConfiguration.WITHOUT_TYPESPECIFIC_METHODS);
		this.graph = graph;
		this.vertexEvalGraphMarker = vertexEvalGraphMarker;
		classname = "SampleQuery";
		this.schema = datagraphSchema;
		scope = new Scope();
		//this.classname = "Query_" + System.currentTimeMillis();
	}

	
	public CodeBlock createBody() {
		CodeList code = new CodeList();
		
		addImports("de.uni_koblenz.jgralab.greql2.executable.*");
		addImports("de.uni_koblenz.jgralab.Graph");
		code.add(staticFieldSnippet);
		code.add(staticInitializerSnippet);
		code.add(classFieldSnippet);		
		Greql2Expression rootExpr = graph.getFirstGreql2Expression();
		CodeSnippet method = new CodeSnippet();
		method.add("");
		method.add("private Graph datagraph;");
		method.add("");
		method.add("public Object execute(de.uni_koblenz.jgralab.Graph graph, java.util.Map<String, Object> boundVariables) {");
		method.add("\tObject result = null;");
		method.add("\tdatagraph = graph;");

		
		//create code for bound variables		
		scope.blockBegin();
		for (IsBoundVarOf_isBoundVarOf_omega inc : rootExpr.getIsBoundVarOf_isBoundVarOf_omegaIncidences()) {
			Variable var = (Variable) inc.getThat();
			scope.addVariable(var.get_name());
			method.add("\tObject " + var.get_name() + " = boundVariables.get(\"" + var.get_name() + "\");");
		}
		code.add(method);
		
		//create code for main query expression
		IsQueryExprOf_isQueryExprOf_omega inc = rootExpr.getFirst_isQueryExprOf_omega();
		Expression queryExpr = (Expression) inc.getThat();
		code.add( new CodeSnippet("\tresult = " + createCodeForExpression(queryExpr) + ";")  );
				
		
		//create code for store as 
		CodeSnippet endOfMethod = new CodeSnippet();
		for (IsIdOfStoreClause_isIdOfStoreClause_omega storeInc : rootExpr.getIsIdOfStoreClause_isIdOfStoreClause_omegaIncidences()) {
			Identifier ident = (Identifier) storeInc.getThat();
			endOfMethod.add("\tboundVariables.put(\"" + ident.get_name() + "\","  + ident.get_name() + ");");
		}
		
		
		scope.blockEnd();
		
		//create code for return and method end
		endOfMethod.add("\treturn result;");
		endOfMethod.add("}");
		code.add(endOfMethod);
		
		//add generated methods
		code.add(new CodeSnippet("",""));
		for (CodeBlock methodBlock : createdMethods) {
			code.addNoIndent(methodBlock);
			code.add(new CodeSnippet("",""));
		}
		staticInitializerSnippet.add("}");
		return code;
	}
	


	
	private String createCodeForExpression(Expression queryExpr) {
		if (queryExpr instanceof FunctionApplication) {
			return createCodeForFunctionApplication((FunctionApplication) queryExpr);
		}
		if (queryExpr instanceof Comprehension) {
			return createCodeForComprehension((Comprehension) queryExpr);
		}
		if (queryExpr instanceof QuantifiedExpression) {
			return createCodeForQuantifiedExpression((QuantifiedExpression) queryExpr);
		}
		if (queryExpr instanceof ConditionalExpression) {
			return createCodeForConditionalExpression((ConditionalExpression) queryExpr);
		}
		if (queryExpr instanceof EdgeSetExpression) {
			return createCodeForEdgeSetExpression((EdgeSetExpression) queryExpr);
		}
		if (queryExpr instanceof VertexSetExpression) {
			return createCodeForVertexSetExpression((VertexSetExpression) queryExpr);
		}
		if (queryExpr instanceof ListRangeConstruction) {
			return createCodeForListRangeConstruction((ListRangeConstruction) queryExpr);
		}
		if (queryExpr instanceof ListConstruction) {
			return createCodeForListConstruction((ListConstruction) queryExpr);
		}
		if (queryExpr instanceof SetConstruction) {
			return createCodeForSetConstruction((SetConstruction) queryExpr);
		}
		//thisVertex and thisEdge should be handled separately before literal and variable
		//in path evaluators, direct access to the respecitve vertices needs to be 
		//encapulated
		if (queryExpr instanceof Literal) {
			return createCodeForLiteral((Literal) queryExpr);
		}
		if (queryExpr instanceof Variable) {
			return createCodeForVariable((Variable) queryExpr);
		}
		if (queryExpr instanceof ForwardElementSet) {
			return createCodeForForwardElementSet((ForwardElementSet) queryExpr);
		}
		
		return "UnsupportedElement";
	}

	
	
	
	//EdgeSetExpression
	private String createCodeForEdgeSetExpression(EdgeSetExpression setExpr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		addImports("de.uni_koblenz.jgralab.Edge");
		addImports("de.uni_koblenz.jgralab.greql2.types.TypeCollection");
		addImports("de.uni_koblenz.jgralab.schema.TypedElementClass");
		addImports("java.util.Collection");
		addImports("java.util.LinkedList");
		CodeList list = new CodeList();
		CodeSnippet typeColSnip = new CodeSnippet();
		typeColSnip.add("TypeCollection typeCollection = new TypeCollection();");
		typeColSnip.add("Collection<TypedElementClass> setOfTypes;");
		for (IsTypeRestrOfExpression_isTypeRestrOfExpression_omega inc : setExpr.getIsTypeRestrOfExpression_isTypeRestrOfExpression_omegaIncidences()) {
			TypeId typeId = (TypeId) inc.getThat();
			typeColSnip.add("setOfTypes = new LinkedList<TypedElementClass>();");
			typeColSnip.add("setOfTypes.add(datagraph.getSchema().getAttributedElementClass(\"" + typeId.get_name() + "\"));");
			if (typeId.is_type()) {
				typeColSnip.add("setOfTypes.addAll(datagraph.getSchema().getAttributedElementClass(\"" + typeId.get_name() + "\").getAllSubclasses());");
			}
			typeColSnip.add("boolean forbidden = " + typeId.is_excluded() + ";");
			typeColSnip.add("typeCollection.addTypes(new TypeCollection(setOfTypes, forbidden));"); 
		}
		list.add(typeColSnip);
		CodeSnippet createEdgeSetSnippet = new CodeSnippet();
		createEdgeSetSnippet.add("PCollection<Edge> edgeSet = JGraLab.set();");
		createEdgeSetSnippet.add("for (Edge e : datagraph.getEdges()) {");
		createEdgeSetSnippet.add("\tif (typeCollection.acceptsType(e.getType())) {");
		createEdgeSetSnippet.add("\t\tedgeSet = edgeSet.plus(e);");
		createEdgeSetSnippet.add("\t}");
		createEdgeSetSnippet.add("}");
		createEdgeSetSnippet.add("return edgeSet;");
		list.add(createEdgeSetSnippet);
		return createMethod(list);
	}
	
	//VertexSetExpression
	private String createCodeForVertexSetExpression(VertexSetExpression setExpr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		addImports("de.uni_koblenz.jgralab.Vertex");
		addImports("de.uni_koblenz.jgralab.greql2.types.TypeCollection");
		addImports("de.uni_koblenz.jgralab.schema.TypedElementClass");
		addImports("java.util.Collection");
		addImports("java.util.LinkedList");
		CodeList list = new CodeList();
		CodeSnippet typeColSnip = new CodeSnippet();
		typeColSnip.add("TypeCollection typeCollection = new TypeCollection();");
		typeColSnip.add("Collection<TypedElementClass> setOfTypes;");
		for (IsTypeRestrOfExpression_isTypeRestrOfExpression_omega inc : setExpr.getIsTypeRestrOfExpression_isTypeRestrOfExpression_omegaIncidences()) {
			TypeId typeId = (TypeId) inc.getThat();
			typeColSnip.add("setOfTypes = new LinkedList<TypedElementClass>();");
			typeColSnip.add("setOfTypes.add(datagraph.getSchema().getAttributedElementClass(\"" + typeId.get_name() + "\"));");
			if (typeId.is_type()) {
				typeColSnip.add("setOfTypes.addAll(datagraph.getSchema().getAttributedElementClass(\"" + typeId.get_name() + "\").getAllSubclasses());");
			}
			typeColSnip.add("boolean forbidden = " + typeId.is_excluded() + ";");
			typeColSnip.add("typeCollection.addTypes(new TypeCollection(setOfTypes, forbidden));"); 
		}
		list.add(typeColSnip);
		CodeSnippet createVertexSetSnippet = new CodeSnippet();
		createVertexSetSnippet.add("PCollection<Vertex> vertexSet = JGraLab.set();");
		createVertexSetSnippet.add("for (Vertex v : datagraph.getVertices()) {");
		createVertexSetSnippet.add("\tif (typeCollection.acceptsType(v.getType())) {");
		createVertexSetSnippet.add("\t\tvertexSet = vertexSet.plus(v);");
		createVertexSetSnippet.add("\t}");
		createVertexSetSnippet.add("}");
		createVertexSetSnippet.add("return vertexSet;");
		list.add(createVertexSetSnippet);
		return createMethod(list);
	}
	
	private String createCodeForListRangeConstruction(ListRangeConstruction listConstr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		CodeList list = new CodeList();
		CodeSnippet listSnippet = new CodeSnippet();
		listSnippet.add("PCollection list = JGraLab.vector();");
		Expression startExpr = (Expression) listConstr.getFirst_isFirstValueOf_omega().getThat();
		Expression endExpr = (Expression) listConstr.getFirst_isLastValueOf_omega().getThat();
		listSnippet.add("for (int i= " + createCodeForExpression(startExpr) + "; i<" + createCodeForExpression(endExpr) + "; i++) {");
		listSnippet.add("list = list.plus(i);");
		listSnippet.add("}");
		listSnippet.add("return list;");
		list.add(listSnippet);
		return createMethod(list);
	}
	
	
	private String createCodeForListConstruction(ListConstruction listConstr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		CodeList list = new CodeList();
		CodeSnippet listSnippet = new CodeSnippet();
		listSnippet.add("PCollection list = JGraLab.vector();");
		StringBuilder builder = new StringBuilder("list = list");
		for (IsPartOf_isPartOf_omega inc : listConstr.getIsPartOf_isPartOf_omegaIncidences()) {
			Expression expr = (Expression) inc.getThat();
			builder.append(".plus(" + createCodeForExpression(expr) + ")");
		}
		builder.append(";");
		listSnippet.add(builder.toString());
		listSnippet.add("return list;");
		list.add(listSnippet);
		return createMethod(list);
	}
	
	private String createCodeForSetConstruction(SetConstruction setConstr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		CodeList list = new CodeList();
		CodeSnippet listSnippet = new CodeSnippet();
		StringBuilder builder = new StringBuilder("list = list");
		for (IsPartOf_isPartOf_omega inc : setConstr.getIsPartOf_isPartOf_omegaIncidences()) {
			Expression expr = (Expression) inc.getThat();
			builder.append(".plus(" + createCodeForExpression(expr) + ")");
		}
		builder.append(";");
		listSnippet.add(builder.toString());
		list.add(listSnippet);
		return createMethod(list);
	}
	
	private String createCodeForConditionalExpression(ConditionalExpression condExpr) {
		CodeList list = new CodeList();

		Expression condition = (Expression) condExpr.getFirst_isConditionOf_omega().getThat();
		Expression trueExpr = (Expression) condExpr.getFirst_isTrueExprOf_omega().getThat();
		Expression falseExpr = (Expression) condExpr.getFirst_isFalseExprOf_omega().getThat();
		
		CodeSnippet snip = new CodeSnippet();
		list.add(snip);
		snip.add("if ((Boolean) " + createCodeForExpression(condition) + ") {" );
		snip.add("\treturn " + createCodeForExpression(trueExpr) + ";" );
		snip.add("} else {");
		snip.add("\treturn " + createCodeForExpression(falseExpr) + ";" );
		snip.add("}");
		
		String retVal = createMethod(list);
		return retVal;
	}
	
	private String createCodeForComprehension(Comprehension compr) {
		addImports("de.uni_koblenz.jgralab.JGraLab");
		addImports("org.pcollections.PCollection");
		CodeList list = new CodeList();
		CodeSnippet initSnippet = new CodeSnippet();
		if (compr instanceof ListComprehension) {
			initSnippet.add("PCollection result = JGraLab.vector();");
		}
		if (compr instanceof SetComprehension) {
			initSnippet.add("PCollection result = JGraLab.set();");
		}
		if (compr instanceof MapComprehension) {
			addImports("org.pcollections.PMap");
			initSnippet.add("PMap result = JGraLab.map();");
		}
		list.add(initSnippet);
		
		Declaration decl = (Declaration) compr.getFirst_isCompDeclOf_omega().getThat();
		
		//Declarations and variable iteration loops
		int declaredVars = 0;
		String tabs = "";
		int simpleDecls = 0;
		scope.blockBegin();
		for (IsSimpleDeclOf_isSimpleDeclOf_omega simpleDeclInc : decl.getIsSimpleDeclOf_isSimpleDeclOf_omegaIncidences()) {
			SimpleDeclaration simpleDecl = (SimpleDeclaration) simpleDeclInc.getThat();
			Expression domain = (Expression) simpleDecl.getFirst_isTypeExprOfDeclaration_omega().getThat();
			CodeSnippet simpleDeclSnippet = new CodeSnippet();
			simpleDeclSnippet.setVariable("simpleDeclNum", Integer.toString(simpleDecls));
			simpleDeclSnippet.add(tabs + "PCollection domain_#simpleDeclNum# = (PCollection) " + createCodeForExpression(domain) + ";");
			list.add(simpleDeclSnippet);
			for (IsDeclaredVarOf_isDeclaredVarOf_omega declaredVarInc : simpleDecl.getIsDeclaredVarOf_isDeclaredVarOf_omegaIncidences()) {
				declaredVars++;
				Variable var = (Variable) declaredVarInc.getThat();
				CodeSnippet varIterationSnippet = new CodeSnippet();
				varIterationSnippet.setVariable("variableName", var.get_name());
				varIterationSnippet.setVariable("simpleDeclNum", Integer.toString(simpleDecls));
				varIterationSnippet.add(tabs + "for (Object #variableName# : domain_#simpleDeclNum#) {");
				tabs += "\t";
				scope.addVariable(var.get_name());
				list.add(varIterationSnippet);
			}
			simpleDecls++;
		}
		
		//condition
		if (decl.getFirst_isConstraintOf_omega() != null) {
			CodeSnippet constraintSnippet = new CodeSnippet();
			constraintSnippet.add(tabs + "boolean constraint = true;");
			for (IsConstraintOf_isConstraintOf_omega constraintInc : decl.getIsConstraintOf_isConstraintOf_omegaIncidences()) {
				Expression constrExpr = (Expression) constraintInc.getThat();
				constraintSnippet.add(tabs + "constraint = constraint && (Boolean) " + createCodeForExpression(constrExpr) + ";");
			}
			constraintSnippet.add(tabs + "if (constraint)");
			list.add(constraintSnippet);
		}


		//main expression
		CodeSnippet iteratedExprSnip = new CodeSnippet();
		if (compr instanceof MapComprehension) {
			Expression keyExpr = (Expression) ((MapComprehension)compr).getFirst_isKeyExprOfComprehension_omega().getThat();
			Expression valueExpr = (Expression) ((MapComprehension)compr).getFirst_isValueExprOfComprehension_omega().getThat();
			iteratedExprSnip.add(tabs + "result.put(" + createCodeForExpression(keyExpr) + "," + createCodeForExpression(valueExpr) + ");");
		} else {
			Expression resultDefinition = (Expression) compr.getFirst_isCompResultDefOf_omega().getThat();
			iteratedExprSnip.add(tabs + "result = result.plus(" + createCodeForExpression(resultDefinition) + ");");
		}
		list.add(iteratedExprSnip);
		//closing parantheses for interation loops
		for (int curLoop=0; curLoop<declaredVars; curLoop++) {
			StringBuilder tabBuff = new StringBuilder();
			for (int i=1; i<(declaredVars-curLoop); i++) {
				tabBuff.append("\t");
			}
			tabs = tabBuff.toString();
			list.add(new CodeSnippet(tabs + "}"));
		}
		list.add(new CodeSnippet("return result;"));
		scope.blockEnd();
		String retVal = createMethod(list);
		return retVal;
	}
	
	
	private String createCodeForQuantifiedExpression(QuantifiedExpression quantExpr) {
		CodeList list = new CodeList();
		addImports("org.pcollections.PCollection");
		Declaration decl = (Declaration) quantExpr.getFirst_isQuantifiedDeclOf_omega().getThat();
		
		//Declarations and variable iteration loops
		int declaredVars = 0;
		String tabs = "";
		int simpleDecls = 0;
		//quantifier
		Quantifier quantifier = (Quantifier) quantExpr.getFirst_isQuantifierOf_omega().getThat();
		switch (quantifier.get_type()) {
		case FORALL:
			break;
		case EXISTS:
			break;
		case EXISTSONE:	
			list.add(new CodeSnippet("boolean result = false;"));
			break;
		}
		scope.blockBegin();
		for (IsSimpleDeclOf_isSimpleDeclOf_omega simpleDeclInc : decl.getIsSimpleDeclOf_isSimpleDeclOf_omegaIncidences()) {
			SimpleDeclaration simpleDecl = (SimpleDeclaration) simpleDeclInc.getThat();
			Expression domain = (Expression) simpleDecl.getFirst_isTypeExprOfDeclaration_omega().getThat();
			CodeSnippet simpleDeclSnippet = new CodeSnippet();
			simpleDeclSnippet.setVariable("simpleDeclNum", Integer.toString(simpleDecls));
			simpleDeclSnippet.add(tabs + "PCollection domain_#simpleDeclNum# = (PCollection) " + createCodeForExpression(domain) + ";");
			list.add(simpleDeclSnippet);
			for (IsDeclaredVarOf_isDeclaredVarOf_omega declaredVarInc : simpleDecl.getIsDeclaredVarOf_isDeclaredVarOf_omegaIncidences()) {
				declaredVars++;
				Variable var = (Variable) declaredVarInc.getThat();
				CodeSnippet varIterationSnippet = new CodeSnippet();
				varIterationSnippet.setVariable("variableName", var.get_name());
				varIterationSnippet.setVariable("simpleDeclNum", Integer.toString(simpleDecls));
				varIterationSnippet.add(tabs + "for (Object #variableName# : domain_#simpleDeclNum#) {");
				tabs += "\t";
				scope.addVariable(var.get_name());
				list.add(varIterationSnippet);
			}
			simpleDecls++;
		}
		
		//condition
		if (decl.getFirst_isConstraintOf_omega() != null) {
			CodeSnippet constraintSnippet = new CodeSnippet();
			constraintSnippet.add(tabs + "boolean constraint = true;");
			for (IsConstraintOf_isConstraintOf_omega constraintInc : decl.getIsConstraintOf_isConstraintOf_omegaIncidences()) {
				Expression constrExpr = (Expression) constraintInc.getThat();
				constraintSnippet.add(tabs + "constraint = constraint && (Boolean) " + createCodeForExpression(constrExpr) + ";");
			}
			constraintSnippet.add(tabs + "if (constraint)");
			list.add(constraintSnippet);
		}



		//main expression
		Expression resultDefinition = (Expression) quantExpr.getFirst_isBoundExprOfQuantifiedExpr_omega().getThat();
		CodeSnippet iteratedExprSnip = new CodeSnippet();
		switch (quantifier.get_type()) {
		case FORALL:
			iteratedExprSnip.add(tabs + "if ( ! (Boolean) " + createCodeForExpression(resultDefinition) + ") return false;");
			break;
		case EXISTS:	
			iteratedExprSnip.add(tabs + "if ( (Boolean) " + createCodeForExpression(resultDefinition) + ") return true;");
			break;
		case EXISTSONE:
			iteratedExprSnip.add(tabs + "if ( (Boolean) " + createCodeForExpression(resultDefinition) + ") {");
			iteratedExprSnip.add(tabs + "\tif (result) {");
			iteratedExprSnip.add(tabs + "\t\treturn false; //two elements exists");
			iteratedExprSnip.add(tabs + "\t} else {");
			iteratedExprSnip.add(tabs + "\t\tresult = true; //first element found");
			iteratedExprSnip.add(tabs + "\t}");
			iteratedExprSnip.add(tabs + "}");
			
		}

		list.add(iteratedExprSnip);
		//closing parantheses for interation loops
		for (int curLoop=0; curLoop<declaredVars; curLoop++) {
			StringBuilder tabBuff = new StringBuilder();
			for (int i=1; i<(declaredVars-curLoop); i++) {
				tabBuff.append("\t");
			}
			tabs = tabBuff.toString();
			list.add(new CodeSnippet(tabs + "}"));
		}
		switch (quantifier.get_type()) {
		case FORALL:
			list.add(new CodeSnippet("return true;"));
			break;
		case EXISTSONE:	
			list.add(new CodeSnippet("return result;"));
			break;
		case EXISTS:
			list.add(new CodeSnippet("return false;"));
		}	
		scope.blockEnd();
		String retVal = createMethod(list);
		return retVal;
	}
	
	
	
	private String createCodeForLiteral(Literal literal) {
		if (literal instanceof StringLiteral) {
			return "\"" + ((StringLiteral)literal).get_stringValue() + "\"";
		}
		if (literal instanceof IntLiteral) {
			return  Integer.toString(((IntLiteral)literal).get_intValue());
		}
		if (literal instanceof LongLiteral) {
			return  Long.toString(((LongLiteral)literal).get_longValue());
		}
		if (literal instanceof DoubleLiteral) {
			return  Double.toString(((DoubleLiteral)literal).get_doubleValue());
		}
		if (literal instanceof BoolLiteral) {
			return  Boolean.toString(((BoolLiteral)literal).is_boolValue());
		}
		if (literal instanceof ThisEdge) {
			return  "thisIncidence";
		}
		if (literal instanceof ThisVertex) {
			return  "thisElement";
		}
		return "UndefinedLiteral";
	}


	private String createCodeForVariable(Variable var) {
		return var.get_name();
	}


	private String createCodeForFunctionApplication(
			FunctionApplication funApp) {
		addImports("de.uni_koblenz.jgralab.greql2.funlib.FunLib");

		//create static field to access function
		FunctionId funId = (FunctionId) funApp.getFirst_isFunctionIdOf_omega().getThat();		
		Function function = FunLib.getFunctionInfo(funId.get_name()).getFunction();
		String functionName = function.getClass().getName();
		String functionSimpleName = function.getClass().getSimpleName();
		if (functionSimpleName.contains("."))
			functionSimpleName = functionSimpleName.substring(functionSimpleName.lastIndexOf("."));
		addStaticField(functionName, functionSimpleName + "_" + functionNumber, "(" + functionName + ") FunLib.getFunctionInfo(\"" + funId.get_name() + "\").getFunction()");		
		//create code list to evaluate function
		CodeList list = new CodeList();
		list.add(new CodeSnippet("return " + functionSimpleName + "_" + functionNumber + ".evaluate("));
		functionNumber++;
		String delim = "";
		Method[] methods = function.getClass().getMethods();
		Method evaluateMethod = null;
		for (Method m : methods) {
			if (m.getName() == "evaluate")
				evaluateMethod = m;
		}
		Class<?>[] paramTypes = evaluateMethod.getParameterTypes();
		int currentParam = 0;
		for (IsArgumentOf_isArgumentOf_omega argInc : funApp.getIsArgumentOf_isArgumentOf_omegaIncidences()) {
			Expression expr = (Expression) argInc.getThat();
			CodeSnippet argEvalSnippet = new CodeSnippet();
			String cast = "(" + paramTypes[currentParam++].getCanonicalName() + ")";
			argEvalSnippet.add("\t\t" + delim + cast + createCodeForExpression(expr));
			delim = ",";
			list.add(argEvalSnippet);
		}
		list.add(new CodeSnippet(");"));
		
		return createMethod(list);
	}
	
	
	
	private String createCodeForForwardElementSet(ForwardElementSet fws) {
		DFA dfa = null;
//		PathDescription pathDescr = (PathDescription) fws.getFirst_isPathOf_GoesTo_PathExpression().getThat();
//		PathDescriptionEvaluator pathDescrEval = (PathDescriptionEvaluator) vertexEvalGraphMarker.getMark(pathDescr);
//		dfa = ((NFA)pathDescrEval.getResult()).getDFA();
		NFA nfa = NFA.createSimpleIncidenceTransition_Db();
		dfa = nfa.getDFA();
		Expression startElementExpr = (Expression) fws.getFirst_isStartExprOf_omega().getThat();
		CodeList list = new CodeList();
		addImports("org.pcollections.PCollection");
		addImports("org.pcollections.PSet");
		addImports("java.util.HashSet");
		addImports("java.util.BitSet");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		addImports("de.uni_koblenz.jgralab.GraphElement");
		addImports("de.uni_koblenz.jgralab.Incidence");
		addImports("de.uni_koblenz.jgralab.greql2.types.pathsearch.ElementStateQueue");
		CodeSnippet initSnippet = new CodeSnippet();
		list.add(initSnippet);
		initSnippet.add("PSet<GraphElement> resultSet = JGraLab.set();");
		initSnippet.add("//one BitSet for each state");
		initSnippet.add("HashSet<GraphElement>[] markedElements = new HashSet[#stateCount#];");
		initSnippet.setVariable("stateCount", Integer.toString(dfa.stateList.size()));
		initSnippet.add("for (int i=0; i<#stateCount#;i++) {");
		initSnippet.add("\tmarkedElements[i] = new HashSet(100);");
		initSnippet.add("}");
		initSnippet.add("BitSet finalStates = new BitSet();");
		for (State s : dfa.stateList) {
			if (s.isFinal) {
				initSnippet.add("finalStates.set(" + s.number + ");");
			}
		}
		initSnippet.add("GraphElement startElement = (GraphElement)" + createCodeForExpression(startElementExpr) + ";");
		initSnippet.add("ElementStateQueue queue = new ElementStateQueue();");
		initSnippet.add("markedElements[" + dfa.initialState.number + "].add(startElement);");
		initSnippet.add("int stateNumber;");
		initSnippet.add("GraphElement element;");
		initSnippet.add("int nextStateNumber;");
		initSnippet.add("GraphElement nextElement;");
		initSnippet.add("boolean isVertex;");
		initSnippet.add("queue.put((GraphElement)v, " + dfa.initialState.number + ");");
		initSnippet.add("while (queue.hasNext()) {");
		initSnippet.add("\telement = queue.currentElement;");
		initSnippet.add("\tstateNumber = queue.currentState;");
		initSnippet.add("\tif (finalStates.get(stateNumber)) {");
		initSnippet.add("\t\tresultSet = resultSet.plus(element);");
		initSnippet.add("\t}");
		initSnippet.add("\tisVertex = element instanceof Vertex;");
		initSnippet.add("\tfor (Incidence inc = element.getFirstIncidence();");
		initSnippet.add("\t\tinc != null; inc = isVertex ? inc.getNextIncidenceAtVertex() : inc.getNextIncidenceAtEdge()) {");
		initSnippet.add("\t\tswitch (stateNumber) {");
		for (State curState : dfa.stateList) {
			CodeList stateCodeList = new CodeList();
			list.add(stateCodeList);
			stateCodeList.add(new CodeSnippet("\t\tcase " + curState.number + ":"));
			for (Transition curTrans : curState.outTransitions) {
				System.out.println("Handling transition " + curTrans.getStartState().number + " --> " + curTrans.endState.number + ":" + curTrans);
				CodeList transitionCodeList = new CodeList();
				stateCodeList.add(transitionCodeList);
				CodeSnippet transBeginSnippet = new CodeSnippet();
				transitionCodeList.addNoIndent(transBeginSnippet);
				//Generate code to get next vertex and state number
				if (curTrans.consumesIncidence()) {
					transBeginSnippet.add("\t\t\tnextElement = isVertex ? inc.getEdge() : inc.getVertex();"); 
				} else {
					transBeginSnippet.add("\t\t\tnextElement = element;");
				}					
				//Generate code to check if next element is marked
				transBeginSnippet.add("\t\t\tif (!markedElements[" + curTrans.endState.number + "].contains(nextElement)) {");
				transitionCodeList.add(createCodeForTransition(curTrans),2);
				transitionCodeList.add(new CodeSnippet("\t\t\t}"));
			}
			stateCodeList.add(new CodeSnippet("\t\tbreak;//break case block"));
		}
		CodeSnippet finalSnippet = new CodeSnippet();
		finalSnippet.add("\t\t}");
		finalSnippet.add("\t}");
		finalSnippet.add("}");
		finalSnippet.add("return resultSet;");
		list.add(finalSnippet);
		return createMethod(list);
	}
	
	
	private CodeSnippet createAddToQueueSnippet(int number) {
		CodeSnippet annToQueueSnippet = new CodeSnippet();
		annToQueueSnippet.add("markedElements[" + number + "].add(nextElement);");
		annToQueueSnippet.add("queue.put(nextElement," + number + ");");
		return annToQueueSnippet;
	}

	

	
	
	private int acceptedIncidenceTypesNumber = 0;
	
	
	private CodeBlock createCodeForTransition(Transition trans) {
		if (trans instanceof SimpleIncidenceTransition_Db) {
			return createCodeForSimpleIncidenceTransition_Db((SimpleIncidenceTransition_Db) trans);
		}
		if (trans instanceof AggregationIncidenceTransition_Db) {
			return createCodeForAggregationIncidenceTransition_Db((AggregationIncidenceTransition_Db) trans);
		}
		return new CodeSnippet("FAILURE: TRANSITION TYPE IS UNKNOWN TO GREQL CODE GENERATOR " + trans.getClass().getSimpleName()); 
	}
	
	private CodeBlock createCodeForSimpleIncidenceTransition_Db(SimpleIncidenceTransition_Db trans) {
		CodeList resultList = new CodeList();
		CodeList curr = resultList;
		addImports("de.uni_koblenz.jgralab.greql2.schema.IncDirection");
		if (trans.getAllowedDirection() != IncDirection.BOTH) {
			addImports("de.uni_koblenz.jgralab.Direction");
			switch (trans.getAllowedDirection()) {
			case IN: 
				curr.add(new CodeSnippet("if (isVertex ^ (inc.getDirection() == Direction.VERTEX_TO_EDGE)) {" ));
			    break;
			case OUT: 
				curr.add(new CodeSnippet("if (isVertex ^ (inc.getDirection() == Direction.EDGE_TO_VERTEX)) {" ));
				break;
			}
		    CodeList body = new CodeList();
		    curr.add(body);
		    curr.add(new CodeSnippet("}"));
		    curr = body;
		}
		TypeCollection typeCollection = trans.getTypeCollection();
		if (typeCollection != null) {
			addStaticField("java.util.BitSet", "acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber, "new java.util.BitSet();");	
			if (typeCollection.getAllowedTypes().isEmpty()) {
				//all types but the forbidden ones are allowed
				addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(0," + schema.getNumberOfTypedElementClasses() + ", true);");
				for (TypedElementClass tc : typeCollection.getForbiddenTypes()) {
					addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(" + schema.getClassId(tc) +", false);" );
				}
			} else {
				//only allowed type are allowed, others are forbidden
				addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(0," + schema.getNumberOfTypedElementClasses() + ", false);");
				for (TypedElementClass tc : typeCollection.getAllowedTypes()) {
					addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(" + schema.getClassId(tc) +",  true);" );
				}
			}
			curr.add(new CodeSnippet("if (acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".get(inc.getType().getId())) {" ));	
			acceptedIncidenceTypesNumber++;
		    CodeList body = new CodeList();
		    curr.add(body);
		    curr.add(new CodeSnippet("}"));
		    curr = body;
		}
		VertexEvaluator predicateEval = trans.getPredicateEvaluator();
		if (predicateEval != null ) {
			//create code for the boolean expression restricting this transition
			//add class fields for this literals, TODO: only if thisIncidence/thisElement literals are used in this predicate
			createThisLiterals();
			CodeSnippet predicateSnippet = new CodeSnippet();
			predicateSnippet.add("thisIncidence = inc;");
			predicateSnippet.add("thisElement = element;");
			predicateSnippet.add("if (" + createCodeForExpression((Expression)predicateEval.getVertex()) + ") {");
		    CodeList body = new CodeList();
		    curr.add(body);
		    curr.addNoIndent(new CodeSnippet("}"));
		    curr = body;
		}
		//add element to queue
		curr.add(createAddToQueueSnippet(trans.endState.number));
		return resultList;
	}
	
	private CodeBlock createCodeForAggregationIncidenceTransition_Db(AggregationIncidenceTransition_Db trans) {
		CodeList resultList = new CodeList();
		CodeList curr = resultList;
		addImports("de.uni_koblenz.jgralab.greql2.schema.IncDirection");
		addImports("de.uni_koblenz.jgralab.schema.IncidenceType");
		{
		curr.add(new CodeSnippet("if (inc.getType().getIncidenceType() != IncidenceType.EDGE) {" ));
	    CodeList body = new CodeList();
	    curr.add(body);
	    curr.add(new CodeSnippet("}"));
	    curr = body;
		}
		TypeCollection typeCollection = trans.getTypeCollection();
		if (typeCollection != null) {
			addStaticField("java.util.BitSet", "acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber, "new java.util.BitSet();");	
			if (typeCollection.getAllowedTypes().isEmpty()) {
				//all types but the forbidden ones are allowed
				addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(0," + schema.getNumberOfTypedElementClasses() + ", true);");
				for (TypedElementClass tc : typeCollection.getForbiddenTypes()) {
					addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(" + schema.getClassId(tc) +", false);" );
				}
			} else {
				//only allowed type are allowed, others are forbidden
				addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(0," + schema.getNumberOfTypedElementClasses() + ", false);");
				for (TypedElementClass tc : typeCollection.getAllowedTypes()) {
					addStaticInitializer("acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".set(" + schema.getClassId(tc) +",  true);" );
				}
			}
			curr.add(new CodeSnippet("if (acceptedIncidenceTypes_" + acceptedIncidenceTypesNumber + ".get(inc.getType().getId())) {" ));	
			acceptedIncidenceTypesNumber++;
		    CodeList body = new CodeList();
		    curr.add(body);
		    curr.add(new CodeSnippet("}"));
		    curr = body;
		}
		VertexEvaluator predicateEval = trans.getPredicateEvaluator();
		if (predicateEval != null ) {
			//create code for the boolean expression restricting this transition
			//add class fields for this literals, TODO: only if thisIncidence/thisElement literals are used in this predicate
			createThisLiterals();
			CodeSnippet predicateSnippet = new CodeSnippet();
			predicateSnippet.add("thisIncidence = inc;");
			predicateSnippet.add("thisElement = element;");
			predicateSnippet.add("if (" + createCodeForExpression((Expression)predicateEval.getVertex()) + ") {");
		    CodeList body = new CodeList();
		    curr.add(body);
		    curr.add(new CodeSnippet("}"));
		    curr = body;
		}
		//add element to queue
		curr.add(createAddToQueueSnippet(trans.endState.number));
		return resultList;
	}
	
	//Helper methods
	/**
	 * Creates a method encapsulating the codelist given and returns the call of that method as a String
	 * @param list
	 * @return
	 */
	private String createMethod(CodeList list) {
		int methodNumber = createdMethods.size();
		String methodName = "internalEvaluationMethod_" + methodNumber;
		
		CodeList methodBlock = new CodeList();
		CodeSnippet start = new CodeSnippet();
		start.add("private Object " + methodName + "(#variables#) {");
		StringBuilder formalParams = new StringBuilder();
		StringBuilder actualParams = new StringBuilder();
		String delim = "";
		for (String s : scope.getDefinedVariables()) {
			formalParams.append(delim +  "Object " + s);
			actualParams.append(delim +  s);
			delim = ",";
		}
		start.setVariable("variables", formalParams.toString());
		methodBlock.add(start);
		methodBlock.add(list);
		CodeSnippet fin = new CodeSnippet("}");
		methodBlock.add(fin);
		createdMethods.add(methodBlock);
		
		return methodName + "(" + actualParams.toString() + ")";
	}

	
	private void createThisLiterals() {
		if (!thisLiteralsCreated) {
			thisLiteralsCreated = true;
			addClassField("Incidence", "thisIncidence", "null");
			addClassField("GraphElement", "thisGraphElement", "null");
		}
	}

	private void addStaticField(String type, String var, String def) {
		staticFieldSnippet.add("static " + type + " " + var + " = " + def + ";", "");
	}
	
	private void addClassField(String type, String var, String def) {
		classFieldSnippet.add(type + " " + var + " = " + def + ";", "");
	}

	private void addStaticInitializer(String statement) {
		staticInitializerSnippet.add(statement);		
	}


	public void createFiles(String pathPrefix) throws GraphIOException {
		String schemaPackage = rootBlock.getVariable("schemaPackage");
		createCode();
		writeCodeToFile(pathPrefix, this.classname + ".java", schemaPackage);
	}


	@Override
	protected CodeBlock createHeader() {
		CodeSnippet s = new CodeSnippet();
		s.add("public class " + classname + " extends AbstractExecutableQuery implements ExecutableQuery {");
		return s;
	}
	
	protected CodeBlock createPackageDeclaration() {
		CodeSnippet code = new CodeSnippet(true);
		code.add("package de.uni_koblenz.jgralab.greql2.executable.queries;");
		return code;
	}
	
}
