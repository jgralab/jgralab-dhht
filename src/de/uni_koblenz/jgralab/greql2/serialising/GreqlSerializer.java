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
/**
 * 
 */
package de.uni_koblenz.jgralab.greql2.serialising;

import java.util.Iterator;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.funlib.graph.EdgeTypeSubgraph;
import de.uni_koblenz.jgralab.greql2.funlib.graph.VertexTypeSubgraph;
import de.uni_koblenz.jgralab.greql2.schema.AggregationPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.AlternativePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.BackwardElementSet;
import de.uni_koblenz.jgralab.greql2.schema.BoolLiteral;
import de.uni_koblenz.jgralab.greql2.schema.Comprehension;
import de.uni_koblenz.jgralab.greql2.schema.ConditionalExpression;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Definition;
import de.uni_koblenz.jgralab.greql2.schema.DefinitionExpression;
import de.uni_koblenz.jgralab.greql2.schema.DoubleLiteral;
import de.uni_koblenz.jgralab.greql2.schema.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.schema.EdgePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.EdgeSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.ElementSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.ExponentiatedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.ExpressionDefinedSubgraph;
import de.uni_koblenz.jgralab.greql2.schema.ForwardElementSet;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.FunctionId;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.Identifier;
import de.uni_koblenz.jgralab.greql2.schema.IntLiteral;
import de.uni_koblenz.jgralab.greql2.schema.IntermediateVertexPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf_isDeclaredVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsSubgraphDefinitionOf_isSubgraphDefinitionOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IteratedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.IterationType;
import de.uni_koblenz.jgralab.greql2.schema.LetExpression;
import de.uni_koblenz.jgralab.greql2.schema.ListComprehension;
import de.uni_koblenz.jgralab.greql2.schema.ListConstruction;
import de.uni_koblenz.jgralab.greql2.schema.ListRangeConstruction;
import de.uni_koblenz.jgralab.greql2.schema.Literal;
import de.uni_koblenz.jgralab.greql2.schema.MapComprehension;
import de.uni_koblenz.jgralab.greql2.schema.MapConstruction;
import de.uni_koblenz.jgralab.greql2.schema.OptionalPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathDescription;
import de.uni_koblenz.jgralab.greql2.schema.PathExistence;
import de.uni_koblenz.jgralab.greql2.schema.PathExpression;
import de.uni_koblenz.jgralab.greql2.schema.PrimaryPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.QuantifiedExpression;
import de.uni_koblenz.jgralab.greql2.schema.Quantifier;
import de.uni_koblenz.jgralab.greql2.schema.RecordConstruction;
import de.uni_koblenz.jgralab.greql2.schema.RecordElement;
import de.uni_koblenz.jgralab.greql2.schema.SequentialPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.SetComprehension;
import de.uni_koblenz.jgralab.greql2.schema.SetConstruction;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.SimplePathDescription;
import de.uni_koblenz.jgralab.greql2.schema.StringLiteral;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphDefinition;
import de.uni_koblenz.jgralab.greql2.schema.SubgraphExpression;
import de.uni_koblenz.jgralab.greql2.schema.TableComprehension;
import de.uni_koblenz.jgralab.greql2.schema.ThisEdge;
import de.uni_koblenz.jgralab.greql2.schema.ThisVertex;
import de.uni_koblenz.jgralab.greql2.schema.TransposedPathDescription;
import de.uni_koblenz.jgralab.greql2.schema.TupleConstruction;
import de.uni_koblenz.jgralab.greql2.schema.UndefinedLiteral;
import de.uni_koblenz.jgralab.greql2.schema.ValueConstruction;
import de.uni_koblenz.jgralab.greql2.schema.Variable;
import de.uni_koblenz.jgralab.greql2.schema.VertexSetExpression;
import de.uni_koblenz.jgralab.greql2.schema.WhereExpression;

/**
 * @author Tassilo Horn &lt;horn@uni-koblenz.de&gt;
 * 
 */
