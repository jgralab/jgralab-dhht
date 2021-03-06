/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
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
package de.uni_koblenz.jgralab.greql2.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.exception.ParsingException;
import de.uni_koblenz.jgralab.greql2.funlib.FunLib;
import de.uni_koblenz.jgralab.greql2.schema.*;

public class GreqlParser extends ParserHelper {
	private Map<RuleEnum, int[]> testedRules = new HashMap<RuleEnum, int[]>();

	private List<Token> tokens = null;

	private int current = 0;

	private int farestOffset = 0;

	private ParsingException farestException = null;

	private Stack<Integer> parsingStack;

	private Stack<Boolean> predicateStack;

	private boolean predicateFulfilled = true;

	private Greql2Schema schema = null;

	private Set<String> subQueryNames = null;

	/**
	 * @return the set of variables which are valid at the current position in
	 *         the query
	 */
	public final Set<String> getValidVariables() {
		return duringParsingvariableSymbolTable.getKnownIdentifierSet();
	}

	private final void ruleSucceeds(RuleEnum rule, int pos) {
		int[] maySucceedArray = testedRules.get(rule);
		maySucceedArray[pos] = current;
	}

	/**
	 * Checks if the rule specified by <code>rule</code> was already tested at
	 * the current token position. If it was already tested, this method skips
	 * the number of tokens which were consumed by the rule in its last
	 * application at the current token
	 * 
	 * @param rule
	 *            the rule to test
	 * @return the current token position if the rule was not applied before or
	 *         -1, if the rule has already been applied successfully at this
	 *         current position in the token stream and thus no second test of
	 *         that rule is needed
	 * @throws ParsingException
	 *             if the rule has already failed at this position
	 */
	private final int alreadySucceeded(RuleEnum rule) {
		int[] maySucceedArray = testedRules.get(rule);
		if (maySucceedArray == null) {
			maySucceedArray = new int[tokens.size() + 1];
			for (int i = 0; i < maySucceedArray.length; i++) {
				maySucceedArray[i] = -1;
			}
			testedRules.put(rule, maySucceedArray);
		}
		int positionOfTokenAfterRule = maySucceedArray[current];
		if (inPredicateMode()) {
			if (positionOfTokenAfterRule == -1) { // not yet tested
				maySucceedArray[current] = -2;
			} else if (positionOfTokenAfterRule == -2) {// rule has not
				// succeeded, fail
				fail("Rule " + rule.toString() + " already tested at position "
						+ current + " Current Token " + lookAhead(0));
				return -2;
			} else {
				current = positionOfTokenAfterRule; // skip tokens consumed by
				// rule in last application
				return -1;
			}
		}
		return current;
	}

	/**
	 * Tests, if the application of the current rule can be skipped, should be
	 * used _only_ and _exclusively_ with the result of alreadySucceeded as
	 * parameter, example: int pos = alreadySucceeded(RuleEnum.EXPRESSION); if
	 * (skipRule(pos)) return null; Expression expr =
	 * parseQuantifiedExpression(); ruleSucceeded(RuleEnum.EXPRESSION, pos);
	 * return expr;
	 * 
	 * @return true if the rule application has already been tested and the
	 *         parser is still in predicate mode, so the rule and the tokens it
	 *         matched last time can be skipped, false otherwise
	 */
	private final boolean skipRule(int pos) {
		return (pos < 0) && inPredicateMode();
	}

	public GreqlParser(String source) {
		this(source, null);
	}

	public GreqlParser(String source, Set<String> subQueryNames) {
		query = source;
		parsingStack = new Stack<Integer>();
		predicateStack = new Stack<Boolean>();
		schema = Greql2Schema.instance();
		graph = schema.createGreqlSyntaxGraph_InMemoryStorage();
		tokens = GreqlLexer.scan(source);
		afterParsingvariableSymbolTable = new SymbolTable();
		duringParsingvariableSymbolTable = new SimpleSymbolTable();
		duringParsingvariableSymbolTable.blockBegin();
		functionSymbolTable = new HashMap<String, FunctionId>();
		graphCleaned = false;
		lookAhead = tokens.get(0);
		this.subQueryNames = subQueryNames;
	}

	protected final boolean isFunctionName(String ident) {
		return ((subQueryNames != null) && subQueryNames.contains(ident))
				|| FunLib.contains(ident);
	}

	public void parse() {
		try {
			parseQuery();
		} catch (ParsingException ex) {
			if (farestException != null) {
				throw farestException;
			} else {
				throw ex;
			}
		}
	}

	@Override
	protected void debug(String s) {
		for (int i = 0; i < parsingStack.size(); i++) {
			System.out.print("    ");
		}
		System.out.println(s);
	}

	private final TokenTypes lookAhead(int i) {
		if (current + i < tokens.size()) {
			return tokens.get(current + i).type;
		} else {
			return TokenTypes.EOF;
		}
	}

	protected final PVector<SourcePosition> createSourcePositionList(
			int startOffset) {
		PVector<SourcePosition> list = JGraLab.vector();
		return list.plus(new SourcePosition(getCurrentOffset() - startOffset,
				startOffset));
	}

	public static GreqlSyntaxGraph parse(String query) {
		return parse(query, null);
	}

	public static GreqlSyntaxGraph parse(String query, Set<String> subQueryNames) {
		GreqlParser parser = new GreqlParser(query, subQueryNames);
		parser.parse();
		return parser.getGraph();
	}

	private final ValueConstruction createPartsOfValueConstruction(
			List<VertexPosition<Expression>> expressions,
			ValueConstruction parent) {
		return (ValueConstruction) createMultipleEdgesToParent(expressions,
				parent, IsPartOf.class);
	}

	private final Vertex createMultipleEdgesToParent(
			List<VertexPosition<Expression>> expressions, Vertex parent,
			Class<? extends BinaryEdge> edgeClass) {
		if (expressions != null) {
			for (VertexPosition<? extends Vertex> expr : expressions) {
				Greql2Aggregation edge = (Greql2Aggregation) graph.createEdge(
						edgeClass, expr.node, parent);
				edge.set_sourcePositions(createSourcePositionList(expr.length,
						expr.offset));
			}
		}
		return parent;
	}

	private final Vertex createMultipleEdgesToParent(
			List<VertexPosition<TypeId>> expressions, Vertex parent,
			Class<? extends BinaryEdge> edgeClass, int i) {
		if (expressions != null) {
			for (VertexPosition<? extends Vertex> expr : expressions) {
				Greql2Aggregation edge = (Greql2Aggregation) graph.createEdge(
						edgeClass, expr.node, parent);
				edge.set_sourcePositions(createSourcePositionList(expr.length,
						expr.offset));
			}
		}
		return parent;
	}

	private final Vertex createMultipleEdgesToParent(
			List<VertexPosition<SimpleDeclaration>> expressions, Vertex parent,
			Class<? extends BinaryEdge> edgeClass, boolean b) {
		if (expressions != null) {
			for (VertexPosition<? extends Vertex> expr : expressions) {
				Greql2Aggregation edge = (Greql2Aggregation) graph.createEdge(
						edgeClass, expr.node, parent);
				edge.set_sourcePositions(createSourcePositionList(expr.length,
						expr.offset));
			}
		}
		return parent;
	}

	private final Vertex createMultipleEdgesToParent(
			List<VertexPosition<Variable>> expressions, Vertex parent,
			Class<? extends BinaryEdge> edgeClass, String s) {
		if (expressions != null) {
			for (VertexPosition<? extends Vertex> expr : expressions) {
				Greql2Aggregation edge = (Greql2Aggregation) graph.createEdge(
						edgeClass, expr.node, parent);
				edge.set_sourcePositions(createSourcePositionList(expr.length,
						expr.offset));
			}
		}
		return parent;
	}

	private final void predicateStart() {
		parsingStack.push(current);
		predicateStack.push(predicateFulfilled);
		predicateFulfilled = true;
	}

	private final void match() {
		current++;
		if (current < tokens.size()) {
			lookAhead = tokens.get(current);
		} else {
			lookAhead = null;
		}
	}

	@Override
	protected boolean inPredicateMode() {
		return !predicateStack.isEmpty();
	}

	private final boolean predicateHolds() {
		return predicateFulfilled;
	}

	private final boolean predicateEnd() {
		current = parsingStack.pop();
		if (current < tokens.size()) {
			lookAhead = tokens.get(current);
		} else {
			lookAhead = null;
		}
		boolean success = predicateFulfilled;
		predicateFulfilled = predicateStack.pop();
		return success;
	}

	private final void fail(String msg) {
		int offset = query.length();
		int length = -1;
		String tokenText = "";
		if (lookAhead != null) {
			offset = lookAhead.getOffset();
			length = lookAhead.getLength();
			tokenText = lookAhead.getValue();
		} else {
			tokenText = lookAhead(0).name();
		}
		ParsingException ex = new ParsingException(msg + " " + lookAhead(0),
				tokenText, offset, length, query);
		predicateFulfilled = false;
		if (getCurrentOffset() > farestOffset) {
			farestException = ex;
			farestOffset = getCurrentOffset();
		}
		throw ex;
	}

	private final String matchIdentifier() {
		if (lookAhead(0) == TokenTypes.IDENTIFIER) {
			String name = lookAhead.getValue();
			if (isValidIdentifier(name)) {
				match();
				return name;
			}
		}
		fail("expected identifier, but found");
		return null;
	}

	private final String matchSimpleName() {
		if (lookAhead(0) == TokenTypes.IDENTIFIER
				|| lookAhead(0) == TokenTypes.V || lookAhead(0) == TokenTypes.E) {
			String name = lookAhead.getValue();
			if (isValidSimpleName(name)) {
				match();
				return name;
			}
		}
		fail("expected simple name, but found");
		return null;
	}

	private final void match(TokenTypes type) {
		if (lookAhead(0) == type) {
			match();
		} else {
			fail("expected " + type + ", but found");
		}
	}

	private static final boolean isValidName(TokenTypes token) {
		switch (token) {
		case MAP:
		case AS:
		case IMPORT:
		case IN:
		case SET:
		case LIST:
		case REC:
		case FROM:
		case WITH:
		case REPORT:
		case WHERE:
		case LET:
			return true;
		default:
			return false;
		}
	}

	private final String matchPackageName() {
		if (((lookAhead(0) == TokenTypes.IDENTIFIER) || isValidName(lookAhead(0)))
				&& isValidPackageName(getLookAheadValue(0))) {
			StringBuilder name = new StringBuilder();
			name.append(lookAhead.getValue());
			match();
			boolean ph = true;
			do {
				if (lookAhead(0) == TokenTypes.DOT) {
					if (((lookAhead(1) == TokenTypes.IDENTIFIER) || isValidName(lookAhead(1)))
							&& isValidPackageName(getLookAheadValue(1))) {
						ph = true;
						match(TokenTypes.DOT);
						name.append(".");
						name.append(lookAhead.getValue());
						match();
					} else {
						ph = false;
					}
				} else {
					ph = false;
				}
			} while (ph);
			return name.toString();
		}
		fail("Package or type name expected, but found");
		return null;
	}

	private String getLookAheadValue(int i) {
		if (current + i < tokens.size()) {
			Token t = tokens.get(current + i);
			return t.getValue();
		} else {
			return null;
		}
	}

	private final String matchQualifiedName() {
		StringBuilder name = new StringBuilder();
		predicateStart();
		try {
			matchPackageName();
			match(TokenTypes.DOT);
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			name.append(matchPackageName());
			name.append(".");
			match(TokenTypes.DOT);
		}
		name.append(matchSimpleName());
		return name.toString();
	}

	private final static boolean isValidPackageName(String s) {
		if ((s == null) || (s.length() == 0)) {
			return false;
		}
		char[] chars = s.toCharArray();
		if (!Character.isLetter(chars[0]) || !Character.isLowerCase(chars[0])
				|| (chars[0] > 127)) {
			return false;
		}
		for (int i = 1; i < chars.length; i++) {
			if (!(Character.isLowerCase(chars[i])
					|| Character.isDigit(chars[i]) || (chars[i] == '_'))
					|| (chars[i] > 127)) {
				return false;
			}
		}
		return true;
	}

	private final static boolean isValidSimpleName(String s) {
		if ((s == null) || (s.length() == 0)) {
			return false;
		}
		char[] chars = s.toCharArray();
		if (!Character.isLetter(chars[0]) || !Character.isUpperCase(chars[0])
				|| (chars[0] > 127)) {
			return false;
		}
		for (int i = 1; i < chars.length; i++) {
			if (chars[i] > 127) {
				return false;
			}
		}
		return true;
	}

