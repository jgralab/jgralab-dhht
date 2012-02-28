package de.uni_koblenz.jgralab.greql2.executable;

import java.util.LinkedList;
import java.util.List;

import com.sun.org.apache.xpath.internal.functions.FuncId;

import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.codegenerator.CodeBlock;
import de.uni_koblenz.jgralab.codegenerator.CodeGenerator;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.codegenerator.CodeList;
import de.uni_koblenz.jgralab.codegenerator.CodeSnippet;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.DoubleLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IntLiteral;
import de.uni_koblenz.jgralab.greql2.schema.IsArgumentOf_isArgumentOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf_isBoundVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf_isDeclaredVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsIdOfStoreClause_isIdOfStoreClause_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsQueryExprOf_isQueryExprOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf_isSimpleDeclOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.schema.Literal;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.StringLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

public class GreqlCodeGenerator extends CodeGenerator {

	GreqlSyntaxGraph graph;
	
	String classname;
	
	private int functionNumber = 1;
	
	CodeSnippet staticFieldSnippet = new CodeSnippet();
	
	List<CodeBlock> createdMethods = new LinkedList<CodeBlock>();
	
	Scope scope;
	
	public GreqlCodeGenerator(GreqlSyntaxGraph graph) {
		super("de.uni_koblenz.jgralab.greql2.executable", "queries", CodeGeneratorConfiguration.WITHOUT_TYPESPECIFIC_METHODS);
		this.graph = graph;
		classname = "SampleQuery";
		scope = new Scope();
		//this.classname = "Query_" + System.currentTimeMillis();
	}

	
	public CodeBlock createBody() {
		CodeList code = new CodeList();
		
		addImports("de.uni_koblenz.jgralab.greql2.executable.*");
		code.add(staticFieldSnippet);
		
		Greql2Expression rootExpr = graph.getFirstGreql2Expression();
		CodeSnippet method = new CodeSnippet();
		method.add("");
		method.add("public Object execute(de.uni_koblenz.jgralab.Graph graph, java.util.Map<String, Object> boundVariables) {");
		method.add("\tObject result = null;");

		
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
		
		return code;
	}
	


	
	private String createCodeForExpression(Expression queryExpr) {
		if (queryExpr instanceof FunctionApplication) {
			return createCodeForFunctionApplication((FunctionApplication) queryExpr);
		}
		if (queryExpr instanceof Comprehension) {
			return createCodeForComprehension((Comprehension) queryExpr);
		}
		//thisVertex and thisEdge should be handled separately before literal and variable
		//in path evaluators, direct access to the respecitve vertices needs to be 
		//encapulated
		if (queryExpr instanceof Variable) {
			return createCodeForVariable((Variable) queryExpr);
		}
		if (queryExpr instanceof Literal) {
			return createCodeForLiteral((Literal) queryExpr);
		}
		return "UnsupportedElement";
	}
	
	
	