public class GreqlSerializer {

	private StringBuffer sb = null;

	public static String serializeGraph(GreqlSyntaxGraph greqlGraph) {
		GreqlSerializer s = new GreqlSerializer();
		return s.serializeGreqlVertex(greqlGraph.getFirstGreql2Expression());
	}

	public static String serializeVertex(Greql2Vertex v) {
		GreqlSerializer s = new GreqlSerializer();
		return s.serializeGreqlVertex(v);
	}

	public String serializeGreqlVertex(Greql2Vertex v) {
		sb = new StringBuffer();
		serializeGreql2Vertex(v, false);
		return sb.toString();
	}

	private void serializeGreql2Vertex(Greql2Vertex v, boolean addSpace) {
		if (v instanceof Declaration) {
			Declaration d = (Declaration) v;
			if (d.getFirstIncidenceToIsQuantifiedDeclOf(Direction.BOTH) != null) {
				serializeDeclaration((Declaration) v, false);
			} else {
				serializeDeclaration((Declaration) v, true);
			}
		} else if (v instanceof Definition) {
			serializeDefinition((Definition) v);
		} else if (v instanceof EdgeDirection) {
			serializeDirection((EdgeDirection) v);
		} else if (v instanceof EdgeRestriction) {
			serializeEdgeRestriction((EdgeRestriction) v);
		} else if (v instanceof Greql2Expression) {
			serializeGreql2Expression((Greql2Expression) v);
		} else if (v instanceof Quantifier) {
			serializeQuantifier((Quantifier) v);
		} else if (v instanceof RecordElement) {
			serializeRecordElement((RecordElement) v);
		} else if (v instanceof SimpleDeclaration) {
			serializeSimpleDeclaration((SimpleDeclaration) v);
		} else if (v instanceof Expression) {
			serializeExpression((Expression) v, false);
		} else {
			throw new GreqlException("Unknown Greql2Vertex " + v + ".");
		}
		if (addSpace) {
			sb.append(' ');
		}
	}

	private void serializeSimpleDeclaration(SimpleDeclaration v) {
		boolean first = true;
		for (IsDeclaredVarOf_isDeclaredVarOf_omega varInc : v.getIsDeclaredVarOf_isDeclaredVarOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeVariable((Variable)varInc.getThat());
		}
		sb.append(": ");
		serializeExpression((Expression) v.getFirst_isTypeExprOf_omega().getThat(), false);
	}

	private void serializeRecordElement(RecordElement v) {
		serializeIdentifier((Identifier) v.getFirst_isRecordIfOf_omega().getThat());
		sb.append(" : ");
		serializeExpression((Expression) v.getFirst_isRecordExprOf_omega().getThat(), false);
	}

	private void serializeQuantifier(Quantifier v) {
		switch (v.get_type()) {
		case EXISTS:
			sb.append("exists");
			break;
		case EXISTSONE:
			sb.append("exists!");
			break;
		case FORALL:
			sb.append("forall");
			break;
		default:
			throw new RuntimeException("FIXME: Unhandled QuantificationType: "
					+ v.get_type());
		}
	}

	private void serializeEdgeRestriction(EdgeRestriction v) {
		String delim = "";
		for (Incidence tidInc : v.getIsTypeIdOf_isTypeIdOf_omegaIncidences()) {
			sb.append(delim);
			serializeIdentifier((Identifier) tidInc.getThat());
			delim = ",";
		}
		for (Incidence ridInc : v.getIsRoleIdOf_isRoleIdOf_omegaIncidences()) {
			sb.append(delim);
			delim = ",";
			serializeIdentifier((Identifier) ridInc.getThat());
		}
		Expression predicate = (Expression) v.getFirst_isBooleanPredicateOf_omega().getThat();
		if (predicate != null) {
			sb.append(" @ ");
			serializeExpression(predicate, false);
		}
	}

	private void serializeDirection(EdgeDirection v) {
		sb.append(v.get_dirValue());
	}