	private final static boolean isValidIdentifier(String s) {
		if ((s == null) || (s.length() == 0)) {
			return false;
		}
		char[] chars = s.toCharArray();
		if (!Character.isJavaIdentifierStart(chars[0])) {
			return false;
		}
		for (int i = 1; i < chars.length; i++) {
			if (!Character.isJavaIdentifierPart(chars[i])) {
				return false;
			}
		}
		return true;
	}

	private final void parseQuery() {
		if (lookAhead(0) == TokenTypes.EOF) {
			return;
		}
		Greql2Expression rootExpr = graph.createGreql2Expression();
		rootExpr.set_importedTypes(parseImports());
		if (lookAhead(0) == TokenTypes.USING) {
			match();
			List<VertexPosition<Variable>> varList = parseVariableList();
			for (VertexPosition<Variable> var : varList) {
				IsBoundVarOf isVarOf = graph.createIsBoundVarOf(var.node,
						rootExpr);
				isVarOf.set_sourcePositions(createSourcePositionList(
						var.length, var.offset));
			}
			match(TokenTypes.COLON);
		}
		int offset = getCurrentOffset();
		Expression expr = parseExpression();
		if (expr == null) {
			return;
		}
		IsQueryExprOf e = graph.createIsQueryExprOf(expr, rootExpr);
		e.set_sourcePositions(createSourcePositionList(offset));
		if (lookAhead(0) == TokenTypes.STORE) {
			match();
			match(TokenTypes.AS);
			Identifier ident = graph.createIdentifier();
			offset = getCurrentOffset();
			ident.set_name(matchIdentifier());
			IsIdOfStoreClause isId = graph.createIsIdOfStoreClause(ident,
					rootExpr);
			isId.set_sourcePositions(createSourcePositionList(offset));
		}
		match(TokenTypes.EOF);
		testIllegalThisLiterals();
		mergeVariablesInGreql2Expression(rootExpr);
	}

	private final PSet<String> parseImports() {
		PSet<String> importedTypes = JGraLab.set();
		while (lookAhead(0) == TokenTypes.IMPORT) {
			match(TokenTypes.IMPORT);
			StringBuilder importedType = new StringBuilder();
			importedType.append(matchPackageName());
			match(TokenTypes.DOT);
			if (lookAhead(0) == TokenTypes.STAR) {
				match(TokenTypes.STAR);
				importedType.append(".*");
			} else {
				importedType.append(".");
				importedType.append(matchSimpleName());
			}
			importedTypes = importedTypes.plus(importedType.toString());
			match(TokenTypes.SEMI);
		}
		return importedTypes;
	}

	private final List<VertexPosition<Variable>> parseVariableList() {
		List<VertexPosition<Variable>> vlist = new ArrayList<VertexPosition<Variable>>();
		int offset = getCurrentOffset();
		vlist.add(new VertexPosition<Variable>(parseVariable(true),
				getLength(offset), offset));
		while (lookAhead(0) == TokenTypes.COMMA) {
			match();
			vlist.add(new VertexPosition<Variable>(parseVariable(true),
					getLength(offset), offset));
		}
		return vlist;
	}

	private final Variable parseVariable(boolean inDeclaration) {
		String varName = matchIdentifier();
		Variable var = null;
		if (!inPredicateMode()) {
			var = graph.createVariable();
			var.set_name(varName);
		}
		if (inDeclaration) {
			duringParsingvariableSymbolTable.insert(varName, var);
		}
		return var;
	}

	private final Expression parseExpression() {
		int pos = alreadySucceeded(RuleEnum.EXPRESSION);
		if (skipRule(pos)) {
			return null;
		}
		Expression expr = parseSubgraphExpression();
		ruleSucceeds(RuleEnum.EXPRESSION, pos);
		return expr;
	}

	/**
	 * Parses the SubgraphDefinition used by a SubgraphExpression to define the
	 * context in which the constrained {@link Expression} is supposed to be
	 * evaluated.
	 * 
	 * The grammatical rules are:
	 * 
	 * SubgraphDefinition = KappaSubgraphDefintion | PartialSubgraphDefinition | LocalSubgraphDefinition |
	 * 		EdgeSubgraphDefinition | VertexSubgraphDefinition | NestedSubgraphDefinition;
	 * KappaSubgraphDefinition = "kappa(" Intliteral ")";
	 * PartialSubgraphDefinition = "partial(" Intliteral ")";
	 * LocalSubgraphDefinition = "local";
	 * EdgeSubgraphDefinition = "eSubgraph(" EdgeType ")";
	 * VertexSubgraphDefinition = "vSubgraph(" VertexType ")";
	 * NestedSubgraphDefinition = "nested(" Identifier ")";
	 * 
	 * @return A SubgraphDefinition-node
	 */
	private final SubgraphDefinition parseSubgraphDefinition() {
		switch (lookAhead(0)) {
		case KAPPA:
			return parseKappaSubgraphDefinition();
		case NESTED:
			return parseNestedSubgraphDefinition();
		case PARTIAL:
			return parsePartialSubgraphDefinition();
		case LOCAL:
			return parseLocalSubgraphDefinition();
		case VSUBGRAPH:
			return parseVertexSubgraphDefinition();
		case ESUBGRAPH:
			return parseEdgeSubgraphDefinition();
		default:
			fail("Expected SubgraphDefinition not found");
			return null;
		}
	}