	private String createCodeForComprehension(Comprehension compr) {
		addImports("org.pcollections.PCollection");
		addImports("de.uni_koblenz.jgralab.JGraLab");
		CodeList list = new CodeList();
		CodeSnippet initSnippet = new CodeSnippet();
		initSnippet.add("PCollection result;");
		if (compr instanceof ListComprehension) {
			initSnippet.add("result = JGraLab.vector();");
		}
		if (compr instanceof SetComprehension) {
			initSnippet.add("result = JGraLab.set();");
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

		//main expression
		Expression resultDefinition = (Expression) compr.getFirst_isCompResultDefOf_omega().getThat();
		CodeSnippet iteratedExprSnip = new CodeSnippet();
		iteratedExprSnip.add(tabs + "result = result.plus(" + createCodeForExpression(resultDefinition) + ");");
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
	
	
	
	private String createCodeForLiteral(Literal literal) {
		if (literal instanceof StringLiteral) {
			return "\"" + ((StringLiteral)literal).get_stringValue() + "\"";
		}
		if (literal instanceof IntLiteral) {
			return  Integer.toString(((IntLiteral)literal).get_intValue());
		}
		if (literal instanceof DoubleLiteral) {
			return  Double.toString(((DoubleLiteral)literal).get_doubleValue());
		}
		return "UndefinedLiteral";
	}


	private String createCodeForVariable(Variable var) {
		return var.get_name();
	}


	private String createCodeForFunctionApplication(
			FunctionApplication funApp) {
		addImports("de.uni_koblenz.jgralab.greql2.funlib.FunLib.FunctionInfo");
		addImports("de.uni_koblenz.jgralab.greql2.funlib.FunLib");
	
		//create static field to access function
		FunctionId funId = (FunctionId) funApp.getFirst_isFunctionIdOf_omega().getThat();		
		String functionInfoName = "_functionInfo_" + functionNumber;
		addStaticField("FunctionInfo", functionInfoName, "FunLib.getFunctionInfo(\"" + funId.get_name() + "\")" );	
		String functionName = FunLib.getFunctionInfo(funId.get_name()).getFunction().getClass().getName();
		String functionSimpleName = FunLib.getFunctionInfo(funId.get_name()).getFunction().getClass().getSimpleName();
		if (functionSimpleName.contains("."))
			functionSimpleName = functionSimpleName.substring(functionSimpleName.lastIndexOf("."));
		addStaticField(functionName, functionSimpleName + "_" + functionNumber, "(" + functionName + ") FunLib.getFunctionInfo(\"" + funId.get_name() + "\").getFunction()");		
		//create code list to evaluate function
		CodeList list = new CodeList();
		int paramSize = funApp.getDegree(IsArgumentOf_isArgumentOf_omega.class);
		String functionParameterArray = "_funParam_" + functionNumber;
		CodeSnippet paramInitSnippet = new CodeSnippet();
		//paramInitSnippet.add("Object[] " + functionParameterArray + " = new Object[" + paramSize + "];");
		addStaticField("Object[]", functionParameterArray, "new Object[" + paramSize + "]");
		list.add(paramInitSnippet);
		int argNum = 0;
		for (IsArgumentOf_isArgumentOf_omega argInc : funApp.getIsArgumentOf_isArgumentOf_omegaIncidences()) {
			Expression expr = (Expression) argInc.getThat();
			CodeSnippet argEvalSnippet = new CodeSnippet();
		//	argEvalSnippet.add("" + functionParameterArray + "[" + argNum++ + "] = " + createCodeForExpression(expr) + ";");
		//	list.add(argEvalSnippet);
		}
		list.add(new CodeSnippet("return " + functionSimpleName + "_" + functionNumber + ".evaluate("));
		String delim = "";
		for (IsArgumentOf_isArgumentOf_omega argInc : funApp.getIsArgumentOf_isArgumentOf_omegaIncidences()) {
			Expression expr = (Expression) argInc.getThat();
			CodeSnippet argEvalSnippet = new CodeSnippet();
			//TODO: Add casts depending on expression
			argEvalSnippet.add(delim + createCodeForExpression(expr));
			delim = ",";
			list.add(argEvalSnippet);
		}
		list.add(new CodeSnippet(");"));
		//list.add(new CodeSnippet("return FunLib.apply(" + functionInfoName + ", " + functionParameterArray + ");"));
		
		functionNumber++;
		return createMethod(list);
	}
	
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


	private void addStaticField(String type, String var, String def) {
		staticFieldSnippet.add("static " + type + " " + var + " = " + def + ";", "");
	}


	public void createFiles(String pathPrefix) throws GraphIOException {
		String schemaPackage = rootBlock.getVariable("schemaPackage");
		createCode();
		writeCodeToFile(pathPrefix, this.classname + ".java", schemaPackage);
	}


	@Override
	protected CodeBlock createHeader() {
		CodeSnippet s = new CodeSnippet();
		s.add("public class " + classname + " implements ExecutableQuery {");
		return s;
	}
	
	protected CodeBlock createPackageDeclaration() {
		CodeSnippet code = new CodeSnippet(true);
		code.add("package de.uni_koblenz.jgralab.greql2.executable.queries;");
		return code;
	}
	
}