	private void serializeDefinition(Definition v) {
		serializeVariable((Variable) v.getFirst_isVarOf_omega().getThat());
		sb.append(" := ");
		serializeExpression((Expression) v.getFirst_isExprOf_omega().getThat(), false);
	}

	private void serializeDeclaration(Declaration v, boolean declOfFWR) {
		boolean first = true;
		for (Incidence sdInc : v.getIsSimpleDeclOf_isSimpleDeclOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeSimpleDeclaration((SimpleDeclaration) sdInc.getThat());
		}

		first = true;
		for (Incidence constrInc: v.getIsConstraintOf_isConstraintOf_omegaIncidences()) {
			if (declOfFWR) {
				sb.append(" with ");
			} else {
				// in QuantifiedExpressions, the constraints are separated with
				// comma
				sb.append(", ");
			}
			serializeExpression((Expression) constrInc.getThat(), false);
		}
	}

	private void serializeGreql2Expression(Greql2Expression greql2Expression) {
		Iterable<? extends Incidence> boundVars = greql2Expression
				.getIsBoundVarOf_isBoundVarOf_omegaIncidences();
		if (boundVars.iterator().hasNext()) {
			sb.append("using ");
			boolean first = true;
			for (Incidence i : boundVars) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				serializeVariable((Variable) i.getThat());
			}
			sb.append(':');
		}