	/**
	 * Parses an {@link EdgeSubgraphDefinition}, which is: "eSubgraph(EdgeType)".
	 * @return
	 */
	private SubgraphDefinition parseEdgeSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.ESUBGRAPH : "Entered parse of EdgeSubgraphDefinition without ESUBGRAPH-token!";
		EdgeSubgraphDefinition result = null;
		match();
		match(TokenTypes.LPAREN);
		Expression typeExpression = parseExpression();
		match(TokenTypes.RPAREN);
		if (!inPredicateMode()) {
			result = graph.createEdgeSubgraphDefinition();
			IsTypeExprOfSubgraphDefinition typeExpr = graph
					.createIsTypeExprOfSubgraphDefinition(typeExpression,
							result);
		}
		return result;
	}

	/**
	 * Parses a {@link VertexSubgraphDefinition}, which is: "vSubgaph(VertexType)".
	 * @return
	 */
	private SubgraphDefinition parseVertexSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.VSUBGRAPH : "Entered parse of VertexSubgraphDefinition without VSUBGRAPH-token!";
		VertexSubgraphDefinition result = null;
		match();
		match(TokenTypes.LPAREN);
		Expression typeExpression = parseExpression();
		match(TokenTypes.RPAREN);
		if (!inPredicateMode()) {
			result = graph.createVertexSubgraphDefinition();
			IsTypeExprOfSubgraphDefinition typeExpr = graph
					.createIsTypeExprOfSubgraphDefinition(typeExpression,
							result);
		}
		return result;
	}

	/**
	 * Method for parsing a LocalSubgraphDefinition, which is an access on the
	 * (logically) local partial graph.
	 * 
	 * @return A LocalSubgraphDefinition-node
	 */
	private final LocalSubgraphDefinition parseLocalSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.LOCAL : "Entered parse of LocalSubgraphDefinition without LOCAL-token!";
		LocalSubgraphDefinition result = null;
		match();
		if (!inPredicateMode()) {
			result = graph.createLocalSubgraphDefinition();
		}
		return result;
	}

	/**
	 * Method for parsing a PartialSubgraphDefinition, which is an access on a
	 * specific partial graph (i. e. "partial(1)" for the partial graph with id
	 * 1).
	 * 
	 * @return A PartialSubgraphDefinition-node
	 */

	private final PartialSubgraphDefinition parsePartialSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.PARTIAL : "Entered parse of a PartialSubgraphDefinition without PARTIAL-token!";
		PartialSubgraphDefinition partialDefinition = null;
		match();
		if (tryMatch(TokenTypes.LPAREN)) {
			IntLiteral id = (IntLiteral) parseLiteral();
			if (!inPredicateMode()) {
				partialDefinition = graph.createPartialSubgraphDefinition();
				IsIdOfPartialGraphDefinition idOf = graph
						.createIsIdOfPartialGraphDefinition(id,
								partialDefinition);
			}
			match(TokenTypes.RPAREN);
		} else {
			fail("Expected opening parenthesis, but found:");
		}
		return partialDefinition;
	}

	/**
	 * Method for parsing a NestedSubgraphDefinition, which is an access on the
	 * nested graph of a specific graph-element (i. e. the subgraph nested
	 * inside a vertex).
	 * 
	 * @return A NestedSubgraphDefinition-node
	 */

	private final NestedSubgraphDefinition parseNestedSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.NESTED : "Entered parse of NestedSubgraphDefinition without NESTED-token!";
		NestedSubgraphDefinition nestedDefinition = null;
		match();
		if (tryMatch(TokenTypes.LPAREN)) {

			if (lookAhead(0) == TokenTypes.IDENTIFIER) {
				Expression id = parseExpression();
				if (!inPredicateMode()) {
					nestedDefinition = graph.createNestedSubgraphDefinition();
					IsExprOfNestedSubgraphDefinition idOf = graph
							.createIsExprOfNestedSubgraphDefinition(id,
									nestedDefinition);
				}
			} else {
				fail("No identifier in nested-definition:");
			}
			match(TokenTypes.RPAREN);
		} else {
			fail("No opening parenthesis in nested-definition:");
		}
		return nestedDefinition;
	}

	/**
	 * Method for parsing a KappaSubgraphDefinition, which is an access on a
	 * view of the graph, defined by a visibility-level (kappa). Any elements
	 * with a kappa below the specified value will not be part of the context
	 * for the evaluation.
	 * 
	 * KappaSubgraphDefinition = "kappa(" IntLiteral ")";
	 * 
	 * @return A KappaSubgraphDefinition-node
	 */
	private final KappaSubgraphDefinition parseKappaSubgraphDefinition() {
		assert lookAhead(0) == TokenTypes.KAPPA : "Entered parse of KappaSubgraphDefinition without KAPPA-token!";
		KappaSubgraphDefinition kappaDefinition = null;
		match();

		if (tryMatch(TokenTypes.LPAREN)) {
			if (lookAhead(0) == TokenTypes.INTLITERAL) {
				int kappa = ((IntegerToken) lookAhead).getNumber().intValue();
				match();
				if (tryMatch(TokenTypes.RPAREN)) {
					if (!inPredicateMode()) {
						kappaDefinition = graph.createKappaSubgraphDefinition();
						kappaDefinition.set_kappa(kappa);
					}
				} else {
					fail("No closing parenthesis in kappa-definition:");
				}
			} else {
				fail("No valid number in kappa-definition:");
			}
		} else {
			fail("No opening parenthesis in kappa-definition:");
		}
		return kappaDefinition;
	}

	/**
	 * Parses a <code>SubgraphExpression</code>.
	 * 
	 * The grammatical rule for SubgraphExpressions is:
	 * 
	 * SubgraphExpression = "(" SubgraphDefinition ":" Expression ")";
	 * SubgraphDefinition = KappaSubgraphDefintion | PartialSubgraphDefinition | LocalSubgraphDefinition |
	 * 		EdgeSubgraphDefinition | VertexSubgraphDefinition | NestedSubgraphDefinition;
	 * @return
	 */
	private final Expression parseSubgraphExpression() {
		int pos = alreadySucceeded(RuleEnum.SUBGRAPHEXPRESSION);
		if (skipRule(pos)) {
			return null;
		}
		Expression result = null;
		if (lookAhead(0) == TokenTypes.LPAREN) {
			predicateStart();
			try {
				match(TokenTypes.LPAREN);
				parseSubgraphDefinition();
				match(TokenTypes.COLON);
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				match();
				int offsetDef = getCurrentOffset();
				SubgraphDefinition subgraphDef = parseSubgraphDefinition();
				match(TokenTypes.COLON);
				int lengthDef = getLength(offsetDef);
				int offsetRestrExpr = getCurrentOffset();
				Expression constrainedExpr = parseExpression();
				match(TokenTypes.RPAREN);
				if (!inPredicateMode()) {
					int lengthRestrExpr = getLength(offsetRestrExpr);
					SubgraphExpression subgraphExpr = graph
							.createSubgraphExpression();
					IsSubgraphDefinitionOf subgraphDefOf = graph
							.createIsSubgraphDefinitionOf(subgraphDef,
									subgraphExpr);
					subgraphDefOf.set_sourcePositions(createSourcePositionList(
							lengthDef, offsetDef));
					IsConstrainedExpressionOf constrainedExpression = graph
							.createIsConstrainedExpressionOf(constrainedExpr,
									subgraphExpr);
					constrainedExpression
							.set_sourcePositions(createSourcePositionList(
									lengthRestrExpr, offsetRestrExpr));
					result = subgraphExpr;
				}
			} else {
				result = parseParenthesedExpression();
			}
		} else {
			result = parseLetExpression();
		}
		ruleSucceeds(RuleEnum.SUBGRAPHEXPRESSION, pos);
		return result;
	}

	private final boolean tryMatch(TokenTypes type) {
		if (lookAhead(0) == type) {
			match();
			return true;
		}
		return false;
	}

	private final Quantifier parseQuantifier() {
		QuantificationType type = null;
		if (tryMatch(TokenTypes.FORALL)) {
			type = QuantificationType.FORALL;
		} else if (tryMatch(TokenTypes.EXISTS_ONE)) {
			type = QuantificationType.EXISTSONE;
		} else if (tryMatch(TokenTypes.EXISTS)) {
			type = QuantificationType.EXISTS;
		}
		if (type != null) {
			if (!inPredicateMode()) {
				for (de.uni_koblenz.jgralab.greql2.schema.Quantifier quantifier : graph
						.getQuantifierVertices()) {
					if (quantifier.get_type() == type) {
						return quantifier;
					}
				}
				Quantifier quantifier = graph.createQuantifier();
				quantifier.set_type(type);
				return quantifier;
			}
			return null;
		} else {
			fail("Expected a quantifier");
			return null;
		}
	}

	private final Expression parseQuantifiedExpression() {
		if ((lookAhead(0) == TokenTypes.EXISTS)
				|| (lookAhead(0) == TokenTypes.EXISTS_ONE)
				|| (lookAhead(0) == TokenTypes.FORALL)) {
			int offsetQuantifier = getCurrentOffset();
			int offsetQuantifiedDecl = 0;
			int offsetQuantifiedExpr = 0;
			int lengthQuantifier = 0;
			int lengthQuantifiedDecl = 0;
			int lengthQuantifiedExpr = 0;
			Quantifier quantifier = parseQuantifier();
			lengthQuantifier = getLength(offsetQuantifier);
			offsetQuantifiedDecl = getCurrentOffset();
			duringParsingvariableSymbolTable.blockBegin();
			Declaration decl = parseQuantifiedDeclaration();
			lengthQuantifiedDecl = getLength(offsetQuantifiedDecl);
			match(TokenTypes.AT);
			offsetQuantifiedExpr = getCurrentOffset();
			Expression boundExpr = parseSubgraphExpression();
			lengthQuantifiedExpr = getLength(offsetQuantifiedExpr);
			QuantifiedExpression quantifiedExpr = null;
			if (!inPredicateMode()) {
				quantifiedExpr = graph.createQuantifiedExpression();
				IsQuantifierOf quantifierOf = graph.createIsQuantifierOf(
						quantifier, quantifiedExpr);
				quantifierOf.set_sourcePositions(createSourcePositionList(
						lengthQuantifier, offsetQuantifier));
				// add declaration
				IsQuantifiedDeclOf quantifiedDeclOf = graph
						.createIsQuantifiedDeclOf(decl, quantifiedExpr);
				quantifiedDeclOf.set_sourcePositions(createSourcePositionList(
						lengthQuantifiedDecl, offsetQuantifiedDecl));
				// add predicate
				IsBoundExprOf boundExprOf = graph
						.createIsBoundExprOfQuantifiedExpr(boundExpr,
								quantifiedExpr);
				boundExprOf.set_sourcePositions(createSourcePositionList(
						lengthQuantifiedExpr, offsetQuantifiedExpr));
			}
			duringParsingvariableSymbolTable.blockEnd();
			return quantifiedExpr;
		} else {
			return parseConditionalExpression();
		}
	}

	private final Expression parseLetExpression() {
		if (lookAhead(0) == TokenTypes.LET) {
			match();
			duringParsingvariableSymbolTable.blockBegin();
			List<VertexPosition<Definition>> defList = parseDefinitionList();
			match(TokenTypes.IN);
			int offset = getCurrentOffset();
			Expression boundExpr = parseLetExpression();
			LetExpression result = null;
			if (!inPredicateMode() && !defList.isEmpty()) {
				int length = getLength(offset);
				result = graph.createLetExpression();
				IsBoundExprOf exprOf = graph.createIsBoundExprOfDefinition(
						boundExpr, result);
				exprOf.set_sourcePositions(createSourcePositionList(length,
						offset));
				for (VertexPosition<Definition> def : defList) {
					IsDefinitionOf definitionOf = graph.createIsDefinitionOf(
							def.node, result);
					definitionOf.set_sourcePositions(createSourcePositionList(
							def.length, def.offset));
				}
			}
			duringParsingvariableSymbolTable.blockEnd();
			return result;
		} else {
			return parseWhereExpression();
		}
	}

	private final Expression parseWhereExpression() {
		int offset = getCurrentOffset();
		Expression expr = parseQuantifiedExpression();
		if (tryMatch(TokenTypes.WHERE)) {
			int length = getLength(offset);
			List<VertexPosition<Definition>> defList = parseDefinitionList();
			WhereExpression result = null;
			if (!inPredicateMode()) {
				result = graph.createWhereExpression();
				IsBoundExprOf exprOf = graph.createIsBoundExprOfDefinition(
						expr, result);
				exprOf.set_sourcePositions(createSourcePositionList(length,
						offset));
				for (VertexPosition<Definition> def : defList) {
					IsDefinitionOf isDefOf = graph.createIsDefinitionOf(
							def.node, result);
					isDefOf.set_sourcePositions(createSourcePositionList(
							length, offset));
				}
			}
			return result;
		} else {
			return expr;
		}
	}

	private final List<VertexPosition<Definition>> parseDefinitionList() {
		List<VertexPosition<Definition>> definitions = null;
		if (!inPredicateMode()) {
			definitions = new ArrayList<VertexPosition<Definition>>();
		}
		do {
			int offset = getCurrentOffset();
			Definition v = parseDefinition();
			int length = getLength(offset);
			if (!inPredicateMode()) {
				definitions.add(new VertexPosition<Definition>(v, offset,
						length));
			}
		} while (tryMatch(TokenTypes.COMMA));
		return definitions;
	}

	private final Definition parseDefinition() {
		int offsetVar = getCurrentOffset();
		Variable var = parseVariable(true);
		int lengthVar = getLength(offsetVar);
		match(TokenTypes.ASSIGN);
		int offsetExpr = getCurrentOffset();
		Expression expr = parseExpression();
		int lengthExpr = getLength(offsetExpr);
		if (!inPredicateMode()) {
			Definition definition = graph.createDefinition();
			IsVarOf varOf = graph.createIsVarOf(var, definition);
			varOf.set_sourcePositions(createSourcePositionList(lengthVar,
					offsetVar));
			IsExprOf exprOf = graph.createIsExprOf(expr, definition);
			exprOf.set_sourcePositions(createSourcePositionList(lengthExpr,
					offsetExpr));
			return definition;
		}
		return null;
	}

	/**
	 * matches conditional expressions
	 * 
	 * @return
	 */
	private final Expression parseConditionalExpression() {
		int offsetExpr = getCurrentOffset();
		Expression result = parseOrExpression();
		int lengthExpr = getLength(offsetExpr);
		if (tryMatch(TokenTypes.QUESTION)) {
			int offsetTrueExpr = getCurrentOffset();
			Expression trueExpr = parseConditionalExpression();
			int lengthTrueExpr = getLength(offsetTrueExpr);
			match(TokenTypes.COLON);
			int offsetFalseExpr = getCurrentOffset();
			Expression falseExpr = parseConditionalExpression();
			int lengthFalseExpr = getLength(offsetFalseExpr);
			if (!inPredicateMode()) {
				ConditionalExpression condExpr = graph
						.createConditionalExpression();
				// add condition
				IsConditionOf conditionOf = graph.createIsConditionOf(result,
						condExpr);
				conditionOf.set_sourcePositions(createSourcePositionList(
						lengthExpr, offsetExpr));
				// add true-expression
				IsTrueExprOf trueExprOf = graph.createIsTrueExprOf(trueExpr,
						condExpr);
				trueExprOf.set_sourcePositions(createSourcePositionList(
						lengthTrueExpr, offsetTrueExpr));
				// add false-expression
				IsFalseExprOf falseExprOf = graph.createIsFalseExprOf(
						falseExpr, condExpr);
				falseExprOf.set_sourcePositions(createSourcePositionList(
						lengthFalseExpr, offsetFalseExpr));
				result = condExpr;
			}
		}
		return result;
	}

	private final Expression parseOrExpression() {
		FunctionConstruct construct = new FunctionConstruct();
		construct.preArg1();
		Expression expr = parseXorExpression();
		construct.preOp(expr);
		if (tryMatch(TokenTypes.OR)) {
			construct.postOp("or");
			return construct.postArg2(parseOrExpression());
		}
		return expr;
	}

	private final Expression parseXorExpression() {
		FunctionConstruct construct = new FunctionConstruct();
		construct.preArg1();
		Expression expr = parseAndExpression();
		construct.preOp(expr);
		if (tryMatch(TokenTypes.XOR)) {
			construct.postOp("xor");
			return construct.postArg2(parseXorExpression());
		}
		return expr;
	}

	private final Expression parseAndExpression() {
		FunctionConstruct construct = new FunctionConstruct();
		construct.preArg1();
		Expression expr = parseEqualityExpression();
		construct.preOp(expr);
		if (tryMatch(TokenTypes.AND)) {
			construct.postOp("and");
			return construct.postArg2(parseAndExpression());
		}
		return expr;
	}

	private final Expression parseEqualityExpression() {
		FunctionConstruct construct = new FunctionConstruct();
		construct.preArg1();
		Expression expr = parseRelationalExpression();
		construct.preOp(expr);
		if (tryMatch(TokenTypes.EQUAL)) {
			construct.postOp("equals");
			return construct.postArg2(parseEqualityExpression());
		} else if (tryMatch(TokenTypes.NOT_EQUAL)) {
			construct.postOp("nequals");
			return construct.postArg2(parseEqualityExpression());
		}
		return expr;
	}

	private final Expression parseRelationalExpression() {
		FunctionConstruct construct = new FunctionConstruct();
		construct.preArg1();
		Expression expr = parseAdditiveExpression();
		construct.preOp(expr);
		String name = null;
		if (tryMatch(TokenTypes.L_T)) {
			name = "leThan";
		} else if (tryMatch(TokenTypes.LE)) {
			name = "leEqual";
		} else if (tryMatch(TokenTypes.GE)) {
			name = "grEqual";
		} else if (tryMatch(TokenTypes.G_T)) {
			name = "grThan";
		} else if (tryMatch(TokenTypes.MATCH)) {
			name = "reMatch";
		}
		if (name != null) {
			construct.postOp(name);
			return construct.postArg2(parseRelationalExpression());
		}
		return expr;
	}

	private final Expression parseAdditiveExpression() {
		FunctionConstruct construct = null;
		String name = null;
		Expression expr = null;
		do {
			if (construct == null) {
				construct = new FunctionConstruct();
				construct.preArg1();
				expr = parseMultiplicativeExpression();
			} else {
				construct = new FunctionConstruct(construct);
			}
			name = null;
			construct.preOp(expr);
			if (tryMatch(TokenTypes.PLUS)) {
				name = "add";
			} else if (tryMatch(TokenTypes.MINUS)) {
				name = "sub";
			} else if (tryMatch(TokenTypes.PLUSPLUS)) {
				name = "concat";
			}
			if (name != null) {
				construct.postOp(name);
				expr = construct.postArg2(parseMultiplicativeExpression());
			}
		} while (name != null);
		return expr;
	}

	private final Expression parseMultiplicativeExpression() {
		FunctionConstruct construct = null;
		String name = null;
		Expression expr = null;
		do {
			if (construct == null) {
				construct = new FunctionConstruct();
				construct.preArg1();
				expr = parseUnaryExpression();
			} else {
				construct = new FunctionConstruct(construct);
			}
			name = null;
			construct.preOp(expr);
			if (tryMatch(TokenTypes.STAR)) {
				name = "mul";
			} else if (tryMatch(TokenTypes.MOD)) {
				name = "mod";
			} else if (tryMatch(TokenTypes.DIV)) {
				name = "div";
			}
			if (name != null) {
				construct.postOp(name);
				expr = construct.postArg2(parseUnaryExpression());
			}
		} while (name != null);
		return expr;
	}

	private final Expression parseUnaryExpression() {
		FunctionConstruct construct = null;
		if ((lookAhead(0) == TokenTypes.NOT)
				|| (lookAhead(0) == TokenTypes.MINUS)) {
			construct = new FunctionConstruct();
			construct.preUnaryOp();
			String opName = null;
			if (tryMatch(TokenTypes.NOT)) {
				opName = "not";
			} else if (tryMatch(TokenTypes.MINUS)) {
				opName = "neg";
			}
			if (!inPredicateMode()) {
				getFunctionId(opName);
			}
			construct.postOp(opName);
		}
		Expression expr = parsePathExpression();
		if (construct != null) {
			return construct.postArg2(expr);
		}
		return expr;
	}

	private final RoleId parseRoleId() {
		String ident = matchIdentifier();
		if (!inPredicateMode()) {
			RoleId roleId = graph.createRoleId();
			roleId.set_name(ident);
			return roleId;
		}
		return null;
	}

	private final Identifier parseIdentifier() {
		String name = matchIdentifier();
		if (!inPredicateMode()) {
			Identifier ident = graph.createIdentifier();
			ident.set_name(name);
			return ident;
		}
		return null;
	}

	private final Expression parseValueAccess() {
		int offset = getCurrentOffset();
		Expression expr = parsePrimaryExpression();
		int length = getLength(offset);
		boolean secondPart = false;
		if (lookAhead(0) == TokenTypes.DOT) {
			secondPart = true;
		}
		if (lookAhead(0) == TokenTypes.LBRACK) {
			predicateStart();
			try {
				match(TokenTypes.LBRACK);
				parsePrimaryPathDescription(); // TODO: pathDescription statt
				// PrimaryPathDescription?
			} catch (ParsingException ex) {
			}
			if (!predicateEnd()) {
				secondPart = true;
			}
		}
		if (secondPart) {
			return parseValueAccess2(expr, offset, length);
		}
		return expr;
	}

	private final Expression parseValueAccess2(Expression arg1, int offsetArg1,
			int lengthArg1) {
		String name = "get";
		int offsetOperator = getCurrentOffset();
		int lengthOperator = 0;
		int lengthArg2 = 0;
		int offsetArg2 = 0;
		Expression arg2 = null;
		if (tryMatch(TokenTypes.DOT)) {
			name = "getValue";
			lengthOperator = 1;
			offsetArg2 = getCurrentOffset();
			arg2 = parseIdentifier();
		} else if (tryMatch(TokenTypes.LBRACK)) {
			offsetArg2 = getCurrentOffset();
			arg2 = parseExpression();
			lengthArg2 = getLength(offsetArg2);
			match(TokenTypes.RBRACK);
			lengthOperator = getLength(offsetOperator);
		}
		Expression result = null;
		if (!inPredicateMode()) {
			result = createFunctionIdAndArgumentOf(getFunctionId(name),
					offsetOperator, lengthOperator, arg1, offsetArg1,
					lengthArg1, arg2, offsetArg2, lengthArg2, true);
		}
		boolean secondPart = false;
		if (lookAhead(0) == TokenTypes.DOT) {
			secondPart = true;
		}
		if (lookAhead(0) == TokenTypes.LBRACK) {
			predicateStart();
			try {
				match(TokenTypes.LBRACK);
				parsePrimaryPathDescription(); // TODO: pathDescription statt
				// PrimaryPathDescription?
			} catch (ParsingException ex) {
			}
			if (!predicateEnd()) {
				secondPart = true;
			}
		}
		if (secondPart) {
			return parseValueAccess2(result, offsetArg1, getLength(offsetArg2));
		}
		return result;
	}

	private final Expression parsePrimaryExpression() {
		if (lookAhead(0) == TokenTypes.LPAREN) {
			return parseParenthesedExpression();
		}

		if ((lookAhead(0) == TokenTypes.V) || (lookAhead(0) == TokenTypes.E)) {
			return parseRangeExpression();
		}

		predicateStart();
		try {
			parseAltPathDescription();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			return parseAltPathDescription();
		}

		if (((lookAhead(0) == TokenTypes.IDENTIFIER)
				|| (lookAhead(0) == TokenTypes.AND)
				|| (lookAhead(0) == TokenTypes.NOT)
				|| (lookAhead(0) == TokenTypes.XOR) || (lookAhead(0) == TokenTypes.OR))
				&& ((lookAhead(1) == TokenTypes.LCURLY) || (lookAhead(1) == TokenTypes.LPAREN))) {
			predicateStart();
			try {
				parseFunctionApplication();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				return parseFunctionApplication();
			}
		}

		predicateStart();
		try {
			parseValueConstruction();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			return parseValueConstruction();
		}
		// System.out.println("LA1: " + lookAhead.getValue());
		predicateStart();
		try {
			parseVariable(false);
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			// System.out.println("LA2: " + lookAhead.getValue());
			return parseVariable(false);
		}
		predicateStart();
		try {
			parseLiteral();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			return parseLiteral();
		}
		if (lookAhead(0) == TokenTypes.FROM) {
			return parseFWRExpression();
		}
		fail("Unrecognized token");
		return null;
	}

	private final Expression parseParenthesedExpression() {
		predicateStart();
		try {
			parseAltPathDescription();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			Expression expr = parseAltPathDescription();
			return expr;
		}
		match(TokenTypes.LPAREN);
		Expression expr = parseExpression();
		match(TokenTypes.RPAREN);
		return expr;
	}

	private final PathDescription parseAltPathDescription() {
		int pos = alreadySucceeded(RuleEnum.ALTERNATIVE_PATH_DESCRIPTION);
		if (skipRule(pos)) {
			return null;
		}
		int offsetPart1 = getCurrentOffset();
		PathDescription part1 = parseIntermediateVertexPathDescription();
		int lengthPart1 = getLength(offsetPart1);
		if (tryMatch(TokenTypes.BOR)) {
			int offsetPart2 = getCurrentOffset();
			PathDescription part2 = parseAltPathDescription();
			int lengthPart2 = getLength(offsetPart2);
			if (!inPredicateMode()) {
				part1 = addPathElement(AlternativePathDescription.class,
						IsAlternativePathOf.class, null, part1, offsetPart1,
						lengthPart1, part2, offsetPart2, lengthPart2);
			}
		}
		ruleSucceeds(RuleEnum.ALTERNATIVE_PATH_DESCRIPTION, pos);
		return part1;
	}

	private final PathDescription parseIntermediateVertexPathDescription() {
		int offsetPart1 = getCurrentOffset();
		PathDescription part1 = parseSequentialPathDescription();
		int lengthPart1 = getLength(offsetPart1);
		predicateStart();
		try {
			parseValueAccess();
			if (predicateHolds()) {
				parseSequentialPathDescription();
			}
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			int offsetExpr = getCurrentOffset();
			Expression restrExpr = parseValueAccess();
			int lengthExpr = getLength(offsetExpr);
			int offsetPart2 = getCurrentOffset();
			PathDescription part2 = parseIntermediateVertexPathDescription();
			int lengthPart2 = getLength(offsetPart2);
			IntermediateVertexPathDescription result = null;
			if (!inPredicateMode()) {
				result = (IntermediateVertexPathDescription) addPathElement(
						IntermediateVertexPathDescription.class,
						IsSubPathOf.class, null, part1, offsetPart1,
						lengthPart1, part2, offsetPart2, lengthPart2);
				IsIntermediateVertexOf intermediateVertexOf = graph
						.createIsIntermediateVertexOf(restrExpr, result);
				intermediateVertexOf
						.set_sourcePositions(createSourcePositionList(
								lengthExpr, offsetExpr));
			}
			return result;
		}
		return part1;
	}

	private final PathDescription parseSequentialPathDescription() {
		int offsetPart1 = getCurrentOffset();
		PathDescription part1 = parseStartRestrictedPathDescription();
		int lengthPart1 = getLength(offsetPart1);
		predicateStart();
		try {
			parseSequentialPathDescription();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			int offsetPart2 = getCurrentOffset();
			PathDescription part2 = parseSequentialPathDescription();
			int lengthPart2 = getLength(offsetPart2);
			// If there is a LCURLY it must be an ElmentRestriction
			if (lookAhead(0) == TokenTypes.LCURLY) {
				predicateStart();
				match(TokenTypes.LCURLY);
				parseElementRestriction();
				match(TokenTypes.RCURLY);
				if (predicateEnd()) {
					match(TokenTypes.LCURLY);
					ElementRestriction elementRest = parseElementRestriction();
					match(TokenTypes.RCURLY);
					if (!inPredicateMode()) {
						graph.createIsEndRestrictionOf(elementRest, part2);
					}
				}
			}
			if (!inPredicateMode()) {
				return addPathElement(SequentialPathDescription.class,
						IsSequenceElementOf.class, null, part1, offsetPart1,
						lengthPart1, part2, offsetPart2, lengthPart2);
			} else {
				return null;
			}
		}
		return part1;
	}

	private final PathDescription parseStartRestrictedPathDescription() {
		int offsetRest = getCurrentOffset();
		ElementRestriction restriction = null;
		int lengthRestr = 0;
		int offsetRestr = 0;
		if (tryMatch(TokenTypes.LCURLY)) {
			predicateStart();
			try {
				parseElementRestriction();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				offsetRestr = getCurrentOffset();
				restriction = parseElementRestriction();
				lengthRestr = getLength(offsetRestr);
				match(TokenTypes.RCURLY);
				tryMatch(TokenTypes.AMP); // We just have to remove a possible
											// AMP because of legacy
											// restrictions ("{ Restr } & -->")
			} else {
				fail("No valid start restriction found:");
			}
		}
		PathDescription pathDescr = parseGoalRestrictedPathDescription();
		if (!inPredicateMode()) {
			if (restriction != null) {
				IsStartRestrictionOf startRestrictionOf = graph
						.createIsStartRestrictionOf(restriction, pathDescr);
				startRestrictionOf
						.set_sourcePositions(createSourcePositionList(
								lengthRestr, offsetRest));
			}
		}
		return pathDescr;
	}

	private final PathDescription parseGoalRestrictedPathDescription() {
		PathDescription pathDescr = parseIteratedOrTransposedPathDescription();
		tryMatch(TokenTypes.AMP); // EndRestriction might start with an AMP
									// because of legacy restriction types
		if (tryMatch(TokenTypes.LCURLY)) {
			int offset = getCurrentOffset();
			ElementRestriction elementRestriction = parseElementRestriction();
			int length = getLength(offset);
			if (!inPredicateMode()) {
				IsEndRestrictionOf endRestictionOf = graph
						.createIsEndRestrictionOf(elementRestriction, pathDescr);
				endRestictionOf.set_sourcePositions(createSourcePositionList(
						length, offset));
			}
			match(TokenTypes.RCURLY);
		}
		return pathDescr;
	}

	private final PathDescription parseIteratedOrTransposedPathDescription() {
		int offsetPath = getCurrentOffset();
		PathDescription pathDescr = parsePrimaryPathDescription();
		int lengthPath = getLength(offsetPath);
		if ((lookAhead(0) == TokenTypes.STAR)
				|| (lookAhead(0) == TokenTypes.PLUS)
				|| (lookAhead(0) == TokenTypes.CARET)) {
			return parseIteration(pathDescr, offsetPath, lengthPath);
		}
		return pathDescr;
	}

	private final PathDescription parseIteration(PathDescription iteratedPath,
			int offsetPath, int lengthPath) {
		IterationType iteration = null;
		PathDescription result = null;
		if (tryMatch(TokenTypes.STAR)) {
			iteration = IterationType.STAR;
		} else if (tryMatch(TokenTypes.PLUS)) {
			iteration = IterationType.PLUS;
		}
		if (iteration != null) {
			if (!inPredicateMode()) {
				IteratedPathDescription ipd = graph
						.createIteratedPathDescription();
				ipd.set_times(iteration);
				IsIteratedPathOf iteratedPathOf = graph.createIsIteratedPathOf(
						iteratedPath, ipd);
				iteratedPathOf.set_sourcePositions(createSourcePositionList(
						lengthPath, offsetPath));
				result = ipd;
			}
		} else if (tryMatch(TokenTypes.CARET)) {
			if (tryMatch(TokenTypes.T)) {
				if (!inPredicateMode()) {
					TransposedPathDescription tpd = graph
							.createTransposedPathDescription();
					IsTransposedPathOf transposedPathOf = graph
							.createIsTransposedPathOf(iteratedPath, tpd);
					transposedPathOf
							.set_sourcePositions(createSourcePositionList(
									lengthPath, offsetPath));
					result = tpd;
				}
			} else {
				int offsetExpr = getCurrentOffset();
				Expression ie = parseNumericLiteral();
				if (!inPredicateMode()) {
					if (!(ie instanceof IntLiteral)) {
						fail("Expected integer constant as iteration quantifier or T, but found");
					}
					int lengthExpr = getLength(offsetExpr);
					ExponentiatedPathDescription epd = graph
							.createExponentiatedPathDescription();
					IsExponentiatedPathOf exponentiatedPathOf = graph
							.createIsExponentiatedPathOf(iteratedPath, epd);
					exponentiatedPathOf
							.set_sourcePositions(createSourcePositionList(
									lengthPath, offsetPath));
					IsExponentOf exponentOf = graph.createIsExponentOf(
							(IntLiteral) ie, epd);
					exponentOf.set_sourcePositions(createSourcePositionList(
							lengthExpr, offsetExpr));
					result = epd;
				}
			}
		} else {
			fail("No iteration at iterated path description");
		}
		if ((lookAhead(0) == TokenTypes.STAR)
				|| (lookAhead(0) == TokenTypes.PLUS)
				|| (lookAhead(0) == TokenTypes.CARET)) {
			return parseIteration(result, offsetPath, getLength(offsetPath));
		}
		return result;
	}

	private final PathDescription parsePrimaryPathDescription() {
		if (lookAhead(0) == TokenTypes.LPAREN) {
			predicateStart();
			try {
				match(TokenTypes.LPAREN);
				parseAltPathDescription();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				match(TokenTypes.LPAREN);
				PathDescription pathDescr = parseAltPathDescription();
				match(TokenTypes.RPAREN);
				return pathDescr;
			}
		}
		if ((lookAhead(0) == TokenTypes.OUTAGGREGATION)
				|| (lookAhead(0) == TokenTypes.INAGGREGATION)) {
			return parseAggregationPathDescription();
		}
		if ((lookAhead(0) == TokenTypes.RARROW)
				|| (lookAhead(0) == TokenTypes.LARROW)
				|| (lookAhead(0) == TokenTypes.ARROW)) {
			return parseSimpleEdgePathDescription();
		}
		if ((lookAhead(0) == TokenTypes.SLARROW)
				|| (lookAhead(0) == TokenTypes.SRARROW)
				|| (lookAhead(0) == TokenTypes.EDGE)) {
			return parseEdgePathDescription();
		}
		if ((lookAhead(0) == TokenTypes.IARROW)
				|| (lookAhead(0) == TokenTypes.ILARROW)
				|| (lookAhead(0) == TokenTypes.IRARROW)) {
			return parseSimpleIncidencePathDescription();
		}
		if (tryMatch(TokenTypes.LBRACK)) {
			int offset = getCurrentOffset();
			PathDescription pathDescr = parseAltPathDescription();
			int length = getLength(offset);
			match(TokenTypes.RBRACK);
			if (!inPredicateMode()) {
				OptionalPathDescription optPathDescr = graph
						.createOptionalPathDescription();
				IsOptionalPathOf optionalPathOf = graph.createIsOptionalPathOf(
						pathDescr, optPathDescr);
				optionalPathOf.set_sourcePositions(createSourcePositionList(
						length, offset));
				return optPathDescr;
			}
			return null;
		}
		fail("Unrecognized token");
		return null;
	}

	/**
	 * Parses a SimpleIncidencePathDescription (which is an incidence-arrow: +>,
	 * <+, <+>).
	 * 
	 * @return A PrimaryPathDescription-node
	 */

	private final PrimaryPathDescription parseSimpleIncidencePathDescription() {
		IncidenceDirection dir = null;
		SimpleIncidencePathDescription result = null;
		IncDirection direction = IncDirection.BOTH;
		if (tryMatch(TokenTypes.IRARROW)) {
			direction = IncDirection.OUT;
		} else if (tryMatch(TokenTypes.ILARROW)) {
			direction = IncDirection.IN;
		} else {
			match(TokenTypes.IARROW);
		}
		if (!inPredicateMode()) {
			result = graph.createSimpleIncidencePathDescription();
			dir = (IncidenceDirection) graph
					.getFirstVertex(IncidenceDirection.class);
			while (dir != null) {
				if (!dir.get_dir().equals(direction)) {
					dir = dir.getNextIncidenceDirection();
				} else {
					break;
				}
			}
			if (dir == null) {
				dir = graph.createIncidenceDirection();
				dir.set_dir(direction);
			}
			graph.createIsIncDirectionOf(dir, result);

		}
		// A LCURLY after an Incidence-arrow can be either an
		// IncidenceRestriction (restriction of the incidence itself) or an
		// ElementRestriction (restriction of the results)
		// An IncidenceRestriction has to be parsed with it's corresponding
		// Incidence, the ElementRestriction must be parsed outside.
		if (lookAhead(0) == TokenTypes.LCURLY) {
			predicateStart();
			try {
				match(TokenTypes.LCURLY);
				parseRoleList();
				match(TokenTypes.RCURLY);
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				match(TokenTypes.LCURLY);
				List<VertexPosition<TypeId>> restr = parseRoleList();
				match(TokenTypes.RCURLY);
				if (!inPredicateMode()) {
					for (VertexPosition<TypeId> restriction : restr) {
						IncidenceRestriction incRestr = graph
								.createIncidenceRestriction();
						// Connect each type to its restriction
						graph.createIsIncTypeIdOf(restriction.node, incRestr);
						// Connect each restriction to the PathDescription-node
						graph.createIsIncRestrOf(incRestr, result);
					}
				}

			}
		}
		return result;
	}

	private List<VertexPosition<TypeId>> parseRoleList() {
		List<VertexPosition<TypeId>> list = new ArrayList<VertexPosition<TypeId>>();

		do {
			int offset = getCurrentOffset();
			TypeId t = parseRoleName();
			int length = getLength(offset);
			list.add(new VertexPosition<TypeId>(t, length, offset));
			if (lookAhead(0) == TokenTypes.COLON) {
				fail("COLON found in TypeId");
			}
		} while (tryMatch(TokenTypes.COMMA));
		return list;
	}

	private TypeId parseRoleName() {
		TypeId type = null;
		if (!inPredicateMode()) {
			type = graph.createTypeId();
		}

		String s = matchRoleName();
		if (!inPredicateMode()) {
			type.set_name(s);
		}

		return type;
	}

	private String matchRoleName() {
		if (lookAhead(0) == TokenTypes.IDENTIFIER) {
			String name = lookAhead.getValue();
			if (isValidIdentifier(name)) {
				match();
				return name;
			}
		}
		fail("expected role name, but found");
		return null;
	}

	/**
	 * Parses an ElementRestriction. Possible types are: {V:VertexTypeList},
	 * {E:EdgeTypeList}, {VE: VertexAndEdgeTypeList}, {ElementSet}, {TypeList @
	 * Expression}.
	 * 
	 * The grammatical rules are:
	 * 
	 * ElementRestriction = "{" ElementSetRestriction | ElementTypeRestriction "}";
	 * ElementSetRestriction = Expression;
	 * ElementTypeRestriction = ("E:" | "V:" | "VE:") TypeList;
	 * 
	 * In this context a TypeList is either exclusively edges, exclusive vertices, or both. Depending on
	 * how it is specified (by E, V or VE).
	 * 
	 * @return An ElementRestriction-node
	 */
	private ElementRestriction parseElementRestriction() {
		// Have to account for legacy Start- and GoalRestrictions first ({Type,
		// Type, Type @ exp}).
		predicateStart();
		try {
			parseTypeRestriction(RestrictionType.BOTH);
			if (tryMatch(TokenTypes.AT)) {
				parseExpression();
			}
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			ElementRestriction result = parseTypeRestriction(RestrictionType.BOTH);
			if (tryMatch(TokenTypes.AT)) {
				Expression expr = parseExpression();
				if (!inPredicateMode()) {
					IsExpressionOfRestriction exprOfRestr = graph
							.createIsExpressionOfRestriction(expr, result);
				}
			}
			return result;
		} else {
			switch (lookAhead(0)) {
			case E: {
				predicateStart();
				try {
					match(TokenTypes.E);
					// Starting with E can only mean EdgeTypeList (if a colon
					// follows)
					match(TokenTypes.COLON);
					parseTypeRestriction(RestrictionType.EDGE);
				} catch (ParsingException ex) {
				}
				if (predicateEnd()) {
					match(TokenTypes.E);
					match(TokenTypes.COLON);
					return parseTypeRestriction(RestrictionType.EDGE);
				} else {
					// or an ElementSet (e. g. restricting to all Edges)
					return parseElementSetRestriction();
				}

			}
			case V: {
				predicateStart();
				try {
					match(TokenTypes.V);
					// Starting with V can mean VertexTypeList (if a colon
					// follows)...
					match(TokenTypes.COLON);
					parseTypeRestriction(RestrictionType.VERTEX);
				} catch (ParsingException ex) {
				}
				if (predicateEnd()) {
					match(TokenTypes.V);
					match(TokenTypes.COLON);
					return parseTypeRestriction(RestrictionType.VERTEX);
					// Or a VertexAndEdgeTypeList (if an E follows)...
				}
				predicateStart();
				try {
					match(TokenTypes.E);
					match(TokenTypes.COLON);
				} catch (ParsingException ex) {
				}
				if (predicateEnd()) {
					match(TokenTypes.E);
					match(TokenTypes.COLON);

					return parseTypeRestriction(RestrictionType.BOTH);
				} else {
					// Or an ElementSet
					return parseElementSetRestriction();
				}
			}
			}
		}
		return null;
	}

	private ElementRestriction parseElementSetRestriction() {
		Expression expr = parseExpression();
		if (expr != null) {
			ElementSetRestriction setRestriction = graph
					.createElementSetRestriction();
			graph.createIsExpressionOfRestriction(expr, setRestriction);
			return setRestriction;
		} else {
			fail("No Expression found in ElementSetRestriction");
		}
		return null;
	}

	private ElementRestriction parseTypeRestriction(RestrictionType rType) {
		List<VertexPosition<TypeId>> typeList = new ArrayList<VertexPosition<TypeId>>();

		typeList = parseTypeExpressionList();
		if (!inPredicateMode()) {
			if (typeList != null && typeList.size() > 0) {
				ElementTypeRestriction typeRestriction = graph
						.createElementTypeRestriction();
				typeRestriction.set_restrType(rType);
				for (VertexPosition<TypeId> type : typeList) {
					graph.createIsTypeIdOfRestriction(type.node,
							typeRestriction);
				}
				return typeRestriction;
			}
		}
		return null;
	}

	private final PrimaryPathDescription parseSimpleEdgePathDescription() {
		EdgeDirection dir = null;
		EdgeRestriction edgeRestr = null;
		String direction = "any";
		int offsetDir = getCurrentOffset();
		int offsetEdgeRestr = 0;
		int lengthEdgeRestr = 0;
		if (tryMatch(TokenTypes.RARROW)) {
			direction = "out";
		} else if (tryMatch(TokenTypes.LARROW)) {
			direction = "in";
		} else {
			match(TokenTypes.ARROW);
		}
		if (tryMatch(TokenTypes.LCURLY)) {
			offsetEdgeRestr = getCurrentOffset();
			edgeRestr = parseEdgeRestriction();
			lengthEdgeRestr = getLength(offsetEdgeRestr);
			match(TokenTypes.RCURLY);
		}
		if (!inPredicateMode()) {
			SimpleEdgePathDescription result = graph
					.createSimpleEdgePathDescription();
			dir = (EdgeDirection) graph.getFirstVertex(EdgeDirection.class);
			while (dir != null) {
				if (!dir.get_dirValue().equals(direction)) {
					dir = dir.getNextEdgeDirection();
				} else {
					break;
				}
			}
			if (dir == null) {
				dir = graph.createEdgeDirection();
				dir.set_dirValue(direction);
			}
			IsEdgeDirectionOf directionOf = graph.createIsEdgeDirectionOf(dir,
					result);
			directionOf.set_sourcePositions(createSourcePositionList(0,
					offsetDir));
			if (edgeRestr != null) {
				IsEdgeRestrOf edgeRestrOf = graph.createIsEdgeRestrOf(
						edgeRestr, result);
				edgeRestrOf.set_sourcePositions(createSourcePositionList(
						lengthEdgeRestr, offsetEdgeRestr));
			}
			return result;
		}
		return null;
	}

	private final PrimaryPathDescription parseAggregationPathDescription() {
		boolean outAggregation = true;
		EdgeRestriction edgeRestr = null;
		int restrOffset = 0;
		int restrLength = 0;
		if (tryMatch(TokenTypes.INAGGREGATION)) {
			outAggregation = false;
		} else {
			match(TokenTypes.OUTAGGREGATION);
		}
		if (tryMatch(TokenTypes.LCURLY)) {
			restrOffset = getCurrentOffset();
			edgeRestr = parseEdgeRestriction();
			restrLength = getLength(restrOffset);
			match(TokenTypes.RCURLY);
		}
		if (!inPredicateMode()) {
			AggregationPathDescription result = graph
					.createAggregationPathDescription();
			result.set_outAggregation(outAggregation);
			if (edgeRestr != null) {
				IsEdgeRestrOf edgeRestrOf = graph.createIsEdgeRestrOf(
						edgeRestr, result);
				edgeRestrOf.set_sourcePositions(createSourcePositionList(
						restrLength, restrOffset));
			}
			return result;
		}
		return null;
	}

	private final EdgePathDescription parseEdgePathDescription() {
		EdgeDirection dir = null;
		boolean edgeStart = false;
		boolean edgeEnd = false;
		String direction = "any";
		int offsetDir = getCurrentOffset();
		if (tryMatch(TokenTypes.SLARROW)) {
			edgeStart = true;
		} else {
			match(TokenTypes.EDGE);
		}
		int offsetExpr = getCurrentOffset();
		Expression expr = parseExpression();
		int lengthExpr = getLength(offsetExpr);

		if (tryMatch(TokenTypes.SRARROW)) {
			edgeEnd = true;
		} else {
			match(TokenTypes.EDGE);
		}

		if (!inPredicateMode()) {
			int lengthDir = getLength(offsetDir);
			EdgePathDescription result = graph.createEdgePathDescription();
			if (edgeStart && !edgeEnd) {
				direction = "in";
			} else if (!edgeStart && edgeEnd) {
				direction = "out";
			}
			dir = (EdgeDirection) graph.getFirstVertex(EdgeDirection.class);
			while (dir != null) {
				if (!dir.get_dirValue().equals(direction)) {
					dir = dir.getNextEdgeDirection();
				} else {
					break;
				}
			}
			if (dir == null) {
				dir = graph.createEdgeDirection();
				dir.set_dirValue(direction);
			}
			IsEdgeDirectionOf directionOf = graph.createIsEdgeDirectionOf(dir,
					result);
			directionOf.set_sourcePositions(createSourcePositionList(lengthDir,
					offsetDir));
			IsEdgeExprOf edgeExprOf = graph.createIsEdgeExprOf(expr, result);
			edgeExprOf.set_sourcePositions(createSourcePositionList(lengthExpr,
					offsetExpr));
			return result;
		}
		return null;
	}

	private final FunctionApplication parseFunctionApplication() {
		List<VertexPosition<TypeId>> typeIds = null;
		if (((lookAhead(0) == TokenTypes.IDENTIFIER)
				|| (lookAhead(0) == TokenTypes.AND)
				|| (lookAhead(0) == TokenTypes.NOT)
				|| (lookAhead(0) == TokenTypes.XOR) || (lookAhead(0) == TokenTypes.OR))
				&& isFunctionName(lookAhead.getValue())
				&& ((lookAhead(1) == TokenTypes.LCURLY) || (lookAhead(1) == TokenTypes.LPAREN))) {
			int offset = getCurrentOffset();
			String name = lookAhead.getValue();
			match();
			int length = getLength(offset);
			if (tryMatch(TokenTypes.LCURLY)) {
				typeIds = parseTypeExpressionList();
				match(TokenTypes.RCURLY);
			}
			match(TokenTypes.LPAREN);
			List<VertexPosition<Expression>> expressions = null;
			if (lookAhead(0) != TokenTypes.RPAREN) {
				expressions = parseExpressionList(TokenTypes.COMMA);
			}
			match(TokenTypes.RPAREN);
			if (!inPredicateMode()) {
				FunctionApplication funApp = graph.createFunctionApplication();
				// retrieve function id or create a new one
				FunctionId functionId = getFunctionId(name);
				IsFunctionIdOf functionIdOf = graph.createIsFunctionIdOf(
						functionId, funApp);
				functionIdOf.set_sourcePositions(createSourcePositionList(
						length, offset));
				if (typeIds != null) {
					for (VertexPosition<TypeId> t : typeIds) {
						IsTypeExprOf typeOf = graph.createIsTypeExprOfFunction(
								t.node, funApp);
						typeOf.set_sourcePositions(createSourcePositionList(
								t.length, t.offset));
					}
				}
				if (expressions != null) {
					for (VertexPosition<Expression> ex : expressions) {
						IsArgumentOf argOf = graph.createIsArgumentOf(ex.node,
								funApp);
						argOf.set_sourcePositions(createSourcePositionList(
								ex.length, ex.offset));
					}
				}
				return funApp;
			}
			return null;
		}
		fail("No function application");
		return null;
	}

	private final Expression parseValueConstruction() {
		if (lookAhead(0) != null) {
			switch (lookAhead(0)) {
			case REC:
				return parseRecordConstruction();
			case MAP:
				return parseMapConstruction();
			case LIST:
				return parseListConstruction();
			case SET:
				match();
				match(TokenTypes.LPAREN);
				if (tryMatch(TokenTypes.RPAREN)) {
					return graph.createSetConstruction();
				}
				List<VertexPosition<Expression>> expressions = parseExpressionList(TokenTypes.COMMA);
				match(TokenTypes.RPAREN);
				if (!inPredicateMode()) {
					return createPartsOfValueConstruction(expressions,
							graph.createSetConstruction());
				} else {
					return null;
				}
			case TUP:
				match();
				match(TokenTypes.LPAREN);
				if (tryMatch(TokenTypes.RPAREN)) {
					return graph.createTupleConstruction();
				}
				expressions = parseExpressionList(TokenTypes.COMMA);
				match(TokenTypes.RPAREN);
				if (!inPredicateMode()) {
					return createPartsOfValueConstruction(expressions,
							graph.createTupleConstruction());
				} else {
					return null;
				}
			}
		}
		fail("Expected value construction, but found");
		return null;
	}

	private final MapConstruction parseMapConstruction() {
		match(TokenTypes.MAP);
		match(TokenTypes.LPAREN);
		if (tryMatch(TokenTypes.RPAREN)) {
			return graph.createMapConstruction();
		}
		MapConstruction mapConstr = null;
		if (!inPredicateMode()) {
			mapConstr = graph.createMapConstruction();
		}
		int offsetKey = getCurrentOffset();
		Expression keyExpr = parseExpression();
		int lengthKey = getLength(offsetKey);
		match(TokenTypes.SRARROW);
		int offsetValue = getCurrentOffset();
		Expression valueExpr = parseExpression();
		int lengthValue = getLength(offsetValue);
		if (!inPredicateMode()) {
			IsKeyExprOfConstruction keyEdge = graph
					.createIsKeyExprOfConstruction(keyExpr, mapConstr);
			keyEdge.set_sourcePositions(createSourcePositionList(lengthKey,
					offsetKey));
			IsValueExprOfConstruction valueEdge = graph
					.createIsValueExprOfConstruction(valueExpr, mapConstr);
			valueEdge.set_sourcePositions(createSourcePositionList(lengthValue,
					offsetValue));
		}
		while (tryMatch(TokenTypes.COMMA)) {
			offsetKey = getCurrentOffset();
			keyExpr = parseExpression();
			lengthKey = getLength(offsetKey);
			match(TokenTypes.SRARROW);
			offsetValue = getCurrentOffset();
			valueExpr = parseExpression();
			lengthValue = getLength(offsetValue);
			if (!inPredicateMode()) {
				IsKeyExprOfConstruction keyEdge = graph
						.createIsKeyExprOfConstruction(keyExpr, mapConstr);
				keyEdge.set_sourcePositions(createSourcePositionList(lengthKey,
						offsetKey));
				IsValueExprOfConstruction valueEdge = graph
						.createIsValueExprOfConstruction(valueExpr, mapConstr);
				valueEdge.set_sourcePositions(createSourcePositionList(
						lengthValue, offsetValue));
			}
		}

		match(TokenTypes.RPAREN);
		return mapConstr;
	}

	private final ValueConstruction parseListConstruction() {
		match(TokenTypes.LIST);
		match(TokenTypes.LPAREN);
		if (tryMatch(TokenTypes.RPAREN)) {
			return graph.createListConstruction();
		}
		ValueConstruction result = null;
		int offsetStart = getCurrentOffset();
		Expression startExpr = parseExpression();
		int lengthStart = getLength(offsetStart);
		if (tryMatch(TokenTypes.DOTDOT)) {
			int offsetEnd = getCurrentOffset();
			Expression endExpr = parseExpression();
			int lengthEnd = getLength(offsetEnd);
			if (!inPredicateMode()) {
				result = graph.createListRangeConstruction();
				IsFirstValueOf firstValueOf = graph.createIsFirstValueOf(
						startExpr, (ListRangeConstruction) result);
				firstValueOf.set_sourcePositions(createSourcePositionList(
						lengthStart, offsetStart));
				IsLastValueOf lastValueOf = graph.createIsLastValueOf(endExpr,
						(ListRangeConstruction) result);
				lastValueOf.set_sourcePositions(createSourcePositionList(
						lengthEnd, offsetEnd));
			}
		} else {
			List<VertexPosition<Expression>> allExpressions = null;
			if (tryMatch(TokenTypes.COMMA)) {
				allExpressions = parseExpressionList(TokenTypes.COMMA);
			}
			if (!inPredicateMode()) {
				VertexPosition<Expression> v = new VertexPosition<Expression>(
						startExpr, lengthStart, offsetStart);
				if (allExpressions == null) {
					allExpressions = new ArrayList<VertexPosition<Expression>>(
							1);
				}
				allExpressions.add(0, v);
				result = createPartsOfValueConstruction(allExpressions,
						graph.createListConstruction());
			}
		}
		match(TokenTypes.RPAREN);
		return result;
	}

	private final ValueConstruction parseRecordConstruction() {
		match(TokenTypes.REC);
		match(TokenTypes.LPAREN);
		List<VertexPosition<RecordElement>> elements = new ArrayList<VertexPosition<RecordElement>>();
		do {
			int offset = getCurrentOffset();
			RecordElement recElem = parseRecordElement();
			int length = getLength(offset);
			elements.add(new VertexPosition<RecordElement>(recElem, length,
					offset));
		} while (tryMatch(TokenTypes.COMMA));
		match(TokenTypes.RPAREN);
		if (!inPredicateMode()) {
			RecordConstruction valueConstr = graph.createRecordConstruction();
			if (elements != null) {
				for (VertexPosition<RecordElement> expr : elements) {
					IsRecordElementOf exprOf = graph.createIsRecordElementOf(
							expr.node, valueConstr);
					exprOf.set_sourcePositions(createSourcePositionList(
							expr.length, expr.offset));
				}
			}
			return valueConstr;
		}
		return null;

	}

	private final RecordElement parseRecordElement() {
		int offsetRecId = getCurrentOffset();
		String recIdName = matchIdentifier();
		int lengthRecId = getLength(offsetRecId);
		match(TokenTypes.COLON);
		int offsetExpr = getCurrentOffset();
		Expression expr = parseExpression();
		int lengthExpr = getLength(offsetExpr);
		if (!inPredicateMode()) {
			RecordId recId = graph.createRecordId();
			recId.set_name(recIdName);
			RecordElement recElement = graph.createRecordElement();
			IsRecordIdOf recIdOf = graph.createIsRecordIdOf(recId, recElement);
			recIdOf.set_sourcePositions(createSourcePositionList(lengthRecId,
					offsetRecId));
			IsRecordExprOf exprOf = graph
					.createIsRecordExprOf(expr, recElement);
			exprOf.set_sourcePositions(createSourcePositionList(lengthExpr,
					offsetExpr));
			return recElement;
		}
		return null;
	}

	private final Declaration parseQuantifiedDeclaration() {
		List<VertexPosition<SimpleDeclaration>> declarations = parseDeclarationList();
		Declaration declaration = null;
		if (!inPredicateMode()) {
			declaration = (Declaration) createMultipleEdgesToParent(
					declarations, graph.createDeclaration(),
					IsSimpleDeclOf.class, false);
		}
		while (tryMatch(TokenTypes.COMMA)) {
			int offsetConstraint = getCurrentOffset();
			Expression constraintExpr = parseExpression();
			int lengthConstraint = getLength(offsetConstraint);
			if (!inPredicateMode()) {
				IsConstraintOf constraintOf = graph.createIsConstraintOf(
						constraintExpr, declaration);
				constraintOf.set_sourcePositions(createSourcePositionList(
						lengthConstraint, offsetConstraint));
			}
			predicateStart();
			try {
				match(TokenTypes.COMMA);
				parseSimpleDeclaration();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				match(TokenTypes.COMMA);
				declarations = parseDeclarationList();
				if (!inPredicateMode()) {
					createMultipleEdgesToParent(declarations, declaration,
							IsSimpleDeclOf.class, false);
				}
			}
		}
		return declaration;
	}

	private final List<VertexPosition<SimpleDeclaration>> parseDeclarationList() {
		List<VertexPosition<SimpleDeclaration>> declList = new ArrayList<VertexPosition<SimpleDeclaration>>();
		int offset = getCurrentOffset();
		SimpleDeclaration decl = parseSimpleDeclaration();
		int length = getLength(offset);
		declList.add(new VertexPosition<SimpleDeclaration>(decl, length, offset));
		if (lookAhead(0) == TokenTypes.COMMA) {
			predicateStart();
			try {
				match(TokenTypes.COMMA);
				parseSimpleDeclaration();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				match(TokenTypes.COMMA);
				declList.addAll(parseDeclarationList());
			}
		}
		return declList;
	}

	private final SimpleDeclaration parseSimpleDeclaration() {
		List<VertexPosition<Variable>> variables = parseVariableList();
		match(TokenTypes.COLON);
		int offset = getCurrentOffset();
		Expression expr = parseExpression();
		int length = getLength(offset);
		if (!inPredicateMode()) {
			SimpleDeclaration simpleDecl = (SimpleDeclaration) createMultipleEdgesToParent(
					variables, graph.createSimpleDeclaration(),
					IsDeclaredVarOf.class, "");
			IsTypeExprOf typeExprOf = graph.createIsTypeExprOfDeclaration(expr,
					simpleDecl);
			typeExprOf.set_sourcePositions(createSourcePositionList(length,
					offset));
			return simpleDecl;
		}
		return null;
	}

	private final List<VertexPosition<Expression>> parseExpressionList(
			TokenTypes separator) {
		int pos = alreadySucceeded(RuleEnum.EXPRESSION_LIST);
		if (skipRule(pos)) {
			return null;
		}
		List<VertexPosition<Expression>> list = new ArrayList<VertexPosition<Expression>>();
		do {
			int offset = getCurrentOffset();
			Expression expr = parseExpression();
			int length = getLength(offset);
			list.add(new VertexPosition<Expression>(expr, length, offset));
		} while (tryMatch(separator));
		ruleSucceeds(RuleEnum.EXPRESSION_LIST, pos);
		return list;
	}

	private final Expression parseRangeExpression() {
		Expression expr = null;
		if (tryMatch(TokenTypes.V)) {
			if (!inPredicateMode()) {
				expr = graph.createVertexSetExpression();
			}
		} else {
			match(TokenTypes.E);
			if (!inPredicateMode()) {
				expr = graph.createEdgeSetExpression();
			}
		}
		if (tryMatch(TokenTypes.LCURLY)) {
			if (!tryMatch(TokenTypes.RCURLY)) {
				List<VertexPosition<TypeId>> typeIds = parseTypeExpressionList();
				match(TokenTypes.RCURLY);
				if (!inPredicateMode()) {
					createMultipleEdgesToParent(typeIds, expr,
							IsTypeRestrOfExpression.class, 0);
				}
			}
		}
		return expr;
	}

	private final List<VertexPosition<TypeId>> parseTypeExpressionList() {
		List<VertexPosition<TypeId>> list = new ArrayList<VertexPosition<TypeId>>();

		do {
			int offset = getCurrentOffset();
			TypeId t = parseTypeId();
			int length = getLength(offset);
			list.add(new VertexPosition<TypeId>(t, length, offset));
			if (lookAhead(0) == TokenTypes.COLON) {
				fail("COLON found in TypeId");
			}
		} while (tryMatch(TokenTypes.COMMA));
		return list;
	}

	private final TypeId parseTypeId() {
		TypeId type = null;
		if (!inPredicateMode()) {
			type = graph.createTypeId();
		}
		if (tryMatch(TokenTypes.CARET)) {
			if (!inPredicateMode()) {
				type.set_excluded(true);
			}
		}
		String s = matchQualifiedName();
		if (!inPredicateMode()) {
			type.set_name(s);
		}
		if (tryMatch(TokenTypes.EXCL)) {
			if (!inPredicateMode()) {
				type.set_type(true);
			}
		}
		return type;
	}

	private TypeOrRoleId parseTypeOrRoleId() {
		TypeOrRoleId id = null;
		predicateStart();
		try {
			parseTypeId();
		} catch (ParsingException ex) {
			// no type id but a role id
		}
		if (predicateEnd()) {
			id = parseTypeId();
		} else {
			id = parseRoleId();
		}
		return id;
	}

	private final List<VertexPosition<? extends TypeOrRoleId>> parseTypeAndRoleExpressionList() {
		List<VertexPosition<? extends TypeOrRoleId>> list = new ArrayList<VertexPosition<? extends TypeOrRoleId>>();
		do {
			int offset = getCurrentOffset();
			TypeOrRoleId id = parseTypeOrRoleId();
			int length = getLength(offset);
			list.add(new VertexPosition<TypeOrRoleId>(id, length, offset));
		} while (tryMatch(TokenTypes.COMMA));
		return list;
	}

	@SuppressWarnings("unchecked")
	private final EdgeRestriction parseEdgeRestriction() {
		List<VertexPosition<TypeId>> typeIds = null;
		List<VertexPosition<RoleId>> roleIds = null;
		Expression predicate = null;
		int predicateOffset = 0;
		int predicateLength = 0;

		predicateStart();
		try {
			parseTypeOrRoleId();
		} catch (ParsingException ex) {
			// failed predicate
		}
		if (predicateEnd()) {
			List<VertexPosition<? extends TypeOrRoleId>> typeOrRoleIds = parseTypeAndRoleExpressionList();
			if (typeOrRoleIds != null) {
				typeIds = new ArrayList<VertexPosition<TypeId>>();
				roleIds = new ArrayList<VertexPosition<RoleId>>();
				for (VertexPosition<? extends TypeOrRoleId> id : typeOrRoleIds) {
					if (id.node instanceof TypeId) {
						typeIds.add((VertexPosition<TypeId>) id);
					} else {
						roleIds.add((VertexPosition<RoleId>) id);
					}
				}
			}
		}
		if (tryMatch(TokenTypes.AT)) {
			predicateOffset = getCurrentOffset();
			predicate = parseExpression();
			predicateLength = getLength(predicateOffset);
		}
		EdgeRestriction er = null;
		if (!inPredicateMode()) {
			er = graph.createEdgeRestriction();
			if (typeIds != null) {
				for (VertexPosition<TypeId> type : typeIds) {
					IsTypeIdOf typeIdOf = graph.createIsTypeIdOf(type.node, er);
					typeIdOf.set_sourcePositions(createSourcePositionList(
							type.length, type.offset));
				}
			}
			if (roleIds != null) {
				for (VertexPosition<RoleId> role : roleIds) {
					IsRoleIdOf roleIdOf = graph.createIsRoleIdOf(role.node, er);
					roleIdOf.set_sourcePositions(createSourcePositionList(
							role.length, role.offset));
				}
			}
			if (predicate != null) {
				IsBooleanPredicateOfEdgeRestriction edge = graph
						.createIsBooleanPredicateOfEdgeRestriction(predicate,
								er);
				edge.set_sourcePositions(createSourcePositionList(
						predicateLength, predicateOffset));
			}
		}
		return er;
	}

	private final Comprehension parseLabeledReportList() {
		TupleConstruction tupConstr = null;
		boolean hasLabel = false;
		int offsetExpr = 0;
		int offset = 0;
		int offsetAsExpr = 0;
		int lengthAsExpr = 0;
		ListComprehension listCompr = null;
		Expression expr = null;
		int lengthExpr = 0;
		Expression asExpr = null;
		match(TokenTypes.REPORT);
		do {
			hasLabel = false;
			offsetExpr = getCurrentOffset();
			offset = offsetExpr;
			expr = parseExpression();
			lengthExpr = getLength(offsetExpr);
			if (tryMatch(TokenTypes.AS)) {
				offsetAsExpr = getCurrentOffset();
				asExpr = parseExpression();
				lengthAsExpr = getLength(offsetAsExpr);
				hasLabel = true;
			}
			if (!inPredicateMode()) {
				if (listCompr == null) {
					listCompr = graph.createListComprehension();
					tupConstr = graph.createTupleConstruction();
					IsCompResultDefOf e = graph.createIsCompResultDefOf(
							tupConstr, listCompr);
					e.set_sourcePositions(createSourcePositionList(
							getLength(offset), offset));
				}
				IsPartOf partOf = graph.createIsPartOf(expr, tupConstr);
				partOf.set_sourcePositions(createSourcePositionList(lengthExpr,
						offsetExpr));
				if (hasLabel) {
					IsTableHeaderOf tableHeaderOf = graph
							.createIsTableHeaderOf(asExpr, listCompr);
					tableHeaderOf.set_sourcePositions(createSourcePositionList(
							lengthAsExpr, offsetAsExpr));
				}
			}
		} while (tryMatch(TokenTypes.COMMA));
		if (!inPredicateMode()
				&& (tupConstr.getDegree(Direction.EDGE_TO_VERTEX) == 1)) {
			Vertex v = ((BinaryEdge) tupConstr.getFirstIncidence(
					Direction.EDGE_TO_VERTEX).getEdge()).getAlpha();
			Edge e2 = tupConstr.getFirstIncidence(Direction.VERTEX_TO_EDGE)
					.getEdge();
			((BinaryEdge) e2).setAlpha(v);
			tupConstr.delete();
		}
		return listCompr;
	}

	private final Comprehension parseReportClause() {
		Comprehension comprehension = null;
		boolean vartable = false;
		boolean map = false;
		TokenTypes separator = TokenTypes.COMMA;
		switch (lookAhead(0)) {
		case REPORT:
			return parseLabeledReportList();
		case REPORTLIST:
			if (!inPredicateMode()) {
				comprehension = graph.createListComprehension();
			}
			match();
			break;
		case REPORTSET:
			if (!inPredicateMode()) {
				comprehension = graph.createSetComprehension();
			}
			match();
			break;
		case REPORTTABLE:
			if (!inPredicateMode()) {
				comprehension = graph.createTableComprehension();
			}
			vartable = true;
			match();
			break;
		case REPORTMAP:
			if (!inPredicateMode()) {
				comprehension = graph.createMapComprehension();
			}
			map = true;
			separator = TokenTypes.SRARROW;
			match();
			break;
		default:
			fail("Unrecognized token");
		}
		int offset = getCurrentOffset();
		List<VertexPosition<Expression>> reportList = parseExpressionList(separator);
		int length = getLength(offset);
		IsCompResultDefOf e = null;
		if (map) {
			if (!inPredicateMode()) {
				if (reportList.size() != 2) {
					fail("reportMap keyExpr -> valueExpr must be followed by exactly two arguments");
				}

				IsKeyExprOfComprehension keyEdge = graph
						.createIsKeyExprOfComprehension(reportList.get(0).node,
								(MapComprehension) comprehension);
				IsValueExprOfComprehension valueEdge = graph
						.createIsValueExprOfComprehension(
								reportList.get(1).node,
								(MapComprehension) comprehension);
				keyEdge.set_sourcePositions(createSourcePositionList(
						reportList.get(0).length, reportList.get(0).offset));
				valueEdge.set_sourcePositions(createSourcePositionList(
						reportList.get(1).length, reportList.get(1).offset));
			}
		} else if (vartable) {
			if (!inPredicateMode()) {
				if ((reportList.size() != 3) && (reportList.size() != 4)) {
					fail("reportTable columHeaderExpr, rowHeaderExpr, cellContent [,tableHeader] must be followed by three or for arguments");
				}
				IsColumnHeaderExprOf cHeaderE = graph
						.createIsColumnHeaderExprOf(reportList.get(0).node,
								(TableComprehension) comprehension);
				cHeaderE.set_sourcePositions(createSourcePositionList(
						reportList.get(0).length, reportList.get(0).offset));
				IsRowHeaderExprOf rHeaderE = graph.createIsRowHeaderExprOf(
						reportList.get(1).node,
						(TableComprehension) comprehension);
				rHeaderE.set_sourcePositions(createSourcePositionList(
						reportList.get(1).length, reportList.get(1).offset));
				e = graph.createIsCompResultDefOf(reportList.get(2).node,
						comprehension);
				e.set_sourcePositions(createSourcePositionList(
						reportList.get(2).length, reportList.get(2).offset));
				if (reportList.size() == 4) {
					IsTableHeaderOf tHeaderE = graph.createIsTableHeaderOf(
							reportList.get(3).node,
							(ComprehensionWithTableHeader) comprehension);
					tHeaderE.set_sourcePositions(createSourcePositionList(
							reportList.get(3).length, reportList.get(3).offset));
				}
			}
		} else {
			if (!inPredicateMode()) {
				if (reportList.size() > 1) {
					TupleConstruction tupConstr = (TupleConstruction) createMultipleEdgesToParent(
							reportList, graph.createTupleConstruction(),
							IsPartOf.class);
					e = graph.createIsCompResultDefOf(tupConstr, comprehension);
				} else {
					e = graph.createIsCompResultDefOf(reportList.get(0).node,
							comprehension);
				}
				e.set_sourcePositions(createSourcePositionList(length, offset));
			}
		}
		return comprehension;
	}

	private final Comprehension parseFWRExpression() {
		match(TokenTypes.FROM);
		int offsetDecl = getCurrentOffset();
		List<VertexPosition<SimpleDeclaration>> declarations = parseDeclarationList();
		int lengthDecl = getLength(offsetDecl);
		duringParsingvariableSymbolTable.blockBegin();
		Declaration declaration = null;
		if (!inPredicateMode()) {
			declaration = graph.createDeclaration();
			createMultipleEdgesToParent(declarations, declaration,
					IsSimpleDeclOf.class, false);
		}
		if (tryMatch(TokenTypes.WITH)) {
			int offsetConstraint = getCurrentOffset();
			Expression constraintExpr = parseExpression();
			int lengthConstraint = getLength(offsetConstraint);
			lengthDecl += lengthConstraint;
			if (!inPredicateMode()) {
				IsConstraintOf constraintOf = graph.createIsConstraintOf(
						constraintExpr, declaration);
				constraintOf.set_sourcePositions(createSourcePositionList(
						lengthConstraint, offsetConstraint));
			}
		}
		Comprehension comprehension = parseReportClause();
		if (!inPredicateMode()) {
			IsCompDeclOf comprDeclOf = graph.createIsCompDeclOf(declaration,
					comprehension);
			comprDeclOf.set_sourcePositions(createSourcePositionList(
					lengthDecl, offsetDecl));
		}
		match(TokenTypes.END);
		duringParsingvariableSymbolTable.blockEnd();
		return comprehension;
	}

	private final Expression parsePathExpression() {
		int pos = alreadySucceeded(RuleEnum.PATH_EXPRESSION);
		if (skipRule(pos)) {
			return null;
		}
		Expression expr = null;
		/*
		 * AlternativePathDescrition as path of backwardVertexSet or
		 * backwardPathSystem
		 */
		/* (alternativePathDescription (SMILEY | restrictedExpression)) => */
		predicateStart();
		try {
			parseAltPathDescription();
			if (!tryMatch(TokenTypes.SMILEY)) {
				parseValueAccess(); // parseRestrictedExpression();
			}
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			expr = parseRegBackwardElementSetOrPathSystem();
		} else {
			predicateStart();
			try {
				parseValueAccess(); // parseRestrictedExpression();
			} catch (ParsingException ex) {
			}
			if (predicateEnd()) {
				int offsetArg1 = getCurrentOffset();
				expr = parseValueAccess();// parseRestrictedExpression();
				int lengthArg1 = getLength(offsetArg1);
				if (lookAhead(0) == TokenTypes.SMILEY) {
					expr = parseRegPathOrPathSystem(expr, offsetArg1,
							lengthArg1);
				} else {
					predicateStart();
					try {
						parseAltPathDescription();
					} catch (ParsingException ex) {
					}
					if (predicateEnd()) {
						expr = parseRegPathExistenceOrForwardElementSet(expr,
								offsetArg1, lengthArg1);
					}
				}
			} else {
				expr = parseAltPathDescription();
			}
		}
		ruleSucceeds(RuleEnum.PATH_EXPRESSION, pos);
		return expr;
	}

	private final Expression parseRegPathExistenceOrForwardElementSet(
			Expression expr, int offsetArg1, int lengthArg1) {
		int offsetExpr = getCurrentOffset();
		int offsetPathDescr = getCurrentOffset();
		PathDescription pathDescr = parseAltPathDescription();
		int lengthPathDescr = getLength(offsetPathDescr);
		Expression restrExpr = null;
		predicateStart();
		try {
			parsePrimaryExpression();
		} catch (ParsingException ex) {
		}
		if (predicateEnd()) {
			restrExpr = parseValueAccess(); // parseRestrictedExpression();
			if (!inPredicateMode()) {
				int lengthExpr = getLength(offsetExpr);
				PathExistence pe = graph.createPathExistence();
				// add start vertex
				IsStartExprOf startElementOf = graph.createIsStartExprOf(expr,
						pe);
				startElementOf.set_sourcePositions(createSourcePositionList(
						lengthArg1, offsetArg1));
				// add target vertex
				IsTargetExprOf targetVertexOf = graph.createIsTargetExprOf(
						restrExpr, pe);
				targetVertexOf.set_sourcePositions(createSourcePositionList(
						lengthExpr, offsetExpr));
				// add pathdescription
				IsPathOf pathOf = graph.createIsPathOf(pathDescr, pe);
				pathOf.set_sourcePositions(createSourcePositionList(
						lengthPathDescr, offsetPathDescr));
				return pe;
			}
			return null;
		} else {
			if (!inPredicateMode()) {
				// create new forward-vertex-set
				ForwardElementSet fvs = graph.createForwardElementSet();
				// add start expr
				IsStartExprOf startVertexOf = graph.createIsStartExprOf(expr,
						fvs);
				startVertexOf.set_sourcePositions(createSourcePositionList(
						lengthArg1, offsetArg1));
				// add pathdescr
				IsPathOf pathOf = graph.createIsPathOf(pathDescr, fvs);
				pathOf.set_sourcePositions(createSourcePositionList(
						lengthPathDescr, offsetPathDescr));
				return fvs;
			}
			return null;
		}
	}

	private final Expression parseRegPathOrPathSystem(Expression arg1,
			int offsetArg1, int lengthArg1) {
		boolean isPath = false;
		int offsetOperator1 = getCurrentOffset();
		int offsetExpr = offsetArg1;
		int lengthExpr = 0;
		Expression restrExpr = null;
		match(TokenTypes.SMILEY);
		int offsetPathDescr = getCurrentOffset();
		PathDescription pathDescr = parseAltPathDescription();
		int lengthPathDescr = getLength(offsetPathDescr);
		int offsetOperator2 = getCurrentOffset();
		if (tryMatch(TokenTypes.SMILEY)) {
			offsetExpr = getCurrentOffset();
			restrExpr = parseValueAccess(); // parseRestrictedExpression();
			lengthExpr = getLength(offsetExpr);
		}
		if (!inPredicateMode()) {
			FunctionId funId = getFunctionId("pathSystem");
			Expression result = createFunctionIdAndArgumentOf(funId,
					offsetOperator1, 3, arg1, offsetArg1, lengthArg1,
					pathDescr, offsetPathDescr, lengthPathDescr, true);
			if (isPath) {
				result = createFunctionIdAndArgumentOf(funId, offsetOperator1,
						3, result, offsetArg1, -offsetArg1 + offsetOperator2
								+ 3, restrExpr, offsetExpr, lengthExpr, true);
			}
			return result;
		}
		return null;
	}

	private final Expression parseRegBackwardElementSetOrPathSystem() {
		boolean isPathSystem = false;
		int offsetPathDescr = getCurrentOffset();
		PathDescription pathDescr = parseAltPathDescription();
		int lengthPathDescr = getLength(offsetPathDescr);
		int offsetOperator = getCurrentOffset();
		if (tryMatch(TokenTypes.SMILEY)) {
			isPathSystem = true;
		}
		int offsetExpr = getCurrentOffset();
		Expression restrExpr = parseValueAccess();// parseRestrictedExpression();
		int lengthExpr = getLength(offsetExpr);
		if (!inPredicateMode()) {
			if (isPathSystem) {
				// create a path-system-functionapplication
				FunctionId f = getFunctionId("pathSystem");
				return createFunctionIdAndArgumentOf(f, offsetOperator, 3,
						pathDescr, offsetPathDescr, lengthPathDescr, restrExpr,
						offsetExpr, lengthExpr, true);
			} else {
				BackwardElementSet bs = graph.createBackwardElementSet();
				IsTargetExprOf targetVertexOf = graph.createIsTargetExprOf(
						restrExpr, bs);
				targetVertexOf.set_sourcePositions(createSourcePositionList(
						lengthExpr, offsetExpr));
				IsPathOf pathOf = graph.createIsPathOf(pathDescr, bs);
				pathOf.set_sourcePositions(createSourcePositionList(
						lengthPathDescr, offsetPathDescr));
				return bs;
			}
		}
		return null;
	}

	private final Expression parseNumericLiteral() {
		if (lookAhead(0) == TokenTypes.DOUBLELITERAL) {
			DoubleLiteral literal = null;
			if (!inPredicateMode()) {
				literal = graph.createDoubleLiteral();
				literal.set_doubleValue(((DoubleToken) lookAhead).getNumber());
			}
			match();
			return literal;
		}
		if ((lookAhead(0) == TokenTypes.HEXLITERAL)
				|| (lookAhead(0) == TokenTypes.OCTLITERAL)) {
			if (((IntegerToken) lookAhead).getNumber().intValue() == ((IntegerToken) lookAhead)
					.getNumber().longValue()) {
				IntLiteral literal = null;
				if (!inPredicateMode()) {
					literal = graph.createIntLiteral();
					literal.set_intValue(((IntegerToken) lookAhead).getNumber()
							.intValue());
				}
				match();
				return literal;
			} else {
				LongLiteral literal = null;
				if (!inPredicateMode()) {
					literal = graph.createLongLiteral();
					literal.set_longValue(((IntegerToken) lookAhead)
							.getNumber());
				}
				match();
				return literal;
			}
		}
		if ((lookAhead(0) == TokenTypes.INTLITERAL)) {
			long value = ((IntegerToken) lookAhead).getNumber().longValue();
			String integerPart = lookAhead.getValue();
			match();
			if (lookAhead(0) == TokenTypes.DOT) {
				String decimalPart = "0";
				match();
				if ((lookAhead(0) == TokenTypes.INTLITERAL)
						|| (lookAhead(0) == TokenTypes.OCTLITERAL)) {
					decimalPart = ((IntegerToken) lookAhead).getValue();
					match();
					// } else if (lookAhead(0) == TokenTypes.REALLITERAL) {
					// decimalPart = lookAhead.getValue().substring(0,
					// lookAhead.getValue().length() - 1);
					// match();
				} else {
					fail("Unrecognized token as part of decimal value");
				}
				if (!inPredicateMode()) {
					String doubleValue = integerPart + "." + decimalPart;
					DoubleLiteral literal = graph.createDoubleLiteral();
					// System.out.println("Real Value: '" + realValue + "'");
					literal.set_doubleValue(Double.parseDouble(doubleValue));
					return literal;
				}
				return null;
			} else {
				if (!inPredicateMode()) {
					if ((value < Integer.MAX_VALUE)
							&& (value > Integer.MIN_VALUE)) {
						IntLiteral literal = graph.createIntLiteral();
						literal.set_intValue((int) value);
						return literal;
					} else {
						LongLiteral literal = graph.createLongLiteral();
						literal.set_longValue(value);
						return literal;
					}
				}
				return null;
			}
		}
		fail("No numeric literal");
		return null;
	}

	private final Expression parseLiteral() {
		if (lookAhead(0) != null) {
			switch (lookAhead(0)) {
			case UNDEFINED: {
				UndefinedLiteral ul = null;
				if (!inPredicateMode()) {
					ul = graph.getFirstUndefinedLiteral();
					if (ul == null) {
						ul = graph.createUndefinedLiteral();
					}
				}
				match();
				return ul;
			}
			case DOUBLELITERAL:
			case HEXLITERAL:
			case INTLITERAL:
			case OCTLITERAL:
				return parseNumericLiteral();
			case STRING: {
				StringLiteral sl = null;
				if (!inPredicateMode()) {
					sl = graph.createStringLiteral();
					sl.set_stringValue(lookAhead.getValue());
				}
				match();
				return sl;
			}
			case THISEDGE: {
				match();
				ThisEdge te = null;
				if (!inPredicateMode()) {
					te = graph.getFirstThisEdge();
					if (te == null) {
						te = graph.createThisEdge();
					}
				}
				return te;
			}
			case THISVERTEX: {
				match();
				ThisVertex tv = null;
				if (!inPredicateMode()) {
					tv = graph.getFirstThisVertex();
					if (tv == null) {
						tv = graph.createThisVertex();
					}
				}
				return tv;
			}
			case TRUE: {
				match();
				BoolLiteral tl = null;
				if (!inPredicateMode()) {
					tl = graph.getFirstBoolLiteral();
					while (tl != null) {
						if (tl.is_boolValue() == true) {
							break;
						}
						tl = tl.getNextBoolLiteral();
					}
					if (tl == null) {
						tl = graph.createBoolLiteral();
						tl.set_boolValue(true);
					}
				}
				return tl;
			}
			case FALSE: {
				match();
				BoolLiteral fl = null;
				if (!inPredicateMode()) {
					fl = graph.getFirstBoolLiteral();
					while (fl != null) {
						if (fl.is_boolValue() == false) {
							break;
						}
						fl = fl.getNextBoolLiteral();
					}
					if (fl == null) {
						fl = graph.createBoolLiteral();
						fl.set_boolValue(false);
					}
				}
				return fl;
			}
			}
		}
		fail("Unrecognized literal");
		return null;
	}

	public Greql2Schema getSchema() {
		return schema;
	}

}
