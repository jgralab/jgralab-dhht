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

package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.greql2.evaluator.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VertexCosts;
import de.uni_koblenz.jgralab.greql2.schema.EdgeRestriction;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.IsBooleanPredicateOfEdgeRestriction_isBooleanPredicateOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsRoleIdOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeIdOf_isTypeIdOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.RoleId;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

/**
 * Evaluates an edge restriction, edges can be restricted with TypeIds and Roles
 *
 * @author ist@uni-koblenz.de
 *
 */
public class EdgeRestrictionEvaluator extends VertexEvaluator {

	/**
	 * The EdgeRestriction vertex in the GReQL Syntaxgraph
	 */
	private EdgeRestriction vertex;

	private VertexEvaluator predicateEvaluator = null;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Greql2Vertex getVertex() {
		return vertex;
	}

	public VertexEvaluator getPredicateEvaluator() {
		return predicateEvaluator;
	}

	/**
	 * The JValueTypeCollection which holds all the allowed and forbidden types
	 */
	private TypeCollection typeCollection = null;

	/**
	 * Returns the typeCollection
	 */
	public TypeCollection getTypeCollection() {
		if (typeCollection == null) {
			evaluate();
		}
		return typeCollection;
	}

	/**
	 * the valid role of an edge
	 */
	private Set<String> validRoles;

	/**
	 * @return the valid edge role
	 */
	public Set<String> getEdgeRoles() {
		return validRoles;
	}

	/**
	 * creates a new EdgeRestriction evaluator
	 *
	 * @param vertex
	 * @param eval
	 */
	public EdgeRestrictionEvaluator(EdgeRestriction vertex, GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	/**
	 * evaluates the EdgeRestriction, creates the typeList and the validEdgeRole
	 */
	@Override
	public Object evaluate() {
		if (typeCollection == null) {
			typeCollection = new TypeCollection();
			IsTypeIdOf_isTypeIdOf_omega typeInc = vertex.getFirst_isTypeIdOf_omega();
			while (typeInc != null) {
				TypeIdEvaluator typeEval = (TypeIdEvaluator) vertexEvalMarker
						.getMark(typeInc.getThat());
				typeCollection.addTypes((TypeCollection) typeEval.getResult());
				typeInc = typeInc.getNextIsTypeIdOf_omegaAtVertex();
			}
		}

		if (vertex.getFirst_isRoleIdOf_omega() != null) {
			validRoles = new HashSet<String>();
			for (IsRoleIdOf e : vertex.getIncidentEdgesOfType_IsRoleIdOf()) {
				RoleId role = (RoleId) e.getAlpha();
				validRoles.add(role.get_name());
			}
		}
		IsBooleanPredicateOfEdgeRestriction_isBooleanPredicateOf_omega predInc = vertex
				.getFirstIncidenceToIsBooleanPredicateOfEdgeRestriction(Direction.EDGE_TO_VERTEX);
		if (predInc != null) {
			// System.out.println("Found a BooleanPredicateOfEdge");
			predicateEvaluator = vertexEvalMarker.getMark(predInc.getThat());
		}
		return null;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		long subtreeCosts = 0;
		if (vertex.getFirstIncidenceToIsTypeIdOf(Direction.EDGE_TO_VERTEX) != null) {
			TypeIdEvaluator tEval = (TypeIdEvaluator) getVertexEvalMarker()
					.getMark(
							vertex.getFirstIncidenceToIsTypeIdOf(Direction.EDGE_TO_VERTEX)
									.getThat());
			subtreeCosts += tEval.getCurrentSubtreeEvaluationCosts(graphSize);
		}
		if (vertex.getFirstIncidenceToIsRoleIdOf(Direction.EDGE_TO_VERTEX) != null) {
			subtreeCosts += 1;
		}

		long transitionCosts = 5;
		return new VertexCosts(transitionCosts, transitionCosts, subtreeCosts
				+ transitionCosts);
	}

}