		serializeExpression((Expression) greql2Expression.getFirst_isQueryExprOf_omega().getThat(), false);
	}

	private void serializeExpression(Expression exp, boolean addSpace) {
		if (exp instanceof ConditionalExpression) {
			serializeConditionalExpression((ConditionalExpression) exp);
		} else if (exp instanceof FunctionApplication) {
			serializeFunctionApplication((FunctionApplication) exp);
		} else if (exp instanceof Literal) {
			serializeLiteral((Literal) exp);
		} else if (exp instanceof Variable) {
			serializeVariable((Variable) exp);
		} else if (exp instanceof Identifier) {
			serializeIdentifier((Identifier) exp);
		} else if (exp instanceof QuantifiedExpression) {
			serializeQuantifiedExpression((QuantifiedExpression) exp);
		} else if (exp instanceof Comprehension) {
			serializeComprehension((Comprehension) exp);
		} else if (exp instanceof DefinitionExpression) {
			serializeDefinitionExpression((DefinitionExpression) exp);
		} else if (exp instanceof ElementSetExpression) {
			serializeElementSetExpression((ElementSetExpression) exp);
		} else if (exp instanceof PathDescription) {
			serializePathDescription((PathDescription) exp);
		} else if (exp instanceof PathExpression) {
			serializePathExpression((PathExpression) exp);
		} else if (exp instanceof SubgraphExpression) {
			serializeSubgraphExpression((SubgraphExpression) exp);
		} else if (exp instanceof ValueConstruction) {
			serializeValueConstruction((ValueConstruction) exp);
		} else {
			throw new GreqlException("Unknown Expression " + exp
					+ ". Serialization so far: " + sb.toString());
		}
		if (addSpace) {
			sb.append(' ');
		}
	}

	private void serializeValueConstruction(ValueConstruction exp) {
		if (exp instanceof ListConstruction) {
			serializeListConstruction((ListConstruction) exp);
		} else if (exp instanceof MapConstruction) {
			serializeMapConstruction((MapConstruction) exp);
		} else if (exp instanceof RecordConstruction) {
			serializeRecordConstruction((RecordConstruction) exp);
		} else if (exp instanceof SetConstruction) {
			serializeSetConstruction((SetConstruction) exp);
		} else if (exp instanceof TupleConstruction) {
			serializeTupleConstruction((TupleConstruction) exp, false);
		} else {
			throw new GreqlException("Unknown ValueConstruction " + exp + ".");
		}
	}

	private void serializeTupleConstruction(TupleConstruction exp,
			boolean implicit) {
		if (!implicit) {
			sb.append("tup(");
		}
		boolean first = true;
		for (Incidence inc : exp.getIsPartOf_isPartOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeExpression((Expression) inc.getThat(), false);
		}
		if (!implicit) {
			sb.append(")");
		}
	}

	private void serializeSetConstruction(SetConstruction exp) {
		sb.append("set(");
		boolean first = true;
		for (Incidence inc : exp.getIsPartOf_isPartOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeExpression((Expression) inc.getThat(), false);
		}
		sb.append(")");
	}

	private void serializeRecordConstruction(RecordConstruction exp) {
		sb.append("rec(");
		boolean first = true;
		for (Incidence inc : exp.getIsRecordElementOf_isRecordElementOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeRecordElement((RecordElement) inc.getThat());
		}
		sb.append(')');
	}

	private void serializeMapConstruction(MapConstruction exp) {
		sb.append("map(");
		Iterator<? extends Incidence> valIncs = exp.getIsValueExprOfComprehension_valueExprOfComprIncidences().iterator();
		String sep = "";
		for (Incidence keyInc : exp.getIsKeyExprOfComprehension_keyExprOfComprIncidences()) {
			sb.append(sep);
			sep = ", ";
			serializeExpression((Expression) keyInc.getThat(), true);
			sb.append("-> ");
			serializeExpression((Expression) valIncs.next().getThat(), false);
		}

		sb.append(")");
	}

	private void serializeListConstruction(ListConstruction exp) {
		sb.append("list(");
		if (exp instanceof ListRangeConstruction) {
			ListRangeConstruction lrc = (ListRangeConstruction) exp;
			serializeExpression((Expression) lrc.getFirst_isFirstValueOf_omega().getThat(), false);
			sb.append("..");
			serializeExpression((Expression) lrc.getFirst_isLastValueOf_omega().getThat(), false);
		} else {
			boolean first = true;
			for (Incidence inc : exp.getIsPartOf_isPartOf_omegaIncidences()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				serializeExpression((Expression) inc.getThat(), false);
			}
		}
		sb.append(")");
	}

	private void serializeSubgraphExpression(
			SubgraphExpression exp) {
		sb.append("on");
		// serialize left
		IsSubgraphDefinitionOf_isSubgraphDefinitionOf_omega isSubgraphDefOf = exp.getFirst_isSubgraphDefinitionOf_omega();
		serializeSubgraphDefinition((SubgraphDefinition) isSubgraphDefOf
				.getThat());
		sb.append(":");
		// serialize right
//		IsR isExprOnSubgraph = exp
//				.getFirstIsExpressionOnSubgraphIncidence(EdgeDirection.IN);
//		serializeExpression((Expression) isExprOnSubgraph.getThat(), true);
	}

	private void serializeSubgraphDefinition(SubgraphDefinition def) {

		if ((def instanceof EdgeTypeSubgraph)
				|| (def instanceof VertexTypeSubgraph)) {
			if (def instanceof EdgeTypeSubgraph) {
				sb.append("e");
			} else {
				sb.append("v");
			}
			sb.append("Subgraph{");
			boolean first = true;
			for (Incidence inc : def.getIsTypeRestrOfSubgraph_isTypeRestrOfSubgraph_omegaIncidences()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				serializeIdentifier((Identifier) inc.getThat());
			}
			sb.append('}');
		} else {
			// subgraph expression defined by arbitrary expression
			Incidence isDefExpr = ((ExpressionDefinedSubgraph) def)
					.getFirst_isSubgraphDefiningExpression_omega();
			serializeExpression((Expression) isDefExpr.getThat(), true);
		}
	}

	private void serializePathExpression(PathExpression exp) {
		if (exp instanceof BackwardElementSet) {
			serializeBackwardElementSet((BackwardElementSet) exp);
		} else if (exp instanceof ForwardElementSet) {
			serializeForwardElementSet((ForwardElementSet) exp);
		} else if (exp instanceof PathExistence) {
			serializePathExistence((PathExistence) exp);
		} else {
			throw new GreqlException("Unknown PathExpression " + exp + ".");
		}
	}

	private void serializePathExistence(PathExistence exp) {
		serializeExpression((Expression) exp.getFirst_isStartExprOf_omega().getThat(), true);
		serializeExpression((Expression) exp.getFirst_isPathOf_GoesTo_PathExpression().getThat(), true);
		serializeExpression((Expression) exp.getFirst_isTargetExprOf_omega().getThat(), false);
	}

	private void serializeForwardElementSet(ForwardElementSet exp) {
		serializeExpression((Expression) exp.getFirst_isStartExprOf_omega().getThat(), true);
		serializeExpression((Expression) exp.getFirst_isTargetExprOf_omega().getThat(), false);
	}

	private void serializeBackwardElementSet(BackwardElementSet exp) {
		serializeExpression((Expression) exp.getFirst_isStartExprOf_omega().getThat(), true);
		serializeExpression((Expression) exp.getFirst_isTargetExprOf_omega().getThat(), false);
	}

	private void serializePathDescription(PathDescription exp) {
		if (!((exp instanceof PrimaryPathDescription) || (exp instanceof OptionalPathDescription))) {
			sb.append('(');
		}
		if (exp.getFirst_isStartRestrictionOf_omega() != null) {
			sb.append("{");
			serializeExpression((Expression) exp.getFirst_isStartRestrictionOf_omega().getThat(), false);
			sb.append("} & ");
		}

		if (exp instanceof AlternativePathDescription) {
			serializeAlternativePathDescription((AlternativePathDescription) exp);
		} else if (exp instanceof ExponentiatedPathDescription) {
			serializeExponentiatedPathDescription((ExponentiatedPathDescription) exp);
		} else if (exp instanceof IntermediateVertexPathDescription) {
			serializeIntermediateVertexPathDescription((IntermediateVertexPathDescription) exp);
		} else if (exp instanceof IteratedPathDescription) {
			serializeIteratedPathDescription((IteratedPathDescription) exp);
		} else if (exp instanceof OptionalPathDescription) {
			serializeOptionalPathDescription((OptionalPathDescription) exp);
		} else if (exp instanceof SequentialPathDescription) {
			serializeSequentialPathDescription((SequentialPathDescription) exp);
		} else if (exp instanceof TransposedPathDescription) {
			serializeTransposedPathDescription((TransposedPathDescription) exp);
		} else if (exp instanceof PrimaryPathDescription) {
			serializePrimaryPathDescription((PrimaryPathDescription) exp);
		} else {
			throw new GreqlException("Unknown PathDescription " + exp + ".");
		}

		if (exp.getFirst_isGoalRestrOf_omega() != null) {
			sb.append(" & {");
			serializeExpression((Expression) exp.getFirst_isGoalRestrOf_omega().getThat(), false);
			sb.append("}");
		}
		if (!((exp instanceof PrimaryPathDescription) || (exp instanceof OptionalPathDescription))) {
			sb.append(')');
		}
	}

	private void serializePrimaryPathDescription(PrimaryPathDescription exp) {
		if (exp instanceof EdgePathDescription) {
			serializeEdgePathDescription((EdgePathDescription) exp);
		} else if (exp instanceof SimplePathDescription) {
			serializeSimplePathDescription((SimplePathDescription) exp);
		} else if (exp instanceof AggregationPathDescription) {
			serializeAggregationPathDescription((AggregationPathDescription) exp);
		} else {
			throw new GreqlException("Unknown PrimaryPathDescription " + exp
					+ ".");
		}

		if (exp.getIsEdgeRestrOf_isEdgeRestrOf_omegaIncidences().iterator().hasNext()) {
			sb.append("{");
			boolean first = true;
			for (Incidence inc : exp.getIsEdgeRestrOf_isEdgeRestrOf_omegaIncidences()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				serializeEdgeRestriction((EdgeRestriction) inc.getThat());
			}
			sb.append("}");
		}
	}

	private void serializeAggregationPathDescription(
			AggregationPathDescription exp) {
		if (exp.is_outAggregation()) {
			sb.append("<>--");
		} else {
			sb.append("--<>");
		}
	}

	private void serializeSimplePathDescription(SimplePathDescription exp) {
		String dir = ((EdgeDirection) exp.getFirst_isEdgeDirOf_omega().getThat()).get_dirValue();
		if (dir.equals("out")) {
			sb.append("-->");
		} else if (dir.equals("in")) {
			sb.append("<--");
		} else {
			sb.append("<->");
		}
	}

	private void serializeEdgePathDescription(EdgePathDescription exp) {
		String dir = ((EdgeDirection) exp.getFirst_isEdgeDirOf_omega().getThat()).get_dirValue();
		if (dir.equals("out")) {
			sb.append("--");
			serializeExpression((Expression) exp.getFirst_isEdgeExprOf_omega().getThat(), false);
			sb.append("->");
		} else if (dir.equals("in")) {
			sb.append("<-");
			serializeExpression((Expression) exp.getFirst_isEdgeExprOf_omega().getThat(), false);
			sb.append("--");
		} else {
			sb.append("<-");
			serializeExpression((Expression) exp.getFirst_isEdgeExprOf_omega().getThat(), false);
			sb.append("->");
		}
	}

	private void serializeTransposedPathDescription(
			TransposedPathDescription exp) {
		serializePathDescription((PathDescription) exp.getFirst_isTransposedPathOf_omega().getThat());
		sb.append("^T");
	}

	private void serializeSequentialPathDescription(
			SequentialPathDescription exp) {
		for (Incidence pdInc : exp.getIsSequenceElementOf_isSequenceElementOf_omegaIncidences()) {
			serializePathDescription((PathDescription) pdInc.getThat());
			sb.append(' ');
		}
	}

	private void serializeOptionalPathDescription(OptionalPathDescription exp) {
		sb.append('[');
		serializePathDescription((PathDescription) exp.getFirst_isOptionalPathOf_omega().getThat());
		sb.append(']');
	}

	private void serializeIteratedPathDescription(IteratedPathDescription exp) {
		serializePathDescription((PathDescription) exp.getFirst_isIteratedPathOf_omega().getThat());
		sb.append(exp.get_times() == IterationType.STAR ? '*' : '+');
	}

	private void serializeIntermediateVertexPathDescription(
			IntermediateVertexPathDescription exp) {
		Iterator<? extends Incidence> sub = exp.getIsSubPathOf_isSubPathOf_omegaIncidences().iterator();
		serializePathDescription((PathDescription) sub.next().getThat());
		serializeExpression((Expression) exp.getFirst_isIntermediateVertexOf_omega().getThat(), false);
		serializePathDescription((PathDescription) sub.next().getThat());
	}

	private void serializeExponentiatedPathDescription(
			ExponentiatedPathDescription exp) {
		serializePathDescription((PathDescription) exp.getFirst_isExponentiatedPathOf_omega().getThat());
		sb.append('^');
		serializeLiteral((Literal) exp.getFirst_isExponentOf_omega().getThat());;
	}

	private void serializeAlternativePathDescription(
			AlternativePathDescription exp) {
		boolean first = true;
		for (Incidence inc : exp.getIsAlternativePathOf_isAlternativePathOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(" | ");
			}
			serializePathDescription((PathDescription) inc.getThat());
		}
	}

	private void serializeElementSetExpression(ElementSetExpression exp) {
		if (exp instanceof VertexSetExpression) {
			sb.append("V");
		} else if (exp instanceof EdgeSetExpression) {
			sb.append("E");
		} else {
			throw new GreqlException("Unknown ElementSetExpression " + exp
					+ ".");
		}

		Iterable<? extends Incidence> typeRestrictions = exp.getIsTypeRestrOfExpression_isTypeRestrOfExpression_omegaIncidences();

		if (!typeRestrictions.iterator().hasNext()) {
			return;
		}

		sb.append("{");
		String sep = "";
		for (Incidence tinc : typeRestrictions) {
			sb.append(sep);
			sep = ", ";
			serializeIdentifier((Identifier) tinc.getThat());
		}
		sb.append("}");
	}

	private void serializeDefinitionExpression(DefinitionExpression exp) {
		if (exp instanceof LetExpression) {
			serializeLetExpression((LetExpression) exp);
		} else if (exp instanceof WhereExpression) {
			serializeWhereExpression((WhereExpression) exp);
		} else {
			throw new GreqlException("Unknown DefinitionExpression " + exp
					+ ".");
		}
	}

	private void serializeWhereExpression(WhereExpression exp) {
		serializeExpression((Expression) exp.getFirst_isBoundExprOf_GoesTo_Greql2Vertex().getThat(), true);
		sb.append("where ");
		boolean first = true;
		for (Incidence defInc : exp.getIsDefinitionOf_isDefinitionOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeDefinition((Definition) defInc.getThat());
		}
	}

	private void serializeLetExpression(LetExpression exp) {
		sb.append("let ");
		boolean first = true;
		for (Incidence defInc : exp.getIsDefinitionOf_isDefinitionOf_omegaIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeDefinition((Definition) defInc.getThat());
		}
		sb.append(" in ");
		serializeExpression((Expression) exp.getFirst_isBoundExprOf_GoesTo_Greql2Vertex().getThat(), true);
	}

	private void serializeComprehension(Comprehension exp) {
		sb.append("from ");
		serializeDeclaration((Declaration) exp.getFirst_isCompDeclOf_omega().getThat(), true);
		if (exp instanceof SetComprehension) {
			sb.append(" reportSet ");
		} else if (exp instanceof ListComprehension) {
			sb.append(" report ");
		} else if (exp instanceof TableComprehension) {
			sb.append(" reportTable ");
		} else if (exp instanceof MapComprehension) {
			sb.append(" reportMap ");
			MapComprehension mc = (MapComprehension) exp;
			// MapComprehensions have no compResultDef, but key and valueExprs
			serializeExpression((Expression) mc.getFirst_isKeyExprOfComprehension_omega().getThat(), false);
			sb.append(", ");
			serializeExpression((Expression) mc.getFirst_isValueExprOfComprehension_omega().getThat(), true);
			sb.append("end");
			return;
		} else {
			throw new GreqlException("Unknown Comprehension " + exp + ".");
		}

		Expression result = (Expression) exp.getFirst_isCompResultDefOf_omega().getThat();;

		if (result instanceof TupleConstruction) {
			// here the tup() can be omitted
			serializeTupleConstruction((TupleConstruction) result, true);
			sb.append(' ');
		} else {
			serializeExpression(result, true);
		}
		sb.append("end");
	}

	private void serializeQuantifiedExpression(QuantifiedExpression exp) {
		sb.append('(');
		serializeQuantifier((Quantifier) exp.getFirst_isQuantifierOf_omega().getThat());
		sb.append(' ');
		serializeDeclaration((Declaration) exp.getFirst_isQuantifiedDeclOf_omega().getThat(), false);
		sb.append(" @ ");
		serializeExpression((Expression) exp.getFirst_isBoundExprOfQuantifiedExpr_omega().getThat(), false);
		sb.append(')');
	}

	private void serializeLiteral(Literal exp) {
		if (exp instanceof BoolLiteral) {
			sb.append(((BoolLiteral) exp).is_boolValue());
		} else if (exp instanceof IntLiteral) {
			sb.append(((IntLiteral) exp).get_intValue());
		} else if (exp instanceof UndefinedLiteral) {
			sb.append("undefined");
		} else if (exp instanceof DoubleLiteral) {
			sb.append(((DoubleLiteral) exp).get_doubleValue());
		} else if (exp instanceof StringLiteral) {
			sb.append("\"");
			sb.append(((StringLiteral) exp).get_stringValue());
			sb.append("\"");
		} else if (exp instanceof ThisEdge) {
			sb.append("thisEdge");
		} else if (exp instanceof ThisVertex) {
			sb.append("thisVertex");
		} else {
			throw new GreqlException("Unknown Literal " + exp + ".");
		}
	}

	private void serializeIdentifier(Identifier exp) {
		sb.append(exp.get_name());
	}

	private void serializeFunctionApplication(FunctionApplication exp) {
		FunctionId fid = (FunctionId) exp.getFirst_isFunctionIdOf_omega().getThat();
		String id = fid.get_name();

		if (id.equals("add")) {
			serializeFunctionApplicationInfix(exp, "+");
			return;
		} else if (id.equals("sub")) {
			serializeFunctionApplicationInfix(exp, "-");
			return;
		} else if (id.equals("mul")) {
			serializeFunctionApplicationInfix(exp, "*");
			return;
		} else if (id.equals("div")) {
			serializeFunctionApplicationInfix(exp, "/");
			return;
		} else if (id.equals("equals")) {
			serializeFunctionApplicationInfix(exp, "=");
			return;
		} else if (id.equals("nequals")) {
			serializeFunctionApplicationInfix(exp, "<>");
			return;
		} else if (id.equals("grEqual")) {
			serializeFunctionApplicationInfix(exp, ">=");
			return;
		} else if (id.equals("grThan")) {
			serializeFunctionApplicationInfix(exp, ">");
			return;
		} else if (id.equals("leEqual")) {
			serializeFunctionApplicationInfix(exp, "<=");
			return;
		} else if (id.equals("leThan")) {
			serializeFunctionApplicationInfix(exp, "<");
			return;
		} else if (id.equals("reMatch")) {
			serializeFunctionApplicationInfix(exp, "=~");
			return;
		} else if (id.equals("mod")) {
			serializeFunctionApplicationInfix(exp, "%");
			return;
		} else if (id.equals("and")) {
			serializeFunctionApplicationInfix(exp, "and");
			return;
		} else if (id.equals("or")) {
			serializeFunctionApplicationInfix(exp, "or");
			return;
		} else if (id.equals("xor")) {
			serializeFunctionApplicationInfix(exp, "xor");
			return;
		} else if (id.equals("concat")) {
			serializeFunctionApplicationInfix(exp, "++");
			return;
		} else if (id.equals("getValue")) {
			serializeFunctionApplicationInfix(exp, ".");
			return;
		}

		serializeIdentifier(fid);
		sb.append('(');
		boolean first = true;
		for (Incidence argInc : exp.getIsArgumentOf_argumentIncidences()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			serializeExpression((Expression) argInc.getThat(), false);
		}
		sb.append(")");
	}

	private void serializeFunctionApplicationInfix(FunctionApplication exp,
			String operator) {
		sb.append("(");
		boolean first = true;
		for (Incidence argInc : exp.getIsArgumentOf_argumentIncidences()) {
			if (first) {
				first = false;
			} else {
				// The DOT operator (myElem.myAttr) shouldn't have spaces
				// arround.
				if (operator.equals(".")) {
					sb.append(operator);
				} else {
					sb.append(' ').append(operator).append(' ');
				}
			}
			serializeExpression((Expression) argInc.getThat(), false);
		}
		sb.append(")");
	}

	private void serializeConditionalExpression(ConditionalExpression expression) {
		serializeExpression((Expression) expression.getFirst_isConditionOf_omega().getThat(), true);
		sb.append("? ");
		serializeExpression((Expression) expression.getFirst_isTrueExprOf_omega().getThat(), true);
		sb.append(": ");
		serializeExpression((Expression) expression.getFirst_isFalseExprOf_omega().getThat(), true);
	}

	private void serializeVariable(Variable v) {
		sb.append(v.get_name());
	}
}
