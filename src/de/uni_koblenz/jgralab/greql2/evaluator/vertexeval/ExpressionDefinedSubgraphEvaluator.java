package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.ExpressionDefinedSubgraph;
import de.uni_koblenz.jgralab.greql2.schema.IsSubgraphDefiningExpression;
import de.uni_koblenz.jgralab.greql2.schema.IsSubgraphDefiningExpression_isSubgraphDefiningExpression_omega;

public class ExpressionDefinedSubgraphEvaluator extends SubgraphDefinitionEvaluator {

	VertexEvaluator subgraphDefExprEvaluator = null;
	
	//ExpressionDefinedSubgraph vertex;
	
	public ExpressionDefinedSubgraphEvaluator(ExpressionDefinedSubgraph vertex, GreqlEvaluator eval) {
		super(vertex, eval);
	//	this.vertex = vertex;
	}
	

	@Override
	public Object evaluate() {
		if (subgraphDefExprEvaluator == null) {	
			ExpressionDefinedSubgraph exprDefinedSubgraph = (ExpressionDefinedSubgraph) vertex;
			IsSubgraphDefiningExpression_isSubgraphDefiningExpression_omega isSubgraphDefiningExpression 
				= exprDefinedSubgraph.getFirst_isSubgraphDefiningExpression_omega();
			Expression subgraphDefExpr = (Expression) isSubgraphDefiningExpression.getThat();
			subgraphDefExprEvaluator = vertexEvalMarker.getMark(subgraphDefExpr);
		}	
		return subgraphDefExprEvaluator.getResult();
	}

	@Override
	protected VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		ExpressionDefinedSubgraph exprDefinedSubgraph = (ExpressionDefinedSubgraph) vertex;
		IsSubgraphDefiningExpression_isSubgraphDefiningExpression_omega isSubgraphDefiningExpression 
		= exprDefinedSubgraph.getFirst_isSubgraphDefiningExpression_omega();
		Expression subgraphDefExpr = (Expression) isSubgraphDefiningExpression.getThat();
		subgraphDefExprEvaluator = vertexEvalMarker.getMark(subgraphDefExpr);
		return subgraphDefExprEvaluator.calculateSubtreeEvaluationCosts(graphSize);
	}



}
